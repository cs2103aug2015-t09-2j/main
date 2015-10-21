package test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;

import org.junit.Test;

import katnote.Logic;
import katnote.UIFeedback;
import katnote.ViewState;
import katnote.task.Task;
import katnote.task.TaskType;

public class TestLogic {

    private static final String TEST_SOURCE_PATH = "TestFiles/";

    private static final String INPUT_ADD = "add Feed cat by 21/10/2015";
    private static final String INPUT_ADD_2 = "add Buy cat food by 21/10/2015";
    private static final String INPUT_EDIT_MODIFY = "edit task 1 set task_title Feed dog";
    private static final String INPUT_EDIT_COMPLETE = "edit task 1 mark completed";
    private static final String INPUT_DELETE = "delete task 1";
    private static final String INPUT_SET_LOCATION = "set location katnote";
    private static final String INPUT_UNDO = "undo";
    private static final String INPUT_REDO = "redo";
    private static final String INPUT_VIEW_INCOMPLETED = "view tasks incompleted";
    private static final String INPUT_VIEW_COMPLETED = "view tasks completed";
    private static final String INPUT_FIND_KEYWORD_EXIST = "find cat";
    private static final String INPUT_FIND_KEYWORD_NOT_EXIST = "find no";
    private static final String INPUT_FIND_KEYWORD_EXIST_UPPERCASE = "find CAT";
    private static final String INPUT_FIND_KEYWORD_EXIST_SUBSTRING = "find CA";
    private static final String INPUT_FIND_KEYWORD_EXIST_PHRASE = "find feed cat";

    public boolean isEqualTask(Task t1, Task t2) {
        return (t1.getTitle().equals(t2.getTitle())
                //&& t1.getTerminateDate().toLocalDate().isEqual(t2.getTerminateDate().toLocalDate())
                && t1.getTaskType() == t2.getTaskType());
    }

    public boolean isEqualTaskList(ArrayList<Task> list1, ArrayList<Task> list2) {
        boolean ans = true;
        if (list1.size() != list2.size()) {
            ans = false;
        } else if (list1.isEmpty() && list2.isEmpty()) {
            ans = true;
        } else {
            for (int i = 0; i < list1.size(); i++) {
                if (!isEqualTask(list1.get(i), list2.get(i))) {
                    ans = false;
                    break;
                }
            }
        }
        return ans;
    }

    public boolean isEqualViewState(ViewState vs1, ViewState vs2) {
        return (isEqualTaskList(vs1.getNormalTasks(), vs2.getNormalTasks())
                && isEqualTaskList(vs1.getFloatingTasks(), vs2.getFloatingTasks())
                && isEqualTaskList(vs1.getEventTasks(), vs2.getEventTasks()));
    }

    public boolean isEqualUIFeedback(UIFeedback f1, UIFeedback f2) {
        boolean isVSEqual = isEqualViewState(f1.getViewState(), f2.getViewState());
        return (f1.isAnError() == f2.isAnError() 
                && f1.isAnExit() == f2.isAnExit() 
                && f1.getMessage().equals(f2.getMessage())
                && isVSEqual);
    }

    private void clearExistingData(String path) {

        File clearData = new File(path + "data.txt");
        if (clearData.exists()) {
            clearData.delete();
        }
    }

    /* Test that command switch cases are correctly executed */
    @Test
    public void testExecuteAdd() {
        try {
            clearExistingData(TEST_SOURCE_PATH);
            
            Logic logic = new Logic(TEST_SOURCE_PATH);
            UIFeedback actual = logic.execute(INPUT_ADD);
            String expectedMsg = "Task: Feed cat added.";
            
            clearExistingData(TEST_SOURCE_PATH);
            
            assertEquals(actual.getMessage(), expectedMsg); 

        } catch (Exception e) {
            System.out.println(e.toString());
            fail("Exception!");
        }   
    }

    /*
    @Test
    public void testExecuteEditModify() {
        try {
            Logic logic = new Logic(TEST_SOURCE_PATH);
            logic.execute(INPUT_ADD);
            logic.
            UIFeedback actual = logic.execute(INPUT_EDIT_MODIFY);

            String expectedMsg = "Task: Feed dog is successfully modified.";
            assertEquals(actual.getMessage(), expectedMsg);  

        } catch (Exception e) {
            System.out.println(e.toString());
            fail("Exception!");
        }   
    }

    @Test
    public void testExecuteEditComplete() {
        try {
            Logic logic = new Logic(TEST_SOURCE_PATH);
            logic.execute(INPUT_ADD);
            UIFeedback actual = logic.execute(INPUT_EDIT_MODIFY);

            String expectedMsg = "Task: Feed cat is marked completed.";
            assertEquals(actual.getMessage(), expectedMsg);  

        } catch (Exception e) {
            System.out.println(e.toString());
            fail("Exception!");
        } 
    }

    @Test
    public void testExecuteUndo() {
        fail("Not yet implemented");
    }

    @Test
    public void testExecuteRedo() {
        fail("Not yet implemented");
    }

    @Test
    public void testExecuteSetLocation() {
        fail("Not yet implemented");
    }

    @Test
    public void testExecuteExit() {
        fail("Not yet implemented");
    }

    @Test
    public void testExecuteInvalidCommand() {
        fail("Not yet implemented");
    }

    @Test
    public void testExecuteViewTask() {
        fail("Not yet implemented");
    }

    @Test
    public void testExecuteFindTask() {
        fail("Not yet implemented");
    }
     */

    /* Test Public Methods to be accessed by UI */

    @Test
    public void testSetViewMapping() {
        try {
            clearExistingData(TEST_SOURCE_PATH);
            
            Logic logic = new Logic(TEST_SOURCE_PATH);
            Task t = new Task();
            t.setID(100); // create a new task of id = 100
            ArrayList<Task> taskList = new ArrayList<Task>();
            taskList.add(t);
            logic.setViewMapping(taskList);

            ArrayList<Integer> actual = logic.getTrackerIDList();
            ArrayList<Integer> expected = new ArrayList<Integer>();
            expected.add(100);
            
            clearExistingData(TEST_SOURCE_PATH);
            
            assertEquals(actual, expected);

        } catch (Exception e) {
            System.out.println(e.toString());
            fail("Exception!");
        }       
    }

    /* Test different types of searchData */


    @Test
    public void testSearchData() { 
        Logic logic;
        UIFeedback feedback;
        ViewState actualVS;
        ViewState expectedVS;
        Task t;
        
        try {
            clearExistingData(TEST_SOURCE_PATH);
            
            // Search for task by keyword in an empty list (partition case)
            logic = new Logic(TEST_SOURCE_PATH);
            feedback = logic.execute(INPUT_VIEW_INCOMPLETED);
            actualVS = feedback.getViewState();
            expectedVS = new ViewState(new ArrayList<Task>(), 
                                        new ArrayList<Task>(), 
                                        new ArrayList<Task>());
            
            clearExistingData(TEST_SOURCE_PATH);
            
            System.out.println("Partition Case - Search in empty list: " + isEqualViewState(actualVS, expectedVS));
            assertTrue(isEqualViewState(actualVS, expectedVS));
            
            // Search incomplete tasks
            logic = new Logic(TEST_SOURCE_PATH);
            logic.execute(INPUT_ADD);
            feedback = logic.execute(INPUT_VIEW_INCOMPLETED);
            actualVS = feedback.getViewState();
            expectedVS = new ViewState(new ArrayList<Task>(), 
                                        new ArrayList<Task>(), 
                                        new ArrayList<Task>());
            t = new Task();
            t.setTitle("Feed cat");
            t.setTaskType(TaskType.NORMAL);
            expectedVS.getNormalTasks().add(t);
            
            clearExistingData(TEST_SOURCE_PATH);
            
            System.out.println("Partition Case - Search incomplete tasks: " + isEqualViewState(actualVS, expectedVS));
            assertTrue(isEqualViewState(actualVS, expectedVS));            
            
            // Search keyword that exist
            logic = new Logic(TEST_SOURCE_PATH);
            logic.execute(INPUT_ADD);
            feedback = logic.execute(INPUT_FIND_KEYWORD_EXIST);
            actualVS = feedback.getViewState();
            expectedVS = new ViewState(new ArrayList<Task>(), 
                                        new ArrayList<Task>(), 
                                        new ArrayList<Task>());
            t = new Task();
            t.setTitle("Feed cat");
            t.setTaskType(TaskType.NORMAL);
            expectedVS.getNormalTasks().add(t);
            
            clearExistingData(TEST_SOURCE_PATH);
            
            System.out.println("Partition Case - Search keyword that exists: " + isEqualViewState(actualVS, expectedVS));
            assertTrue(isEqualViewState(actualVS, expectedVS));
            
            // Search keyword that exist
            logic = new Logic(TEST_SOURCE_PATH);
            logic.execute(INPUT_ADD);
            logic.execute(INPUT_ADD_2);
            feedback = logic.execute(INPUT_FIND_KEYWORD_EXIST);
            actualVS = feedback.getViewState();
            expectedVS = new ViewState(new ArrayList<Task>(), 
                                        new ArrayList<Task>(), 
                                        new ArrayList<Task>());
            t = new Task();
            t.setTitle("Feed cat");
            t.setTaskType(TaskType.NORMAL);
            expectedVS.getNormalTasks().add(t);
            
            Task t2 = new Task();
            t2.setTitle("Buy cat food");
            t2.setTaskType(TaskType.NORMAL);
            expectedVS.getNormalTasks().add(t2);
            
            clearExistingData(TEST_SOURCE_PATH);
            
            /*
            System.out.println(actualVS.getNormalTasks().get(0).getTitle() + " " + expectedVS.getNormalTasks().get(0).getTitle());
            System.out.println(actualVS.getNormalTasks().get(1).getTitle() + " " + expectedVS.getNormalTasks().get(1).getTitle());
            
            System.out.println(isEqualTask(actualVS.getNormalTasks().get(0), expectedVS.getNormalTasks().get(0)));
            System.out.println(isEqualTask(actualVS.getNormalTasks().get(1), expectedVS.getNormalTasks().get(1)));
            */
            
            System.out.println("Partition Case - Search keyword that exists: " + isEqualViewState(actualVS, expectedVS));
            assertTrue(isEqualViewState(actualVS, expectedVS));
            
            // Search keyword that doesn't exist (partition case)
            logic = new Logic(TEST_SOURCE_PATH);
            logic.execute(INPUT_ADD);
            feedback = logic.execute(INPUT_FIND_KEYWORD_NOT_EXIST);
            actualVS = feedback.getViewState();
            expectedVS = new ViewState(new ArrayList<Task>(), 
                                        new ArrayList<Task>(), 
                                        new ArrayList<Task>());
            
            clearExistingData(TEST_SOURCE_PATH);
            
            System.out.println("Partition Case - Search keyword that doesn't exists: " + isEqualViewState(actualVS, expectedVS));
            assertTrue(isEqualViewState(actualVS, expectedVS));
            
            // Search keyword that is a substring
            logic = new Logic(TEST_SOURCE_PATH);
            logic.execute(INPUT_ADD);
            feedback = logic.execute(INPUT_FIND_KEYWORD_EXIST_SUBSTRING);
            actualVS = feedback.getViewState();
            expectedVS = new ViewState(new ArrayList<Task>(), 
                                        new ArrayList<Task>(), 
                                        new ArrayList<Task>());
                  
            clearExistingData(TEST_SOURCE_PATH);
            
            System.out.println("Partition Case - Search keyword that is a substring: " + isEqualViewState(actualVS, expectedVS));
            assertTrue(isEqualViewState(actualVS, expectedVS));
            
            // Test case sensitive of search by keyword (should be case insensitive)
            logic = new Logic(TEST_SOURCE_PATH);
            logic.execute(INPUT_ADD);
            feedback = logic.execute(INPUT_FIND_KEYWORD_EXIST_UPPERCASE);
            actualVS = feedback.getViewState();
            expectedVS = new ViewState(new ArrayList<Task>(), 
                                        new ArrayList<Task>(), 
                                        new ArrayList<Task>());
            
            t = new Task();
            t.setTitle("Feed cat");
            t.setTaskType(TaskType.NORMAL);
            expectedVS.getNormalTasks().add(t);
            
            clearExistingData(TEST_SOURCE_PATH);
            
            System.out.println("Partition Case - Search keyword that is a substring: " + isEqualViewState(actualVS, expectedVS));
            assertTrue(isEqualViewState(actualVS, expectedVS));
            
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception!");
        }



        // Test case sensitivity of keyword search (should be case insensitive)
    }




}
