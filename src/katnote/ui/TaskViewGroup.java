package katnote.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class TaskViewGroup extends AnchorPane {
    private static final String LAYOUT_FXML = "/katnote/resources/ui/TaskViewGroup.fxml";

    @FXML
    private Label groupHeader;

    @FXML
    private VBox taskViewList;

    public TaskViewGroup(String header) {
        loadFXML();
        groupHeader.setText(header);
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

    public void addTaskRow(TaskRow row) {
        taskViewList.getChildren().add(row);
    }

    public void addDetialedTaskRow(TaskDetailedRow row) {
        taskViewList.getChildren().add(row);
    }

}
