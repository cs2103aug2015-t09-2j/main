package katnote.command;

public class CommandConstants {
	public static final String TASK_NAME = "task_name";
	public static final String TIME_BY = "by";
	public static final String TIME_TO = "to";
	public static final String TIME_FROM = "from";
	public static final String TIME_REPEAT = "repeat";
	public static final String TIME_UNTIL = "until";
	
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
