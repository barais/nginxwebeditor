package org.kevoree.nginx.client;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * Creates a file system tree widget with a split view of the files
 * in a table view.
 *
 */
public class FileSystemTreeWidget extends Composite {
	// Uses a server side service to retrieve properties from propertiesSvc
    // The initial values are replaced by the service values
    private static HashMap<String, String> properties; 
	private Tree tree;
	private boolean expandTree = false; 		// used when a table item is selected
	private String tItemNameClicked = "";
    private FileTableWidget table;
    private TreeItem targetItem;
    private HTML footer;
    private String path;
    private MainMenuWidget mmw;
    private FileSystemServiceAsync fileSystemSvc = GWT.create(FileSystemService.class);

    /**
     * Creates a file system tree with a table view of the files
     * @param treeTitle title of the tree
     * @param table the table to display the files and directories
     * @param footer the footer which will display the location on the file system
     */
    public FileSystemTreeWidget(String treeTitle, FileTableWidget table, HTML footer) {
    	this.table = table;
        this.footer = footer;
        tree = new Tree();
        table.setFsTree(this);        
        initWidget(tree);
    }
    
	/**
     * Returns the system file separator
     * @return the system file separator
     */
    public static String getFileSeparator() {
    	return properties.get("file.separator");
    }
    
    /**
     * Returns the path for the download servlet
     * @return the path for the download servlet
     */
    public static String getDownloadServlet() {
    	return properties.get("download.servlet");
    }
    
    /**
     * Returns the current path in the tree
     * @return the current path in the tree
     */
    public String getPath() {
		return path;
	}
    
    /**
     * Sets the application main menu
     * @param mmw the main menu to be set
     */
    public void setMainMenu(MainMenuWidget mmw) {
    	this.mmw = mmw;
    }
    
    /**
     * Returns the tree's main menu
     * @return the main menu
     */
    public MainMenuWidget getMainMenu() {
    	return mmw;
    }
    
    /**
     * Updates the tree and table view after changes.
     */
    public void updateTree() {
    	GWT.log("Update Tree/Table "+ path);
    	fetchTreeItems(targetItem, path);
    	fetchTableItems(path);
    }

    /**
     * Creates a tree based on the the root.folder.names in the properties.file
     * The tree has the ability to have four root folders: root.folder.name, root2.folder.name,
     * root3.folder.name, and root4.folder.name
     * Tree is created after the properties are read from the server
     */
	public void createFileSystemTree() {
		String root2Search = properties.get("root2.search");
        String root3Search = properties.get("root3.search");
        String root4Search = properties.get("root4.search");
        
        tree.setAnimationEnabled(true);        
        TreeItem tItem = new TreeItem(new OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(properties.get("root.folder.name")));        
        tItem.addItem(new OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml("Loading..."));        
        tree.addItem(tItem);
        
        if(root2Search != null && !root2Search.equals("")) {
	        // add info root
	        TreeItem tItem2 = new TreeItem(new OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(properties.get("root2.folder.name")));
	        tree.addItem(tItem2); 
	        // load the 2nd directory in tree structure
	        path = findPath(tItem2);
	        GWT.log("updated path = " + path);
	        fetchTreeItems(tItem2, path);
        }
        
        if(root3Search != null && !root3Search.equals("")) {
        	TreeItem tItem3 = new TreeItem(new OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(properties.get("root3.folder.name")));
	        tree.addItem(tItem3); 
	        // load the 2nd directory in tree structure
	        path = findPath(tItem3);
	        GWT.log("updated path = " + path);
	        fetchTreeItems(tItem3, path);
        }
        
        if(root4Search != null && !root4Search.equals("")) {
        	TreeItem tItem4 = new TreeItem(new OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(properties.get("root4.folder.name")));
	        tree.addItem(tItem4); 
	        // load the 2nd directory in tree structure
	        path = findPath(tItem4);
	        GWT.log("updated path = " + path);
	        fetchTreeItems(tItem4, path);
        }
        
        tree.addOpenHandler(getOpenHandler());
        tree.addSelectionHandler(getSelectionHandler());
        tree.setSelectedItem(tItem, true);        
    }
	
	/**
	 * Sets the properties for the application
	 * @param properties the properties to set
	 */
	public static void setProperties(HashMap<String, String> properties) {
		FileSystemTreeWidget.properties = properties;
	}
	
	/**
	 * Sets the current directory path the user is at for the tree, and the display
	 * in the status bar
	 * @param path the directory path 
	 */
	public void setPath(String path) {
		GWT.log("selected path = " + path);
        fetchTableItems(path);
        // Remove the full path to the files
        String footerStr = null;
        
        if(path == null)
        	footerStr = "";
        else if(path.contains(properties.get("root.search")))
        	footerStr = path.substring(path.indexOf(properties.get("root.search")));
        else if(path.contains(properties.get("root2.search")))
        	footerStr = path.substring(path.indexOf(properties.get("root2.search")));
        else if(path.contains(properties.get("root3.search")))
        	footerStr = path.substring(path.indexOf(properties.get("root3.search")));
        else if(path.contains(properties.get("root4.search")))
        	footerStr = path.substring(path.indexOf(properties.get("root4.search")));
        
        footer.setHTML("Current path : " + footerStr);
	}
	
	/**
	 * Returns the underlining Tree for the widget
	 * @return the tree for this widget
	 */
	public Tree getTree() {
		return tree;
	}
	
	/**
	 * 
	 * @param it
	 */
	public void getChildren(TreeItem it) {
		path = findPath(it);
        GWT.log("updated path = " + path);
        fetchTreeItems(it, path);
	}

	/**
	 * @param expandTree the expandTree to set
	 */
	public void setExpandTree(boolean expandTree) {
		this.expandTree = expandTree;
	}

	/**
	 * @param tItemNameClicked the tItemNameClicked to set
	 */
	public void settItemNameClicked(String tItemNameClicked) {
		this.tItemNameClicked = tItemNameClicked;
	}

    private OpenHandler<TreeItem> getOpenHandler() {
        return new OpenHandler<TreeItem>() {
            public void onOpen(OpenEvent<TreeItem> oe) {
                GWT.log("Tree Item opened");
                targetItem = oe.getTarget();
                // stupid but would be useful.
                if (targetItem.getChildCount() > 1) {
                    return;
                }
                expandTree = false;
                getChildren(targetItem);
            }
        };
    }

    private SelectionHandler<TreeItem> getSelectionHandler() {
        return new SelectionHandler<TreeItem>() {
            public void onSelection(SelectionEvent<TreeItem> se) {
            	targetItem = se.getSelectedItem();
                path = findPath(targetItem);
                setPath(path);
            }
        };
    }

    private String findPath(TreeItem item) {
        TreeItem parent = item.getParentItem();
                
        if (parent == null) {
        	
        	if(item.getText().equals(properties.get("root.folder.name")))
            	return properties.get("root.directory"); 
            else if(item.getText().equals(properties.get("root2.folder.name")))
            	return properties.get("root2.directory");
            else if(item.getText().equals(properties.get("root3.folder.name")))
            	return properties.get("root3.directory");
            else 
            	return properties.get("root4.directory");
            
        } else {
            return findPath(parent) + properties.get("file.separator") + item.getText();
        }
    }

    private void fetchTreeItems(final TreeItem father, String path) {
        if (this.fileSystemSvc == null) {
            fileSystemSvc = GWT.create(FileSystemService.class);
        }
        AsyncCallback<FileWrapper[]> callback = new AsyncCallback<FileWrapper[]>() {

            public void onFailure(Throwable thrwbl) {
                //Something wrong. to be handled.
            	GWT.log("ERROR: " + thrwbl.getMessage());
            }
            public void onSuccess(FileWrapper[] results) {
            	GWT.log("Father " + father.getText());
                expandTreeItem(father, results);
                if(expandTree) {                	
                	father.setState(true);
                	father.setSelected(false);
                	
                	for(int i = 0; i < father.getChildCount(); i++) {
                		GWT.log("\tChild " + father.getChild(i).getText() + " Clicked Name " + tItemNameClicked);
                		if(father.getChild(i).getText().equals(tItemNameClicked)) {                			
                			father.getChild(i).setSelected(true);
                			tree.setSelectedItem(father.getChild(i));
                		}
                	}                	                	
                }
            }
        };
        fileSystemSvc.getContents(path, callback);
    }

    private void fetchTableItems(String path) {
        if (this.fileSystemSvc == null) {
            fileSystemSvc = GWT.create(FileSystemService.class);
        }
        AsyncCallback<FileWrapper[]> callback = new AsyncCallback<FileWrapper[]>() {

            public void onFailure(Throwable thrwbl) {
                //Something wrong. to be handled.
            	GWT.log("ERROR: " + thrwbl.getMessage());
            }
            public void onSuccess(FileWrapper[] results) {
                table.updateTableContent(results, true);
            }
        };
        fileSystemSvc.getContents(path, callback);
    }

    private void expandTreeItem(TreeItem father, FileWrapper[] files) {
        father.removeItems();
        for (FileWrapper file : files) {
            if (file.getKind() == FileType.DIR) {
                TreeItem newItem = new TreeItem(new OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(file.getName()));
                father.addItem(newItem);
                newItem.addItem(new OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml("Loading..."));
            }
        }
    }
}

