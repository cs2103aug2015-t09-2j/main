package katnote;

import katnote.command.CommandDetail;
import katnote.command.CommandType;
import katnote.command.Parser;

public class Logic {
	
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
		CommandDetail parsedTask = Parser.parseCommand(command);	
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
	public String process(CommandDetail parsedTask) {
		CommandType type = parsedTask.getCommandType();
		String response;
		
		switch (type) {
			case ADD:
				response = Storage.addNormalTask(parsedTask);
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
