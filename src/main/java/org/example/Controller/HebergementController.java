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

public class HebergementController {
    private final ServiceHebergement serviceHebergement = new ServiceHebergement();
    @FXML
    private ImageView imageView;
    @FXML
    private GridPane gridPaneHebergements;
    @FXML
    private Button loginButton;
    @FXML
    private TextField nomtf;

    @FXML
    private TextField adressetf;

    @FXML
    private TextField prixtf;

    @FXML
    private TextField dispotf;

    public void initialize() {
        try {
            loadHebergements(); // Charger les données
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void loadHebergements() throws SQLException {
        gridPaneHebergements.getChildren().clear(); // Réinitialiser l'affichage
        List<Hebergement> hebergements = serviceHebergement.afficher();
        int row = 0;
        int col = 0;

        for (Hebergement hebergement : hebergements) {
            VBox hebergementCard = createHebergementCard(hebergement);
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

        // Nom de l'hébergement
        Label hebergementNom = new Label(hebergement.getNom());
        hebergementNom.getStyleClass().add("hebergement-name");

        // Prix de l'hébergement
        Label hebergementPrix = new Label("Prix/Nuit : " + String.format("%.2f", hebergement.getPrixParNuit()) + " TND");
        hebergementPrix.getStyleClass().add("hebergement-price");

        // Message de disponibilité
        Label hebergementDispo = new Label(hebergement.isDisponible() ? "Disponible" : "Indisponible");
        hebergementDispo.setStyle(hebergement.isDisponible() ? "-fx-text-fill: green;" : "-fx-text-fill: red;");

        // Bouton de réservation
        Button reserverButton = new Button("Réserver");
        reserverButton.getStyleClass().addAll("button", "reserve-button");

        // Ajouter les éléments à la carte de l'hébergement
        hebergementCard.getChildren().addAll(hebergementImage, hebergementNom, hebergementPrix, hebergementDispo, reserverButton);

        // Si l'hébergement est disponible, on peut cliquer sur réserver
        if (!hebergement.isDisponible()) {
            reserverButton.setDisable(true); // Désactiver le bouton si non disponible
            hebergementCard.getChildren().add(new Label("Rupture de stock"));
        }

        // Gestion de l'événement du bouton "Réserver"
        reserverButton.setOnAction(event -> {
            Dialog<Pair<LocalDate, Integer>> dialog = new Dialog<>();
            dialog.setTitle("Réserver un hébergement");
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

            // Convertir les résultats
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

                // Validation des entrées
                if (selectedDate == null || selectedDate.isBefore(LocalDate.now())) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur de réservation");
                    alert.setContentText("La date est invalide.");
                    alert.showAndWait();
                    return;
                }

                // Si tout est validé, confirmer la réservation
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Réservation confirmée");
                alert.setContentText("Vous avez réservé " + hebergement.getNom() + " pour " + selectedDate);
                alert.showAndWait();
            });
        });

        return hebergementCard;
    }




    private void modifierHebergement(Hebergement hebergement) {
        // Création de la boîte de dialogue
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modifier Hébergement");
        dialog.setHeaderText("Modifiez les informations de l'hébergement");

        // Création des champs de saisie
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nomField = new TextField(hebergement.getNom());
        TextField prixField = new TextField(String.valueOf(hebergement.getPrixParNuit()));
        ComboBox<String> dispoBox = new ComboBox<>();
        dispoBox.getItems().addAll("true", "false");
        dispoBox.setValue(hebergement.isDisponible() ? "true" : "false");

        grid.add(new Label("Nom :"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Prix par nuit :"), 0, 1);
        grid.add(prixField, 1, 1);
        grid.add(new Label("Disponible :"), 0, 2);
        grid.add(dispoBox, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Ajout des boutons
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Affichage de la boîte de dialogue
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Vérifications de saisie
            String nouveauNom = nomField.getText().trim();
            String prixTexte = prixField.getText().trim();
            String nouvelleDispo = dispoBox.getValue();

            if (nouveauNom.isEmpty()) {
                afficherAlerte("Erreur", "Le nom ne peut pas être vide !");
                return;
            }

            double prix;
            try {
                prix = Double.parseDouble(prixTexte);
                if (prix <= 0) {
                    afficherAlerte("Erreur", "Le prix doit être un nombre positif !");
                    return;
                }
            } catch (NumberFormatException e) {
                afficherAlerte("Erreur", "Veuillez entrer un prix valide !");
                return;
            }

            try {
                // Mise à jour des informations
                hebergement.setNom(nouveauNom);
                hebergement.setPrixParNuit(prix);
                hebergement.setDisponible(Boolean.parseBoolean(nouvelleDispo));

                serviceHebergement.modifier(hebergement);
                loadHebergements();
                afficherAlerte("Succès", "Hébergement modifié avec succès !");
            } catch (SQLException e) {
                e.printStackTrace();
                afficherAlerte("Erreur SQL", "Erreur lors de la modification !");
            }
        }
    }

    // Méthode pour afficher une alerte
    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.show();
    }





    private void supprimerHebergement(Hebergement hebergement) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Suppression d'Hébergement");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cet hébergement ?");
        alert.setContentText("Cette action est irréversible.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                serviceHebergement.supprimer(hebergement.getIdheb());
                loadHebergements();
                System.out.println("🛑 Hébergement supprimé avec succès !");
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("❌ Erreur lors de la suppression !");
            }
        }
    }

    @FXML
    void AjouterHebergement(ActionEvent event) {
        try {
            // 🔹 Vérifier si le nom est vide
            String nom = nomtf.getText().trim();
            if (nom.isEmpty()) {
                throw new IllegalArgumentException("⚠ Le champ 'Nom' ne peut pas être vide !");
            }

            // 🔹 Vérifier si l'adresse est vide
            String adresse = adressetf.getText().trim();
            if (adresse.isEmpty()) {
                throw new IllegalArgumentException("⚠ Le champ 'Adresse' ne peut pas être vide !");
            }

            // 🔹 Vérifier si le prix est valide et positif
            String prixText = prixtf.getText().trim();
            if (prixText.isEmpty()) {
                throw new IllegalArgumentException("⚠ Le champ 'Prix' ne peut pas être vide !");
            }
            float prix = Float.parseFloat(prixText);
            if (prix <= 0) {
                throw new IllegalArgumentException("⚠ Le prix doit être un nombre positif !");
            }

            // 🔹 Vérifier si la disponibilité est bien 0 ou 1
            String dispoText = dispotf.getText().trim();
            if (!dispoText.equals("0") && !dispoText.equals("1")) {
                throw new IllegalArgumentException("⚠ La disponibilité doit être 1 (Oui) ou 0 (Non) !");
            }
            boolean disponible = dispoText.equals("1");
            String imageUrl = "defaultImage.jpg";
            // ✅ Création de l'objet Hebergement

            Hebergement hebergement = new Hebergement(0, nom, adresse, prix, disponible,imageUrl);

            // ✅ Ajout dans la base de données
            serviceHebergement.ajouter(hebergement);

            // ✅ Message de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setContentText("🏨 Hébergement ajouté avec succès !");
            alert.show();

            // Recharge les hébergements après ajout
            loadHebergements();

        } catch (NumberFormatException e) {
            // 🚨 Message d'erreur si le prix n'est pas un nombre valide
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setContentText("❌ Veuillez entrer un prix valide (nombre positif) !");
            alert.show();
        } catch (IllegalArgumentException e) {
            // 🚨 Message d'erreur pour les autres erreurs de saisie
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Erreur de saisie");
            alert.setContentText(e.getMessage());
            alert.show();
        } catch (SQLException e) {
            // 🚨 Message d'erreur si un problème survient lors de l'ajout
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur SQL");
            alert.setContentText("❌ Problème lors de l'ajout de l'hébergement : " + e.getMessage());
            alert.show();
        }
    }

    @FXML
    private void goToAjouterHebergement(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterhebergement.fxml"));
        Parent root = loader.load();

        // Récupérer le contrôleur de la fenêtre AjouterHebergement
        AjouterHebergementController ajouterController = loader.getController();

        // Passer la référence de HebergementController
        ajouterController.setHebergementController(this);

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void goToReservation(ActionEvent event) {
        try {
            // Charger le fichier FXML de la page des produits
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ServiceAdmin.fxml"));
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

    public void goToHotel(ActionEvent event) {
        try {
            // Charger le fichier FXML de la page des produits
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherHebergement.fxml"));
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

    public void goTStore(ActionEvent actionEvent) {
    }

    public void goToProduit(ActionEvent event) {
        try {
            // Charger le fichier FXML de la page des produits
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminDashboardProduit.fxml"));
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
}
