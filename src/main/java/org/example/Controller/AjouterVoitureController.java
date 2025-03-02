package org.example.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.example.entities.Voiture;
import org.example.services.ServiceVoiture;

import java.sql.SQLException;

public class AjouterVoitureController {

    @FXML
    private TextField marqueField;
    @FXML
    private TextField prixField;

    private final ServiceVoiture serviceVoiture = new ServiceVoiture();

    // Ajouter la voiture
    @FXML
    private void ajouterVoiture() {
        try {
            // Valider les champs
            String marque = marqueField.getText().trim();
            if (marque.isEmpty()) {
                throw new IllegalArgumentException("⚠ Le champ 'Marque' ne peut pas être vide !");
            }

            float prix;
            try {
                prix = Float.parseFloat(prixField.getText().trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("⚠ Veuillez entrer un prix valide !");
            }

            if (prix <= 0) {
                throw new IllegalArgumentException("⚠ Le prix doit être un nombre positif !");
            }

            // Créer un nouvel objet Voiture
            Voiture voiture = new Voiture(0,12,"x",true,"cc");
            voiture.setMarque(marque);
            voiture.setPrix(prix);
            voiture.setDisponible(true); // La disponibilité est toujours vraie (true)

            // Ajouter la voiture à la base de données
            serviceVoiture.ajouter(voiture);

            // Afficher un message de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setContentText("🚗 Voiture ajoutée avec succès !");
            alert.show();

            // Fermer la fenêtre d'ajout de voiture après l'ajout
            marqueField.getScene().getWindow().hide();
        } catch (IllegalArgumentException e) {
            afficherAlerte("Erreur de saisie", e.getMessage());
        } catch (SQLException e) {
            afficherAlerte("Erreur SQL", "❌ Problème lors de l'ajout de la voiture : " + e.getMessage());
        }
    }

    // Méthode pour afficher une alerte
    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.show();
    }
}