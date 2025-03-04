package Wedding.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.geometry.Side;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import javafx.scene.control.Label;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import tn.esprit.tacheuser.models.User;
import tn.esprit.tacheuser.utils.SessionManager;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController {
    private User currentUser; // Ajoutez ce champ

    @FXML
    private Label welcomeLabel;
    @FXML
    private Button loginButton;

    @FXML
    private Button logoutButton;

    @FXML
    private ImageView cartIcon;



    @FXML

    private void goToEvent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/reservation.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de la page des produits.");
        }
    }

    public void initialize(URL location, ResourceBundle resources) {
        // Récupérer l'utilisateur actuel depuis SessionManager
        currentUser = SessionManager.getUser();
        if (currentUser != null) {
            System.out.println("Utilisateur connecté dans HomeController : " + currentUser.getNom());
        } else {
            System.out.println("Aucun utilisateur connecté dans HomeController.");
        }
    }
    @FXML
    private void goToDriveAndStay(MouseEvent event) {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem voitureItem = new MenuItem("Voiture");
        MenuItem hebergementItem = new MenuItem("Hébergement");

        voitureItem.setOnAction(e -> goToVoiture());
        hebergementItem.setOnAction(e -> goToHebergement());

        contextMenu.getItems().addAll(voitureItem, hebergementItem);

        // Afficher le menu à la position du bouton
        Node source = (Node) event.getSource();
        contextMenu.show(source, Side.BOTTOM, 0, 0);
    }
    private void goToVoiture() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/voitureclient.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de la page des voitures.");
        }
    }

    private void goToHebergement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hebergementclient.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de la page des hébergements.");
        }
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





    public void setUser(User user) {
        this.currentUser = user;
        welcomeLabel.setText("Bienvenue, " + currentUser.getPrenom() + " !");
    }
    @FXML
    public void goToStore(javafx.event.ActionEvent event) {
        try {
            // Charger le fichier FXML de la page des produits
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Product.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur de la page des produits
            ProductController productController = loader.getController();

            // Passer l'utilisateur connecté au contrôleur
            productController.setCurrentUser(SessionManager.getUser()); // Assurez-vous que SessionManager.getUser() retourne l'utilisateur connecté

            // Récupérer la scène actuelle
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Changer la scène pour afficher la page des produits
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de la page des produits.");
        }
    }


    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
}
