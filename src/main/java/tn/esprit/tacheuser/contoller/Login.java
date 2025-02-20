package tn.esprit.tacheuser.contoller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import tn.esprit.tacheuser.service.UserService;
import tn.esprit.tacheuser.models.User;

import java.io.IOException;

public class Login {

    @FXML
    private TextField mailField;
    @FXML
    private PasswordField passwordField;


    @FXML

    private void handleLogin() {
        String email = mailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Veuillez remplir tous les champs.");
            return;
        }

        UserService userService = new UserService();
        User authenticatedUser = userService.authenticate(email, password);

        if (authenticatedUser != null) {
            showSuccess("Connexion réussie !");

            if (email.equals("admin@admin.com") && password.equals("123456")) {
                loadUsersScreen(); // Rediriger l'admin vers users.fxml
            } else {
                Profile.setCurrentUser(authenticatedUser); // Stocker l'utilisateur connecté
                loadProfileScreen();  // Rediriger vers le profil utilisateur
            }
        } else {
            showError("Identifiants incorrects. Veuillez réessayer.");
        }
    }

    // Charger users.fxml
    private void loadUsersScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/tacheuser/User/users.fxml"));
            Scene usersScene = new Scene(loader.load());

            Stage stage = (Stage) mailField.getScene().getWindow();
            stage.setScene(usersScene);
            stage.show();
        } catch (IOException e) {
            showError("Erreur lors du chargement de la gestion des utilisateurs.");
        }
    }

    // Afficher un message d'erreur
    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erreur de Connexion");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Afficher un message de succès
    private void showSuccess(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Connexion réussie");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Après une connexion réussie
    private void loadProfileScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/tacheuser/User/profil.fxml"));  // Remplacer par le bon chemin
            Scene profileScene = new Scene(loader.load());

            // Récupérer la scène actuelle et la remplacer
            Stage stage = (Stage) mailField.getScene().getWindow();
            stage.setScene(profileScene);
            stage.show();
        } catch (IOException e) {
            showError("Erreur lors du chargement du profil.");
        }
    }


    @FXML
    private void handleRegister() throws IOException {
        // Charger la scène d'inscription
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/tacheuser/User/register.fxml"));
        Scene registerScene = new Scene(loader.load());

        // Récupérer la scène actuelle et la remplacer
        Stage stage = (Stage) mailField.getScene().getWindow();
        stage.setScene(registerScene);
        stage.show();
    }
}
