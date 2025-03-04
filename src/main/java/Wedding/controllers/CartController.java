package Wedding.controllers;

import Wedding.entities.*;
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
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CartController {
    private final ServiceCommande serviceCommande = new ServiceCommande();
    private Commande currentCommande;  // La commande actuelle
    private static User currentUser;


    @FXML
    private GridPane gridPaneCart;
    @FXML
    private Label totalPriceLabel;
    public void initialize() {
        try {
            initializeCurrentCommande(); // Assure que la commande est bien initialisée
            loadCart();
            // Charge les produits du panier
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

    public void initializeCurrentCommande() {
        // Vérifier si un utilisateur est connecté
        if (!SessionManager.isUserLoggedIn()) {
            System.err.println("❌ Erreur : Aucun utilisateur connecté.");
            return; // Arrêter l'exécution pour éviter les erreurs
        }

        // Récupérer l'utilisateur connecté
        User currentUser = SessionManager.getUser();
        System.out.println("✅ Utilisateur connecté : " + currentUser.getNom());

        // Créer une instance du service commande
        ServiceCommande serviceCommande = new ServiceCommande();

        try {
            // Récupérer ou créer une commande liée à cet utilisateur
            Commande commande = serviceCommande.getCommandeByUser(currentUser);

            if (commande == null) {
                // Créer une nouvelle commande pour l'utilisateur
                commande = new Commande();
                // Par exemple, on stocke ici le prénom de l'utilisateur
                commande.setUtilisateur(currentUser.getPrenom());
                // Définir d'autres propriétés par défaut
                commande.setDateCommande(LocalDateTime.now());
                commande.setStatut("RESERVE");
                commande.setTotal(0.0);
                serviceCommande.ajouterCommande(commande);
            }

            // Associer la commande au panier
            this.currentCommande = commande;
            System.out.println("🛒 Commande actuelle chargée avec succès.");

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'initialisation de la commande : " + e.getMessage());
        }
    }


    private void loadCart() throws SQLException {
        if (currentCommande == null || currentCommande.getId() == -1) {
            System.out.println("Aucune commande en attente trouvée.");
            totalPriceLabel.setText("Total: 0.00 TND"); // Mettre à jour le total à 0 si le panier est vide
            return;
        }

        // Récupérer les produits et leurs quantités réservées
        List<Pair<Produit, Integer>> cartProductsWithQuantity = serviceCommande.getProduitsEtQuantitesDansPanier(currentCommande.getId());

        if (cartProductsWithQuantity == null || cartProductsWithQuantity.isEmpty()) {
            System.out.println("Le panier est vide.");
            totalPriceLabel.setText("Total: 0.00 TND"); // Mettre à jour le total à 0 si le panier est vide
            return;
        }

        gridPaneCart.getChildren().clear(); // Nettoyer l'affichage
        int row = 0;
        double total = 0.0;

        for (Pair<Produit, Integer> pair : cartProductsWithQuantity) {
            Produit produit = pair.getKey();
            int quantiteReservee = pair.getValue();

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

            // Créer les labels pour le nom, le prix et la quantité du produit
            Label productName = new Label(produit.getNom());
            productName.setFont(Font.font("Georgia", 18));
            productName.setTextFill(Color.web("#6d8c7a"));

            Label productPrice = new Label("Prix unitaire: " + String.format("%.2f", produit.getPrix()) + " TND");
            productPrice.setFont(Font.font("Georgia", 16));
            productPrice.setTextFill(Color.web("#6d8c7a"));

            Label productQuantity = new Label("Quantité: " + quantiteReservee);
            productQuantity.setFont(Font.font("Georgia", 16));
            productQuantity.setTextFill(Color.web("#6d8c7a"));

            // Calculer le prix total pour ce produit (prix unitaire * quantité)
            double prixTotalProduit = produit.getPrix() * quantiteReservee;
            Label productTotalPrice = new Label("Total: " + String.format("%.2f", prixTotalProduit) + " TND");
            productTotalPrice.setFont(Font.font("Georgia", 16));
            productTotalPrice.setTextFill(Color.web("#6d8c7a"));

            // Ajouter le prix total de ce produit au total général
            total += prixTotalProduit;

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
            gridPaneCart.add(productPrice, 2, row); // Prix unitaire du produit
            gridPaneCart.add(productQuantity, 3, row); // Quantité réservée
            gridPaneCart.add(productTotalPrice, 4, row); // Prix total pour ce produit
            gridPaneCart.add(removeButton, 5, row); // Bouton "Supprimer"

            row++; // Passer à la ligne suivante
        }

        // Afficher le total général
        totalPriceLabel.setText("Total: " + String.format("%.2f", total) + " TND");
    }
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public void openPaymentWindow(ActionEvent event, double totalAmount) {
        try {
            // Vérifiez que le fichier FXML existe
            URL fxmlLocation = getClass().getResource("/Payment.fxml");
            if (fxmlLocation == null) {
                System.err.println("Fichier FXML introuvable : /Payment.fxml");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            // Passer le montant total au contrôleur de paiement
            PaymentController paymentController = loader.getController();
            paymentController.initData(totalAmount);

            // Afficher la fenêtre de paiement
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Paiement");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleConfirmOrder(ActionEvent event) {
        try {
            if (currentCommande != null && currentCommande.getId() != -1 && currentUser != null) {
                // Confirmer la commande en passant l'ID de la commande et l'utilisateur
                serviceCommande.confirmerCommande(currentCommande.getId(), currentUser.getPrenom());

                System.out.println("Commande confirmée avec ID : " + currentCommande.getId());

                // Récupérer les produits et leurs quantités réservées
                List<Pair<Produit, Integer>> produitsEtQuantites = serviceCommande.getProduitsEtQuantitesDansPanier(currentCommande.getId());

                // Calculer le total en fonction de la quantité réservée
                double total = calculateTotal(produitsEtQuantites);

                // Créer une facture après la confirmation de la commande
                ServiceFacture serviceFacture = new ServiceFacture();
                Facture facture = new Facture(0, currentCommande, java.time.LocalDateTime.now(), currentUser.getPrenom(), total);                serviceFacture.ajouterFacture(facture, currentUser.getPrenom());
                System.out.println("Facture créée pour la commande ID : " + currentCommande.getId());

                // Générer la facture HTML
                List<Produit> produits = produitsEtQuantites.stream()
                        .map(Pair::getKey)
                        .collect(Collectors.toList());
                String outputPath = "facture_commande_" + currentCommande.getId() + ".html";
                InvoiceGenerator.generateInvoice(currentUser, facture, produits, outputPath);

                // Afficher un message de succès
                showAlert("Commande Confirmée", "Votre commande a été confirmée et une facture a été générée.", Alert.AlertType.INFORMATION);

                // Ouvrir la fenêtre de paiement avec le montant total
                openPaymentWindow(event, total);
            } else {
                showAlert("Aucune commande", "Il n'y a aucune commande à confirmer.", Alert.AlertType.WARNING);
            }
        } catch (SQLException | IOException e) {
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
    private double calculateTotal(List<Pair<Produit, Integer>> cartProductsWithQuantity) {
        double total = 0.0;
        for (Pair<Produit, Integer> pair : cartProductsWithQuantity) {
            Produit produit = pair.getKey();
            int quantite = pair.getValue();
            total += produit.getPrix() * quantite;
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
        if (currentCommande != null && currentUser != null) { // Vérifiez que l'utilisateur est disponible
            try {
                ServiceFacture serviceFacture = new ServiceFacture();

                // Récupérer la facture en passant l'ID de la commande et l'utilisateur
                Facture facture = serviceFacture.getFactureByCommandeId(currentCommande.getId(), currentUser.getPrenom());

                if (facture != null) {
                    // Ouvrir un FileChooser pour choisir l'emplacement du fichier
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Enregistrer la facture");
                    fileChooser.setInitialFileName("Facture_Commande_" + currentCommande.getId() + ".pdf");
                    File file = fileChooser.showSaveDialog(null);

                    if (file != null) {
                        // Générer le PDF à l'emplacement choisi, en passant l'utilisateur (currentUser)
                        PdfGenerator.generateInvoicePdf(facture, currentUser, file.getAbsolutePath());

                        // Afficher un message dans la console (optionnel)
                        System.out.println("Facture générée avec succès : " + file.getAbsolutePath());

                        // Afficher un message à l'utilisateur
                        showAlert("Succès", "La facture a été générée avec succès.", Alert.AlertType.INFORMATION);
                    }
                } else {
                    System.out.println("Aucune facture trouvée pour cette commande.");
                    showAlert("Aucune facture", "Aucune facture trouvée pour cette commande.", Alert.AlertType.WARNING);
                }
            } catch (SQLException | IOException | DocumentException | com.lowagie.text.DocumentException e) {
                System.err.println("Erreur lors de la génération de la facture : " + e.getMessage());
                showAlert("Erreur", "Une erreur est survenue lors de la génération de la facture : " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            System.out.println("Aucune commande sélectionnée ou utilisateur non connecté.");
            showAlert("Erreur", "Aucune commande sélectionnée ou utilisateur non connecté.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handlePayment(ActionEvent event) {
        try {
            if (currentCommande != null && currentCommande.getId() != -1) {
                // Récupérer les produits et leurs quantités réservées
                List<Pair<Produit, Integer>> produitsEtQuantites = serviceCommande.getProduitsEtQuantitesDansPanier(currentCommande.getId());

                // Calculer le total en fonction de la quantité réservée
                double total = calculateTotal(produitsEtQuantites);

                // Ouvrir la fenêtre de paiement avec le montant total
                openPaymentWindow(event, total);
            } else {
                showAlert("Aucune commande", "Il n'y a aucune commande à payer.", Alert.AlertType.WARNING);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors du calcul du total : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
}