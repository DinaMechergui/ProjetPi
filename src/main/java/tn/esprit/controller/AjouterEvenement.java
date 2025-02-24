package tn.esprit.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import tn.esprit.entities.Evenement;
import tn.esprit.services.ServiceEvenement;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class AjouterEvenement {

    @FXML
    private Button boutontf;

    @FXML
    private DatePicker datetf;

    @FXML
    private TextField lieutf;

    @FXML
    private TextField nomtf;

    @FXML
    void ajouterEvenement(ActionEvent event) {
        // Récupérer les valeurs des champs
        String nom = nomtf.getText().trim();
        String lieu = lieutf.getText().trim();
        LocalDate date = datetf.getValue();

        // Vérifier les champs vides
        if (nom.isEmpty() || lieu.isEmpty() || date == null) {
            afficherAlerte("Erreur", "Tous les champs doivent être remplis !");
            return;
        }

        // Vérifier que le nom et le lieu contiennent seulement des lettres et espaces
        if (!nom.matches("[a-zA-ZÀ-ÿ\\s]+")) {
            afficherAlerte("Erreur", "Le nom ne doit contenir que des lettres !");
            return;
        }
        if (!lieu.matches("[a-zA-ZÀ-ÿ\\s]+")) {
            afficherAlerte("Erreur", "Le lieu ne doit contenir que des lettres !");
            return;
        }

        // Vérifier que la date sélectionnée n'est pas dans le passé
        if (date.isBefore(LocalDate.now())) {
            afficherAlerte("Erreur", "La date ne peut pas être dans le passé !");
            return;
        }

        // Vérifier si l'événement existe déjà (nom + lieu)
        ServiceEvenement serviceEvenement = new ServiceEvenement();
        try {
            if (serviceEvenement.existeDeja(nom, lieu)) {
                afficherAlerte("Erreur", "Un événement avec ce nom et ce lieu existe déjà !");
                return;
            }

            // Si l'événement n'existe pas, l'ajouter
            Evenement evenement = new Evenement(0, nom, lieu, date.toString());
            serviceEvenement.ajouter(evenement);
            afficherAlerte("Succès", "Événement ajouté avec succès !");
            naviguerVersListeEvenements();

        } catch (SQLException e) {
            afficherAlerte("Erreur", "Problème lors de l'ajout : " + e.getMessage());
        }
    }

    // Méthode pour afficher une alerte
    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.show();
    }

    // Méthode pour naviguer vers la liste des événements après ajout
    private void naviguerVersListeEvenements() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherEvenement.fxml"));
            nomtf.getScene().setRoot(root);
        } catch (IOException e) {
            afficherAlerte("Erreur", "Impossible de naviguer : " + e.getMessage());
        }
    }
}
