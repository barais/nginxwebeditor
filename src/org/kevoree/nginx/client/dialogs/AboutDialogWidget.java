package org.kevoree.nginx.client.dialogs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * File Manager's about dialog box. Used to provide information about the 
 * file manager to the users.
 * 
 */
public class AboutDialogWidget extends Composite {

	private DialogBox aboutDialog;
    private String title = "About";
    private HTML msgTxt;
    
    /**
     * Creates an about dialog for the file manager
     */
    public AboutDialogWidget(){
        VerticalPanel panel = new VerticalPanel();
        aboutDialog = createDialog();
        aboutDialog.hide();
        panel.add(createOpenButton());
        initWidget(panel);        
    }
        
    /**
     * Hides the dialog box
     */
    public void hide() {
    	aboutDialog.hide();
    }

    /**
     * Displays the dialog
     * @return the command for opening the dialog
     */
    public Command createOpenCommand(){
        return new Command() {
            public void execute() {
                aboutDialog.show();
            }
        };
    }
    
    /**
     * Sets the html message for the about dialog
     * @param msg html message
     */
    public void setMsgTxt(String msg) {
    	msgTxt.setHTML(msg);
    }
    
    private DialogBox createDialog(){
        VerticalPanel panel = new VerticalPanel();
        DialogBox dialog = new DialogBox(true);
        dialog.setAnimationEnabled(true);
        dialog.center();
        dialog.setText(title);
        dialog.setGlassEnabled(true);
        msgTxt = new HTML();
        panel.add(msgTxt);
        panel.add(createCloseButton());
        dialog.add(panel);

        return dialog;
    }    

    private Button createOpenButton(){
        return new Button(title, new ClickHandler(){
            public void onClick(ClickEvent ce) {
                aboutDialog.show();
            }
        });
    }

    private Button createCloseButton(){
        return new Button("close", new ClickHandler(){
            public void onClick(ClickEvent ce) {
               aboutDialog.hide();
            }
        });
    }
}
