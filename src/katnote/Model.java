package katnote;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;

import katnote.command.CommandDetail;
import katnote.command.CommandProperties;
import katnote.parser.EditTaskOption;
import katnote.task.Task;
import katnote.task.TaskType;
import katnote.utils.DateTimeUtils;
import katnote.utils.KatDateTime;

/**
 * The main class in the Storage component.
 * Creates 3 other sub classes: StorageEncoder, StorageDecoder and StorageData.
 * The main class has a native parser for the commands sent from the Logic component.
 * @author sk
 *
 */
public class Model {
	
	// Private Variables
	private StorageDecoder _decoder;
	private StorageEncoder _encoder;
	private StorageData _data;
	
	private Hashtable<String, String> _definitions;
	
	private ArrayList<Task> _dataLog;
	private ArrayList<Task> _dataNormalTasks;
	private ArrayList<Task> _dataFloatingTasks;
	private ArrayList<Task> _dataEventTasks;
	
	private Stack<String> _undoLog;
	private Stack<String> _redoLog;
	private Stack<Task> _undoTaskObjLog;
	private Stack<Task> _redoTaskObjLog;
	private String _response;
		
	// Constants
	private static final String DATA_FILENAME = "data.txt";
	private static final int MAX_BUFFER_SIZE = 1024;
	
	private static final String NULL_DATE = "null";
	private static final String STR_TRUE = "true";
	private static final String STR_FALSE = "false";
	
	// Messages
	private static final String MSG_MIGRATE_CONFIRM = "Save location has successfully moved from %s to %s.";
	private static final String MSG_DATA_FILE_READY = "data.txt is ready for use in %s";
	private static final String MSG_TASK_ADDED = "Task: %s added.";
	private static final String MSG_EDIT_TASK_COMPLETED = "Task: %s is marked completed.";
	private static final String MSG_EDIT_TASK_MODIFIED = "Task: %s is successfully modified.";
	private static final String MSG_EDIT_TASK_DELETED = "Task: %s is successfully deleted.";
	private static final String MSG_EDIT_TASK_REPLACED = "Task: %s is successfully replaced with %s";
	private static final String MSG_EDIT_TASK_CLEAR_ALL = "All tasks cleared.";
	private static final String MSG_EDIT_TASK_CLEAR_COMPLETED = "All completed tasks cleared.";
	private static final String MSG_EDIT_TASK_CLEAR_INCOMPLETE = "All incomplete tasks cleared.";
	private static final String MSG_EDIT_TASK_POSTPONE = "Task: %s is postponed to %s";
	private static final String MSG_UNDO_CONFIRM = "%s %s undone.";
	private static final String MSG_REDO_CONFIRM = "%s %s redone.";
	private static final String MSG_IMPORT_CONFIRM = "Successfully imported %s to %s";
	private static final String MSG_DEFINITION_REPLACED = "Definition for %s set to %s";
	private static final String MSG_DEFINITION_ADDED = "New definition for %s set to %s";
	
	private static final String MSG_ERR_IO = "I/O Exception.";
	private static final String MSG_ERR_MISSING_DATA = "Cannot locate data.txt in source.";
	private static final String MSG_ERR_INVALID_TYPE = "Invalid task type: ";
	private static final String MSG_ERR_INVALID_ARGUMENTS = "Invalid arguments.";
	private static final String MSG_ERR_JSON_PARSE_ERROR = "Unabled to parse String to JSONObject.";
	private static final String MSG_ERR_TASK_NOT_MODIFIED = "Unable to process modify parameters.";
	private static final String MSG_ERR_IMPORT_LOCATION_MISSING = "Unable to find data.txt in specified import location.";
	private static final String MSG_ERR_UNDO = "No actions left to undo.";
	private static final String MSG_ERR_REDO = "No actions left to redo.";
	private static final String MSG_ERR_REVERSE_EXCEPTION = "Unable to perform a reverse for action : ";
	private static final String MSG_ERR_REVERSE_ADD = "Unable to perform reverse for adding of task : ";
	private static final String MSG_ERR_REVERSE_MODIFY = "Unable to perform reverse for modifying of task : ";
	private static final String MSG_ERR_REVERSE_DELETE = "Unable to perform reverse for deleting of task : ";
	private static final String MSG_ERR_REVERSE_COMPLETE = "Unable to perform reverse for completion of task : ";
	private static final String MSG_ERR_REVERSE_REPLACE = "Unable to perform reverse for replacing of task : ";
	private static final String MSG_ERR_REVERSE_CLEAR = "Unable to perform reverse for clear tasks.";
	private static final String MSG_ERR_REVERSE_POSTPONE = "Unable to perform reverse for postponed task.";
	private static final String MSG_ERR_START_AFTER_END = "Invalid start date. Start date is after end date.";
	private static final String MSG_ERR_INVALID_TASK_TYPE = "Invalid Task Type. Expecting Event task type.";
	
	private static final String MSG_LOG_START = "<start>";
	
	// Task Property Keys
	private static final String KEY_ID = "id";
	private static final String KEY_TITLE= "title";
	private static final String KEY_TASK_TYPE = "task_type";
	private static final String KEY_START_DATE = "start_date";
	private static final String KEY_END_DATE = "end_date";
	private static final String KEY_REPEAT_OPTION = "repeat_option";
	private static final String KEY_TERMINATE_DATE = "terminate_date";
	private static final String KEY_DESCRIPTION = "description";
	private static final String KEY_CATEGORY = "category";
	private static final String KEY_COMPLETED = "completed";
	
	private static final String TYPE_NORMAL = "NORMAL";
	private static final String TYPE_FLOATING = "FLOATING";
	private static final String TYPE_EVENT = "EVENT";
	
	private static final String CLEAR_ALL = "clear all";
	private static final String CLEAR_COMPLETED = "clear completed";
	private static final String CLEAR_INCOMPLETE = "clear incomplete";
	
	// Undo and Redo
	private static final Exception REVERSE_EXCEPTION = new Exception("Reverse Exception");
	
	private static final String ADD_TASK = "Add Task:";
	private static final String EDIT_MODIFY = "Modify Task:";
	private static final String EDIT_DELETE = "Delete Task:";
	private static final String EDIT_COMPLETE = "Mark Task:";
	private static final String EDIT_REPLACE = "Replaced Task:";
	private static final String EDIT_CLEAR = "Clear Tasks:";
	private static final String EDIT_POSTPONE = "Postponed Task:";
	
	// Standard Default Definitions
	private static final String MORNING = "9.00am";
	private static final String AFTERNOON = "12.00pm";
	private static final String NIGHT = "9.00pm";
	private static final String EVENING = "7.00pm";
	
	// Format
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

	// Constructor
	public Model(String path) throws Exception {
		
		_data = new StorageData(path);
		_decoder = new StorageDecoder();
		_encoder = new StorageEncoder();
		_undoLog = new Stack<String>();
		_undoTaskObjLog = new Stack<Task>();
		_redoLog = new Stack<String>();
		_redoTaskObjLog = new Stack<Task>();
		_dataNormalTasks = new ArrayList<Task>();
		_dataFloatingTasks = new ArrayList<Task>();
		_dataEventTasks = new ArrayList<Task>();
		initDefinitions();
		_dataLog = _decoder.decode();
		splitTaskType(_dataLog);
	}
	

	/**
	 * Add a task and encode it into the data file with its ID.
	 * @param task with all the required data.
	 * @return the response message of a successful addition of a task.
	 * @throws Exception 
	 */
	public String addTask(Task task) throws Exception {
		
    	task.setID(getNextID());
    	_dataLog.add(task);

	    splitTaskType(task);
	    
		_encoder.encode();
		
		// Update UndoLog
		_undoLog.push(ADD_TASK);
		_undoTaskObjLog.push(task);
		resetRedoLog();
		
		_response = String.format(MSG_TASK_ADDED, task.getTitle());
		return _response;
	}
	
	/** 
	 * Edit a task and marks it as completed.
	 * @param taskID which will correspond to the index of the task in the dataLog.
	 * @return the response message of a successful change in the completed flag.
	 * @throws Exception 
	 */
	public String editComplete(int taskID) throws Exception {
		
		Task editedTask = _dataLog.get(taskID);
	    editedTask.setCompleted(true);
	    
	    _encoder.encode();
	    
	    // Update UndoLog
        _undoLog.push(EDIT_COMPLETE);
        _undoTaskObjLog.push(editedTask);
        resetRedoLog();
	    
		_response = String.format(MSG_EDIT_TASK_COMPLETED, editedTask.getTitle());
		return _response;
	}
	
	/**
	 * Modify the fields of a task.
	 * Eg: Floating Tasks should not allow modification of any sort of time.
	 * @param task with the data described above.
	 * @return the response message of a successful modification to the specified task.
	 * @throws Exception 
	 */
	public String editModify(int taskID, EditTaskOption editOption) throws Exception {
	    
	    Task editedTask = _dataLog.get(taskID);
	    Task oldTask = new Task(editedTask);
	    String optionName = editOption.getOptionName();
	    
	    switch (optionName) {
	        // I don't think we should allow modification of task id*
	        case CommandProperties.TASK_ID :
	            handleException(new Exception(), MSG_ERR_TASK_NOT_MODIFIED);
//	            editedTask.setID(Integer.parseInt(editOption.getOptionValue()));
//	            break;
	        case CommandProperties.TASK_TITLE :
	            editedTask.setTitle(editOption.getOptionValue());
	            break;
	        case CommandProperties.TIME_FROM :
	            if (!editedTask.getTaskType().equals(TaskType.EVENT)) {
	                return handleError(MSG_ERR_INVALID_ARGUMENTS);
	            }
	            LocalDateTime newStartDate = DateTimeUtils.updateDateTime(editedTask.getStartDate(), editOption.getOptionValueDate());
	            if (editedTask.getEndDate().isBefore(newStartDate)) {
	                return handleError(MSG_ERR_START_AFTER_END);
	            }
	            editedTask.setStartDate(editOption.getOptionValueDate());
	            break;
	        case CommandProperties.TIME_BY :
	        case CommandProperties.TIME_TO :
	            editedTask.setEndDate(editOption.getOptionValueDate());
	            break;
	        case CommandProperties.TIME_REPEAT :
	            handleException(new Exception(), MSG_ERR_TASK_NOT_MODIFIED);
//	            editedTask.setRepeatOption(editOption.getOptionValue());
//	            break;
	        case CommandProperties.TIME_UNTIL :
	            handleException(new Exception(), MSG_ERR_TASK_NOT_MODIFIED);
//	            editedTask.setTerminateDate(editOption.getOptionValueDate());
//	            break;
	        case CommandProperties.TASK_DESCRIPTION :
	            handleException(new Exception(), MSG_ERR_TASK_NOT_MODIFIED);
//	            editedTask.setDescription(editOption.getOptionValue());
//	            break;
	        case CommandProperties.TASK_CATEGORY :
	            handleException(new Exception(), MSG_ERR_TASK_NOT_MODIFIED);
//	            editedTask.setCategory(editOption.getOptionValue());
//	            break;
	        default:
	            handleException(new Exception(), MSG_ERR_TASK_NOT_MODIFIED);
	    }
	    
	    splitTaskType(_dataLog);
	    
	    _encoder.encode();
	    
	    // Update UndoLog
        _undoLog.push(EDIT_MODIFY);
        _undoTaskObjLog.push(oldTask);
        resetRedoLog();
	    
	    _response = String.format(MSG_EDIT_TASK_MODIFIED, editedTask.getTitle());
	    return _response;
	}
	
	/**
	 * Delete a certain task by task id.
	 * @param taskID which will correspond to the index of the task in the dataLog.
	 * @return the response message of a deletion of the specified task.
	 * @throws Exception 
	 */
	public String editDelete(int taskID) throws Exception {
		
		Task oldTask = _dataLog.get(taskID);
	    String title = _dataLog.get(taskID).getTitle();
	    _dataLog.remove(taskID);
	    updateID(_dataLog);
	    
	    splitTaskType(_dataLog);
		
		_encoder.encode();
		
		// Update UndoLog
        _undoLog.push(EDIT_DELETE);
        _undoTaskObjLog.push(oldTask);
        resetRedoLog();
		
		_response = String.format(MSG_EDIT_TASK_DELETED, title);
		return _response;
	}
	
	/**
	 * Clears all tasks of the specified type.
	 * Available types : CLEAR ALL, CLEAR COMPLETED, CLEAR INCOMPLETE.
	 * This function is not undo-able.
	 * @param type
	 * @return the response message of the success of clearing all of the specified tasks.
	 * @throws Exception
	 */
	public String editClear(String type) throws Exception {
	    // TODO: Undo-functionality?
	    switch (type) {
	        case CLEAR_ALL :
	            _response = clearAll();
	            break;
	        case CLEAR_COMPLETED :
	            _response = clearCompleted();
	            break;
	        case CLEAR_INCOMPLETE :
	            _response = clearIncomplete();
	            break;
	        default :
	            return handleError(MSG_ERR_INVALID_ARGUMENTS);
	    }
	    return _response;
	}
	
	public String replace(int taskID, Task newTask) {
	    
	    Task editedTask = _dataLog.get(taskID);
	    Task oldTask = new Task(editedTask);
	    
	    // Replace with new data.
	    editedTask.setTitle(newTask.getTitle());
	    editedTask.setTaskType(newTask.getTaskType());
	    editedTask.setStartDate(newTask.getStartDate());
	    editedTask.setEndDate(newTask.getEndDate());
	    editedTask.setRepeatOption(newTask.getRepeatOption());
	    editedTask.setTerminateDate(newTask.getTerminateDate());
	    editedTask.setDescription(newTask.getDescription());
	    editedTask.setCategory(newTask.getCategory());
	    editedTask.setCompleted(newTask.isCompleted());
	    
	    splitTaskType(_dataLog);
	    
	    // Update UndoLog
	    _undoLog.push(EDIT_REPLACE);
	    _undoTaskObjLog.push(oldTask);
	    resetRedoLog();
	    
	    _response = String.format(MSG_EDIT_TASK_REPLACED, oldTask.getTitle() ,newTask.getTitle());
	    return _response;
	}
	
	/**
	 * Reverses the last action under the _undoLog.
	 * Undo-able actions : addTask, editModify, editDelete, editComplete
	 * @return the response message of a the success of undoing.
	 * @throws Exception 
	 */
	public String undo() throws Exception {
		
	    if (_undoLog.isEmpty()) {
	        _response = handleError(MSG_ERR_UNDO);
	        return _response;
	    }
	    String undoAction = _undoLog.pop();
	    Task taskObj = _undoTaskObjLog.pop();
	    reverseUndo(undoAction, taskObj);
		_response = String.format(MSG_UNDO_CONFIRM, undoAction, taskObj.getTitle());
		return _response;
	}
	
	/**
	 * Reverses the last action under the _redoLog.
	 * Redo-able actions : addTask, editModify, editDelete, editComplete
	 * @return the response message of a the success of redoing.
	 * @throws Exception 
	 */
	public String redo() throws Exception {
		
	    if (_redoLog.isEmpty()) {
            _response = handleError(MSG_ERR_REDO);
            return _response;
        }
	    String redoAction = _redoLog.pop();
	    Task taskObj = _redoTaskObjLog.pop();
	    reverseRedo(redoAction, taskObj);
		_response = String.format(MSG_REDO_CONFIRM, redoAction, taskObj.getTitle());
		return _response;
	}
	
	/**
	 * Set a definition to a new value. Can be used to add a new definition
	 * or replace an existing definition.
	 * @param keyword, value
	 * @return the response message of the success of setting the definition.
	 * @throws Exception 
	 */
	public String setDefinition(String keyword, String value) throws Exception {
	    
	    if (_definitions.containsKey(keyword)) {
	        _definitions.replace(keyword, value);
	        _response = String.format(MSG_DEFINITION_REPLACED, keyword, value);
	    } else {
	        _definitions.put(keyword, value);
	        _response = String.format(MSG_DEFINITION_ADDED, keyword, value);
	    }
	    
	    _encoder.encode();
	    
	    return _response;
	}
	
	/**
	 * Changes the save location of data.txt
	 * @param commandDetail with the SAVE_LOCATION property.
	 * @return the response message of a successful change in location.
	 * @throws Exception 
	 */
	public String setLocation(CommandDetail commandDetail) throws Exception {
	    
	    if (commandDetail.getFilePath() == null) {
	        return handleException(new IllegalArgumentException(), MSG_ERR_INVALID_ARGUMENTS);
	    }
		
		String newSaveLocation = commandDetail.getFilePath();

		_response = _data.setPath(newSaveLocation);
		return _response;
	}
	
	public String postpone(int taskID, KatDateTime newDate) throws Exception {

	    Task editedTask = _dataLog.get(taskID);
	    
	    if (!editedTask.getTaskType().equals(TaskType.EVENT)) {
	        return handleError(String.format(MSG_ERR_INVALID_TASK_TYPE));
	    }
	    
	    Task oldTask = new Task(editedTask);
	    LocalDateTime start = editedTask.getStartDate();
	    LocalDateTime end = editedTask.getEndDate();
	    
	    // Construct newStartDate
	    if (!newDate.hasDate() && !newDate.hasTime()) {
	        return handleError(MSG_ERR_INVALID_ARGUMENTS);
	    }
	    System.out.println(!newDate.hasDate() && !newDate.hasTime());
	    if (!newDate.hasDate()) {
	        newDate.changeDate(start.toLocalDate());
	    } else if (!newDate.hasTime()) {
	        newDate.changeTime(start.toLocalTime());
	    } else {
	        return handleError(MSG_ERR_INVALID_ARGUMENTS);
	    }
	    
	    // Make new dates
	    LocalDateTime newStartDate = newDate.toLocalDateTime();
	    LocalDateTime newEndDate = addTimeDiff(start, end, newStartDate);

	    editedTask.setStartDate(newStartDate);
	    editedTask.setEndDate(newEndDate);
	    
	    _encoder.encode();
	    
	    // Update UndoLog
	    _undoLog.push(EDIT_POSTPONE);
        _undoTaskObjLog.push(oldTask);
        resetRedoLog();
        
        _response = String.format(MSG_EDIT_TASK_POSTPONE, editedTask.getTitle(), editedTask.getStartDate());
        return _response;
	}
	
	/**
	 * Imports the data.txt file from the specified location and saves it to the local version.
	 * @param commandDetail with the IMPORT_LOCATION property.
	 * @return the response message of a successful import of data.
	 * @throws Exception 
	 */
	public String importData(CommandDetail commandDetail) throws Exception {
	    
	    if (commandDetail.getProperty(CommandProperties.FILE_PATH) == null) {
            return handleException(new IllegalArgumentException(), MSG_ERR_INVALID_ARGUMENTS);
        }
	    String importLocation = (String) commandDetail.getProperty(CommandProperties.FILE_PATH);
	    
	    _response = _data.importData(importLocation);
	    return _response;
	}
	
	public String getDataFilePath() {
	    return _data.getDataFilePath();
	}
	
	// Get tasks data.
	public ArrayList<Task> getData() {
	    return _dataLog;
	}
	
	public ArrayList<Task> getNormalTasks() {
	    return _dataNormalTasks;
	}
	
	public ArrayList<Task> getFloatingTasks() {
	    return _dataFloatingTasks;
	}
	
	public ArrayList<Task> getEventTasks() {
	    return _dataEventTasks;
	}
	
	public Hashtable<String, String> getDefinitions() {
	    return _definitions;
	}

	// Helper Methods	
	private void resetRedoLog() {
	
	    _redoLog.clear();
	    _redoTaskObjLog.clear();
	}
	
	private void reverseUndo(String type, Task taskObj) throws Exception {
	    
	    switch (type) {
	        case ADD_TASK :
	            undoAdd(taskObj);
	            break;
	        case EDIT_MODIFY :
	            undoModify(taskObj);
	            break;
	        case EDIT_DELETE :
	            undoDelete(taskObj);
	            break;
	        case EDIT_COMPLETE :
	            undoComplete(taskObj);
	            break;
	        case EDIT_REPLACE :
	            undoReplace(taskObj);
	            break;
	        case EDIT_CLEAR :
	            undoClear();
	            break;
	        case EDIT_POSTPONE :
	            undoPostpone(taskObj);
	            break;
	        default :
	            handleException(REVERSE_EXCEPTION, MSG_ERR_REVERSE_EXCEPTION + type);
	    }
	}
	
	private void undoAdd(Task taskObj) throws Exception {
	    // Delete the added task.
	    try {
	        Task oldTask = _dataLog.get(taskObj.getID());
	        int removeID = taskObj.getID();
            _dataLog.remove(removeID);
            updateID(_dataLog);
            
            splitTaskType(_dataLog);
            _encoder.encode();

            _redoLog.push(ADD_TASK);
            _redoTaskObjLog.push(oldTask);
            
        } catch (Exception e) {
            handleException(e, MSG_ERR_REVERSE_ADD + taskObj.getTitle());
        }
	    
	}
	
	private void undoModify(Task taskObj) throws Exception {
	    // Delete and add back the old task
	    try {
	        Task oldTask = _dataLog.get(taskObj.getID());
	        int removeID = taskObj.getID();
            _dataLog.remove(removeID);
            _dataLog.add(taskObj.getID(), taskObj);
            updateID(_dataLog);
            
            splitTaskType(_dataLog);
            _encoder.encode();
            
            _redoLog.push(EDIT_MODIFY);
            _redoTaskObjLog.push(oldTask);
            
        } catch (Exception e) {
            handleException(e, MSG_ERR_REVERSE_MODIFY + taskObj.getTitle());
        }
	    
	}
	
	private void undoDelete(Task taskObj) throws Exception {
	    // Add back the task
	    try {
	        _dataLog.add(taskObj.getID(), taskObj);
	        
	        splitTaskType(_dataLog);
	        _encoder.encode();
	        
	        _redoLog.push(EDIT_DELETE);
            _redoTaskObjLog.push(taskObj);
            
	    } catch (Exception e) {
	        handleException(e, MSG_ERR_REVERSE_DELETE + taskObj.getTitle());
	    }
	}
	
	private void undoComplete(Task taskObj) throws Exception {
        // Change task to incomplete.
        try {
            taskObj.setCompleted(false);
            
            _encoder.encode();
            
            _redoLog.push(EDIT_COMPLETE);
            _redoTaskObjLog.push(taskObj);
            
        } catch (Exception e) {
            handleException(e, MSG_ERR_REVERSE_COMPLETE + taskObj.getTitle());
        }
    }
	
	private void undoReplace(Task taskObj) throws Exception {
	    // Replace new task with old task
	    try {
	        Task newTask = _dataLog.get(taskObj.getID());
	        Task redoTask = new Task(newTask);
	        
	        // Replace with new data.
	        newTask.setTitle(taskObj.getTitle());
	        newTask.setTaskType(taskObj.getTaskType());
	        newTask.setStartDate(taskObj.getStartDate());
	        newTask.setEndDate(taskObj.getEndDate());
	        newTask.setRepeatOption(taskObj.getRepeatOption());
	        newTask.setTerminateDate(taskObj.getTerminateDate());
	        newTask.setDescription(taskObj.getDescription());
	        newTask.setCategory(taskObj.getCategory());
	        newTask.setCompleted(taskObj.isCompleted());
	        
	        splitTaskType(_dataLog);
	        _encoder.encode();
	        
	        _redoLog.push(EDIT_REPLACE);
            _redoTaskObjLog.push(redoTask);
	    } catch (Exception e) {
	        handleException(e, MSG_ERR_REVERSE_REPLACE + taskObj.getTitle());
	    }
	}
	
	private void undoClear() {
	    //TODO: Awaiting implementation of clear() function.
	}
    
	private void undoPostpone(Task taskObj) throws Exception {
	    // Replace task with old task
	    try {
	        Task newTask = _dataLog.get(taskObj.getID());
	        Task redoTask = new Task(newTask);
	        
	        // Replace start and end dates.
	        newTask.setStartDate(taskObj.getStartDate());
	        newTask.setEndDate(taskObj.getEndDate());
	        
	        _encoder.encode();
	        
	        _redoLog.push(EDIT_POSTPONE);
	        _redoTaskObjLog.push(redoTask);
	    } catch (Exception e) {
            handleException(e, MSG_ERR_REVERSE_POSTPONE + taskObj.getTitle());
        }
	}
	
	private void reverseRedo(String type, Task taskObj) throws Exception {

        switch (type) {
            case ADD_TASK :
                redoAdd(taskObj);
                break;
            case EDIT_MODIFY :
                redoModify(taskObj);
                break;
            case EDIT_DELETE :
                redoDelete(taskObj);
                break;
            case EDIT_COMPLETE :
                redoComplete(taskObj);
                break;
            case EDIT_REPLACE :
                redoReplace(taskObj);
                break;
            case EDIT_CLEAR :
                redoClear();
                break;
            case EDIT_POSTPONE :
                redoPostpone(taskObj);
                break;
            default :
                handleException(REVERSE_EXCEPTION, MSG_ERR_REVERSE_EXCEPTION + type);
        }
	}
	private void redoAdd(Task taskObj) throws Exception {
	    // Add back the task
	    try {
	        _dataLog.add(taskObj.getID(), taskObj);

	        splitTaskType(_dataLog);
	        _encoder.encode();

	        _undoLog.push(ADD_TASK);
	        _undoTaskObjLog.push(taskObj);

	    } catch (Exception e) {
	        handleException(e, MSG_ERR_REVERSE_ADD + taskObj.getTitle());
	    }

	}

	private void redoModify(Task taskObj) throws Exception {
	    // Delete and add back the old task
	    try {
	        Task oldTask = _dataLog.get(taskObj.getID());
	        int removeID = taskObj.getID();
	        _dataLog.remove(removeID);
	        _dataLog.add(taskObj.getID(), taskObj);
	        updateID(_dataLog);

	        splitTaskType(_dataLog);
	        _encoder.encode();

	        _undoLog.push(EDIT_MODIFY);
	        _undoTaskObjLog.push(oldTask);

	    } catch (Exception e) {
	        handleException(e, MSG_ERR_REVERSE_MODIFY + taskObj.getTitle());
	    }

	}

	private void redoDelete(Task taskObj) throws Exception {
	    // Delete the new task
	    try {
	        Task oldTask = _dataLog.get(taskObj.getID());
	        int removeID = taskObj.getID();
	        _dataLog.remove(removeID);
	        updateID(_dataLog);

	        splitTaskType(_dataLog);
	        _encoder.encode();

	        _undoLog.push(EDIT_DELETE);
	        _undoTaskObjLog.push(oldTask);

	    } catch (Exception e) {
	        handleException(e, MSG_ERR_REVERSE_DELETE + taskObj.getTitle());
	    }
	}

	private void redoComplete(Task taskObj) throws Exception {
	    // Change task to incomplete.
	    try {
	        taskObj.setCompleted(true);

	        _encoder.encode();

	        _undoLog.push(EDIT_COMPLETE);
	        _undoTaskObjLog.push(taskObj);

	    } catch (Exception e) {
	        handleException(e, MSG_ERR_REVERSE_COMPLETE + taskObj.getTitle());
	    }
	}
	
	private void redoReplace(Task taskObj) throws Exception {
	    // Change oldTask back to new Task
	    try {
            Task oldTask = _dataLog.get(taskObj.getID());
            Task undoTask = new Task(oldTask);
            
            // Replace with new data.
            oldTask.setTitle(taskObj.getTitle());
            oldTask.setTaskType(taskObj.getTaskType());
            oldTask.setStartDate(taskObj.getStartDate());
            oldTask.setEndDate(taskObj.getEndDate());
            oldTask.setRepeatOption(taskObj.getRepeatOption());
            oldTask.setTerminateDate(taskObj.getTerminateDate());
            oldTask.setDescription(taskObj.getDescription());
            oldTask.setCategory(taskObj.getCategory());
            oldTask.setCompleted(taskObj.isCompleted());
            
            splitTaskType(_dataLog);
            _encoder.encode();
            
            _undoLog.push(EDIT_REPLACE);
            _undoTaskObjLog.push(undoTask);
            
        } catch (Exception e) {
            handleException(e, MSG_ERR_REVERSE_REPLACE + taskObj.getTitle());
        }
	}
	
	private void redoClear() {
	    // TODO: Awaiting implementation of clear() function.
	}
	
	private void redoPostpone(Task taskObj) throws Exception {
	    // Replace old task with redo task
        try {
            Task oldTask = _dataLog.get(taskObj.getID());
            Task undoTask = new Task(oldTask);
            
            // Replace start and end dates.
            oldTask.setStartDate(taskObj.getStartDate());
            oldTask.setEndDate(taskObj.getEndDate());
            
            _encoder.encode();
            
            _undoLog.push(EDIT_POSTPONE);
            _undoTaskObjLog.push(undoTask);
            
        } catch (Exception e) {
            handleException(e, MSG_ERR_REVERSE_POSTPONE + taskObj.getTitle());
        }
	}
	
	
	/**
	 * Add new definitions here. Initialises the definitions table with default keywords. Runs before decoding.
	 */
	private void initDefinitions() {
	    
	    _definitions = new Hashtable<String, String>();
	    _definitions.put("morning", MORNING);
	    _definitions.put("afternoon", AFTERNOON);
	    _definitions.put("evening", EVENING);
	    _definitions.put("night", NIGHT);
	}
	
	private LocalDateTime addTimeDiff(LocalDateTime start, LocalDateTime end, LocalDateTime newStart) {
	    
	    LocalDateTime newEnd = newStart;
	    
	    long years = start.until(end, ChronoUnit.YEARS);
	    newEnd = newEnd.plusYears(years);
	    
	    long months = start.until(end, ChronoUnit.MONTHS);
        newEnd = newEnd.plusMonths(months);

        long days = start.until(end, ChronoUnit.DAYS);
        newEnd = newEnd.plusDays(days);
        
        long hours = start.until(end, ChronoUnit.HOURS);
        newEnd = newEnd.plusHours(hours);
        
        long minutes = start.until(end, ChronoUnit.MINUTES) % 60;
        newEnd = newEnd.plusMinutes(minutes);
        
	    return newEnd;
	}
	    
	private void splitTaskType(Task task) {
	    TaskType type = task.getTaskType();
        switch (type) {
            case NORMAL :
                _dataNormalTasks.add(task);
                break;
            case FLOATING :
                _dataFloatingTasks.add(task);
                break;
            case EVENT :
                _dataEventTasks.add(task);
                break;
            default :
                _dataNormalTasks.add(task);
                break;
        }
	}
	
	private void splitTaskType(ArrayList<Task> taskArray) {
	    
	    // Reset
	    _dataNormalTasks = new ArrayList<Task>();
	    _dataFloatingTasks = new ArrayList<Task>();
	    _dataEventTasks = new ArrayList<Task>();
	    
	    TaskType type;
	    for (Task t : taskArray) {
	        type = t.getTaskType();
	        switch(type) {
	            case NORMAL :
	                _dataNormalTasks.add(t);
	                break;
	            case FLOATING :
	                _dataFloatingTasks.add(t);
	                break;
	            case EVENT :
	                _dataEventTasks.add(t);
	                break;
	            default :
	                _dataNormalTasks.add(t);
	                break;
	        }
	    }
	}
	
	private int getNextID() {
	    return _dataLog.size();
	}
	
	private void updateID(ArrayList<Task> taskArray) {
	    // Update the id-field for the tasks on each shift.
	    for (int i=0; i<taskArray.size(); i++) {
	        Task updateTask = taskArray.get(i);
	        updateTask.setID(i);
	    }
	}
	
	private String clearAll() throws Exception {
	    _dataLog.clear();
	    splitTaskType(_dataLog);
	    _encoder.encode();
	    return MSG_EDIT_TASK_CLEAR_ALL;
	}
	
	private String clearCompleted() throws Exception {
	    for (Task t : _dataLog) {
            if (t.isCompleted()) {
                _dataLog.remove(t);
            }
        }
        splitTaskType(_dataLog);
        _encoder.encode();
        return MSG_EDIT_TASK_CLEAR_COMPLETED;
	}
	
	private String clearIncomplete() throws Exception {
	    for (Task t : _dataLog) {
            if (!t.isCompleted()) {
                _dataLog.remove(t);
            }
        }
        splitTaskType(_dataLog);
        _encoder.encode();
        return MSG_EDIT_TASK_CLEAR_INCOMPLETE;
	}
	
	@SuppressWarnings("unused")
	/**
	 * Reserved for Unit Testing only. Runs the decoder.
	 * @throws Exception
	 */ 
    public ArrayList<Task> testDecode() throws Exception {
	    return _decoder.decode();
	}
	
	private String handleException(Exception e, String msg) throws Exception {
	    throw new Exception(e + " - " + msg);
	}
	
	private String handleError(String msg) {
		return ("Encountered Error: " + msg);
	}
	
	// Sub Classes
	
	/**
	 * This helper class runs upon creation. It will search for the data.txt file and decode the
	 * data within into the application memory for modifications.
	 * @author sk
	 *
	 */
	class StorageDecoder {
	    
	    // Private variables
	    
	    // Constructor
	    public StorageDecoder() throws Exception {
	        
	    }

		public ArrayList<Task> decode() throws Exception {
			
		    // Setup Environment
		    String[] headerTokens;
		    ArrayList<Task> taskArray = new ArrayList<Task>();
		    String line;
		    
			BufferedReader bReader = new BufferedReader(new FileReader(_data.getDataFilePath()));
			
			// Read through data.txt
			while ((line = bReader.readLine()) != null && !line.equals(MSG_LOG_START)) {
			    // Read header data. Till <start>
			    headerTokens = line.split(" ");
			    interpret(headerTokens);
			}
			
			while ((line = bReader.readLine()) != null) {
			    Task newTask = parseLineToTask(line);
			    taskArray.add(newTask);
			}
			
			// Finished decoding data.txt, close reader.
			bReader.close();
			
			// return taskArray
			return taskArray;
		}
		
		// Helper Method
		private void interpret(String[] tokenArray) {
		    
		    // Add Definitions to hash table.
		    if (tokenArray.length < 1) {
		        // Discard
		        return;
		    } else {
		        String keyword = tokenArray[0];
		        String value = tokenArray[1];
		        if (_definitions.containsKey(keyword)) {
		            _definitions.replace(keyword, value);
		        } else {
		            _definitions.put(keyword, value);
		        }
		    }
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
        private Task parseLineToTask(String line) throws Exception {
		    
		    // Setup Environment
            String jsonText = line;
            JSONParser parser = new JSONParser();
            ContainerFactory containerFactory = new ContainerFactory(){
                public List creatArrayContainer() {
                    return new LinkedList();
                }
                public Map createObjectContainer() {
                    return new LinkedHashMap();
                }                    
            };
		    Task newTask = null;
		    try {		         	        
		        // Push parse details to JSONArray
		        Map jsonMap = (Map) parser.parse(jsonText, containerFactory);
		        JSONArray array = new JSONArray();
		        array.add(jsonMap.get(KEY_ID));
		        array.add(jsonMap.get(KEY_TITLE));
		        array.add(jsonMap.get(KEY_TASK_TYPE));
		        array.add(jsonMap.get(KEY_START_DATE));
		        array.add(jsonMap.get(KEY_END_DATE));
		        array.add(jsonMap.get(KEY_REPEAT_OPTION));
		        array.add(jsonMap.get(KEY_TERMINATE_DATE));
		        array.add(jsonMap.get(KEY_DESCRIPTION));
		        array.add(jsonMap.get(KEY_CATEGORY));
		        array.add(jsonMap.get(KEY_COMPLETED));
		        
		        // Create new Task and return
		        newTask = new Task(array);
		        return newTask;
		        
		    } catch(Exception e){
		        handleException(e, MSG_ERR_JSON_PARSE_ERROR);
		    }
		     
		    return newTask;
		}		
	}
	
	/**
	 * This helper class runs after each modification (By default: Auto-save after each action).
	 * It will encode the modified version of the application memory (_data) into the data.txt file.
	 * @author sk
	 *
	 */
	class StorageEncoder {
	    
	    // Private Variables
	    Integer _id;
	    
	    // Constructor
	    public StorageEncoder() {

	    }
		
		public void encode() throws Exception {

			// Setup environment
		    ArrayList<Task> taskArray = _dataLog;
		    _id = 0;
		    
		    PrintWriter pWriter = createNewDataLog(_data.getDataFilePath());
		    // Print all definitions before <start>
		    for (String keyword : _definitions.keySet()) {
		        pWriter.println(keyword + " " + _definitions.get(keyword));
		    }
		    pWriter.println(MSG_LOG_START);
		    
		    // Iterate through Log
		    for (Task t : taskArray) {
		        pWriter.println(getJSONTaskString(t));
		        _id++;
		    }
		    
		    pWriter.close();
		    // Encoding done. Data saved.
		}
		
		private PrintWriter createNewDataLog(String path) throws Exception {
		    
		    File dataFile = new File(path);
		    
		    try {
    		    if (!dataFile.exists()) {
    		        // Should not happen; Recovery.
    		        dataFile.createNewFile();
    		    }
    		    PrintWriter pWriter = new PrintWriter(dataFile);
    		    return pWriter;
    		    
		    } catch (Exception e) {
		        throw new Exception(e);
		    }
		}
		
		@SuppressWarnings("unchecked")
        private String getJSONTaskString(Task t) {
		    
		    Map taskMap = new LinkedHashMap();
            taskMap.put(KEY_ID, _id.toString());
            taskMap.put(KEY_TITLE, t.getTitle());
            taskMap.put(KEY_TASK_TYPE, typeToString(t.getTaskType()));
            taskMap.put(KEY_START_DATE, dateToString(t.getStartDate()));
            taskMap.put(KEY_END_DATE, dateToString(t.getEndDate()));
            taskMap.put(KEY_REPEAT_OPTION, t.getRepeatOption());
            taskMap.put(KEY_TERMINATE_DATE, dateToString(t.getTerminateDate()));
            taskMap.put(KEY_DESCRIPTION, t.getDescription());
            taskMap.put(KEY_CATEGORY, t.getCategory());
            taskMap.put(KEY_COMPLETED, boolToString(t.isCompleted()));
            String jsonText = JSONValue.toJSONString(taskMap);
            return jsonText;
		}
		
		// Helper Methods
		private String typeToString(TaskType taskType) {
		    
		    String type;
		    
		    if (taskType == null) {
		        return null;
		    }
		    switch (taskType) {
		        case NORMAL :
		            type = TYPE_NORMAL;
		            break;
		        case FLOATING:
		            type = TYPE_FLOATING;
		            break;
		        case EVENT :
		            type = TYPE_EVENT;
		            break;
		        default:
		            return TYPE_NORMAL;
		    }
		    
		    return type;
		}
		
		private String dateToString(LocalDateTime date) {
		    if (date == null) {
		        return NULL_DATE;
		    } else {
		        String dateStr = date.format(DATE_FORMATTER);
		        return dateStr;
		    }
		}
		
		private String boolToString(boolean bool) {
		    if (bool) {
		        return STR_TRUE;
		    } else {
		        return STR_FALSE;
		    }
		}
	}
	
	/**
	 * This helper class will store the location of the data.txt and related functionalities.
	 * 
	 * @author sk
	 *
	 */
	class StorageData {
	
		// Private Variables
		private String _sourcePath;
		private String _dataFilePath;
		
		// Constructor
		public StorageData(String path) throws Exception {
			
			_sourcePath = path;
            _dataFilePath = path + DATA_FILENAME;
			_response = createFiles();
			// System.out.println(_response);
		}
		
		// Get
		public String getPath() {
			
			return _sourcePath;
		}
		
		public String getDataFilePath() {
		    
		    return _dataFilePath;
		}
		
		// Set
		public String setPath(String newPath) throws Exception {
			
			_response = migrateData(_sourcePath + DATA_FILENAME, newPath + DATA_FILENAME);
			_sourcePath = newPath;
			_dataFilePath = newPath + DATA_FILENAME;
			return _response;
		}
		
		// Import
		public String importData(String importLocation) throws Exception {
		    
		    // Look for data.txt in importLocation
		    String importFilePath = importLocation + DATA_FILENAME;
		    File importFile = new File(importFilePath);
		    if (!importFile.exists()) {
		        return handleError(MSG_ERR_IMPORT_LOCATION_MISSING);
		    }
		    
		    // Import data over. (Overwrite onto existing)
		    File dataFile = new File(_dataFilePath);
		    if (dataFile.exists()) {
		        dataFile.delete();
		    }
		    copyData(importFilePath, _dataFilePath);
		    
		    _response = String.format(MSG_IMPORT_CONFIRM, importFilePath, _dataFilePath);
		    return _response;
		}
		
		// Helper Methods
		private String createFiles() throws Exception {
			
			try {
				// Create data file.
				File dataFile = new File(_sourcePath + DATA_FILENAME);
				if (!dataFile.exists()) {
					dataFile.createNewFile();
				}
				return String.format(MSG_DATA_FILE_READY, dataFile.getPath());
			} catch (IOException e) {
				return handleException(e, MSG_ERR_IO);
			}
		}
		
		private String migrateData(String oldLoc, String newLoc) throws Exception {
			
			// Execute
			try {
				File oldFile = new File(oldLoc);
	    	    File newFile = new File(newLoc);
	    	    
	    	    if (!oldFile.exists()) {
	    	    	return handleError(MSG_ERR_MISSING_DATA);
	    	    }
	    		
	    	    newFile.createNewFile();
	    	    FileInputStream inStream = new FileInputStream(oldFile);
	    	    FileOutputStream outStream = new FileOutputStream(newFile);
	        	
	    	    byte[] buffer = new byte[MAX_BUFFER_SIZE];
	    		
	    	    int length;
	    	    //copy the file content in bytes 
	    	    while ((length = inStream.read(buffer)) > 0) {
	    	    	outStream.write(buffer, 0, length);
	    	    }
	    	 
	    	    inStream.close();
	    	    outStream.close();
	    	    
	    	    //delete the original file
	    	    oldFile.delete();
	    	    
	    	    return String.format(MSG_MIGRATE_CONFIRM, oldLoc, newLoc);
	    	    
			} catch (IOException e) {
				return handleException(e, MSG_ERR_IO);
			}
		}
		
		private void copyData(String src, String dest) throws Exception {
		    
		    try {
		        File srcFile = new File(src);
		        File destFile = new File(dest);
		        
		        destFile.createNewFile();
		        FileInputStream inStream = new FileInputStream(srcFile);
		        FileOutputStream outStream = new FileOutputStream(destFile);
		        
		        byte[] buffer = new byte[MAX_BUFFER_SIZE];
		        
		        int length;
		        while((length = inStream.read(buffer)) > 0) {
		            outStream.write(buffer, 0, length);
		        }
		        
		        inStream.close();
		        outStream.close();
		        
		   } catch (IOException e) {
		        handleException(e, MSG_ERR_IO);
		   }
		}
	}
}