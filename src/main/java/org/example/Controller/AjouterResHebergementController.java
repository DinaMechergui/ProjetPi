package org.example.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.entities.Hebergement;
import org.example.entities.ReservationHebergement;
import org.example.services.ServiceResHebergement;
import org.example.services.SmsService;

import java.io.IOException;
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
    private Button confirmerButton;

    @FXML
    private Button retourButton;

    @FXML
    private Button sendConfirmationButton;


    @FXML
    private Label errorLabel;

    @FXML
    private Label personnalisationLabel;

    private Hebergement hebergement;

    private final ServiceResHebergement serviceReservationHebergement = new ServiceResHebergement();
    private static final float PRIX_MAX_PERSONNALISATION = 400;

    public void setHebergementData(Hebergement hebergement) {
        this.hebergement = hebergement;
        mettreAJourPrixTotal(); // Calculer le prix dès l'affichage
    }

    @FXML
    private void retour(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hebergementclient.fxml"));
            Parent root = loader.load();
            Scene scene = retourButton.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            afficherErreur("❌ Erreur lors du chargement de la page de retour : " + e.getMessage());
        }
    }

    @FXML
    public void ajouterResHebergement() {
        try {
            String client = nompretf.getText();
            LocalDate dateDebut = ddtf.getValue();
            LocalDate dateFin = dftf.getValue();
            float prixTotal = Float.parseFloat(prixttf.getText());

            // Vérifications des champs
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
            if (serviceReservationHebergement.estReserve(hebergement.getIdheb(), dateDebut, dateFin)) {
                afficherErreur("❌ Cet hébergement est déjà réservé à ces dates !");
                return;
            }

            // Création et enregistrement de la réservation
            ReservationHebergement reservation = new ReservationHebergement(
                    hebergement.getIdheb(), client,
                    java.sql.Date.valueOf(dateDebut),
                    java.sql.Date.valueOf(dateFin),
                    prixTotal
            );
            serviceReservationHebergement.ajouter(reservation);

            afficherMessage("✅ Réservation effectuée avec succès !");
            clearFields();
        } catch (NumberFormatException e) {
            afficherErreur("❌ Erreur : Prix total invalide !");
        } catch (SQLException e) {
            afficherErreur("❌ Erreur avec la base de données : " + e.getMessage());
        }
    }

    private void clearFields() {
        nompretf.clear();
        ddtf.setValue(null);
        dftf.setValue(null);
        prixttf.setText("0");

    }

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
        configureDatePickers();

        // Activation du bouton Confirmer uniquement si le formulaire est valide
        nompretf.textProperty().addListener((obs, oldValue, newValue) -> validateForm());
        ddtf.valueProperty().addListener((obs, oldValue, newValue) -> validateForm());
        dftf.valueProperty().addListener((obs, oldValue, newValue) -> validateForm());


    }

    private void configureDatePickers() {
        ddtf.setDayCellFactory(picker -> createDateCell(LocalDate.now(), null));
        dftf.setDayCellFactory(picker -> createDateCell(LocalDate.now(), ddtf.getValue()));

        ddtf.valueProperty().addListener((obs, oldValue, newValue) -> {
            dftf.setValue(null);
            dftf.setDayCellFactory(picker -> createDateCell(LocalDate.now(), newValue));
            mettreAJourPrixTotal();
        });

        dftf.valueProperty().addListener((obs, oldValue, newValue) -> mettreAJourPrixTotal());
    }

    private DateCell createDateCell(LocalDate minDate, LocalDate maxDate) {
        return new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(minDate) || (maxDate != null && date.isBefore(maxDate))) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        };
    }

    private void mettreAJourPrixTotal() {
        LocalDate dateDebut = ddtf.getValue();
        LocalDate dateFin = dftf.getValue();

        if (dateDebut != null && dateFin != null && !dateFin.isBefore(dateDebut)) {
            float prixTotal = calculerPrixTotal(dateDebut, dateFin);
            prixttf.setText(String.valueOf(prixTotal));

            if (prixTotal > PRIX_MAX_PERSONNALISATION) {
                afficherPopupPersonalisation();
            }
        } else {
            prixttf.setText("0");
        }
    }

    private float calculerPrixTotal(LocalDate dateDebut, LocalDate dateFin) {
        long nombreDeNuits = java.time.temporal.ChronoUnit.DAYS.between(dateDebut, dateFin);
        return (float) (hebergement.getPrixParNuit() * nombreDeNuits);
    }

    private void afficherPopupPersonalisation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Personnalisation disponible !");
        alert.setHeaderText("Votre réservation dépasse " + PRIX_MAX_PERSONNALISATION + "€. Voulez-vous ajouter des options ?");
        alert.setContentText("Choisissez une option :");

        ButtonType buttonOui = new ButtonType("Oui");
        ButtonType buttonNon = new ButtonType("Non", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonOui, buttonNon);

        alert.showAndWait().ifPresent(response -> {
            if (response == buttonOui) {
                ouvrirFenetrePersonalisation();
            }
        });
    }

    private void ouvrirFenetrePersonalisation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/personalisation.fxml"));
            Parent root = loader.load();

            PersonalisationController controller = loader.getController();
            controller.setReservationController(this);

            Stage stage = new Stage();
            stage.setTitle("Personnalisation");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            afficherErreur("Erreur lors de l'ouverture de la personnalisation : " + e.getMessage());
        }
    }

    @FXML
    public void sendReminder(ActionEvent event) {
        try {
            String client = nompretf.getText();
            LocalDate dateDebut = ddtf.getValue();
            LocalDate dateFin = dftf.getValue();

            // Vérifications avant l'envoi
            if (client.isEmpty() || dateDebut == null || dateFin == null) {
                afficherErreur("⚠️ Veuillez remplir tous les champs pour envoyer un rappel !");
                return;
            }

            // Préparer le message de confirmation
            String message = "Confirmation de réservation : Bonjour " + client + ", votre réservation pour l'hotel" + hebergement.getNom() +  "est confirmée. " +
                    "Votre séjour commence le " + dateDebut + " et se termine le " + dateFin + ". Merci pour votre réservation !";

            // Utiliser la classe SmsService pour envoyer le rappel
            SmsService smsService = new SmsService();
            smsService.envoyerSmsRappel("+21620584986", message);  // Remplace "+1234567890" par le numéro du client

            // Afficher un message de confirmation à l'utilisateur
            afficherMessage("✅ Rappel de réservation envoyé avec succès !");
        } catch (Exception e) {
            afficherErreur("❌ Erreur lors de l'envoi du rappel : " + e.getMessage());
        }
    }


    @FXML
    public void afficherPersonnalisation(String personnalisation) {
        personnalisationLabel.setText("Personnalisation : " + personnalisation);
        personnalisationLabel.setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
    }

    private void validateForm() {
        boolean isValid = !nompretf.getText().isEmpty() &&
                ddtf.getValue() != null &&
                dftf.getValue() != null &&
                !dftf.getValue().isBefore(ddtf.getValue());
        confirmerButton.setDisable(!isValid);
    }
}
