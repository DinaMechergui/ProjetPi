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
        if (recommandations == null || recommandations.isEmpty()) {
            System.out.println("Aucune recommandation à afficher.");
            return;
        }

        gridPaneRecommandations.getChildren().clear(); // Réinitialiser l'affichage
        int row = 0;
        int col = 0;

        for (Hebergement hebergement : recommandations) {
            VBox hebergementCard = createHebergementCard(hebergement); // Créer une carte pour l'hébergement
            gridPaneRecommandations.add(hebergementCard, col, row); // Ajouter la carte à la grille
            col++;
            if (col > 2) { // 3 colonnes par ligne
                col = 0;
                row++;
            }
        }
    }

    // Méthode pour créer une carte d'hébergement
    private VBox createHebergementCard(Hebergement hebergement) {
        VBox card = new VBox(10);
        card.getStyleClass().add("hebergement-card");

        Label nomLabel = new Label("Nom : " + hebergement.getNom());
        Label adresseLabel = new Label("Adresse : " + hebergement.getAdresse());
        Label prixLabel = new Label("Prix : " + hebergement.getPrixParNuit() + " TND");

        card.getChildren().addAll(nomLabel, adresseLabel, prixLabel);
        return card;
    }

    // Méthode pour retourner à la vue précédente
    @FXML
    private void retour() {
        Stage stage = (Stage) gridPaneRecommandations.getScene().getWindow();
        stage.close(); // Fermer la fenêtre des recommandations
    }
}