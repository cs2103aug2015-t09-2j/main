package katnote.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class TaskDetailedRow extends AnchorPane {

    @FXML
    private Label taskDescriptionLabel;

    @FXML
    private Label dateTimeLabel;

    private static final String LAYOUT_FXML = "/katnote/resources/ui/TaskDetailedRow.fxml";

    public TaskDetailedRow(String description, String dateTime) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(LAYOUT_FXML));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        taskDescriptionLabel.setText(description);
        dateTimeLabel.setText(dateTime);
    }

}
