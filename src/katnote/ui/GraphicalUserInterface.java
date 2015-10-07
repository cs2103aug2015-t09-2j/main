package katnote.ui;

import katnote.Logic;
import katnote.Task;
import katnote.UIFeedback;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;

public class GraphicalUserInterface extends Application  {
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
        Font.loadFont(
	            getClass().getResource("/katnote/resources/ui/font/sen/sen-extrabold.otf").toExternalForm(), 
	            10
	          );
        Font f = Font.loadFont(
	                  getClass().getResource("/katnote/resources/ui/font/sen/sen-bold.otf").toExternalForm(), 
	                  10
	                );
    }
	public void initRootLayout() {
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
	
	public void setUpTaskViewer(){
	    taskViewer = new TaskViewer();
	    rootLayout.setCenter(taskViewer);
	}
	
	public void updateTaskViewer(Task[] tasks){
        taskViewer.clearViewer();
        taskViewer.loadDetailedListOfTask(tasks);
	}
	
	public void handleCommandInput(CommandBarController commandBarController, String inputText){
		UIFeedback feedback;
        try {
            feedback = logic.execute(inputText);
            commandBarController.setResponseText(feedback.getMessage(), feedback.isAnError()); 
            if(!feedback.isAnError()){       
                Task[] tasks = feedback.getTaskList().toArray(new Task[0]);
                if(tasks.length == 0){
                    updateTaskViewer(tasks);
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            commandBarController.setResponseText(e.getMessage(), true); 
        }
		
	}
	

}
