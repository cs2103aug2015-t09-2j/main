package katnote;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import katnote.command.CommandDetail;
import katnote.command.CommandProperties;
import katnote.command.CommandType;
import katnote.parser.EditTaskOption;
import katnote.parser.Parser;
import katnote.task.Task;

public class Logic {

    private Model model;
    private ArrayList<Integer> listOfTaskIDLastDisplayed;
    private String sourcePathStr;

    // Error Messages
    private static final String MSG_ERR_INVALID_TYPE = "Invalid command type: %s"; // %s
                                                                                   // is
                                                                                   // the
                                                                                   // command
                                                                                   // type
    private static final String MSG_ERR_INVALID_ARGS = "Invalid arguments: %s"; // %s
                                                                                // is
                                                                                // the
                                                                                // string
                                                                                // of
                                                                                // arguments
    private static final String MSG_SOURCE_PATH = "sourcepath.txt";
    private static final String MSG_DEFAULT_SOURCE_PATH = "";

    // Constructor
    public Logic() throws Exception {

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
        model = new Model(sourcePathStr);
        listOfTaskIDLastDisplayed = new ArrayList<Integer>();
    }

    // Public methods

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
        UIFeedback response;
        CommandDetail parsedTask = Parser.parseCommand(command);
        response = process(parsedTask);
        return response;
    }

    public UIFeedback process(CommandDetail commandDetail) throws Exception {
        CommandType type = commandDetail.getCommandType();
        UIFeedback response = new UIFeedback();

        Task task;
        switch (type) {
            case ADD_TASK :
                task = new Task(commandDetail);
                response.setResponse(model.addTask(task));
                break;

            case EDIT_MODIFY :
                int idTaskToEdit = getTaskIDFromIndex(getTaskIndex(commandDetail));
                EditTaskOption editOptions = getEditTaskOption(commandDetail);
                response.setResponse(model.editModify(idTaskToEdit, editOptions)); // TODO
                break;

            case EDIT_COMPLETE :
                int idTaskToComplete = getTaskIDFromIndex(getTaskIndex(commandDetail));
                response.setResponse(model.editComplete(idTaskToComplete)); // TODO
                break;

            case DELETE_TASK :
                // getting index from commandDetail
                int indexDelete = getTaskIndex(commandDetail);
                int idOfTaskToDelete = getTaskIDFromIndex(indexDelete);

                // listOfTaskIDLastDisplayed.remove(indexDelete); // remove from
                // internal ID list
                response.setResponse(model.editDelete(idOfTaskToDelete)); // pass
                                                                          // ID
                                                                          // to
                                                                          // Model
                break;

            case VIEW_TASK :
                response.setTaskList(viewTask(commandDetail));
                getID(response.getTaskList());
                break;

            case UNDO :
                response.setResponse(model.undoLast());
                break;

            case REDO :
                response.setResponse(model.redo());
                break;

            case FIND_TASKS :
                response.setTaskList(find(commandDetail));
                getID(response.getTaskList());
                break;

            case SET_LOCATION :
                String newSaveLocation = (String) commandDetail.getProperty(CommandProperties.LOCATION);
                setSourcePath(newSaveLocation);
                response.setResponse(model.setLocation(commandDetail));

                break;

            default :
                response.setError(true);
                response.setResponse(String.format(MSG_ERR_INVALID_TYPE, type));
                break;
        }
        return response;

    }

    /*-- Main Functions --*/

    private void getID(ArrayList<Task> taskList) {
        listOfTaskIDLastDisplayed = new ArrayList<Integer>(); // clear list

        if (!taskList.isEmpty()) {
            for (int i = 0; i < taskList.size(); i++) {
                listOfTaskIDLastDisplayed.add(taskList.get(i).getID());
            }
        }
    }

    /**
     * Search for tasks that match the specifications in the commandDetail input
     * 
     * @param commandDetail
     *            Contains the criteria for searching
     * @return An ArrayList of tasks that match the search criteria
     */

    private ArrayList<Task> find(CommandDetail commandDetail) {
        Search srch = new Search(model.getData()); // Retrieves ArrayList of
                                                   // tasks currently in memory
                                                   // from model

        // if search all tasks by keyword
        String keyword = commandDetail.getString(CommandProperties.FIND_KEYWORDS);
        srch.setKeyword(keyword);
        return srch.findByKeyword();
    }

    /**
     * Returns the list of tasks that should be displayed according to the
     * viewing criteria
     * 
     * @param commandDetail
     *            specifies the criteria of what tasks should be retrieved
     * @return ArrayList of tasks that satisfy the view criteria
     */
    private ArrayList<Task> viewTask(CommandDetail commandDetail) {
        Search srch = new Search(model.getData()); // Retrieves ArrayList of
                                                   // tasks currently in memory
                                                   // from model

        return srch.findAllTasks();

        // srch.setIsCompleted(false);
        // return srch.findAllIncompleteTasks();

        // Date end = (Date) commandDetail.getDate(CommandProperties.TIME_BY);
        // srch.setEnd(end);
        // return findIncompleteTaskDueOn();

    }

    /*-- Helper Functions --*/

    private EditTaskOption getEditTaskOption(CommandDetail commandDetail) {
        return (EditTaskOption) commandDetail.getProperty(CommandProperties.EDIT_SET_PROPERTY);
    }

    // Returns the ID of the task specified by the index based on
    // listOfTaskIDLastDisplayed
    private int getTaskIDFromIndex(int index) {
        return listOfTaskIDLastDisplayed.get(index);
    }

    // Returns the INDEX of the task specified in commandDetail based on what's
    // displayed
    private int getTaskIndex(CommandDetail commandDetail) {
        return Integer.valueOf(commandDetail.getString(CommandProperties.TASK_ID)) - 1;
    }

    private void setSourcePath(String newPath) throws FileNotFoundException {

        File sourcePath = new File(MSG_SOURCE_PATH);
        PrintWriter pw = new PrintWriter(sourcePath);
        pw.println(newPath);
        pw.close();
    }

}

class Search {

    private ArrayList<Task> data_;
    private String keyword_;
    private boolean isCompleted_;

    private Date start_;
    private Date end_;

    public Search(ArrayList<Task> data) { // initialise a Search object with the
                                          // list of Tasks to be search
        data_ = data;
    }

    public void setKeyword(String keyword) {
        keyword_ = keyword;
    }

    public void setIsCompleted(boolean isCompleted) {
        isCompleted_ = isCompleted;
    }

    public void setStart(Date start) {
        start_ = start;
    }

    public void setEnd(Date end) {
        end_ = end;
    }

    public ArrayList<Task> searchData() {
        return null;
    }

    /**
     * Finds all the tasks that contains the keyword (either in the task's name
     * or description) NOTE: This is case insensitive
     * 
     * @param data
     *            ArrayList of all tasks that are added before in the memory
     * @param keyword
     *            String that tasks found should contain in their Name or
     *            Description fields
     * @return List of tasks that contains the input keyword
     */
    ArrayList<Task> findByKeyword() {
        ArrayList<Task> tasksFound = new ArrayList<Task>();

        for (int i = 0; i < data_.size(); i++) {
            Task task = data_.get(i);
            String taskTitle = task.getTitle();
            String taskDescription = task.getDescription();

            if (isContain(taskTitle, keyword_) || isContain(taskDescription, keyword_)) {
                tasksFound.add(task);
            }
        }
        return tasksFound;
    }

    ArrayList<Task> findAllIncompleteTasks() {
        ArrayList<Task> tasksFound = new ArrayList<Task>();

        for (int i = 0; i < data_.size(); i++) {
            Task task = data_.get(i);
            if (!task.isCompleted()) {
                tasksFound.add(task);
            }
        }
        return tasksFound;
    }

    ArrayList<Task> findAllTasks() {
        return data_;
    }

    /**
     * Finds all the uncompleted tasks that are due either BEFORE or BY the
     * input date
     * 
     * @param data
     *            ArrayList of all tasks that are added before in the memory
     * @param input
     *            The date that tasks found should be due by
     * @return List of incomplete tasks with due dates before or equals to the
     *         input date (regardless of time)
     */
    ArrayList<Task> findIncompleteTaskDueOn() {
        LocalDate duedate = dateToLocalDate(end_);
        ArrayList<Task> tasksFound = new ArrayList<Task>();

        for (int i = 0; i < data_.size(); i++) {
            Task task = data_.get(i);
            if (!task.isCompleted()) {
                LocalDate taskDue = dateToLocalDate(task.getEndDate());
                if (!taskDue.isAfter(duedate)) {
                    tasksFound.add(task);
                }
            }
        }
        return tasksFound;
    }

    // Creates a LocalDate object from a Date object
    public static LocalDate dateToLocalDate(Date input) {
        LocalDate date = input.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return date;
    }

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
