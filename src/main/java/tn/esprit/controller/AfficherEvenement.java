package tn.esprit.controller;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.Modality;
import tn.esprit.entities.Evenement;
import tn.esprit.services.ServiceEvenement;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AfficherEvenement {
    private ServiceEvenement serviceEvenement = new ServiceEvenement();

    @FXML
    private GridPane gridPaneEvenements;

    @FXML
    public void initialize() {
        try {
            loadEvenements();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadEvenements() throws SQLException {
        List<Evenement> evenements = serviceEvenement.afficher();
        gridPaneEvenements.getChildren().clear(); // Nettoyer avant d'ajouter
        gridPaneEvenements.setHgap(15);
        gridPaneEvenements.setVgap(15);

        int row = 0, col = 0;

        for (Evenement evenement : evenements) {
            VBox evenementCard = new VBox(10);
            evenementCard.getStyleClass().add("evenement-card");
            evenementCard.setStyle("""
                -fx-background-color: #ffffff;
                -fx-border-radius: 10;
                -fx-background-radius: 10;
                -fx-border-color: #e0e0e0;
                -fx-padding: 15;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);
            """);

            Label evenementNom = new Label("🎉 " + evenement.getNom());
            evenementNom.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

            Label evenementLieu = new Label("📍 " + evenement.getLieu());
            evenementLieu.setStyle("-fx-text-fill: #666;");

            Label evenementDate = new Label("📅 " + evenement.getDate());
            evenementDate.setStyle("-fx-text-fill: #666;");

            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(Pos.CENTER_RIGHT);

            Button modifierButton = new Button("✏️ Modifier");
            modifierButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-border-radius: 5;");
            modifierButton.setOnAction(event -> ouvrirFenetreModification(evenement));

            // Bouton "Ajouter invite" avec style personnalisé
            Button ajouterInviteBtn = new Button("➕ Ajouter invite");
            ajouterInviteBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-border-radius: 5;");
            ajouterInviteBtn.setOnAction(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Ajouterinvite.fxml"));
                    Parent root = loader.load();

                    // Récupérer le contrôleur de Ajouterinvite
                    Ajouterinvite ajouterinviteCtrl = loader.getController();
                    // Transmettre l'événement sélectionné
                    ajouterinviteCtrl.setEvenementActuel(evenement);

                    // Navigation vers la nouvelle vue
                    // Ici, on utilise l'un des nœuds existants pour changer la scène
                    ajouterInviteBtn.getScene().setRoot(root);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    afficherAlerte("Erreur", "Impossible de charger la page Ajouterinvite : " + ex.getMessage());
                }
            });




            Button supprimerButton = new Button("🗑️ Supprimer");
            supprimerButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-border-radius: 5;");
            supprimerButton.setOnAction(event -> {
                try {
                    serviceEvenement.supprimer(evenement.getId());
                    loadEvenements(); // Rafraîchir la liste après suppression
                    System.out.println("Événement supprimé : " + evenement.getNom());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            buttonBox.getChildren().addAll(modifierButton, supprimerButton,ajouterInviteBtn);
            evenementCard.getChildren().addAll(evenementNom, evenementLieu, evenementDate, buttonBox);
            evenementCard.setPadding(new Insets(10));

            gridPaneEvenements.add(evenementCard, col, row);

            col++;
            if (col > 2) { // 3 colonnes par ligne
                col = 0;
                row++;
            }
        }
    }

    private void ouvrirFenetreModification(Evenement evenement) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Modifier l'Événement");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #f9f9f9; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label titleLabel = new Label("📝 Modifier l'événement");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setAlignment(Pos.CENTER);

        TextField nomField = new TextField(evenement.getNom());
        TextField lieuField = new TextField(evenement.getLieu());
        TextField dateField = new TextField(evenement.getDate());

        formGrid.addRow(0, new Label("Nom :"), nomField);
        formGrid.addRow(1, new Label("Lieu :"), lieuField);
        formGrid.addRow(2, new Label("Date :"), dateField);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button confirmerButton = new Button("✅ Enregistrer");
        confirmerButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-border-radius: 5;");
        confirmerButton.setOnAction(event -> {
            try {
                serviceEvenement.modifier(evenement.getId(), nomField.getText(), lieuField.getText(), dateField.getText());
                loadEvenements(); // Rafraîchir la liste après modification
                popupStage.close();
                System.out.println("Événement modifié : " + nomField.getText());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        Button annulerButton = new Button("❌ Annuler");
        annulerButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-border-radius: 5;");
        annulerButton.setOnAction(event -> popupStage.close());

        buttonBox.getChildren().addAll(annulerButton, confirmerButton);

        layout.getChildren().addAll(titleLabel, formGrid, buttonBox);

        Scene scene = new Scene(layout, 350, 300);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }
    // Méthode d'affichage d'alertes
    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }

}