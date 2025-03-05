package org.example.Controller;

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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.example.entities.Hebergement;
import org.example.services.ServiceHebergement;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class HebergementControllerClient {
    private final ServiceHebergement serviceHebergement = new ServiceHebergement();

    @FXML
    private GridPane gridPaneHebergements; // GridPane pour afficher les hébergements
    @FXML
    private Button loginButton; // Bouton de connexion pour navigation

    // Méthode pour naviguer vers la page des produits
    @FXML
    private void goToStore() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Product.fxml"));
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

    // Méthode d'initialisation pour charger les hébergements à l'ouverture
    public void initialize() {
        try {
            loadHebergements(); // Charger les données des hébergements
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour charger les hébergements et afficher des cartes dans le GridPane
    private void loadHebergements() throws SQLException {
        gridPaneHebergements.getChildren().clear(); // Réinitialiser l'affichage
        List<Hebergement> hebergements = serviceHebergement.afficher(); // Récupérer la liste des hébergements
        int row = 0;
        int col = 0;

        for (Hebergement hebergement : hebergements) {
            // Créer la carte de l'hébergement
            VBox hebergementCard = new VBox(10);
            hebergementCard.getStyleClass().add("hebergement-card");

            // Image de l'hébergement
            ImageView hebergementImage = new ImageView();
            String imageUrl = hebergement.getImageUrl();

            if (imageUrl != null && !imageUrl.isEmpty()) {
                try {
                    Image image = new Image(imageUrl); // Si c'est une URL valide
                    hebergementImage.setImage(image);
                } catch (Exception e) {
                    hebergementImage.setImage(new Image("file:defaultImage.jpg")); // Image par défaut en cas d'erreur
                    e.printStackTrace();
                }
            } else {
                hebergementImage.setImage(new Image("file:defaultImage.jpg")); // Image par défaut si l'URL est vide
            }

            // Paramètres pour l'affichage de l'image
            hebergementImage.setFitWidth(150);
            hebergementImage.setFitHeight(150);
            hebergementImage.setPreserveRatio(true);

            // Nom, adresse et prix
            Label hebergementNom = new Label(hebergement.getNom());
            hebergementNom.getStyleClass().add("hebergement-name");

            Label hebergementAdresse = new Label("Adresse : " + hebergement.getAdresse());
            hebergementAdresse.getStyleClass().add("hebergement-address");

            Label hebergementPrix = new Label("Prix/Nuit : " + String.format("%.2f", hebergement.getPrixParNuit()) + " TND");
            hebergementPrix.getStyleClass().add("hebergement-price");

            // Disponibilité
            Label hebergementDispo = new Label(hebergement.isDisponible() ? "Disponible" : "Indisponible");
            hebergementDispo.getStyleClass().add("hebergement-availability");
            hebergementDispo.setStyle(hebergement.isDisponible() ? "-fx-text-fill: green;" : "-fx-text-fill: red;");

            // Bouton de réservation
            Button reserverButton = new Button("Réserver");
            reserverButton.getStyleClass().add("button");
            if (!hebergement.isDisponible()) {
                reserverButton.setDisable(true); // Désactiver le bouton si l'hébergement est indisponible
            }
            reserverButton.setOnAction(event -> ouvrirFormulaireReservation(hebergement));

            // Ajouter les éléments à la carte
            hebergementCard.getChildren().addAll(hebergementImage, hebergementNom, hebergementAdresse, hebergementPrix, hebergementDispo, reserverButton);

            // Ajouter la carte au GridPane
            gridPaneHebergements.add(hebergementCard, col, row);
            col++;
            if (col > 2) {
                col = 0;
                row++;
            }
        }
    }

    // Méthode pour ouvrir un formulaire de réservation avec les détails d'un hébergement
    private void ouvrirFormulaireReservation(Hebergement hebergement) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterReservationHebergement.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur de la nouvelle page et passer les données de l'hébergement
            AjouterResHebergementController controller = loader.getController();
            controller.setHebergementData(hebergement);

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Réservation Hébergement");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour naviguer vers la page des événements
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
            System.out.println("Erreur lors du chargement de la page des événements.");
        }
    }

    // Méthode pour naviguer vers la page des invités
    @FXML
    private void goToInvite() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/menuOrganizer.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de la page des invités.");
        }
    }

    // Méthode pour revenir à la page des hébergements
    @FXML
    private void goToDriveAndStay() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hebergementclient.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de la page des hébergements.");
        }
    }

    // Méthode pour ouvrir la page du panier
    @FXML
    public void goToCart(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Cart.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}