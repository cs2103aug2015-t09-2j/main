//@@author A0125447E
package katnote.ui;

import katnote.KatNoteLogger;
import katnote.logic.Logic;
import katnote.logic.UIFeedback;
import katnote.logic.ViewState;
import katnote.task.Task;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

public class GraphicalUserInterface extends Application {
    private static GraphicalUserInterface instance;
    private static final Logger log = KatNoteLogger.getLogger(GraphicalUserInterface.class.getName());
    private static final String CORE_LAYOUT_FXML = "/katnote/resources/ui/CoreLayout.fxml";
    private static final String SPLASH_LAYOUT_FXML = "/katnote/resources/ui/SplashLayout.fxml";

    private StackPane rootLayout;
    private BorderPane coreLayout;
    private BorderPane splashLayout;
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

    public static GraphicalUserInterface getInstance() {
        if (instance == null) {
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

    private void loadResources() {
        log.log(Level.INFO, "loading resources");
        Font.loadFont(getClass().getResource("/katnote/resources/ui/font/sen/sen-extrabold.otf").toExternalForm(), 10);
        Font.loadFont(getClass().getResource("/katnote/resources/ui/font/sen/sen-bold.otf").toExternalForm(), 10);
    }

    private void initialize(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("KatNote");
        this.primaryStage.getIcons().add(new Image("/katnote/resources/ui/Kat.png"));
        try {
            logic = new Logic();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void initRootLayout() {
        log.log(Level.FINE, "initilizing rootLayout");

        loadSplash();
        loadCoreLayout();
        Scene scene = setUpScene();
        setUpStage(scene);

        // to prevent focus on the commandBar
        splashLayout.requestFocus();
    }

    public void loadSplash() {
        log.log(Level.FINE, "initilizing rootLayout");
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(SPLASH_LAYOUT_FXML));
            splashLayout = (BorderPane) loader.load();

            splashLayout.addEventFilter(KeyEvent.KEY_RELEASED, event -> hideSplash());
            splashLayout.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> hideSplash());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideSplash() {
        rootLayout.getChildren().remove(splashLayout);
        splashLayout = null;
    }

    private void loadCoreLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(CORE_LAYOUT_FXML));
            coreLayout = (BorderPane) loader.load();
            commandBarController = loader.getController();
            commandBarController.setMainUI(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Scene setUpScene() {
        assert(coreLayout != null);
        assert(splashLayout != null);

        rootLayout = new StackPane();
        rootLayout.getChildren().add(coreLayout);
        rootLayout.getChildren().add(splashLayout);

        Scene scene = new Scene(rootLayout);
        return scene;
    }

    private void setUpStage(Scene scene) {
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public void setUpTaskViewer() {
        log.log(Level.INFO, "setUpTaskViewer");
        taskViewer = new TaskViewer();
        coreLayout.setCenter(taskViewer);
        displayedTaskList = updateTaskViewer(logic.getInitialViewState(), false);
        logic.setViewMapping(displayedTaskList);
    }

    public ArrayList<Task> updateTaskViewer(ViewState viewState, boolean isSearch) {
        log.log(Level.INFO, "updateTaskViewer");
        TaskViewFormatter listFormat = new TaskViewFormatter(viewState, isSearch);
        taskViewer.loadTaskFormat(listFormat);
        return listFormat.getOrderedTaskList();
    }

    public void handleCommandInput(String inputText) {
        log.log(Level.FINE, "input recieved: " + inputText);
        try {
            UIFeedback feedback = logic.execute(inputText);
            boolean isErrorResponse = feedback.isAnError();
            commandBarController.setResponseText(feedback.getMessage(), isErrorResponse);
            if (feedback.isAnExit()) {
                primaryStage.close();
            } else if (!isErrorResponse) {
                processViewState(feedback);
            }
        } catch (Exception e) {
            log.log(Level.WARNING, "Exception: " + e.getMessage());
            commandBarController.setErrorText(e.getMessage());
            e.printStackTrace();
        }

    }

    private void processViewState(UIFeedback feedback) {
        ViewState viewState = feedback.getViewState();
        if (viewState != null) {
            displayedTaskList = updateTaskViewer(viewState, feedback.isASearch());
            logic.setViewMapping(displayedTaskList);
        }
    }

}
