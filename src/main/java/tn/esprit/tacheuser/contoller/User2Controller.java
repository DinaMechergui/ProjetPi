package tn.esprit.tacheuser.contoller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.tacheuser.models.User;
import tn.esprit.tacheuser.utils.MySQLConnection;

import java.sql.*;
import java.io.IOException;

public class User2Controller {

    @FXML
    private GridPane userGrid;

    @FXML
    private Button navigateButton;

    @FXML
    public void initialize() {
        // Connexion à la base de données via la classe MySQLConnection
        MySQLConnection dbConnection = MySQLConnection.getInstance();
        Connection connection = dbConnection.getConnection();

        try {
            String query = "SELECT * FROM user";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            int row = 0;
            int col = 0;

            userGrid.setStyle("-fx-grid-lines-visible: true; -fx-hgap: 15; -fx-vgap: 15;");
            userGrid.setPrefWidth(800);
            userGrid.setPrefHeight(600);

            while (resultSet.next()) {
                User user = new User(
                        resultSet.getInt("id"),
                        resultSet.getString("nom"),
                        resultSet.getString("prenom"),
                        resultSet.getString("mail"),
                        resultSet.getString("tel"),
                        resultSet.getString("gender"),
                        resultSet.getString("age"),
                        resultSet.getString("password"),
                        resultSet.getString("role")
                );

                HBox userCard = createUserCard(user);
                userGrid.add(userCard, col, row);

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

    private HBox createUserCard(User user) {
        HBox card = new HBox(15);
        // Changer la couleur de fond et bordure de la carte pour une couleur pastel
        card.setStyle("-fx-background-color: #e0e5e8; -fx-padding: 20; -fx-border-radius: 10; -fx-border-width: 2; -fx-border-color: #326862;");
        card.setPrefWidth(350);
        card.setPrefHeight(250);

        VBox userInfo = new VBox(10);
        userInfo.setStyle("-fx-padding: 10;");

        // Changer la couleur de fond des champs de texte en pastel
        TextField nameField = new TextField(user.getNom() + " " + user.getPrenom());
        nameField.setStyle("-fx-font-size: 14px; -fx-background-color: #adccbe; -fx-border-radius: 5px;");
        TextField emailField = new TextField(user.getMail());
        emailField.setStyle("-fx-font-size: 12px; -fx-background-color: #adccbe; -fx-border-radius: 5px;");
        TextField telField = new TextField(user.getTel());
        telField.setStyle("-fx-font-size: 12px; -fx-background-color: #adccbe; -fx-border-radius: 5px;");
        TextField genderField = new TextField(user.getGender());
        genderField.setStyle("-fx-font-size: 12px; -fx-background-color: #adccbe; -fx-border-radius: 5px;");
        TextField ageField = new TextField(user.getAge());
        ageField.setStyle("-fx-font-size: 12px; -fx-background-color: #adccbe; -fx-border-radius: 5px;");

        userInfo.getChildren().addAll(nameField, emailField, telField, genderField, ageField);

        // Boutons avec couleurs pastel
        Button updateButton = new Button("Update");
        Button deleteButton = new Button("Delete");

        updateButton.setStyle("-fx-background-color: #a8d0e6; -fx-text-fill: #ae9558; -fx-font-size: 12px; -fx-padding: 10; -fx-border-radius: 5px;");
        deleteButton.setStyle("-fx-background-color: #e4c1be; -fx-text-fill: #8e6763; -fx-font-size: 12px; -fx-padding: 10; -fx-border-radius: 5px;");

        updateButton.setOnAction(e -> handleUpdate(user, nameField.getText(), emailField.getText(), telField.getText(), genderField.getText(), ageField.getText()));
        deleteButton.setOnAction(e -> handleDelete(user, card));

        userInfo.getChildren().addAll(updateButton, deleteButton);
        card.getChildren().add(userInfo);

        return card;
    }

    private void handleUpdate(User user, String newName, String newEmail, String newTel, String newGender, String newAge) {
        System.out.println("Updating user: " + user.getNom());
        // Mettre à jour la base de données...
    }

    private void handleDelete(User user, HBox card) {
        System.out.println("Deleting user: " + user.getNom());
        // Supprimer l'utilisateur de la base de données...
        userGrid.getChildren().remove(card);  // Supprimer l'élément de l'interface
    }

    private void handleAdd(User user) {
        System.out.println("Add new user!");
    }

    @FXML
    private void handleNavigate() throws IOException {
        // Charger une nouvelle vue FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/tacheuser/User/users.fxml"));
        Scene scene = new Scene(loader.load());

        // Obtenir la scène actuelle et la changer
        Stage stage = (Stage) navigateButton.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

}
