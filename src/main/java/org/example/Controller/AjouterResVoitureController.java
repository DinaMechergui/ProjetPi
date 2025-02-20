package org.example.Controller;

import org.example.entities.ReservationVoiture;
import org.example.services.ServiceResVoiture;
import org.example.entities.Voiture;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.time.LocalDate;

public class AjouterResVoitureController {

    @FXML
    private TextField nompretf;

    @FXML
    private DatePicker ddtf;

    @FXML
    private DatePicker dftf;

    @FXML
    private TextField prixttf;

    private Voiture voiture;

    private ServiceResVoiture serviceResVoiture = new ServiceResVoiture();

    // Méthode pour recevoir l'objet voiture et pré-remplir les informations
    public void setVoitureData(Voiture voiture) {
        this.voiture = voiture;
        if (voiture != null) {
            // Pré-remplir les données de la voiture si nécessaire
            // Par exemple, si vous voulez afficher la marque dans un champ texte ou autre
            // marqueTextField.setText(voiture.getMarque()); // Si vous avez un champ pour afficher la marque
        }
    }

    @FXML
    public void ajouterResVoiture() {
        try {
            // Récupérer les valeurs des champs du formulaire
            String client = nompretf.getText();
            LocalDate datedebut = ddtf.getValue();
            LocalDate datefin = dftf.getValue();
            float prixtotal = Float.parseFloat(prixttf.getText());

            // Vérifier si les champs sont bien remplis
            if (client.isEmpty() || datedebut == null || datefin == null || prixtotal <= 0) {
                afficherErreur("⚠️ Veuillez remplir tous les champs correctement !");
                return;
            }

            // Créer l'objet ReservationVoiture avec les informations de la voiture
            if (voiture != null) {
                ReservationVoiture reservation = new ReservationVoiture(
                        voiture.getMarque(), client,
                        java.sql.Date.valueOf(datedebut),
                        java.sql.Date.valueOf(datefin),
                        prixtotal
                );
                reservation.setIdvoiture(voiture.getIdvoiture()); // Utiliser l'ID de la voiture

                // Appeler le service pour ajouter la réservation
                serviceResVoiture.ajouter(reservation);

                // Message de succès
                afficherMessage("✅ Réservation ajoutée avec succès !");
            } else {
                afficherErreur("❌ La voiture sélectionnée est invalide !");
            }

        } catch (NumberFormatException e) {
            afficherErreur("❌ Erreur : Prix total invalide !");
        } catch (SQLException e) {
            afficherErreur("❌ Problème lors de l'ajout à la base de données : " + e.getMessage());
            e.printStackTrace(); // Afficher plus de détails sur l'erreur
        }
    }

    // Méthode pour afficher les erreurs
    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.show();
    }

    // Méthode pour afficher les messages de succès
    private void afficherMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setContentText(message);
        alert.show();
    }
}