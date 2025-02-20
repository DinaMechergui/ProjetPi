package org.example.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.entities.Voiture;
import org.example.services.ServiceVoiture;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class VoitureControllerClient {
    private ServiceVoiture serviceVoiture = new ServiceVoiture();

    @FXML
    private GridPane gridPaneVoitures;

    @FXML
    private void goToStore() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherVoiture.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) gridPaneVoitures.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de la page des voitures.");
        }
    }

    public void initialize() {
        try {
            loadVoitures();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadVoitures() throws SQLException {
        gridPaneVoitures.getChildren().clear();
        List<Voiture> voitures = serviceVoiture.afficher();
        int row = 0, col = 0;

        for (Voiture voiture : voitures) {
            VBox voitureCard = new VBox(10);
            voitureCard.getStyleClass().add("voiture-card");

            Label voitureMarque = new Label("Marque : " + voiture.getMarque());
            Label voiturePrix = new Label("Prix : " + String.format("%.2f", voiture.getPrix()) + " TND / jour");
            Label voitureDispo = new Label(voiture.isDisponible() ? "Disponible" : "Non disponible");
            voitureDispo.setStyle(voiture.isDisponible() ? "-fx-text-fill: green;" : "-fx-text-fill: red;");

            Button reserverButton = new Button("Réserver");
            reserverButton.setOnAction(event -> ouvrirPageReservation(voiture));

            voitureCard.getChildren().addAll(voitureMarque, voiturePrix, voitureDispo, reserverButton);
            gridPaneVoitures.add(voitureCard, col, row);

            col++;
            if (col > 2) {
                col = 0;
                row++;
            }
        }
    }

    private void ouvrirPageReservation(Voiture voiture) {
        if (!voiture.isDisponible()) {
            afficherErreur("❌ Cette voiture n'est pas disponible pour la réservation.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterReservationVoiture.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur de la page de réservation
            AjouterResVoitureController controller = loader.getController();
            if (controller != null) {
                controller.setVoitureData(voiture); // Passer l'objet voiture au contrôleur
            } else {
                afficherErreur("Erreur : Impossible de récupérer le contrôleur de la réservation !");
                return;
            }

            Stage stage = new Stage();
            stage.setTitle("Ajouter une Réservation");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            afficherErreur("❌ Erreur lors de l'ouverture de la page de réservation : " + e.getMessage());
        }
    }


    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.show();
    }

}
