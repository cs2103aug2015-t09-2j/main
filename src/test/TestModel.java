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
    
    /**
     * Usage: (title, taskType, start, end, due)
     * @param title
     * @param taskType
     * @param start
     * @param end
     * @param due
     * @return the task object.
     */
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
    
    private void clearExistingData(String path) {
        
        File clearData = new File(path + DATA_FILENAME);
        if (clearData.exists()) {
            clearData.delete();
        }
    }
    
    // ACTUAL TESTS

    @Test
    public void testSetLocation() {
        try {
            clearExistingData(TEST_PATH);
            
            System.out.println("=== Set Save Location ===");
            String newPath = "TestFiles/NewTestModel/";
            
            Model testModel = new Model(TEST_PATH);
            CommandDetail testCmd = new CommandDetail();
            testCmd.setProperty(CommandProperties.LOCATION, newPath);
            
            testModel.setLocation(testCmd);
            
            File newFile = new File(newPath + DATA_FILENAME);
            File oldFile = new File(TEST_PATH + DATA_FILENAME);
            
            System.out.println("Result 1 - New File exist: " + newFile.exists() + " Expected: true");
            System.out.println("Result 2 - Old File exist: " + oldFile.exists() + " Expected: false");
            boolean result1 = newFile.exists();
            boolean result2 = oldFile.exists();
            
            // Clean up
            if (newFile.exists()) {
                newFile.delete();
            }
            System.out.println("=== End Test ===\n");
            
            assertTrue(result1);
            assertFalse(result2);
            
        } catch (Exception e) {
            System.out.println(e);            
            fail("Exception!");
        }
    }
    
    @Test
    public void testAddTask() {
        try {
            clearExistingData(TEST_PATH);
            
            System.out.println("=== Add Task ===");
            Model testModel = new Model(TEST_PATH);
            CommandDetail addTaskCmd;
            
            LocalDateTime date1 = LocalDateTime.of(2015, 10, 23, 23, 59);
            //System.out.println("Date Field: " + date1.format(DATE_FORMATTER));
            
            addTaskCmd = createTask("feed cat", TaskType.NORMAL, null, null, date1);
            Task task1 = new Task(addTaskCmd);
            
            testModel.addTask(task1);
            
            boolean result = compareFile(testModel.getDataFilePath(), TEST_PATH_ADDTASK);
            
            // Clean Up
            clearExistingData(TEST_PATH);
            System.out.println("Result 1 - Task Added Correctly: " + result + " Expected: true");
            System.out.println("=== End Test ===\n");
            
            assertTrue(result);
            
        } catch (Exception e) {
            System.out.println(e);           
            fail("Exception!");
        }
    }

    @Test
    public void testImportLocation() {
        try {
            clearExistingData(TEST_PATH);
            
            System.out.println("=== Import ===");
            Model testModel = new Model(TEST_PATH);
            CommandDetail importCmd = new CommandDetail();
            boolean result1 = false;
            String importPath1 = "TestFiles/ImportLoc/";
            importCmd.setProperty(CommandProperties.FILE_PATH, importPath1);
            
            testModel.importData(importCmd);
            
            File fileToImport = new File(importPath1 + DATA_FILENAME);
            File importedFile = new File(TEST_PATH + DATA_FILENAME);
            
            System.out.println("FileToImport exist: " + fileToImport.exists());
            System.out.println("ImportedFile exist: " + importedFile.exists());
            
            if (fileToImport.exists() && importedFile.exists()) {
                result1 = compareFile(fileToImport.getPath(), importedFile.getPath());
            }
            
            clearExistingData(TEST_PATH);
            
            boolean result2 = false;
            String importPath2 = "TestFiles/ImportLocBlank/";
            importCmd.setProperty(CommandProperties.FILE_PATH, importPath2);
            
            String response = testModel.importData(importCmd);
            String expected = "Encountered Error: Unable to find data.txt in specified import location.";
            result2 = response.equals(expected);

            System.out.println("Result 1 - File imported correctly: " + result1 + " Expected: true");
            System.out.println("Result 2 - Blank File not imported: " + result2 + " Expected: true");
            System.out.println("=== End Test ===\n");
            
            clearExistingData(TEST_PATH);
            
            assertTrue(result1);
            assertTrue(result2);

        } catch (Exception e) {
            System.out.println(e);           
            fail("Exception!");
        }

    }
    
    @Test
    public void testDeleteTask() {
        try {
            clearExistingData(TEST_PATH);
            
            System.out.println("=== Delete Task ===");
            Model testModel = new Model(TEST_PATH);
            LocalDateTime date1 = LocalDateTime.of(2015, 11, 26, 23, 59, 59);
            LocalDateTime date2 = LocalDateTime.of(2015, 12, 23, 23, 59, 59);
            CommandDetail addTaskCmd1 = createTask("feed cat", TaskType.NORMAL, null, null, date1);
            CommandDetail addTaskCmd2 = createTask("feed fish", TaskType.NORMAL, null, null, date2);
            CommandDetail addTaskCmd3 = createTask("feed dog", TaskType.FLOATING, null, null, null);
            Task task1 = new Task(addTaskCmd1);
            Task task2 = new Task(addTaskCmd2);
            Task task3 = new Task(addTaskCmd3);
            String response1;
            String response2;
            String response3;
            
            testModel.addTask(task1);
            testModel.addTask(task2);
            testModel.addTask(task3);
            
            boolean result1 = false;
            boolean result2 = false;
            boolean result3 = false;
            
            response1 = testModel.editDelete(1);
            result1 = response1.equals("Task: feed fish is successfully deleted.") && (testModel.getData().size() == 2);
            
            response2 = testModel.editDelete(1);
            try {
                response3 = testModel.editDelete(1);
            } catch (IndexOutOfBoundsException e) {
                response3 = e.toString();
                result3 = true;
            }
            
            result2 = response2.equals("Task: feed dog is successfully deleted.") && (testModel.getData().size() == 1);

            System.out.println("Result 1 - Task deleted correctly: " + result1 + " Expected: true");
            System.out.println("Result 2 - Task updates after delete and delete works correctly: " + result2 + " Expected: true");
            System.out.println("Result 3 - Delete recognises incorrect delete command: " + result3 + " Expected: true");
            System.out.println("=== End Test ===\n");
            
            clearExistingData(TEST_PATH);
            
            assertTrue(result1);
            assertTrue(result2);
            assertTrue(result3);
            
        } catch (Exception e) {
            System.out.println(e);           
            fail("Exception!");
        }

    }
    
    @Test
    public void testEncodeDecode() {
        try {
            clearExistingData(TEST_PATH);
            
            System.out.println("=== Encoding and Decoding ===");
            Model testModel = new Model(TEST_PATH);
            LocalDateTime date1 = LocalDateTime.of(2015, 11, 26, 23, 59, 59);
            LocalDateTime date2 = LocalDateTime.of(2015, 12, 23, 23, 59, 59);
            CommandDetail addTaskCmd1 = createTask("feed cat", TaskType.NORMAL, null, null, date1);
            CommandDetail addTaskCmd2 = createTask("feed fish", TaskType.NORMAL, null, null, date2);
            CommandDetail addTaskCmd3 = createTask("feed dog", TaskType.FLOATING, null, null, null);
            Task task1 = new Task(addTaskCmd1);
            Task task2 = new Task(addTaskCmd2);
            Task task3 = new Task(addTaskCmd3);
            
            testModel.addTask(task1);
            testModel.addTask(task2);
            testModel.addTask(task3);
            
            boolean result1 = false;
            
            try {
                ArrayList<Task> decodedData = testModel.testDecode();
                result1 = true;
                System.out.println("Actual Data Size: " + decodedData.size() + " Expected Data Size: " + testModel.getData().size());
                if (decodedData.size() != testModel.getData().size()) {
                    result1 = false;
                }
                for (int i=0; i<decodedData.size(); i++) {
                    System.out.println("Actual Item " + i + " : " + decodedData.get(i).getTitle() + " Expected Item " + i + " : " + testModel.getData().get(i).getTitle()); 
                    if (!decodedData.get(i).getTitle().equals(testModel.getData().get(i).getTitle())) {
                        result1 = false;
                    }
                }
            } catch (Exception e) {
                System.out.println("Unable to decode: " + e);
            }

            System.out.println("Result 1 - KatNote encodes and decodes successfully: " + result1 + " Expected: true");
            System.out.println("=== End Test ===\n");
            
            clearExistingData(TEST_PATH);
            
            assertTrue(result1);
            
        } catch (Exception e) {
            System.out.println(e);           
            fail("Exception!");
        }

    }
    
    @Test
    public void testUndoRedo() {
        try {
            clearExistingData(TEST_PATH);
            
            System.out.println("=== Undo and Redo ===");
            Model testModel = new Model(TEST_PATH);
            LocalDateTime date1 = LocalDateTime.of(2015, 11, 26, 23, 59, 59);
            LocalDateTime date2 = LocalDateTime.of(2015, 12, 23, 23, 59, 59);
            CommandDetail addTaskCmd1 = createTask("feed cat", TaskType.NORMAL, null, null, date1);
            CommandDetail addTaskCmd2 = createTask("feed fish", TaskType.NORMAL, null, null, date2);
            CommandDetail addTaskCmd3 = createTask("feed dog", TaskType.FLOATING, null, null, null);
            Task task1 = new Task(addTaskCmd1);
            Task task2 = new Task(addTaskCmd2);
            Task task3 = new Task(addTaskCmd3);
            
            testModel.addTask(task1);
            testModel.addTask(task2);
            testModel.addTask(task3);
            
            boolean result1 = true;
            boolean result2 = true;
            boolean result3 = true;
            boolean result4 = true;
            boolean result5 = true;
            
            // Procedure
            testModel.undo();
            testModel.undo();
            if (testModel.getData().size() != 1) {
                System.out.println("Undo procedure is incorrect. Incorrect task undone.");
                result1 = false;
            } else if (!testModel.getData().get(0).getTitle().equals(task1.getTitle())) {
                System.out.println("Undo procedure is incorrect. Incorrect task undone.");
                result1 = false;
            }
            
            testModel.undo();
            String response1 = testModel.undo();
            if (!response1.equals("Encountered Error: No actions left to undo.")) {
                System.out.println("Undo not logged correctly. Out of bounds not detected.");
                result2 = false;
            }
            
            testModel.redo();
            if (testModel.getData().size() != 1) {
                System.out.println("Redo procedure is incorrect. Incorrect task redone.");
                result3 = false;
            } else if (!testModel.getData().get(0).getTitle().equals(task1.getTitle())) {
                System.out.println("Redo procedure is incorrect. Incorrect task redone.");
                result3 = false;
            }
            
            testModel.redo();
            testModel.redo();
            String response2 = testModel.redo();
            if (!response2.equals("Encountered Error: No actions left to redo.")) {
                System.out.println("Redo not logged correctly. Out of bounds not detected.");
                result4 = false;
            }
            testModel.undo();
            testModel.addTask(task3);
            String response3 = testModel.redo();
            if (!response3.equals("Encountered Error: No actions left to redo.")) {
                System.out.println("Redo not cleared upon new user command.");
                result4 = false;
            }
            
            testModel.editDelete(1);
            testModel.editDelete(1);
            testModel.addTask(task2);
            testModel.addTask(task3);
            testModel.undo();
            testModel.undo();
            testModel.redo();
            testModel.undo();
            testModel.redo();
            testModel.redo();
            if (testModel.getData().size() != 3) {
                System.out.println("Redo + Undo procedure is incorrect. Actual Tasks left: " + testModel.getData().size() + " Expected Tasks Left: " + 3);
                result5 = false;
            } else if (!testModel.getData().get(2).getTitle().equals(task3.getTitle())) {
                System.out.println("Redo + Undo procedure is incorrect. Actual task(2): " + testModel.getData().get(2).getTitle() + " Expected task(2): " + task3.getTitle());
                result5 = false;
            }

            System.out.println("Result 1 - Undo Test 1: " + result1 + " Expected: true");
            System.out.println("Result 2 - Undo Test 2: " + result2 + " Expected: true");
            System.out.println("Result 3 - Redo Test 1: " + result3 + " Expected: true");
            System.out.println("Result 4 - Redo Test 2: " + result4 + " Expected: true");
            System.out.println("Result 5 - Undo + Redo Test 1: " + result5 + " Expected: true");
            System.out.println("=== End Test ===\n");
            
            clearExistingData(TEST_PATH);
            
            assertTrue(result1);
            assertTrue(result2);
            assertTrue(result3);
            assertTrue(result4);
            assertTrue(result5);
            
        } catch (Exception e) {
            System.out.println(e);           
            fail("Exception!");
        }

    }
}
