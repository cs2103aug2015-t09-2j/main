package katnote;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import katnote.command.CommandDetail;
import katnote.command.CommandProperties;
import katnote.command.CommandType;
import katnote.parser.EditTaskOption;
import katnote.parser.Parser;
import katnote.parser.ViewTaskOption;
import katnote.task.Task;

public class Logic {

    private Model model_;
    private Tracker tracker_;
    private String sourcePathStr;

    // Error Messages
    private static final String MSG_ERR_INVALID_TYPE = "Invalid command type: %s"; // %s is the command type
    private static final String MSG_ERR_INVALID_ARGS = "Invalid arguments: %s"; // %s is the String of args

    // Source Paths
    private static final String MSG_SOURCE_PATH = "sourcepath.txt";
    private static final String MSG_DEFAULT_SOURCE_PATH = "";

    // Constructor
    public Logic() throws Exception {
        
        // Create Model object
        File sourcePath = new File(MSG_SOURCE_PATH);
        if (!sourcePath.exists()) {
            sourcePath.createNewFile();
        }
        BufferedReader br = new BufferedReader(new FileReader(sourcePath));
        String line;
        line = br.readLine();
        if (line == null) {
            sourcePathStr = MSG_DEFAULT_SOURCE_PATH;
        } else {
            sourcePathStr = line;
        }
        br.close();

        assert (sourcePathStr != null);
        model_ = new Model(sourcePathStr);
        
        // Create Tracker object
        tracker_ = new Tracker();
    }

    /* Public methods */

    /**
     * Takes in String command from GUI, parse and process the command and
     * return feedback to GUI
     * 
     * @param command
     *            String input from GUI command line interface
     * @return returns a UIFeedback object containing information for the GUI to
     *         display.
     * @throws Exception
     */
    public UIFeedback execute(String command) throws Exception {
        UIFeedback feedback;
        CommandDetail parsedTask = Parser.parseCommand(command);
        feedback = process(parsedTask);
        return feedback;
    }
    
    /**
     * Reads from Model to retrieve the current uncompleted tasks data
     * @return viewState to be displayed when software starts up
     */
    public ViewState getInitialViewState() {
        ViewState vs = new ViewState();
        updateViewState(vs);
        return vs;
    }
    
    /**
     * Updates tracker's ID list with the latest information based on the input ArrayList of Tasks
     * @param taskList The list of tasks whose ID is to be updated to the tracker
     */
    public void setViewMapping(ArrayList<Task> taskList) {
        tracker_.setViewMapping(taskList);
    }
    
    
    /*-- Main Functions --*/
    
    /**
     * Reads in commandDetail obj and returns a UIFeedback obj accordingly
     * @param commandDetail
     *              commandDetail object to be processed
     * @return UIFeedback object with details after commandDetail is processed
     * @throws Exception
     */
    public UIFeedback process(CommandDetail commandDetail) throws Exception {
        CommandType type = commandDetail.getCommandType();
        UIFeedback feedback = new UIFeedback();
        ViewState vs = feedback.getViewState();
        int taskID; // for edit/delete commands

        Task task;
        switch (type) {
            case ADD_TASK :
                task = new Task(commandDetail);
                feedback.setResponse(model_.addTask(task));
                
                updateViewState(vs);
                break;

            case EDIT_MODIFY :
                taskID = tracker_.getTaskID(commandDetail.getTaskIndex());
                EditTaskOption editOptions = commandDetail.getEditTaskOption();
                feedback.setResponse(model_.editModify(taskID, editOptions));
                
                updateViewState(vs);                
                break;

            case EDIT_COMPLETE :
                taskID = tracker_.getTaskID(commandDetail.getTaskIndex());
                feedback.setResponse(model_.editComplete(taskID));
                
                updateViewState(vs);
                break;

            case DELETE_TASK :
                // get index from commandDetail
                taskID = tracker_.getTaskID(commandDetail.getTaskIndex());
                feedback.setResponse(model_.editDelete(taskID)); // pass ID to Model
                
                updateViewState(vs);               
                break;

            case VIEW_TASK :
                feedback.setViewState(viewTask(commandDetail));
                break;

            case UNDO :
                feedback.setResponse(model_.undo());

                updateViewState(vs);  
                break;

            case REDO :
                feedback.setResponse(model_.redo());

                updateViewState(vs);
                break;

            case FIND_TASKS :
                feedback.setViewState(find(commandDetail));

                updateViewState(vs);
                break;

            case SET_LOCATION :
                String newSaveLocation = (String) commandDetail.getProperty(CommandProperties.LOCATION);
                setSourcePath(newSaveLocation);
                feedback.setResponse(model_.setLocation(commandDetail));

                updateViewState(vs);
                break;

            default :
                feedback.setError(true);
                feedback.setResponse(String.format(MSG_ERR_INVALID_TYPE, type));

                updateViewState(vs);
                break;
        }
        return feedback;
    }


    /**
     * Search for tasks that match the specifications in the commandDetail input
     * 
     * @param commandDetail
     *            Contains the criteria for searching
     * @return An ArrayList of tasks that match the search criteria
     */

    private ViewState find(CommandDetail commandDetail) {
        Search search = new Search(); 
        ViewState vs = new ViewState();
        
        // Find by keyword, incomplete tasks:
        search.setKeyword(commandDetail.getFindKeywords());
        search.setIsCompleted(false);
     
        // search and return
        //TODO: Refactor this into a method
        vs = new ViewState(search.searchData(model_.getNormalTasks()),
                           search.searchData(model_.getFloatingTasks()),
                           search.searchData(model_.getEventTasks()));   
        return vs;

    }

    /**
     * Returns the list of tasks that should be displayed according to the
     * viewing criteria
     * 
     * @param commandDetail
     *            specifies the criteria of what tasks should be retrieved
     * @return ArrayList of tasks that satisfy the view criteria
     */
    private ViewState viewTask(CommandDetail commandDetail) {
        Search search = new Search();
        ViewState vs = new ViewState();
        ViewTaskOption viewType = commandDetail.getViewTaskOption();
        
        switch (viewType) {
            case COMPLETED :
                search.setIsCompleted(true);
                break;
                
            case INCOMPLETED :
                search.setIsCompleted(false);
                break;
            
            case ALL :
                search.setIsCompleted(null);
                break;
            
            /*
            case DUE_BY :
                search.setDue(commandDetail.getDueDate());
                break;
            */
                
            default:
                break;
        }  
              
        // search and return
        //TODO: Refactor this into a method
        vs = new ViewState(search.searchData(model_.getNormalTasks()),
                           search.searchData(model_.getFloatingTasks()),
                           search.searchData(model_.getEventTasks()));    
        return vs;
    }
            

    /*-- Helper Functions --*/

    private void setSourcePath(String newPath) throws FileNotFoundException {

        File sourcePath = new File(MSG_SOURCE_PATH);
        PrintWriter pw = new PrintWriter(sourcePath);
        pw.println(newPath);
        pw.close();
    }
    
    /**
     * Updates input ViewState with latest data from Model
     * @param vs
     *          ViewState to be updated
     */
    //TODO: update this method accordingly to include sorting/remove completed tasks
    private void updateViewState(ViewState vs) { 
        vs.setNormalTasks(model_.getNormalTasks());
        vs.setFloatingTasks(model_.getFloatingTasks());
        vs.setEventTasks(model_.getEventTasks());
    }

}

class Tracker {
    
    private static final int INDEX_OFFSET = 1;
    private ArrayList<Integer> taskIDList_; // The list of tasksIDs that is currently displayed
       
    public Tracker() {
        taskIDList_ = new ArrayList<Integer>();
        
    }
    
    public ArrayList<Integer> getIDList() {
        return taskIDList_;
    }
    
    /**
     * @param index
     *          Task index as indicated by command input
     * @return  ID of request task, -1 if index is out of bounds.
     */
    public int getTaskID(int index) {
        index -= INDEX_OFFSET;
        if (taskIDList_.size() > index) {
            return taskIDList_.get(index);
        } else {
            return -1;
        }
    }
    
    /**
     * Updates the taskIDList_ with the latest information based on the input ArrayList of Tasks
     * @param taskList The list of tasks whose ID is to be updated to the tracker
     */
    public void setViewMapping(ArrayList<Task> taskList) {
        taskIDList_ = new ArrayList<Integer>(); // clear list

        if (!taskList.isEmpty()) {
            for (int i = 0; i < taskList.size(); i++) {
                taskIDList_.add(taskList.get(i).getID());
            }
        }
    }  
}

class Search {
    
    /*
    private ArrayList<Task> normalData_;
    private ArrayList<Task> floatingData_;
    private ArrayList<Task> eventData_;
    */
    
    private String keyword_;
    private Boolean isCompleted_; // will be set to null if searching for both

    private LocalDateTime start_; // for events only
    private LocalDateTime end_; // for events only
    private LocalDateTime due_; 
    
    /*
    // initialise a Search object with the list of Tasks to be searched
    public Search(ArrayList<Task> normal, ArrayList<Task> floating, ArrayList<Task> event ) { 
        normalData_ = new ArrayList<Task>(normal);
        floatingData_ = new ArrayList<Task>(floating);
        eventData_ = new ArrayList<Task>(event);      
    }
    
    public void setNormalData(ArrayList<Task> normal) {
        normalData_ = new ArrayList<Task>(normal);
    }
    
    public void setFloatingData(ArrayList<Task> floating) {
        normalData_ = new ArrayList<Task>(floating);
    }
    
    public void setEventData(ArrayList<Task> event) {
        normalData_ = new ArrayList<Task>(event);
    }
    */
    
    public Search() { }
    
    public void setKeyword(String keyword) {
        keyword_ = keyword;
    }

    public void setIsCompleted(Boolean isCompleted) {
        isCompleted_ = isCompleted;
    }

    public void setStart(LocalDateTime start) {
        start_ = start;
    }

    public void setEnd(LocalDateTime end) {
        end_ = end;
    }
    
    public void setDue(LocalDateTime due) {
        due_ = due;
    }
    
    /* Main search method */
    
    public ArrayList<Task> searchData(ArrayList<Task> list) {
        ArrayList<Task> searched = new ArrayList<Task>(list);
        
        if (keyword_ != null) {
            searched = new ArrayList<Task>(findByKeyword(searched));
        }
        
        if (due_ != null) {
            searched = new ArrayList<Task>(findDueBy(searched));
        }
        
        if (isCompleted_ != null) {
            searched = new ArrayList<Task>(findByIsCompleted(searched));
        }
        
        return searched;
        
    }
    
    /* Sub Searching Methods */
    
    /**
     * Finds the tasks that contains the keyword (either in the task's name
     * or description) NOTE: This is case insensitive
     * 
     * @param data
     *            ArrayList of tasks to be searched
     * @param keyword
     *            String that tasks found should contain in their Name or
     *            Description fields
     * @return List of tasks that contains the input keyword
     */
    public ArrayList<Task> findByKeyword(ArrayList<Task> data) {
        ArrayList<Task> tasksFound = new ArrayList<Task>();

        for (int i = 0; i < data.size(); i++) {
            Task task = data.get(i);
            String taskTitle = task.getTitle();
            String taskDescription = task.getDescription();

            if (isContain(taskTitle, keyword_) || isContain(taskDescription, keyword_)) {
                tasksFound.add(task);
            }
        }
        return tasksFound;
    }

    /**
     * Finds tasks according to the isCompleted attribute
     * @param data
     *          ArrayList of tasks to be searched
     * @return ArrayList of tasks whose isCompleted() == isCompleted_ in Search object
     */
    public ArrayList<Task> findByIsCompleted(ArrayList<Task> data) {
        ArrayList<Task> tasksFound = new ArrayList<Task>();

        for (int i = 0; i < data.size(); i++) {
            Task task = data.get(i);
            if (task.isCompleted() == isCompleted_) {
                tasksFound.add(task);
            }
        }
        return tasksFound;
    }


    /**
     * Finds tasks that are due either BEFORE or BY the
     * input date
     * 
     * @param data
     *            ArrayList of tasks to be searched
     * @param input
     *            The date that tasks found should be due by
     * @return List of tasks with due dates before or equals to the
     *         input date (regardless of time)
     */
    public ArrayList<Task> findDueBy(ArrayList<Task> data) {
        LocalDate duedate = due_.toLocalDate();
        ArrayList<Task> tasksFound = new ArrayList<Task>();

        for (int i = 0; i < data.size(); i++) {
            Task task = data.get(i);
            LocalDate taskDue = task.getEndDate().toLocalDate();
            if (!taskDue.isAfter(duedate)) {
                tasksFound.add(task);
            }
        }
        return tasksFound;
    }
    
    /* Helper Methods */

    // returns true if line contains exactly the input word, case insensitive
    private static boolean isContain(String line, String word) {
        String lowerCaseWord = word.toLowerCase();
        String lowerCaseLine = line.toLowerCase();

        String pattern = "\\b" + lowerCaseWord + "\\b";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(lowerCaseLine);
        return m.find();
    }

}