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

import java.util.ResourceBundle;

public class Users implements Initializable {

    @FXML private ListView<String> usersListView;
    @FXML private TextField nomField, prenomField, mailField, telField, roleField;

    private UserService userService = new UserService();
    private User selectedUser;  // Stores selected user for update/delete

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadUsers();
    }

    // Load users into ListView
    private void loadUsers() {
        usersListView.getItems().clear();
        List<User> users = userService.getAllUsers();

        for (User user : users) {
            usersListView.getItems().add(user.getId() + " - " + user.getNom() + " " + user.getPrenom() + " - " + user.getMail());
        }
    }

    // Handle selection in the ListView
    @FXML
    private void handleUserSelection() {
        String selected = usersListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            int userId = Integer.parseInt(selected.split(" - ")[0]);  // Extract user ID
            selectedUser = userService.getUserById(userId);

            if (selectedUser != null) {
                nomField.setText(selectedUser.getNom());
                prenomField.setText(selectedUser.getPrenom());
                mailField.setText(selectedUser.getMail());
                telField.setText(selectedUser.getTel());
                roleField.setText(selectedUser.getRole());
            }
        }
    }

    @FXML
    private void goToReclamations() {
        try {
            System.out.println("Chargement de reclamation.fxml...");
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

    // Méthode générique pour afficher les alertes
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Add a new user
    @FXML
    private void handleAdd() {
        User newUser = new User(0, nomField.getText(), prenomField.getText(), mailField.getText(),
                telField.getText(), "", "", "", roleField.getText());

        userService.addUser(newUser);
        clearFields();
        loadUsers();
    }

    // Update selected user
    @FXML
    private void handleUpdate() {
        if (selectedUser != null) {
            selectedUser.setNom(nomField.getText());
            selectedUser.setPrenom(prenomField.getText());
            selectedUser.setMail(mailField.getText());
            selectedUser.setTel(telField.getText());
            selectedUser.setRole(roleField.getText());

            userService.updateUser(selectedUser);
            clearFields();
            loadUsers();
        } else {
            showAlert("Sélectionner un utilisateur à modifier !");
        }
    }

    // Delete selected user
    @FXML
    private void handleDelete() {
        if (selectedUser != null) {
            userService.deleteUser(selectedUser.getId());
            clearFields();
            loadUsers();
        } else {
            showAlert("Sélectionner un utilisateur à supprimer !");
        }
    }

    // Clear input fields
    private void clearFields() {
        nomField.clear();
        prenomField.clear();
        mailField.clear();
        telField.clear();
        roleField.clear();
        selectedUser = null;
    }

    // Display an alert message
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attention");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
