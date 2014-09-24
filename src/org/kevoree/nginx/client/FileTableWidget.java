package org.kevoree.nginx.client;

import java.util.ArrayList;

import org.kevoree.nginx.client.table.FileTable;
import org.kevoree.nginx.client.table.Sortable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ycp.cs.dh.acegwt.client.ace.AceEditor;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorTheme;

/**
 * Creates a table view for displaying files and directories.
 * 
 *
 */
public class FileTableWidget extends Composite implements Sortable {

	private FileSystemTreeWidget fsTree;
	private PopupMenu ppMenu;
	private FileTable table;
	private FileSystemServiceAsync fileSystemSvc = GWT
			.create(FileSystemService.class);
	private boolean isAdmin = true;
	private static String downloadServlet = "/dn?fn="; // on the server
														// /filemanager/dn?fn=
														// local testing use
														// /dn?fn=
	private String sortAscImage = "imgs/asc.png";
	private String sortDescImage = "imgs/desc.png";
	private int sortColIndex = 0;

	// Holds the current direction of sort: Asc/Desc
	private int sortDirection = SORT_ASC;

	private ArrayList<FileWrapper> currentTableFiles;

	/**
	 * Creates a table view of files and directories
	 * 
	 * @param isAdmin
	 * @param downloadServlet
	 */
	public FileTableWidget(boolean isAdmin) {
		this.isAdmin = isAdmin;
		currentTableFiles = new ArrayList<FileWrapper>();

		table = new FileTable();
		table.setCellSpacing(0);
		createTableHeader();

		table.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {

				Cell cell = table.getCellForEvent(event);
				int row = cell.getRowIndex();
				if (cell.getCellIndex() == 0 && row == 0) {
					sort(0);
					table.removeAllRows();
					createTableHeader();
					addSortedFiles();
				}

				if (row != 0) {
					if (sortDirection == SORT_DESC) {
						row = currentTableFiles.size() - row;
					} else {
						row = row - 1;
					}

					FileWrapper file = currentTableFiles.get(row);

					if (file.getKind() == FileType.DIR) {
						GWT.log(file.getPath());
						TreeItem tI = fsTree.getTree().getSelectedItem();
						fsTree.setExpandTree(true);
						fsTree.getChildren(tI);

						GWT.log("Selected node " + tI.getText());

						fsTree.settItemNameClicked(file.getName());
						fsTree.setPath(file.getPath());
					} else {
						editFile(file);
					}
				}
			}
		});

		initWidget(table);
	}

	/**
	 * @param downloadServlet
	 *            the downloadServlet to set
	 */
	public static void setDownloadServlet(String downloadServlet) {
		FileTableWidget.downloadServlet = downloadServlet;
	}

	/**
	 * Updates the table with files and directories
	 * 
	 * @param files
	 *            the files and directories to add to the table
	 */
	public void updateTableContent(FileWrapper[] files, boolean ascOrder) {
		if (ascOrder)
			sortDirection = SORT_ASC;
		cleanupTableContent();
		currentTableFiles.clear();
		createTableHeader();
		for (FileWrapper file : files) {
			insertFileRow(file);
			currentTableFiles.add(file);
		}
	}

	/**
	 * Retrieves the file system tree that's associated with this table.
	 * 
	 * @return the file system tree
	 */
	public FileSystemTreeWidget getFsTree() {
		return fsTree;
	}

	/**
	 * Sets the file system Tree that will be associated with this table.
	 * 
	 * @param fsTree
	 *            the tree that is associated with the table
	 */
	public void setFsTree(FileSystemTreeWidget fsTree) {
		this.fsTree = fsTree;
	}

	/**
	 * Determines which column in the table is sorted and alternates from
	 * ascending to descending order.
	 * 
	 * @param columnIdx
	 *            the column being sorted. Columns are zero based
	 */
	public void sort(int columnIdx) {
		// files always come from the server in sorted order
		if (this.sortColIndex != columnIdx) {
			// New Column Header clicked
			// Reset the sortDirection to ASC
			this.sortDirection = SORT_ASC;
		} else {
			// Same Column Header clicked
			// Reverse the sortDirection
			this.sortDirection = (this.sortDirection == SORT_ASC) ? SORT_DESC
					: SORT_ASC;
		}
		this.sortColIndex = columnIdx;
	}

	/**
	 * Shows the New folder and upload popup menu when users right-click on a
	 * folder
	 * 
	 * @param x
	 *            horizontal location on the screen
	 * @param y
	 *            vertical location on the screen
	 */
	protected void showMenu(int x, int y) {
		final int xx = x;
		final int yy = y;
		final PopupMenu popMenu = new PopupMenu(fsTree);
		ppMenu = popMenu;
		popMenu.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			public void setPosition(int offsetWidth, int offsetHeight) {
				popMenu.setPopupPosition(xx, yy);
			}
		});
	}

	private void createTableHeader() {
		DOM.setElementAttribute(table.getElement(), "id", "files-table");
		table.setHTML(0, 0, getTableHeaderHTML("Name"));
		table.setText(0, 1, "Modified at");
		table.setText(0, 2, "Type");
		table.setText(0, 3, "Control");

		table.getCellFormatter().addStyleName(0, 0,
				"files-table-column files-table-header-cell");
		table.getCellFormatter().addStyleName(0, 1,
				"files-table-column files-table-header-cell");
		table.getCellFormatter().addStyleName(0, 2,
				"files-table-column files-table-header-cell");
		table.getCellFormatter().addStyleName(0, 3,
				"files-table-column files-table-header-cell");
	}

	private void insertFileRow(FileWrapper file) {
		int numRows = table.getRowCount();
		table.setWidget(numRows, 0,
				getFileIconedName(file.getName(), file.getKind()));
		table.setText(numRows, 1, file.getModified());
		table.setText(numRows, 2, file.getKind().toString());
		FlowPanel controlPanel = new FlowPanel();
		if (file.getKind() != FileType.DIR) {
			// controlPanel.add(createDownloadButton(numRows, file));
			controlPanel.add(createEditButton(numRows, file));
		}
		if (isAdmin) {
			controlPanel.add(createDeleteButton(numRows, file));
			controlPanel.add(createRenameButton(numRows, file));
		}

		table.setWidget(numRows, 3, controlPanel);
		if (numRows % 2 == 0) {
			table.getRowFormatter().addStyleName(numRows, "files-table-odd");
		}
	}

	private Button createDownloadButton(final int rowNumber,
			final FileWrapper file) {
		return new Button("Download", new ClickHandler() {
			public void onClick(ClickEvent ce) {
				ce.stopPropagation();
				downloadFile(file);
			}
		});
	}

	private Button createEditButton(final int rowNumber, final FileWrapper file) {
		return new Button("Edit", new ClickHandler() {
			public void onClick(ClickEvent ce) {
				ce.stopPropagation();
				editFile(file);
			}
		});
	}

	private Button createDeleteButton(final int rowNumber,
			final FileWrapper file) {
		return new Button("Delete", new ClickHandler() {
			public void onClick(ClickEvent ce) {
				ce.stopPropagation();
				StringBuffer msg = new StringBuffer();
				if (file.getKind() == FileType.DIR) {
					msg.append("WARNING! You are about to delete a directory and all of it's contents.");
					msg.append(" Are you sure you want to delete the directory : ");
					msg.append(file.getName());
				} else {
					msg.append("Are you sure you want to delete : ");
					msg.append(file.getName());
				}
				if (!Window.confirm(msg.toString())) {
					return;
				}
				if (fileSystemSvc == null) {
					fileSystemSvc = GWT.create(FileSystemService.class);
				}
				AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {

					public void onFailure(Throwable thrwbl) {
						// Something wrong. to be handled.
						GWT.log("ERROR: " + thrwbl.getMessage());
					}

					public void onSuccess(Boolean deleted) {
						if (deleted) {
							/* table.removeRow(rowNumber); */
							fsTree.updateTree();
						} else {
							Window.alert("ERROR : File is not deleted!");
						}
					}
				};
				fileSystemSvc.deleteFile(file.getPath(), callback);
			}
		});
	}

	private Button createRenameButton(final int rowNumber,
			final FileWrapper file) {
		return new Button("Rename", new ClickHandler() {
			public void onClick(ClickEvent ce) {
				ce.stopPropagation();
				StringBuffer msg = new StringBuffer();
				if (file.getKind() == FileType.DIR) {
					msg.append("What do you want to rename this directory, ");
					msg.append(file.getName());
					msg.append(", to? ");
				} else {
					msg.append("What do you want to rename this file, ");
					msg.append(file.getName());
					msg.append(", to? ");
				}

				String newName = Window.prompt(msg.toString(), file.getName());

				if (newName == null || newName.equals(""))
					return;

				if (fileSystemSvc == null) {
					fileSystemSvc = GWT.create(FileSystemService.class);
				}
				AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {

					public void onFailure(Throwable thrwbl) {
						// Something wrong. to be handled.
						GWT.log("ERROR: " + thrwbl.getMessage());
					}

					public void onSuccess(Boolean renamed) {
						if (renamed) {
							fsTree.updateTree();
						} else {
							Window.alert("ERROR : Not renamed!");
						}
					}
				};

				// System.out.println("Path "+file.getPath());
				String path = file.getPath().substring(
						0,
						file.getPath().lastIndexOf(
								FileSystemTreeWidget.getFileSeparator()) + 1);
				fileSystemSvc.renameFile(file.getPath(), path + newName,
						callback);
			}
		});
	}

	private void cleanupTableContent() {
		if (ppMenu != null)
			ppMenu.hide();
		table.removeAllRows();
	}

	private HTML getFileIconedName(String name, FileType type) {
		switch (type) {
		case AVI:
			return new HTML("<a href='#'><img src='imgs/icon_avi.png' />"
					+ "<span>  " + name + "</span></a>");
		case DOC:
			return new HTML("<a href='#'><img src='imgs/icon_docx.png' />"
					+ "<span>  " + name + "</span></a>");
		case XLSX:
			return new HTML("<a href='#'><img src='imgs/icon_xlsx.png' />"
					+ "<span>  " + name + "</span></a>");
		case HTML:
			return new HTML("<a href='#'><img src='imgs/icon_html.png' />"
					+ "<span>  " + name + "</span></a>");
		case IMG:
			return new HTML("<a href='#'><img src='imgs/icon_img.png' />"
					+ "<span>  " + name + "</span></a>");
		case MOV:
			return new HTML("<a href='#'><img src='imgs/icon_mov.png' />"
					+ "<span>  " + name + "</span></a>");
		case MP3:
			return new HTML("<a href='#'><img src='imgs/icon_mp.png' />"
					+ "<span>  " + name + "</span></a>");
		case PDF:
			return new HTML("<a href='#'><img src='imgs/icon_pdf.png' />"
					+ "<span>  " + name + "</span></a>");
		case PPTX:
			return new HTML("<a href='#'><img src='imgs/icon_pptx.png' />"
					+ "<span>  " + name + "</span></a>");
		case TXT:
			return new HTML("<a href='#'><img src='imgs/icon_txt.png' />"
					+ "<span>  " + name + "</span></a>");
		case WMV:
			return new HTML("<a href='#'><img src='imgs/icon_wmv.png' />"
					+ "<span>  " + name + "</span></a>");
		case DIR:
			return new HTML(
					"<span style='cursor: pointer !important;'><img src='imgs/icon_folder.gif' />"
							+ "<span>  " + name + "</span></span>");
		case MM:
			return new HTML("<a href='#'><img src='imgs/icon_mm.gif' />"
					+ "<span>  " + name + "</span></a>");
		case WPD:
			return new HTML("<a href='#'><img src='imgs/icon_wpd.png' />"
					+ "<span>  " + name + "</span></a>");
		case ZIP:
			return new HTML("<a href='#'><img src='imgs/icon_zip.png' />"
					+ "<span>  " + name + "</span></a>");
		default:
			return new HTML("<img src='imgs/icon_other.png' />" + "<span>  "
					+ name + "</span>");
		}
	}

	private String getTableHeaderHTML(String name) {
		StringBuffer headerText = new StringBuffer();
		headerText.append(name);
		headerText.append("&nbsp;<img id='arrow' border='0' src=");

		if (this.sortDirection == SORT_ASC) {
			headerText.append("'" + this.sortAscImage + "' alt='Ascending' ");
		} else {
			headerText.append("'" + this.sortDescImage + "' alt='Descending' ");
		}
		headerText.append("/>");
		return headerText.toString();
	}

	private void addSortedFiles() {
		if (sortDirection == SORT_ASC) {
			FileWrapper[] files = new FileWrapper[currentTableFiles.size()];
			updateTableContent(currentTableFiles.toArray(files), false);
		} else {
			for (int i = currentTableFiles.size() - 1; i >= 0; --i) {
				insertFileRow(currentTableFiles.get(i));
			}
		}
	}

	private void downloadFile(final FileWrapper file) {
		String cleanPath = file.getPath();
		if (cleanPath.contains("&"))
			cleanPath = cleanPath.replace("&", "%26");
		else if (cleanPath.contains("#"))
			cleanPath = cleanPath.replace("#", "%23");
		GWT.log(downloadServlet + cleanPath);
		Window.open(downloadServlet + cleanPath, "_blank", "enabled");
	}

	private void editFile(final FileWrapper file) {
		fileSystemSvc.getFileContents(file, new AsyncCallback<String>() {

			@Override
			public void onSuccess(String result) {
				DialogBox c = createDialogBox(file,result);
				c.center();
				c.show();

			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}
		});

	}

	private DialogBox createDialogBox(final FileWrapper file,final String result) {
		// Create a dialog box and set the caption text
		final DialogBox dialogBox = new DialogBox();
		dialogBox.ensureDebugId("cwDialogBox");
		dialogBox.setText("Test");
		dialogBox.setSize("800px", "650px");
		// Create a table to layout the content
		VerticalPanel dialogContents = new VerticalPanel();
		// dialogContents.setSpacing(4);
		dialogBox.setWidget(dialogContents);

		// Add some text to the top of the dialog

		final AceEditor editor = new AceEditor();
		editor.setWidth("800px");
		editor.setHeight("600px");
		editor.startEditor();
		editor.setTheme(AceEditorTheme.TWILIGHT);
		editor.setText(result);
		dialogContents.add(editor);
		

		HorizontalPanel hpanel = new HorizontalPanel();
		// Add a close button at the bottom of the dialog
		Button closeButton = new Button("close", new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
			}
		});
		Button commitAndRestart = new Button("comixt and restart nginx",
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						fileSystemSvc.editandCommitFileContents(file, editor.getText(), new AsyncCallback<Boolean>() {

							@Override
							public void onSuccess(Boolean result) {
								if (result)
									dialogBox.hide();
								else
									Window.alert("cannot restart nginx, please check your conf");
							}
							@Override
							public void onFailure(Throwable caught) {
								Window.alert("cannot restart nginx, please check your conf");
							}
						});			
						
					}
				});

		hpanel.add(commitAndRestart);
		hpanel.add(closeButton);

		hpanel.setWidth("800px");
		hpanel.setCellHorizontalAlignment(closeButton,
				HasHorizontalAlignment.ALIGN_RIGHT);
		hpanel.setCellHorizontalAlignment(closeButton,
				HasHorizontalAlignment.ALIGN_RIGHT);
		dialogContents.setCellHorizontalAlignment(hpanel,
				HasHorizontalAlignment.ALIGN_RIGHT);

		dialogContents.add(hpanel);

		// Return the dialog box
		return dialogBox;
	}
}