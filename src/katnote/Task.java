package katnote;

import java.text.SimpleDateFormat;
import java.util.Date;

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
    private static final String MSG_ERR_PARSE_EXCEPTION = "Error: Unable to parse inputs to Task object. ";
    private static final int MAX_ARG = 10;
    private static final SimpleDateFormat DATE_FORMAT_LONG = new SimpleDateFormat("EEEE, MMM dd, yyyy HH:mm:ss a"); // Eg: "Friday, Jun 7, 2013 12:10:56 PM"
    private static final SimpleDateFormat DATE_FORMAT_DAY = new SimpleDateFormat("E, MMM dd yyyy"); // Eg: "Fri, June 7 2013"
    private static final SimpleDateFormat DATE_FORMAT_SHORT = new SimpleDateFormat("MMM dd, yyyy"); // Eg: "Jun 7, 2013"

    // Constructor
    public Task(String[] args) throws Exception {
        
        assert(args.length == MAX_ARG);
        
        // Currently choosing DATE_FORMAT_LONG for all dates.
        try {
            set_id(Integer.parseInt(args[0]));
            set_title(args[1]);
            set_taskType(args[2]);
            set_startDate(DATE_FORMAT_LONG.parse(args[3]));
            set_endDate(DATE_FORMAT_LONG.parse(args[4]));
            set_repeatOption(args[5]);
            set_terminateDate(DATE_FORMAT_LONG.parse(args[6]));
            set_description(args[7]);
            set_category(args[8]);
            set_completed(Boolean.parseBoolean(args[9]));
        } catch (Exception e) {
            throw new Exception(MSG_ERR_PARSE_EXCEPTION + e);
        }
    }

    // Getter and Setter
    
    public Integer get_id() {
        return _id;
    }

    public void set_id(Integer _id) {
        this._id = _id;
    }

    public String get_title() {
        return _title;
    }

    public void set_title(String _title) {
        this._title = _title;
    }

    public String get_taskType() {
        return _taskType;
    }

    public void set_taskType(String _taskType) {
        this._taskType = _taskType;
    }

    public Date get_startDate() {
        return _startDate;
    }

    public void set_startDate(Date _startDate) {
        this._startDate = _startDate;
    }

    public Date get_endDate() {
        return _endDate;
    }

    public void set_endDate(Date _endDate) {
        this._endDate = _endDate;
    }

    public String get_repeatOption() {
        return _repeatOption;
    }

    public void set_repeatOption(String _repeatOption) {
        this._repeatOption = _repeatOption;
    }

    public Date get_terminateDate() {
        return _terminateDate;
    }

    public void set_terminateDate(Date _terminateDate) {
        this._terminateDate = _terminateDate;
    }

    public String get_description() {
        return _description;
    }

    public void set_description(String _description) {
        this._description = _description;
    }

    public String get_category() {
        return _category;
    }

    public void set_category(String _category) {
        this._category = _category;
    }

    public Boolean get_completed() {
        return _completed;
    }

    public void set_completed(Boolean _completed) {
        this._completed = _completed;
    }
    
}
