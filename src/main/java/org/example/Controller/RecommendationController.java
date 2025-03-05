package org.example.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.entities.Hebergement;

import java.util.List;

public class RecommendationController {

    @FXML
    private GridPane gridPaneRecommandations; // GridPane pour afficher les recommandations

    // Méthode pour afficher les recommandations
    public void afficherRecommandations(List<Hebergement> recommandations) {
        System.out.println("Nombre de recommandations reçues : " + (recommandations != null ? recommandations.size() : "null"));

        if (recommandations == null || recommandations.isEmpty()) {
            System.out.println("Aucune recommandation à afficher.");
            Label messageLabel = new Label("Aucune recommandation disponible.");
            gridPaneRecommandations.add(messageLabel, 0, 0);
            return;
        }

        gridPaneRecommandations.getChildren().clear(); // Réinitialiser l'affichage
        int row = 0;
        int col = 0;

        for (Hebergement hebergement : recommandations) {
            System.out.println("Création de la carte pour : " + hebergement.getNom());

            VBox hebergementCard = createHebergementCard(hebergement);
            gridPaneRecommandations.add(hebergementCard, col, row);

            System.out.println("Ajout de la carte à la position : row=" + row + ", col=" + col);

            col++;
            if (col > 2) { // 3 colonnes par ligne
                col = 0;
                row++;
            }

        }
    }

    private VBox createHebergementCard(Hebergement hebergement) {
        try {
            VBox card = new VBox(10);
            card.getStyleClass().add("hebergement-card");

            Label nomLabel = new Label("Nom : " + hebergement.getNom());
            Label adresseLabel = new Label("Adresse : " + hebergement.getAdresse());
            Label prixLabel = new Label("Prix : " + hebergement.getPrixParNuit() + " TND");

            card.getChildren().addAll(nomLabel, adresseLabel, prixLabel);
            return card;
        } catch (Exception e) {
            System.err.println("Erreur lors de la création de la carte : " + e.getMessage());
            return new VBox(new Label("Erreur lors du chargement des données."));
        }
    }

    // Méthode pour retourner à la vue précédente
    @FXML
    private void retour() {
        Stage stage = (Stage) gridPaneRecommandations.getScene().getWindow();
        stage.close(); // Fermer la fenêtre des recommandations
    }
}