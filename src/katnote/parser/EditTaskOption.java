package katnote.parser;

import katnote.utils.KatDateTime;
import katnote.utils.StringUtils;

public class EditTaskOption {
    private String taskOptionName;
    private String taskOptionValue;

    public EditTaskOption(String taskOptionName, String taskOptionValue) {
        this.taskOptionName = taskOptionName;
        this.taskOptionValue = taskOptionValue;
    }

    public EditTaskOption(String editTaskOption) {
        taskOptionName = StringUtils.getFirstWord(editTaskOption);
        taskOptionValue = StringUtils.removeFirstWord(editTaskOption);
    }

    public String getOptionName() {
        return taskOptionName;
    }

    public String getOptionValue() {
        return taskOptionValue;
    }

    public Object getOptionValueObject() {
        return PropertyParser.parseOptionValue(taskOptionName, taskOptionValue);
    }

    public KatDateTime getOptionValueDate() {
        return (KatDateTime) PropertyParser.parseOptionValue(taskOptionName, taskOptionValue);
    }
}
