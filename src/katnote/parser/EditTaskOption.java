//@@author A0126517H
package katnote.parser;

import katnote.utils.KatDateTime;
import katnote.utils.StringUtils;

public class EditTaskOption {
    private String taskOptionName;
    private String taskOptionValue;

    /**
     * Creates new EditTaskOption with desired option name and option value
     * 
     * @param taskOptionName
     * @param taskOptionValue
     */
    public EditTaskOption(String taskOptionName, String taskOptionValue) {
        this.taskOptionName = taskOptionName;
        this.taskOptionValue = taskOptionValue;
    }

    /**
     * Creates new EditTaskOption with the first word is option name and the
     * rest is option value
     * 
     * @param editTaskOption
     */
    public EditTaskOption(String editTaskOption) {
        taskOptionName = StringUtils.getFirstWord(editTaskOption);
        taskOptionValue = StringUtils.removeFirstWord(editTaskOption);
    }

    /**
     * Returns option name
     * 
     * @return option name
     */
    public String getOptionName() {
        return taskOptionName;
    }

    /**
     * Returns option value
     * 
     * @return option value
     */
    public String getOptionValue() {
        return taskOptionValue;
    }

    /**
     * Returns option value as an object based on the option name. If the option
     * value cannot be converted, returns null instead.
     * 
     * @return New Object representing the option value
     */
    public Object getOptionValueObject() {
        try {
            return PropertyParser.parseOptionValue(taskOptionName, taskOptionValue);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns option value as an KatDateTime object based on the option name.
     * If the option value cannot be converted, returns null instead.
     * 
     * @return New KatDateTime object representing the option value
     */
    public KatDateTime getOptionValueDate() {
        return (KatDateTime) getOptionValueObject();
    }
}
