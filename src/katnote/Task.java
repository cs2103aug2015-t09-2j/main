package katnote;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONArray;

/**
 * This will be the association class used across KatNote. It will be the standardized task object that will be passed between components.
 * @author sk
 *
 */
public class Task {

    // Private Variables
    private Integer _id;
    private String _title;
    private String _taskType;
    private Date _startDate;
    private Date _endDate;
    private String _repeatOption;
    private Date _terminateDate;
    private String _description;
    private String _category;
    private Boolean _completed;
    
    // Constants
    private static final int MAX_ARG_SIZE = 10;
    
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
    
    
    // Format
    private static final SimpleDateFormat DATE_FORMAT_LONG = new SimpleDateFormat("EEEE, MMM dd, yyyy HH:mm:ss a"); // Eg: "Friday, Jun 7, 2013 12:10:56 PM"
    private static final SimpleDateFormat DATE_FORMAT_DAY = new SimpleDateFormat("E, MMM dd yyyy"); // Eg: "Fri, June 7 2013"
    private static final SimpleDateFormat DATE_FORMAT_SHORT = new SimpleDateFormat("MMM dd, yyyy"); // Eg: "Jun 7, 2013"

    
    // Constructor using JSONArray
    public Task(JSONArray array) throws Exception {
        
        assert(array.size() == MAX_ARG_SIZE);
        
        String[] args = new String[MAX_ARG_SIZE];
        for (int i=0; i<MAX_ARG_SIZE; i++) {
            args[i] = (String) array.get(i);
        }
        // Currently choosing DATE_FORMAT_LONG for all dates.
        try {
            setID(Integer.parseInt(args[INDEX_ID]));
            setTitle(args[INDEX_TITLE]);
            setTaskType(args[INDEX_TASK_TYPE]);
            setStartDate(DATE_FORMAT_LONG.parse(args[INDEX_START_DATE]));
            setEndDate(DATE_FORMAT_LONG.parse(args[INDEX_END_DATE]));
            setRepeatOption(args[INDEX_REPEAT_OPTION]);
            setTerminateDate(DATE_FORMAT_LONG.parse(args[INDEX_TERMINATE_DATE]));
            setDescription(args[INDEX_DESCRIPTION]);
            setCategory(args[INDEX_CATEGORY]);
            setCompleted(Boolean.parseBoolean(args[INDEX_COMPLETED]));
        } catch (Exception e) {
            throw new Exception(MSG_ERR_PARSE_EXCEPTION + e);
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

    public String getTaskType() {
        return _taskType;
    }

    public void setTaskType(String _taskType) {
        this._taskType = _taskType;
    }

    public Date getStartDate() {
        return _startDate;
    }

    public void setStartDate(Date _startDate) {
        this._startDate = _startDate;
    }

    public Date getEndDate() {
        return _endDate;
    }

    public void setEndDate(Date _endDate) {
        this._endDate = _endDate;
    }

    public String getRepeatOption() {
        return _repeatOption;
    }

    public void setRepeatOption(String _repeatOption) {
        this._repeatOption = _repeatOption;
    }

    public Date getTerminateDate() {
        return _terminateDate;
    }

    public void setTerminateDate(Date _terminateDate) {
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

    public Boolean getCompleted() {
        return _completed;
    }

    public void setCompleted(Boolean _completed) {
        this._completed = _completed;
    }
    
}
