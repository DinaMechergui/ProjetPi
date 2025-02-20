package org.example.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.entities.Hebergement;
import org.example.services.ServiceHebergement;

import java.sql.SQLException;

public class AjouterHebergementController {

    @FXML
    private TextField nomtf;

    @FXML
    private TextField adressetf;

    @FXML
    private TextField prixtf;

    @FXML
    private TextField dispotf;

    private final ServiceHebergement serviceHebergement = new ServiceHebergement();
    private HebergementController hebergementController; // Référence du contrôleur principal

    // Setter pour passer HebergementController
    public void setHebergementController(HebergementController controller) {
        this.hebergementController = controller;
    }

    @FXML
    void AjouterHebergement(ActionEvent event) {
        try {
            String nom = nomtf.getText().trim();
            if (nom.isEmpty()) {
                throw new IllegalArgumentException("⚠ Le champ 'Nom' ne peut pas être vide !");
            }

            String adresse = adressetf.getText().trim();
            if (adresse.isEmpty()) {
                throw new IllegalArgumentException("⚠ Le champ 'Adresse' ne peut pas être vide !");
            }

            String prixText = prixtf.getText().trim();
            if (prixText.isEmpty()) {
                throw new IllegalArgumentException("⚠ Le champ 'Prix' ne peut pas être vide !");
            }
            float prix = Float.parseFloat(prixText);
            if (prix <= 0) {
                throw new IllegalArgumentException("⚠ Le prix doit être un nombre positif !");
            }

            String dispoText = dispotf.getText().trim();
            if (!dispoText.equals("0") && !dispoText.equals("1")) {
                throw new IllegalArgumentException("⚠ La disponibilité doit être 1 (Oui) ou 0 (Non) !");
            }
            boolean disponible = dispoText.equals("1");

            Hebergement hebergement = new Hebergement(0, nom, adresse, prix, disponible);
            serviceHebergement.ajouter(hebergement);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setContentText("🏨 Hébergement ajouté avec succès !");
            alert.show();

            // Met à jour la liste des hébergements
            if (hebergementController != null) {
                hebergementController.loadHebergements();
            }

            // Ferme la fenêtre d'ajout
            ((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setContentText("❌ Veuillez entrer un prix valide !");
            alert.show();
        } catch (IllegalArgumentException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Erreur de saisie");
            alert.setContentText(e.getMessage());
            alert.show();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur SQL");
            alert.setContentText("❌ Problème lors de l'ajout de l'hébergement : " + e.getMessage());
            alert.show();
        }
    }
}
