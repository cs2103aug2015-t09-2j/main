package katnote;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;

import org.json.simple.JSONObject;

/**
 * The main class in the Storage component.
 * Creates 3 other sub classes: StorageEncoder, StorageDecoder and StorageData.
 * The main class has a native parser for the commands sent from the Logic component.
 * @author sk
 *
 */
public class Storage {
	
	// Private Variables
	private StorageDecoder _decoder;
	private StorageEncoder _encoder;
	private StorageData _data;
	private ArrayList<String> _actionLog;
	private ArrayList<String> _dataLog;
	private ArrayList<String> _editOldTaskStates;
		
	// Constants
	private static final String DATA_FILENAME = "data.txt";
	private static final int MAX_BUFFER_SIZE = 1024;
	
	// Messages
	private static final String MSG_MIGRATE_CONFIRM = "Save location has successfully moved from %s to %s.";
	private static final String MSG_ERR_IO = "I/O Exception.";
	private static final String MSG_ERR_MISSING_DATA = "Cannot locate data.txt in source.";
	private static final String MSG_ERR_INVALID_TYPE = "Invalid task type: ";
	
	// Command Types
	private static final String ADD_NORMAL = "add_normal";
	private static final String ADD_FLOATING = "add_floating";
	private static final String ADD_EVENT = "add_event";
	private static final String ADD_RECURRING = "add_recurring";
	private static final String EDIT_COMPLETE = "edit_complete";
	private static final String EDIT_MODIFY = "edit_modify";
	private static final String EDIT_DELETE = "edit_delete";
	private static final String VIEW_TASK = "view_task";
	private static final String UNDO = "undo";

	// Constructor
	public Storage(String path) {
		
		_data = new StorageData(path);
		_decoder = new StorageDecoder();
		_encoder = new StorageEncoder();
		_actionLog = new ArrayList<String>();
		_editOldTaskStates = new ArrayList<String>();
		_decoder.decode();
	}
	
	// Main Methods
	public void parseCommand(String type, String[] args) {
		switch (type) {
			case ADD_NORMAL : 		addNormalTask(args);
									break;
									
			case ADD_FLOATING : 	addFloatingTask(args);
									break;
									
			case ADD_EVENT : 		addEventTask(args);
									break;
									
			case ADD_RECURRING : 	addRecurringTask(args);
									break;
									
			case EDIT_COMPLETE : 	editComplete(args);
									break;
									
			case EDIT_MODIFY :		editModify(args);
									break;
			
			case EDIT_DELETE :		editDelete(args);
									break;
				
			case VIEW_TASK : 		viewTask(args);
									break;
									
			case UNDO:				undoLast();
									break;
									
			default :				handleError(MSG_ERR_INVALID_TYPE + type);
									break;
		}
	}

	/**
	 * Add a task with a due date.
	 * args[0]: title
	 * args[1]: due date
	 * args[2]: description
	 * args[3]: category
	 * @param args
	 */
	private void addNormalTask(String[] args) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Add task without a due date.
	 * args[0]: title
	 * args[1]: description
	 * args[2]: category
	 * @param args
	 */
	private void addFloatingTask(String[] args) {
		// TODO Auto-generated method stub
		
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
	private void addEventTask(String[] args) {
		// TODO Auto-generated method stub
		
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
	private void addRecurringTask(String[] args) {
		// TODO Auto-generated method stub
		
	}
	
	/** 
	 * Edit a task and marks it as completed.
	 * args[0]: task_id
	 * @param args
	 */
	private void editComplete(String[] args) {
		// TODO Auto-generated method stub
		
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
	private void editModify(String[] args) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Delete a certain task.
	 * args[0]: task_id
	 * @param args
	 */
	private void editDelete(String[] args) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * View a certain task.
	 * args[0]: task_id - If !null, args[1 .. 3] will be ignored.
	 * args[1]: completed
	 * args[2]: start date
	 * args[3]: end date
	 * @param args
	 */
	private void viewTask(String[] args) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Reverses the last action under the _actionLog.
	 */
	private void undoLast() {
		// TODO Auto-generated method stub
		
	}

	// Helper Methods
	private void handleException(Exception e, String msg) {
		
		System.out.println("Encountered Exception: " + msg);
		e.printStackTrace();
		System.exit(-1);
	}
	
	private void handleError(String msg) {
		System.out.println("Encountered Error: " + msg);
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
			createFiles();
		}
		
		// Get
		public String getPath() {
			
			return _sourcePath;
		}
		
		// Set
		public void setPath(String newPath) {
			
			migrateData(_sourcePath + DATA_FILENAME, newPath + DATA_FILENAME);
			_sourcePath = newPath;
		}
		
		// Helper Methods
		private void createFiles() {
			
			try {
				// Create data file.
				File dataFile = new File(_sourcePath + DATA_FILENAME);
				if (!dataFile.exists()) {
					dataFile.createNewFile();
				}
			} catch (IOException e) {
				handleException(e, MSG_ERR_IO);
			}
		}
		
		private void migrateData(String oldLoc, String newLoc) {
			
			// Execute
			try {
				File oldFile = new File(oldLoc);
	    	    File newFile = new File(newLoc);
	    	    
	    	    if (!oldFile.exists()) {
	    	    	handleError(MSG_ERR_MISSING_DATA);
	    	    	return;
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
	    	    
	    	    System.out.format(MSG_MIGRATE_CONFIRM, oldLoc, newLoc);
	    	    
			} catch (IOException e) {
				handleException(e, MSG_ERR_IO);
			}
		}
	}
}