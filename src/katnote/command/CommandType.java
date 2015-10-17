package katnote.command;

public enum CommandType {
	// ADD
	ADD_NORMAL,
	ADD_FLOATING,
	ADD_EVENT,
	ADD_RECURRING,

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
	SET_LOCATION,
	
	// Unknown or invalid command
	UNKNOWN, 
}
