package katnote;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;

import org.json.simple.JSONObject;

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
	private ArrayList<String> _dataLog;
	private ArrayList<String> _editOldTaskState;
	private String _response;
		
	// Constants
	private static final String DATA_FILENAME = "data.txt";
	private static final int MAX_BUFFER_SIZE = 1024;
	
	// Messages
	private static final String MSG_MIGRATE_CONFIRM = "Save location has successfully moved from %s to %s.";
	private static final String MSG_DATA_FILE_READY = "data.txt is ready for use in %s";
	private static final String MSG_NORMAL_TASK_ADDED = "Normal Task: %s added.";
	private static final String MSG_FLOATING_TASK_ADDED = "Floating Task: %s added.";
	private static final String MSG_EVENT_TASK_ADDED = "Event Task: %s added.";
	private static final String MSG_RECURRING_TASK_ADDED = "Recurring Task: %s added";
	private static final String MSG_EDIT_TASK_COMPLETED = "Task %d: %s is marked completed.";
	private static final String MSG_EDIT_TASK_MODIFIED = "Task %d: %s is successfully modified.";
	private static final String MSG_EDIT_TASK_DELETED = "Task %d: %s is successfully deleted.";
	private static final String MSG_UNDO_CONFIRM = "%s undone.";
	private static final String MSG_REDO_CONFIRM = "%s redone.";
	
	private static final String MSG_ERR_IO = "I/O Exception.";
	private static final String MSG_ERR_MISSING_DATA = "Cannot locate data.txt in source.";
	private static final String MSG_ERR_INVALID_TYPE = "Invalid task type: ";
	private static final String MSG_ERR_INVALID_ARGUMENTS = "Invalid arguments.";

	// Constructor
	public Model(String path) {
		
		_data = new StorageData(path);
		_decoder = new StorageDecoder();
		_encoder = new StorageEncoder();
		_actionLog = new ArrayList<String>();
		_editOldTaskState = new ArrayList<String>();
		_decoder.decode();
	}
	

	/**
	 * Add a task with a due date.
	 * args[0]: title
	 * args[1]: due date
	 * args[2]: description
	 * args[3]: category
	 * @param args
	 */
	public String addNormalTask(CommandDetail commandDetail) {
		// TODO Auto-generated method stub
		String taskTitle = (String) commandDetail.getProperty(CommandProperties.TASK_TITLE);
		_response = String.format(MSG_NORMAL_TASK_ADDED, taskTitle);
		return _response;
	}
	
	/**
	 * Add task without a due date.
	 * args[0]: title
	 * args[1]: description
	 * args[2]: category
	 * @param args
	 */
	public String addFloatingTask(CommandDetail commandDetail) {
		// TODO Auto-generated method stub
		String taskTitle = (String) commandDetail.getProperty(CommandProperties.TASK_TITLE);
		_response = String.format(MSG_NORMAL_TASK_ADDED, taskTitle);
		return _response;
	}
	
	/**
	 * Add task with a start date and end date.
	 * args[0]: title
	 * args[1]: start date
	 * args[2]: end date
	 * args[3]: description
	 * args[4]: category
	 * @param args
	 */
	public String addEventTask(CommandDetail commandDetail) {
		// TODO Auto-generated method stub
		String taskTitle = (String) commandDetail.getProperty(CommandProperties.TASK_TITLE);
		_response = String.format(MSG_NORMAL_TASK_ADDED, taskTitle);
		return _response;
	}
	
	/**
	 * Add task that repeats on a regular basis.
	 * args[0]: title
	 * args[1]: task option - floating / normal / event
	 * args[2]: start date
	 * args[3]: end date - due date for normal
	 * args[4]: repeat option - recurring interval
	 * args[5]: terminate date - null = forever
	 * args[6]: description
	 * args[7]: category
	 * @param args
	 */
	public String addRecurringTask(CommandDetail commandDetail) {
		// TODO Auto-generated method stub
		String taskTitle = (String) commandDetail.getProperty(CommandProperties.TASK_TITLE);
		_response = String.format(MSG_NORMAL_TASK_ADDED, taskTitle);		
		return _response;
	}
	
	/** 
	 * Edit a task and marks it as completed.
	 * args[0]: task_id
	 * @param args
	 */
	public String editComplete(CommandDetail commandDetail) {
		// TODO Auto-generated method stub
		String taskId = (String) commandDetail.getProperty(CommandProperties.TASK_ID);
		_response = String.format(MSG_EDIT_TASK_COMPLETED, taskId, "<title>");
		return _response;
	}
	
	/**
	 * Modify the fields of a task.
	 * null = no change.
	 * args[0]: task_id
	 * args[1]: title
	 * args[2]: task type
	 * args[3]: start date
	 * args[4]: end date
	 * args[5]: repeat option
	 * args[6]: terminate date
	 * args[7]: description
	 * args[8]: category
	 * args[9]: completed
	 * @param args
	 */
	public String editModify(CommandDetail commandDetail) {
		// TODO Auto-generated method stub
		String taskId = (String) commandDetail.getProperty(CommandProperties.TASK_ID);
		_response = String.format(MSG_EDIT_TASK_MODIFIED, taskId, "<title>");
		return _response;
	}
	
	/**
	 * Delete a certain task.
	 * args[0]: task_id
	 * @param args
	 */
	public String editDelete(CommandDetail commandDetail) {
		// TODO Auto-generated method stub
		String taskId = (String) commandDetail.getProperty(CommandProperties.TASK_ID);
		_response = String.format(MSG_EDIT_TASK_DELETED, taskId, "<title>");
		return _response;
	}
	
	/**
	 * View a certain task.
	 * args[0]: task_id - If !null, args[1 .. 3] will be ignored.
	 * args[1]: completed
	 * args[2]: start date
	 * args[3]: end date
	 * @param args
	 */
	public String viewTask(CommandDetail commandDetail) {
		// TODO Auto-generated method stub
		_response = "<Task Details>";
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
	
	public String find(CommandDetail commandDetail) {
		// TODO Auto-generated method stub
		_response = "<List of matching tasks>";
		return _response;
	}
	
	/**
	 * Changes the save location of data.txt
	 * args[0]: path of new location
	 * @param args
	 * @return
	 */
	public String setLocation(CommandDetail commandDetail) {
		
		String newSaveLocation = (String) commandDetail.getProperty(CommandProperties.SAVE_LOCATION);
		
		if (newSaveLocation == null) {
			return handleException(new IllegalArgumentException(), MSG_ERR_INVALID_ARGUMENTS);
		}
		_response = _data.setPath(newSaveLocation);
		return _response;
	}

	// Helper Methods
	private String handleException(Exception e, String msg) {
		return ("Encountered Exception: " + e + " - " + msg);
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

		public void decode() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	/**
	 * This helper class runs after each modification (By default: Auto-save after each action).
	 * It will encode the modified version of the application memory into the data.txt file.
	 * @author sk
	 *
	 */
	class StorageEncoder {
		
		public void encode() {
			// TODO Auto-generated method stub
			
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
		
		// Constructor
		public StorageData(String path) {
			
			_sourcePath = path;
			_response = createFiles();
			System.out.println(_response);
		}
		
		// Get
		public String getPath() {
			
			return _sourcePath;
		}
		
		// Set
		public String setPath(String newPath) {
			
			_response = migrateData(_sourcePath + DATA_FILENAME, newPath + DATA_FILENAME);
			_sourcePath = newPath;
			return _response;
		}
		
		// Helper Methods
		private String createFiles() {
			
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
		
		private String migrateData(String oldLoc, String newLoc) {
			
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