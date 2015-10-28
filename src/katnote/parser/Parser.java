package katnote.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import katnote.KatNoteLogger;
import katnote.command.CommandDetail;
import katnote.command.CommandProperties;
import katnote.command.CommandType;
import katnote.task.TaskType;
import katnote.utils.StringUtils;

public class Parser {       
    private static final String COMMAND_SPLIT_PATTERN = "(?:([^\"]\\S*)|\"(.+?)\")\\s*";
    private static final int COMMAND_SPLIT_PATTERN_NORMAL_POS = 1;
    private static final int COMMAND_SPLIT_PATTERN_QUOTED_POS = 2;
    
    private static final String DEFAULT_SPLIT_PATTERN = "\\s+";
    private static final String IGNORE_CASES_PATTERN = "(?i)";
    private static final String STR_EMPTY = "";
    private static final String STR_SPACE = " ";

    private static final int TOKENS_PROPERTIES_START_POS = 1;
    private static final int TOKENS_TASK_NAME_POS = 0;
    private static final int TOKENS_OPTION_POS = 0;

    private static final int TASK_OPTION_VIEW_TYPE_POS = 0;
    private static final int TASK_OPTION_VIEW_DETAIL_POS = 1;
        

    // Class logger
    private static final Logger log = KatNoteLogger.getLogger(Parser.class.getName());    

    /*
     * Converts the input command string into CommandDetail format containing
     * command type as well as all data field related to that type of command.
     * 
     * @param commandStr the command you want to parse
     * 
     * @return CommandDetail object containing the command type and all data
     * fields related to that type of command
     */
    public static CommandDetail parseCommand(String commandStr) {
        log.info(String.format("Parse command: %1$s", commandStr));

        // determine command type
        StringBuilder truncatedCommand = new StringBuilder();
        String startKeyword = determineStartKeyword(commandStr, truncatedCommand);
        commandStr = truncatedCommand.toString();

        // split command
        List<String> tokens = getTokensFromCommand(commandStr);

        // parse command based on start keyword
        try {
            switch (startKeyword) {
                case CommandKeywords.KW_ADD:
                    return parseAddCommand(tokens);
                case CommandKeywords.KW_VIEW:
                    return parseViewCommand(tokens);
                case CommandKeywords.KW_FIND:
                    return parseFindCommand(tokens);
                case CommandKeywords.KW_DELETE:
                case CommandKeywords.KW_DELETE_TASK:
                    return parseDeleteCommand(tokens);
                case CommandKeywords.KW_MARK:
                case CommandKeywords.KW_MARK_TASK:
                    return parseMarkCommand(tokens);
                case CommandKeywords.KW_EDIT:
                case CommandKeywords.KW_EDIT_TASK:
                    return parseEditCommand(tokens);
                case CommandKeywords.KW_IMPORT:
                    return parseImportCommand(tokens);
                case CommandKeywords.KW_EXPORT:
                    return parseExportCommand(tokens);
                case CommandKeywords.KW_HELP:
                    return parseHelpCommand(tokens);
                case CommandKeywords.KW_SET_LOCATION:
                    return parseSetLocationCommand(tokens);
                case CommandKeywords.KW_UNDO:
                    return new CommandDetail(CommandType.UNDO);
                case CommandKeywords.KW_REDO :
                    return new CommandDetail(CommandType.REDO);
                case CommandKeywords.KW_EXIT :
                    return new CommandDetail(CommandType.EXIT);
                default :
                    return new CommandDetail(CommandType.UNKNOWN);
            }
        } catch (Exception e) {
            return new CommandDetail(CommandType.UNKNOWN);
        }
    }

    /*
     * Determines the start keyword of the command
     * 
     * @param truncatedCommand This StringBuilder object used to store the
     * command after truncated its start keyword
     * 
     * @return The start keyword of the command
     * 
     */
    public static String determineStartKeyword(String commandStr, StringBuilder truncatedCommand) {
        // Trim command before processing
        commandStr = commandStr.trim();        
        truncatedCommand.setLength(0);
        // check different starts of command
        String lowerCaseCommandStr = commandStr.trim().toLowerCase();
        for (String startKeyword : CommandKeywords.START_KEYWORDS_LIST) {
            if (lowerCaseCommandStr.startsWith(startKeyword)) {
                truncatedCommand.append(commandStr.replaceFirst(IGNORE_CASES_PATTERN + startKeyword, STR_EMPTY).trim());
                return startKeyword;
            }
        }
        // if no start found, assume the start is "add"
        truncatedCommand.append(commandStr);
        return CommandKeywords.KW_ADD;
    }

    /*
     * Splits the command string based on space but take quoted substrings as one
     * word
     * 
     * @param commandStr The command string passed from Logic
     * 
     * @return List of result tokens
     */
    public static List<String> getTokensFromCommand(String commandStr) {
        Matcher m = Pattern.compile(COMMAND_SPLIT_PATTERN).matcher(commandStr);
        List<String> tokens = new ArrayList<String>();
        StringBuilder currentToken = null;
        boolean isLastTokenKeyword = false;
        while (m.find()) {
            String token = StringUtils.concat(m.group(COMMAND_SPLIT_PATTERN_NORMAL_POS),
                    m.group(COMMAND_SPLIT_PATTERN_QUOTED_POS));
            boolean isKeyword = CommandKeywords.isMainKeyword(token);

            if (currentToken == null || isKeyword != isLastTokenKeyword) {
                if (currentToken != null) {
                    tokens.add(currentToken.toString());
                }
                currentToken = new StringBuilder(token);
            } else {
                currentToken.append(STR_SPACE + token);
            }

            isLastTokenKeyword = isKeyword;
        }
        if (currentToken != null) {
            tokens.add(currentToken.toString());
        }

        return tokens;
    }

    /*
     * Parses Add command. Command format:
     *   - add TASK_TITLE [by TIME_BY] [from TIME_FROM to TIME_TO]
     * 
     * 
     */
    private static CommandDetail parseAddCommand(List<String> tokens) throws Exception {
        CommandDetail command = new CommandDetail(CommandType.ADD_TASK);
        String taskTitle = tokens.get(TOKENS_TASK_NAME_POS);
        command.setProperty(CommandProperties.TASK_TITLE, taskTitle);
        addCommandProperties(tokens, TOKENS_PROPERTIES_START_POS, command);
        if (command.hasProperty(CommandProperties.TIME_BY)) {
            command.setProperty(CommandProperties.TASK_TYPE, TaskType.NORMAL);
        } else if (command.hasProperty(CommandProperties.TIME_FROM) && command.hasProperty(CommandProperties.TIME_TO)) {
            command.setProperty(CommandProperties.TASK_TYPE, TaskType.EVENT);
        } else {
            command.setProperty(CommandProperties.TASK_TYPE, TaskType.FLOATING);
        }
        return command;
    }

    /*
     * Parses view command. Command format:
     *   - view tasks [completed] [on TIME_ON] [from TIME_FROM to TIME_TO]
     *   - view task TASK_ID
     * 
     */
    private static CommandDetail parseViewCommand(List<String> tokens) throws Exception {
        CommandDetail command = new CommandDetail(CommandType.VIEW_TASK);
        // Parse view options ("tasks", "tasks completed" or "task TASK_ID")
        String[] viewOptions = tokens.get(TOKENS_OPTION_POS).split(DEFAULT_SPLIT_PATTERN);
        switch (viewOptions[TASK_OPTION_VIEW_TYPE_POS].toLowerCase()) {
            case CommandKeywords.KW_SINGLE_TASK :
                command.setCommandType(CommandType.VIEW_TASK_WITH_ID);
                Integer taskId = Integer.valueOf(viewOptions[TASK_OPTION_VIEW_DETAIL_POS]);
                command.setProperty(CommandProperties.TASK_ID, taskId);
                break;
            case CommandKeywords.KW_TASKS :                
                Boolean viewCompletedTask = false;
                if (TASK_OPTION_VIEW_DETAIL_POS < viewOptions.length) {
                    switch (viewOptions[TASK_OPTION_VIEW_DETAIL_POS]) {
                        case CommandKeywords.KW_COMPLETED :
                            viewCompletedTask = true;
                            break;
                        case CommandKeywords.KW_INCOMPLETED :
                            viewCompletedTask = false;
                            break;
                        case CommandKeywords.KW_ALL :
                            viewCompletedTask = null;
                            break;
                    }
                }
                command.setProperty(CommandProperties.TASKS_COMPLETED_OPTION, viewCompletedTask);
                break;
            default :
                return new CommandDetail(CommandType.UNKNOWN);
        }
        // add time properties
        addCommandProperties(tokens, TOKENS_PROPERTIES_START_POS, command);
        // view task option
        ViewTaskOption viewOption = ViewTaskOption.ALL;
        if (command.hasProperty(CommandProperties.TIME_BY)){
            viewOption = ViewTaskOption.DUE_BY;
        }
        else if (command.hasProperty(CommandProperties.TIME_FROM)){
            viewOption = ViewTaskOption.START_FROM;
        }
        command.setProperty(CommandProperties.TASKS_VIEW_OPTION, viewOption);
        return command;
    }

    /*
     * Parses find command. Command format:
     *   - find KEYWORDS [in CATEGORY]
     * 
     */
    private static CommandDetail parseFindCommand(List<String> tokens) throws Exception {
        CommandDetail command = new CommandDetail(CommandType.FIND_TASKS);
        String keywords = tokens.get(TOKENS_TASK_NAME_POS);
        command.setProperty(CommandProperties.FIND_KEYWORDS, keywords);
        // add set or mark option
        addCommandProperties(tokens, TOKENS_PROPERTIES_START_POS, command);
        return command;
    }

    /*
     * Parses edit command. Command format:
     *   - edit [task] TASK_ID TASK_OPTION_NAME TASK_OPTION_VALUE
     * 
     */
    private static CommandDetail parseEditCommand(List<String> tokens) throws Exception {
        CommandDetail command = new CommandDetail(CommandType.EDIT_MODIFY);
        // read task id
        Integer taskId = Integer.valueOf(tokens.get(TOKENS_OPTION_POS));
        command.setProperty(CommandProperties.TASK_ID, taskId);
        // add set option
        addCommandProperties(tokens, TOKENS_PROPERTIES_START_POS, command);
        return command;
    }
    
    /*
     * Parses mark command. Command format:
     *   - mark [task] TASK_ID completed/incompleted
     *   - mark [task] completed/incompleted TASK_ID
     */
    private static CommandDetail parseMarkCommand(List<String> tokens) throws Exception {
        CommandDetail command = new CommandDetail(CommandType.EDIT_COMPLETE);
        // read command option
        String commandOptions[] = tokens.get(TOKENS_OPTION_POS).split(DEFAULT_SPLIT_PATTERN);
        Integer taskId;
        String markOption;        
        if (StringUtils.isDigits(commandOptions[0])){ // mark TASK_ID completed
            taskId = Integer.valueOf(commandOptions[0]);
            markOption = commandOptions[1];
        }
        else{  // mark completed TASK_ID
            taskId = Integer.valueOf(commandOptions[1]);
            markOption = commandOptions[0];
        }
        
        command.setProperty(CommandProperties.TASK_ID, taskId);
        command.setProperty(CommandProperties.EDIT_MARK, markOption);
        return command;
    }

    /*
     * Parses delete command. Command format:
     *   - delete [task] TASK_ID
     * 
     */
    private static CommandDetail parseDeleteCommand(List<String> tokens) throws Exception {
        CommandDetail command = new CommandDetail(CommandType.DELETE_TASK);
        // read task id
        Integer taskId = Integer.valueOf(tokens.get(TOKENS_OPTION_POS));
        command.setProperty(CommandProperties.TASK_ID, taskId);
        return command;
    }

    /*
     * Parses import command. Command format:
     *   - import FILE_PATH
     * 
     */
    private static CommandDetail parseImportCommand(List<String> tokens) {
        CommandDetail command = new CommandDetail(CommandType.IMPORT);
        String filePath = tokens.get(TOKENS_OPTION_POS);
        command.setProperty(CommandProperties.FILE_PATH, filePath);
        return command;
    }

    /*
     * Parses export command. Command format:
     *   - export FILE_PATH
     * 
     */
    private static CommandDetail parseExportCommand(List<String> tokens) {
        CommandDetail command = new CommandDetail(CommandType.EXPORT);
        String filePath = tokens.get(TOKENS_OPTION_POS);
        command.setProperty(CommandProperties.FILE_PATH, filePath);
        return command;
    }
    
    /*
     * Parses set save location command. Command format:
     *   - set location FILE_PATH
     * 
     */
    private static CommandDetail parseSetLocationCommand(List<String> tokens) {
        CommandDetail command = new CommandDetail(CommandType.SET_LOCATION);
        String filePath = tokens.get(TOKENS_OPTION_POS);
        command.setProperty(CommandProperties.FILE_PATH, filePath);
        return command;
    }

    /*
     * Parses help command. Command format:
     *   - help
     *   - help COMMAND
     * 
     */
    private static CommandDetail parseHelpCommand(List<String> tokens) {
        CommandDetail command = new CommandDetail(CommandType.HELP);
        try {
            String filePath = tokens.get(TOKENS_OPTION_POS);
            command.setProperty(CommandProperties.MAIN_CONTENT, filePath);
        } catch (IndexOutOfBoundsException e) {
            // don't need to do nothing
        }
        return command;
    }

    /*
     * Adds command properties to command detail
     */
    private static CommandDetail addCommandProperties(List<String> tokens, int pos, CommandDetail command) {
        while (pos < tokens.size()) {
            String key = tokens.get(pos);
            pos++;
            String value = tokens.get(pos);
            pos++;
            PropertyParser.parseProperty(key, value, command);
        }
        return command;
    }

}
