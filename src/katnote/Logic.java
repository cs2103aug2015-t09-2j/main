package katnote;

import katnote.command.CommandDetail;
import katnote.command.CommandType;
import katnote.command.Parser;

public class Logic {
	
	private static final String SET_LOCATION = "set_location"; //set save location

	// Error Messages
	private static final String MSG_ERR_INVALID_TYPE = "Invalid command type: %s"; //%s is the command type
	private static final String MSG_ERR_INVALID_ARGS = "Invalid arguments: %s"; // %s is the string of arguments
	
	
	
	
	private Model model;
	
	
	public Logic(){
		model = new Model("");
	}
	
	
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
	 * Takes in a CommandDetail object containing the parsed command, pass it to Storage to handle the 
	 * respective command types, and return the response message.
	 * 
	 * @param commandDetail	CommandDetail object of the parsed command
	 * @return String with the corresponding response after processing the command
	 */
	public String process(CommandDetail commandDetail) {
		CommandType type = commandDetail.getCommandType();
		String response;
		
		switch (type) {
			case ADD_NORMAL:
				response = model.addNormalTask(commandDetail);
				break;									
			case ADD_FLOATING :
				response = model.addFloatingTask(commandDetail);
				break;
									
			case ADD_EVENT :
				response = model.addEventTask(commandDetail);
				break;
									
			case ADD_RECURRING :
				response = model.addRecurringTask(commandDetail);
				break;
									
			case EDIT_COMPLETE :
				response = model.editComplete(commandDetail);
				break;
									
			case EDIT_MODIFY :
				response = model.editModify(commandDetail);
				break;
			
			case EDIT_DELETE :
				response = model.editDelete(commandDetail);
				break;
				
			case VIEW_TASK : 
				response = model.viewTask(commandDetail);
				break;
									
			case UNDO:
				response = model.undoLast();
				break;
									
			case REDO:
				response = model.redo();
				break;
			
			case FIND:
				response = model.find(commandDetail);
				break;
			
			case SET_LOCATION:
				response = model.setLocation(commandDetail);
				break;
			
			default :
				response = String.format(MSG_ERR_INVALID_TYPE, type);
				break;
		}
		
		return response;
	}
	
	
}
