package katnote.command;

public enum CommandType {
	// ADD
	ADD_NORMAL,

	// READ
	VIEW_TASK,
	VIEW_TASK_WITH_ID,
	FIND_TASKS,

	// UPDATE
	EDIT_MODIFY,
	EDIT_COMPLETE,

	// DELETE
	DELETE_TASK,

	// Others
	UNDO,
	REDO,
	IMPORT,
	EXPORT,
	HELP,
	SET_LOCATION,
	EXIT,
	
	// Unknown or invalid command
	UNKNOWN,
}
