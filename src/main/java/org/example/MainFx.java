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

       /* FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajoutervoiture.fxml"));
        try {
            Parent parent = loader.load();
            Scene scene = new Scene(parent);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Ajouter voiture");
            primaryStage.show();
        }catch (IOException e){
            throw new RuntimeException(e);
        }



        try {
            FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/ajouterhebergement.fxml"));
            Parent parent = loader1.load();

            Scene scene = new Scene(parent);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Ajouter Hébergement");
            primaryStage.setResizable(false); // Empêche le redimensionnement
            primaryStage.show();

        } catch (IOException e) {
            System.err.println("❌ Erreur lors du chargement de l'interface : " + e.getMessage());
            e.printStackTrace();
        }*/

      FXMLLoader loader = new FXMLLoader(getClass().getResource("/voitureclient.fxml"));
        try {
            Parent parent = loader.load();
            Scene scene = new Scene(parent);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Gestion des voitures");
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

     /*   try {
            // Charger l'interface graphique depuis FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/affichervoiture.fxml"));
            Parent root = loader.load();


            // Configurer la scène
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Afficher Voiture");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

          /*  Parent root = FXMLLoader.load(getClass().getResource("/AfficherHebergement.fxml")); // Charge la page Hébergement
            primaryStage.setTitle("Gestion des Hébergements"); // Nom de la fenêtre
            primaryStage.setScene(new Scene(root, 800, 600)); // Taille de la fenêtre
            primaryStage.show(); // Afficher la fenêtre
    }*/

