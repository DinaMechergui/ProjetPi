package Wedding.controllers;

import Wedding.entities.Commande;
import Wedding.entities.Facture;
import Wedding.entities.PdfGenerator;
import Wedding.entities.Produit;
import Wedding.service.ServiceCommande;
import Wedding.service.ServiceFacture;
import Wedding.utils.MyDatabase;
import com.itextpdf.text.DocumentException;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Pair;
import tn.esprit.tacheuser.models.User;
import tn.esprit.tacheuser.utils.SessionManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import entities.reserve;
import entities.ServiceItem;
import services.ServiceReservation;

public class CartController {
    private final ServiceCommande serviceCommande = new ServiceCommande();
    private final ServiceReservation serviceReservation = new ServiceReservation();
    private Commande currentCommande;
    private static User currentUser;

    @FXML
    private GridPane gridPaneCart;
    @FXML
    private Label totalPriceLabel;

    public void initialize() {
        try {
            initializeCurrentCommande();
            loadCart();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initializeCurrentCommande() {
        if (!SessionManager.isUserLoggedIn()) {
            System.err.println("❌ Erreur : Aucun utilisateur connecté.");
            return;
        }

        currentUser = SessionManager.getUser();
        if (currentUser == null) {
            System.err.println("❌ Erreur : L'utilisateur actuel est null.");
            return;
        }

        System.out.println("✅ Utilisateur connecté : " + currentUser.getNom());

        try {
            currentCommande = serviceCommande.getCommandeByUser(currentUser);

            if (currentCommande == null) {
                currentCommande = new Commande();
                currentCommande.setUtilisateur(currentUser.getPrenom());
                currentCommande.setDateCommande(LocalDateTime.now());
                currentCommande.setStatut("RESERVE");
                currentCommande.setTotal(0.0);
                serviceCommande.ajouterCommande(currentCommande);
            }

            System.out.println("🛒 Commande actuelle chargée avec succès.");

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'initialisation de la commande : " + e.getMessage());
        }
    }
    // ✅ Load products and reserved services into the cart
    private void loadCart() throws SQLException {
        if (currentCommande == null || currentCommande.getId() == -1) {
            System.out.println("❌ Aucune commande active trouvée.");
            totalPriceLabel.setText("Total: 0.00 TND");
            return;
        }

        List<reserve> cartServices = serviceReservation.getReservedServicesByUser(currentUser.getPrenom());
        List<Pair<Produit, Integer>> cartProducts = serviceCommande.getProduitsEtQuantitesDansPanier(currentCommande.getId());

        gridPaneCart.getChildren().clear();
        int row = 0;
        double total = 0.0;

        for (reserve reservation : cartServices) {
            if (reservation.getService() != null) {
                total += displayServiceInCart(reservation.getService(), row++);
            }
        }

        for (Pair<Produit, Integer> pair : cartProducts) {
            total += displayProductInCart(pair.getKey(), pair.getValue(), row++);
        }

        totalPriceLabel.setText("Total: " + String.format("%.2f", total) + " TND");

        // ✅ Mettre à jour le total dans la base de données
        serviceCommande.updateTotalPrice(currentCommande.getId());
    }

    private double displayProductInCart(Produit produit, int quantity, int row) {
        return addCartRow(produit.getNom(), produit.getPrix(), quantity, produit.getImageUrl(), () -> {
            try {
                serviceCommande.removeProductFromCart(currentCommande.getId(), produit.getId());
                loadCart();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Une erreur est survenue lors de la suppression du produit.", Alert.AlertType.ERROR);
            }
        }, row);
    }
    // ✅ Display reserved services in the cart
    private double displayServiceInCart(ServiceItem service, int row) {
        return addCartRow(service.getNom(), service.getPrix(), 1, service.getImageUrl(), () -> {
            try {
                serviceReservation.removeServiceFromCart(currentUser.getPrenom(), service.getId());
                loadCart();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Une erreur est survenue lors de la suppression du service.", Alert.AlertType.ERROR);
            }
        }, row);
    }
    private double addCartRow(String name, double price, int quantity, String imageUrl, Runnable onRemove, int row) {
        ImageView imageView = new ImageView();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            imageView.setImage(new Image(imageUrl));
            imageView.setFitWidth(90);
            imageView.setFitHeight(90);
            imageView.setPreserveRatio(true);
        }

        Label nameLabel = new Label(name);
        nameLabel.setFont(Font.font("Georgia", 18));
        nameLabel.setTextFill(Color.web("#6d8c7a"));

        Label priceLabel = new Label("Prix: " + String.format("%.2f", price) + " TND");
        Label quantityLabel = new Label("Quantité: " + quantity);

        Button removeButton = new Button("Supprimer");
        removeButton.setOnAction(event -> onRemove.run());

        gridPaneCart.add(imageView, 0, row);
        gridPaneCart.add(nameLabel, 1, row);
        gridPaneCart.add(priceLabel, 2, row);
        gridPaneCart.add(quantityLabel, 3, row);
        gridPaneCart.add(removeButton, 4, row);

        return price * quantity;
    }
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    @FXML
    private void handlePayment(ActionEvent event) {
        try {
            if (currentCommande != null && currentCommande.getId() != -1) {
                List<Pair<Produit, Integer>> produitsEtQuantites = serviceCommande.getProduitsEtQuantitesDansPanier(currentCommande.getId());
                double total = calculateTotal(produitsEtQuantites , currentCommande.getId());
                openPaymentWindow(event, total);
            } else {
                showAlert("Aucune commande", "Il n'y a aucune commande à payer.", Alert.AlertType.WARNING);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors du calcul du total : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleConfirmOrder(ActionEvent event) {
        try {
            if (currentCommande != null && currentCommande.getId() != -1 && currentUser != null) {
                // ✅ Recalculate total before confirmation
                double total = serviceCommande.calculateTotalForCommande(currentCommande.getId());
                System.out.println("🛒 Total calculé : " + total);

                // ✅ Update total in database
                serviceCommande.updateTotalPrice(currentCommande.getId());

                // ✅ Confirm the order
                serviceCommande.confirmerCommande(currentCommande.getId(), currentUser.getPrenom());

                System.out.println("✅ Commande confirmée avec ID : " + currentCommande.getId());

                // ✅ Save the invoice with the correct total
                ServiceFacture serviceFacture = new ServiceFacture();
                Facture facture = new Facture(0, currentCommande, LocalDateTime.now(), currentUser.getPrenom(), total);
                serviceFacture.ajouterFacture(facture, currentUser.getPrenom());

                System.out.println("🧾 Facture créée pour la commande ID : " + currentCommande.getId());

                showAlert("Commande Confirmée", "Votre commande a été confirmée et une facture a été générée.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Aucune commande", "Il n'y a aucune commande à confirmer.", Alert.AlertType.WARNING);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de la confirmation.", Alert.AlertType.ERROR);
        }
    }



    private double calculateTotal(List<Pair<Produit, Integer>> produitsEtQuantites, int commandeId) throws SQLException {
        double total = 0.0;

        // Calculate total for products
        if (produitsEtQuantites != null) {
            for (Pair<Produit, Integer> pair : produitsEtQuantites) {
                Produit produit = pair.getKey();
                int quantite = pair.getValue();

                if (produit != null) {
                    total += produit.getPrix() * quantite;
                }
            }
        }

        // Calculate total for services
        String query = "SELECT COALESCE(SUM(prix_total), 0) FROM reservation WHERE event_id = ?";

        try (Connection connection = MyDatabase.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, commandeId);  // Assuming event_id corresponds to commande_id
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                double totalServices = rs.getDouble(1);
                total += totalServices;
                // ✅ Add service prices to total
            }
        }

        return total;
    }


    private void openPaymentWindow(ActionEvent event, double total) {}

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

    public void showInvoice(ActionEvent actionEvent) {}

    public void goToProductsPage(MouseEvent mouseEvent) {}
}
