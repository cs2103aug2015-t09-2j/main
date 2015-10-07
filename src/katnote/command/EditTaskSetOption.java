package katnote.command;

import java.util.Date;

public class EditTaskSetOption {
    private String taskOptionName;
    private String taskOptionValue;
    
    public EditTaskSetOption(String taskOptionName, String taskOptionValue){
        this.taskOptionName = taskOptionName;
        this.taskOptionValue = taskOptionValue;
    }
    
    public EditTaskSetOption(String editTaskOption){
        String[] options = editTaskOption.split(" ");
        taskOptionName = options[0];
        taskOptionValue = options[1];
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
