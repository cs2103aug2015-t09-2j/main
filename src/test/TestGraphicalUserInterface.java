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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import katnote.ui.CommandBarController;
import katnote.ui.GraphicalUserInterface;
import katnote.ui.TaskDetailedRow;
import katnote.ui.TaskRow;
import katnote.ui.TaskViewGroup;
import test.ViewDataPackage.TaskGroupPackage;

public class TestGraphicalUserInterface extends GuiTest {
    private static final String GROUP_TITLE_FLOATING_TASKS = "Task to do";
    TextField commandInput;
    GraphicalUserInterface app;     
    
    public void verifyTaskGroup(TaskGroupPackage expectedDataSet, TaskViewGroup taskGroup){
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

    public void verifyTaskGroupList(TaskGroupPackage[] testDataPackageArray, VBox taskGroupList){
        ObservableList<Node> nodes = taskGroupList.getChildren();
        assertEquals(testDataPackageArray.length, nodes.size());
        for(int i = 0; i < nodes.size(); i++){
            TaskViewGroup viewGroup = (TaskViewGroup) nodes.get(i);
            verifyTaskGroup(testDataPackageArray[i], viewGroup);
        }        
    }
    
    public void verifyView(ViewDataPackage data){
        Label responseLabel = (Label) find("#responseLabel");
        VBox taskGroupList = (VBox) find("#TaskList");
        assertEquals(data.getResponse(), responseLabel.getText());
        verifyTaskGroupList(data.getViewList(), taskGroupList);
    }

    @Test
    public void systemTest(){
        SystemTestDataParser testData = new SystemTestDataParser();
        ArrayList<String> inputs = testData.getInputs();
        ArrayList<ViewDataPackage> outputs = testData.getOutputs();
        
        assertEquals(inputs.size(), outputs.size());
        for(int i = 0; i < inputs.size(); i++){
            String input = inputs.get(i);
            ViewDataPackage data = outputs.get(i);
            //type(input).push(KeyCode.ENTER);
            commandInput.setText(input);
            sleep(1, TimeUnit.SECONDS);
            push(KeyCode.ENTER);
            
            verifyView(data);            
        }
        
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
