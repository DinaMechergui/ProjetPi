package tn.esprit.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
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
        gridPaneCadeaux.getChildren().clear();
        gridPaneCadeaux.setHgap(15);
        gridPaneCadeaux.setVgap(15);

        int row = 0, col = 0;
        for (Cadeau cadeau : cadeaux) {
            VBox cadeauCard = new VBox(10);
            cadeauCard.setStyle("""
                -fx-background-color: #ffffff;
                -fx-border-radius: 10;
                -fx-background-radius: 10;
                -fx-border-color: #e0e0e0;
                -fx-padding: 15;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);
            """);

            Label cadeauNom = new Label("🎁 " + cadeau.getNom());
            cadeauNom.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

            Label cadeauDescription = new Label("📜 " + cadeau.getDescription());
            cadeauDescription.setStyle("-fx-text-fill: #666;");

            Label cadeauDisponibilite = new Label(cadeau.isDisponibilite() ? "✅ Disponible" : "❌ Non disponible");
            cadeauDisponibilite.setStyle("-fx-font-weight: bold; -fx-text-fill: "
                    + (cadeau.isDisponibilite() ? "#2ecc71" : "#e74c3c") + ";");

            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(Pos.CENTER_RIGHT);

            Button reserverButton = new Button("🛒 Réserver");
            reserverButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-border-radius: 5;");
            reserverButton.setOnAction(event -> {
                try {
                    cadeau.setDisponibilite(false);
                    serviceCadeau.modifier(cadeau);
                    loadCadeaux();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            Button modifierButton = new Button("✏️ Modifier");
            modifierButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-border-radius: 5;");
            modifierButton.setOnAction(event -> ouvrirFenetreModification(cadeau));

            Button supprimerButton = new Button("🗑️ Supprimer");
            supprimerButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-border-radius: 5;");
            supprimerButton.setOnAction(event -> {
                try {
                    serviceCadeau.supprimer(cadeau.getId());
                    loadCadeaux();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            buttonBox.getChildren().addAll( modifierButton, supprimerButton);
            cadeauCard.getChildren().addAll(cadeauNom, cadeauDescription, cadeauDisponibilite, buttonBox);
            cadeauCard.setPadding(new Insets(10));

            gridPaneCadeaux.add(cadeauCard, col, row);
            col++;
            if (col > 2) {
                col = 0;
                row++;
            }
        }
    }

    private void ouvrirFenetreModification(Cadeau cadeau) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Modifier Cadeau");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #f9f9f9; -fx-border-radius: 10;");

        Label titleLabel = new Label("📝 Modifier le Cadeau");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setAlignment(Pos.CENTER);

        TextField nomField = new TextField(cadeau.getNom());
        TextArea descriptionField = new TextArea(cadeau.getDescription());
        CheckBox disponibiliteCheckBox = new CheckBox("Disponible");
        disponibiliteCheckBox.setSelected(cadeau.isDisponibilite());

        formGrid.addRow(0, new Label("Nom :"), nomField);
        formGrid.addRow(1, new Label("Description :"), descriptionField);
        formGrid.addRow(2, new Label("Disponibilité :"), disponibiliteCheckBox);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button confirmerButton = new Button("✅ Enregistrer");
        confirmerButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-border-radius: 5;");
        confirmerButton.setOnAction(event -> {
            try {
                cadeau.setNom(nomField.getText());
                cadeau.setDescription(descriptionField.getText());
                cadeau.setDisponibilite(disponibiliteCheckBox.isSelected());
                serviceCadeau.modifier(cadeau);
                loadCadeaux();
                popupStage.close();
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
}