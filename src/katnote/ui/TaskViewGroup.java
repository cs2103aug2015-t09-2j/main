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
    
    public TaskViewGroup(String header, String[] taskDescriptions){
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
        addTaskRows(taskDescriptions);
    }
    
    private void addTaskRows(String[] taskDescriptions){
        for(String d : taskDescriptions){
            taskViewList.getChildren().add(new TaskRow(d));
        }
    }

}
