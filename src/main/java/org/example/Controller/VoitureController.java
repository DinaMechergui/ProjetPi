package org.example.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.entities.Voiture;
import org.example.services.ServiceVoiture;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class VoitureController {
    private ServiceVoiture serviceVoiture = new ServiceVoiture();

    // 📌 Champs pour l'affichage des voitures
    @FXML
    private GridPane gridPaneVoitures;

    // 📌 Champs pour l'ajout d'une voiture
    @FXML
    private TextField marquetf;
    @FXML
    private TextField prixtf;
    @FXML
    private TextField dispotf;
    @FXML
    private Button ajouterVoitureBtn;

    @FXML
    private void goToStore() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherVoiture.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ajouterVoitureBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de la page des voitures.");
        }
    }

    // 📌 Initialisation (chargement des voitures)
    public void initialize() {
        try {
            loadVoitures();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 🔹 Charger les voitures et les afficher dans un GridPane
    private void loadVoitures() throws SQLException {
        gridPaneVoitures.getChildren().clear(); // Réinitialiser l'affichage
        List<Voiture> voitures = serviceVoiture.afficher();
        int row = 0, col = 0;

        for (Voiture voiture : voitures) {
            VBox voitureCard = new VBox(10);
            voitureCard.getStyleClass().add("voiture-card");

            Label voitureMarque = new Label("Marque : " + voiture.getMarque());
            Label voiturePrix = new Label("Prix : " + String.format("%.2f", voiture.getPrix()) + " TND / jour");
            Label voitureDispo = new Label(voiture.isDisponible() ? "Disponible" : "Non disponible");
            voitureDispo.setStyle(voiture.isDisponible() ? "-fx-text-fill: green;" : "-fx-text-fill: red;");



            Button modifierButton = new Button("Modifier");
            modifierButton.setOnAction(event -> modifierVoiture(voiture));

            Button supprimerButton = new Button("Supprimer");
            supprimerButton.setOnAction(event -> supprimerVoiture(voiture));

            voitureCard.getChildren().addAll(voitureMarque, voiturePrix, voitureDispo, modifierButton, supprimerButton);
            gridPaneVoitures.add(voitureCard, col, row);

            col++;
            if (col > 2) { col = 0; row++; }
        }
    }

    // ✅ Ajouter une voiture

    @FXML
    private void goToAjouterVoiture() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterVoiture.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter une Voiture");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de la page d'ajout de voiture.");
        }
    }
    @FXML
    void AjouterVoiture(ActionEvent event) {
        try {
            // Vérifications des champs
            String marque = marquetf.getText().trim();
            if (marque.isEmpty()) throw new IllegalArgumentException("⚠ Le champ 'Marque' ne peut pas être vide !");

            String prixText = prixtf.getText().trim();
            if (prixText.isEmpty()) throw new IllegalArgumentException("⚠ Le champ 'Prix' ne peut pas être vide !");
            float prix = Float.parseFloat(prixText);
            if (prix <= 0) throw new IllegalArgumentException("⚠ Le prix doit être un nombre positif !");

            String dispoText = dispotf.getText().trim();
            if (!dispoText.equals("0") && !dispoText.equals("1")) throw new IllegalArgumentException("⚠ La disponibilité doit être 1 (Oui) ou 0 (Non) !");
            boolean disponible = dispoText.equals("1");

            // Ajouter la voiture à la base de données
            Voiture voiture = new Voiture(0, prix, marque, disponible);
            serviceVoiture.ajouter(voiture);

            // Rafraîchir l'affichage après l'ajout
            loadVoitures();

            // Message de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setContentText("🚗 Voiture ajoutée avec succès !");
            alert.show();
        } catch (NumberFormatException e) {
            afficherAlerte("Erreur de saisie", "❌ Veuillez entrer un prix valide (nombre positif) !");
        } catch (IllegalArgumentException e) {
            afficherAlerte("Erreur de saisie", e.getMessage());
        } catch (SQLException e) {
            afficherAlerte("Erreur SQL", "❌ Problème lors de l'ajout de la voiture : " + e.getMessage());
        }
    }

    // ✅ Modifier une voiture
    private void modifierVoiture(Voiture voiture) {
        // Créer une fenêtre modale avec un formulaire pour modifier les informations de la voiture
        Dialog<Voiture> dialog = new Dialog<>();
        dialog.setTitle("Modifier Voiture");
        dialog.setHeaderText("Modifier les informations de la voiture");

        // Définir un bouton OK pour valider la modification
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        // Créer les champs de saisie
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField marqueField = new TextField(voiture.getMarque());
        TextField prixField = new TextField(String.valueOf(voiture.getPrix()));
        TextField dispoField = new TextField(voiture.isDisponible() ? "true" : "false");

        grid.add(new Label("Marque:"), 0, 0);
        grid.add(marqueField, 1, 0);
        grid.add(new Label("Prix:"), 0, 1);
        grid.add(prixField, 1, 1);
        grid.add(new Label("Disponibilité (true/false):"), 0, 2);
        grid.add(dispoField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Lorsque l'utilisateur clique sur OK
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                try {
                    // Valider les champs
                    String marque = marqueField.getText().trim();
                    if (marque.isEmpty()) {
                        afficherAlerte("Erreur de saisie", "⚠ Le champ 'Marque' ne peut pas être vide !");
                        return null;
                    }

                    float prix = Float.parseFloat(prixField.getText().trim());
                    if (prix <= 0) {
                        afficherAlerte("Erreur de saisie", "⚠ Le prix doit être un nombre positif !");
                        return null;
                    }

                    String dispoText = dispoField.getText().trim();
                    if (!dispoText.equals("true") && !dispoText.equals("false")) {
                        afficherAlerte("Erreur de saisie", "⚠ La disponibilité doit être 'true' ou 'false' !");
                        return null;
                    }
                    boolean disponible = Boolean.parseBoolean(dispoText);

                    // Appliquer les modifications sur la voiture
                    voiture.setMarque(marque);
                    voiture.setPrix(prix);
                    voiture.setDisponible(disponible);

                    // Modifier la voiture dans la base de données
                    serviceVoiture.modifier(voiture);

                    // Rafraîchir l'affichage
                    loadVoitures();

                    // Retourner l'objet modifié
                    return voiture;

                } catch (NumberFormatException e) {
                    afficherAlerte("Erreur de saisie", "⚠ Veuillez entrer un prix valide !");
                } catch (SQLException e) {
                    afficherAlerte("Erreur", "❌ Erreur lors de la modification !");
                }
            }
            return null;
        });

        // Afficher la fenêtre modale
        dialog.showAndWait();
    }


    // ❌ Supprimer une voiture
    private void supprimerVoiture(Voiture voiture) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Êtes-vous sûr de vouloir supprimer cette voiture ?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                serviceVoiture.supprimer(voiture.getIdvoiture());
                loadVoitures();
            } catch (SQLException e) {
                afficherAlerte("Erreur", "❌ Erreur lors de la suppression !");
            }
        }
    }

    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.show();
    }
}
