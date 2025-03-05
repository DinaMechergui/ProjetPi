package tn.esprit.tacheuser.contoller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import tn.esprit.tacheuser.service.UserService;
import tn.esprit.tacheuser.utils.SessionManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.Node;
import javafx.event.ActionEvent;


public class ResetPasswordController {

    @FXML
    private TextField codeField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button resetButton;

    private final UserService userService = new UserService();

    @FXML
    private void handleResetPassword() {
        String enteredCode = codeField.getText().trim();
        String newPassword = newPasswordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();
        String storedCode = SessionManager.getVerificationCode();
        String email = SessionManager.getResetEmail();

        if (enteredCode.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Erreur", "Tous les champs doivent être remplis.");
            return;
        }

        if (!enteredCode.equals(storedCode)) {
            showAlert("Erreur", "Le code de vérification est incorrect.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showAlert("Erreur", "Les mots de passe ne correspondent pas.");
            return;
        }

        if (newPassword.length() < 6) {
            showAlert("Erreur", "Le mot de passe doit contenir au moins 6 caractères.");
            return;
        }

        boolean passwordUpdated = userService.updatePassword(email, newPassword);

        if (passwordUpdated) {
            showAlert("Succès", "Le mot de passe a été mis à jour avec succès.");
            SessionManager.clearResetSession();
        } else {
            showAlert("Erreur", "Échec de la mise à jour du mot de passe.");
        }
    }
    @FXML
    private void handleGoToLoginn(ActionEvent event) { // Ajout du paramètre event
        try {
            // Load the login FXML page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/tacheuser/User/login.fxml"));
            Parent root = loader.load();

            // Get the current stage from the event source
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.show();

            System.out.println("✅ Redirecting to login page.");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page de connexion.");
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
