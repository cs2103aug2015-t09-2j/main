package katnote;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import katnote.command.CommandDetail;
import katnote.command.CommandProperties;
import katnote.command.CommandType;
import katnote.parser.EditTaskOption;
import katnote.parser.Parser;
import katnote.parser.ViewTaskOption;
import katnote.task.Task;
import katnote.task.TaskDueDateComparator;
import katnote.task.TaskStartDateComparator;
import katnote.task.TaskType;
import katnote.utils.KatDateTime;

public class Logic {

    private Model model_;
    private Tracker tracker_;
    private String sourcePathStr;

    // Error Messages
    private static final String MSG_ERR_INVALID_TYPE = "Invalid command type: %s"; // %s is the command type
    
    // Response Messages
    private static final String MSG_RESPONSE_SEARCH_KEYWORD = "Found %d tasks with keyword %s.";
    private static final String MSG_RESPONSE_SEARCH_DEFAULT = "Found %d tasks.";
    private static final String MSG_RESPONSE_VIEW = "Displaying %d tasks.";

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
     * For testing purpose
     */
    public ArrayList<Integer> getTrackerIDList() {
        return tracker_.getIDList();
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
        int tasksFound = 0; // number of tasks displayed for view/search commands

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
            
            case POSTPONE:
                taskID = tracker_.getTaskID(commandDetail.getTaskIndex());
                feedback.setResponse(model_.postpone(taskID, commandDetail.getStartDate()));
                
                updateViewState(vs);
                break;

            case DELETE_TASK :
                // get index from commandDetail
                taskID = tracker_.getTaskID(commandDetail.getTaskIndex());
                feedback.setResponse(model_.editDelete(taskID)); // pass ID to Model
                
                updateViewState(vs);               
                break;

            case VIEW_TASK :
                feedback.setViewState(viewTask(commandDetail)); //note: viewTask sorts the list as well
                vs = feedback.getViewState();
                
                //TODO: REFACTOR
                if (vs.getEventTasks() != null) {
                    tasksFound += vs.getEventTasks().size();
                }
                
                if (vs.getNormalTasks() != null) {
                    tasksFound += vs.getNormalTasks().size();
                }
                
                if (vs.getFloatingTasks() != null) {
                    tasksFound += vs.getFloatingTasks().size();
                }
                
                feedback.setResponse(String.format(MSG_RESPONSE_VIEW, tasksFound));
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
                vs = feedback.getViewState();
                
                //updateViewState(vs);
                String keyword = commandDetail.getFindKeywords();
                
                //TODO: REFACTOR
                if (vs.getEventTasks() != null) {
                    tasksFound += vs.getEventTasks().size();
                }
                
                if (vs.getNormalTasks() != null) {
                    tasksFound += vs.getNormalTasks().size();
                }
                
                if (vs.getFloatingTasks() != null) {
                    tasksFound += vs.getFloatingTasks().size();
                }
               
                feedback.setResponse(String.format(MSG_RESPONSE_SEARCH_KEYWORD, tasksFound, keyword));
                break;

            case SET_LOCATION :
                String newSaveLocation = (String) commandDetail.getFilePath();
                setSourcePath(newSaveLocation);
                feedback.setResponse(model_.setLocation(commandDetail));
                updateViewState(vs);
                
            case IMPORT :
                //String importLocation = (String) commandDetail.getFilePath();
                feedback.setResponse(model_.importData(commandDetail));
                
                updateViewState(vs);
                
            case EXIT :
                feedback.setExit(true);
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
        vs = searchAndUpdate(vs, search);
        return vs;

    }

    /**
     * Returns the list of tasks that should be displayed according to the
     * viewing criteria (and is sorted)
     * 
     * @param commandDetail
     *            specifies the criteria of what tasks should be retrieved
     * @return ArrayList of tasks that satisfy the view criteria
     */
    private ViewState viewTask(CommandDetail commandDetail) {
        Search search = new Search();
        ViewState vs = new ViewState();
        LocalDateTime dueDate;
        LocalDateTime startDate;
        
        Boolean isCompleted = commandDetail.getTaskCompletedOption();
        
        if (commandDetail.getDueDate() == null) {
            dueDate = null;
        } else {
            dueDate = commandDetail.getDueDate().toLocalDateTime();
        }

        if (commandDetail.getStartDate() == null) {
            startDate = null;
        } else {           
            KatDateTime katDate = commandDetail.getStartDate();
            
            if (katDate.getTime() == null) {
                LocalDate date = katDate.getDate();
                LocalTime dummy = LocalTime.of(00, 00);
                startDate = LocalDateTime.of(date, dummy);
            } else { 
                startDate = commandDetail.getStartDate().toLocalDateTime();
            }
        }
        
        search.setIsCompleted(isCompleted);
        search.setDue(dueDate);
        search.setStart(startDate);
              
        // search and return
        vs = searchAndUpdate(vs, search);   
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
     * Searches through the Model data based on the input Search object and updates 
     * the input ViewState with the newly searched state.
     * @param vs
     *          ViewState to be updated
     * @param search
     *          Search object containing the information for sorting
     * @return ViewState object containing lists of tasks that are searched according to the
     * input Search object and sorted in order of their due dates whenever possible.
     */
    private ViewState searchAndUpdate(ViewState vs, Search search) {
        //search
        ArrayList<Task> normal = search.searchData(model_.getNormalTasks());
        ArrayList<Task> floating = search.searchData(model_.getFloatingTasks());
        ArrayList<Task> event = search.searchData(model_.getEventTasks());
        
        //update
        vs.setNormalTasks(sortByDueDate(normal));
        vs.setFloatingTasks(floating);
        vs.setEventTasks(sortByStartDate(event));
        
        return vs;
    }
    
    
    /**
     * Updates input ViewState with the processed data from Model - sorted 
     * according to due dates and filtered out completed tasks.
     * @param vs
     *          ViewState to be updated
     */
    private void updateViewState(ViewState vs) { 
        vs.setNormalTasks(processNormalTaskList(model_.getNormalTasks()));
        vs.setFloatingTasks(getIncomplete(model_.getFloatingTasks()));
        vs.setEventTasks(processEventTaskList(model_.getEventTasks()));
    }
    
    /**
     * Processes the input Normal task list by filtering out the completed tasks and sorting them in accordance
     * to their due dates (earlier tasks come first).
     * @param taskList
     *              The ArrayList of Task to be filtered and processed. Task type == NORMAL
     * @return Returns a filtered list of incomplete tasks that are sorted in accordance of their due dates
     */
    private ArrayList<Task> processNormalTaskList(ArrayList<Task> taskList) {
        ArrayList<Task> newList = new ArrayList<Task>(taskList);
        newList = getIncomplete(newList);
        newList = sortByDueDate(newList);
        newList = filterFirstWeekByDueDate(newList);
        return newList;
    }
    
    /**
     * Processes the input Event task list by filtering out the completed tasks and sorting them in accordance
     * to their start dates (earlier tasks come first).
     * @param taskList
     *              The ArrayList of Task to be filtered and processed. Task type == EVENT
     * @return Returns a filtered list of incomplete tasks that are sorted in accordance of their start dates
     */
    private ArrayList<Task> processEventTaskList(ArrayList<Task> taskList) {
        ArrayList<Task> newList = new ArrayList<Task>(taskList);
        newList = getIncomplete(newList);
        newList = sortByStartDate(newList);
        newList = filterFirstWeekByStartDate(newList);
        return newList;
    }
    
    /**
     * Sorts the input task list according to their due dates (earliest will come first)
     * @param taskList List of tasks to be sorted. Task types should be != FLOATING
     */
    private ArrayList<Task> sortByDueDate(ArrayList<Task> taskList) {
        ArrayList<Task> newList = new ArrayList<Task>(taskList);
        Collections.sort(newList, new TaskDueDateComparator());
        return newList;
    }
    
    /**
     * Sorts the input task list according to their start dates. 
     * Earliest will come first. Time comparison precision is up till Seconds
     * @param taskList List of tasks to be sorted. Task types should be == EVENT
     */
    private ArrayList<Task> sortByStartDate(ArrayList<Task> taskList) {
        ArrayList<Task> newList = new ArrayList<Task>(taskList);
        Collections.sort(newList, new TaskStartDateComparator());
        return newList;
    }
    
    /**
     * Removes all completed tasks from input list
     * @param list ArrayList of Task that is to be filtered
     * @return the ArrayList of uncompleted Task
     */
    private ArrayList<Task> getIncomplete(ArrayList<Task> list) {
        ArrayList<Task> tasksFound = new ArrayList<Task>();

        for (int i = 0; i < list.size(); i++) {
            Task task = list.get(i);
            if (!task.isCompleted()) {
                tasksFound.add(task);
            }
        }
        return tasksFound;
    }
    
    // Filter out tasks that do not end within the week with
    // reference to current Date.
    // TaskType == NORMAL
    private ArrayList<Task> filterFirstWeekByDueDate(ArrayList<Task> list) {
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
    
    // Filter out tasks that do not start within the week with
    // reference to current Date.
    // TaskType == EVENT
    private ArrayList<Task> filterFirstWeekByStartDate(ArrayList<Task> list) {
        LocalDate startDate = LocalDate.now().plusWeeks(1);
        ArrayList<Task> tasksFound = new ArrayList<Task>();

        for (int i = 0; i < list.size(); i++) {
            Task task = list.get(i);
            LocalDate taskDue = task.getStartDate().toLocalDate();
            if (!taskDue.isAfter(startDate)) {
                tasksFound.add(task);
            }
        }
        return tasksFound;
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
        
        TaskType type = list.get(0).getTaskType();
        
        if (keyword_ != null) {
            searched = new ArrayList<Task>(findByKeyword(searched));
        }
        
        if (due_ != null) {
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
            //String taskDescription = task.getDescription();

            if (isContain(taskTitle, keyword_)) { //TODO: Include comparison for taskDescription
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
            
            if (type == TaskType.FLOATING || task.getEndDate() == null) {
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
                //System.out.println("Is floating task.");
                break;
            }
            
            if (type == TaskType.EVENT) {        
                taskStart = task.getEndDate();
            } else {
                taskStart = task.getEndDate();
            }
            
            if (!taskStart.isBefore(start)) {
                //System.out.println(task.getTitle() + " is added");
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
        
        //System.out.printf("Starting isContain(%s, %s)...\n", line, word);
        String lowerCaseWord = word.toLowerCase();
        String lowerCaseLine = line.toLowerCase();

        String pattern = "\\b" + lowerCaseWord + "\\b";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(lowerCaseLine);
        boolean ans = m.find();
        //System.out.printf("isContain(%s, %s) = %s\n", line, word, ans);
        return ans;
    }

}