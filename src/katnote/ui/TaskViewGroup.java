//@@author A0125447E
package katnote.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * UI component that will house UI row components: TaskRow and TaskDetailedRow.
 * The UI row components are stacked on vertically
 * 
 * @author Wz
 *
 */
public class TaskViewGroup extends AnchorPane {
    private static final String LAYOUT_FXML = "/katnote/resources/ui/TaskViewGroup.fxml";

    @FXML
    private Label groupHeader;

    @FXML
    private VBox taskViewList;

    /**
     * Constructs a TaskViewGroup object with the given header
     * 
     * @param header name string of the group
     */
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

    public VBox getListChildren() {
        return taskViewList;
    }

    public String getGroupHeaderText() {
        return groupHeader.getText();
    }

}
