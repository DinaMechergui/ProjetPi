package tn.esprit.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import tn.esprit.entities.Cadeau;
import tn.esprit.services.ServiceCadeau;

import java.sql.SQLException;
import java.util.List;

public class AfficherCadeau {
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
            cadeauCard.setStyle("-fx-border-color: #ccc; " +
                    "-fx-background-color: #f9f9f9; " +
                    "-fx-border-radius: 10; " +
                    "-fx-background-radius: 10; " +
                    "-fx-padding: 15; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 5);");

            // Nom du cadeau
            Label cadeauNom = new Label("Nom : " + cadeau.getNom());
            cadeauNom.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333;");

            // Description du cadeau
            Label cadeauDescription = new Label("Description : " + cadeau.getDescription());
            cadeauDescription.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");

            // Disponibilité du cadeau
            Label cadeauDisponibilite = new Label("Disponibilité : " + (cadeau.isDisponibilite() ? "Disponible" : "Non disponible"));
            cadeauDisponibilite.setStyle("-fx-font-size: 12px; -fx-text-fill: " + (cadeau.isDisponibilite() ? "green;" : "red;"));

            // Bouton "Réserver"
            Button reserverButton = new Button("Réserver");
            reserverButton.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-size: 12px; -fx-background-radius: 5;");
            reserverButton.setOnAction(event -> {
                try {
                    cadeau.setDisponibilite(false);
                    serviceCadeau.modifier(cadeau);
                    loadCadeaux();
                    System.out.println("Cadeau réservé : " + cadeau.getNom());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            // Bouton "Modifier"
            Button modifierButton = new Button("Modifier");
            modifierButton.setStyle("-fx-background-color: blue; -fx-text-fill: white; -fx-font-size: 12px; -fx-background-radius: 5;");
            modifierButton.setOnAction(event -> showModifierDialog(cadeau));

            // Bouton "Supprimer"
            Button supprimerButton = new Button("Supprimer");
            supprimerButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-size: 12px; -fx-background-radius: 5;");
            supprimerButton.setOnAction(event -> {
                try {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation de suppression");
                    alert.setHeaderText("Êtes-vous sûr de vouloir supprimer ce cadeau ?");
                    alert.setContentText("Cette action est irréversible.");

                    if (alert.showAndWait().get() == ButtonType.OK) {
                        serviceCadeau.supprimer(cadeau.getId());
                        loadCadeaux();
                        System.out.println("Cadeau supprimé : " + cadeau.getNom());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            // Ajouter les éléments à la carte
            cadeauCard.getChildren().addAll(cadeauNom, cadeauDescription, cadeauDisponibilite, modifierButton, supprimerButton);

            // Ajouter la carte au GridPane
            gridPaneCadeaux.add(cadeauCard, col, row);

            // Gestion dynamique des colonnes (3 par ligne)
            col++;
            if (col > 2) {
                col = 0;
                row++;
            }
        }
    }

    private void showModifierDialog(Cadeau cadeau) {
        Dialog<Cadeau> dialog = new Dialog<>();
        dialog.setTitle("Modifier Cadeau");

        TextField nomField = new TextField(cadeau.getNom());
        TextArea descriptionField = new TextArea(cadeau.getDescription());
        CheckBox disponibiliteCheckBox = new CheckBox("Disponible");
        disponibiliteCheckBox.setSelected(cadeau.isDisponibilite());

        dialog.getDialogPane().setContent(new VBox(10, new Label("Nom:"), nomField, new Label("Description:"), descriptionField, new Label("Disponibilité:"), disponibiliteCheckBox));

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, cancelButton);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                cadeau.setNom(nomField.getText());
                cadeau.setDescription(descriptionField.getText());
                cadeau.setDisponibilite(disponibiliteCheckBox.isSelected());

                try {
                    serviceCadeau.modifier(cadeau);
                    loadCadeaux();
                    System.out.println("Cadeau modifié : " + cadeau.getNom());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return null;
        });

        dialog.showAndWait();
    }
}
