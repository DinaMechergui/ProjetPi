package tn.esprit.tacheuser.contoller;

import Wedding.controllers.HomeController;
import Wedding.controllers.ProductController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import tn.esprit.tacheuser.service.UserService;
import tn.esprit.tacheuser.models.User;
import tn.esprit.tacheuser.utils.SessionManager;

import java.io.IOException;

public class Login {
    private User authenticatedUser; // Déclaration en tant que champ de classe

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

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Veuillez entrer une adresse email valide.");
            return;
        }

        UserService userService = new UserService();
        User authenticatedUser = userService.authenticate(email, password);

        if (authenticatedUser != null) {
            showSuccess("Connexion réussie !");

            // Stocker l'utilisateur dans SessionManager
            SessionManager.setUser(authenticatedUser);

            // Redirection vers la page Home
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home.fxml"));
                Parent root = loader.load();

                // Récupérer le contrôleur de la page Home
                HomeController homeController = loader.getController();

                // Passer l'utilisateur connecté au contrôleur de la page Home
                homeController.setUser(authenticatedUser);

                // Afficher la scène
                Stage stage = (Stage) mailField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showError("Erreur lors du chargement de la page d'accueil.");
            }
        } else {
            showError("Identifiants incorrects. Veuillez réessayer.");
        }
    }   private void loadUsersScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/tacheuser/User/users.fxml"));
            Scene usersScene = new Scene(loader.load());

            Stage stage = (Stage) mailField.getScene().getWindow();
            stage.setScene(usersScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors du chargement de la gestion des utilisateurs.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erreur de Connexion");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleNavigateToResponsableInscription() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/tacheuser/User/responsable_inscription.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) mailField.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Connexion réussie");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadProfileScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/tacheuser/User/profil.fxml"));
            Scene profileScene = new Scene(loader.load());

            Stage stage = (Stage) mailField.getScene().getWindow();
            stage.setScene(profileScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors du chargement du profil.");
        }
    }

    @FXML
    private void handleRegister() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/tacheuser/User/register.fxml"));
        Scene registerScene = new Scene(loader.load());

        Stage stage = (Stage) mailField.getScene().getWindow();
        stage.setScene(registerScene);
        stage.show();
    }

    private void loadHomeScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/home.fxml"));
            Scene homeScene = new Scene(loader.load());

            Stage stage = (Stage) mailField.getScene().getWindow();
            stage.setScene(homeScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors du chargement de la page d'accueil.");
        }
    }
}