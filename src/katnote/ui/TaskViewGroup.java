package katnote.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class TaskViewGroup extends AnchorPane {

    @FXML
    private Label groupHeader;

    @FXML
    private VBox taskViewList;
    
    private static final String LAYOUT_FXML = "/katnote/resources/ui/TaskViewGroup.fxml";
    
    public TaskViewGroup(String header){
        FXMLLoader loader = new FXMLLoader(getClass().getResource(LAYOUT_FXML));
        loader.setController(this);
        loader.setRoot(this);
        
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        taskViewList.setFillWidth(true);
        groupHeader.setText(header);
    }
    
    public void addTaskRows(String[] taskDescriptions){
        for(String d : taskDescriptions){
            taskViewList.getChildren().add(new TaskRow(d, false));
        }
    } 
    public void addTaskRow(String taskDescription, boolean isDone){
        taskViewList.getChildren().add(new TaskRow(taskDescription, isDone));
    }

}
