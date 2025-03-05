package Wedding.controllers;

import Wedding.entities.*;
import Wedding.service.ServiceCommande;
import Wedding.service.ServiceFacture;
import Wedding.utils.MyDatabase;
import com.itextpdf.text.DocumentException;
import entities.ServiceItem;
import entities.reserve;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
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
import services.ServiceReservation;
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
import java.util.Random;
import java.util.stream.Collectors;

public class CartController {
    private final ServiceCommande serviceCommande = new ServiceCommande();
    private Commande currentCommande;  // La commande actuelle
    private static User currentUser;
    private double total; // Variable pour stocker le total du panier

    @FXML
    private TextField promoCodeField;

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
            return;
        }

        currentUser = SessionManager.getUser();
        if (currentUser == null) {
            System.err.println("❌ Erreur : L'utilisateur actuel est null.");
            return;
        }

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
            System.out.println("❌ Aucune commande en attente trouvée.");
            totalPriceLabel.setText("Total: 0.00 TND");
            return;
        }

        int commandeId = currentCommande.getId();
        gridPaneCart.getChildren().clear(); // Nettoyer l'affichage avant de charger les données
        total = 0.0; // Réinitialiser le total

        // 🔹 Récupération des produits dans le panier
        List<Pair<Produit, Integer>> cartProductsWithQuantity = serviceCommande.getProduitsEtQuantitesDansPanier(commandeId);

        // 🔹 Récupération des services réservés
        ServiceReservation serviceReservation = new ServiceReservation();
        List<reserve> cartServices = serviceReservation.getReservedServicesByUser(currentUser.getPrenom());

        System.out.println("🔍 Produits récupérés : " + cartProductsWithQuantity.size());
        System.out.println("🔍 Services récupérés : " + cartServices.size());

        int row = 0;

        // 🔹 Affichage des services dans le panier
        for (reserve reservation : cartServices) {
            if (reservation.getService() != null) {
                total += displayServiceInCart(reservation.getService(), reservation.getPrixTotal(), row++);
            }
        }

        // 🔹 Affichage des produits dans le panier
        for (Pair<Produit, Integer> pair : cartProductsWithQuantity) {
            total += displayProductInCart(pair.getKey(), pair.getValue(), row++);
        }

        // 🔹 Mettre à jour l'affichage du total
        totalPriceLabel.setText("Total: " + String.format("%.2f", total) + " TND");
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


    private double displayServiceInCart(ServiceItem service, double prixTotal, int row) {
        return addCartRow(service.getNom(), service.getPrix(), 1, service.getImageUrl(), () -> {
            try {
                System.out.println("🗑️ Suppression du service ID " + service.getId() + " pour l'utilisateur " + currentUser.getPrenom());
                ServiceReservation.removeServiceFromCart(currentUser.getPrenom(), service.getId());
                loadCart();  // Recharger le panier après suppression
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Une erreur est survenue lors de la suppression du service.", Alert.AlertType.ERROR);
            }
        }, row);
    }
    @FXML

    private void handleConfirmOrder(ActionEvent event) {
        try {
            if (currentCommande != null && currentCommande.getId() != -1 && currentUser != null) {
                // Confirmer la commande
                serviceCommande.confirmerCommande(currentCommande.getId(), currentUser.getPrenom());

                // Utiliser le total de la commande
                double total = currentCommande.getTotal();

                // Générer un code promo si le total dépasse 10 000 TND
                String codePromo = null;
                if (total > 10000) {
                    codePromo = generatePromoCode();
                    showAlert("Félicitations !", "Vous avez dépensé plus de 10 000 TND. Voici un code promo : " + codePromo, Alert.AlertType.INFORMATION);
                }

                // ✅ Vérifier la connexion avant de générer la facture
                MyDatabase.getInstance().getConnection();

                // Créer la facture avec le total de la commande et le code promo
                ServiceFacture serviceFacture = new ServiceFacture();
                Facture facture = new Facture(0, currentCommande, LocalDateTime.now(), currentUser.getPrenom(), total, codePromo);
                serviceFacture.ajouterFacture(facture, currentUser.getPrenom());

                System.out.println("✅ Facture créée pour la commande ID : " + currentCommande.getId());

                // Générer la facture HTML
                List<Pair<Produit, Integer>> produitsEtQuantites = serviceCommande.getProduitsEtQuantitesDansPanier(currentCommande.getId());
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
            showAlert("Erreur", "Une erreur est survenue : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private String generatePromoCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }
        return code.toString();
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
        if (currentCommande == null || currentUser == null) {
            System.out.println("❌ Aucune commande sélectionnée ou utilisateur non connecté.");
            showAlert("Erreur", "Aucune commande sélectionnée ou utilisateur non connecté.", Alert.AlertType.WARNING);
            return;
        }

        try {
            System.out.println("🔄 Vérification de la connexion à la base de données...");
            Connection connection = MyDatabase.getInstance().getConnection();
            if (connection == null || connection.isClosed()) {
                showAlert("Erreur de connexion", "Impossible de se connecter à la base de données.", Alert.AlertType.ERROR);
                return;
            }

            ServiceFacture serviceFacture = new ServiceFacture();
            Facture facture = serviceFacture.getFactureByCommandeId(currentCommande.getId(), currentUser.getPrenom());

            if (facture == null) {
                System.out.println("❌ Aucune facture trouvée pour cette commande.");
                showAlert("Aucune facture", "Aucune facture trouvée pour cette commande.", Alert.AlertType.WARNING);
                return;
            }

            // 📂 Ouvrir un FileChooser pour choisir l'emplacement du fichier PDF
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer la facture");
            fileChooser.setInitialFileName("Facture_Commande_" + currentCommande.getId() + ".pdf");
            File file = fileChooser.showSaveDialog(null);

            if (file != null) {
                // ✅ Générer le PDF à l'emplacement choisi
                PdfGenerator.generateInvoicePdf(facture, currentUser, file.getAbsolutePath());

                // ✅ Afficher un message de succès
                System.out.println("✅ Facture générée avec succès : " + file.getAbsolutePath());
                showAlert("Succès", "La facture a été générée avec succès.", Alert.AlertType.INFORMATION);
            }

        } catch (SQLException ex) {
            System.err.println("❌ Erreur SQL lors de la récupération de la facture : " + ex.getMessage());
            showAlert("Erreur SQL", "Une erreur est survenue lors de la récupération de la facture : " + ex.getMessage(), Alert.AlertType.ERROR);
        } catch (IOException | DocumentException | com.lowagie.text.DocumentException ex) {
            System.err.println("❌ Erreur lors de la génération du PDF : " + ex.getMessage());
            showAlert("Erreur PDF", "Une erreur est survenue lors de la génération de la facture : " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }


    @FXML
    private void handlePayment(ActionEvent event) {
        try {
            if (currentCommande != null && currentCommande.getId() != -1) {
                // Récupérer les produits et leurs quantités réservées
                List<Pair<Produit, Integer>> produitsEtQuantites = serviceCommande.getProduitsEtQuantitesDansPanier(currentCommande.getId());

                // Calculer le total en fonction de la quantité réservée
                double total = calculateTotal(produitsEtQuantites,currentCommande.getId());

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

    @FXML
    private void applyPromoCode(ActionEvent event) throws SQLException {
        String promoCode = promoCodeField.getText();
        if (isValidPromoCode(promoCode)) {
            // Récupérer le total actuel de la commande
            double total = currentCommande.getTotal();

            // Appliquer la réduction de 10%
            double reduction = 0.1; // 10% de réduction
            double totalApresReduction = total * (1 - reduction);

            // Mettre à jour le total dans la commande
            currentCommande.setTotal(totalApresReduction);

            // Mettre à jour le total dans la base de données
            serviceCommande.updateCommande(currentCommande);

            // Mettre à jour l'affichage
            totalPriceLabel.setText("Total après réduction: " + String.format("%.2f", totalApresReduction) + " TND");

            showAlert("Code promo appliqué", "Une réduction de 10% a été appliquée.", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Code promo invalide", "Le code promo entré n'est pas valide.", Alert.AlertType.ERROR);
        }
    }


    private boolean isValidPromoCode(String promoCode) {
        try {
            String query = "SELECT COUNT(*) FROM facture WHERE code_promo = ?";

            // ✅ Obtenir une connexion active depuis l'instance Singleton de MyDatabase
            Connection connection = MyDatabase.getInstance().getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, promoCode);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int count = rs.getInt(1);
                        return count > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @FXML
    private void handleLogoClick(MouseEvent event) {
        try {
            // Charger le fichier FXML de la page d'accueil
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur de la page d'accueil
            HomeController homeController = loader.getController();

            // Passer l'utilisateur connecté au contrôleur de la page d'accueil
            homeController.setCurrentUser(SessionManager.getUser()); // Assurez-vous que SessionManager.getUser() retourne l'utilisateur connecté

            // Récupérer la scène actuelle
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Changer la scène pour afficher la page d'accueil
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors du chargement de la page d'accueil.", Alert.AlertType.ERROR);
        }
    }



    // Méthode pour recevoir l'utilisateur actuel



}