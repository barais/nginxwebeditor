package org.kevoree.nginx.client.dialogs;

import org.kevoree.nginx.client.FileSystemService;
import org.kevoree.nginx.client.FileSystemServiceAsync;
import org.kevoree.nginx.client.FileSystemTreeWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * New folder dialog for the file manager
 *
 */
public class NewDialogWidget extends Composite {

	private FileSystemServiceAsync fileSystemSvc = GWT.create(FileSystemService.class);
	private FileSystemTreeWidget fsTree;
	private DialogBox newDialog;
	private TextBox nameTxtBox;
	private Label descLbl;
    private String title = "New Folder";
    private String folderName = "";
    private String path = "";
    
    /**
     * Creates a help dialog
     */
    public NewDialogWidget(FileSystemTreeWidget fsTree){
        VerticalPanel panel = new VerticalPanel();
        this.fsTree = fsTree;
        descLbl = new Label();
        newDialog = createDialog();
        newDialog.hide();
        panel.add(createOpenButton());        
        initWidget(panel);
    }

    /**
     * Creates the command to open the dialog
     * @return the command
     */
    public Command createOpenCommand(){    	
        return new Command() {
            public void execute() {
            	folderName = "";	// clear folderName
            	path = fsTree.getPath();
            	nameTxtBox.setText("");
            	descLbl.setText("You are creating a folder in the \"" +
    	        		fsTree.getTree().getSelectedItem().getText() + 
    	        		"\" folder. Type the name of your new folder:");
                newDialog.show();
            }
        };
    }
    
    /**
     * Displays the dialog
     */
    public void hide() {
    	newDialog.hide();
    }
    
    /**
     * Resets the dialog's name field
     */
    public void reset() {
    	nameTxtBox.setText("");
    }

    private DialogBox createDialog(){
        VerticalPanel panel = new VerticalPanel();
        DialogBox dialog = new DialogBox(true);
        dialog.setAnimationEnabled(true);
        dialog.center();
        dialog.setText(title);
        dialog.setGlassEnabled(true);
        panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        panel.add(descLbl);
        nameTxtBox = new TextBox();
        panel.add(nameTxtBox);        
        HorizontalPanel hPanel = new HorizontalPanel();        
        hPanel.add(createOKButton());
        hPanel.add(createCancelButton());
        panel.add(hPanel);
        dialog.add(panel);
        
        return dialog;
    }

    private Button createOpenButton(){
        return new Button(title, new ClickHandler(){
            public void onClick(ClickEvent ce) {
            	nameTxtBox.setText("");
            	descLbl.setText("You are creating a folder in the \"" +
            	        		fsTree.getTree().getSelectedItem().getText() + 
            	        		"\" folder.<br/>Type the name of your <b>new</b> folder:");
                newDialog.show();
            }
        });
    }

    private Button createOKButton(){    	
        return new Button("OK", new ClickHandler(){
            public void onClick(ClickEvent ce) {
            	newDialog.hide();
            	folderName = nameTxtBox.getText();
            	if(folderName == null || folderName.length() <= 1) {
            		Window.alert("Your folder has to be more than one character!");
            		return;
            	} else if(folderName.contains("\\") || folderName.contains("/")) {
            		Window.alert("Your folder cannot contain a '\\' or '/'!");
            		return;
            	}
            	
            	/*
            	Client Code can't use File to create or determine if a file exists
            	File f = new File(path+"/"+folderName);
            	if(f.exists()) {
            		Window.alert("The folder '" + folderName + "' already exists!");
            		return;
            	}*/
            	            	
            	if (fileSystemSvc == null) {
                    fileSystemSvc = GWT.create(FileSystemService.class);
                }
                AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {

                    public void onFailure(Throwable thrwbl) {
                        //Something wrong. to be handled.
                    	GWT.log("ERROR: " + thrwbl.getMessage());
                    }
                    public void onSuccess(Boolean result) {
                    	fsTree.updateTree(); // updates tree and table
                    }
                };                
                fileSystemSvc.mkDir(path + FileSystemTreeWidget.getFileSeparator() + folderName, callback);                            	
            }
        });
    }
    
    private Button createCancelButton(){    	
        return new Button("Cancel", new ClickHandler(){
            public void onClick(ClickEvent ce) {
            	newDialog.hide();
            	folderName = "";
            	nameTxtBox.setText("");
            }
        });        
    }
}
