package katnote.command;

public enum CommandType {
	// ADD
	ADD_NORMAL,
	ADD_FLOATING,
	ADD_EVENT,
	ADD_RECURRING,

	// READ
	VIEW_TASK,
	FIND,

	// UPDATE
	EDIT_COMPLETE,
	EDIT_MODIFY,

	// DELETE
	EDIT_DELETE,

	// Others
	UNDO,
	REDO,
	SET_LOCATION
}
