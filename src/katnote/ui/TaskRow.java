package katnote.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class TaskRow extends AnchorPane {
    private static final String CHECKMARK_CLASS_NAME = "check";
    private static final String LAYOUT_FXML = "/katnote/resources/ui/TaskRow.fxml";

    @FXML
    private Label taskDescriptionLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private Pane checkmarkPane;


    public TaskRow(String taskDescription, boolean isDone) {
        loadFXML();
        setTaskDescription(taskDescription);
        setCompletedProperty(isDone);
    }

    public String getDescription() {
        return taskDescriptionLabel.getText();
    }

    private void setCompletedProperty(boolean isDone) {
        if (isDone) {
            checkmarkPane.getStyleClass().add(CHECKMARK_CLASS_NAME);
        }
    }

    private void loadFXML() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(LAYOUT_FXML));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setTaskDescription(String text) {
        taskDescriptionLabel.setText(text);
    }

}
