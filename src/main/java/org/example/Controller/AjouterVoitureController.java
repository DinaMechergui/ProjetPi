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

    private ServiceVoiture serviceVoiture = new ServiceVoiture();

    // Ajouter la voiture
    @FXML
    private void ajouterVoiture() {
        try {
            String marque = marqueField.getText().trim();
            if (marque.isEmpty()) throw new IllegalArgumentException("⚠ Le champ 'Marque' ne peut pas être vide !");

            float prix = Float.parseFloat(prixField.getText().trim());
            if (prix <= 0) throw new IllegalArgumentException("⚠ Le prix doit être un nombre positif !");

            // La disponibilité est toujours vraie (true)
            boolean disponible = true;

            // Ajouter la voiture à la base de données
            Voiture voiture = new Voiture(0, prix, marque, disponible);
            serviceVoiture.ajouter(voiture);

            // Afficher un message de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setContentText("🚗 Voiture ajoutée avec succès !");
            alert.show();

            // Fermer la fenêtre d'ajout de voiture après l'ajout
            marqueField.getScene().getWindow().hide();
        } catch (NumberFormatException e) {
            afficherAlerte("Erreur de saisie", "⚠ Veuillez entrer un prix valide !");
        } catch (IllegalArgumentException e) {
            afficherAlerte("Erreur de saisie", e.getMessage());
        } catch (SQLException e) {
            afficherAlerte("Erreur SQL", "❌ Problème lors de l'ajout de la voiture.");
        }
    }

    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.show();
    }
}
