package katnote;

import java.util.ArrayList;

import katnote.command.CommandDetail;
import katnote.command.CommandType;
import katnote.command.Parser;

public class Logic {

    private Model model;
    
	// Error Messages
	private static final String MSG_ERR_INVALID_TYPE = "Invalid command type: %s"; //%s is the command type
	private static final String MSG_ERR_INVALID_ARGS = "Invalid arguments: %s"; // %s is the string of arguments
	
	
	// Constructor
	public Logic(){
		model = new Model("");
	}
	
	// Public methods 
	
	/**
	 * Takes in String command from GUI, parse and process the command and return feedback to GUI
	 * 
	 * @param command	String input from GUI command line interface
	 * @return	returns a UIFeedback object containing information for the GUI to display. 
	 */
	public UIFeedback execute(String command) {
		UIFeedback response;
		CommandDetail parsedTask = Parser.parseCommand(command);	
		response = process(parsedTask);		
		return response;
	}
	
	public UIFeedback process(CommandDetail commandDetail) {
		CommandType type = commandDetail.getCommandType();
		UIFeedback response = new UIFeedback();
			
		Task task;
        switch (type) {
			case ADD_NORMAL:
			    task = new Task(commandDetail); //TODO: constructor for task, para: cmdDetail
				response.setMessage(model.addTask(task));
				break;									
									
			case EDIT_COMPLETE : //pass cmdDetails (give Model the ID)
			    response.setMessage(model.editComplete(task));
				break;
									
			case EDIT_MODIFY : // create new Task object
			    task = new Task(commandDetail); // this new task will contain the fields that are modified, rest is null fields
			    response.setMessage(model.editModify(task));
				break;
			
			case EDIT_DELETE : // pass cmdDetails
			    response.setMessage(model.editDelete(task));
				break;
				
			case VIEW_TASK : 
				response.setTaskList(viewTask(commandDetail)); // returns ArrayList
				break;
									
			case UNDO:
			    response.setResponse(model.undoLast());
				break;
									
			case REDO:
			    response.setResponse(model.redo());
				break;
			
			case FIND:
				response.setTaskList(find(commandDetail)); // returns ArrayList
				break;
			
			case SET_LOCATION:
			    response.setResponse(model.setLocation(commandDetail));
				break;
			
			default :
			    response.setError(true);
				response.setResponse(String.format(MSG_ERR_INVALID_TYPE, type));
				break;
		}
		return response;
		
	}

    private ArrayList<Task> find(CommandDetail commandDetail) {
        // TODO Auto-generated method stub
        return null;
    }

    private ArrayList<Task> viewTask(CommandDetail commandDetail) {
        // TODO Auto-generated method stub
        return null;
    }
	
	
}
