package katnote.command;

import java.time.LocalDateTime;
import java.util.HashMap;

import katnote.parser.EditTaskOption;
import katnote.parser.ViewTaskOption;
import katnote.task.TaskType;

public class CommandDetail {
    protected CommandType commandType;
    protected HashMap<String, Object> commandData;

    /*
     * Creates new CommandDetail object of corresponding command type
     */
    public CommandDetail(CommandType commandType) {
        this.commandType = commandType;
        commandData = new HashMap<String, Object>();
    }

    /*
     * Creates new CommandDetail object with unknown command type
     */
    public CommandDetail() {
        this.commandType = CommandType.UNKNOWN;
        commandData = new HashMap<String, Object>();
    }

    /*
     * Sets the type of command
     */
    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }

    /*
     * Returns type of command
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
     * Returns value of property with given key
     * 
     * @param key
     */
    public Object getProperty(String key) {
        return commandData.get(key);
    }

    /*
     * Returns value of string property with given key
     * 
     * @param key
     */
    public String getString(String key) {
        return (String) commandData.get(key);
    }

    /*
     * Returns value of LocalDateTime property with given key
     * 
     * @param key
     */
    public LocalDateTime getDate(String key) {
        return (LocalDateTime) commandData.get(key);
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
     * Returns type of task
     */
    public TaskType getTaskType() {
        return (TaskType) commandData.get(CommandProperties.TASK_TYPE);
    }

    /*
     * Returns title of task
     */
    public String getTitle() {
        return getString(CommandProperties.TASK_TITLE);
    }

    /*
     * Returns start date
     */
    public LocalDateTime getStartDate() {
        return getDate(CommandProperties.TIME_FROM);
    }

    /*
     * Returns end date
     */
    public LocalDateTime getEndDate() {
        return getDate(CommandProperties.TIME_TO);
    }

    /*
     * Returns due date
     */
    public LocalDateTime getDueDate() {
        return getDate(CommandProperties.TIME_BY);
    }

    /*
     * Returns keywords for command type FIND
     */
    public String getFindKeywords() {
        return getString(CommandProperties.FIND_KEYWORDS);
    }

    /*
     * Returns main content of command for command type FIND, HELP
     */
    public String getMainContent() {
        return getString(CommandProperties.MAIN_CONTENT);
    }

    public String getFilePath() {
        return getString(CommandProperties.FILE_PATH);
    }
    
    public ViewTaskOption getViewTaskOption() {
        return (ViewTaskOption) commandData.get(CommandProperties.TASKS_VIEW_OPTION);
    }

}
