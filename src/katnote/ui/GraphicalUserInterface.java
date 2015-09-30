package katnote.ui;

import katnote.KatNote;
import katnote.Logic;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

public class GraphicalUserInterface extends Application  {
	private BorderPane rootLayout;
	private Stage primaryStage;	
	private Logic logic;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("KatNote");
		logic = new Logic();
		
		initRootLayout();
	}
	public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(KatNote.class.getResource("resources/ui/RootLayout.fxml"));
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
	
	public void handleCommandInput(CommandBarController commandBarController, String inputText){
		String response = logic.execute(inputText);
		commandBarController.setResponseText(response);
	}
	

}
