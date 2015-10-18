package katnote.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class TaskRow extends AnchorPane {

    @FXML
    private Label taskDescriptionLabel;

    @FXML
    private Label timeLabel;

    private static final String LAYOUT_FXML = "/katnote/resources/ui/TaskRow.fxml";

    public TaskRow(String taskDescription) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(LAYOUT_FXML));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setTaskDescription(taskDescription);
    }

    public void setTaskDescription(String text) {
        taskDescriptionLabel.setText(text);
    }

}
