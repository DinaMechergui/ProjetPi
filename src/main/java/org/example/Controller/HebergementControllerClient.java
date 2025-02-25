package org.example.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.entities.Hebergement;
import org.example.services.ServiceHebergement;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class HebergementControllerClient {
    private final ServiceHebergement serviceHebergement = new ServiceHebergement();

    @FXML
    private GridPane gridPaneHebergements;

    public void initialize() {
        try {
            loadHebergements(); // Charger les données
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadHebergements() throws SQLException {
        gridPaneHebergements.getChildren().clear(); // Réinitialiser l'affichage
        List<Hebergement> hebergements = serviceHebergement.afficher();
        int row = 0;
        int col = 0;

        for (Hebergement hebergement : hebergements) {
            VBox hebergementCard = createHebergementCard(hebergement);
            gridPaneHebergements.add(hebergementCard, col, row);
            col++;
            if (col > 2) {
                col = 0;
                row++;
            }
        }
    }

    private VBox createHebergementCard(Hebergement hebergement) {
        VBox hebergementCard = new VBox(10);
        hebergementCard.getStyleClass().add("hebergement-card");

        Label hebergementNom = new Label("Nom : " + hebergement.getNom());
        Label hebergementAdresse = new Label("Adresse : " + hebergement.getAdresse());
        Label hebergementPrix = new Label("Prix/Nuit : " + String.format("%.2f", hebergement.getPrixParNuit()) + " TND");
        Label hebergementDispo = new Label(hebergement.isDisponible() ? "Disponible" : "Indisponible");
        hebergementDispo.setStyle(hebergement.isDisponible() ? "-fx-text-fill: green;" : "-fx-text-fill: red;");

        Button reserverButton = new Button("Réserver");
        reserverButton.setOnAction(event -> ouvrirFormulaireReservation(event, hebergement));

        hebergementCard.getChildren().addAll(hebergementNom, hebergementAdresse, hebergementPrix, hebergementDispo, reserverButton);
        return hebergementCard;
    }

    private void ouvrirFormulaireReservation(ActionEvent event, Hebergement hebergement) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterReservationHebergement.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur de la nouvelle page
            AjouterResHebergementController controller = loader.getController();
            controller.setHebergementData(hebergement); // Passer les données de l'hébergement

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Réservation Hébergement");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
