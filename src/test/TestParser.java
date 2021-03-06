//@@author A0126517H
package test;

import static org.junit.Assert.*;

import org.junit.Test;

import katnote.command.CommandDetail;
import katnote.command.CommandType;
import katnote.parser.CommandParseException;
import katnote.parser.Parser;
import katnote.parser.ViewTaskOption;

public class TestParser {

    @Test
    public void testDetermineStartKeyword() {
        StringBuilder truncatedCommand = new StringBuilder();
        String keyword = Parser.determineStartKeyword("Add hello by tuesday", truncatedCommand);
        assertEquals("add", keyword);
        assertEquals("hello by tuesday", truncatedCommand.toString());
    }

    @Test
    public void testAddCommand1() throws CommandParseException {
        CommandDetail commandDetail = Parser.parseCommand("ADD hello by tuesday");
        assertEquals(CommandType.ADD_TASK, commandDetail.getCommandType());
        assertEquals("hello", commandDetail.getTitle());
        assertNotNull(commandDetail.getDueDate());
    }

    @Test
    public void testAddCommand2() throws CommandParseException {
        CommandDetail commandDetail = Parser.parseCommand("-a hello on 29/11 8pm");
        assertEquals(CommandType.ADD_TASK, commandDetail.getCommandType());
        assertEquals("hello", commandDetail.getTitle());
        assertEquals("2015-11-29T20:00", commandDetail.getStartDate().toString());
        assertEquals("2015-11-29T20:00", commandDetail.getEndDate().toString());
    }

    @Test
    public void testAddCommand3() throws CommandParseException {
        CommandDetail commandDetail = Parser.parseCommand("hello by today");
        assertEquals(CommandType.ADD_TASK, commandDetail.getCommandType());
        assertEquals("hello", commandDetail.getTitle());
        assertNotNull(commandDetail.getDueDate());
        // System.out.println("Due Date = " +
        // commandDetail.getDueDate().toString());
    }

    @Test
    public void testAddCommand4() throws CommandParseException {
        CommandDetail commandDetail = Parser.parseCommand("hello from tomorrow 2pm to 5pm");
        assertEquals(CommandType.ADD_TASK, commandDetail.getCommandType());
        assertEquals("hello", commandDetail.getTitle());
        assertNotNull(commandDetail.getStartDate());
        assertNotNull(commandDetail.getEndDate());
        // System.out.println("Start Date = " +
        // commandDetail.getStartDate().toString());
        // System.out.println("End Date = " +
        // commandDetail.getEndDate().toString());
    }

    @Test
    public void testEditCommand1() throws CommandParseException {
        CommandDetail commandDetail = Parser.parseCommand("EDit 4 title hello");
        assertEquals(CommandType.EDIT_MODIFY, commandDetail.getCommandType());
        assertEquals(4, commandDetail.getTaskIndex());
        assertEquals("title", commandDetail.getEditTaskOption().getOptionName());
        assertEquals("hello", commandDetail.getEditTaskOption().getOptionValue());
    }

    @Test
    public void testEditCommand2() throws CommandParseException {
        CommandDetail commandDetail = Parser.parseCommand("-e 4 title hello");
        assertEquals(CommandType.EDIT_MODIFY, commandDetail.getCommandType());
        assertEquals(4, commandDetail.getTaskIndex());
        assertEquals("title", commandDetail.getEditTaskOption().getOptionName());
        assertEquals("hello", commandDetail.getEditTaskOption().getOptionValue());
    }

    @Test
    public void testMarkCommand1() throws CommandParseException {
        CommandDetail commandDetail = Parser.parseCommand("mark 4 completed");
        assertEquals(CommandType.EDIT_COMPLETE, commandDetail.getCommandType());
        assertEquals(4, commandDetail.getTaskIndex());
        // assertEquals("completed", commandDetail.getMarkOption());
    }

    @Test
    public void testMarkCommand2() throws CommandParseException {
        CommandDetail commandDetail = Parser.parseCommand("-m 4 completed");
        assertEquals(CommandType.EDIT_COMPLETE, commandDetail.getCommandType());
        assertEquals(4, commandDetail.getTaskIndex());
        assertEquals(true, commandDetail.getTaskCompletedOption());
    }

    @Test
    public void testPostponeCommand1() throws CommandParseException {
        CommandDetail commandDetail = Parser.parseCommand("postpone 4 5/11");
        assertEquals(CommandType.POSTPONE, commandDetail.getCommandType());
        assertEquals(4, commandDetail.getTaskIndex());
        assertEquals("2015-11-05", commandDetail.getStartDate().toString());
    }

    @Test
    public void testPostponeCommand2() throws CommandParseException {
        CommandDetail commandDetail = Parser.parseCommand("-p 4 to 5/11");
        assertEquals(CommandType.POSTPONE, commandDetail.getCommandType());
        assertEquals(4, commandDetail.getTaskIndex());
        assertEquals("2015-11-05", commandDetail.getStartDate().toString());
    }

    @Test
    public void testDeleteCommand1() throws CommandParseException {
        CommandDetail commandDetail = Parser.parseCommand("delete 4");
        assertEquals(CommandType.DELETE_TASK, commandDetail.getCommandType());
        assertEquals(4, commandDetail.getTaskIndex());
    }

    @Test
    public void testDeleteCommand2() throws CommandParseException {
        CommandDetail commandDetail = Parser.parseCommand("-d 4");
        assertEquals(CommandType.DELETE_TASK, commandDetail.getCommandType());
        assertEquals(4, commandDetail.getTaskIndex());
    }

    @Test
    public void testViewMultipleTasksCommand1() throws CommandParseException { // View
                                                                               // multiple
                                                                               // task
        CommandDetail commandDetail = Parser.parseCommand("view on monday");
        assertEquals(CommandType.VIEW_TASK, commandDetail.getCommandType());
        assertEquals(ViewTaskOption.START_FROM, commandDetail.getViewTaskOption());
        assertNotNull(commandDetail.getStartDate());
        assertNotNull(commandDetail.getEndDate());
    }

    @Test
    public void testViewMultipleTasksCommand2() throws CommandParseException { // View
                                                                               // multiple
                                                                               // task
        CommandDetail commandDetail = Parser.parseCommand("view tasks completed from tuesday 2pm to 5pm");
        assertEquals(CommandType.VIEW_TASK, commandDetail.getCommandType());
        assertEquals(ViewTaskOption.START_FROM, commandDetail.getViewTaskOption());
        assertNotNull(commandDetail.getStartDate());
        assertNotNull(commandDetail.getEndDate());
    }

    @Test
    public void testViewMultipleTasksCommand3() throws CommandParseException { // View
                                                                               // multiple
                                                                               // task
        CommandDetail commandDetail = Parser.parseCommand("-v completed by tuesday 2pm");
        assertEquals(CommandType.VIEW_TASK, commandDetail.getCommandType());
        assertEquals(ViewTaskOption.DUE_BY, commandDetail.getViewTaskOption());
        assertNull(commandDetail.getStartDate());
        assertNotNull(commandDetail.getDueDate());
    }

    @Test
    public void testViewMultipleTasksCommand4() throws CommandParseException { // View
                                                                               // multiple
                                                                               // task
        CommandDetail commandDetail = Parser.parseCommand("view all");
        assertEquals(CommandType.VIEW_TASK, commandDetail.getCommandType());
        assertEquals(ViewTaskOption.ALL, commandDetail.getViewTaskOption());
        assertNull(commandDetail.getStartDate());
        assertNull(commandDetail.getEndDate());
    }

    @Test
    public void testViewSingleTaskCommand1() throws CommandParseException { // View
                                                                            // single
                                                                            // task
        CommandDetail commandDetail = Parser.parseCommand("view task 3");
        assertEquals(CommandType.VIEW_TASK_WITH_ID, commandDetail.getCommandType());
        assertEquals(3, commandDetail.getTaskIndex());
    }

    @Test
    public void testOtherCommand() throws CommandParseException {
        // undo
        CommandDetail undoCommand = Parser.parseCommand("undo");
        assertEquals(CommandType.UNDO, undoCommand.getCommandType());
        // redo
        CommandDetail redoCommand = Parser.parseCommand("redo");
        assertEquals(CommandType.REDO, redoCommand.getCommandType());
        // help
        CommandDetail helpCommand1 = Parser.parseCommand("help");
        assertEquals(CommandType.HELP, helpCommand1.getCommandType());
        // help
        CommandDetail helpCommand2 = Parser.parseCommand("help add");
        assertEquals(CommandType.HELP, helpCommand2.getCommandType());
        assertEquals("add", helpCommand2.getMainContent());
        // import
        CommandDetail importCommand = Parser.parseCommand("import D:\blabla");
        assertEquals(CommandType.IMPORT, importCommand.getCommandType());
        assertEquals("D:\blabla", importCommand.getFilePath());
        // export
        CommandDetail exportCommand = Parser.parseCommand("export D:\blabla");
        assertEquals(CommandType.EXPORT, exportCommand.getCommandType());
        assertEquals("D:\blabla", exportCommand.getFilePath());
        // set location
        CommandDetail setLocationCommand = Parser.parseCommand("set location D:\blabla");
        assertEquals(CommandType.SET_LOCATION, setLocationCommand.getCommandType());
        assertEquals("D:\blabla", setLocationCommand.getFilePath());
    }

}
