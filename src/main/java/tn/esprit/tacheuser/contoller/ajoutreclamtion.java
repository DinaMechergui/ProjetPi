package tn.esprit.tacheuser.contoller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import tn.esprit.tacheuser.models.Reclamation;
import tn.esprit.tacheuser.utils.MySQLConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ajoutreclamtion {

    @FXML
    private TextField titleField;
    @FXML
    private TextArea descriptionField;

    // Handle reclamation submission
    @FXML
    private void handleSubmitReclamation() {
        String title = titleField.getText();
        String description = descriptionField.getText();

        Reclamation reclamation = new Reclamation();
        reclamation.setSujet(title);
        reclamation.setDescription(description);

        // Call the addReclamation method to insert it into the database
        if (addReclamation(reclamation)) {
            System.out.println("Réclamation soumise ! Titre: " + title + ", Description: " + description);
        } else {
            System.out.println("❌ Erreur lors de l'ajout de la réclamation.");
        }

        System.out.println("Réclamation soumise ! Titre: " + title + ", Description: " + description);
    }

    // Navigate back to the profile page
    @FXML
    private void handleBackToProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/tacheuser/User/profil.fxml"));
            Scene profileScene = new Scene(loader.load());

            // Récupérer la scène actuelle et la remplacer
            Stage stage = (Stage) titleField.getScene().getWindow();
            stage.setScene(profileScene);
            stage.show();
        } catch (IOException e) {
            System.out.println("❌ Erreur lors du retour au profil : " + e.getMessage());
        }
    }
    public boolean addReclamation(Reclamation reclamation) {
        String query = "INSERT INTO reclamations (sujet, description, statut) VALUES (?, ?, ?)";
        try (Connection conn = MySQLConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, reclamation.getSujet());
            stmt.setString(2, reclamation.getDescription());
            stmt.setString(3, "En attente");
            stmt.executeUpdate();
            System.out.println("✅ Réclamation ajoutée !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout : " + e.getMessage());
        }
        return false;
    }
}
