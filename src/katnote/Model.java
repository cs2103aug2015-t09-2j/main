package katnote;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;

import katnote.command.CommandProperties;
import katnote.command.CommandDetail;
import katnote.command.EditTaskSetOption;

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
	private ArrayList<String> _actionLog;
	private ArrayList<Task> _dataLog;
	private ArrayList<String> _editOldTaskState;
	private String _response;
		
	// Constants
	private static final String DATA_FILENAME = "data.txt";
	private static final int MAX_BUFFER_SIZE = 1024;
	private static final int MAX_ARG_SIZE = 10;
	private static final int INDEX_ID = 0;
	private static final int INDEX_TITLE = 1;
	private static final int INDEX_TRANSLATION = 1; // For translating internal indexing to displayed indexing.
	
	private static final String NULL_DATE = "null";
	private static final String STR_TRUE = "true";
	private static final String STR_FALSE = "false";
	
	// Messages
	private static final String MSG_MIGRATE_CONFIRM = "Save location has successfully moved from %s to %s.";
	private static final String MSG_DATA_FILE_READY = "data.txt is ready for use in %s";
	private static final String MSG_TASK_ADDED = "Task: %s added.";
	private static final String MSG_EDIT_TASK_COMPLETED = "Task %d: %s is marked completed.";
	private static final String MSG_EDIT_TASK_MODIFIED = "Task %d: %s is successfully modified.";
	private static final String MSG_EDIT_TASK_DELETED = "Task %d: %s is successfully deleted.";
	private static final String MSG_UNDO_CONFIRM = "%s undone.";
	private static final String MSG_REDO_CONFIRM = "%s redone.";
	
	private static final String MSG_ERR_IO = "I/O Exception.";
	private static final String MSG_ERR_MISSING_DATA = "Cannot locate data.txt in source.";
	private static final String MSG_ERR_INVALID_TYPE = "Invalid task type: ";
	private static final String MSG_ERR_INVALID_ARGUMENTS = "Invalid arguments.";
	private static final String MSG_ERR_JSON_PARSE_ERROR = "Unabled to parse String to JSONObject.";
	private static final String MSG_ERR_TASK_NOT_MODIFIED = "Unable to process modify parameters.";
	
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
	
	// Format
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

	// Constructor
	public Model(String path) throws Exception {
		
		_data = new StorageData(path);
		_decoder = new StorageDecoder();
		_encoder = new StorageEncoder();
		_actionLog = new ArrayList<String>();
		_editOldTaskState = new ArrayList<String>();
		_dataLog = _decoder.decode();
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
	    
		_encoder.encode();
		
		_response = String.format(MSG_TASK_ADDED, task.getTitle());
		return _response;
	}
	
	/** 
	 * Edit a task and marks it as completed.
	 * @param commandDetail with the TASK_ID property.
	 * @return the response message of a successful change in the completed flag.
	 * @throws Exception 
	 */
	public String editComplete(int editTaskID) throws Exception {
		
		Task editedTask = _dataLog.get(editTaskID);
	    editedTask.setCompleted(true);
	    
	    _encoder.encode();
	    
		_response = String.format(MSG_EDIT_TASK_COMPLETED, editedTask.getID(), editedTask.getTitle());
		return _response;
	}
	
	/**
	 * Modify the fields of a task.
	 * null = no change.
	 * args[0]: task_id <= This will NOT change. Taken as reference number.
	 * args[1]: title
	 * args[2]: task type
	 * args[3]: start date
	 * args[4]: end date
	 * args[5]: repeat option
	 * args[6]: terminate date
	 * args[7]: description
	 * args[8]: category
	 * args[9]: completed
	 * @param task with the data described above.
	 * @return the response message of a successful modification to the specified task.
	 * @throws Exception 
	 */
	public String editModify(int taskID, EditTaskSetOption editOption) throws Exception {
	    
	    Task editedTask = _dataLog.get(taskID);
	    String optionName = editOption.getOptionName();
	    switch (optionName) {
	        case CommandProperties.TASK_ID :
	            editedTask.setID(Integer.parseInt(editOption.getOptionValue()));
	            break;
	        case CommandProperties.TASK_TITLE :
	            editedTask.setTitle(editOption.getOptionValue());
	            break;
	        case CommandProperties.TIME_FROM :
	            editedTask.setStartDate(editOption.getOptionValueDate());
	            break;
	        case CommandProperties.TIME_BY :
	            editedTask.setEndDate(editOption.getOptionValueDate());
	            break;
	        case CommandProperties.TIME_REPEAT :
	            editedTask.setRepeatOption(editOption.getOptionValue());
	            break;
	        case CommandProperties.TIME_UNTIL :
	            editedTask.setTerminateDate(editOption.getOptionValueDate());
	            break;
	        default:
	            _response = String.format(MSG_ERR_TASK_NOT_MODIFIED, taskID, editedTask.getTitle());
	            return _response;
	    }
	    
	    _encoder.encode();
	    
	    _response = String.format(MSG_EDIT_TASK_MODIFIED, taskID, editedTask.getTitle());
	    return _response;
	}
	
	/**
	 * Delete a certain task by task id.
	 * @param iDOfTaskToDelete which will correspond to the index of the task in the dataLog.
	 * @return the response message of a deletion of the specified task.
	 * @throws Exception 
	 */
	public String editDelete(int idOfTaskToDelete) throws Exception {
		
		String title = _dataLog.get(idOfTaskToDelete).getTitle();
		int displayedID = idOfTaskToDelete + INDEX_TRANSLATION;
	    _dataLog.remove(idOfTaskToDelete);
		
		_encoder.encode();
		
		_response = String.format(MSG_EDIT_TASK_DELETED, idOfTaskToDelete + 1, title);
		return _response;
	}
	
	/**
	 * Reverses the last action under the _actionLog.
	 */
	public String undoLast() {
		// TODO Auto-generated method stub
		_response = String.format(MSG_UNDO_CONFIRM, "undo type");
		return _response;
	}
	
	public String redo() {
		// TODO Auto-generated method stub
		_response = String.format(MSG_REDO_CONFIRM, "redo type");
		return _response;
	}
	
	/**
	 * Changes the save location of data.txt
	 * @param commandDetail with the SAVE_LOCATION property.
	 * @return the response message of a successful change in location.
	 * @throws Exception 
	 */
	public String setLocation(CommandDetail commandDetail) throws Exception {
		
		String newSaveLocation = (String) commandDetail.getProperty(CommandProperties.SAVE_LOCATION);
		
		if (newSaveLocation == null) {
			return handleException(new IllegalArgumentException(), MSG_ERR_INVALID_ARGUMENTS);
		}
		_response = _data.setPath(newSaveLocation);
		return _response;
	}
	
	// Get all tasks.
	public ArrayList<Task> getData() {
	    return _dataLog;
	}

	// Helper Methods
	private int getNextID() {
	    return _dataLog.size();
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
		    ArrayList<Task> taskArray = new ArrayList<Task>();
		    String line;
		    
			BufferedReader bReader = new BufferedReader(new FileReader(_data.getDataFilePath()));
			
			// Read through data.txt
			while ((line = bReader.readLine()) != null && !line.equals(MSG_LOG_START)) {
			    // Read till start of task logs
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
	    
	    // Constructor
	    public StorageEncoder() {

	    }
		
		public void encode() throws Exception {

			// Setup environment
		    ArrayList<Task> taskArray = _dataLog;
		    
		    PrintWriter pWriter = createNewDataLog(_data.getDataFilePath());
		    pWriter.println(MSG_LOG_START);
		    
		    // Iterate through Log
		    for (Task t : taskArray) {
		        pWriter.println(getJSONTaskString(t));
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
            taskMap.put(KEY_ID, t.getID().toString());
            taskMap.put(KEY_TITLE, t.getTitle());
            taskMap.put(KEY_TASK_TYPE, t.getTaskType());
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
		private String dateToString(Date date) {
		    if (date == null) {
		        return NULL_DATE;
		    } else {
		        String dateStr = DATE_FORMAT.format(date);
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
			System.out.println(_response);
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
	}
}