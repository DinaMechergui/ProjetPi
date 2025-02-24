package tn.esprit.controller;


import javafx.fxml.FXML;
import javafx.scene.control.*;
        import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import tn.esprit.entities.Cadeau;
import tn.esprit.services.ServiceCadeau;


import java.sql.SQLException;
import java.util.List;

public class AfficherCadeauClient {
    private ServiceCadeau serviceCadeau = new ServiceCadeau();

    @FXML
    private GridPane gridPaneCadeaux;

    @FXML
    public void initialize() {
        try {
            loadCadeaux();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadCadeaux() throws SQLException {
        List<Cadeau> cadeaux = serviceCadeau.afficher();
        gridPaneCadeaux.getChildren().clear(); // Nettoyer avant d'ajouter

        int row = 0;
        int col = 0;

        for (Cadeau cadeau : cadeaux) {
            // Créer une carte pour chaque cadeau
            VBox cadeauCard = new VBox(10);
            cadeauCard.getStyleClass().add("cadeau-card");
            cadeauCard.setStyle("-fx-border-color: #ccc; -fx-padding: 10; -fx-background-radius: 10;");

            // Nom du cadeau
            Label cadeauNom = new Label("Nom : " + cadeau.getNom());
            cadeauNom.getStyleClass().add("cadeau-nom");

            // Description du cadeau
            Label cadeauDescription = new Label("Description : " + cadeau.getDescription());
            cadeauDescription.getStyleClass().add("cadeau-description");

            // Disponibilité du cadeau
            Label cadeauDisponibilite = new Label("Disponibilité : " + (cadeau.isDisponibilite() ? "Disponible" : "Non disponible"));
            cadeauDisponibilite.getStyleClass().add("cadeau-disponibilite");

            // Bouton "Réserver"
            Button reserverButton = new Button("Réserver");
            reserverButton.getStyleClass().add("reserver-button");
            reserverButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");
            reserverButton.setOnAction(event -> {
                try {
                    // Logique pour réserver le cadeau
                    cadeau.setDisponibilite(false);
                    serviceCadeau.modifier(cadeau); // Mise à jour de la disponibilité dans la base de données
                    loadCadeaux(); // Rafraîchir la liste des cadeaux
                    System.out.println("Cadeau réservé : " + cadeau.getNom());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            // Ajouter les éléments à la carte
            cadeauCard.getChildren().addAll(cadeauNom, cadeauDescription, cadeauDisponibilite, reserverButton);

            // Ajouter la carte au GridPane
            gridPaneCadeaux.add(cadeauCard, col, row);

            // Passer à la colonne suivante
            col++;
            if (col > 2) { // 3 colonnes par ligne
                col = 0;
                row++;
            }
        }
    }
}
