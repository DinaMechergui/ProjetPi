package tn.esprit.tacheuser.contoller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.tacheuser.models.User;

import java.io.IOException;

public class Profile {

    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField ageField;
    @FXML
    private TextField genderField;

    private static User currentUser; // Stocke l'utilisateur connecté

    // Méthode pour définir l'utilisateur connecté
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    @FXML
    private void initialize() {
        if (currentUser != null) {
            nameField.setText(currentUser.getNom() + " " + currentUser.getPrenom());
            emailField.setText(currentUser.getMail());
            phoneField.setText(currentUser.getTel());
            ageField.setText(currentUser.getAge());
            genderField.setText(currentUser.getGender());
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/tacheuser/User/login.fxml"));
            Scene loginScene = new Scene(loader.load());

            // Récupérer la scène actuelle et la remplacer
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(loginScene);
            stage.show();
        } catch (IOException e) {
            System.out.println("❌ Erreur lors de la déconnexion : " + e.getMessage());
        }
    }

    // Méthode pour naviguer vers l'écran de réclamation
    @FXML
    private void handleGoToReclamation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/tacheuser/User/ajoutreclamtion.fxml"));
            Scene reclamationScene = new Scene(loader.load());

            // Récupérer la scène actuelle et la remplacer
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(reclamationScene);
            stage.show();
        } catch (IOException e) {
            System.out.println("❌ Erreur lors de la navigation vers la réclamation : " + e.getMessage());
        }
    }
}
