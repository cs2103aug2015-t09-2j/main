package katnote.ui;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import katnote.Task;

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
        taskViewGroupList.setFillWidth(true);
    }
    public void clearViewer(){
        taskViewGroupList.getChildren().clear();
    }
    
    public void addNewTaskViewGroup(TaskViewGroup taskViewGroup){
        taskViewGroupList.getChildren().add(taskViewGroup);
    }
    //To-do: modify TaskRow to allow for more details such as time
    public void loadListOfGroupedTasks(Task[][] arrayOfTaskGroups, String[] groupHeadings){
        assert(arrayOfTaskGroups.length == groupHeadings.length);
        
        for(int i = 0; i < arrayOfTaskGroups.length; i++){
            TaskViewGroup viewGroup = new TaskViewGroup(groupHeadings[i]);
            Task[] tasksInGroup = arrayOfTaskGroups[i];
            for(Task t : tasksInGroup){
                viewGroup.addTaskRow(t.getTitle());
            }
            addNewTaskViewGroup(viewGroup);
        }
    }
    
    public void loadDetailedListOfTask(Task[] tasks){
        for(Task t : tasks){
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
            Date taskDate = t.getTerminateDate();
            String dateString = dateFormat.format(taskDate);
            String timeString = timeFormat.format(taskDate);
            String dateTime = "Due: " + dateString + " " + timeString;            
            TaskDetailedRow row = new TaskDetailedRow(t.getTitle(), dateTime);
            taskViewGroupList.getChildren().add(row);
            
        }
    }
}
