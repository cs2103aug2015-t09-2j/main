//@@author A0125447E
package test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Test;
import org.loadui.testfx.GuiTest;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import katnote.ui.GraphicalUserInterface;
import katnote.ui.TaskDetailedRow;
import katnote.ui.TaskRow;
import katnote.ui.TaskViewGroup;
import test.ViewDataPackage.TaskGroupPackage;

public class TestGraphicalUserInterface extends GuiTest {
    private static final String GROUP_TITLE_FLOATING_TASKS = "Task to do";
    private static final String TEST_FILE_PATH = "TestFiles/SystemTestFiles/";
    private TextField commandInput;

    public void verifyTaskGroup(TaskGroupPackage expectedDataSet, TaskViewGroup taskGroup) {
        String groupHeader = taskGroup.getGroupHeaderText();
        assertEquals(groupHeader, expectedDataSet.getGroupHeader());
        int noOfChildren = taskGroup.getListChildren().getChildren().size();

        String[] expectedTaskDescription = expectedDataSet.getTaskDesciptions();
        String[] expectedDateString = expectedDataSet.getDateString();
        String[] expectedIndexString = expectedDataSet.getIndexString();
        assertEquals(expectedTaskDescription.length, noOfChildren);
        ObservableList<Node> list = taskGroup.getListChildren().getChildren();

        if (groupHeader.equals(GROUP_TITLE_FLOATING_TASKS)) {
            for (int i = 0; i < noOfChildren; i++) {
                TaskRow row = (TaskRow) list.get(i);
                assertEquals(expectedTaskDescription[i], row.getDescription());
            }

        } else {
            for (int i = 0; i < noOfChildren; i++) {
                TaskDetailedRow row = (TaskDetailedRow) list.get(i);
                assertEquals(expectedTaskDescription[i], row.getDescription());
                assertEquals(expectedDateString[i], row.getDateString());
                assertEquals(expectedIndexString[i], row.getIndexString());
            }
        }

    }

    public void verifyTaskGroupList(TaskGroupPackage[] testDataPackageArray, VBox taskGroupList) {
        ObservableList<Node> nodes = taskGroupList.getChildren();
        assertEquals(testDataPackageArray.length, nodes.size());
        for (int i = 0; i < nodes.size(); i++) {
            TaskViewGroup viewGroup = (TaskViewGroup) nodes.get(i);
            verifyTaskGroup(testDataPackageArray[i], viewGroup);
        }
    }

    public void verifyView(ViewDataPackage data) {
        Label responseLabel = (Label) find("#responseLabel");
        VBox taskGroupList = (VBox) find("#TaskList");
        assertEquals(data.getResponse(), responseLabel.getText());
        verifyTaskGroupList(data.getViewList(), taskGroupList);
    }

    @Test
    public void systemTest() {
        SystemTestDataParser testData = new SystemTestDataParser();
        ArrayList<String> inputs = testData.getInputs();
        ArrayList<ViewDataPackage> outputs = testData.getOutputs();

        assertEquals(inputs.size(), outputs.size());

        // mouse test
        String input = inputs.get(0);
        ViewDataPackage data = outputs.get(0);
        commandInput.setText(input);
        System.out.println(String.format("Testing: \"%s\"", input));
        click("#inputButton");
        click("#commandInputBox");

        for (int i = 1; i < inputs.size(); i++) {
            input = inputs.get(i);
            data = outputs.get(i);
            // type(input).push(KeyCode.ENTER);
            commandInput.setText(input);
            System.out.println(String.format("Testing: \"%s\"", input));
            sleep(250, TimeUnit.MILLISECONDS);
            push(KeyCode.ENTER);
            sleep(250, TimeUnit.MILLISECONDS);

            verifyView(data);
        }

    }

    @Override
    protected Parent getRootNode() {
        return null;
    }

    @After
    public void cleanUp() {
        File f = new File(TEST_FILE_PATH + "data.txt");
        if (f.exists()) {
            f.delete();
        }
    }

    @Override
    public void setupStage() throws Throwable {
        GraphicalUserInterface.configureTestMode(true, TEST_FILE_PATH);

        new Thread(() -> {
            GraphicalUserInterface.launch(GraphicalUserInterface.class);
        }).start();
        // let the application load
        sleep(2, TimeUnit.SECONDS);
        click("#commandInputBox");
        commandInput = (TextField) find("#commandInputBox");

    }
}
