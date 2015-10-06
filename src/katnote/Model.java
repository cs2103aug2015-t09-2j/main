package katnote;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import katnote.command.CommandProperties;
import katnote.command.CommandDetail;

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
	private int _nextID;
		
	// Constants
	private static final String DATA_FILENAME = "data.txt";
	private static final int MAX_BUFFER_SIZE = 1024;
	private static final int MAX_ARG_SIZE = 10;
	private static final int INDEX_ID = 0;
	private static final int INDEX_TITLE = 1;
	
	// Messages
	private static final String MSG_MIGRATE_CONFIRM = "Save location has successfully moved from %s to %s.";
	private static final String MSG_DATA_FILE_READY = "data.txt is ready for use in %s";
	private static final String MSG_TASK_ADDED = "Task: %s added.";
	private static final String MSG_EDIT_TASK_COMPLETED = "Task %i: %s is marked completed.";
	private static final String MSG_EDIT_TASK_MODIFIED = "Task %i: %s is successfully modified.";
	private static final String MSG_EDIT_TASK_DELETED = "Task %i: %s is successfully deleted.";
	private static final String MSG_UNDO_CONFIRM = "%s undone.";
	private static final String MSG_REDO_CONFIRM = "%s redone.";
	
	private static final String MSG_ERR_IO = "I/O Exception.";
	private static final String MSG_ERR_MISSING_DATA = "Cannot locate data.txt in source.";
	private static final String MSG_ERR_INVALID_TYPE = "Invalid task type: ";
	private static final String MSG_ERR_INVALID_ARGUMENTS = "Invalid arguments.";
	
	private static final String MSG_LOG_START = "<start>";

	// Constructor
	public Model(String path) throws Exception {
		
		_data = new StorageData(path);
		_decoder = new StorageDecoder();
		_encoder = new StorageEncoder();
		_actionLog = new ArrayList<String>();
		_editOldTaskState = new ArrayList<String>();
		_dataLog = _decoder.decode();
		_nextID = getNextID();
	}
	

	/**
	 * Add a task and encode it into the data file with its ID.
	 * @param task with all the required data.
	 * @return the response message of a successful addition of a task.
	 * @throws Exception 
	 */
	public String addTask(Task task) throws Exception {
		
	    task.setID(getNextID());
	    
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
	public String editComplete(CommandDetail commandDetail) throws Exception {
		
	    Integer taskID = (Integer) commandDetail.getProperty(CommandProperties.TASK_ID);
		Task editedTask = _dataLog.get(taskID);
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
	public String editModify(Task task) throws Exception {
		
		int oldTaskID = task.getID();
		Task oldTask = _dataLog.get(oldTaskID);
		
		if (task.getTitle() != null) {
		    oldTask.setTitle(task.getTitle());
		}
		if (task.getTaskType() != null) {
            oldTask.setTaskType(task.getTaskType());
        }
		if (task.getStartDate() != null) {
            oldTask.setStartDate(task.getStartDate());
        }
		if (task.getEndDate() != null) {
            oldTask.setEndDate(task.getEndDate());
        }
		if (task.getRepeatOption() != null) {
            oldTask.setRepeatOption(task.getRepeatOption());
        }
		if (task.getTerminateDate() != null) {
            oldTask.setTerminateDate(task.getTerminateDate());
        }
		if (task.getDescription() != null) {
            oldTask.setDescription(task.getDescription());
        }
		if (task.getCategory() != null) {
            oldTask.setCategory(task.getCategory());
        }
		
		_encoder.encode();
		
		_response = String.format(MSG_EDIT_TASK_MODIFIED, oldTaskID, task.getTitle());
		return _response;
	}
	
	/**
	 * Delete a certain task by task id.
	 * @param commandDetail with the TASK_ID property.
	 * @return the response message of a deletion of the specified task.
	 * @throws Exception 
	 */
	public String editDelete(CommandDetail commandDetail) throws Exception {
		
		Integer taskID = (Integer) commandDetail.getProperty(CommandProperties.TASK_ID);
		_dataLog.remove(taskID);
		
		_encoder.encode();
		
		_response = String.format(MSG_EDIT_TASK_DELETED, taskID, "<title>");
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
		    int lastID = 0;
		    
			BufferedReader bReader = new BufferedReader(new FileReader(_data.getDataFilePath()));
			
			// Read through data.txt
			while (!(line = bReader.readLine()).equals(MSG_LOG_START)) {
			    // Read till start of task logs
			}
			
			while ((line = bReader.readLine()) != null) {
			    lastID++;
			    Object obj = JSONValue.parse(line);
			    JSONArray array = (JSONArray) obj;
			    array.set(INDEX_ID, lastID);
			    Task newTask = new Task(array);
			    taskArray.add(newTask);
			}
			
			// Finished decoding data.txt, close reader.
			bReader.close();
			
			// return taskArray
			return taskArray;
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
            taskMap.put("id", t.getID());
            taskMap.put("title", t.getTitle());
            taskMap.put("task type", t.getTaskType());
            taskMap.put("start date", t.getStartDate());
            taskMap.put("end date", t.getEndDate());
            taskMap.put("repeat option", t.getRepeatOption());
            taskMap.put("terminate date", t.getTerminateDate());
            taskMap.put("description", t.getDescription());
            taskMap.put("category", t.getCategory());
            taskMap.put("completed", t.getCompleted());
            String jsonText = JSONValue.toJSONString(taskMap);
            return jsonText;
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