package org.example.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.entities.Voiture;

import java.util.List;

public class RecommendationVoitureController {

    @FXML
    private GridPane gridPaneRecommandations; // GridPane pour afficher les recommandations

    // Méthode pour afficher les recommandations
    public void afficherRecommandations(List<Voiture> recommandations) {
        if (recommandations == null || recommandations.isEmpty()) {
            System.out.println("Aucune recommandation à afficher.");
            return;
        }

        gridPaneRecommandations.getChildren().clear(); // Réinitialiser l'affichage
        int row = 0;
        int col = 0;

        for (Voiture voiture : recommandations) {
            VBox voitureCard = createVoitureCard(voiture); // Créer une carte pour la voiture
            gridPaneRecommandations.add(voitureCard, col, row); // Ajouter la carte à la grille
            col++;
            if (col > 2) { // 3 colonnes par ligne
                col = 0;
                row++;
            }
        }
    }

    // Méthode pour créer une carte de voiture
    private VBox createVoitureCard(Voiture voiture) {
        VBox card = new VBox(10);
        card.getStyleClass().add("voiture-card");

        Label marqueLabel = new Label("Marque : " + voiture.getMarque());
        Label prixLabel = new Label("Prix/Jour : " + voiture.getPrix() + " TND");
        Label disponibiliteLabel = new Label("Disponibilité : " + (voiture.isDisponible() ? "Disponible" : "Indisponible"));

        card.getChildren().addAll(marqueLabel, prixLabel, disponibiliteLabel);
        return card;
    }

    // Méthode pour retourner à la vue précédente
    @FXML
    private void retour() {
        Stage stage = (Stage) gridPaneRecommandations.getScene().getWindow();
        stage.close(); // Fermer la fenêtre des recommandations
    }
}