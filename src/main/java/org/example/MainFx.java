package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.Controller.AfficherVoitureController;

import java.io.IOException;

public class MainFx extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

       /* FXMLLoader loader = new FXMLLoader(getClass().getResource("/voitureclient.fxml"));
        try {
            Parent parent = loader.load();
            Scene scene = new Scene(parent);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Nos voitures");
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/


       /* FXMLLoader loader = new FXMLLoader(getClass().getResource("/affichervoiture.fxml"));
        try {
            Parent parent = loader.load();
            Scene scene = new Scene(parent);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Gestion des voitures");
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherHebergement.fxml"));
        try {
            Parent parent = loader.load();
            Scene scene = new Scene(parent);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Gestion des hebergements");
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }

}
