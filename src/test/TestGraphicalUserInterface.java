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
import katnote.ui.TaskViewGroup;

public class TestGraphicalUserInterface extends GuiTest {
    
    @Test
    public void TestAddingTasks(){
        //let the application load
        sleep(1, TimeUnit.SECONDS);
            
        click("#commandInputBox").type("add do homework by 30/10/2015 3pm").press(KeyCode.ENTER).release(KeyCode.ENTER);
        
        VBox taskGroupList = (VBox)find("#TaskList");
        ObservableList<Node> nodes = taskGroupList.getChildren();
        TaskViewGroup restGroup = (TaskViewGroup)nodes.get(0);
        assertEquals(restGroup.getGroupHeaderText(), "The Rest");
        TaskDetailedRow testRow = (TaskDetailedRow)restGroup.getListChildren().getChildren().get(0);
        assertEquals(testRow.getDescription(), "1. do homework");
        
        click("#commandInputBox").type("add do homework1 by today").press(KeyCode.ENTER).release(KeyCode.ENTER);
        
        taskGroupList = (VBox)find("#TaskList");
        nodes = taskGroupList.getChildren();
        TaskViewGroup todayGroup = (TaskViewGroup)nodes.get(0);
        assertEquals(todayGroup.getGroupHeaderText(), "Today");
        testRow = (TaskDetailedRow)todayGroup.getListChildren().getChildren().get(0);
        assertEquals(testRow.getDescription(), "1. do homework1");
        
        click("#commandInputBox").type("add do homework2 by tomorrow 5pm").press(KeyCode.ENTER).release(KeyCode.ENTER);
        
        taskGroupList = (VBox)find("#TaskList");
        nodes = taskGroupList.getChildren();
        TaskViewGroup tmrGroup = (TaskViewGroup)nodes.get(1);
        assertEquals(tmrGroup.getGroupHeaderText(), "Tomorrow");
        testRow = (TaskDetailedRow)tmrGroup.getListChildren().getChildren().get(0);
        assertEquals(testRow.getDescription(), "2. do homework2");
        
        click("#commandInputBox").type("add do homework3 by 25/10/2015 10am").press(KeyCode.ENTER).release(KeyCode.ENTER);
        
        taskGroupList = (VBox)find("#TaskList");
        nodes = taskGroupList.getChildren();
        restGroup = (TaskViewGroup)nodes.get(2);
        assertEquals(restGroup.getGroupHeaderText(), "The Rest");
        testRow = (TaskDetailedRow)restGroup.getListChildren().getChildren().get(0);
        assertEquals(testRow.getDescription(), "3. do homework3");
    }
    
    @Override
    protected Parent getRootNode() {
        return null;
    }

    @Override
    public void setupStage() throws Throwable {
        File f = new File("data.txt");
        f.delete();
        new Thread(() -> GraphicalUserInterface.launch(GraphicalUserInterface.class))
            .start();
    }
}
