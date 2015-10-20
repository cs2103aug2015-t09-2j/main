package katnote;

import java.util.ArrayList;
import java.util.Collections;

import katnote.task.Task;
import katnote.task.TaskDueDateComparator;

public class ViewState {
    private ArrayList<Task> normalTasks_;
    private ArrayList<Task> floatingTasks_;
    private ArrayList<Task> eventTasks_; // not needed for now
    private ArrayList<Task> allTasks_; // not needed for now
    
    //Constructor
    public ViewState() {
       
    }
    
    public ViewState(ArrayList<Task> normalTasks,
                     ArrayList<Task> floatingTasks,
                     ArrayList<Task> eventTasks) {
    
        normalTasks_ = new ArrayList<Task>(normalTasks);
        floatingTasks_ = new ArrayList<Task>(floatingTasks);
        eventTasks_ = new ArrayList<Task>(eventTasks);
    }
    
    public ViewState(ArrayList<Task> all) {
        allTasks_ = new ArrayList<Task> (all);
    }
    
    // Accessors 
    public ArrayList<Task> getNormalTasks() {
        return normalTasks_;
    }
    
    public ArrayList<Task> getFloatingTasks() {
        return floatingTasks_;
    }
    
    public ArrayList<Task> getEventTasks() {
        return eventTasks_;
    }
    
    public ArrayList<Task> getAllTasks() {
        return allTasks_;
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
    
    public void setAllTasks(ArrayList<Task> all) {
        allTasks_ = new ArrayList<Task>(all);
    }
            
}

