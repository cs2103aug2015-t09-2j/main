//@@author A0124552
package test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import katnote.Model;
import katnote.command.CommandDetail;
import katnote.command.CommandProperties;
import katnote.parser.EditTaskOption;
import katnote.task.Task;
import katnote.task.TaskType;
import katnote.utils.KatDateTime;

import org.junit.Test;

public class TestModel {

    // Constants
    private static final String DATA_FILENAME = "data.txt";
    private static final String DATA_BACKUP_FILENAME = "dataOld.txt";
    private static final String TEST_PATH = "TestFiles/TestModel/";
    private static final String TEST_PATH_ADDTASK = "TestFiles/TestModel/addTaskExpected.txt";
    private static final String TEST_PATH_SETDEFINITION = "TestFiles/TestModel/setDefinitionExpected.txt";
    private static final String TEST_PATH_SETLOCATION = "TestFiles/TestModel/setLocationExpected.txt";
    private static final String TEST_PATH_POSTPONE = "TestFiles/TestModel/postponeExpected.txt";

    // Messages
    private static final String MSG_ERR_IO = "I/O Exception.";
    private static final String MSG_ERR_MISSING_FILE = "Missing File: ";

    // Dates
    private static final LocalDateTime TODAY = LocalDateTime.now();

    /**
     * Compare the contents of both text files.
     * 
     * @param actual
     * @param expected
     * @return true if both files are the same in content and false if
     *         otherwise.
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

        for (int i = 0; i < contentActual.size(); i++) {
            if (!contentActual.get(i).equals(contentExpected.get(i))) {
                System.err.println("Mismatch on line: " + (i + 1));
                System.err.println("Actual Line = " + contentActual.get(i));
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
     * 
     * @param title
     * @param taskType
     * @param start
     * @param end
     * @param due
     * @return the task object.
     */
    private CommandDetail createTask(String title, TaskType taskType, KatDateTime start, KatDateTime end,
            KatDateTime due) {

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

        File clearDataOld = new File(path + DATA_BACKUP_FILENAME);
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
            KatDateTime date1 = new KatDateTime(LocalDateTime.of(2015, 11, 26, 23, 59, 59));
            CommandDetail addTaskCmd1 = createTask("feed cat", TaskType.NORMAL, null, null, date1);
            Task task1 = new Task(addTaskCmd1);
            CommandDetail testCmd = new CommandDetail();
            testCmd.setProperty(CommandProperties.FILE_PATH, "invalidLocation");

            boolean result4 = true;
            try {
                testModel.setLocation(testCmd);
            } catch (Exception e) {
                result4 = false;
            }

            testCmd.setProperty(CommandProperties.FILE_PATH, newPath);

            testModel.addTask(task1);

            testModel.setLocation(testCmd);

            File newFile = new File(newPath + DATA_FILENAME);
            File oldFile = new File(TEST_PATH + DATA_FILENAME);

            boolean result3 = compareFile(testModel.getDataFilePath(), TEST_PATH_SETLOCATION);

            System.out.println("Result 1 - New File exist: " + newFile.exists() + " Expected: true");
            System.out.println("Result 2 - Old File exist: " + oldFile.exists() + " Expected: false");
            System.out.println("Result 3 - New File contents are correct: " + result3 + "Expected: true");
            System.out.println("Result 4 - Set location to invalid location: " + result4 + "Expected: false");
            boolean result1 = newFile.exists();
            boolean result2 = oldFile.exists();

            // Clean up
            if (newFile.exists()) {
                newFile.delete();
            }
            System.out.println("=== End Test ===\n");

            assertTrue(result1);
            assertFalse(result2);
            assertTrue(result3);
            assertFalse(result4);

        } catch (Exception e) {
            System.err.println(e);
            fail("Exception!");
        }
    }

    @Test
    public void testAddTask() {
        try {
            clearExistingData(TEST_PATH);

            System.out.println("=== Add Task ===");
            Model testModel = new Model(TEST_PATH);
            CommandDetail addTaskCmd1;
            CommandDetail addTaskCmd2;

            KatDateTime date1 = new KatDateTime(LocalDateTime.of(2015, 10, 23, 23, 59));
            // System.out.println("Date Field: " +
            // date1.format(DATE_FORMATTER));

            addTaskCmd1 = createTask("feed cat", TaskType.NORMAL, null, null, date1);
            Task task1 = new Task(addTaskCmd1);
            addTaskCmd2 = createTask("  ", TaskType.FLOATING, null, null, null);
            Task task2 = new Task(addTaskCmd2);

            testModel.addTask(task1);
            testModel.addTask(task2);

            boolean result = compareFile(testModel.getDataFilePath(), TEST_PATH_ADDTASK);

            // Clean Up
            clearExistingData(TEST_PATH);
            System.out.println("Result 1 - Tasks Added Correctly: " + result + " Expected: true");
            System.out.println("=== End Test ===\n");

            assertTrue(result);

        } catch (Exception e) {
            System.err.println(e);
            fail("Exception!");
        }
    }

    @Test
    public void testSetDefinition() {
        try {
            clearExistingData(TEST_PATH);

            System.out.println("=== Add Task ===");
            Model testModel = new Model(TEST_PATH);

            testModel.setDefinition("night", "10.30pm");
            testModel.setDefinition("afternoon", "2.00pm");
            testModel.setDefinition("morning", "8.00am");
            testModel.setDefinition("sleeptime", "11.00pm");
            testModel.setDefinition("wakeup", "7.30am");
            testModel.setDefinition("evening", "7.30pm");

            boolean result = compareFile(testModel.getDataFilePath(), TEST_PATH_SETDEFINITION);

            // Clean Up
            clearExistingData(TEST_PATH);
            System.out.println("Result 1 - Definitions set correctly: " + result + " Expected: true");
            System.out.println("=== End Test ===\n");

            assertTrue(result);

        } catch (Exception e) {
            System.err.println(e);
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

            try {
                testModel.importData(importCmd);
            } catch (Exception e) {
                result2 = true;
            }

            System.out.println("Result 1 - File imported correctly: " + result1 + " Expected: true");
            System.out.println("Result 2 - Blank File not imported: " + result2 + " Expected: true");
            System.out.println("=== End Test ===\n");

            clearExistingData(TEST_PATH);

            assertTrue(result1);
            assertTrue(result2);

        } catch (Exception e) {
            System.err.println(e);
            fail("Exception!");
        }

    }

    @Test
    public void testDeleteTask() {
        try {
            clearExistingData(TEST_PATH);

            System.out.println("=== Delete Task ===");
            Model testModel = new Model(TEST_PATH);
            KatDateTime date1 = new KatDateTime(LocalDateTime.of(2015, 11, 26, 23, 59, 59));
            KatDateTime date2 = new KatDateTime(LocalDateTime.of(2015, 12, 23, 23, 59, 59));
            CommandDetail addTaskCmd1 = createTask("feed cat", TaskType.NORMAL, null, null, date1);
            CommandDetail addTaskCmd2 = createTask("feed fish", TaskType.NORMAL, null, null, date2);
            CommandDetail addTaskCmd3 = createTask("feed dog", TaskType.FLOATING, null, null, null);
            Task task1 = new Task(addTaskCmd1);
            Task task2 = new Task(addTaskCmd2);
            Task task3 = new Task(addTaskCmd3);
            String response1;
            String response2;

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
                testModel.editDelete(1);
            } catch (IndexOutOfBoundsException e) {
                result3 = true;
            }

            result2 = response2.equals("Task: feed dog is successfully deleted.") && (testModel.getData().size() == 1);

            System.out.println("Result 1 - Task deleted correctly: " + result1 + " Expected: true");
            System.out.println("Result 2 - Task updates after delete and delete works correctly: " + result2
                    + " Expected: true");
            System.out.println("Result 3 - Delete recognises incorrect delete command: " + result3 + " Expected: true");
            System.out.println("=== End Test ===\n");

            clearExistingData(TEST_PATH);

            assertTrue(result1);
            assertTrue(result2);
            assertTrue(result3);

        } catch (Exception e) {
            System.err.println(e);
            fail("Exception!");
        }

    }

    @Test
    public void testEncodeDecode() {
        try {
            clearExistingData(TEST_PATH);

            System.out.println("=== Encoding and Decoding ===");
            Model testModel = new Model(TEST_PATH);
            KatDateTime date1 = new KatDateTime(LocalDateTime.of(2015, 11, 26, 23, 59, 59));
            KatDateTime date2 = new KatDateTime(LocalDateTime.of(2015, 12, 23, 23, 59, 59));
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
                System.out.println("Actual Data Size: " + decodedData.size() + " Expected Data Size: "
                        + testModel.getData().size());
                if (decodedData.size() != testModel.getData().size()) {
                    result1 = false;
                }
                for (int i = 0; i < decodedData.size(); i++) {
                    System.out.println("Actual Item " + i + " : " + decodedData.get(i).getTitle() + " Expected Item "
                            + i + " : " + testModel.getData().get(i).getTitle());
                    if (!decodedData.get(i).getTitle().equals(testModel.getData().get(i).getTitle())) {
                        result1 = false;
                    }
                }
            } catch (Exception e) {
                System.err.println("Unable to decode: " + e);
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
            KatDateTime date1 = new KatDateTime(LocalDateTime.of(2015, 11, 26, 23, 59, 59));
            KatDateTime date2 = new KatDateTime(LocalDateTime.of(2015, 12, 23, 23, 59, 59));
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
            boolean result2 = false;
            boolean result3 = false;
            boolean result4 = false;
            boolean result5 = true;

            // Procedure
            testModel.undo();

            testModel.undo();

            if (!testModel.getData().get(0).getTitle().equals(task1.getTitle())) {
                System.err.println("Undo procedure is incorrect. Incorrect task undone.");
                result1 = false;
            } else {
                result1 = true;
            }

            testModel.undo();
            try {
                testModel.undo();
            } catch (Exception e) {
                result2 = true;
            }

            testModel.redo();
            if ((testModel.getData().size() != 1) || (!testModel.getData().get(0).getTitle().equals(task1.getTitle()))) {
                System.err.println("Redo procedure is incorrect. Incorrect task redone.");
                result3 = false;
            } else {
                result3 = true;
            }

            testModel.redo();
            testModel.redo();

            try {
                testModel.redo();
            } catch (Exception e1) {

                testModel.undo();
                testModel.addTask(task3);

                try {
                    testModel.redo();
                } catch (Exception e2) {
                    result4 = true;
                }
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
                System.err.println("Redo + Undo procedure is incorrect. Actual Tasks left: "
                        + testModel.getData().size() + " Expected Tasks Left: " + 3);
                result5 = false;
            } else if (!testModel.getData().get(2).getTitle().equals(task3.getTitle())) {
                System.err.println("Redo + Undo procedure is incorrect. Actual task(2): "
                        + testModel.getData().get(2).getTitle() + " Expected task(2): " + task3.getTitle());
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
            System.err.println(e);
            fail("Exception!");
        }

    }

    @Test
    public void testPostpone() {
        try {
            clearExistingData(TEST_PATH);

            System.out.println("=== Postpone ===");
            Model testModel = new Model(TEST_PATH);
            CommandDetail addTaskCmd1;
            CommandDetail addTaskCmd2;
            CommandDetail addTaskCmd3;
            CommandDetail addTaskCmd4;
            CommandDetail addTaskCmd5;
            CommandDetail addTaskCmd6;

            KatDateTime date1 = new KatDateTime(LocalDateTime.of(2016, 10, 23, 23, 59));
            KatDateTime date2 = new KatDateTime(LocalDateTime.of(2016, 10, 25, 23, 59));
            KatDateTime date3 = new KatDateTime(LocalDateTime.of(2016, 10, 26, 23, 59));
            KatDateTime date4 = new KatDateTime(LocalDateTime.of(2017, 11, 28, 23, 59));
            KatDateTime date5 = new KatDateTime(LocalDateTime.of(2016, 10, 29, 23, 59));
            KatDateTime date6 = new KatDateTime(LocalDateTime.of(2016, 10, 28, 22, 59));
            KatDateTime date7 = new KatDateTime(LocalDateTime.of(2016, 10, 28, 21, 00));

            addTaskCmd1 = createTask("feed cat", TaskType.NORMAL, null, null, date1);
            Task task1 = new Task(addTaskCmd1);
            addTaskCmd2 = createTask("something", TaskType.FLOATING, null, null, null);
            Task task2 = new Task(addTaskCmd2);
            addTaskCmd3 = createTask("testEvent1", TaskType.EVENT, date2, date3, null);
            Task task3 = new Task(addTaskCmd3);
            addTaskCmd4 = createTask("testEvent2", TaskType.EVENT, date2, date3, null);
            Task task4 = new Task(addTaskCmd4);
            addTaskCmd5 = createTask("testEvent3", TaskType.EVENT, date2, date3, null);
            Task task5 = new Task(addTaskCmd5);
            addTaskCmd6 = createTask("testEvent4", TaskType.EVENT, date2, date5, null);
            Task task6 = new Task(addTaskCmd6);

            testModel.addTask(task1);
            testModel.addTask(task2);
            testModel.addTask(task3);
            testModel.addTask(task4);
            testModel.addTask(task5);
            testModel.addTask(task6);

            boolean result1 = false;
            try {
                testModel.postpone(0, date6);
            } catch (Exception e) {
                result1 = true;
            }

            boolean result2 = false;
            try {
                testModel.postpone(1, date6);
            } catch (Exception e) {
                result2 = true;
            }

            testModel.postpone(2, date4);
            testModel.postpone(3, date5);
            testModel.postpone(4, date6);
            testModel.postpone(5, date7);

            boolean result3 = compareFile(testModel.getDataFilePath(), TEST_PATH_POSTPONE);

            // Clean Up
            clearExistingData(TEST_PATH);
            System.out.println("Result 1 - Normal tasks not postponed: " + result1 + " Expected: true");
            System.out.println("Result 2 - Floating tasks not postponed: " + result2 + " Expected: true");
            System.out.println("Result 3 - Multiple tasks postponed to correct dates: " + result3 + " Expected: true");
            System.out.println("=== End Test ===\n");

            assertTrue(result1);
            assertTrue(result2);
            assertTrue(result3);

        } catch (Exception e) {
            System.err.println(e);
            fail("Exception!");
        }
    }

    @Test
    public void testModify() {
        try {
            clearExistingData(TEST_PATH);

            System.out.println("=== Modify ===");
            Model testModel = new Model(TEST_PATH);
            KatDateTime date1 = new KatDateTime(LocalDateTime.of(2016, 1, 26, 12, 00));
            KatDateTime date2 = new KatDateTime(LocalDateTime.of(2016, 2, 23, 23, 00));
            KatDateTime date3 = new KatDateTime(LocalDateTime.of(2016, 1, 28, 23, 00));
            KatDateTime date4 = new KatDateTime(LocalDateTime.of(2016, 1, 19, 23, 00));

            CommandDetail addTaskCmd1 = createTask("feed cat", TaskType.NORMAL, null, null, date1);
            CommandDetail addTaskCmd2 = createTask("feed fish", TaskType.EVENT, date3, date2, null);
            CommandDetail addTaskCmd3 = createTask("feed dog", TaskType.FLOATING, null, null, null);
            CommandDetail addTaskCmd4 = createTask("feed Kat!", TaskType.FLOATING, null, null, null);
            Task task1 = new Task(addTaskCmd1);
            Task task2 = new Task(addTaskCmd2);
            Task task3 = new Task(addTaskCmd3);
            Task task4 = new Task(addTaskCmd4);

            EditTaskOption option1 = new EditTaskOption(CommandProperties.TASK_TITLE, "new title");
            EditTaskOption option2 = new EditTaskOption(CommandProperties.TIME_FROM, "19/1/16 11pm");
            EditTaskOption option3 = new EditTaskOption(CommandProperties.TIME_BY, "23/2/16 11pm");
            EditTaskOption option4 = new EditTaskOption(CommandProperties.TIME_FROM, "11/3/16 3pm");

            testModel.addTask(task1);
            testModel.addTask(task2);
            testModel.addTask(task3);
            testModel.addTask(task4);
            testModel.getData().get(1).setCompleted(true);

            boolean result1 = false;
            boolean result2 = false;
            boolean result3 = true;
            boolean result4 = false;
            boolean result5 = true;
            boolean result6 = false;
            boolean result7 = true;

            // Modification
            try {
                testModel.editModify(2, option3);
                if (testModel.getData().get(2).getTaskType().equals(TaskType.NORMAL)) {
                    result1 = true;
                } else {
                    System.err.println("Change in end date does not change task type from Floating to Normal.");
                }
            } catch (Exception e) {
                System.err.println("Unable to set end date for floating task.");
            }

            try {
                testModel.editModify(2, option2);
                if (testModel.getData().get(2).getTaskType().equals(TaskType.EVENT)) {
                    result2 = true;
                } else {
                    System.err.println("Change in start date does not change task type from Normal to Event.");
                }
            } catch (Exception e) {
                System.err.println("Unable to set start date for normal task.");
            }

            try {
                testModel.editModify(3, option2);
                System.err.println("Warning! Change in Floating to Event task type with change in start date.");
            } catch (Exception e) {
                result3 = false;
            }

            testModel.editComplete(0);
            testModel.editIncomplete(1);
            if ((testModel.getData().get(0).isCompleted()) && (!testModel.getData().get(1).isCompleted())) {
                result4 = true;
            } else {
                System.err.println("editComplete and/or editComplete not executed correctly.");
            }

            try {
                testModel.editComplete(0);
                System.err.println("Repeated setting of completed task not denied.");
            } catch (Exception e) {
                result5 = false;
            }

            testModel.editModify(0, option1);
            if (testModel.getData().get(0).getTitle().equals("new title")) {
                result6 = true;
            } else {
                System.err.println("Edit of task title not captured.");
            }

            try {
                testModel.editModify(1, option4);
                System.err.println("Setting of start date beyond end date not denied.");
            } catch (Exception e) {
                result7 = false;
            }

            // Clean Up
            clearExistingData(TEST_PATH);
            System.out.println("Result 1 - Change from Floating to Normal task by editing end date: " + result1
                    + " Expected: true");
            System.out.println("Result 2 - Change from Normal to Event task by editing start date: " + result2
                    + " Expected: true");
            System.out.println("Result 3 - Change from Floating to Event: " + result3 + " Expected: false");
            System.out.println("Result 4 - Change in completed flag: " + result4 + " Expected: true");
            System.out.println("Result 5 - Repeated change in completed flag: " + result5 + " Expected: false");
            System.out.println("Result 6 - Change in task title: " + result6 + " Expected: true");
            System.out.println("Result 7 - Change start date to beyond end date: " + result7 + " Expected: false");
            System.out.println("=== End Test ===\n");

            assertTrue(result1);
            assertTrue(result2);
            assertFalse(result3);
            assertTrue(result4);
            assertFalse(result5);
            assertTrue(result6);
            assertFalse(result7);

        } catch (Exception e) {
            System.err.println(e);
            fail("Exception!");
        }
    }
}
