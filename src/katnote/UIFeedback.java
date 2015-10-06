/* This program contains the service class UIFeedback and its methods */

package katnote;

import java.util.ArrayList;

public class UIFeedback {
    private boolean isError_; // true if UIFeedback contains an error reponseMessage
    private ArrayList<Task> taskList_;
    private String responseMessage_;
    
    /*-- Constructors --*/
    
    public UIFeedback() {
        isError_ = false;
        taskList_ = new ArrayList<Task>();
    }
    
    public UIFeedback(boolean isError, ArrayList<Task> taskList, String responseMessage) {
        isError_ = isError;
        taskList_ = new ArrayList<Task>(taskList);
        responseMessage_ = responseMessage;
    }
    
    // Constructor for view/search command response
    public UIFeedback(ArrayList<Task> taskList, String responseMessage) {
        isError_ = false;
        taskList_ = new ArrayList<Task>(taskList);
        responseMessage_ = responseMessage;
    }
    
    // Constructor for the response of commands that are not view/search
    public UIFeedback(String responseMessage) {
        isError_ = false;
        responseMessage_ = responseMessage;
    }
    
    // Constructor for a response with an error message
    public UIFeedback(boolean isError, String responseMessage) {
        isError_ = isError;
        responseMessage_ = responseMessage;
    }
   
    /*-- Public Methods --*/
    
    public void setError(boolean isError) {
        isError_ = isError;
    }
    
    public void setTaskList(ArrayList<Task> taskList) {
        taskList_ = new ArrayList<Task>(taskList);
    }
    
    public void setResponse(String responseMessage) {
        responseMessage_ = responseMessage;
    }
     
    public boolean isAnError() {
        return isError_;
    }
    
    /**
     * Returns the list of tasks
     * @return Returns the ArrayList of Tasks of UIFeedback object.
     */
    public ArrayList<Task> getTaskList() {
        return taskList_;
    }
    
    /**
     * 
     * @return Returns the String containing the response message of the UIFeedback object.
     */
    public String getMessage() {
        return responseMessage_;
    }
       
}