//@@author A0131003J

/* This program contains the service class ViewState and its methods */

package katnote.logic;

import java.util.ArrayList;
import katnote.task.Task;

public class ViewState {
    private ArrayList<Task> normalTasks_;
    private ArrayList<Task> floatingTasks_;
    private ArrayList<Task> eventTasks_;
    
    //Constructor
    public ViewState() {
        normalTasks_ = new ArrayList<Task>();
        floatingTasks_ = new ArrayList<Task>();
        eventTasks_ = new ArrayList<Task>();
    }
    
    public ViewState(ViewState vs) {
        normalTasks_ = vs.getNormalTasks();
        floatingTasks_ = vs.getFloatingTasks();
        eventTasks_ = vs.getEventTasks();
    }
    
    public ViewState(ArrayList<Task> normalTasks,
                     ArrayList<Task> floatingTasks,
                     ArrayList<Task> eventTasks) {
    
        normalTasks_ = new ArrayList<Task>(normalTasks);
        floatingTasks_ = new ArrayList<Task>(floatingTasks);
        eventTasks_ = new ArrayList<Task>(eventTasks);
    }
    
    
    // Accessors/Getters 
    public ArrayList<Task> getNormalTasks() {
        return normalTasks_;
    }
    
    public ArrayList<Task> getFloatingTasks() {
        return floatingTasks_;
    }
    
    public ArrayList<Task> getEventTasks() {
        return eventTasks_;
    }
       
    
    /**
     * 
     * @return Returns the total number of tasks found in all 3 task lists.
     */
    public int getViewStateSize() {
        int size = 0;
        
        if (eventTasks_ != null) {
            size += eventTasks_.size();
        }
        
        if (normalTasks_ != null) {
            size += normalTasks_.size();
        }
        
        if (floatingTasks_ != null) {
            size += floatingTasks_.size();
        }
        
        return size;
    }
    
    // Setters
    public void setNormalTasks(ArrayList<Task> list) {
        normalTasks_ = new ArrayList<Task>(list);
        
    }

    public void setFloatingTasks(ArrayList<Task> list) {       
        floatingTasks_ = new ArrayList<Task>(list);
    }
    
    public void setEventTasks(ArrayList<Task> list) {
        eventTasks_ = new ArrayList<Task>(list);
    }   
            
}

