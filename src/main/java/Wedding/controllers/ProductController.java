package Wedding.controllers;

import Wedding.entities.Commande;
import Wedding.entities.Produit;
import Wedding.entities.Reservation;
import Wedding.service.ServiceCommande;
import Wedding.service.ServiceProduit;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProductController {
    private ServiceProduit serviceProduit = new ServiceProduit();
    private ServiceCommande serviceCommande = new ServiceCommande();
    private Commande currentCommande;

    @FXML
    private GridPane gridPaneProduits;
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
    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText("Ceci est une alerte !");
        alert.showAndWait(); // Utilisez showAndWait() pour bloquer jusqu'à ce que l'utilisateur ferme l'alerte
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


    public void initialize() {
        try {
            loadProducts();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadProducts() throws SQLException {
        List<Produit> products = serviceProduit.afficher();
        int row = 0;
        int col = 0;

        for (Produit product : products) {
            // Créer une carte de produit
            VBox productCard = new VBox(10);
            productCard.getStyleClass().add("product-card");

            // Image du produit
            ImageView productImage = new ImageView(new Image(product.getImageUrl()));
            productImage.setFitWidth(150);
            productImage.setFitHeight(150);
            productImage.setPreserveRatio(true);

            // Nom du produit
            Label productName = new Label(product.getNom());
            productName.getStyleClass().add("product-name");

            // Prix du produit
            Label productPrice = new Label("Price: " + String.format("%.2f", product.getPrix()) + " TND");
            productPrice.getStyleClass().add("product-price");

            // Bouton "Reserve"
            Button reserveButton = new Button("Reserve");
            reserveButton.getStyleClass().addAll("button", "reserve-button"); // Correction ici
            reserveButton.setOnAction(event -> {
                try {
                    if (currentCommande == null) {
                        currentCommande = new Commande(0, "User1", LocalDateTime.now(), "RESERVE", new ArrayList<>());
                    }

                    Reservation reservationExistante = trouverReservationExistante(product);

                    if (reservationExistante != null) {
                        reservationExistante.setQuantite(reservationExistante.getQuantite() + 1);
                    } else {
                        Reservation nouvelleReservation = new Reservation(null, currentCommande, product, 1);
                        currentCommande.ajouterReservation(nouvelleReservation);
                    }

                    int commandeId = serviceCommande.ajouterOuMettreAJourReservation("User1", product);

                    if (currentCommande.getId() == 0) {
                        currentCommande.setId(commandeId);
                    }

                    System.out.println("Produit ajouté au panier : " + product.getNom());

                    // 🚀 **Ajout de l'alerte ici**
                    showAlert("Réservation réussie", "Le produit '" + product.getNom() + "' a été réservé avec succès !", Alert.AlertType.INFORMATION);

                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Erreur", "Une erreur s'est produite lors de la réservation.", Alert.AlertType.ERROR);
                }
            });

            // Ajouter les éléments à la carte
            productCard.getChildren().addAll(productImage, productName, productPrice, reserveButton);

            // Ajouter la carte au GridPane
            gridPaneProduits.add(productCard, col, row);

            // Passer à la colonne suivante
            col++;
            if (col > 2) { // 3 colonnes par ligne
                col = 0;
                row++;
            }
        }
    }
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Reservation trouverReservationExistante(Produit produit) {
        for (Reservation reservation : currentCommande.getReservations()) {
            if (reservation.getProduit().equals(produit)) {
                return reservation; // Retourne la réservation existante
            }
        }
        return null; // Aucune réservation trouvée
    }

    @FXML
    public void goToCart(MouseEvent event) {
        try {
            // Charger la page du panier
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Cart.fxml"));
            Parent root = loader.load();

            // Récupérer la scène actuelle pour accéder à la fenêtre (Stage)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root)); // Changer la scène vers la page du panier
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}