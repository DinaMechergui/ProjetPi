package Wedding.controllers;

import Wedding.entities.Commande;
import Wedding.entities.Facture;
import Wedding.entities.Produit;
import Wedding.service.ServiceCommande;
import Wedding.service.ServiceFacture;
import Wedding.utils.MyDatabase;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CartController {
    private final ServiceCommande serviceCommande = new ServiceCommande();
    private Commande currentCommande;  // La commande actuelle

    @FXML
    private GridPane gridPaneCart;
    @FXML
    private Label totalPriceLabel;
    public void initialize() {
        try {
            initializeCurrentCommande(); // Assure que la commande est bien initialisée
            loadCart(); // Charge les produits du panier
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goToCommand(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Commande.fxml"));
        Parent commandParent = loader.load();
        Scene commandScene = new Scene(commandParent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(commandScene);
    }

    private void initializeCurrentCommande() throws SQLException {
        if (currentCommande == null || currentCommande.getId() == -1) {
            String sql = "SELECT id FROM commande WHERE utilisateur = ? AND statut = 'RESERVE' ORDER BY date DESC LIMIT 1";
            try (PreparedStatement stmt = MyDatabase.getConnection().prepareStatement(sql)) {
                stmt.setString(1, "User1"); // Remplacer par l'utilisateur actuel
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    currentCommande = new Commande();
                    currentCommande.setId(rs.getInt("id"));
                    System.out.println("Commande trouvée : ID = " + currentCommande.getId());
                } else {
                    System.out.println("Aucune commande en attente trouvée.");
                }
            }
        }
    }

    private void loadCart() throws SQLException {
        if (currentCommande == null || currentCommande.getId() == -1) {
            System.out.println("Aucune commande en attente trouvée.");
            return;
        }

        List<Produit> cartProducts = serviceCommande.getProduitsDansPanier(currentCommande.getId());
        if (cartProducts == null || cartProducts.isEmpty()) {
            System.out.println("Le panier est vide.");
            totalPriceLabel.setText("Total: 0.00 TND"); // Mettre à jour le total à 0 si le panier est vide
            return;
        }

        gridPaneCart.getChildren().clear(); // Nettoyer l'affichage
        int row = 0;
        for (Produit produit : cartProducts) {
            // Créer un ImageView pour l'image du produit
            ImageView productImageView = new ImageView();
            try {
                // Charger l'image depuis l'URL stockée dans la base de données
                String imageUrl = produit.getImageUrl();
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Image productImage = new Image(imageUrl);
                    productImageView.setImage(productImage); // Définir l'image dans l'ImageView
                    productImageView.setFitWidth(90); // Ajuster la largeur de l'image
                    productImageView.setFitHeight(90); // Ajuster la hauteur de l'image
                    productImageView.setPreserveRatio(true); // Maintenir le ratio
                } else {
                    System.out.println("Aucune URL d'image trouvée pour le produit : " + produit.getNom());
                }
            } catch (Exception e) {
                productImageView.setImage(null); // En cas d'erreur, ne pas afficher d'image
                System.out.println("Erreur lors du chargement de l'image : " + e.getMessage());
            }

            // Créer les labels pour le nom et le prix du produit
            Label productName = new Label(produit.getNom());
            productName.setFont(Font.font("Georgia", 18));
            productName.setTextFill(Color.web("#6d8c7a"));

            Label productPrice = new Label("Prix: " + String.format("%.2f", produit.getPrix()) + " TND");
            productPrice.setFont(Font.font("Georgia", 16));
            productPrice.setTextFill(Color.web("#6d8c7a"));

            // Créer le bouton "Supprimer"
            Button removeButton = new Button("Supprimer");
            removeButton.setStyle("-fx-background-color: #e14d3c; -fx-text-fill: white; -fx-font-size: 14px;");
            removeButton.setOnAction(event -> {
                try {
                    serviceCommande.removeProductFromCart(currentCommande.getId(), produit.getId());
                    loadCart(); // Rafraîchir le panier après suppression
                    showAlert("Suppression réussie", "Le produit '" + produit.getNom() + "' a été supprimé du panier.", Alert.AlertType.INFORMATION);
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Erreur", "Une erreur est survenue lors de la suppression.", Alert.AlertType.ERROR);
                }
            });

            // Ajouter les éléments à la GridPane
            gridPaneCart.add(productImageView, 0, row); // Image du produit
            gridPaneCart.add(productName, 1, row); // Nom du produit
            gridPaneCart.add(productPrice, 2, row); // Prix du produit
            gridPaneCart.add(removeButton, 3, row); // Bouton "Supprimer"

            row++; // Passer à la ligne suivante
        }

        // Calculer et afficher le total
        double total = calculateTotal(cartProducts);
        totalPriceLabel.setText("Total: " + String.format("%.2f", total) + " TND");
    }    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleConfirmOrder() {
        try {
            if (currentCommande != null && currentCommande.getId() != -1) {
                // Confirmer la commande
                serviceCommande.confirmerCommande(currentCommande.getId());
                System.out.println("Commande confirmée avec ID : " + currentCommande.getId());

                // Créer une facture après la confirmation de la commande
                ServiceFacture serviceFacture = new ServiceFacture();
                double total = calculateTotal(serviceCommande.getProduitsDansPanier(currentCommande.getId()));
                Facture facture = new Facture(0, currentCommande, java.time.LocalDateTime.now(), total);

                serviceFacture.ajouterFacture(facture);
                System.out.println("Facture créée pour la commande ID : " + currentCommande.getId());

                showAlert("Commande Confirmée", "Votre commande a été confirmée et une facture a été générée.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Aucune commande", "Il n'y a aucune commande à confirmer.", Alert.AlertType.WARNING);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de la confirmation ou de la création de la facture : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancelOrder() {
        try {
            if (currentCommande != null && currentCommande.getId() != -1) {
                serviceCommande.annulerReservation(currentCommande.getId());
                showAlert("Commande Annulée", "Votre commande a été annulée.", Alert.AlertType.INFORMATION);
                System.out.println("Commande annulée.");
            } else {
                showAlert("Aucune commande", "Il n'y a aucune commande à annuler.", Alert.AlertType.WARNING);
            }
            goToProductsPage(null); // Revenir à la page des produits
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de l'annulation.", Alert.AlertType.ERROR);
        }
    }
    private double calculateTotal(List<Produit> cartProducts) {
        double total = 0.0;
        for (Produit produit : cartProducts) {
            total += produit.getPrix();
        }
        return total;
    }


    @FXML
    public void goToProductsPage(MouseEvent event) {
        try {
            // Charger la page des produits
            Parent root = FXMLLoader.load(getClass().getResource("/Product.fxml"));
            Scene scene = new Scene(root);

            // Récupérer la scène actuelle pour accéder à la fenêtre (Stage)
            Stage stage;
            if (event != null) {
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            } else {
                // Si event est null, récupérer la scène actuelle via une autre méthode
                stage = (Stage) gridPaneCart.getScene().getWindow();
            }

            stage.setScene(scene); // Changer la scène vers la page des produits
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showInvoice() {
        if (currentCommande != null) {
            try {
                ServiceFacture serviceFacture = new ServiceFacture();
                Facture facture = serviceFacture.getFactureByCommandeId(currentCommande.getId());

                if (facture != null) {
                    showAlert("Facture", "Date : " + facture.getDateFacture() +
                            "\nTotal : " + facture.getTotal() + " TND", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Aucune facture", "Aucune facture trouvée pour cette commande.", Alert.AlertType.WARNING);
                }
            } catch (SQLException e) {
                showAlert("Erreur", "Erreur lors de la récupération de la facture : " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Aucune commande sélectionnée", "Veuillez sélectionner une commande pour voir la facture.", Alert.AlertType.WARNING);
        }
    }

}
