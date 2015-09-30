package katnote;

public class Logic {
	
	// Command Types

	/* Create */
	private static final String ADD_NORMAL = "add_normal";
	private static final String ADD_FLOATING = "add_floating";
	private static final String ADD_EVENT = "add_event";
	private static final String ADD_RECURRING = "add_recurring";

	/* Read */
	private static final String VIEW_TASK = "view_task";
	private static final String FIND = "find";

	/* Update */
	private static final String EDIT_COMPLETE = "edit_complete";
	private static final String EDIT_MODIFY = "edit_modify";

	/* Delete */
	private static final String EDIT_DELETE = "edit_delete";

	/* Others */
	private static final String UNDO = "undo";
	private static final String REDO = "redo"; 
	private static final String SET_LOCATION = "set_location"; //set save location

	// Error Messages
	private static final String MSG_ERR_INVALID_TYPE = "Invalid command type: %s"; //%s is the command type
	private static final String MSG_ERR_INVALID_ARGS = "Invalid arguments: %s"; // %s is the string of arguments
	
	
	/**
	 * Takes in String command from GUI, parse and process the command and return the response message
	 * 
	 * @param command	String input from GUI command line interface
	 * @return	String with the message to be displayed by the GUI after the command is executed
	 */
	public String execute(String command) {
		String response;
		CommandDetails parsedTask = Parser.getCommandDetails(command);	
		response = process(parsedTask);		
		return response;
	}
	
	/**
	 * Takes in a CommandDetails object containing the parsed command, pass it to Storage to handle the 
	 * respective command types, and return the response message.
	 * 
	 * @param parsedTask	CommandDetails object of the parsed command
	 * @return String with the corresponding response after processing the command
	 */
	public String process(CommandDetails parsedTask) {
		String type = parsedTask.getType();
		String args = parsedTask.getArgs();
		String response;
		
		switch (type) {
			case ADD_NORMAL : 		response = Storage.addNormalTask(args);
									break;
									
			case ADD_FLOATING : 	response = Storage.addFloatingTask(args);
									break;
									
			case ADD_EVENT : 		response = Storage.addEventTask(args);
									break;
									
			case ADD_RECURRING : 	response = Storage.addRecurringTask(args);
									break;
									
			case EDIT_COMPLETE : 	response = Storage.editComplete(args);
									break;
									
			case EDIT_MODIFY :		response = Storage.editModify(args);
									break;
			
			case EDIT_DELETE :		response = Storage.editDelete(args);
									break;
				
			case VIEW_TASK : 		response = Storage.viewTask(args);
									break;
									
			case UNDO:				response = Storage.undoLast();
									break;
									
			case REDO:				response = Storage.redo();
									break;
			
			case FIND:				response = Storage.find(args);
									break;
			
			case SET_LOCATION:		response = Storage.setLocation(args);
									break;
			
			default :				response = String.format(MSG_ERR_INVALID_TYPE, type);
									break;
		}
		
		return response;
	}
	
	
}
