package org.kevoree.nginx.client;

import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;

/**
 * PopupMenu for right clicks over the table view of the application for
 * uploading and creating new directories in the application.
 *
 */
public class PopupMenu extends DecoratedPopupPanel {
	
	/**
	 * Creates a popup menu for the admin features.
	 * This popup uses the commands from the main menu
	 * @param fsTree the application's FileSystemTreeWidget
	 */
	public PopupMenu(FileSystemTreeWidget fsTree) {
		super(true);		
    	MenuBar popMenuBar = new MenuBar(true);
		MenuItem newFolderItem = new MenuItem("New Folder", fsTree.getMainMenu().getNewDialogWidget().createOpenCommand());		
		popMenuBar.addItem(newFolderItem);
		setAutoHideEnabled(true);		
		setWidget(popMenuBar);		  
	}	
}

