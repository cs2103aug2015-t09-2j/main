package katnote.parser;

import java.util.Date;

import katnote.utils.StringUtils;

public class EditTaskOption {
    private String taskOptionName;
    private String taskOptionValue;
    
    public EditTaskOption(String taskOptionName, String taskOptionValue){
        this.taskOptionName = taskOptionName;
        this.taskOptionValue = taskOptionValue;
    }
    
    public EditTaskOption(String editTaskOption){
        taskOptionName = StringUtils.getFirstWord(editTaskOption);
        taskOptionValue = StringUtils.removeFirstWord(editTaskOption);
    }
    
    public String getOptionName(){
        return taskOptionName;
    }
    
    public String getOptionValue(){
        return taskOptionValue;
    }
    
    public Object getOptionValueObject(){
        return PropertyParser.parseOptionValue(taskOptionName, taskOptionValue);
    }
    
    public Date getOptionValueDate(){
        return (Date) PropertyParser.parseOptionValue(taskOptionName, taskOptionValue);
    }
}
