package katnote.command;

import java.util.Date;
import java.util.HashMap;

import katnote.parser.EditTaskOption;
import katnote.task.TaskType;

public class CommandDetail {
    protected CommandType commandType;
    protected HashMap<String, Object> commandData;

    /*
     * Create new CommandDetail object of corresponding command type
     */
    public CommandDetail(CommandType commandType) {
        this.commandType = commandType;
        commandData = new HashMap<String, Object>();
    }

    /*
     * Create new CommandDetail object with unknown command type
     */
    public CommandDetail() {
        this.commandType = CommandType.UNKNOWN;
        commandData = new HashMap<String, Object>();
    }

    /*
     * Set the type of command
     */
    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }

    /*
     * Return type of command
     */
    public CommandType getCommandType() {
        return commandType;
    }

    /*
     * Associates the specified value with the specified key in the properties
     * map. If the map previously contained a mapping for the key, the old value
     * is replaced.
     * 
     */
    public void setProperty(String key, Object value) {
        commandData.put(key, value);
    }

    /*
     * Returns <tt>true</tt> if this map contains a mapping for the specified
     * key.
     *
     * @param key The key whose presence in this map is to be tested
     * 
     * @return <tt>true</tt> if this map contains a mapping for the specified
     * key.
     */
    public boolean hasProperty(String key) {
        return commandData.containsKey(key);
    }

    /*
     * Return value of property with given key
     * 
     * @param key
     */
    public Object getProperty(String key) {
        return commandData.get(key);
    }

    /*
     * Return value of string property with given key
     * 
     * @param key
     */
    public String getString(String key) {
        return (String) commandData.get(key);
    }

    /*
     * Return value of Date property with given key
     * 
     * @param key
     */
    public Date getDate(String key) {
        return (Date) commandData.get(key);
    }

    public EditTaskOption getEditTaskOption() {
        return (EditTaskOption) commandData.get(CommandProperties.EDIT_SET_PROPERTY);
    }

    /*
     * Returns the TASK_ID value of the task specified in commandDetail
     */
    public int getTaskIndex() {
        return (Integer) commandData.get(CommandProperties.TASK_ID);
    }

    /*
     * Return type of task
     */
    public TaskType getTaskType() {
        return (TaskType) commandData.get(CommandProperties.TASK_TYPE);
    }

    /*
     * Return title of task
     */
    public String getTitle() {
        return getString(CommandProperties.TASK_TITLE);
    }

    /*
     * Return start date
     */
    public Date getStartDate() {
        return getDate(CommandProperties.TIME_FROM);
    }

    /*
     * Return end date
     */
    public Date getEndDate() {
        return getDate(CommandProperties.TIME_TO);
    }

    /*
     * Return due date
     */
    public Date getDueDate() {
        return getDate(CommandProperties.TIME_BY);
    }

    /*
     * Return keywords for command type FIND
     */
    public String getFindKeywords() {
        return getString(CommandProperties.FIND_KEYWORDS);
    }

    /*
     * Return main content of command for command type FIND, HELP
     */
    public String getMainContent() {
        return getString(CommandProperties.MAIN_CONTENT);
    }

    public String getFilePath() {
        return getString(CommandProperties.FILE_PATH);
    }

}
