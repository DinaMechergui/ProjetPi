package tn.esprit.tacheuser.contoller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import tn.esprit.tacheuser.models.Responsable;
import tn.esprit.tacheuser.utils.MySQLConnection;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import javafx.scene.Scene;
import javafx.stage.Stage; // Assurez-vous d'importer Stage

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.scene.Parent;

public class ResponsableInscriptionController {

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private TextField telField;

    @FXML
    private Button submitButton;

    @FXML
    private void handleSubmit() {
        // Get values from the form fields
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String description = descriptionField.getText();
        String tel = telField.getText();

        // Validation: Check if all required fields are filled
        if (nom.isEmpty() || prenom.isEmpty() || description.isEmpty() || tel.isEmpty()) {
            showAlert(AlertType.ERROR, "Erreur", "Tous les champs sont obligatoires !");
            return;
        }

        // Prepare SQL query to insert the responsable data into the database
        String sql = "INSERT INTO responsable (nom, prenom, description, tel) VALUES (?, ?, ?, ?)";

        // Use the singleton MySQLConnection instance to get the connection
        try (Connection conn = MySQLConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set the parameters for the SQL query
            stmt.setString(1, nom);
            stmt.setString(2, prenom);
            stmt.setString(3, description);
            stmt.setString(4, tel);

            // Execute the query
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                showAlert(AlertType.INFORMATION, "Succès", "Responsable inscrit avec succès !");
                clearForm();
            } else {
                showAlert(AlertType.ERROR, "Erreur", "Erreur lors de l'inscription du responsable.");
            }

        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Erreur de base de données", "Une erreur s'est produite lors de la connexion à la base de données.");
            e.printStackTrace();
        }
    }
    @FXML
    private void handleTakeMeToLogin() {
        // Charger le fichier FXML pour la scène de connexion
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/tacheuser/User/login.fxml"));
            Parent loginScene = loader.load();

            // Obtenir la scène actuelle et définir la nouvelle scène de connexion
            Scene scene = new Scene(loginScene);
            Stage stage = (Stage) submitButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Erreur", "Une erreur s'est produite lors du chargement de la scène de connexion.");
            e.printStackTrace();
        }
    }
    @FXML
    private void handleTakeMeToLoginresponsable() {
        // Charger le fichier FXML pour la scène de connexion
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/tacheuser/User/ResponsableLogin.fxml"));
            Parent loginScene = loader.load();

            // Obtenir la scène actuelle et définir la nouvelle scène de connexion
            Scene scene = new Scene(loginScene);
            Stage stage = (Stage) submitButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Erreur", "Une erreur s'est produite lors du chargement de la scène de connexion.");
            e.printStackTrace();
        }
    }
    // Method to show alerts
    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Method to clear the form fields after successful submission
    private void clearForm() {
        nomField.clear();
        prenomField.clear();
        descriptionField.clear();
        telField.clear();
    }
}
