package Wedding.controllers;

import Wedding.entities.Commande;
import Wedding.entities.Facture;
import Wedding.entities.Produit;
import Wedding.entities.Reservation;
import Wedding.service.ServiceCommande;
import Wedding.service.ServiceFacture;
import Wedding.service.ServiceProduit;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommandeController {

    // Services pour interagir avec la base de données
    private ServiceCommande serviceCommande = new ServiceCommande();
    private ServiceProduit serviceProduit = new ServiceProduit();

    // Composants de l'interface utilisateur
    @FXML
    private ListView<Produit> productListView; // Liste des produits disponibles
    @FXML
    private Button reserveButton; // Bouton pour réserver un produit
    @FXML
    private Button confirmButton; // Bouton pour confirmer la commande
    @FXML
    private Button cancelButton; // Bouton pour annuler la réservation
    @FXML
    private TextArea reservationSummary; // Zone de texte pour afficher le résumé des réservations

    // Données
    private ObservableList<Produit> productList; // Liste observable des produits
    private Commande currentCommande; // Commande en cours

    // Méthode d'initialisation
    public void initialize() {
        try {
            loadProducts();
            showAlert("Test", "Ceci est un test d'affichage", Alert.AlertType.INFORMATION);
// Charger les produits au démarrage
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement des produits : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // Charger les produits depuis la base de données
    private void loadProducts() throws SQLException {
        productList = FXCollections.observableArrayList(serviceProduit.afficher());
        productListView.setItems(productList);
    }

    // Trouver une réservation existante pour un produit donné
    public Reservation trouverReservationExistante(Produit produit) {
        if (currentCommande != null) {
            for (Reservation reservation : currentCommande.getReservations()) {
                if (reservation.getProduit().equals(produit)) {
                    return reservation;
                }
            }
        }
        return null; // Aucune réservation trouvée
    }

    // Réserver un produit
    @FXML
    private void reserveProduct() {
        Produit selectedProduct = productListView.getSelectionModel().getSelectedItem(); // Produit sélectionné
        if (selectedProduct != null) {
            // Vérification du stock
            if (selectedProduct.getStock() <= 0) {
                showAlert("Stock épuisé", "Ce produit n'est plus en stock et ne peut pas être réservé.", Alert.AlertType.WARNING);
                return; // Stoppe l'exécution si le stock est épuisé
            }

            try {
                if (currentCommande == null) {
                    currentCommande = new Commande(0, "User1", LocalDateTime.now(), "RESERVE", new ArrayList<>());
                }

                Reservation reservationExistante = trouverReservationExistante(selectedProduct);

                if (reservationExistante != null) {
                    reservationExistante.setQuantite(reservationExistante.getQuantite() + 1);
                } else {
                    Reservation nouvelleReservation = new Reservation(null, currentCommande, selectedProduct, 1);
                    currentCommande.ajouterReservation(nouvelleReservation);
                }

                serviceCommande.ajouterOuMettreAJourReservation(currentCommande.getUtilisateur(), selectedProduct);
                showAlert("Succès", "Votre réservation a été enregistrée avec succès.", Alert.AlertType.INFORMATION);

            } catch (SQLException e) {
                showAlert("Erreur de réservation", "Une erreur s'est produite lors de la réservation : " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Aucun produit sélectionné", "Veuillez sélectionner un produit pour réserver.", Alert.AlertType.WARNING);
        }
    }

    // Confirmer la commande
    @FXML
    private void confirmOrder() {
        if (currentCommande != null) {
            try {
                // Confirmer la commande
                serviceCommande.confirmerCommande(currentCommande.getId());

                // Créer une facture
                ServiceFacture serviceFacture = new ServiceFacture();
                Facture facture = new Facture(0, currentCommande, LocalDateTime.now(), currentCommande.calculerTotal());
                serviceFacture.ajouterFacture(facture);

                // Afficher une alerte de succès
                showAlert("Commande confirmée", "Votre commande a été confirmée avec succès ! Une facture a été générée.", Alert.AlertType.INFORMATION);

                // Mettre à jour le résumé de la réservation
                reservationSummary.appendText("Commande confirmée ! Facture générée.\n");

            } catch (SQLException e) {
                showAlert("Erreur de confirmation", "Une erreur s'est produite lors de la confirmation de la commande : " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Aucune commande à confirmer", "Aucune commande en cours pour être confirmée.", Alert.AlertType.WARNING);
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


    // Annuler la réservation
    @FXML
    private void cancelReservation() {
        if (currentCommande != null) {
            try {
                // Annuler la réservation dans la base de données
                serviceCommande.annulerReservation(currentCommande.getId());

                // Afficher une alerte de succès
                showAlert("Réservation annulée", "Votre réservation a été annulée avec succès.", Alert.AlertType.INFORMATION);

                // Mettre à jour le résumé de la réservation
                reservationSummary.appendText("Réservation annulée.\n");

                // Réinitialiser la commande en cours
                currentCommande = null;

            } catch (SQLException e) {
                // Afficher une alerte d'erreur en cas d'échec
                showAlert("Erreur d'annulation", "Une erreur s'est produite lors de l'annulation de la réservation : " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            // Afficher une alerte si aucune réservation n'est à annuler
            showAlert("Aucune réservation à annuler", "Aucune réservation n'est en cours pour être annulée.", Alert.AlertType.WARNING);
        }
    }

    // Méthode utilitaire pour afficher des alertes
    private void showAlert(String title, String message, Alert.AlertType type) {
        Platform.runLater(() -> { // Assure que l'alerte s'affiche sur le thread JavaFX
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}