package katnote.command;

import java.util.Date;

import katnote.utils.StringUtils;

public class EditTaskSetOption {
    private String taskOptionName;
    private String taskOptionValue;
    
    public EditTaskSetOption(String taskOptionName, String taskOptionValue){
        this.taskOptionName = taskOptionName;
        this.taskOptionValue = taskOptionValue;
    }
    
    public EditTaskSetOption(String editTaskOption){
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
        return Parser.parseOptionValue(taskOptionName, taskOptionValue);
    }
    
    public Date getOptionValueDate(){
        return (Date) Parser.parseOptionValue(taskOptionName, taskOptionValue);
    }
}
