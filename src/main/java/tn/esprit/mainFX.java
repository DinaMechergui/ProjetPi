package tn.esprit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class mainFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
     /*   FXMLLoader loader = new FXMLLoader(getClass().getResource("/Ajouterinvite.fxml"));
        try {
            Parent parent = loader.load();
            Scene scene = new Scene(parent);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Ajouter invite");
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/

     /*   FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterEvenement.fxml"));
        try {
            Parent parent = loader.load();
            Scene scene = new Scene(parent);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Ajouter evenement");
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/

      /* FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterCadeau.fxml"));
        try {
            Parent parent = loader.load();
            Scene scene = new Scene(parent);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Ajouter Cadeau");
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/

  /*   FXMLLoader loader = new FXMLLoader(getClass().getResource("/Afficherinvite.fxml"));
        try {
            Parent parent = loader.load();
            Scene scene = new Scene(parent);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Afficher invite");
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
 /* FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageEvenement.fxml"));
        try {
            Parent parent = loader.load();
            Scene scene = new Scene(parent);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Afficher evenement");
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
       /*   FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherCadeau.fxml"));
        try {
            Parent parent = loader.load();
            Scene scene = new Scene(parent);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Afficher evenement");
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
             FXMLLoader loader = new FXMLLoader(getClass().getResource("/menuOrganizer.fxml"));
        try {
            Parent parent = loader.load();
            Scene scene = new Scene(parent);
            primaryStage.setScene(scene);
            primaryStage.setTitle("menu");
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
