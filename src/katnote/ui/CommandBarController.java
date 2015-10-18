package katnote.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class CommandBarController {
    private GraphicalUserInterface mainUI;

    @FXML
    private TextField commandBar;
    @FXML
    private Label responseLabel;

    @FXML
    void onKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            mainUI.handleCommandInput(this, commandBar.getText());
            commandBar.clear();
        }
    }

    public void setMainUI(GraphicalUserInterface mainUI) {
        this.mainUI = mainUI;
    }

    public void setResponseText(String response) {
        setResponseText(response, false);
    }

    public void setResponseText(String response, boolean isError) {
        if (isError) {
            responseLabel.setStyle("-fx-text-fill: red;");
        } else {
            responseLabel.setStyle("-fx-text-fill: black;");
        }
        responseLabel.setText(response);
    }

}