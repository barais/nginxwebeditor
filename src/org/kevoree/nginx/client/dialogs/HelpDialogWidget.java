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
 * Help dialog for the file manager
 *
 */
public class HelpDialogWidget extends Composite {

	private DialogBox helpDialog;
    private String title = "Help";
    private HTML msgTxt;
    
    /**
     * Creates a help dialog
     */
    public HelpDialogWidget(){
        VerticalPanel panel = new VerticalPanel();
        helpDialog = createDialog();
        helpDialog.hide();
        panel.add(createOpenButton());
        initWidget(panel);        
    }

    /**
     * Creates the command to open the dialog
     * @return the command
     */
    public Command createOpenCommand() {
        return new Command() {
            public void execute() {            	
                helpDialog.show();
            }
        };
    }
    
    /**
     * Displays the dialog
     * @return the command for opening the dialog
     */
    public void hide() {
    	helpDialog.hide();
    }
    
    /**
     * Sets the html message for the help dialog
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
                helpDialog.show();
            }
        });
    }

    private Button createCloseButton(){
        return new Button("close", new ClickHandler(){
            public void onClick(ClickEvent ce) {
                helpDialog.hide();
            }
        });
    }
}
