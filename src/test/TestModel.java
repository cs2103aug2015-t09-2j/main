package test;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import katnote.Model;
import katnote.command.CommandDetail;
import katnote.command.CommandProperties;
import katnote.task.Task;
import katnote.task.TaskType;

import org.junit.Test;

public class TestModel {
    
    // Constants
    private static final String DATA_FILENAME = "data.txt";
    private static final int MAX_BUFFER_SIZE = 1024;
    private static final String TEST_PATH = "TestFiles/TestModel/";
    private static final String TEST_PATH_ADDTASK = "TestFiles/TestModel/addTaskExpected.txt";
    
    // Messages
    private static final String MSG_ERR_IO = "I/O Exception.";
    private static final String MSG_ERR_MISSING_FILE = "Missing File: ";
    
    // Dates
    private static final LocalDateTime TODAY = LocalDateTime.now();
    private static final LocalDateTime TOMORROW = TODAY.plusDays(1);
    private static final LocalDateTime NEXTWEEK = TODAY.plusWeeks(1);
    
    // Format
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    
    /**
     * Compare the contents of both text files.
     * @param actual
     * @param expected
     * @return true if both files are the same in content and false if otherwise.
     * @throws Exception
     */
    public boolean compareFile(String actual, String expected) throws Exception {
        
        File actualFile = new File(actual);
        File expectedFile = new File(expected);
        
        if (!actualFile.exists()) {
            handleException(new FileNotFoundException(), MSG_ERR_MISSING_FILE + actual);
        }
        if (!expectedFile.exists()) {
            handleException(new FileNotFoundException(), MSG_ERR_MISSING_FILE + expected);
        }
        
        BufferedReader readerActual = new BufferedReader(new FileReader(actualFile));
        BufferedReader readerExpected = new BufferedReader(new FileReader(expectedFile));
        
        ArrayList<String> contentActual = new ArrayList<String>();
        ArrayList<String> contentExpected = new ArrayList<String>();
        
        String line;
        
        try {
            while ((line = readerActual.readLine()) != null) {
                contentActual.add(line);
            }
            
            while ((line = readerExpected.readLine()) != null) {
                contentExpected.add(line);
            }
        } catch (IOException e) {
            handleException(e, MSG_ERR_IO);
        }
        
        readerActual.close();
        readerExpected.close();
        
        if (contentActual.size() != contentExpected.size()) {
            System.err.println("Different content length!");
            return false;
        }
        
        for (int i=0; i<contentActual.size(); i++) {
            if(!contentActual.get(i).equals(contentExpected.get(i))) {
                System.err.println("Mismatch on line: " + (i+1));
                return false;
            }
        }
        
        return true;
    }
    
    private String handleException(Exception e, String msg) throws Exception {
        throw new Exception(e + " - " + msg);
    }
    
    private CommandDetail createTask(String title, TaskType taskType, LocalDateTime start, LocalDateTime end, LocalDateTime due) {
        
        CommandDetail cmd = new CommandDetail();
        if (!title.isEmpty()) {
            cmd.setProperty(CommandProperties.TASK_TITLE, title);
        }
        if (taskType != null) {
            cmd.setProperty(CommandProperties.TASK_TYPE, taskType);
        }
        if (start != null) {
            cmd.setProperty(CommandProperties.TIME_FROM, start);
        }
        if (end != null) {
            cmd.setProperty(CommandProperties.TIME_TO, end);
        }
        if (due != null) {
            cmd.setProperty(CommandProperties.TIME_BY, due);
        }
        return cmd;
    }
    
    // ACTUAL TESTS

//    @Test
//    public void testSetLocation() {
//        try {
//            String newPath = "TestFiles/NewTestModel/";
//            
//            Model testModel = new Model(TEST_PATH);
//            CommandDetail testCmd = new CommandDetail();
//            testCmd.setProperty(CommandProperties.LOCATION, newPath);
//            
//            testModel.setLocation(testCmd);
//            
//            File newFile = new File(newPath + DATA_FILENAME);
//            File oldFile = new File(TEST_PATH + DATA_FILENAME);
//            
//            System.out.println("New File exist: " + newFile.exists());
//            System.out.println("Old File exist: " + oldFile.exists());
//            assertTrue(newFile.exists());
//            assertFalse(oldFile.exists());
//            
//            // Clean up
//            if (newFile.exists()) {
//                newFile.delete();
//            }
//            
//        } catch (Exception e) {
//            System.out.println(e);
//            
//            String newPath = "TestFiles/NewTestModel/";
//            File newFile = new File(newPath + DATA_FILENAME);
//            File oldFile = new File(TEST_PATH + DATA_FILENAME);
//            // Clean up
//            if (newFile.exists()) {
//                newFile.delete();
//            }
//            if (oldFile.exists()) {
//                oldFile.delete();
//            }
//            
//            fail("Exception!");
//        }
//    }
    
    @Test
    public void testAddTask() {
        try {
            Model testModel = new Model(TEST_PATH);
            CommandDetail addTaskCmd;
            
            LocalDateTime date1 = LocalDateTime.of(2015, 10, 23, 23, 59);
            System.out.println("Date Field: " + date1.format(DATE_FORMATTER));
            
            addTaskCmd = createTask("feed cat", TaskType.NORMAL, null, null, TODAY);
            Task task1 = new Task(addTaskCmd);
            
            testModel.addTask(task1);
            
            assertTrue(compareFile(testModel.getDataFilePath(), TEST_PATH_ADDTASK));
            
            // Clean Up
            File testData = new File(testModel.getDataFilePath());
            if (testData.exists()) {
                testData.delete();
            }
            
        } catch (Exception e) {
            System.out.println(e);
            
            // Clean Up
            File testData = new File(TEST_PATH + DATA_FILENAME);
            if (testData.exists()) {
                testData.delete();
            }
            
            fail("Exception!");
        }
    }

//    @Test
//    public void testImportLocation() {
//
//    }
}
