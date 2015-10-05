package katnote.ui;

import katnote.Logic;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;

public class GraphicalUserInterface extends Application  {
	private BorderPane rootLayout;
	private Stage primaryStage;	
	private Logic logic;
	private TaskViewer taskViewer;

	@Override
	public void start(Stage primaryStage) {
	    Font f = Font.loadFont(
	            getClass().getResource("/katnote/resources/ui/LT.ttf").toExternalForm(), 
	            10
	          );
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("KatNote");
		logic = new Logic();
		
		initRootLayout();
		addTaskViewer();
		updateTaskViewer();
        updateTaskViewer();
        updateTaskViewer();
        updateTaskViewer();
        updateTaskViewer();
        updateTaskViewer();
        updateTaskViewer();
        updateTaskViewer();
	}
	public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/katnote/resources/ui/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();
            CommandBarController commandBarController = loader.getController();
            commandBarController.setMainUI(this);

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }	
	
	public void addTaskViewer(){
	    taskViewer = new TaskViewer();
	    rootLayout.setCenter(taskViewer);
	}
	
	public void updateTaskViewer(){
	    String[] tasks = { "do A", "do B" };
	    taskViewer.addNewTaskViewGroup(new TaskViewGroup("today", tasks));
	}
	
	public void handleCommandInput(CommandBarController commandBarController, String inputText){
		String response = logic.execute(inputText);
		commandBarController.setResponseText(response);
	}
	

}
