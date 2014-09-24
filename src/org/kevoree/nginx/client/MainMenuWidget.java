package org.kevoree.nginx.client;

import org.kevoree.nginx.client.dialogs.AboutDialogWidget;
import org.kevoree.nginx.client.dialogs.HelpDialogWidget;
import org.kevoree.nginx.client.dialogs.NewDialogWidget;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuBar;


/**
 * The main top application menu for the application.
 *
 */
public class MainMenuWidget extends Composite {
		
	private MenuBar menu;
	private NewDialogWidget newDialog;
    private HelpDialogWidget helpDialog;
    private AboutDialogWidget aboutDialog;
    private boolean isAdmin = true;
    
    /**
     * Creates the main file menu
     */
    public MainMenuWidget(FileSystemTreeWidget fsTree, boolean isAdmin) {
    	this.isAdmin = isAdmin;
        helpDialog = new HelpDialogWidget();
        aboutDialog = new AboutDialogWidget();
        if(isAdmin) {
        	newDialog = new NewDialogWidget(fsTree);
        	fsTree.setMainMenu(this);
        }

        createMenu();
        initWidget(menu);
    }
    
    /**
     * Returns the new directory dialog
     * @return the new directory dialog
     */
    public NewDialogWidget getNewDialogWidget() {
    	return newDialog;
    }
    
    /**
     * Sets the about dialog message
     * @param msg html about message
     */
    public void setAboutMsg(String msg) {
    	aboutDialog.setMsgTxt(msg);
    }
    
    /**
     * Sets the help dialog message
     * @param msg html help message
     */
    public void setHelpMsg(String msg) {
    	helpDialog.setMsgTxt(msg);
    }

    private void createMenu() {
        menu = new MenuBar();
        /* Admin Features */
        if(isAdmin) {
	        MenuBar fileMenu = new MenuBar(true);
	        fileMenu.addItem("New folder", newDialog.createOpenCommand());
	        /*fileMenu.addItem("Rename", new Command() {
	        	   	public void execute() {
	        	   		Window.alert("Rename");
	        	   	}
	        	});*/
	        menu.addItem("File", fileMenu);
        }
        /* END Admin Features */
        
        MenuBar helpMenu = new MenuBar(true);
        helpMenu.addItem("Help", helpDialog.createOpenCommand());
        helpMenu.addItem("About", aboutDialog.createOpenCommand());
        menu.addItem("Help", helpMenu);               
    }
}
