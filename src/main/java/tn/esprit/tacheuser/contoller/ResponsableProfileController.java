package tn.esprit.tacheuser.contoller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import tn.esprit.tacheuser.models.Responsable;
import tn.esprit.tacheuser.utils.MySQLConnection;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.control.Alert;

import java.sql.*;

public class ResponsableProfileController {

    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField telField;

    private Responsable responsable;
    public void setResponsable(Responsable responsable) {
        this.responsable = responsable;
        // Set data to fields
        if (responsable != null) {
            nomField.setText(responsable.getNom());
            prenomField.setText(responsable.getPrenom());
            descriptionField.setText(responsable.getDescription());
            telField.setText(responsable.getTel());
        }
    }
    @FXML
    public void initialize() {
        // Fetch the responsable's data from the database (simulating the logged-in responsable)
        responsable = getLoggedInResponsable();

        if (responsable != null) {
            // Set data to fields
            nomField.setText(responsable.getNom());
            prenomField.setText(responsable.getPrenom());
            descriptionField.setText(responsable.getDescription());
            telField.setText(responsable.getTel());
        }
    }

    private Responsable getLoggedInResponsable() {
        Responsable loggedInResponsable = null;
        Connection connection = MySQLConnection.getInstance().getConnection();

        // Replace this with logic to get the logged-in user's ID (session management)
        int loggedInResponsableId = 1;  // For example, getting a sample logged-in Responsable

        String query = "SELECT * FROM responsable WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, loggedInResponsableId);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                loggedInResponsable = new Responsable(
                        resultSet.getInt("id"),
                        resultSet.getString("nom"),
                        resultSet.getString("prenom"),
                        resultSet.getString("description"),
                        resultSet.getString("tel")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loggedInResponsable;
    }

    // Handle Logout action (navigate back to the login page)

    @FXML
    private void handleLogout() {
        try {
            // Load the login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/tacheuser/User/login.fxml"));
            Stage stage = (Stage) nomField.getScene().getWindow();  // Get the current window (stage)
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("An error occurred while loading the login screen.");
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
