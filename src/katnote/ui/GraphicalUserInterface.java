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
    private static GraphicalUserInterface instance;
    private static final Logger log = KatNoteLogger.getLogger(GraphicalUserInterface.class.getName());
    private static final String ROOT_LAYOUT_FXML = "/katnote/resources/ui/RootLayout.fxml";

    private BorderPane rootLayout;
    private Stage primaryStage;
    private Logic logic;
    private TaskViewer taskViewer;
    private ArrayList<Task> displayedTaskList;
    private CommandBarController commandBarController;

    public ArrayList<Task> getDisplayedTaskList() {
        return displayedTaskList;
    }

    public void start(String[] args) {
        launch(args);
    }    
    
    public static GraphicalUserInterface getInstance(){
        if(instance == null){
            throw new NullPointerException("App not initialized");
        }
        return instance;
    }

    public CommandBarController getCommandController() {
        return commandBarController;
    }

    @Override
    public void start(Stage primaryStage) {
        instance = this;
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
            commandBarController = loader.getController();
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
        log.log(Level.INFO, "setUpTaskViewer");
        taskViewer = new TaskViewer();
        rootLayout.setCenter(taskViewer);
        displayedTaskList = updateTaskViewer(logic.getInitialViewState());
        logic.setViewMapping(displayedTaskList);
    }

    public ArrayList<Task> updateTaskViewer(ViewState viewState) {
        log.log(Level.INFO, "updateTaskViewer");
        TaskViewFormatter listFormat = new TaskViewFormatter(viewState);
        taskViewer.loadTaskFormat(listFormat);
        return listFormat.getOrderedTaskList();
    }

    public void handleCommandInput(String inputText) {
        log.log(Level.FINE, "input recieved: " + inputText);
        UIFeedback feedback;
        try {
            feedback = logic.execute(inputText);
            commandBarController.setResponseText(feedback.getMessage(), feedback.isAnError());
            if (feedback.isAnExit()) {
                primaryStage.close();
            } else if (!feedback.isAnError()) {
                ViewState viewState = feedback.getViewState();
                if (viewState != null) {
                    displayedTaskList = updateTaskViewer(viewState);
                    logic.setViewMapping(displayedTaskList);
                }
            }
        } catch (Exception e) {
            log.log(Level.WARNING, "Exception: " + e.getMessage());
            commandBarController.setErrorText(e.getMessage());
            e.printStackTrace();
        }

    }

}
