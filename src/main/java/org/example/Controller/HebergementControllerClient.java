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
    private GridPane gridPaneHebergements;
    @FXML
    private Button loginButton;

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

    public void initialize() {
        try {
            loadHebergements(); // Charger les données
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadHebergements() throws SQLException {
        gridPaneHebergements.getChildren().clear(); // Réinitialiser l'affichage
        List<Hebergement> hebergements = serviceHebergement.afficher();
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
            reserverButton.setOnAction(event -> reserverHebergement(hebergement));

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
    private VBox createHebergementCard(Hebergement hebergement) {
        VBox hebergementCard = new VBox(10);
        hebergementCard.getStyleClass().add("hebergement-card");

        // Image de l'hébergement
        ImageView hebergementImage = new ImageView();
        String imageUrl = hebergement.getImageUrl();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                Image image = new Image(imageUrl);
                hebergementImage.setImage(image);
            } catch (Exception e) {
                hebergementImage.setImage(new Image("file:defaultImage.jpg"));
                e.printStackTrace();
            }
        } else {
            hebergementImage.setImage(new Image("file:defaultImage.jpg"));
        }

        hebergementImage.setFitWidth(150);
        hebergementImage.setFitHeight(150);
        hebergementImage.setPreserveRatio(true);

        // Nom de l'hébergement
        Label hebergementNom = new Label(hebergement.getNom());
        hebergementNom.getStyleClass().add("hebergement-name");

        // Prix de l'hébergement
        Label hebergementPrix = new Label("Prix/Nuit: " + String.format("%.2f", hebergement.getPrixParNuit()) + " TND");
        hebergementPrix.getStyleClass().add("hebergement-price");

        // Disponibilité de l'hébergement
        Label hebergementDispo = new Label(hebergement.isDisponible() ? "Disponible" : "Indisponible");
        hebergementDispo.setStyle(hebergement.isDisponible() ? "-fx-text-fill: green;" : "-fx-text-fill: red;");

        // Bouton "Réserver"
        Button reserverButton = new Button("Réserver");
        reserverButton.getStyleClass().add("reserve-button");

        // Ajouter les éléments à la carte de l'hébergement
        hebergementCard.getChildren().addAll(hebergementImage, hebergementNom, hebergementPrix, hebergementDispo, reserverButton);

        // Désactiver le bouton "Réserver" si l'hébergement n'est pas disponible
        if (!hebergement.isDisponible()) {
            reserverButton.setDisable(true);
            hebergementCard.getChildren().add(new Label("Rupture de stock"));
        }

        // Action lors du clic sur "Réserver"
        reserverButton.setOnAction(event -> {
            // Logique pour gérer la réservation
            Dialog<Pair<LocalDate, Integer>> dialog = new Dialog<>();
            dialog.setTitle("Réservation d'Hébergement");
            dialog.setHeaderText("Veuillez entrer la date et la quantité pour la réservation");

            ButtonType reserveButtonType = new ButtonType("Réserver", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(reserveButtonType, ButtonType.CANCEL);

            // Formulaire de réservation
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            DatePicker datePicker = new DatePicker();
            Spinner<Integer> quantitySpinner = new Spinner<>(1, 5, 1);

            grid.add(new Label("Date:"), 0, 0);
            grid.add(datePicker, 1, 0);
            grid.add(new Label("Quantité:"), 0, 1);
            grid.add(quantitySpinner, 1, 1);

            dialog.getDialogPane().setContent(grid);

            // Convertir le résultat
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == reserveButtonType) {
                    return new Pair<>(datePicker.getValue(), quantitySpinner.getValue());
                }
                return null;
            });

            Optional<Pair<LocalDate, Integer>> result = dialog.showAndWait();

            result.ifPresent(dateQuantity -> {
                LocalDate selectedDate = dateQuantity.getKey();
                int selectedQuantity = dateQuantity.getValue();

                // Validation de la date
                if (selectedDate == null || selectedDate.isBefore(LocalDate.now())) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur de réservation");
                    alert.setContentText("La date est invalide.");
                    alert.showAndWait();
                    return;
                }

                // Confirmation de la réservation
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Réservation confirmée");
                alert.setContentText("Réservation de " + hebergement.getNom() + " pour " + selectedDate);
                alert.showAndWait();
            });
        });

        return hebergementCard;
    }

    private void reserverHebergement(Hebergement hebergement) {
        System.out.println("Hébergement réservé : " + hebergement.getNom());
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Réservation");
        alert.setContentText("Vous avez réservé : " + hebergement.getNom());
        alert.show();
    }

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
    @FXML
    private void goToInvite() {
        try {
            // Charger le fichier FXML de la page des produits
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/menuOrganizer.fxml"));
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
    private void goToDriveAndStay() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherHebergement.fxml"));
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

    @FXML
    public void goToCart(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Cart.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root)); // Changer la scène vers la page du panier
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
