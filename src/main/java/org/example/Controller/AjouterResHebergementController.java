package org.example.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.example.entities.Hebergement;
import org.example.entities.ReservationHebergement;
import org.example.services.ServiceResHebergement;


import java.sql.SQLException;
import java.time.LocalDate;

public class AjouterResHebergementController {

    @FXML
    private TextField nompretf;

    @FXML
    private DatePicker ddtf;

    @FXML
    private DatePicker dftf;

    @FXML
    private TextField prixttf;

    @FXML
    private Button reserverButton;

    private Hebergement hebergement;

    private final ServiceResHebergement serviceReservationHebergement = new ServiceResHebergement();

    public void setHebergementData(Hebergement hebergement) {
        this.hebergement = hebergement;
        prixttf.setText(String.valueOf(hebergement.getPrixParNuit())); // Pré-remplir le prix
    }

    @FXML
    public void ajouterResHebergement() {
        try {
            // Récupérer les valeurs du formulaire
            String client = nompretf.getText();
            LocalDate dateDebut = ddtf.getValue();
            LocalDate dateFin = dftf.getValue();
            float prixTotal = Float.parseFloat(prixttf.getText());

            // Vérification des champs
            if (client.isEmpty() || dateDebut == null || dateFin == null || prixTotal <= 0) {
                afficherErreur("⚠️ Veuillez remplir tous les champs correctement !");
                return;
            }

            // Vérifier si l'hébergement est disponible
            if (serviceReservationHebergement.estReserve(hebergement.getIdheb(), dateDebut, dateFin)) {
                afficherErreur("❌ Cet hébergement est déjà réservé à ces dates !");
                return;
            }

            // Créer et enregistrer la réservation
            ReservationHebergement reservation = new ReservationHebergement(
                    hebergement.getIdheb(), client,
                    java.sql.Date.valueOf(dateDebut),
                    java.sql.Date.valueOf(dateFin),
                    prixTotal
            );

            serviceReservationHebergement.ajouter(reservation);

            afficherMessage("✅ Réservation effectuée avec succès !");
        } catch (NumberFormatException e) {
            afficherErreur("❌ Erreur : Prix total invalide !");
        } catch (SQLException e) {
            afficherErreur("❌ Erreur avec la base de données : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.show();
    }

    private void afficherMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setContentText(message);
        alert.show();
    }
}
