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

public class Parser {
    private static final String COMMAND_SPLIT_PATTERN = "([^\"]\\S*|\".+?\")\\s*";
    private static final String DEFAULT_SPLIT_PATTERN = "\\s+";

    private static final int TOKENS_PROPERTIES_START_POS = 1;
    private static final int TOKENS_TASK_NAME_POS = 0;
    private static final int TOKENS_OPTION_POS = 0;

    private static final int TASK_OPTION_VIEW_TYPE_POS = 0;
    private static final int TASK_OPTION_VIEW_DETAIL_POS = 1;

    // Class logger
    private static final Logger log = KatNoteLogger.getLogger(Parser.class.getName());

    /*
     * Convert the input command string into CommandDetail format containing
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
                case CommandKeywords.KW_ADD :
                    return parseAddCommand(tokens);
                case CommandKeywords.KW_VIEW :
                    return parseViewCommand(tokens);
                case CommandKeywords.KW_FIND :
                    return parseFindCommand(tokens);
                case CommandKeywords.KW_DELETE :
                case CommandKeywords.KW_DELETE_TASK :
                    return parseDeleteCommand(tokens);
                case CommandKeywords.KW_EDIT :
                case CommandKeywords.KW_EDIT_TASK :
                    return parseEditCommand(tokens);
                case CommandKeywords.KW_IMPORT :
                    return parseImportCommand(tokens);
                case CommandKeywords.KW_EXPORT :
                    return parseExportCommand(tokens);
                case CommandKeywords.KW_HELP :
                    return parseHelpCommand(tokens);
                case CommandKeywords.KW_UNDO :
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
     * Determine the start keyword of the command
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
        for (String startKeyword : CommandKeywords.START_KEYWORDS_LIST) {
            if (commandStr.startsWith(startKeyword)) {
                truncatedCommand.append(commandStr.replaceFirst(startKeyword, "").trim());
                return startKeyword;
            }
        }
        // if no start found, assume the start is "add"
        truncatedCommand.append(commandStr);
        return CommandKeywords.KW_ADD;
    }

    /*
     * Split the command string based on space but take quoted substrings as one
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
            String token = m.group(1).replace("\"", "");
            boolean isKeyword = CommandKeywords.isMainKeyword(token);

            if (currentToken == null || isKeyword != isLastTokenKeyword) {
                if (currentToken != null) {
                    tokens.add(currentToken.toString());
                }
                currentToken = new StringBuilder(token);
            } else {
                currentToken.append(" " + token);
            }

            isLastTokenKeyword = isKeyword;
        }
        if (currentToken != null) {
            tokens.add(currentToken.toString());
        }

        return tokens;
    }

    /*
     * Parse Add command. Command format: - add TASK_TITLE [by TIME_BY] [from
     * TIME_FROM to TIME_TO] [repeat REPEAT_INTERVAL [until TIME_UNTIL]]
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
     * Parse view command. Command format: - view tasks [completed] [on TIME_ON]
     * [from TIME_FROM to TIME_TO] - view task TASK_ID
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
                ViewTaskOption viewOption = ViewTaskOption.COMPLETED;
                if (TASK_OPTION_VIEW_DETAIL_POS < viewOptions.length) {
                    switch (viewOptions[TASK_OPTION_VIEW_DETAIL_POS]) {
                        case CommandKeywords.KW_COMPLETED :
                            viewOption = ViewTaskOption.COMPLETED;
                            break;
                        case CommandKeywords.KW_INCOMPLETED :
                            viewOption = ViewTaskOption.INCOMPLETED;
                            break;
                        case CommandKeywords.KW_ALL :
                            viewOption = ViewTaskOption.ALL;
                            break;
                    }
                }
                command.setProperty(CommandProperties.TASKS_VIEW_OPTION, viewOption);
                break;
            default :
                return new CommandDetail(CommandType.UNKNOWN);
        }
        // add time properties
        addCommandProperties(tokens, TOKENS_PROPERTIES_START_POS, command);
        return command;
    }

    /*
     * Parse find command. Command format: - find KEYWORDS [in CATEGORY]
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
     * Parse edit command. Command format: - edit [task] TASK_ID set
     * TASK_OPTION_NAME TASK_OPTION_VALUE - edit [task] TASK_ID mark completed
     * 
     */
    private static CommandDetail parseEditCommand(List<String> tokens) throws Exception {
        CommandDetail command = new CommandDetail(CommandType.EDIT_MODIFY);
        // read task id
        Integer taskId = Integer.valueOf(tokens.get(TOKENS_OPTION_POS));
        command.setProperty(CommandProperties.TASK_ID, taskId);
        // add set or mark option
        addCommandProperties(tokens, TOKENS_PROPERTIES_START_POS, command);
        if (command.hasProperty(CommandProperties.EDIT_MARK)) {
            command.setCommandType(CommandType.EDIT_COMPLETE);
        }
        return command;
    }

    /*
     * Parse delete command. Command format: - delete [task] TASK_ID
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
     * Parse import command. Command format: - import FILE_PATH
     * 
     */
    private static CommandDetail parseImportCommand(List<String> tokens) {
        CommandDetail command = new CommandDetail(CommandType.IMPORT);
        String filePath = tokens.get(TOKENS_OPTION_POS);
        command.setProperty(CommandProperties.FILE_PATH, filePath);
        return command;
    }

    /*
     * Parse export command. Command format: - export FILE_PATH
     * 
     */
    private static CommandDetail parseExportCommand(List<String> tokens) {
        CommandDetail command = new CommandDetail(CommandType.EXPORT);
        String filePath = tokens.get(TOKENS_OPTION_POS);
        command.setProperty(CommandProperties.FILE_PATH, filePath);
        return command;
    }

    /*
     * Parse help command. Command format: - help - help COMMAND
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
     * add command properties to command detail
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
