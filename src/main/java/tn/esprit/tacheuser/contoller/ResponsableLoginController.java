package tn.esprit.tacheuser.contoller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tn.esprit.tacheuser.models.Responsable;
import tn.esprit.tacheuser.utils.MySQLConnection;

import java.sql.*;
import java.io.IOException;

public class ResponsableLoginController {

    @FXML
    private TextField identifiantField;

    @FXML
    private void handleLogin() {
        String identifiant = identifiantField.getText().trim();

        if (identifiant.isEmpty()) {
            showAlert("Identifiant cannot be empty.");
        } else {
            // Proceed with login logic
            Responsable responsable = verifyLogin(identifiant);
            if (responsable != null) {
                // Login successful, load the profile page
                System.out.println("Logging in with Identifiant: " + identifiant);
                loadProfilePage(responsable);
            } else {
                showAlert("Invalid Identifiant.");
            }
        }
    }

    private Responsable verifyLogin(String identifiant) {
        Responsable responsable = null;
        Connection connection = MySQLConnection.getInstance().getConnection();
        String query = "SELECT * FROM responsable WHERE identifiant = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, identifiant);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                // Login successful, create Responsable object
                responsable = new Responsable(
                        resultSet.getInt("id"),
                        resultSet.getString("nom"),
                        resultSet.getString("prenom"),
                        resultSet.getString("description"),
                        resultSet.getString("tel")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("An error occurred while connecting to the database.");
        }
        return responsable;
    }

    private void loadProfilePage(Responsable responsable) {
        try {


            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/tacheuser/User/profilresponsable.fxml"));
            Scene profileScene = new Scene(loader.load());

            // Pass the Responsable object to the profile controller
            ResponsableProfileController controller = loader.getController();
            controller.setResponsable(responsable);

            // Get the current stage and switch to profile page
            Stage currentStage = (Stage) identifiantField.getScene().getWindow();
            currentStage.setScene(profileScene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to load the profile page.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
