package test;

import static org.junit.Assert.*;

import org.junit.Test;

import katnote.command.CommandDetail;
import katnote.command.CommandType;
import katnote.parser.Parser;

public class TestParser {

    @Test
    public void testDetermineStartKeyword() {
        StringBuilder truncatedCommand = new StringBuilder();
        String keyword = Parser.determineStartKeyword("Add hello by tuesday", truncatedCommand);
        assertEquals("add", keyword);
        assertEquals("hello by tuesday", truncatedCommand.toString());
    }

    @Test
    public void testAddCommand1() {
        CommandDetail commandDetail = Parser.parseCommand("ADD hello by tuesday");
        assertEquals(CommandType.ADD_TASK, commandDetail.getCommandType());
        assertEquals("hello", commandDetail.getTitle());
        assertNotNull(commandDetail.getDueDate());
    }
    
    @Test
    public void testAddCommand2() {
        CommandDetail commandDetail = Parser.parseCommand("hello by today");
        assertEquals(CommandType.ADD_TASK, commandDetail.getCommandType());
        assertEquals("hello", commandDetail.getTitle());
        assertNotNull(commandDetail.getDueDate());
        //System.out.println("Due Date = " + commandDetail.getDueDate().toString());
    }
    
    @Test
    public void testAddCommand3() {
        CommandDetail commandDetail = Parser.parseCommand("hello from tomorrow 2pm to 5pm");
        assertEquals(CommandType.ADD_TASK, commandDetail.getCommandType());
        assertEquals("hello", commandDetail.getTitle());
        assertNotNull(commandDetail.getStartDate());
        assertNotNull(commandDetail.getEndDate());
        //System.out.println("Start Date = " + commandDetail.getStartDate().toString());
        //System.out.println("End Date = " + commandDetail.getEndDate().toString());
    }

    @Test
    public void testEditCommand1() {
        CommandDetail commandDetail = Parser.parseCommand("EDit task 4 task_title hello");
        assertEquals(CommandType.EDIT_MODIFY, commandDetail.getCommandType());
        assertEquals(4, commandDetail.getTaskIndex());
        assertEquals("task_title", commandDetail.getEditTaskOption().getOptionName());
        assertEquals("hello", commandDetail.getEditTaskOption().getOptionValue());
    }

    @Test
    public void testEditCommand2() {
        CommandDetail commandDetail = Parser.parseCommand("edit 4 task_title hello");
        assertEquals(CommandType.EDIT_MODIFY, commandDetail.getCommandType());
        assertEquals(4, commandDetail.getTaskIndex());
        assertEquals("task_title", commandDetail.getEditTaskOption().getOptionName());
        assertEquals("hello", commandDetail.getEditTaskOption().getOptionValue());
    }
    
    @Test
    public void testMarkCommand1() {
        CommandDetail commandDetail = Parser.parseCommand("mark 4 completed");
        assertEquals(CommandType.EDIT_COMPLETE, commandDetail.getCommandType());
        assertEquals(4, commandDetail.getTaskIndex());
        assertEquals("completed", commandDetail.getMarkOption());
    }

    @Test
    public void testDeleteCommand1() {
        CommandDetail commandDetail = Parser.parseCommand("delete 4");
        assertEquals(CommandType.DELETE_TASK, commandDetail.getCommandType());
        assertEquals(4, commandDetail.getTaskIndex());
    }

    @Test
    public void testDeleteCommand2() {
        CommandDetail commandDetail = Parser.parseCommand("delete task 4");
        assertEquals(CommandType.DELETE_TASK, commandDetail.getCommandType());
        assertEquals(4, commandDetail.getTaskIndex());
    }

    @Test
    public void testViewCommand1() { // View multiple task
        CommandDetail commandDetail = Parser.parseCommand("view tasks on monday");
        assertEquals(CommandType.VIEW_TASK, commandDetail.getCommandType());
        assertNotNull(commandDetail.getStartDate());
        assertNotNull(commandDetail.getEndDate());
    }

    @Test
    public void testViewCommand2() { // View single task
        CommandDetail commandDetail = Parser.parseCommand("view task 3");
        assertEquals(CommandType.VIEW_TASK_WITH_ID, commandDetail.getCommandType());
        assertEquals(3, commandDetail.getTaskIndex());
    }

    @Test
    public void testOtherCommand() {
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
    }

}
