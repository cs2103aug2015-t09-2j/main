package test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.loadui.testfx.GuiTest;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import katnote.ui.GraphicalUserInterface;
import katnote.ui.TaskDetailedRow;
import katnote.ui.TaskRow;
import katnote.ui.TaskViewGroup;

public class TestGraphicalUserInterface extends GuiTest {
    private static final String GROUP_TITLE_FLOATING_TASKS = "Task to do";

    class TaskGroupPackage {
        private String groupHeader;
        private String[] taskDescription;
        private String[] dateString;
        
        TaskGroupPackage(String groupHeader, String[] taskDescription, String[] dateString){
            this.groupHeader = groupHeader;
            this.taskDescription = taskDescription;
            this.dateString = dateString;
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
        
    }
    public void verifyTaskGroup(TaskViewGroup taskGroup, TaskGroupPackage expectedDataSet){
        String groupHeader = taskGroup.getGroupHeaderText();
        assertEquals(groupHeader, expectedDataSet.getGroupHeader());
        int noOfChildren = taskGroup.getListChildren().getChildren().size();
        
        String[] expectedTaskDescription = expectedDataSet.getTaskDesciptions();
        String[] expectedDateString = expectedDataSet.getDateString();
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
    public void TestAddingTasks() {
        // let the application load
        sleep(1, TimeUnit.SECONDS);
        
        TaskGroupPackage testDataPackage1 = new TaskGroupPackage("The Rest", 
                new String[]{"1. do homework"}, 
                new String[]{"Due: 30 Oct 15 03:00 PM"});
        TaskGroupPackage[] testDataPackageArray = { testDataPackage1 };

        click("#commandInputBox").type("add do homework by 30/10/2015 3pm").push(KeyCode.ENTER);

        VBox taskGroupList = (VBox) find("#TaskList");
        verifyTaskGroupList(taskGroupList, testDataPackageArray);        

        click("#commandInputBox").type("add do homework1 by today").push(KeyCode.ENTER);
        

        
        testDataPackage1 = new TaskGroupPackage("Today", 
                new String[]{"1. do homework1"}, 
                new String[]{"Due: 11:59 PM"});
        
        TaskGroupPackage testDataPackage2 = new TaskGroupPackage("The Rest", 
                new String[]{"2. do homework"}, 
                new String[]{"Due: 30 Oct 15 03:00 PM"});
        
        testDataPackageArray = new TaskGroupPackage[]{ testDataPackage1, testDataPackage2 };

        taskGroupList = (VBox) find("#TaskList");
        verifyTaskGroupList(taskGroupList, testDataPackageArray); 

        click("#commandInputBox").type("add do homework2 by tomorrow 5pm").push(KeyCode.ENTER);
        
        testDataPackage1 = new TaskGroupPackage("Today", 
                new String[]{"1. do homework1"}, 
                new String[]{"Due: 11:59 PM"});
        
        testDataPackage2 = new TaskGroupPackage("Tomorrow", 
                new String[]{"2. do homework2"}, 
                new String[]{"Due: 05:00 PM"});
        
        TaskGroupPackage testDataPackage3 = new TaskGroupPackage("The Rest", 
                new String[]{"3. do homework"}, 
                new String[]{"Due: 30 Oct 15 03:00 PM"});

        taskGroupList = (VBox) find("#TaskList");
        testDataPackageArray = new TaskGroupPackage[]{ testDataPackage1, testDataPackage2, testDataPackage3 };

        click("#commandInputBox").type("add do homework3 by 25/10/2015 10am").push(KeyCode.ENTER);

        testDataPackage1 = new TaskGroupPackage("Today", 
                new String[]{"1. do homework1"}, 
                new String[]{"Due: 11:59 PM"});
        
        testDataPackage2 = new TaskGroupPackage("Tomorrow", 
                new String[]{"2. do homework2"}, 
                new String[]{"Due: 05:00 PM"});
        
        testDataPackage3 = new TaskGroupPackage("The Rest", 
                new String[]{"3. do homework3", "4. do homework"}, 
                new String[]{"Due: 25 Oct 15 10:00 AM", "Due: 30 Oct 15 03:00 PM"});

        taskGroupList = (VBox) find("#TaskList");
        testDataPackageArray = new TaskGroupPackage[]{ testDataPackage1, testDataPackage2, testDataPackage3 };
    }

    @Override
    protected Parent getRootNode() {
        return null;
    }

    @Override
    public void setupStage() throws Throwable {
        // reset data
        File f = new File("data.txt");
        f.delete();
        new Thread(() -> GraphicalUserInterface.launch(GraphicalUserInterface.class)).start();
    }
}
