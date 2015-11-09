//@@author A0125447E
package katnote.ui;

import java.io.IOException;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * TaskViewer is the UI class that houses a list of TaskViewGroup objects The
 * TaskViewGroup objects are displayed vertically stacked on top of each other.
 * 
 * @author Wz
 *
 */
public class TaskViewer extends AnchorPane {
    private static final String LAYOUT_FXML = "/katnote/resources/ui/TaskViewer.fxml";

    @FXML
    private VBox taskViewGroupList;

    public TaskViewer() {
        loadFXML();
        taskViewGroupList.setFillWidth(true);
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

    public void clearViewer() {
        taskViewGroupList.getChildren().clear();
    }

    public void loadTaskFormat(TaskViewFormatter listFormat) {
        clearViewer();
        updateTaskView(listFormat);
    }

    private void updateTaskView(TaskViewFormatter listFormat) {
        ArrayList<TaskViewGroup> viewList = listFormat.getFormattedViewGroupList();

        for (TaskViewGroup viewGroup : viewList) {
            taskViewGroupList.getChildren().add(viewGroup);
        }
    }

}
