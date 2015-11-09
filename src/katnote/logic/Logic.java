//@@author A0131003J

/* This program contains the Logic class and its methods. Logic handles the redirection of
 * commands to the Parser and Model classes, and implements the searching/viewing commands.
 * Logic will then return a UIFeedback object after processing the command. 
 */

package katnote.logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import katnote.KatNoteLogger;
import katnote.Model;
import katnote.command.CommandDetail;
import katnote.command.CommandType;
import katnote.parser.EditTaskOption;
import katnote.parser.Parser;
import katnote.task.Task;
import katnote.task.TaskDueDateComparator;
import katnote.task.TaskStartDateComparator;
import katnote.task.TaskType;
import katnote.utils.KatDateTime;

public class Logic {

    // Error Messages
    private static final String MSG_ERR_INVALID_TYPE = "Invalid command type: %s"; // %s is the command type
    private static final String MSG_ERR_INVALID_INDEX = "Invalid index!";
    
    // Response Messages
    private static final String MSG_RESPONSE_SEARCH_KEYWORD = "Found %d tasks with keyword %s.";
    private static final String MSG_RESPONSE_VIEW = "Displaying %d tasks.";

    // Source Paths
    private static final String MSG_SOURCE_PATH = "sourcepath.txt";
    private static final String MSG_DEFAULT_SOURCE_PATH = "";
    
    // Error Index
    private static final int ERR_INDEX = -1;
    
    // Logging
    private static final Logger LOG = KatNoteLogger.getLogger(Logic.class.getName());
    private static final String LOG_EXECUTE = "Executing Command: %s"; // %s is the command
    private static final String LOGGING_MARK_INCOMPLETE = "Command Type: MARK INCOMPLETE";
    private static final String LOGGING_MARK_COMPLETE = "Command Type: MARK COMPLETE";
    private static final String LOGGING_INVALID = "Command Type: INVALID";
    private static final String LOGGING_EXIT = "Command Type: EXIT";
    private static final String LOGGING_IMPORT = "Command Type: IMPORT";
    private static final String LOGGING_SET_LOCATION = "Command Type: SET LOCATION";
    private static final String LOGGING_FIND = "Command Type: FIND";
    private static final String LOGGING_REDO = "Command Type: REDO";
    private static final String LOGGING_UNDO = "Command Type: UNDO";
    private static final String LOGGING_VIEW = "Command Type: VIEW";
    private static final String LOGGING_DELETE = "Command Type: DELETE";
    private static final String LOGGING_POSTPONE = "Command Type: POSTPONE";
    private static final String LOGGING_EDIT = "Command Type: EDIT";
    private static final String LOGGING_ADD = "Command Type: ADD";
   
    // Private Class Variables
    private Model model_;
    private Tracker tracker_;
    private String sourcePathStr;

    /* Constructors */
    public Logic() throws Exception {        
        tracker_ = new Tracker();
        
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
    }
    
    public Logic(String path) throws Exception {
        model_ = new Model(path);
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
        LOG.log(Level.INFO, String.format(LOG_EXECUTE, command));
        CommandDetail parsedTask = Parser.parseCommand(command);
        UIFeedback feedback = process(parsedTask);
        return feedback;
    }
    
    /**
     * Reads from Model to retrieve the default view state
     * @return viewState to be displayed when software starts up
     */
    public ViewState getInitialViewState() {
        ViewState vs = new ViewState(getDefaultViewState());
        return vs;
    }
    
    /**
     * For testing purposes
     */
    public ArrayList<Integer> getTrackerIDList() {
        return tracker_.getIDList();
    }
    
    public ArrayList<Task> getModelData() {
        return model_.getData();
    }
    
    public ArrayList<Task> getModelEventData() {
        return model_.getEventTasks();
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
        int taskID; // for edit/delete commands
        int tasksFound; //number of tasks displayed for view/search commands
        
        switch (type) {
            case ADD_TASK :
                LOG.log(Level.INFO, LOGGING_ADD);
                Task task = new Task(commandDetail);
                feedback.setResponse(model_.addTask(task));
                feedback.setViewState(getDefaultViewState());
                break;

            case EDIT_MODIFY :
                LOG.log(Level.INFO, LOGGING_EDIT);
                taskID = tracker_.getTaskID(commandDetail.getTaskIndex());
                
                if (taskID != ERR_INDEX) {
                    EditTaskOption editOptions = commandDetail.getEditTaskOption();                    
                    feedback.setResponse(model_.editModify(taskID, editOptions)); 
                } else {
                    feedback.setError(true);
                    feedback.setResponse(MSG_ERR_INVALID_INDEX);
                }
                                              
                feedback.setViewState(getDefaultViewState());            
                break;

            case EDIT_COMPLETE :              
                taskID = tracker_.getTaskID(commandDetail.getTaskIndex());
                
                if (taskID != ERR_INDEX) {
                    markTask(commandDetail, feedback, taskID);
                } else {
                    feedback.setError(true);
                    feedback.setResponse(MSG_ERR_INVALID_INDEX);
                }

                feedback.setViewState(getDefaultViewState());
                break; 
            
            case POSTPONE:
                LOG.log(Level.INFO, LOGGING_POSTPONE);
                taskID = tracker_.getTaskID(commandDetail.getTaskIndex()); 
                
                if (taskID != ERR_INDEX) {
                    feedback.setResponse(model_.postpone(taskID, commandDetail.getStartDate()));
                } else {
                    feedback.setError(true);
                    feedback.setResponse(MSG_ERR_INVALID_INDEX);
                }
                                               
                feedback.setViewState(getDefaultViewState());
                break;

            case DELETE_TASK :
                LOG.log(Level.INFO, LOGGING_DELETE);
                taskID = tracker_.getTaskID(commandDetail.getTaskIndex());
                
                if (taskID != ERR_INDEX) {
                    feedback.setResponse(model_.editDelete(taskID));
                } else {
                    feedback.setError(true);
                    feedback.setResponse(MSG_ERR_INVALID_INDEX);
                }
                                               
                feedback.setViewState(getDefaultViewState());             
                break;

            case VIEW_TASK :
                LOG.log(Level.INFO, LOGGING_VIEW);
                feedback.setViewState(find(commandDetail));
                
                tasksFound = feedback.getViewState().getViewStateSize();               
                feedback.setResponse(String.format(MSG_RESPONSE_VIEW, tasksFound));
                
                break;

            case UNDO :
                LOG.log(Level.INFO, LOGGING_UNDO);
                feedback.setResponse(model_.undo());
                feedback.setViewState(getDefaultViewState()); 
                break;

            case REDO :
                LOG.log(Level.INFO, LOGGING_REDO);
                feedback.setResponse(model_.redo());
                feedback.setViewState(getDefaultViewState());
                break;

            case FIND_TASKS :
                LOG.log(Level.INFO, LOGGING_FIND);
                feedback.setViewState(find(commandDetail));
                feedback.setSearch(true);
                
                tasksFound = feedback.getViewState().getViewStateSize();            
                String keyword = commandDetail.getFindKeywords();               
                feedback.setResponse(String.format(MSG_RESPONSE_SEARCH_KEYWORD, tasksFound, keyword));
                
                break;

            case SET_LOCATION :
                LOG.log(Level.INFO, LOGGING_SET_LOCATION);
                String newSaveLocation = (String) commandDetail.getFilePath();               
                feedback.setResponse(model_.setLocation(commandDetail));
                setSourcePath(newSaveLocation);
                feedback.setViewState(getDefaultViewState());
                break;
                
            case IMPORT :
                LOG.log(Level.INFO, LOGGING_IMPORT);
                feedback.setResponse(model_.importData(commandDetail));                
                feedback.setViewState(getDefaultViewState());
                break;
                
            case EXIT :
                LOG.log(Level.INFO, LOGGING_EXIT);
                feedback.setExit(true);
                break;

            default :
                LOG.log(Level.INFO, LOGGING_INVALID);
                feedback.setError(true);
                feedback.setResponse(String.format(MSG_ERR_INVALID_TYPE, type));
                feedback.setViewState(getDefaultViewState());
                break;
        }
        return feedback;
    }


    /**
     * Searches for tasks that match the specifications in the commandDetail input,
     * then returns a sorted view state.
     * 
     * @param commandDetail
     *            Contains the criteria for searching
     * @return ViewState object containing the filtered and sorted tasks.
     */

    private ViewState find(CommandDetail commandDetail) {
        Search search = new Search(); 
        ViewState vs = new ViewState();
        
        // Getting the search parameters
        Boolean isCompleted = commandDetail.getTaskCompletedOption();       
        LocalDateTime dueDate = getSearchDueDateFromCommandDetail(commandDetail);
        LocalDateTime startDate = getSearchStartDateFromCommandDetail(commandDetail);
        String keyword = commandDetail.getFindKeywords();
        
        // Setting the search parameters
        search.setIsCompleted(isCompleted);
        search.setDue(dueDate);
        search.setStart(startDate); 
        search.setKeyword(keyword);
     
        // search and return
        vs = getSearchedAndSortedViewState(search);
        return vs;

    } 

    /*-- Helper Functions --*/
    
    /**
     * Calls the correct editComplete/Incomplete function according to the input CommandDetail,
     * and sets the corresponding response message for the input UIFeedback object. 
     * @param cmdDetail CommandDetail object that contains information on whether the command is 
     *                  an edit complete or edit incomplete.
     * @param feedback  UIFeedback object whose response message will be set after the command is
     *                  executed.
     * @param taskID    ID of the task to be edited.
     * @throws Exception 
     */
    private void markTask(CommandDetail cmdDetail, UIFeedback feedback, int taskID) throws Exception {
        if (cmdDetail.getTaskCompletedOption()) {
            LOG.log(Level.INFO, LOGGING_MARK_COMPLETE);
            feedback.setResponse(model_.editComplete(taskID));  
        } else {
            LOG.log(Level.INFO, LOGGING_MARK_INCOMPLETE);
            feedback.setResponse(model_.editIncomplete(taskID));
        }
    }
    
    /**
     * Searches through Model data based on the input Search object and returns
     * a searched and sorted ViewState object.
     * @param search
     *          Search object containing the information for sorting
     * @return ViewState object containing lists of tasks that are searched according to the
     * input Search object and sorted in order of their due dates whenever possible.
     */
    private ViewState getSearchedAndSortedViewState(Search search) {
        
        //search
        ArrayList<Task> normal = search.searchData(model_.getNormalTasks());
        ArrayList<Task> floating = search.searchData(model_.getFloatingTasks());
        ArrayList<Task> event = search.searchData(model_.getEventTasks());
        
        //set
        ViewState vs = new ViewState(normal, floating, event);
        
        //Sort
        sortViewStateByDate(vs);
        
        return vs;
    }
    
    /**
     * Returns the processed ViewState object - filtered out tasks not within the week,
     * and completed tasks, and is sorted by date.
     * 
     * @return ViewState object with the processed data from Model.
     */
    private ViewState getDefaultViewState() {
        ViewState vs = new ViewState(model_.getNormalTasks(),
                                     model_.getFloatingTasks(),
                                     model_.getEventTasks());  
        filterFirstWeek(vs);
        filterIncompleted(vs);
        sortViewStateByDate(vs);
        return vs;       
    }
    
    /**
     * Gives the LocalDateTime form of the startDate search parameter from the input commandDetail.
     * null is returned if no such parameter exists. A default day (today) and time (00:00) will be
     * set in the LocalDateTime object if either of the fields are not specified.
     * @param commandDetail
     *              Contains the KatDateTime object startDate, a search parameter.  
     * @return The LocalDateTime object of the startDate search parameter. 
     */
    private LocalDateTime getSearchStartDateFromCommandDetail (CommandDetail commandDetail) {
        LocalDateTime startDate;
        KatDateTime katDate = commandDetail.getStartDate();
        
        if (katDate == null) {
            startDate = null;
        } else {           
            
            // if time isn't specified, set a default time 00:00
            if (katDate.getTime() == null) { 
                LocalDate date = katDate.getDate();
                LocalTime defaultTime = LocalTime.of(00, 00);
                startDate = LocalDateTime.of(date, defaultTime);
                
            // if date isn't specified, set default date today
            } else if (katDate.getDate() == null) { 
                LocalDate defaultDate = LocalDate.now();
                LocalTime time = katDate.getTime();
                startDate = LocalDateTime.of(defaultDate, time);
            } else{
                startDate = katDate.toLocalDateTime();
            }
        }
        return startDate;
    }
    
    /**
     * Gives the LocalDateTime form of the duetDate search parameter from the input commandDetail.
     * null is returned if no such parameter exists. A default day (today) and time (23:59) will be
     * set in the returned LocalDateTime object if either of the fields are not specified.
     * @param commandDetail
     *              Contains the KatDateTime object dueDate, a search parameter.  
     * @return The LocalDateTime object of the dueDate search parameter. 
     */
    private LocalDateTime getSearchDueDateFromCommandDetail (CommandDetail commandDetail) {
        LocalDateTime dueDate;
        KatDateTime katDate = commandDetail.getDueDate();
        
        if (katDate == null) {          
            dueDate = null;
        } else {  
            
            // if time isn't specified, set a default time 23:59
            if (katDate.getTime() == null) { 
                LocalDate date = katDate.getDate();
                LocalTime defaultTime = LocalTime.of(23, 59);
                dueDate = LocalDateTime.of(date, defaultTime);
                
            // if date isn't specified, set default date today
            } else if (katDate.getDate() == null) { 
                LocalDate defaultDate = LocalDate.now();
                LocalTime time = katDate.getTime();
                dueDate = LocalDateTime.of(defaultDate, time);
            } else{
                dueDate = katDate.toLocalDateTime();
            }
        }
        return dueDate;
    }
    
    /**
     * Sets the input path as the source path for KatNote
     * @param newPath   path name to be set
     * @throws FileNotFoundException   
     */
    private void setSourcePath(String newPath) throws FileNotFoundException {

        File sourcePath = new File(MSG_SOURCE_PATH);
        PrintWriter pw = new PrintWriter(sourcePath);
        pw.println(newPath);
        pw.close();
    }
       
    /**
     * Sorts viewState in accordance to date. Normal tasks are sorted by
     * their due dates; Events are sorted by their start dates.
     * @param vs
     *          ViewState to be sorted.
     */
    private void sortViewStateByDate(ViewState vs) {
        ArrayList<Task> normal = vs.getNormalTasks();
        ArrayList<Task> event = vs.getEventTasks();
        
        vs.setNormalTasks(sortByDueDate(normal));
        vs.setEventTasks(sortByStartDate(event));
    }
      
    /**
     * Filters out the completed tasks in the input ViewState object
     * @param vs    The ViewState object to be filtered.
     */
    private void filterIncompleted(ViewState vs) {
        assert(vs != null);
        
        vs.setNormalTasks(getIncompleteTasks(vs.getNormalTasks()));
        vs.setFloatingTasks(getIncompleteTasks(vs.getFloatingTasks()));
        vs.setEventTasks(getIncompleteTasks(vs.getEventTasks()));      
    }
    
    /**
     * Filters the input ViewState object such that only the tasks within 
     * the first week from today is left.
     * @param vs    The ViewState object to be filtered.
     */
    private void filterFirstWeek(ViewState vs) {
        assert(vs != null);
        
        vs.setNormalTasks(filterFirstWeekByDueDate(vs.getNormalTasks()));
        vs.setEventTasks(filterFirstWeekByStartDate(vs.getEventTasks()));
    }
    
    /**
     * Returns the sorted input task list according to their due dates. 
     * Earliest will come first. Time comparison precision is up till Seconds
     * @param taskList List of tasks to be sorted. Task types should be != FLOATING
     */
    private ArrayList<Task> sortByDueDate(ArrayList<Task> taskList) {
        assert(taskList != null);
        
        ArrayList<Task> newList = new ArrayList<Task>(taskList);
        Collections.sort(newList, new TaskDueDateComparator());
        return newList;
    }
    
    /**
     * Returns the sorted input task list according to their start dates. 
     * Earliest will come first. Time comparison precision is up till Seconds
     * @param taskList List of tasks to be sorted. Task types should be == EVENT
     */
    private ArrayList<Task> sortByStartDate(ArrayList<Task> taskList) {
        assert(taskList != null);
        
        ArrayList<Task> newList = new ArrayList<Task>(taskList);
        Collections.sort(newList, new TaskStartDateComparator());
        return newList;
    }
    
    /**
     * Returns the input list with all completed tasks removed.
     * @param list ArrayList of Task that is to be filtered
     * @return the ArrayList of uncompleted Task
     */
    private ArrayList<Task> getIncompleteTasks(ArrayList<Task> list) {
        assert(list != null);
        
        ArrayList<Task> tasksFound = new ArrayList<Task>();

        for (int i = 0; i < list.size(); i++) {
            Task task = list.get(i);
            if (!task.isCompleted()) {
                tasksFound.add(task);
            }
        }
        return tasksFound;
    }
     
    /**
     * Filters out tasks that do not end within the week with reference to current Date,
     * and returns the list.
     * @param list  ArrayList of Task that is to be filtered. TaskType == NORMAL
     */
    private ArrayList<Task> filterFirstWeekByDueDate(ArrayList<Task> list) {
        assert(list != null);
        
        LocalDate duedate = LocalDate.now().plusWeeks(1);
        ArrayList<Task> tasksFound = new ArrayList<Task>();

        for (int i = 0; i < list.size(); i++) {
            Task task = list.get(i);
            LocalDate taskDue = task.getEndDate().toLocalDate();
            if (!taskDue.isAfter(duedate)) {
                tasksFound.add(task);
            }
        }
        return tasksFound;
    }
    
    /**
     * Filters out tasks that do not start by the end of the week with reference 
     * to current Date, and returns the list.
     * @param list  ArrayList of Task that is to be filtered. TaskType == EVENT
     */
    private ArrayList<Task> filterFirstWeekByStartDate(ArrayList<Task> list) {
        assert(list != null);
        
        LocalDate startDate = LocalDate.now().plusWeeks(1);
        ArrayList<Task> tasksFound = new ArrayList<Task>();

        for (int i = 0; i < list.size(); i++) {
            Task task = list.get(i);
            LocalDate taskStart = task.getStartDate().toLocalDate();
            if (!taskStart.isAfter(startDate)) {
                tasksFound.add(task);
            }
        }
        return tasksFound;
    }

}

class Tracker {
    
    private static final int INDEX_OFFSET = 1;
    private static final int ERR_INDEX = -1;
    
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
        if (index < 0 || index >= taskIDList_.size()) {
            return ERR_INDEX;
        } else
            return taskIDList_.get(index);
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
    
    private String keyword_;
    private Boolean isCompleted_; // will be set to null if searching for both

    private LocalDateTime start_; // for events only
    private LocalDateTime due_; // Includes comparison of time too
    
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
    
    public void setDue(LocalDateTime due) {
        due_ = due;
    }
    
    /* Main search method */
    
    public ArrayList<Task> searchData(ArrayList<Task> list) {
        ArrayList<Task> searched = new ArrayList<Task>(list);    
        
        if (list.size() <= 0) {
            return searched;
        }        
        
        if (keyword_ != null) {
            searched = new ArrayList<Task>(findByKeyword(searched));
        }
        
        if (due_ != null) {
            //System.out.println("due_ isnt null");
            searched = new ArrayList<Task>(findDueBy(searched));
        }

        if (isCompleted_ != null) {
            searched = new ArrayList<Task>(findByIsCompleted(searched));
        }

        if (start_ != null) {
            searched = new ArrayList<Task>(findStartFrom(searched));
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
    private ArrayList<Task> findByKeyword(ArrayList<Task> data) {
        ArrayList<Task> tasksFound = new ArrayList<Task>();
        
        for (int i = 0; i < data.size(); i++) {
            Task task = data.get(i);
            String taskTitle = task.getTitle();

            if (isContain(taskTitle, keyword_)) {
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
    private ArrayList<Task> findByIsCompleted(ArrayList<Task> data) {
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
     * input date. Comparison inclusive till seconds.
     * 
     * @param data
     *            ArrayList of tasks to be searched.
     * @return List of tasks with due dates/time before or equals to the
     *         input date. 
     */
    private ArrayList<Task> findDueBy(ArrayList<Task> data) {
        ArrayList<Task> tasksFound = new ArrayList<Task>();
        LocalDateTime duedate = due_;
        LocalDateTime taskDue;
                       
        for (int i = 0; i < data.size(); i++) {
            Task task = data.get(i);
            TaskType type = task.getTaskType();
            
            if (task.getEndDate() == null) {
                break;
            }
            
            if (type == TaskType.EVENT) { 
                taskDue = task.getStartDate(); 
            } else {
                taskDue = task.getEndDate();
            }
            
            if (!taskDue.isAfter(duedate)) {
                tasksFound.add(task);
            }
        }
        return tasksFound;
    }
    
    /**
     * Finds tasks that starts either AFTER or ON the
     * input date. Comparison inclusive till seconds.
     * 
     * @param data
     *            ArrayList of tasks to be searched. Note TaskType == EVENT
     * @return List of tasks with start date/time after or equals to the
     *         input date
     */
    private ArrayList<Task> findStartFrom(ArrayList<Task> data) {
        //System.out.println("Starting findStartFrom");
        LocalDateTime start = start_;
        ArrayList<Task> tasksFound = new ArrayList<Task>();
        LocalDateTime taskStart;
        
        for (int i = 0; i < data.size(); i++) {
            Task task = data.get(i);
            TaskType type = task.getTaskType();
            
            if (type == TaskType.FLOATING || task.getEndDate() == null) {
                break;
            }
            
            if (type == TaskType.EVENT) {        
                taskStart = task.getEndDate();
            } else {
                taskStart = task.getEndDate();
            }
            
            if (!taskStart.isBefore(start)) {
                tasksFound.add(task);
            }
        }
        return tasksFound;
    }
    
    /* Helper Methods */

    // returns true if line contains exactly the input word, case insensitive
    private static boolean isContain(String line, String word) {
        if (line == null) {
            return false;
        }
        
        String lowerCaseWord = word.toLowerCase();
        String lowerCaseLine = line.toLowerCase();

        String pattern = "\\b" + lowerCaseWord + "\\b";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(lowerCaseLine);
        boolean ans = m.find();
        return ans;
    }

}