//@@author A0125447E
package katnote.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

/**
 * Custom UI component for floating tasks
 * Displays the task title and a tick that represents if the task is done
 * @author Wz
 *
 */
public class TaskRow extends AnchorPane {
    private static final String CHECKMARK_CLASS_NAME = "check";
    private static final String LAYOUT_FXML = "/katnote/resources/ui/TaskRow.fxml";
    private static final String DISPLAY_INDEX_STRING = "%d.";

    @FXML
    private Label taskDescriptionLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private Label viewIndexLabel;

    @FXML
    private Pane checkmarkPane;

    public TaskRow(int viewIndex, String taskDescription, boolean isDone) {
        loadFXML();
        setIndexProperty(viewIndex);
        setTaskDescription(taskDescription);
        setCompletedProperty(isDone);
    }

    private void setIndexProperty(int viewIndex) {
        viewIndexLabel.setText(String.format(DISPLAY_INDEX_STRING, viewIndex));
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
