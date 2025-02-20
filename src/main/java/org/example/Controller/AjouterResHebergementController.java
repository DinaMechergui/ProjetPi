package org.example.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.example.entities.ReservationHebergement;
import org.example.services.ServiceResHebergement;


import java.sql.SQLException;

public class AjouterResHebergementController {

    @FXML
    private DatePicker ddtf;

    @FXML
    private DatePicker dftf;

    @FXML
    private TextField nompretf;

    @FXML
    private TextField prixttf;

    @FXML
    void AjouterResHebergement(ActionEvent event) {
        ServiceResHebergement serviceReservationHebergement = new ServiceResHebergement();

        try {
            // 🔹 Vérifier si le nom du preneur est vide
            String nomPrenom = nompretf.getText().trim();
            if (nomPrenom.isEmpty()) {
                throw new IllegalArgumentException("⚠ Le champ 'Nom du preneur' ne peut pas être vide !");
            }

            // 🔹 Vérifier si le prix est valide et positif
            String prixText = prixttf.getText().trim();
            if (prixText.isEmpty()) {
                throw new IllegalArgumentException("⚠ Le champ 'Prix' ne peut pas être vide !");
            }
            float prix = Float.parseFloat(prixText);
            if (prix <= 0) {
                throw new IllegalArgumentException("⚠ Le prix doit être un nombre positif !");
            }

            // 🔹 Vérifier si les dates sont valides
            if (ddtf.getValue() == null || dftf.getValue() == null) {
                throw new IllegalArgumentException("⚠ Les dates de début et de fin doivent être renseignées !");
            }

            // ✅ Création de l'objet ReservationHebergement
            ReservationHebergement reservation = new ReservationHebergement(
                   0, "client","date","date", prix);

            // ✅ Ajout dans la base de données
            serviceReservationHebergement.ajouter(reservation);

            // ✅ Message de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setContentText("🏨 Réservation ajoutée avec succès !");
            alert.show();

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
            alert.setContentText("❌ Problème lors de l'ajout de la réservation : " + e.getMessage());
            alert.show();
        }
    }
}
