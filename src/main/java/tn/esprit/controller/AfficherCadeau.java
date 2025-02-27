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

            // Bouton "Modifier"
            Button modifierButton = new Button("Modifier");
            modifierButton.getStyleClass().add("modifier-button");
            modifierButton.setStyle("-fx-background-color: blue; -fx-text-fill: white;");
            modifierButton.setOnAction(event -> {
                // Ouvrir une fenêtre de modification avec tous les champs
                showModifierDialog(cadeau);
            });

            // Bouton "Supprimer"
            Button supprimerButton = new Button("Supprimer");
            supprimerButton.getStyleClass().add("supprimer-button");
            supprimerButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
            supprimerButton.setOnAction(event -> {
                try {
                    // Demander confirmation avant de supprimer
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation de suppression");
                    alert.setHeaderText("Êtes-vous sûr de vouloir supprimer ce cadeau ?");
                    alert.setContentText("Cette action est irréversible.");

                    if (alert.showAndWait().get() == ButtonType.OK) {
                        serviceCadeau.supprimer(cadeau.getId()); // Supprimer le cadeau de la base de données
                        loadCadeaux(); // Rafraîchir la liste des cadeaux après suppression
                        System.out.println("Cadeau supprimé : " + cadeau.getNom());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            // Ajouter les éléments à la carte
            cadeauCard.getChildren().addAll(cadeauNom, cadeauDescription, cadeauDisponibilite, reserverButton, modifierButton, supprimerButton);

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

    // Méthode pour afficher un formulaire de modification
    private void showModifierDialog(Cadeau cadeau) {
        // Créer une fenêtre pour modifier tous les champs du cadeau
        Dialog<Cadeau> dialog = new Dialog<>();
        dialog.setTitle("Modifier Cadeau");

        // Créer les champs pour modifier le nom, la description, et la disponibilité
        TextField nomField = new TextField(cadeau.getNom());
        TextArea descriptionField = new TextArea(cadeau.getDescription());
        CheckBox disponibiliteCheckBox = new CheckBox("Disponible");
        disponibiliteCheckBox.setSelected(cadeau.isDisponibilite());

        // Ajouter les champs dans la fenêtre de dialogue
        dialog.getDialogPane().setContent(new VBox(10, new Label("Nom:"), nomField, new Label("Description:"), descriptionField, new Label("Disponibilité:"), disponibiliteCheckBox));

        // Ajouter les boutons OK et Annuler
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, cancelButton);

        // Gérer le bouton OK
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                // Mettre à jour les informations du cadeau avec les nouvelles valeurs
                cadeau.setNom(nomField.getText());
                cadeau.setDescription(descriptionField.getText());
                cadeau.setDisponibilite(disponibiliteCheckBox.isSelected());

                try {
                    serviceCadeau.modifier(cadeau); // Mettre à jour le cadeau dans la base de données
                    loadCadeaux(); // Rafraîchir la liste des cadeaux
                    System.out.println("Cadeau modifié : " + cadeau.getNom());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return null;
        });

        // Afficher la fenêtre de dialogue
        dialog.showAndWait();
    }
}