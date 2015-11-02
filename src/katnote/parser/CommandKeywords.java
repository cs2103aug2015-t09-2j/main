package katnote.parser;

import java.util.Arrays;
import java.util.HashSet;

public class CommandKeywords {
    // keywords for command types
    public static final String KW_ADD = "add";
    public static final String KW_EDIT = "edit";
    public static final String KW_EDIT_TASK = "edit task";
    public static final String KW_MARK = "mark";
    public static final String KW_POSTPONE = "postpone";
    public static final String KW_DELETE = "delete";
    public static final String KW_DELETE_TASK = "delete task";
    public static final String KW_VIEW = "view";
    public static final String KW_VIEW_SINGLE_TASK = "view task";
    public static final String KW_VIEW_MULTIPLE_TASK = "view tasks";
    public static final String KW_FIND = "find";
    public static final String KW_UNDO = "undo";
    public static final String KW_REDO = "redo";
    public static final String KW_IMPORT = "import";
    public static final String KW_EXPORT = "export";
    public static final String KW_HELP = "help";
    public static final String KW_SET_LOCATION = "set location";
    public static final String KW_EXIT = "exit";

    // property keywords
    public static final String KW_SINGLE_TASK = "task";
    public static final String KW_TASKS = "tasks";
    public static final String KW_ALL = "all";
    public static final String KW_COMPLETED = "completed";
    public static final String KW_INCOMPLETED = "incompleted";

    // keywords for time properties
    public static final String KW_BY = "by";
    public static final String KW_FROM = "from";
    public static final String KW_TO = "to";
    public static final String KW_ON = "on";

    // keywords for view, add properties
    public static final String KW_SET = "set";
    

    public static final String[] START_KEYWORDS_LIST = new String[] {
            KW_ADD, KW_EDIT_TASK, KW_EDIT, KW_MARK, KW_POSTPONE,
            KW_DELETE_TASK, KW_DELETE,
            // KW_VIEW_MULTIPLE_TASK, KW_VIEW_SINGLE_TASK,
            KW_VIEW, KW_FIND,
            KW_UNDO, KW_REDO, KW_IMPORT, KW_EXPORT, KW_HELP, KW_SET_LOCATION, KW_EXIT
    };

    private static final String[] MAIN_KEYWORDS_LIST = new String[] {
            KW_BY, KW_FROM, KW_TO, KW_ON,
            KW_SET,
    };

    private static final HashSet<String> MAIN_KEYWORDS_SET = new HashSet<String>(Arrays.asList(MAIN_KEYWORDS_LIST));

    /*
     * Checks a token whether it is a main keyword or not
     */
    public static final boolean isMainKeyword(String token) {
        return MAIN_KEYWORDS_SET.contains(token.toLowerCase());
    }
}
