package test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.loadui.testfx.GuiTest;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import katnote.ui.CommandBarController;
import katnote.ui.GraphicalUserInterface;
import katnote.ui.TaskDetailedRow;
import katnote.ui.TaskRow;
import katnote.ui.TaskViewGroup;

public class TestGraphicalUserInterface extends GuiTest {
    private static final String GROUP_TITLE_FLOATING_TASKS = "Task to do";
    TextField commandInput;
    GraphicalUserInterface app;
    
    public static class TaskGroupPackage {
        private String groupHeader;
        private String[] taskDescription;
        private String[] dateString;
        private String[] indexString;
        
        public TaskGroupPackage(String groupHeader, String[] taskDescription, String[] dateString, String[] indexString){
            this.groupHeader = groupHeader;
            this.taskDescription = taskDescription;
            this.dateString = dateString;
            this.indexString = indexString;
        }
        
        public String getGroupHeader(){
            return groupHeader;
        }
        public String[] getTaskDesciptions(){
            return taskDescription;
        }
        public String[] getDateString(){
            return dateString;
        }
        public String[] getIndexString(){
            return indexString;
        }
        
    }
    
    public void verifyTaskGroup(TaskViewGroup taskGroup, TaskGroupPackage expectedDataSet){
        String groupHeader = taskGroup.getGroupHeaderText();
        assertEquals(groupHeader, expectedDataSet.getGroupHeader());
        int noOfChildren = taskGroup.getListChildren().getChildren().size();
        
        String[] expectedTaskDescription = expectedDataSet.getTaskDesciptions();
        String[] expectedDateString = expectedDataSet.getDateString();
        String[] expectedIndexString = expectedDataSet.getIndexString();
        assertEquals(expectedTaskDescription.length, noOfChildren);
        ObservableList<Node> list = taskGroup.getListChildren().getChildren();
        
        if(groupHeader.equals(GROUP_TITLE_FLOATING_TASKS)){
            for(int i = 0; i < noOfChildren; i++){
                TaskRow row = (TaskRow)list.get(i);
                assertEquals(expectedTaskDescription[i], row.getDescription());
            }     
            
        } else {
            for(int i = 0; i < noOfChildren; i++){
                TaskDetailedRow row = (TaskDetailedRow)list.get(i);
                assertEquals(expectedTaskDescription[i], row.getDescription());
                assertEquals(expectedDateString[i], row.getDateString());
                assertEquals(expectedIndexString[i], row.getIndexString());
            }               
        }
             
    }

    public void verifyTaskGroupList(VBox taskGroupList, TaskGroupPackage[] testDataPackageArray){
        ObservableList<Node> nodes = taskGroupList.getChildren();
        for(int i = 0; i < nodes.size(); i++){
            TaskViewGroup viewGroup = (TaskViewGroup) nodes.get(i);
            verifyTaskGroup(viewGroup, testDataPackageArray[i]);
        }        
    }

    @Test
    public void systemTest(){
        SystemTestDataParser testData = new SystemTestDataParser();
        ArrayList<String> inputs = testData.getInputs();
        ArrayList<TaskGroupPackage[]> outputs = testData.getOutputs();
        
        assertEquals(inputs.size(), outputs.size());
        for(int i = 0; i < inputs.size(); i++){
            String input = inputs.get(i);
            TaskGroupPackage[] testDataPackageArray = outputs.get(i);
            //type(input).push(KeyCode.ENTER);
            commandInput.setText(input);
            sleep(1, TimeUnit.SECONDS);
            push(KeyCode.ENTER);
            
            
            VBox taskGroupList = (VBox) find("#TaskList");
            verifyTaskGroupList(taskGroupList, testDataPackageArray); 
        }
        
    }
    
/*    public void testAddingTasks() {
        TaskGroupPackage testDataPackage1, testDataPackage2, testDataPackage3, testDataPackage4;
        
        //absolute
        type("add do homework by 30/10/2015").push(KeyCode.ENTER);
        testDataPackage1 = new TaskGroupPackage("Friday      30 Oct 15", 
                new String[]{"1. do homework"}, 
                new String[]{"Due: 11:59 PM"});
        TaskGroupPackage[] testDataPackageArray = { testDataPackage1 };
        VBox taskGroupList = (VBox) find("#TaskList");
        verifyTaskGroupList(taskGroupList, testDataPackageArray);   

        //relative: today
        commandInput.setText("add do homework1 by today");
        push(KeyCode.ENTER);
        testDataPackage1 = new TaskGroupPackage("Today", 
                new String[]{"1. do homework1"}, 
                new String[]{"Due: 11:59 PM"});        
        testDataPackage2 = new TaskGroupPackage("Friday      30 Oct 15", 
                new String[]{"2. do homework"}, 
                new String[]{"Due: 11:59 PM"});        
        testDataPackageArray = new TaskGroupPackage[]{ testDataPackage1, testDataPackage2 };
        taskGroupList = (VBox) find("#TaskList");
        verifyTaskGroupList(taskGroupList, testDataPackageArray); 

        //relative: tomorrow with time
        commandInput.setText("add do homework2 by tomorrow 5pm");
        push(KeyCode.ENTER);   
        testDataPackage1 = new TaskGroupPackage("Today", 
                new String[]{"1. do homework1"}, 
                new String[]{"Due: 11:59 PM"});        
        testDataPackage2 = new TaskGroupPackage("Tomorrow", 
                new String[]{"2. do homework2"}, 
                new String[]{"Due: 05:00 PM"});        
        testDataPackage3 = new TaskGroupPackage("Friday      30 Oct 15", 
                new String[]{"3. do homework"}, 
                new String[]{"Due: 11:59 PM"});
        testDataPackageArray = new TaskGroupPackage[]{ testDataPackage1, testDataPackage2, testDataPackage3 };
        taskGroupList = (VBox) find("#TaskList");
        verifyTaskGroupList(taskGroupList, testDataPackageArray);   
        
        //absolute with time
        commandInput.setText("add do homework3 by 30/10/2015 10am");
        push(KeyCode.ENTER);
        testDataPackage1 = new TaskGroupPackage("Today", 
                new String[]{"1. do homework1"}, 
                new String[]{"Due: 11:59 PM"});        
        testDataPackage2 = new TaskGroupPackage("Tomorrow", 
                new String[]{"2. do homework2"}, 
                new String[]{"Due: 05:00 PM"});        
        testDataPackage3 = new TaskGroupPackage("Friday      30 Oct 15", 
                new String[]{"3. do homework3", "4. do homework"}, 
                new String[]{"Due: 10:00 AM", "Due: 11:59 PM"});
        testDataPackageArray = new TaskGroupPackage[]{ testDataPackage1, testDataPackage2, testDataPackage3 }; 
        taskGroupList = (VBox) find("#TaskList");
        verifyTaskGroupList(taskGroupList, testDataPackageArray);    
        
        //floating
        commandInput.setText("add do homework4");
        push(KeyCode.ENTER);
        
        testDataPackage1 = new TaskGroupPackage("Task to do", 
                new String[]{"1. do homework4"}, 
                new String[]{});        
        testDataPackage2 = new TaskGroupPackage("Today", 
                new String[]{"2. do homework1"}, 
                new String[]{"Due: 11:59 PM"});         
        testDataPackage3 = new TaskGroupPackage("Tomorrow", 
                new String[]{"3. do homework2"}, 
                new String[]{"Due: 05:00 PM"});      
        testDataPackage4 = new TaskGroupPackage("Friday      30 Oct 15", 
                new String[]{"4. do homework3", "5. do homework"}, 
                new String[]{"Due: 10:00 AM", "Due: 11:59 PM"});
        testDataPackageArray = new TaskGroupPackage[]{ testDataPackage1, testDataPackage2, testDataPackage3, testDataPackage4 }; 
        taskGroupList = (VBox) find("#TaskList");
        verifyTaskGroupList(taskGroupList, testDataPackageArray); 
        
        sleep(1, TimeUnit.SECONDS);    
    }    
    
    public void testEditingTasks() {
        TaskGroupPackage testDataPackage1, testDataPackage2, testDataPackage3, testDataPackage4;
        TaskGroupPackage[] testDataPackageArray;
        VBox taskGroupList;
        
        //edit title
        type("edit 1 set task").push(KeyCode.SHIFT, KeyCode.MINUS).type("title study notes").push(KeyCode.ENTER);
        testDataPackage1 = new TaskGroupPackage("Task to do", 
                new String[]{"1. study notes"}, 
                new String[]{});        
        testDataPackage2 = new TaskGroupPackage("Today", 
                new String[]{"2. do homework1"}, 
                new String[]{"Due: 11:59 PM"});         
        testDataPackage3 = new TaskGroupPackage("Tomorrow", 
                new String[]{"3. do homework2"}, 
                new String[]{"Due: 05:00 PM"});      
        testDataPackage4 = new TaskGroupPackage("Friday      30 Oct 15", 
                new String[]{"4. do homework3", "5. do homework"}, 
                new String[]{"Due: 10:00 AM", "Due: 11:59 PM"});
        testDataPackageArray = new TaskGroupPackage[]{ testDataPackage1, testDataPackage2, testDataPackage3, testDataPackage4 }; 
        taskGroupList = (VBox) find("#TaskList");
        verifyTaskGroupList(taskGroupList, testDataPackageArray);    
        
        //edit due date
        type("edit 5 set due").push(KeyCode.SHIFT, KeyCode.MINUS).type("time 1/11/15").push(KeyCode.ENTER);
        testDataPackage1 = new TaskGroupPackage("Task to do", 
                new String[]{"1. study notes"}, 
                new String[]{});        
        testDataPackage2 = new TaskGroupPackage("Today", 
                new String[]{"2. do homework1"}, 
                new String[]{"Due: 11:59 PM"});         
        testDataPackage3 = new TaskGroupPackage("Tomorrow", 
                new String[]{"3. do homework2"}, 
                new String[]{"Due: 05:00 PM"});      
        testDataPackage4 = new TaskGroupPackage("Friday      30 Oct 15", 
                new String[]{"4. do homework3"}, 
                new String[]{"Due: 10:00 AM"});
        TaskGroupPackage testDataPackage5 = new TaskGroupPackage("Sunday      01 Nov 15", 
                new String[]{"5. do homework"}, 
                new String[]{"Due: 11:59 PM"});
        testDataPackageArray = new TaskGroupPackage[]{ testDataPackage1, testDataPackage2, testDataPackage3, testDataPackage4, testDataPackage5 }; 
        taskGroupList = (VBox) find("#TaskList");
        verifyTaskGroupList(taskGroupList, testDataPackageArray);        
        
        sleep(1, TimeUnit.SECONDS);    
    }
    
    public void testMarkingTasksDone(){
        TaskGroupPackage testDataPackage1, testDataPackage2, testDataPackage3, testDataPackage4;
        TaskGroupPackage[] testDataPackageArray;
        VBox taskGroupList;

        
        //edit title
        type("mark 2 done").push(KeyCode.ENTER);
        testDataPackage1 = new TaskGroupPackage("Task to do", 
                new String[]{"1. study notes"}, 
                new String[]{});                
        testDataPackage2 = new TaskGroupPackage("Tomorrow", 
                new String[]{"2. do homework2"}, 
                new String[]{"Due: 05:00 PM"});      
        testDataPackage3 = new TaskGroupPackage("Friday      30 Oct 15", 
                new String[]{"3. do homework3"}, 
                new String[]{"Due: 10:00 AM"});
        testDataPackage4 = new TaskGroupPackage("Sunday      01 Nov 15",
                new String[]{"4. do homework"}, 
                new String[]{"Due: 11:59 PM"});
        testDataPackageArray = new TaskGroupPackage[]{ testDataPackage1, testDataPackage2, testDataPackage3, testDataPackage4 }; 
        taskGroupList = (VBox) find("#TaskList");
        verifyTaskGroupList(taskGroupList, testDataPackageArray);  
        
        type("view tasks completed").push(KeyCode.ENTER);
        testDataPackage1 = new TaskGroupPackage("Today", 
                new String[]{"1. do homework1"}, 
                new String[]{"Due: 11:59 PM"}); 
        testDataPackageArray = new TaskGroupPackage[]{ testDataPackage1 }; 
        taskGroupList = (VBox) find("#TaskList");
        verifyTaskGroupList(taskGroupList, testDataPackageArray);     
        
        sleep(1, TimeUnit.SECONDS);  
        
    }
*/
    @Override
    protected Parent getRootNode() {
        return null;
    }

    @Override
    public void setupStage() throws Throwable {
        // reset data
        File f = new File("data.txt");
        f.delete();
        new Thread(() -> {
            GraphicalUserInterface.launch(GraphicalUserInterface.class);
        }).start();
        // let the application load
        sleep(2, TimeUnit.SECONDS);        
        click("#commandInputBox");
        commandInput = (TextField)find("#commandInputBox");
        
        app = GraphicalUserInterface.getInstance();
    }
}
