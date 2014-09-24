package org.kevoree.nginx.client;

import java.util.HashMap;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;

/**
 * File Manager has two modes: Admin and normal mode.
 * The admin mode allows you to upload, rename, and delete files
 * The normal mode only allows you to download files.
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Filemanager implements EntryPoint {
	
	private static HashMap<String, String> properties;
	private PropertiesServiceAsync propertiesSvc = GWT.create(PropertiesService.class);
	private static FileSystemTreeWidget fsTree;
	private static FileTableWidget fsTable;
	private static MainMenuWidget mmw;
	
	/**
	 * This is the entry point method.
	 * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
	 */	
	public void onModuleLoad() {
		
		String aparam = Window.Location.getParameter("t"); 	// 'a' for admin mode -- admin mode can upload
		boolean adm = false;								// set to false
		if(aparam != null)
			adm = true;
		
		getProperties();	// retrieve properties from the servlet

		DockLayoutPanel mainPanel = new DockLayoutPanel(Unit.EM);		
		FlowPanel headerPanel = new FlowPanel();
				  
		//Files Table
        fsTable = new FileTableWidget(adm);        
        HTML footer = new HTML();
        //Files Tree with a temp root name till getProperties returns
        fsTree = new FileSystemTreeWidget("Root", fsTable, footer);
        //MainMenu
		mmw = new MainMenuWidget(fsTree, adm);		
        headerPanel.add(mmw);   
        mainPanel.addNorth(headerPanel, 2);
        
        //Footer on south        
        DOM.setElementAttribute(footer.getElement(), "id", "footer");
        mainPanel.addSouth(footer, 2);
                
        //Main content of main layout is a Navigator(west) + content(east), with a splitter in between.
        SplitLayoutPanel mainContentPanel = new SplitLayoutPanel();

        //FileSystem Tree on west
        ScrollPanel treeScrollPanel = new ScrollPanel();        
        treeScrollPanel.add(fsTree);        
        mainContentPanel.addWest(treeScrollPanel, 200);

        //Main content area
        DockLayoutPanel contentMainPanel = new DockLayoutPanel(Unit.EM);
        ScrollPanel contentScrollPanel = new ScrollPanel();

        //Adding FilesTable
        contentScrollPanel.add(fsTable);
        contentMainPanel.add(contentScrollPanel);
        DOM.setElementAttribute(contentMainPanel.getElement(), "id", "content");
        mainContentPanel.add(contentMainPanel);
        
        mainPanel.add(mainContentPanel);
        RootLayoutPanel.get().add(mainPanel);        
		RootPanel.get("loading").setVisible(false);		
	}
	
	private void getProperties() {
        if (propertiesSvc == null) {
        	propertiesSvc = GWT.create(PropertiesService.class);
        }
        AsyncCallback<HashMap<String, String>> callback = new AsyncCallback<HashMap<String, String>>() {

            public void onFailure(Throwable thrwbl) {
            	GWT.log("ERROR: " + thrwbl.getMessage());
            	Window.alert("ERROR: " + thrwbl.getMessage());
            }
            public void onSuccess(HashMap<String, String> results) {
            	properties = results;
            	if(properties.size() > 0) {
            		FileSystemTreeWidget.setProperties(properties);
            		fsTree.createFileSystemTree();
            		FileTableWidget.setDownloadServlet(properties.get("download.servlet"));
            		mmw.setAboutMsg(properties.get("about.msg"));
            		mmw.setHelpMsg(properties.get("help.msg"));
            	} else {
            		Window.alert("Please contact your administrator!\nCan't get configuration. Properties file is missing!");
            	}
            }
        };
        propertiesSvc.getProperties(callback);        
    }
}

