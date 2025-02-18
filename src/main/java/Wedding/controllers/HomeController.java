package Wedding.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.IOException;

public class HomeController {

    @FXML
    private Button loginButton;

    @FXML
    private Button logoutButton;

    @FXML
    private ImageView cartIcon;

    @FXML
    private void goToStore() {
        try {
            // Charger le fichier FXML de la page des produits
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Product.fxml"));
            Parent root = loader.load();

            // Récupérer la scène actuelle
            Stage stage = (Stage) loginButton.getScene().getWindow();

            // Changer la scène pour afficher la page des produits
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de la page des produits.");
        }
    }

    @FXML
    private void goToEvent() {
        // Logique pour aller à la page Event
        System.out.println("Naviguer vers Event");
    }

    @FXML
    private void goToDriveAndStay() {
        // Logique pour aller à la page Drive and Stay
        System.out.println("Naviguer vers Drive and Stay");
    }

    @FXML
    private void goToInvite() {
        // Logique pour aller à la page Invité
        System.out.println("Naviguer vers Invité");
    }

    @FXML
    private void handleLogin() {
        // Logique pour le login
        System.out.println("Login cliqué");
        loginButton.setVisible(false);
        logoutButton.setVisible(true);
    }

    @FXML
    private void handleLogout() {
        // Logique pour le logout
        System.out.println("Logout cliqué");
        logoutButton.setVisible(false);
        loginButton.setVisible(true);
    }

    @FXML
    private void goToCart(MouseEvent event) {
        // Logique pour aller à la page du panier
        System.out.println("Icône du panier cliquée");
    }
}