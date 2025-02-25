package tn.esprit.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import tn.esprit.entities.Evenement;
import tn.esprit.entities.Invite;
import tn.esprit.services.ServiceEvenement;
import tn.esprit.services.ServiceInvite;

import java.sql.SQLException;
import java.util.List;

public class Ajouterinvite {

    @FXML
    private TextField nomtf;

    @FXML
    private TextField prenomtf;

    @FXML
    private TextField emailtf;

    @FXML
    private TextField teltf;

    @FXML
    private TextField confimationtf;

    @FXML
    private  ComboBox<String> evenementCB; // ComboBox pour la sélection de l'événement

    // Label optionnel pour afficher l'événement courant
    @FXML
    private  Label evenementActuelLabel;

    private final ServiceEvenement serviceEvenement = new ServiceEvenement();

    // Variable pour stocker l'événement actuellement sélectionné
    private Evenement evenementActuel;

    // Setter pour recevoir l'événement sélectionné
    public void setEvenementActuel(Evenement evenement) {
        this.evenementActuel = evenement;
        // Afficher le nom de l'événement dans le label (si le label est présent dans le FXML)
        if (evenementActuelLabel != null) {
            evenementActuelLabel.setText("Événement : " + evenement.getNom());
        }
        // Pré-sélectionner cet événement dans le ComboBox
        if (evenementCB != null) {
            evenementCB.getSelectionModel().select(evenement.getNom());
        }
    }

    @FXML
    public void initialize() {
        // Définition des placeholders
        nomtf.setPromptText("Entrez le nom");
        prenomtf.setPromptText("Entrez le prénom");
        emailtf.setPromptText("Entrez l'email");
        teltf.setPromptText("Entrez le téléphone (8 chiffres)");
        confimationtf.setPromptText("true / false");
        evenementCB.setPromptText("Sélectionnez un événement");

        // Remplissage du ComboBox avec les noms des événements
        Platform.runLater(() -> {
            List<Evenement> evenements = serviceEvenement.getAllEvenements();
            if (evenements == null || evenements.isEmpty()) {
                afficherErreur("⚠ Aucun événement trouvé !");
            } else {
                evenements.forEach(e -> evenementCB.getItems().add(e.getNom()));
            }
        });
    }

    @FXML
    void ajouterInvite(ActionEvent event) {
        ServiceInvite serviceInvite = new ServiceInvite();

        // Récupération des valeurs
        String nom = nomtf.getText().trim();
        String prenom = prenomtf.getText().trim();
        String email = emailtf.getText().trim();
        String telephone = teltf.getText().trim();
        String confirmationStr = confimationtf.getText().trim();

        // Vérification qu'un événement est sélectionné
        String nomEvenement = evenementCB.getSelectionModel().getSelectedItem();
        if (nomEvenement == null) {
            afficherErreur("Veuillez sélectionner un événement !");
            return;
        }

        // Validation des champs obligatoires
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || telephone.isEmpty() || confirmationStr.isEmpty()) {
            afficherErreur("Tous les champs doivent être renseignés !");
            return;
        }

        // Validation du téléphone : exactement 8 chiffres
        if (!telephone.matches("\\d{8}")) {
            afficherErreur("Le téléphone doit contenir exactement 8 chiffres.");
            return;
        }

        // Validation de l'email
        if (!email.contains("@")) {
            afficherErreur("L'email doit contenir le caractère '@'.");
            return;
        }

        // Conversion de la confirmation en booléen
        boolean confirmation;
        try {
            confirmation = Boolean.parseBoolean(confirmationStr);
        } catch (Exception e) {
            afficherErreur("Le champ 'Confirmation' doit être 'true' ou 'false'.");
            return;
        }

        try {
            // Récupérer l'ID de l'événement
            int evenementId;
            if (evenementActuel != null && evenementActuel.getNom().equals(nomEvenement)) {
                evenementId = evenementActuel.getId();
            } else {
                evenementId = serviceEvenement.getIdByName(nomEvenement);
            }
            if (evenementId == -1) {
                afficherErreur("L'événement sélectionné est introuvable !");
                return;
            }

            // Créer l'objet Invite et l'ajouter
            Invite invite = new Invite(0, nom, prenom, email, telephone, confirmation, evenementId);
            serviceInvite.ajouter(invite);
            afficherInformation("✅ L'invité a été ajouté avec succès !");
            reinitialiserChamps();
        } catch (SQLException e) {
            afficherErreur("❌ Une erreur est survenue : " + e.getMessage());
        }
    }

    // Réinitialiser les champs après ajout
    private void reinitialiserChamps() {
        nomtf.clear();
        prenomtf.clear();
        emailtf.clear();
        teltf.clear();
        confimationtf.clear();
        evenementCB.getSelectionModel().clearSelection();
    }

    // Affichage d'une alerte d'erreur
    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText("Problème de saisie ou de traitement");
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Affichage d'une alerte d'information
    private void afficherInformation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText("Opération réussie");
        alert.setContentText(message);
        alert.showAndWait();
    }
}