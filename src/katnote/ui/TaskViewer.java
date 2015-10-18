package katnote.ui;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import katnote.task.Task;

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
