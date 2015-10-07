package katnote.command;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {	
	private static final String COMMAND_SPLIT_PATTERN = "([^\"]\\S*|\".+?\")\\s*";
	private static final String DEFAULT_SPLIT_PATTERN = "\\s+";
	
	private static final int TOKENS_PROPERTIES_START_POS = 2;
	private static final int TOKENS_TASK_NAME_POS = 1;
	private static final int TOKENS_VIEW_TASK_OPTION_POS = 1;
	
	private static final int TASK_OPTION_VIEW_TYPE_POS = 0;
	private static final int TASK_OPTION_VIEW_DETAIL_POS = 1;

	private static final String COMMAND_ADD = "add";
	private static final String COMMAND_VIEW = "view";
	private static final String COMMAND_EDIT = "edit";
	private static final String COMMAND_FIND = "find";
	private static final String COMMAND_DELETE = "delete";
	
	private static final String PROPERTY_SINGLE_TASK = "task";
	private static final String PROPERTY_TASKS = "tasks";
	private static final String PROPERTY_COMPLETED = "completed";
	
	/*
	 * 
	 * @param commandStr the command you want to parse
	 */
	public static CommandDetail parseCommand(String commandStr){		
		List<String> tokens = getTokensFromCommand(commandStr);		
		if (tokens.isEmpty()){
			return new CommandDetail(CommandType.UNKNOWN);
		}		
		
		String commandType = tokens.get(0);
		switch (commandType){
			case COMMAND_ADD:
				return parseAddCommand(tokens);
			case COMMAND_VIEW:
			    return parseViewCommand(tokens);
			case COMMAND_FIND:
			    return parseFindCommand(tokens);
			case COMMAND_DELETE:
			    return parseDeleteCommand(tokens);
			case COMMAND_EDIT:
			    return parseEditCommand(tokens);
			default:
				return new CommandDetail(CommandType.UNKNOWN);
		}		
	}

	/*
	 * Parse Add command. Command format:
	 *     - add TASK_TITLE [by TIME_BY] [from TIME_FROM to TIME_TO] [repeat REPEAT_INTERVAL [until TIME_UNTIL]]
	 *         
	 * 
	 */
	private static CommandDetail parseAddCommand(List<String> tokens) {
		// TODO: support for other types of ADD
		CommandDetail command = new CommandDetail(CommandType.ADD_NORMAL);	
		try{
		    String taskTitle = tokens.get(TOKENS_TASK_NAME_POS);
	        command.setProperty(CommandProperties.TASK_TITLE, taskTitle);
            addCommandProperties(tokens, TOKENS_PROPERTIES_START_POS, command);
        }
        catch (Exception e){
            return new CommandDetail(CommandType.UNKNOWN);
        }
        return command;
	}
	
	/*
     * Parse view command. Command format:
     *     - view tasks [completed] [on TIME_ON] [from TIME_FROM to TIME_TO]
     *         
     * 
     */
    private static CommandDetail parseViewCommand(List<String> tokens) {
        CommandDetail command = new CommandDetail(CommandType.VIEW_TASK);               
        try{
            // Parse view options ("tasks", "tasks completed" or "task TASK_ID")
            String[] viewOptions = tokens.get(TOKENS_VIEW_TASK_OPTION_POS).split(DEFAULT_SPLIT_PATTERN);
            switch (viewOptions[TASK_OPTION_VIEW_TYPE_POS].toLowerCase()){
                case PROPERTY_SINGLE_TASK:
                    command.setCommandType(CommandType.VIEW_TASK_WITH_ID);
                    command.setProperty(CommandProperties.TASK_ID, viewOptions[TASK_OPTION_VIEW_DETAIL_POS]);
                    break;
                case PROPERTY_TASKS:
                    boolean viewCompleted = TASK_OPTION_VIEW_DETAIL_POS < viewOptions.length && viewOptions[TASK_OPTION_VIEW_DETAIL_POS].equals(PROPERTY_COMPLETED);
                    command.setProperty(CommandProperties.TASKS_COMPLETED_OPTION, viewCompleted);
                    break;
                default:
                    return new CommandDetail(CommandType.UNKNOWN);
            }
            //add time properties
            addCommandProperties(tokens, TOKENS_PROPERTIES_START_POS, command);
        }
        catch (Exception e){
            return new CommandDetail(CommandType.UNKNOWN);
        }
        return command;
    }
    
    /*
     * Parse find command. Command format:
     *     - find KEYWORDS [in CATEGORY]
     * 
     */
    private static CommandDetail parseFindCommand(List<String> tokens) {
        CommandDetail command = new CommandDetail(CommandType.FIND_TASKS);
        try{
            String keywords = tokens.get(TOKENS_TASK_NAME_POS);
            command.setProperty(CommandProperties.FIND_KEYWORDS, keywords);
            // add set or mark option
            addCommandProperties(tokens, TOKENS_PROPERTIES_START_POS, command);            
        }
        catch (Exception e){
            return new CommandDetail(CommandType.UNKNOWN);
        }
        return command;
    }
    
    /*
     * Parse edit command. Command format:
     *     - edit task TASK_ID set TASK_OPTION_NAME TASK_OPTION_VALUE
     *     - edit task TASK_ID mark completed
     * 
     */
    private static CommandDetail parseEditCommand(List<String> tokens) {
        CommandDetail command = new CommandDetail(CommandType.EDIT_TASK);               
        try{
            // read edit option ("task TASK_ID")
            readTaskIdFromTokens(tokens, command);
            // add set or mark option
            addCommandProperties(tokens, TOKENS_PROPERTIES_START_POS, command);
            
        }
        catch (Exception e){
            return new CommandDetail(CommandType.UNKNOWN);
        }
        return command;
    }
    
    /*
     * Parse delete command. Command format:
     *     - delete task TASK_ID
     * 
     */
    private static CommandDetail parseDeleteCommand(List<String> tokens) {
        CommandDetail command = new CommandDetail(CommandType.DELETE_TASK);               
        try{
            // read delete option ("task TASK_ID")
            readTaskIdFromTokens(tokens, command);
        }
        catch (Exception e){
            return new CommandDetail(CommandType.UNKNOWN);
        }
        return command;
    }
    

    private static void readTaskIdFromTokens(List<String> tokens, CommandDetail command) throws Exception {
        String[] viewOptions = tokens.get(TOKENS_VIEW_TASK_OPTION_POS).split(DEFAULT_SPLIT_PATTERN);
        switch (viewOptions[TASK_OPTION_VIEW_TYPE_POS].toLowerCase()){
            case PROPERTY_SINGLE_TASK:
                command.setProperty(CommandProperties.TASK_ID, viewOptions[TASK_OPTION_VIEW_DETAIL_POS]);
                break;
            default:
                throw new Exception();
        }
    }

    private static CommandDetail addCommandProperties(List<String> tokens, int pos, CommandDetail command) {
        while (pos < tokens.size()){
            String key = tokens.get(pos);
            pos++;
            String value = tokens.get(pos); 
            pos++;
            Date date;
            switch (key){
                case CommandProperties.TIME_BY:
                case CommandProperties.TIME_FROM:
                case CommandProperties.TIME_TO:
                case CommandProperties.TIME_UNTIL:                
                    //Date time value
                    date = DateParser.parseDate(value);
                    command.setProperty(key, date);
                    break;
                case CommandProperties.TIME_ON:
                    //TODO: take the begin of day to FROM and end of day to TO
                    date = DateParser.parseDate(value);
                    command.setProperty(key, date);
                    break;
                default:
                    //String value
                    command.setProperty(key, value);
                    break;
            }
        }        
        return command;
    }
	
	public static List<String> getTokensFromCommand(String commandStr){
		Matcher m = Pattern.compile(COMMAND_SPLIT_PATTERN).matcher(commandStr);
		List<String> tokens = new ArrayList<String>();
		StringBuilder currentToken = null;
		boolean isLastTokenKeyword = false;
		while (m.find()){
			String token = m.group(1);			
			boolean isKeyword = CommandKeywords.isKeyword(token);
			
			if (currentToken == null || isKeyword != isLastTokenKeyword){
				if (currentToken != null){
					tokens.add(currentToken.toString());
				}
				currentToken = new StringBuilder(token);
			}
			else{					
				currentToken.append(" " + token);
			}
			
			isLastTokenKeyword = isKeyword;
		}		
		if (currentToken != null){
			tokens.add(currentToken.toString());
		}
		
		return tokens;
	}
}
