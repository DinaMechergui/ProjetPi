package Wedding.controllers;

import Wedding.entities.Commande;
import Wedding.entities.Produit;
import Wedding.entities.Reservation;
import Wedding.service.ServiceCommande;
import Wedding.service.ServiceProduit;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
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
import javafx.util.Pair;
import tn.esprit.tacheuser.models.User;
import tn.esprit.tacheuser.utils.SessionManager;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductController {
    private ServiceProduit serviceProduit = new ServiceProduit();
    private ServiceCommande serviceCommande = new ServiceCommande();
    private Commande currentCommande;
    private User currentUser; // Ajoutez ce champ pour stocker l'utilisateur connecté

    @FXML
    private GridPane gridPaneProduits;
    @FXML
    private Button loginButton;

    @FXML
    private Button logoutButton;

    @FXML
    private ImageView cartIcon;

    @FXML
    private void goToStore(ActionEvent event) {
        try {
            // Charger le fichier FXML de la page des produits
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Product.fxml"));
            Parent root = loader.load();

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

    @FXML
    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText("Ceci est une alerte !");
        alert.showAndWait(); // Utilisez showAndWait() pour bloquer jusqu'à ce que l'utilisateur ferme l'alerte
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
        // 🔥 Ensure currentUser is set from the session
        this.currentUser = SessionManager.getUser();

        if (this.currentUser == null) {
            System.err.println("❌ Erreur : Aucun utilisateur connecté.");
            return;
        }

        System.out.println("✅ Utilisateur connecté : " + currentUser.getNom());

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
            reserveButton.getStyleClass().addAll("button", "reserve-button");

            // Message de rupture de stock
            Label outOfStockLabel = new Label("Rupture de stock");
            outOfStockLabel.getStyleClass().add("out-of-stock-label");
            outOfStockLabel.setTextFill(Color.RED); // Optionnel : changer la couleur du texte

            // Vérifier le stock
            if (product.getStock() == 0) {
                // Désactiver le bouton "Reserve" et afficher le message de rupture de stock
                reserveButton.setDisable(true);
                productCard.getChildren().addAll(productImage, productName, productPrice, outOfStockLabel);
            } else {
                // Activer le bouton "Reserve" et permettre la réservation
                reserveButton.setDisable(false);
                productCard.getChildren().addAll(productImage, productName, productPrice, reserveButton);

                // Gestion de l'événement du bouton "Reserve"
                reserveButton.setOnAction(event -> {
                    // Créer une boîte de dialogue personnalisée
                    Dialog<Pair<LocalDate, Integer>> dialog = new Dialog<>();
                    dialog.setTitle("Réserver un produit");
                    dialog.setHeaderText("Veuillez entrer la date et la quantité pour la réservation");

                    // Définir les boutons de la boîte de dialogue
                    ButtonType reserveButtonType = new ButtonType("Réserver", ButtonBar.ButtonData.OK_DONE);
                    dialog.getDialogPane().getButtonTypes().addAll(reserveButtonType, ButtonType.CANCEL);

                    // Créer les champs de formulaire
                    GridPane grid = new GridPane();
                    grid.setHgap(10);
                    grid.setVgap(10);
                    grid.setPadding(new Insets(20, 150, 10, 10));

                    DatePicker datePicker = new DatePicker();
                    Spinner<Integer> quantitySpinner = new Spinner<>(1, product.getStock(), 1);

                    // Ajouter un ChangeListener pour gérer les changements de valeur
                    quantitySpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
                        if (newValue != null && newValue > product.getStock()) {
                            // Si la nouvelle valeur dépasse le stock, réinitialiser à l'ancienne valeur
                            quantitySpinner.getValueFactory().setValue(oldValue);
                        }
                    });

                    grid.add(new Label("Date:"), 0, 0);
                    grid.add(datePicker, 1, 0);
                    grid.add(new Label("Quantité:"), 0, 1);
                    grid.add(quantitySpinner, 1, 1);

                    dialog.getDialogPane().setContent(grid);

                    // Convertir le résultat en Pair<LocalDate, Integer>
                    dialog.setResultConverter(dialogButton -> {
                        if (dialogButton == reserveButtonType) {
                            return new Pair<>(datePicker.getValue(), quantitySpinner.getValue());
                        }
                        return null;
                    });

                    // Afficher la boîte de dialogue et attendre la réponse
                    Optional<Pair<LocalDate, Integer>> result = dialog.showAndWait();

                    result.ifPresent(dateQuantity -> {
                        LocalDate selectedDate = dateQuantity.getKey();
                        int selectedQuantity = dateQuantity.getValue();

                        // Vérifier si la date est valide
                        if (selectedDate == null) {
                            showAlert("Aucune date sélectionnée", "Veuillez choisir une date pour la réservation.", Alert.AlertType.WARNING);
                            return;
                        }

                        if (selectedDate.isBefore(LocalDate.now())) {
                            showAlert("Date invalide", "Vous ne pouvez pas réserver pour une date passée.", Alert.AlertType.ERROR);
                            return;
                        }

                        try {
                            if (serviceCommande.dateDejaReservee(product.getId(), selectedDate)) {
                                showAlert("Date non disponible", "Ce produit est déjà réservé à cette date.", Alert.AlertType.WARNING);
                                return;
                            }

                            if (product.getStock() < selectedQuantity) {
                                showAlert("Stock insuffisant", "Il n'y a pas assez de stock pour ce produit.", Alert.AlertType.WARNING);
                                return;
                            }

                            if (currentCommande == null) {
                                currentCommande = new Commande(0, currentUser.getNom(), LocalDateTime.now(), "RESERVE", new ArrayList<>(), new ArrayList<>(), 0.0);
                            }

                            Reservation reservationExistante = trouverReservationExistante(product);
                            if (reservationExistante != null) {
                                reservationExistante.setQuantite(reservationExistante.getQuantite() + selectedQuantity);
                            } else {
                                Reservation nouvelleReservation = new Reservation(null, currentCommande, product, selectedQuantity);
                                currentCommande.ajouterReservation(nouvelleReservation);
                            }

                            int commandeId = serviceCommande.ajouterOuMettreAJourReservation(currentUser.getPrenom(), product, selectedQuantity);

                            if (currentCommande.getId() == 0) {
                                currentCommande.setId(commandeId);
                            }

                            System.out.println("Produit ajouté au panier : " + product.getNom());
                            showAlert("Succès", "Votre réservation a été enregistrée avec succès.", Alert.AlertType.INFORMATION);

                        } catch (SQLException e) {
                            showAlert("Erreur de réservation", "Une erreur s'est produite : " + e.getMessage(), Alert.AlertType.ERROR);
                        }
                    });
                });
            }

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

            // Récupérer le contrôleur de la page du panier
            CartController cartController = loader.getController();

            // Passer l'utilisateur connecté au contrôleur
            cartController.setCurrentUser(SessionManager.getUser()); // Assurez-vous que SessionManager.getUser() retourne l'utilisateur connecté

            // Récupérer la scène actuelle pour accéder à la fenêtre (Stage)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root)); // Changer la scène vers la page du panier
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void goToEvent(ActionEvent event) {
        try {
            // Charger le fichier FXML de la page des produits
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/reservation.fxml"));
            Parent root = loader.load();

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


    @FXML
    private void goToInvite(ActionEvent event) {
        try {
            // Charger le fichier FXML de la page des produits
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/menuOrganizer.fxml"));
            Parent root = loader.load();

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

    @FXML
    private void goToDriveAndStay(ActionEvent event) {
        try {
            // Charger le fichier FXML de la page des produits
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hebergementclient.fxml"));
            Parent root = loader.load();

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