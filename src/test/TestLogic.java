//@@author A0131003J

package test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;

import org.junit.Test;

import katnote.logic.Logic;
import katnote.logic.UIFeedback;
import katnote.logic.ViewState;
import katnote.task.Task;
import katnote.task.TaskType;

public class TestLogic {
    private static final String TEST_SOURCE_PATH = "TestFiles/";

    private static final String INPUT_EDIT_MODIFY = "edit 1 title Feed cat";
    private static final String INPUT_MARK_COMPLETE = "mark 1 completed";
    private static final String INPUT_MARK_INCOMPLETE = "mark 1 incompleted";
    private static final String INPUT_DELETE = "delete 1";
    private static final String INPUT_DELETE_INVALID = "delete 0";
    private static final String INPUT_POSTPONE = "postpone 1 to 9/11/2015";
    private static final String INPUT_UNDO = "undo";
    private static final String INPUT_REDO = "redo";
    private static final String INPUT_EXIT = "exit";
      
    private static final String INPUT_ADD_FLOAT1 = "add float1";
    private static final String INPUT_ADD_FLOAT2 = "add float2";
    private static final String INPUT_ADD_EVENT1 = "add event1 from 8/11/2015 6pm to 13/11/2015 7pm";
    private static final String INPUT_ADD_EVENT2 = "add event2 from 8/11/2015 to 11/11/2015";
    private static final String INPUT_ADD_EVENT3 = "add event3 from 8/11/2015 to 12/11/2015";
    private static final String INPUT_ADD_EVENT4 = "add event4 from 9/11/2015 to 10/11/2015";
    private static final String INPUT_ADD_TASK1 = "task1 by 1/12/2015 6am";
    private static final String INPUT_ADD_TASK2 = "task2 by 1/12/2015";   

    private static final String INPUT_VIEW_INCOMPLETED = "view incomplete";
    private static final String INPUT_VIEW_COMPLETED = "view completed";
    private static final String INPUT_VIEW = "view";
    
    private static final String INPUT_VIEW_ON_EXIST = "view on 8/11/2015";
    private static final String INPUT_VIEW_ON_NONEXIST = "view on 8/11/2016";

    private static final String INPUT_VIEW_BY_EXIST = "view by 1/12/2015";
    private static final String INPUT_VIEW_BY_NONEXIST = "view by 8/11/2014";
    private static final String INPUT_VIEW_FROM_TO_EXIST_NO_TIME = "view from 8/11/2015 to 1/12/2015"; 
    private static final String INPUT_VIEW_FROM_NONEXIST_TO_NONEXIST = "view from 8/11/2014 to 1/12/2016";
    private static final String INPUT_VIEW_FROM_TO_EXIST_NO_DATE = "view from 6pm to 7pm";

    private static final String INPUT_FIND_KEYWORD_EXIST = "find task";
    private static final String INPUT_FIND_KEYWORD_NONEXIST = "find no";
    private static final String INPUT_FIND_KEYWORD_EXIST_UPPERCASE = "find TASK1";
    private static final String INPUT_FIND_KEYWORD_EXIST_SUBSTRING = "find ta";

    private static Task float1 = new Task("float1", TaskType.FLOATING, true);
    private static Task float2 = new Task("float2", TaskType.FLOATING, false);
    private static Task event1 = new Task("event1", TaskType.EVENT, false);
    private static Task event2 = new Task("event2", TaskType.EVENT, false);
    private static Task event3 = new Task("event3", TaskType.EVENT, false);
    private static Task event4 = new Task("event4", TaskType.EVENT, false);
    private static Task task1 = new Task("task1", TaskType.NORMAL, false);
    private static Task task2 = new Task("task2", TaskType.NORMAL, false);
    
    
    /* Utility Methods */
    public boolean isEqualTask(Task t1, Task t2) {
        boolean ans = t1.getTitle().equals(t2.getTitle()) && t1.getTaskType() == t2.getTaskType();
        //System.out.println(t1.getTitle() + "  " + t2.getTitle() + ": " + ans);
        return ans;
    }

    public boolean isEqualTaskList(ArrayList<Task> list1, ArrayList<Task> list2) {
        boolean ans = true;
        if (list1.isEmpty() && list2.isEmpty()) {
            ans = true;
        } else if (list2.isEmpty() && !list1.isEmpty()) {
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
        return (f1.isAnError() == f2.isAnError() && f1.isAnExit() == f2.isAnExit()
                && f1.getMessage().equals(f2.getMessage()) && isVSEqual);
    }

    private void clearExistingData(String path) {

        File clearData = new File(path + "data.txt");
        if (clearData.exists()) {
            clearData.delete();
        }
    }
    
    // Creates a schedule with preloaded tasks. 
    private void fillSchedule(Logic logic) {
        try {
            logic.execute(INPUT_ADD_FLOAT1);
            logic.execute(INPUT_ADD_FLOAT2);
            logic.execute(INPUT_ADD_EVENT1);
            logic.execute(INPUT_ADD_EVENT2);
            logic.execute(INPUT_ADD_EVENT3);
            logic.execute(INPUT_ADD_EVENT4);
            logic.execute(INPUT_ADD_TASK1);
            logic.execute(INPUT_ADD_TASK2);
            UIFeedback fb = logic.execute(INPUT_VIEW);
            logic.setViewMapping(fb.getViewState().getFloatingTasks());
            logic.execute(INPUT_MARK_COMPLETE);
            
        } catch (Exception e) {
            System.out.println(e.toString());
            fail("Exception!");
        }
    }
    
    
    /*-----------------------------------------------*/

    /* Test that command switch cases are correctly executed */
    
    // Equivalence Partition Case 
    @Test
    public void testExecuteAdd() {
        try {
            Logic logic = new Logic(TEST_SOURCE_PATH);
            UIFeedback actual = logic.execute(INPUT_ADD_FLOAT1);
            String expectedMsg = "Task: float1 added.";
            assertEquals(actual.getMessage(), expectedMsg); 

        } catch (Exception e) {
            System.out.println(e.toString());
            fail("Exception!");
        } 
        clearExistingData(TEST_SOURCE_PATH);
    }

    // Equivalence Partition Case 
    @Test
    public void testExecuteEditModify() {
        try {
            Logic logic = new Logic(TEST_SOURCE_PATH);
            fillSchedule(logic);
            
            UIFeedback fb = logic.execute(INPUT_VIEW);
            logic.setViewMapping(fb.getViewState().getFloatingTasks());
            UIFeedback actual = logic.execute(INPUT_EDIT_MODIFY);

            String expectedMsg = "Task: Feed cat is successfully modified.";
            assertEquals(actual.getMessage(), expectedMsg);  

        } catch (Exception e) {
            System.out.println(e.toString());
            fail("Exception!");
        }
        clearExistingData(TEST_SOURCE_PATH);
    }
    
    // Equivalence Partition Case 
    @Test
    public void testExecuteEditComplete() {
        try {
            Logic logic = new Logic(TEST_SOURCE_PATH);
            fillSchedule(logic);
            
            UIFeedback fb = logic.execute(INPUT_VIEW);
            logic.setViewMapping(fb.getViewState().getFloatingTasks());
            UIFeedback actual = logic.execute(INPUT_MARK_COMPLETE);

            String expectedMsg = "Task: float2 is marked completed.";
            assertEquals(actual.getMessage(), expectedMsg);  

        } catch (Exception e) {
            System.out.println(e.toString());
            fail("Exception!");
        }
        clearExistingData(TEST_SOURCE_PATH);
    }
    
    // Equivalence Partition Case 
    @Test
    public void testExecuteEditIncomplete() {
        try {
            Logic logic = new Logic(TEST_SOURCE_PATH);
            fillSchedule(logic);
            
            UIFeedback fb = logic.execute(INPUT_VIEW_COMPLETED);
            logic.setViewMapping(fb.getViewState().getFloatingTasks());
            UIFeedback actual = logic.execute(INPUT_MARK_INCOMPLETE);

            String expectedMsg = "Task: float1 is marked incomplete.";
            assertEquals(actual.getMessage(), expectedMsg);  

        } catch (Exception e) {
            System.out.println(e.toString());
            fail("Exception!");
        }
        clearExistingData(TEST_SOURCE_PATH);
    }
    
    // Equivalence Partition Case - Invalid Index Exception Handling
    @Test
    public void testExecuteDeleteInvalidIndex() {
        try {
            Logic logic = new Logic(TEST_SOURCE_PATH);
            fillSchedule(logic);
            
            UIFeedback fb = logic.execute(INPUT_VIEW);
            logic.setViewMapping(fb.getViewState().getFloatingTasks());
            UIFeedback actual = logic.execute(INPUT_DELETE_INVALID);

            String expectedMsg = "Invalid index!";
            assertEquals(actual.getMessage(), expectedMsg);  

        } catch (Exception e) {
            System.out.println(e.toString());
            fail("Exception!");
        }
        clearExistingData(TEST_SOURCE_PATH);
    }
    
    // Equivalence Partition Case 
    @Test
    public void testExecuteDelete() {
        try {
            Logic logic = new Logic(TEST_SOURCE_PATH);
            fillSchedule(logic);
            
            UIFeedback fb = logic.execute(INPUT_VIEW);
            logic.setViewMapping(fb.getViewState().getFloatingTasks());
            UIFeedback actual = logic.execute(INPUT_DELETE);

            String expectedMsg = "Task: float2 is successfully deleted.";
            assertEquals(actual.getMessage(), expectedMsg);  

        } catch (Exception e) {
            System.out.println(e.toString());
            fail("Exception!");
        }
        clearExistingData(TEST_SOURCE_PATH);
    }
    
    // Equivalence Partition Case 
    @Test
    public void testExecutePostpone() {
        try {
            Logic logic = new Logic(TEST_SOURCE_PATH);
            fillSchedule(logic);
            
            UIFeedback fb = logic.execute(INPUT_VIEW);
            logic.setViewMapping(fb.getViewState().getEventTasks());
            UIFeedback actual = logic.execute(INPUT_POSTPONE);

            String expectedMsg = "Task: event2 is postponed to 2015-11-09T00:00";
            assertEquals(actual.getMessage(), expectedMsg);  

        } catch (Exception e) {
            System.out.println(e.toString());
            fail("Exception!");
        }
        clearExistingData(TEST_SOURCE_PATH);
    }
    
    // Equivalence Partition Case 
    @Test
    public void testExecuteUndo() {
        try {
            Logic logic = new Logic(TEST_SOURCE_PATH);
            fillSchedule(logic);
            
            UIFeedback fb = logic.execute(INPUT_VIEW);
            logic.setViewMapping(fb.getViewState().getFloatingTasks());
            logic.execute(INPUT_MARK_COMPLETE);
            fb = logic.execute(INPUT_UNDO);
            
            String expectedMsg = "Mark Task: float2 undone.";
            assertEquals(fb.getMessage(), expectedMsg);
        } catch (Exception e) {
            System.out.println(e.toString());
            fail("Exception!");
        }
        clearExistingData(TEST_SOURCE_PATH);
    }
    
    // Equivalence Partition Case 
    @Test
    public void testExecuteRedo() {
        try {
            Logic logic = new Logic(TEST_SOURCE_PATH);
            fillSchedule(logic);
            
            UIFeedback fb = logic.execute(INPUT_VIEW);
            logic.setViewMapping(fb.getViewState().getFloatingTasks());
            logic.execute(INPUT_MARK_COMPLETE);
            logic.execute(INPUT_UNDO);
            fb = logic.execute(INPUT_REDO);
            
            String expectedMsg = "Mark Task: float2 redone.";
            assertEquals(fb.getMessage(), expectedMsg);
        } catch (Exception e) {
            System.out.println(e.toString());
            fail("Exception!");
        }
        clearExistingData(TEST_SOURCE_PATH);
    }
    
    // Equivalence Partition Case 
    @Test
    public void testExecuteExit() {
        try {
            Logic logic = new Logic(TEST_SOURCE_PATH);
            UIFeedback actual = logic.execute(INPUT_EXIT);
            
            assertTrue(actual.isAnExit());
                
        } catch (Exception e) {
            System.out.println(e.toString());
            fail("Exception!");
        }
        clearExistingData(TEST_SOURCE_PATH);
    }
    
    // Equivalence Partition Case 
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

    // Tests the view command and correctness of task sorting in ViewState
    @Test
    public void testView() { 

        try {
            Logic logic = new Logic(TEST_SOURCE_PATH);
            UIFeedback feedback;
            ViewState actualVS;
            ViewState expectedVS;

            fillSchedule(logic); 
            // Equivalence Partition Case - View Completed Tasks
            feedback = logic.execute(INPUT_VIEW_COMPLETED); 

            actualVS = feedback.getViewState();
            expectedVS = new ViewState();
            expectedVS.getFloatingTasks().add(float1);

            System.out.println("Equivalence Partition Case - View Completed Tasks: " 
                                + isEqualViewState(actualVS, expectedVS));
            //assertTrue(isEqualViewState(actualVS, expectedVS));


            // Equivalence Partition Case - View Incomplete Tasks
            feedback = logic.execute(INPUT_VIEW_INCOMPLETED);
            actualVS = feedback.getViewState();
            expectedVS = new ViewState();
            expectedVS.getFloatingTasks().add(float2); 
            
            expectedVS.getEventTasks().add(event2);
            expectedVS.getEventTasks().add(event3);
            expectedVS.getEventTasks().add(event1);          
            expectedVS.getEventTasks().add(event4);
            expectedVS.getNormalTasks().add(task1);
            expectedVS.getNormalTasks().add(task2);
            
            System.out.println("Equivalence Partition Case - View Incomplete Tasks: " 
                                + isEqualViewState(actualVS, expectedVS));
            assertTrue(isEqualViewState(actualVS, expectedVS));


            // Equivalence Partition Case - View tasks on Date (with matches)
            feedback = logic.execute(INPUT_VIEW_ON_EXIST);

            actualVS = feedback.getViewState();
            expectedVS = new ViewState();
            expectedVS.getEventTasks().add(event2);
            expectedVS.getEventTasks().add(event3);
            expectedVS.getEventTasks().add(event1);

            System.out.println("Equivalence Partition Case - View tasks on Date (with matches): "
                                + isEqualViewState(actualVS, expectedVS));
            assertTrue(isEqualViewState(actualVS, expectedVS));


            // Equivalence Partition Case - View tasks on Date (without matches)
            feedback = logic.execute(INPUT_VIEW_ON_NONEXIST);
            actualVS = feedback.getViewState();
            expectedVS = new ViewState();

            System.out.println("Equivalence Partition Case - View tasks on Date (without matches): "
                                + isEqualViewState(actualVS, expectedVS));
            assertTrue(isEqualViewState(actualVS, expectedVS));


            // Equivalence Partition Case - View tasks by Date (with matches)
            feedback = logic.execute(INPUT_VIEW_BY_EXIST);

            actualVS = feedback.getViewState();
            expectedVS = new ViewState();
            expectedVS.getNormalTasks().add(task1);
            expectedVS.getNormalTasks().add(task2);

            System.out.println("Equivalence Partition Case - View tasks by Date (with matches): "
                                + isEqualViewState(actualVS, expectedVS));
            assertTrue(isEqualViewState(actualVS, expectedVS));


            // Equivalence Partition Case - View tasks by Date (without matches)
            feedback = logic.execute(INPUT_VIEW_BY_NONEXIST);
            actualVS = feedback.getViewState();
            expectedVS = new ViewState();

            System.out.println("Equivalence Partition Case - View tasks by Date (without matches): "
                                + isEqualViewState(actualVS, expectedVS));
            assertTrue(isEqualViewState(actualVS, expectedVS));


            // Equivalence Partition and Boundary Case - View tasks from to
            // (with matches, time not specified, and dates are at the boundary
            // of the date range of the schedule)
            feedback = logic.execute(INPUT_VIEW_FROM_TO_EXIST_NO_TIME);

            actualVS = feedback.getViewState();
            expectedVS = new ViewState();
            expectedVS.getEventTasks().add(event2);
            expectedVS.getEventTasks().add(event3);
            expectedVS.getEventTasks().add(event1);
            expectedVS.getEventTasks().add(event4);
            expectedVS.getNormalTasks().add(task1);
            expectedVS.getNormalTasks().add(task2);

            System.out.println("Equivalence Partition and Boundary Case - View tasks from to "
                                + "(with matches, time not specified, and dates are at the boundary "
                                + "of the date range of the schedule): " + isEqualViewState(actualVS, expectedVS));
            assertTrue(isEqualViewState(actualVS, expectedVS));


            // Equivalence Partition Case - View tasks from to (date not
            // specified)
            feedback = logic.execute(INPUT_VIEW_FROM_TO_EXIST_NO_DATE);
            actualVS = feedback.getViewState();
            expectedVS = new ViewState();

            System.out.println("Equivalence Partition Case - View tasks from to (date not specified): "
                                + isEqualViewState(actualVS, expectedVS));
            assertTrue(isEqualViewState(actualVS, expectedVS));


            // Boundary Case - View tasks from to (using dates beyond the
            // boundary of the tasks in schedule)
            feedback = logic.execute(INPUT_VIEW_FROM_NONEXIST_TO_NONEXIST);
            actualVS = feedback.getViewState();
            expectedVS = new ViewState();

            System.out.println("Boundary Case - View tasks from to "
                                + "(using dates beyond the boundary of the tasks in schedule): "
                                + isEqualViewState(actualVS, expectedVS));
            assertTrue(isEqualViewState(actualVS, expectedVS));

            // Equivalence Partition - View Empty Schedule
            clearExistingData(TEST_SOURCE_PATH);
            logic = new Logic(TEST_SOURCE_PATH);
            actualVS = feedback.getViewState();
            expectedVS = new ViewState();

            System.out.println("Equivalence Partition Case - View tasks from an "
                    + "empty schedule: "
                    + isEqualViewState(actualVS, expectedVS));
            assertTrue(isEqualViewState(actualVS, expectedVS));

        } catch (Exception e) {
            System.out.println(e.toString());
            fail("Exception!");
        }
        
        clearExistingData(TEST_SOURCE_PATH);
    } 
    
    @Test
    public void testFind() {
        
        try {
            Logic logic = new Logic(TEST_SOURCE_PATH);
            UIFeedback feedback;
            ViewState actualVS;
            ViewState expectedVS;
            Task t; 
            
                       
            // Equivalence Partition Case - Search for task by keyword in an empty list
            feedback = logic.execute(INPUT_VIEW_INCOMPLETED);
            actualVS = feedback.getViewState();
            expectedVS = new ViewState();
           
            System.out.println("Equivalence Partition Case - Search in empty schedule: " 
                                + isEqualViewState(actualVS, expectedVS));
            assertTrue(isEqualViewState(actualVS, expectedVS));

            fillSchedule(logic);
            
            // Search keyword that exist
            feedback = logic.execute(INPUT_FIND_KEYWORD_EXIST);
            actualVS = feedback.getViewState();
            expectedVS = new ViewState();
            expectedVS.getNormalTasks().add(task1);
            expectedVS.getNormalTasks().add(task2);

            System.out.println("Equivalence Partition Case - Search keyword that exists: " 
                                + isEqualViewState(actualVS, expectedVS));
            assertTrue(isEqualViewState(actualVS, expectedVS));

            // Equivalence Partition Case - Search keyword that doesn't exist
            feedback = logic.execute(INPUT_FIND_KEYWORD_NONEXIST);
            actualVS = feedback.getViewState();
            expectedVS = new ViewState();

            System.out.println("Equivalence Partition Case - Search keyword that doesn't exist: " 
                                + isEqualViewState(actualVS, expectedVS));
            assertTrue(isEqualViewState(actualVS, expectedVS));

            
            // Equivalence Partition Case - Search keyword that is a substring of word that matches
            feedback = logic.execute(INPUT_FIND_KEYWORD_EXIST_SUBSTRING);
            actualVS = feedback.getViewState();
            expectedVS = new ViewState();

            System.out.println("Equivalence Partition Case - Search keyword that is a substring of word "
                                + "that matches: " + isEqualViewState(actualVS, expectedVS));
            assertTrue(isEqualViewState(actualVS, expectedVS));

            
            // Equivalence Partition Case - Test the case insensitivity of search function
            feedback = logic.execute(INPUT_FIND_KEYWORD_EXIST_UPPERCASE);
            actualVS = feedback.getViewState();
            expectedVS = new ViewState();
            expectedVS.getNormalTasks().add(task1);

            System.out.println("Equivalence Partition Case - Test the case insensitivity of search "
                                + "function: " + isEqualViewState(actualVS, expectedVS));
            assertTrue(isEqualViewState(actualVS, expectedVS));
                        
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception!");
        }
        
        clearExistingData(TEST_SOURCE_PATH);
    }
}

