package org.example.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

    private void loadHebergements() throws SQLException {
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
        TextInputDialog dialog = new TextInputDialog(hebergement.getNom());
        dialog.setTitle("Modifier Hébergement");
        dialog.setHeaderText("Modifier les informations de l'hébergement");
        dialog.setContentText("Nouveau nom :");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(nouveauNom -> {
            TextInputDialog prixDialog = new TextInputDialog(String.valueOf(hebergement.getPrixParNuit()));
            prixDialog.setTitle("Modifier Prix");
            prixDialog.setContentText("Nouveau prix :");

            Optional<String> prixResult = prixDialog.showAndWait();
            prixResult.ifPresent(nouveauPrix -> {
                TextInputDialog dispoDialog = new TextInputDialog(hebergement.isDisponible() ? "true" : "false");
                dispoDialog.setTitle("Modifier Disponibilité");
                dispoDialog.setContentText("Disponible ? (true/false) :");

                Optional<String> dispoResult = dispoDialog.showAndWait();
                dispoResult.ifPresent(nouvelleDispo -> {
                    try {
                        hebergement.setNom(nouveauNom);
                        hebergement.setPrixParNuit(Double.parseDouble(nouveauPrix));
                        hebergement.setDisponible(Boolean.parseBoolean(nouvelleDispo));

                        serviceHebergement.modifier(hebergement);
                        loadHebergements();
                        System.out.println("✅ Hébergement modifié avec succès !");
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.err.println("❌ Erreur lors de la modification !");
                    }
                });
            });
        });
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
        // Charger le fichier FXML du formulaire d'ajout d'hébergement
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterhebergement.fxml"));
        Parent root = loader.load();

        // Créer une nouvelle scène pour le formulaire d'ajout
        Stage stage = new Stage();
        stage.setScene(new Scene(root));

        // Afficher la scène (la nouvelle fenêtre)
        stage.show();

        // Fermer la fenêtre actuelle (optionnel)
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }
}
