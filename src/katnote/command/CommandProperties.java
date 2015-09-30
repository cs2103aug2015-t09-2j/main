package katnote.command;

public class CommandProperties {
	public static final String TASK_TITLE = "task_title";
	public static final String TASK_ID = "task_id";
	
	public static final String TIME_BY = "by";
	public static final String TIME_TO = "to";
	public static final String TIME_FROM = "from";
	public static final String TIME_REPEAT = "repeat";
	public static final String TIME_UNTIL = "until";
	
	public static final String SAVE_LOCATION = "save_location";
	
	// TODO: Command Types

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
}
