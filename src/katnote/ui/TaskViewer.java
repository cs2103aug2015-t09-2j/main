package katnote.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class TaskViewer extends AnchorPane{

    @FXML
    private VBox taskViewGroupList;

    private static final String LAYOUT_FXML = "/katnote/resources/ui/TaskViewer.fxml";

    public TaskViewer(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource(LAYOUT_FXML));
        loader.setController(this);
        loader.setRoot(this);
        
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void clearTaskGroups(){
        taskViewGroupList.getChildren().clear();
    }
    
    public void addNewTaskViewGroup(TaskViewGroup taskViewGroup){
        taskViewGroupList.getChildren().add(taskViewGroup);
    }
}
