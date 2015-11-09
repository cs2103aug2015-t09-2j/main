package katnote.task;

import java.time.LocalDateTime;

import katnote.command.CommandDetail;
import katnote.utils.DateTimeUtils;
import katnote.utils.KatDateTime;

/**
 * This will be the association class used across KatNote. It will be the
 * standardized task object that will be passed between components.
 * 
 * @author sk
 *
 */
public class Task {

    // Private Variables
    private Integer _id;
    private String _title;
    private TaskType _taskType;
    private LocalDateTime _startDate;
    private LocalDateTime _endDate;
    private String _repeatOption;
    private LocalDateTime _terminateDate; // only for recurring tasks
    private String _description;
    private String _category;
    private Boolean _completed = false;

    // Messages
    private static final String MSG_ERR_PARSE_EXCEPTION = "Error: Unable to parse inputs to Task object. ";

    // Constructor of empty Task object
    public Task() {
        
    }

    /*
     * Constructor using CommandDetail
     */
    public Task(CommandDetail commandDetail) throws Exception {
        // Currently choosing DATE_FORMAT_LONG for all dates.
        try {
            setTitle(commandDetail.getTitle());
            setTaskType(commandDetail.getTaskType());
            
            if (commandDetail.getStartDate() != null) {
                setStartDate(commandDetail.getStartDate().toLocalDateTime());
            }
            
            if (commandDetail.getEndDate() != null) {
                setEndDate(commandDetail.getEndDate().toLocalDateTime());
            }
            
            if (commandDetail.getDueDate() != null) {
                setEndDate(commandDetail.getDueDate().toLocalDateTime());
            }
            
        } catch (Exception e) {
            throw new Exception(MSG_ERR_PARSE_EXCEPTION + e);
        }
    }

    public Task(String taskTitle, TaskType type) {
        setTitle(taskTitle);
        setTaskType(type);
    }
    
    // Constructor for duplicating task object without reference.
    public Task(Task task) {
        setID(task.getID());
        setTitle(task.getTitle());
        setTaskType(task.getTaskType());
        setStartDate(task.getStartDate());
        setEndDate(task.getEndDate());
        setRepeatOption(task.getRepeatOption());
        setTerminateDate(task.getTerminateDate());
        setDescription(task.getDescription());
        setCategory(task.getCategory());
        setCompleted(task.isCompleted());
    }

    // Getter and Setter

    public Integer getID() {
        return _id;
    }

    public void setID(Integer _id) {
        this._id = _id;
    }

    public String getTitle() {
        return _title;
    }

    public void setTitle(String _title) {
        this._title = _title;
    }

    public TaskType getTaskType() {
        return _taskType;
    }

    public void setTaskType(TaskType taskType) {
        _taskType = taskType;
    }

    public LocalDateTime getStartDate() {
        return _startDate;
    }

    public void setStartDate(LocalDateTime _startDate) {
        this._startDate = _startDate;
    }
    
    public void setStartDate(KatDateTime newStartDate) {
        _startDate = DateTimeUtils.updateDateTime(_startDate, newStartDate);
    }

    public LocalDateTime getEndDate() {
        return _endDate;
    }

    public void setEndDate(LocalDateTime _endDate) {
        this._endDate = _endDate;
    }
    
    public void setEndDate(KatDateTime newEndDate) {
        if (_endDate == null) {
            _endDate = newEndDate.toLocalDateTime();
        } else {
            _endDate = DateTimeUtils.updateDateTime(_endDate, newEndDate);
        }
    }

    public String getRepeatOption() {
        return _repeatOption;
    }

    public void setRepeatOption(String _repeatOption) {
        this._repeatOption = _repeatOption;
    }

    public LocalDateTime getTerminateDate() {
        return _terminateDate;
    }

    public void setTerminateDate(LocalDateTime _terminateDate) {
        this._terminateDate = _terminateDate;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String _description) {
        this._description = _description;
    }

    public String getCategory() {
        return _category;
    }

    public void setCategory(String _category) {
        this._category = _category;
    }

    public boolean isCompleted() {
        return _completed;
    }

    public void setCompleted(Boolean _completed) {
        this._completed = _completed;
    }

}
