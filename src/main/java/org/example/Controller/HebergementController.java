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
import org.example.entities.Hebergement;
import org.example.services.ServiceHebergement;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class HebergementController {
    private final ServiceHebergement serviceHebergement = new ServiceHebergement();

    @FXML
    private ImageView imageView;

    @FXML
    private GridPane gridPaneHebergements;

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

        // Bouton de modification
        Button modifierButton = new Button("Modifier");
        modifierButton.getStyleClass().addAll("button", "modify-button");
        modifierButton.setOnAction(event -> modifierHebergement(hebergement));

        // Bouton de suppression
        Button supprimerButton = new Button("Supprimer");
        supprimerButton.getStyleClass().addAll("button", "delete-button");
        supprimerButton.setOnAction(event -> supprimerHebergement(hebergement));

        // Ajouter les éléments à la carte de l'hébergement
        hebergementCard.getChildren().addAll(hebergementImage, hebergementNom, hebergementPrix, hebergementDispo, modifierButton, supprimerButton);

        // Si l'hébergement est indisponible, on peut désactiver les boutons
        if (!hebergement.isDisponible()) {
            modifierButton.setDisable(true);
            supprimerButton.setDisable(true);
            hebergementCard.getChildren().add(new Label("Rupture de stock"));
        }

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
            // Vérifier si le nom est vide
            String nom = nomtf.getText().trim();
            if (nom.isEmpty()) {
                throw new IllegalArgumentException("⚠ Le champ 'Nom' ne peut pas être vide !");
            }

            // Vérifier si l'adresse est vide
            String adresse = adressetf.getText().trim();
            if (adresse.isEmpty()) {
                throw new IllegalArgumentException("⚠ Le champ 'Adresse' ne peut pas être vide !");
            }

            // Vérifier si le prix est valide et positif
            String prixText = prixtf.getText().trim();
            if (prixText.isEmpty()) {
                throw new IllegalArgumentException("⚠ Le champ 'Prix' ne peut pas être vide !");
            }
            float prix = Float.parseFloat(prixText);
            if (prix <= 0) {
                throw new IllegalArgumentException("⚠ Le prix doit être un nombre positif !");
            }

            // Vérifier si la disponibilité est bien 0 ou 1
            String dispoText = dispotf.getText().trim();
            if (!dispoText.equals("0") && !dispoText.equals("1")) {
                throw new IllegalArgumentException("⚠ La disponibilité doit être 1 (Oui) ou 0 (Non) !");
            }
            boolean disponible = dispoText.equals("1");

            // Création de l'objet Hebergement
            Hebergement hebergement = new Hebergement(0, nom, adresse, prix, disponible,"data:image/jpeg;base64,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");

            // Ajout dans la base de données
            serviceHebergement.ajouter(hebergement);

            // Message de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setContentText("🏨 Hébergement ajouté avec succès !");
            alert.show();

            // Recharge les hébergements après ajout
            loadHebergements();

        } catch (NumberFormatException e) {
            // Message d'erreur si le prix n'est pas un nombre valide
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setContentText("❌ Veuillez entrer un prix valide (nombre positif) !");
            alert.show();
        } catch (IllegalArgumentException e) {
            // Message d'erreur pour les autres erreurs de saisie
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Erreur de saisie");
            alert.setContentText(e.getMessage());
            alert.show();
        } catch (SQLException e) {
            // Message d'erreur si un problème survient lors de l'ajout
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