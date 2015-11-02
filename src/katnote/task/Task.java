package katnote.task;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.json.simple.JSONArray;

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

    // Constants
    private static final int MAX_ARG_SIZE = 10;
    private static final String NULL_DATE = "null";
    private static final String STR_TRUE = "true";
    private static final String STR_FALSE = "false";

    private static final int INDEX_ID = 0;
    private static final int INDEX_TITLE = 1;
    private static final int INDEX_TASK_TYPE = 2;
    private static final int INDEX_START_DATE = 3;
    private static final int INDEX_END_DATE = 4;
    private static final int INDEX_REPEAT_OPTION = 5;
    private static final int INDEX_TERMINATE_DATE = 6;
    private static final int INDEX_DESCRIPTION = 7;
    private static final int INDEX_CATEGORY = 8;
    private static final int INDEX_COMPLETED = 9;

    // Messages
    private static final String MSG_ERR_PARSE_EXCEPTION = "Error: Unable to parse inputs to Task object. ";

    // Constructor using JSONArray
    public Task(JSONArray array) throws Exception {

        assert (array.size() == MAX_ARG_SIZE);

        // ideally this segment should be moved to the loading part and not
        // within
        // the tasks
        String[] args = new String[MAX_ARG_SIZE];
        for (int i = 0; i < MAX_ARG_SIZE; i++) {
            args[i] = (String) array.get(i);
        }
        // Currently choosing DATE_FORMAT_LONG for all dates.
        try {
            setID(Integer.parseInt(args[INDEX_ID]));
            setTitle(args[INDEX_TITLE]);
            // impomptu code to allow the code to still work
            if (args[INDEX_TASK_TYPE] != null) {
                switch (args[INDEX_TASK_TYPE]) {
                    case "EVENT" :
                        setTaskType(TaskType.EVENT);
                        break;
                    case "FLOATING" :
                        setTaskType(TaskType.FLOATING);
                        break;
                    case "NORMAL" :
                    default :
                        setTaskType(TaskType.NORMAL);
                }
            }
            setStartDate(stringToDate(args[INDEX_START_DATE]));
            setEndDate(stringToDate(args[INDEX_END_DATE]));
            setRepeatOption(args[INDEX_REPEAT_OPTION]);
            setTerminateDate(stringToDate(args[INDEX_TERMINATE_DATE]));
            setDescription(args[INDEX_DESCRIPTION]);
            setCategory(args[INDEX_CATEGORY]);
            setCompleted(stringToBool(args[INDEX_COMPLETED]));
        } catch (Exception e) {
            throw new Exception(MSG_ERR_PARSE_EXCEPTION + e);
        }
    }

    /*
     * Constructor using CommandDetail
     */
    public Task(CommandDetail commandDetail) throws Exception {
        // Currently choosing DATE_FORMAT_LONG for all dates.
        try {
            // TODO: setID(some_number);
            setTitle(commandDetail.getTitle());
            setTaskType(commandDetail.getTaskType());
            setStartDate(commandDetail.getStartDate().toLocalDateTime());
            // TODO: setRepeatOption(args[INDEX_REPEAT_OPTION]);
            if (commandDetail.getEndDate() != null) {
                setEndDate(commandDetail.getEndDate().toLocalDateTime());
            }
            if (commandDetail.getDueDate() != null) {
                setEndDate(commandDetail.getDueDate().toLocalDateTime());
            }
            // TODO:
            // setDescription(commandDetail.getString(CommandProperties.TASK_DESCRIPTION));
            // TODO: setCategory(args[INDEX_CATEGORY]);
            // TODO: setCompleted(Boolean.parseBoolean(args[INDEX_COMPLETED]));
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
    
    //Empty  Constructor for testing
    public Task() { }
    
    // Helper Methods

    private LocalDateTime stringToDate(String dateStr) throws ParseException {
        if (dateStr.equals(NULL_DATE)) {
            return null;
        } else {
            LocalDateTime date = LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
            return date;
        }
    }

    private Boolean stringToBool(String bool) {
        if (bool.equals(STR_TRUE)) {
            return true;
        } else {
            return false;
        }
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
        _endDate = DateTimeUtils.updateDateTime(_endDate, newEndDate);
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
