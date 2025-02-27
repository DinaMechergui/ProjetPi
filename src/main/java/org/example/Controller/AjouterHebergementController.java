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

            // La disponibilité est toujours vraie (1)
            boolean disponible = true;

            Hebergement hebergement = new Hebergement(0 , nom, adresse, prix, disponible,"https://media.istockphoto.com/id/119926339/photo/resort-swimming-pool.jpg?s=612x612&w=0&k=20&c=9QtwJC2boq3GFHaeDsKytF4-CavYKQuy1jBD2IRfYKc=");
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
