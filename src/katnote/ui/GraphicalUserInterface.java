package katnote.ui;

import katnote.KatNoteLogger;
import katnote.Logic;
import katnote.UIFeedback;
import katnote.ViewState;
import katnote.task.Task;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;

public class GraphicalUserInterface extends Application {
    private static final boolean IS_GUI_FORMAT = true;
    private static final Logger log = KatNoteLogger.getLogger(GraphicalUserInterface.class.getName());
    private static final String ROOT_LAYOUT_FXML = "/katnote/resources/ui/RootLayout.fxml";

    private BorderPane rootLayout;
    private Stage primaryStage;
    private Logic logic;
    private TaskViewer taskViewer;

    @Override
    public void start(Stage primaryStage) {
        loadResources();
        initialize(primaryStage);

        initRootLayout();
        setUpTaskViewer();
    }

    private void initialize(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("KatNote");
        try {
            logic = new Logic();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void loadResources() {
        log.log(Level.INFO, "loading resources");
        Font.loadFont(getClass().getResource("/katnote/resources/ui/font/sen/sen-extrabold.otf").toExternalForm(), 10);
        Font.loadFont(getClass().getResource("/katnote/resources/ui/font/sen/sen-bold.otf").toExternalForm(), 10);
    }

    public void initRootLayout() {
        log.log(Level.FINE, "initilizing rootLayout");
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(ROOT_LAYOUT_FXML));
            rootLayout = (BorderPane) loader.load();
            CommandBarController commandBarController = loader.getController();
            commandBarController.setMainUI(this);

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUpTaskViewer() {
        taskViewer = new TaskViewer();
        rootLayout.setCenter(taskViewer);
        ArrayList<Task> listOfTaskMapping = updateTaskViewer(logic.getInitialViewState());
        logic.setViewMapping(listOfTaskMapping);
    }

    public ArrayList<Task> updateTaskViewer(ViewState viewState) {
        TaskViewFormatter listFormat = new TaskViewFormatter(viewState, IS_GUI_FORMAT);
        taskViewer.loadTaskFormat(listFormat);
        return listFormat.getOrderedTaskList();
    }

    public void handleCommandInput(CommandBarController commandBarController, String inputText) {
        UIFeedback feedback;
        try {
            feedback = logic.execute(inputText);
            commandBarController.setResponseText(feedback.getMessage(), feedback.isAnError());
            if(feedback.isAnExit()){
                primaryStage.close();
            } else if (!feedback.isAnError()) {
                ViewState viewState = feedback.getViewState();
                if (viewState != null) {
                    ArrayList<Task> listOfTaskMapping = updateTaskViewer(viewState);
                    logic.setViewMapping(listOfTaskMapping);
                }
            }
        } catch (Exception e) {
            commandBarController.setErrorText(e.getMessage());
            e.printStackTrace();
        }

    }

}
