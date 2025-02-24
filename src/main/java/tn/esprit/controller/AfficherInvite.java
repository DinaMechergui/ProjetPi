package tn.esprit.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.Modality;
import tn.esprit.entities.Invite;
import tn.esprit.services.ServiceInvite;

import java.sql.SQLException;
import java.util.List;

public class AfficherInvite {
    private ServiceInvite serviceInvite = new ServiceInvite();

    @FXML
    private GridPane gridPaneInvites;

    @FXML
    public void initialize() {
        try {
            loadInvites();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadInvites() throws SQLException {
        List<Invite> invites = serviceInvite.afficher();
        gridPaneInvites.getChildren().clear(); // Nettoyer avant d'ajouter
        gridPaneInvites.setHgap(15);
        gridPaneInvites.setVgap(15);

        int row = 0, col = 0;
        for (Invite invite : invites) {
            VBox inviteCard = new VBox(10);
            inviteCard.getStyleClass().add("invite-card");
            inviteCard.setStyle("""
                -fx-background-color: #ffffff;
                -fx-border-radius: 10;
                -fx-background-radius: 10;
                -fx-border-color: #e0e0e0;
                -fx-padding: 15;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);
            """);

            Label inviteNom = new Label("👤 " + invite.getNom() + " " + invite.getPrenom());
            inviteNom.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

            Label inviteEmail = new Label("📧 " + invite.getEmail());
            inviteEmail.setStyle("-fx-text-fill: #666;");

            Label inviteTelephone = new Label("📞 " + invite.getTelephone());
            inviteTelephone.setStyle("-fx-text-fill: #666;");

            Label inviteConfirmation = new Label(invite.isConfirmation() ? "✅ Confirmé" : "❌ Non confirmé");
            inviteConfirmation.setStyle("-fx-font-weight: bold; -fx-text-fill: "
                    + (invite.isConfirmation() ? "#2ecc71" : "#e74c3c") + ";");

            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(Pos.CENTER_RIGHT);

            Button modifierButton = new Button("✏️ Modifier");
            modifierButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-border-radius: 5;");
            modifierButton.setOnAction(event -> ouvrirFenetreModification(invite));

            Button supprimerButton = new Button("🗑️ Supprimer");
            supprimerButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-border-radius: 5;");
            supprimerButton.setOnAction(event -> {
                try {
                    serviceInvite.supprimer(invite.getId());
                    loadInvites(); // Rafraîchir la liste après suppression
                    System.out.println("Invité supprimé : " + invite.getNom());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            buttonBox.getChildren().addAll(modifierButton, supprimerButton);
            inviteCard.getChildren().addAll(inviteNom, inviteEmail, inviteTelephone, inviteConfirmation, buttonBox);
            inviteCard.setPadding(new Insets(10));

            gridPaneInvites.add(inviteCard, col, row);
            col++;
            if (col > 2) { // 3 colonnes par ligne
                col = 0;
                row++;
            }
        }
    }

    private void ouvrirFenetreModification(Invite invite) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Modifier l'Invité");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #f9f9f9; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label titleLabel = new Label("📝 Modifier l'Invité");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setAlignment(Pos.CENTER);

        TextField nomField = new TextField(invite.getNom());
        TextField prenomField = new TextField(invite.getPrenom());
        TextField emailField = new TextField(invite.getEmail());
        TextField telephoneField = new TextField(invite.getTelephone());

        CheckBox confirmationCheckBox = new CheckBox("Confirmé");
        confirmationCheckBox.setSelected(invite.isConfirmation());

        formGrid.addRow(0, new Label("Nom :"), nomField);
        formGrid.addRow(1, new Label("Prénom :"), prenomField);
        formGrid.addRow(2, new Label("Email :"), emailField);
        formGrid.addRow(3, new Label("Téléphone :"), telephoneField);
        formGrid.addRow(4, new Label("Confirmation :"), confirmationCheckBox);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button confirmerButton = new Button("✅ Enregistrer");
        confirmerButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-border-radius: 5;");
        confirmerButton.setOnAction(event -> {
            // Validation du téléphone: doit contenir exactement 8 chiffres
            String telInput = telephoneField.getText().trim();
            if (!telInput.matches("\\d{8}")) {
                afficherAlerte("Erreur de validation", "Le téléphone doit contenir exactement 8 chiffres.");
                return;
            }

            // Validation de l'email: doit contenir un '@'
            String emailInput = emailField.getText().trim();
            if (!emailInput.contains("@")) {
                afficherAlerte("Erreur de validation", "L'email doit contenir un '@'.");
                return;
            }

            try {
                invite.setNom(nomField.getText());
                invite.setPrenom(prenomField.getText());
                invite.setEmail(emailField.getText());
                invite.setTelephone(telephoneField.getText());
                invite.setConfirmation(confirmationCheckBox.isSelected());

                serviceInvite.modifier(invite);
                loadInvites(); // Rafraîchir la liste après modification
                popupStage.close();
                System.out.println("Invité modifié : " + nomField.getText());
            } catch (SQLException e) {
                e.printStackTrace();
                afficherAlerte("Erreur", "Une erreur est survenue lors de la modification : " + e.getMessage());
            }
        });

        Button annulerButton = new Button("❌ Annuler");
        annulerButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-border-radius: 5;");
        annulerButton.setOnAction(event -> popupStage.close());

        buttonBox.getChildren().addAll(annulerButton, confirmerButton);
        layout.getChildren().addAll(titleLabel, formGrid, buttonBox);

        Scene scene = new Scene(layout, 350, 350);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    private void afficherAlerte(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
