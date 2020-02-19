package SchedulingApp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    /**
     * Starts application and loads the Login screen
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setResizable(false);
        Parent root = FXMLLoader.load(getClass().getResource("ViewController/Login.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    /**
     * launches args
     */
    public static void main(String[] args) { launch(args); }
}
