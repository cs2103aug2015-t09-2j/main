//@@author A0125447E
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

/**
 * Custom UI component for events and normal tasks.
 * Displays the task title and a tick that represents if the task is done
 * Also displays the date elements.
 * @author Wz
 *
 */
public class TaskDetailedRow extends AnchorPane {

    private static final String CHECKMARK_CLASS_NAME = "check";
    private static final String NORMAL_TASK_DATE_FORMAT = "Due: %1s %2s";
    private static final String NORMAL_TASK_TIME_FORMAT = "Due: %1s";
    private static final String EVENT_DATE_TIME_FORMAT = "Start: %1s           End: %2s";
    private static final String DATE_TIME_PATTERN = "dd MMM yy hh:mm a";
    private static final String TIME_PATTERN = "hh:mm a";
    private static final String DATE_PATTERN = "dd MMM yy";
    private static final String LAYOUT_FXML = "/katnote/resources/ui/TaskDetailedRow.fxml";
    private static final String DISPLAY_INDEX_STRING = "%d.";

    @FXML
    private Label taskDescriptionLabel;
    @FXML
    private Label dateTimeLabel;
    @FXML
    private Pane checkmarkPane;
    @FXML
    private Label viewIndexLabel;
    private Task task;
    private int index;
    private boolean isDateHidden;

    public TaskDetailedRow(Task task, int viewIndex, boolean isDateHidden) {
        loadFXML();
        this.task = task;
        this.index = viewIndex;
        this.isDateHidden = isDateHidden;

        setIndexProperty(viewIndex);
        setDescriptionProperty();
        setDateTimeProperty();
        setCompletedProperty();
    }

    private void setIndexProperty(int viewIndex) {
        viewIndexLabel.setText(String.format(DISPLAY_INDEX_STRING, viewIndex));
    }

    private void setDescriptionProperty() {
        taskDescriptionLabel.setText(task.getTitle());
    }

    public String getDescription() {
        return taskDescriptionLabel.getText();
    }

    public String getDateString() {
        return dateTimeLabel.getText();
    }

    public String getIndexString() {
        return viewIndexLabel.getText();
    }

    private void setDateTimeProperty() {
        if (task.getTaskType() == TaskType.NORMAL) {
            LocalDateTime date = task.getEndDate();
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATE_PATTERN);
            String dateString = date.format(dateFormat);
            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern(TIME_PATTERN);
            String timeString = date.format(timeFormat);
            String dateTime;
            if (isDateHidden) {
                dateTime = String.format(NORMAL_TASK_TIME_FORMAT, timeString);
            } else {
                dateTime = String.format(NORMAL_TASK_DATE_FORMAT, dateString, timeString);
            }
            dateTimeLabel.setText(dateTime);
        } else if (task.getTaskType() == TaskType.EVENT) {
            LocalDateTime startDate = task.getStartDate();
            LocalDateTime endDate = task.getEndDate();
            DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
            String startDateString = startDate.format(dateTimeFormat);
            String endDateString = endDate.format(dateTimeFormat);
            String dateTime = String.format(EVENT_DATE_TIME_FORMAT, startDateString, endDateString);
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
