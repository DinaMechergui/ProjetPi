package tn.esprit.tacheuser.contoller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.tacheuser.service.UserService;
import tn.esprit.tacheuser.models.User;

import java.io.IOException;

public class Register {

    @FXML
    private TextField nomField, prenomField, mailField, telField, genderField, ageField, statusField, roleField;
    @FXML
    private PasswordField passwordField;

    private final UserService userService = new UserService();

    @FXML
    private void handleRegister() {
        // Récupérer les valeurs des champs
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String mail = mailField.getText().trim();
        String tel = telField.getText().trim();
        String gender = genderField.getText().trim();
        String password = passwordField.getText().trim();
        String age = ageField.getText().trim();
        String status = statusField.getText().trim();
        String role = roleField.getText().trim();

        // Vérification des champs vides
        if (nom.isEmpty() || prenom.isEmpty() || mail.isEmpty() || tel.isEmpty() ||
                gender.isEmpty() || password.isEmpty() ||
                age.isEmpty() || status.isEmpty() || role.isEmpty()) {
            showAlert("Erreur", "Tous les champs doivent être remplis.", Alert.AlertType.ERROR);
            return;
        }

        // Vérification que l'âge est bien numérique
        if (!age.matches("\\d+")) {
            showAlert("Erreur", "L'âge doit être un nombre valide.", Alert.AlertType.ERROR);
            return;
        }

        // Vérification du format de l'email
        if (!mail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            showAlert("Erreur", "Adresse e-mail invalide.", Alert.AlertType.ERROR);
            return;
        }

        // Création de l'utilisateur
        User newUser = new User(0, nom, prenom, mail, tel, gender, age, password, status, role);

        // Enregistrement de l'utilisateur
        try {
            userService.addUser(newUser);
            showAlert("Succès", "Utilisateur inscrit avec succès !", Alert.AlertType.INFORMATION);
            goToLogin(); // Rediriger vers la page de connexion après inscription réussie
        } catch (Exception e) {
            showAlert("Erreur", "Échec de l'inscription : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/tacheuser/User/login.fxml"));
            Scene loginScene = new Scene(loader.load());

            Stage stage = (Stage) mailField.getScene().getWindow();
            stage.setScene(loginScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement de la page de connexion.", Alert.AlertType.ERROR);
        }
    }

    // Méthode pour afficher un message d'alerte
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
