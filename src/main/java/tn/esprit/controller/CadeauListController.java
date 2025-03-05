package tn.esprit.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import tn.esprit.entities.Cadeau;
import tn.esprit.services.ServiceCadeau;

import java.sql.SQLException;
import java.util.List;

public class CadeauListController {
    private ServiceCadeau serviceCadeau = new ServiceCadeau();
    @FXML
    private GridPane cadeauGrid;
    private String nomInvite;
    @FXML
    private Label bienvenueLabel;

    public void setNomInvite(String nomInvite) {
        this.nomInvite = nomInvite;
        bienvenueLabel.setText("Bienvenue " + nomInvite + " dans notre événement");
        bienvenueLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-alignment: center;");
    }

    public void setCodeSuffix(String codeSuffix) {
        String nomInvite = serviceCadeau.getNomInviteById(codeSuffix);
        if (nomInvite != null) {
            bienvenueLabel.setText("Bienvenue " + nomInvite + " dans notre événement");
        } else {
            bienvenueLabel.setText("Invité non trouvé");
        }
    }

    public void setCadeaux(List<Cadeau> cadeaux) {
        cadeauGrid.getChildren().clear();
        int column = 0;
        int row = 0;
        for (Cadeau cadeau : cadeaux) {
            VBox cadeauBox = new VBox(10);
            cadeauBox.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-width: 1; -fx-background-color: #fff; -fx-background-radius: 5;");
            cadeauBox.setAlignment(Pos.CENTER);
            cadeauBox.setPadding(new Insets(10));

            // Label pour le nom du cadeau
            Label nameLabel = new Label("Nom : " + cadeau.getNom());
            nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

            // Label pour la description du cadeau
            Label descriptionLabel = new Label("Description : " + cadeau.getDescription());
            descriptionLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
            descriptionLabel.setWrapText(true);

            // Indicateur de disponibilité
            Label disponibiliteLabel = new Label(cadeau.isDisponibilite() ? "Disponible" : "Reserver");
            disponibiliteLabel.setStyle(cadeau.isDisponibilite() ? "-fx-text-fill: green;" : "-fx-text-fill: red;");

            // VBox pour contenir le bouton "Réserver"
            VBox buttonBox = new VBox();
            buttonBox.setAlignment(Pos.CENTER);

            // Bouton "Réserver" (ajouté uniquement si le cadeau est disponible)
            if (cadeau.isDisponibilite()) {
                Button reserverButton = new Button("🛒 Réserver");
                reserverButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-border-radius: 5;");
                reserverButton.setOnAction(event -> {
                    try {
                        // Mettre à jour la disponibilité du cadeau
                        cadeau.setDisponibilite(false);
                        serviceCadeau.modifier(cadeau);

                        // Mettre à jour l'interface utilisateur
                        disponibiliteLabel.setText("Non disponible");
                        disponibiliteLabel.setStyle("-fx-text-fill: red;");
                        buttonBox.getChildren().clear(); // Supprimer le bouton après réservation
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
                buttonBox.getChildren().add(reserverButton);
            }

            cadeauBox.getChildren().addAll(nameLabel, descriptionLabel, disponibiliteLabel, buttonBox);
            cadeauGrid.add(cadeauBox, column, row);
            column++;
            if (column == 3) { // Nombre de colonnes souhaité
                column = 0;
                row++;
            }
        }
    }

    private void handleReservation(Cadeau cadeau) {
        // Logique de réservation du cadeau
        // Par exemple, afficher une alerte de confirmation
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Réservation");
        alert.setHeaderText(null);
        alert.setContentText("Vous avez réservé le cadeau : " + cadeau.getNom());
        alert.showAndWait();
    }
}
