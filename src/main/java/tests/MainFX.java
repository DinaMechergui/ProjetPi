package tests;  // Changed package to match project structure

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class MainFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML file from root of classpath
            Parent root = FXMLLoader.load(getClass().getResource("/reservation.fxml"));

            // Set up the scene and stage
            Scene scene = new Scene(root);
            primaryStage.setTitle("Event Manager");  // More appropriate title
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            // Proper error handling
            System.err.println("Failed to load FXML file:");
            e.printStackTrace();
            showErrorAlert("Fatal Error", "Could not load application interface");
        }
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}