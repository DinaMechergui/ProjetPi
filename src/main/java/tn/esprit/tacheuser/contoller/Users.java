package tn.esprit.tacheuser.contoller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import tn.esprit.tacheuser.service.UserService;
import tn.esprit.tacheuser.models.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;
import java.util.List;
import java.io.IOException;
import javafx.scene.input.MouseEvent;
import javafx.scene.Node;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import javafx.stage.Stage;

public class Users implements Initializable {

    @FXML private ListView<User> usersListView;
    @FXML private TextField nomField, prenomField, mailField, telField, roleField;

    private UserService userService = new UserService();
    private User selectedUser;  // Stores selected user for update/delete

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadUsers();
    }
    @FXML
    private void goToAdminResponsable(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/tacheuser/User/admin_responsable.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Gestion des Responsables");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Load users into ListView
    private void loadUsers() {
        usersListView.getItems().clear();
        List<User> users = userService.getAllUsers(); // Get the list of users from the database

        for (User user : users) {
            usersListView.getItems().add(user);  // Add User objects to the ListView
        }

        // Set a custom cell factory to display user name and email, without showing the ID
        usersListView.setCellFactory(param -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                } else {
                    // Display user name and email (not the ID)
                    setText(user.getNom() + " " + user.getPrenom() + " - " + user.getMail());
                }
            }
        });
    }

    // Handle selection in the ListView
    @FXML
    private void handleUserSelection() {
        selectedUser = usersListView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            // Set fields to the selected user values
            nomField.setText(selectedUser.getNom());
            prenomField.setText(selectedUser.getPrenom());
            mailField.setText(selectedUser.getMail());
            telField.setText(selectedUser.getTel());
            roleField.setText(selectedUser.getRole());
        }
    }

    @FXML
    public void handleImageClick(MouseEvent event) throws IOException {
        // Load the new scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/tacheuser/User/User2.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void handlestat(MouseEvent event) throws IOException {
        // Charger le nouveau FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/tacheuser/User/stat.fxml"));
          // Assurez-vous que le contrôleur est bien défini
        Parent root = loader.load();

        // Récupérer le stage à partir de l'événement (c'est-à-dire la scène du bouton/cliqueur)
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Changer la scène
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    public void handleexportpdf(MouseEvent event) throws IOException {
        Document document = new Document();
        try {
            // Spécifier le fichier de sortie pour le PDF
            PdfWriter.getInstance(document, new FileOutputStream("users_list.pdf"));

            // Ouvrir le document pour écrire
            document.open();

            // Titre du document
            Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
            Paragraph title = new Paragraph("Liste des Utilisateurs", titleFont);
            document.add(title);

            // Espacement avant la liste
            document.add(new Paragraph("\n"));

            // Obtenir la liste des utilisateurs
            List<User> users = userService.getAllUsers();

            // Style de texte pour les informations des utilisateurs
            Font userFont = new Font(Font.FontFamily.TIMES_ROMAN, 12);

            // Ajouter chaque utilisateur dans le PDF
            for (User user : users) {
                String userDetails = "Nom: " + user.getNom() + "\n" +
                        "Prénom: " + user.getPrenom() + "\n" +
                        "Email: " + user.getMail() + "\n" +
                        "Téléphone: " + user.getTel() + "\n" +
                        "Rôle: " + user.getRole() + "\n";
                document.add(new Paragraph(userDetails, userFont));
                document.add(new Paragraph("\n"));
            }

            // Fermer le document
            document.close();

            // Afficher un message de confirmation
            showAlert("Succès", "Le PDF a été exporté avec succès!", Alert.AlertType.INFORMATION);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'exportation du PDF.", Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void goToReclamations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/tacheuser/User/reclamation.fxml"));
            Scene reclamationScene = new Scene(loader.load());

            Stage stage = (Stage) usersListView.getScene().getWindow();
            stage.setScene(reclamationScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement de la page des réclamations.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleAdd() {
        User newUser = new User(0, nomField.getText(), prenomField.getText(), mailField.getText(),
                telField.getText(), "", "", "", roleField.getText());

        userService.addUser(newUser);
        clearFields();
        loadUsers();  // Reload users after adding
    }

    @FXML
    private void handleUpdate() {
        selectedUser = usersListView.getSelectionModel().getSelectedItem(); // Ensure selectedUser is updated from ListView
        if (selectedUser != null) {
            // Update the User object with values from the fields
            selectedUser.setNom(nomField.getText());
            selectedUser.setPrenom(prenomField.getText());
            selectedUser.setMail(mailField.getText());
            selectedUser.setTel(telField.getText());
            selectedUser.setRole(roleField.getText());

            // Call the service to update the user in the database
            userService.updateUser(selectedUser);
            clearFields();
            loadUsers();  // Reload users after updating
        } else {
            showAlert("Sélectionner un utilisateur à modifier !");
        }
    }

    @FXML
    private void handleDelete() {
        selectedUser = usersListView.getSelectionModel().getSelectedItem(); // Ensure selectedUser is updated from ListView
        if (selectedUser != null) {
            // Call the service to delete the user from the database using the ID
            userService.deleteUser(selectedUser.getId());
            clearFields();
            loadUsers();  // Reload users after deleting
        } else {
            showAlert("Sélectionner un utilisateur à supprimer !");
        }
    }

    private void clearFields() {
        nomField.clear();
        prenomField.clear();
        mailField.clear();
        telField.clear();
        roleField.clear();
        selectedUser = null;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attention");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
