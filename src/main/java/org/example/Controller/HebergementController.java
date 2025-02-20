package org.example.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

        Label hebergementNom = new Label("Nom : " + hebergement.getNom());
        Label hebergementAdresse = new Label("Adresse : " + hebergement.getAdresse());
        Label hebergementPrix = new Label("Prix/Nuit : " + String.format("%.2f", hebergement.getPrixParNuit()) + " TND");
        Label hebergementDispo = new Label(hebergement.isDisponible() ? "Disponible" : "Indisponible");
        hebergementDispo.setStyle(hebergement.isDisponible() ? "-fx-text-fill: green;" : "-fx-text-fill: red;");


        Button modifierButton = new Button("Modifier");
        modifierButton.setOnAction(event -> modifierHebergement(hebergement));

        Button supprimerButton = new Button("Supprimer");
        supprimerButton.setOnAction(event -> supprimerHebergement(hebergement));

        hebergementCard.getChildren().addAll(hebergementNom, hebergementAdresse, hebergementPrix, hebergementDispo, modifierButton, supprimerButton);
        return hebergementCard;
    }

    private void reserverHebergement(Hebergement hebergement) {
        System.out.println("Hébergement réservé : " + hebergement.getNom());
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

            // ✅ Création de l'objet Hebergement
            Hebergement hebergement = new Hebergement(0, nom, adresse, prix, disponible);

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
}
