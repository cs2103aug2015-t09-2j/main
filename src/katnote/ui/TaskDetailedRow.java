package katnote.ui;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import katnote.task.Task;
import katnote.task.TaskType;

public class TaskDetailedRow extends AnchorPane {

    private static final String CHECKMARK_CLASS_NAME = "check";
    private static final String NORMAL_TASK_DATE_FORMAT = "Due: %1s %2s ";
    private static final String NORMAL_TASK_TIME_FORMAT = "Due: %1s";
    private static final String TIME_PATTERN = "hh:mm a";
    private static final String DATE_PATTERN = "dd MMM yy";
    private static final String LAYOUT_FXML = "/katnote/resources/ui/TaskDetailedRow.fxml";
    
    @FXML
    private Label taskDescriptionLabel;
    @FXML
    private Label dateTimeLabel;
    @FXML
    private Pane checkmarkPane;
    private Task task;
    private int index;
    private boolean isDateHidden;

    public TaskDetailedRow(Task task, int index){
        this(task, index, false);
    }
    
    public TaskDetailedRow(Task task, int index, boolean isDateHidden) {
        loadFXML();
        this.task = task;
        this.index = index;
        this.isDateHidden = isDateHidden;
        
        setDescriptionProperty();
        setDateTimeProperty();
        setCompletedProperty();
    }
    private void setDescriptionProperty() {
        taskDescriptionLabel.setText(index + ". " + task.getTitle());
    }
    private void setDateTimeProperty() {
        if(task.getTaskType() == TaskType.NORMAL){
            LocalDateTime date = task.getEndDate();
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATE_PATTERN);
            String dateString = date.format(dateFormat);            
            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern(TIME_PATTERN);
            String timeString = date.format(timeFormat);
            String dateTime;
            if(isDateHidden){
                dateTime = String.format(NORMAL_TASK_TIME_FORMAT, timeString);                
            } else {
                dateTime = String.format(NORMAL_TASK_DATE_FORMAT, dateString, timeString);                  
            }
            
            dateTimeLabel.setText(dateTime);
        }
    }

    private void setCompletedProperty() {
        if (task.isCompleted()) {
            checkmarkPane.getStyleClass().add(CHECKMARK_CLASS_NAME);
        }
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

}
