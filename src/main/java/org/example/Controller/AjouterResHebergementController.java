package org.example.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.entities.Hebergement;
import org.example.entities.ReservationHebergement;
import org.example.services.ServiceResHebergement;
import java.sql.Date;
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

    @FXML
    private Label errorLabel; // Label pour afficher les erreurs

    private Hebergement hebergement;

    private final ServiceResHebergement serviceReservationHebergement = new ServiceResHebergement();

    public void setHebergementData(Hebergement hebergement) {
        this.hebergement = hebergement;
        prixttf.setText(String.valueOf(hebergement.getPrixParNuit())); // Pré-remplir le prix
    }
/*
    @FXML
    public void ajouterResHebergement() {
        try {
            // Récupérer les valeurs du formulaire
            String client = nompretf.getText();
            LocalDate dateDebut = ddtf.getValue();
            LocalDate dateFin = dftf.getValue();
            float prixTotal = Float.parseFloat(prixttf.getText());

            // Vérification des champs
            if (client.isEmpty() || dateDebut == null || dateFin == null) {
                afficherErreur("⚠️ Veuillez remplir tous les champs !");
                return;
            }

            if (dateDebut.isBefore(LocalDate.now())) {
                afficherErreur("❌ La date de début ne peut pas être dans le passé !");
                return;
            }

            if (dateFin.isBefore(dateDebut)) {
                afficherErreur("❌ La date de fin doit être après la date de début !");
                return;
            }

            if (prixTotal <= 0) {
                afficherErreur("❌ Prix total invalide !");
                return;
            }

            // Vérifier si l'hébergement est déjà réservé
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
*/
    private void afficherErreur(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: red;");
    }

    private void afficherMessage(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: green;");
    }

    @FXML
    public void initialize() {
        // Désactiver les dates passées pour ddtf (Date Début)
        ddtf.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now())) { // Si la date est passée, on la désactive
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;"); // Rouge clair pour indiquer désactivation
                }
            }
        });

        // Désactiver les dates passées et s'assurer que la fin est après le début
        dftf.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now()) || (ddtf.getValue() != null && date.isBefore(ddtf.getValue()))) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });

        // Ajouter un écouteur pour mettre à jour la date de fin dynamiquement
        ddtf.valueProperty().addListener((obs, oldValue, newValue) -> {
            dftf.setValue(null); // Réinitialiser la date de fin
            dftf.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    if (date.isBefore(LocalDate.now()) || date.isBefore(newValue)) {
                        setDisable(true);
                        setStyle("-fx-background-color: #ffc0cb;");
                    }
                }
            });
        });
    }


}