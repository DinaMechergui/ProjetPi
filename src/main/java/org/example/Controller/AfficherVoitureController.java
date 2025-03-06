package org.example.Controller;

import org.example.entities.Voiture;
import org.example.services.ServiceVoiture;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AfficherVoitureController {
    private ServiceVoiture serviceVoiture = new ServiceVoiture();

    @FXML
    private GridPane gridPaneVoitures;

    @FXML
    private Button loginButton;

    @FXML
    private Button logoutButton;

    @FXML
    private void goToStore() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherVoiture.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
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
        gridPaneVoitures.getChildren().clear(); // Réinitialiser l'affichage
        List<Voiture> voitures = serviceVoiture.afficher();
        int row = 0;
        int col = 0;

        for (Voiture voiture : voitures) {
            VBox voitureCard = new VBox(10);
            voitureCard.getStyleClass().add("voiture-card");

            // 🖼️ Image de la voiture
            ImageView voitureImage = new ImageView();
            voitureImage.setFitWidth(150); // Largeur fixe
            voitureImage.setFitHeight(100); // Hauteur fixe
            voitureImage.setPreserveRatio(true); // Garde les proportions

            try {
                Image image = new Image(voiture.getImageUrl(), true);
                voitureImage.setImage(image);
            } catch (Exception e) {
                System.err.println("⚠ Erreur lors du chargement de l'image : " + voiture.getImageUrl());
            }

            // Marque de la voiture
            Label voitureMarque = new Label("Marque : " + voiture.getMarque());
            voitureMarque.getStyleClass().add("voiture-marque");

            // Prix de la voiture
            Label voiturePrix = new Label("Prix : " + String.format("%.2f", voiture.getPrix()) + " TND / jour");
            voiturePrix.getStyleClass().add("voiture-prix");

            // Disponibilité
            Label voitureDispo = new Label(voiture.isDisponible() ? "Disponible" : "Non disponible");
            voitureDispo.getStyleClass().add("voiture-dispo");
            voitureDispo.setStyle(voiture.isDisponible() ? "-fx-text-fill: green;" : "-fx-text-fill: red;");

            // Bouton "Réserver"
            Button reserverButton = new Button("Réserver");
            reserverButton.getStyleClass().addAll("button", "reserver-button");
            reserverButton.setDisable(!voiture.isDisponible());
            reserverButton.setOnAction(event -> System.out.println("Voiture réservée : " + voiture.getMarque()));

            // Bouton "Modifier"
            Button modifierButton = new Button("Modifier");
            modifierButton.getStyleClass().addAll("button", "modifier-button");
            modifierButton.setOnAction(event -> modifierVoiture(voiture));

            // Bouton "Supprimer"
            Button supprimerButton = new Button("Supprimer");
            supprimerButton.getStyleClass().addAll("button", "supprimer-button");
            supprimerButton.setOnAction(event -> supprimerVoiture(voiture));

            // Ajouter les éléments à la carte
            voitureCard.getChildren().addAll(voitureImage, voitureMarque, voiturePrix, voitureDispo, reserverButton, modifierButton, supprimerButton);

            // Ajouter la carte au GridPane
            gridPaneVoitures.add(voitureCard, col, row);

            col++;
            if (col > 2) { // 3 colonnes par ligne
                col = 0;
                row++;
            }
        }
    }

    private void modifierVoiture(Voiture voiture) {
        TextInputDialog dialog = new TextInputDialog(voiture.getMarque());
        dialog.setTitle("Modifier Voiture");
        dialog.setHeaderText("Modifier les informations de la voiture");
        dialog.setContentText("Nouvelle marque :");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(nouvelleMarque -> {
            TextInputDialog prixDialog = new TextInputDialog(String.valueOf(voiture.getPrix()));
            prixDialog.setTitle("Modifier Prix");
            prixDialog.setHeaderText("Modifier le prix de la voiture");
            prixDialog.setContentText("Nouveau prix :");

            Optional<String> prixResult = prixDialog.showAndWait();
            prixResult.ifPresent(nouveauPrix -> {
                try {
                    voiture.setMarque(nouvelleMarque);
                    voiture.setPrix(Float.parseFloat(nouveauPrix));

                    serviceVoiture.modifier(voiture);
                    loadVoitures();
                    System.out.println("✅ Voiture modifiée avec succès !");
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.err.println("❌ Erreur lors de la modification !");
                }
            });
        });
    }

    private void supprimerVoiture(Voiture voiture) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Suppression de Voiture");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette voiture ?");
        alert.setContentText("Cette action est irréversible.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                serviceVoiture.supprimer(voiture.getIdvoiture());
                loadVoitures();
                System.out.println("🛑 Voiture supprimée avec succès !");
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("❌ Erreur lors de la suppression !");
            }
        }
    }
}
