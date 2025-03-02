package Wedding.controllers;

import Wedding.entities.*;
import Wedding.service.*;
import entities.ServiceItem;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.geometry.Insets;
import services.ServiceService;
import tn.esprit.tacheuser.models.User;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommandeController {

    // Services pour interagir avec la base de données
    private ServiceCommande serviceCommande = new ServiceCommande();
    private ServiceProduit serviceProduit = new ServiceProduit();
    private ServiceService serviceService = new ServiceService();
    private ServiceFacture serviceFacture = new ServiceFacture();
    private static User currentUser;
    private double total;

    // Composants de l'interface utilisateur
    @FXML
    private ListView<Produit> productListView;
    @FXML
    private ListView<ServiceItem> serviceListView;
    @FXML
    private Button reserveProductButton, reserveButton1, confirmButton, cancelButton;
    @FXML
    private TextArea reservationSummary;

    // Données
    private ObservableList<Produit> productList;
    private ObservableList<ServiceItem> serviceList;
    private Commande currentCommande;

    public void initialize() {
        try {
            loadProducts();
            loadServices();
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des données : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadProducts() throws SQLException {
        productList = FXCollections.observableArrayList(serviceProduit.afficher());
        productListView.setItems(productList);
    }

    private void loadServices() throws SQLException {
        serviceList = FXCollections.observableArrayList(serviceService.afficher());
        serviceListView.setItems(serviceList);
    }

    @FXML
    private void reserveProduct() {
        Produit selectedProduct = productListView.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            showAlert("Aucun produit sélectionné", "Veuillez sélectionner un produit.", Alert.AlertType.WARNING);
            return;
        }

        Dialog<Pair<LocalDate, Integer>> dialog = new Dialog<>();
        dialog.setTitle("Réserver un produit");
        dialog.setHeaderText("Choisissez la date et la quantité");

        ButtonType reserveButtonType = new ButtonType("Réserver", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(reserveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        DatePicker datePicker = new DatePicker();
        Spinner<Integer> quantitySpinner = new Spinner<>(1, selectedProduct.getStock(), 1);

        grid.add(new Label("Date:"), 0, 0);
        grid.add(datePicker, 1, 0);
        grid.add(new Label("Quantité:"), 0, 1);
        grid.add(quantitySpinner, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton ->
                dialogButton == reserveButtonType ? new Pair<>(datePicker.getValue(), quantitySpinner.getValue()) : null
        );

        Optional<Pair<LocalDate, Integer>> result = dialog.showAndWait();

        result.ifPresent(dateQuantity -> {
            LocalDate selectedDate = dateQuantity.getKey();
            int selectedQuantity = dateQuantity.getValue();

            if (selectedDate == null || selectedDate.isBefore(LocalDate.now())) {
                showAlert("Date invalide", "Veuillez choisir une date valide.", Alert.AlertType.ERROR);
                return;
            }

            try {
                if (serviceCommande.dateDejaReservee(selectedProduct.getId(), selectedDate)) {
                    showAlert("Date non disponible", "Ce produit est déjà réservé à cette date.", Alert.AlertType.WARNING);
                    return;
                }

                if (currentCommande == null) {
                    currentCommande = new Commande(0, currentUser.getNom(), LocalDateTime.now(), "RESERVE", new ArrayList<>(), new ArrayList<>(), 0.0);
                }

                Reservation nouvelleReservation = new Reservation(null, currentCommande, selectedProduct, selectedQuantity);
                currentCommande.ajouterReservation(nouvelleReservation);

                serviceCommande.ajouterOuMettreAJourReservation(currentCommande.getUtilisateur(), selectedProduct, selectedQuantity);
                showAlert("Succès", "Produit réservé avec succès.", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Erreur", "Une erreur s'est produite : " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    @FXML
    private void reserveService() {
        ServiceItem selectedService = serviceListView.getSelectionModel().getSelectedItem();
        if (selectedService == null) {
            showAlert("Aucun service sélectionné", "Veuillez sélectionner un service.", Alert.AlertType.WARNING);
            return;
        }

        Dialog<LocalDate> dialog = new Dialog<>();
        dialog.setTitle("Réserver un service");
        dialog.setHeaderText("Choisissez la date");

        ButtonType reserveButtonType = new ButtonType("Réserver", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(reserveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        DatePicker datePicker = new DatePicker();
        grid.add(new Label("Date:"), 0, 0);
        grid.add(datePicker, 1, 0);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> dialogButton == reserveButtonType ? datePicker.getValue() : null);

        Optional<LocalDate> result = dialog.showAndWait();

        result.ifPresent(selectedDate -> {
            if (selectedDate.isBefore(LocalDate.now())) {
                showAlert("Date invalide", "Veuillez choisir une date valide.", Alert.AlertType.ERROR);
                return;
            }

            if (currentCommande == null) {
                currentCommande = new Commande(0, currentUser.getNom(), LocalDateTime.now(), "RESERVE", new ArrayList<>(), new ArrayList<>(), 0.0);
            }

            try {
                // Enregistrer le service réservé dans la base de données
                serviceCommande.ajouterServiceReserve(currentCommande.getId(), selectedService, selectedDate);
                showAlert("Succès", "Service réservé avec succès.", Alert.AlertType.INFORMATION);

                // Rediriger vers la page du panier
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Cart.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) reserveButton1.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (SQLException | IOException e) {
                showAlert("Erreur", "Une erreur s'est produite : " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    @FXML
    private void confirmOrder() {
        if (currentCommande != null) {
            try {
                serviceCommande.confirmerCommande(currentCommande.getId(), currentUser.getPrenom());
                Facture facture = new Facture(0, currentCommande, java.time.LocalDateTime.now(), currentUser.getPrenom(), total);
                serviceFacture.ajouterFacture(facture, currentUser.getPrenom());
                showAlert("Succès", "Commande confirmée avec services et produits.", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Erreur", "Une erreur s'est produite : " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Erreur", "Aucune commande à confirmer.", Alert.AlertType.WARNING);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}