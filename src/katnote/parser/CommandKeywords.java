//@@author A0126517H
package katnote.parser;

import java.util.Arrays;
import java.util.HashSet;

public class CommandKeywords {
    // keywords for command types
    public static final String KW_ADD = "add";
    public static final String KW_ADD_SHORT = "-a";
    public static final String KW_EDIT = "edit";
    public static final String KW_EDIT_SHORT = "-e";
    public static final String KW_CHANGE = "change";
    public static final String KW_MARK = "mark";
    public static final String KW_MARK_SHORT = "-m";
    public static final String KW_POSTPONE = "postpone";
    public static final String KW_POSTPONE_SHORT = "-p";
    public static final String KW_DELETE = "delete";
    public static final String KW_DELETE_SHORT = "-d";
    public static final String KW_VIEW = "view";
    public static final String KW_VIEW_SINGLE_TASK = "view task";
    public static final String KW_VIEW_MULTIPLE_TASK = "view tasks";
    public static final String KW_VIEW_MULTIPLE_TASK_SHORT = "-v";
    public static final String KW_FIND = "find";
    public static final String KW_UNDO = "undo";
    public static final String KW_REDO = "redo";
    public static final String KW_IMPORT = "import";
    public static final String KW_EXPORT = "export";
    public static final String KW_HELP = "help";
    public static final String KW_SET_LOCATION = "set location";
    public static final String KW_EXIT = "exit";

    // keywords for command properties
    public static final String KW_SINGLE_TASK = "task";
    public static final String KW_TASKS = "tasks";
    public static final String KW_ALL = "all";
    public static final String KW_COMPLETED = "completed";
    public static final String KW_DONE = "done";
    public static final String KW_INCOMPLETE = "incomplete";

    // keywords for time properties
    public static final String KW_BY = "by";
    public static final String KW_FROM = "from";
    public static final String KW_TO = "to";
    public static final String KW_ON = "on";
    public static final String KW_AT = "at";

    // keywords for view, add properties
    public static final String KW_SET = "set";

    public static final String[] START_KEYWORDS_LIST = new String[] { KW_ADD, KW_ADD_SHORT, KW_MARK,
            KW_MARK_SHORT, KW_POSTPONE, KW_POSTPONE_SHORT, KW_EDIT, KW_EDIT_SHORT, KW_CHANGE, KW_DELETE,
            KW_DELETE_SHORT, KW_VIEW_MULTIPLE_TASK, KW_VIEW_SINGLE_TASK, KW_VIEW, KW_VIEW_MULTIPLE_TASK_SHORT,
            KW_FIND, KW_UNDO, KW_REDO, KW_IMPORT, KW_EXPORT, KW_HELP, KW_SET_LOCATION, KW_EXIT };

    private static final String[] MAIN_KEYWORDS_LIST = new String[] {
            KW_BY, KW_FROM, KW_TO, KW_ON, KW_AT,
    };

    private static final HashSet<String> MAIN_KEYWORDS_SET = new HashSet<String>(
            Arrays.asList(MAIN_KEYWORDS_LIST));

    /**
     * Checks a token whether it is a main keyword or not
     * 
     * @param token
     * @return true if token is in main keywords list, otherwise false
     */
    public static final boolean isMainKeyword(String token) {
        return MAIN_KEYWORDS_SET.contains(token.toLowerCase());
    }
}
