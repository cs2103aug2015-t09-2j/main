//@@author A0125447E
package katnote.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class CommandBarController {
    private static final String NORMAL_STYLE = "-fx-text-fill: black;";
    private static final String ERROR_STYLE = "-fx-text-fill: red;";

    private GraphicalUserInterface mainUI;

    @FXML
    private TextField commandBar;
    @FXML
    private Label responseLabel;

    @FXML
    void onKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!commandBar.getText().isEmpty()) {
                mainUI.handleCommandInput(commandBar.getText());
                commandBar.clear();
            }
        }
    }

    @FXML
    void onInputClicked(MouseEvent event) {
        if (!commandBar.getText().isEmpty()) {
            mainUI.handleCommandInput(commandBar.getText());
            commandBar.clear();
        }
    }

    /**
     * Binds the main UI to the controller for forwarding controller events
     * 
     * @param mainUI
     */
    public void setMainUI(GraphicalUserInterface mainUI) {
        this.mainUI = mainUI;
    }

    /**
     * Display the error response and in red
     * 
     * @param response Error response message
     */
    public void setErrorText(String response) {
        setResponseText(response, true);
    }

    /**
     * Display response message and set its color.
     * 
     * @param response Response message to display
     * @param isError true for red and false for black
     */
    public void setResponseText(String response, boolean isError) {
        setResponseErrorState(isError);
        responseLabel.setText(response);
    }

    private void setResponseErrorState(boolean isError) {
        if (isError) {
            responseLabel.setStyle(ERROR_STYLE);
        } else {
            responseLabel.setStyle(NORMAL_STYLE);
        }
    }

}