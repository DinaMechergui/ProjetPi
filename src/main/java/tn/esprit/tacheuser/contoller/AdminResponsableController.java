package tn.esprit.tacheuser.contoller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.tacheuser.models.Responsable;
import tn.esprit.tacheuser.utils.MySQLConnection;

import java.sql.*;
import java.io.IOException;

public class AdminResponsableController {

    @FXML
    private GridPane responsableGrid;

    @FXML
    public void initialize() {
        Connection connection = MySQLConnection.getInstance().getConnection();

        try {
            String query = "SELECT * FROM responsable";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            int row = 0;
            int col = 0;

            responsableGrid.setStyle("-fx-grid-lines-visible: true; -fx-hgap: 15; -fx-vgap: 15;");
            responsableGrid.setPrefWidth(800);
            responsableGrid.setPrefHeight(600);

            while (resultSet.next()) {
                Responsable responsable = new Responsable(
                        resultSet.getInt("id"),
                        resultSet.getString("nom"),
                        resultSet.getString("prenom"),
                        resultSet.getString("description"),
                        resultSet.getString("tel")
                );

                HBox responsableCard = createResponsableCard(responsable);
                responsableGrid.add(responsableCard, col, row);

                col++;
                if (col > 2) {
                    col = 0;
                    row++;
                }
            }

            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private HBox createResponsableCard(Responsable responsable) {
        HBox card = new HBox(15);
        card.setStyle("-fx-background-color: #e0e5e8; -fx-padding: 20; -fx-border-radius: 10; -fx-border-width: 2; -fx-border-color: #326862;");
        card.setPrefWidth(350);
        card.setPrefHeight(250);

        VBox infoBox = new VBox(10);
        infoBox.setStyle("-fx-padding: 10;");

        TextField nameField = new TextField(responsable.getNom() + " " + responsable.getPrenom());
        nameField.setEditable(false);
        nameField.setStyle("-fx-font-size: 14px; -fx-background-color: #adccbe; -fx-border-radius: 5px;");

        TextField telField = new TextField(responsable.getTel());
        telField.setEditable(false);
        telField.setStyle("-fx-font-size: 12px; -fx-background-color: #adccbe; -fx-border-radius: 5px;");

        TextField descriptionField = new TextField(responsable.getDescription());
        descriptionField.setEditable(false);
        descriptionField.setStyle("-fx-font-size: 12px; -fx-background-color: #adccbe; -fx-border-radius: 5px;");
        descriptionField.setPrefWidth(300);  // Largeur agrandie
        descriptionField.setPrefHeight(60);  // Hauteur agrandie

        // Champ pour attribuer un identifiant
        TextField identifiantField = new TextField();
        identifiantField.setPromptText("Attribuer un identifiant");
        identifiantField.setStyle("-fx-font-size: 12px; -fx-background-color: #f5e6cc; -fx-border-radius: 5px;");

        Button assignIdButton = new Button("Attribuer ID");
        assignIdButton.setStyle("-fx-background-color: #a8d0e6; -fx-text-fill: #ae9558; -fx-font-size: 12px; -fx-padding: 10; -fx-border-radius: 5px;");
        assignIdButton.setOnAction(e -> handleAssignId(responsable, identifiantField.getText()));

        infoBox.getChildren().addAll(nameField, telField, descriptionField, identifiantField, assignIdButton);
        card.getChildren().add(infoBox);

        return card;
    }


    private void handleAssignId(Responsable responsable, String newId) {
        if (newId.isEmpty()) {
            System.out.println("L'identifiant ne peut pas être vide !");
            return;
        }

        Connection connection = MySQLConnection.getInstance().getConnection();
        String query = "UPDATE responsable SET identifiant = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newId);
            stmt.setInt(2, responsable.getId());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Identifiant attribué avec succès !");
            } else {
                System.out.println("Échec de l'attribution de l'identifiant.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
