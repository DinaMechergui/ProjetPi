package tn.esprit.tacheuser.contoller;
import tn.esprit.tacheuser.utils.FaceCapture;
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
import tn.esprit.tacheuser.utils.EmailSender;
import tn.esprit.tacheuser.utils.SessionManager;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javafx.scene.Parent;
import javafx.event.ActionEvent;
import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class Login {

    @FXML
    private TextField mailField;  // Garder cette déclaration unique
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
                loadUsersScreen();
            } else {
                Profile.setCurrentUser(authenticatedUser);
                AjoutReclamationController.setCurrentUser(authenticatedUser);
                loadProfileScreen();
            }
        } else {
            showError("Identifiants incorrects. Veuillez réessayer.");
        }
    }

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

    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erreur de Connexion");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleForgotPassword() {
        String email = mailField.getText().trim();
        if (email.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer votre email.");
            return;
        }

        // Générer un code 2FA
        String code = EmailSender.generateVerificationCode();

        // Stocker l'email et le code dans la session
        SessionManager.setResetEmail(email);
        SessionManager.setVerificationCode(code);

        // Envoyer l'email avec le code
        boolean emailSent = EmailSender.sendEmail(email, code);

        if (emailSent) {
            // Rediriger vers la page de réinitialisation du mot de passe
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/tacheuser/User/reset_password.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) mailField.getScene().getWindow(); // Récupérer la fenêtre actuelle
                stage.setScene(new Scene(root)); // Changer la scène
                stage.show(); // Afficher la nouvelle page

                System.out.println("✅ Code envoyé à : " + email);
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Erreur", "Impossible d'ouvrir la page de réinitialisation.");
            }
        } else {
            showAlert("Erreur", "L'envoi du code a échoué. Vérifiez votre email.");
        }
    }



    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
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

    public static boolean compareFaces(String username, String image1Path) {
        try {
            // Encoder l'image capturée en base64
            String image1Base64 = encodeImageToBase64(image1Path);

            // Construire le JSON à envoyer
            JSONObject json = new JSONObject();
            json.put("image1", image1Base64);
            json.put("username", username);  // Envoie également le nom d'utilisateur

            // Connexion au serveur Flask
            URL url = new URL("http://localhost:5000/compare_faces");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Envoyer le JSON
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.toString().getBytes());
                os.flush();
            }

            // Vérifier le code de réponse HTTP
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                System.out.println("Erreur HTTP : " + responseCode);
                return false;
            }

            // Lire la réponse complète
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                responseBuilder.append(line);
            }
            br.close();

            // Vérifier le résultat
            String response = responseBuilder.toString();
            System.out.println("Réponse du serveur : " + response);

            JSONObject jsonResponse = new JSONObject(response);
            return jsonResponse.getBoolean("match");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    // Méthode pour encoder une image en Base64
    private static String encodeImageToBase64(String imagePath) throws IOException {
        File file = new File(imagePath);
        byte[] fileContent = new byte[(int) file.length()];
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            fileInputStream.read(fileContent);
        }
        return Base64.getEncoder().encodeToString(fileContent);
    }


    public void handleLoginWithPhoto(ActionEvent event) {
        String email = mailField.getText(); // Récupérer l'email

        if (email.isEmpty()) {
            showError("Veuillez entrer votre email.");
            return;
        }

        // Ouvrir un FileChooser pour sélectionner une image
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            String selectedImagePath = selectedFile.getAbsolutePath(); // Récupérer le chemin de l'image

            // Vérifier la correspondance des visages
            boolean isMatch = compareFaces(email, selectedImagePath);

            if (isMatch) {
                // Vérifier si l'utilisateur existe dans la base de données
                UserService userService = new UserService();
                User authenticatedUser = userService.getUserByEmail(email); // Nouvelle méthode pour récupérer l'utilisateur

                if (authenticatedUser != null) {
                    showSuccess("Connexion réussie !");

                    if (email.equals("admin@admin.com")) {
                        loadUsersScreen(); // Rediriger vers l'interface admin
                    } else {
                        Profile.setCurrentUser(authenticatedUser);
                        AjoutReclamationController.setCurrentUser(authenticatedUser);
                        loadProfileScreen(); // Rediriger vers le profil
                    }
                } else {
                    showError("Utilisateur non trouvé.");
                }
            } else {
                showError("Échec de la reconnaissance faciale.");
            }
        } else {
            showError("Veuillez sélectionner une image.");
        }
    }

}

