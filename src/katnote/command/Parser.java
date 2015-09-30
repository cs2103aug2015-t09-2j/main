package katnote.command;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {	
	private static final String COMMAND_SPLIT_PATTERN = "([^\"]\\S*|\".+?\")\\s*";
	
	private static final int TOKENS_PROPERTIES_START_POS = 2;
	private static final int TOKENS_TASK_NAME_POS = 1;

	private static final String COMMAND_ADD = "add";
	
	/*
	 * 
	 * @param commandStr the command you want to parse
	 */
	public static CommandDetail parseCommand(String commandStr){		
		List<String> tokens = getTokensFromCommand(commandStr);		
		if (tokens.isEmpty()){
			return null;
		}
		
		String commandType = tokens.get(0);
		switch (commandType){
			case COMMAND_ADD:
				return parseAddCommand(tokens);
			default:
				return null;
		}		
	}

	private static CommandDetail parseAddCommand(List<String> tokens) {
		CommandDetail command = new CommandDetail(CommandType.ADD);
		command.setProperty(CommandConstants.TASK_NAME, tokens.get(TOKENS_TASK_NAME_POS));
		int pos = TOKENS_PROPERTIES_START_POS;
		try{
			while (pos < tokens.size()){
				String key = tokens.get(pos);
				pos++;
				String value = tokens.get(pos);	
				pos++;
				command.setProperty(key, value);			
			}
		}
		catch (IndexOutOfBoundsException e){
			return null;
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
			
			if (currentToken == null || isKeyword == isLastTokenKeyword){
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
