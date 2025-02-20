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
    private ComboBox<String> evenementCB; // ComboBox pour la sélection de l'événement

    private final ServiceEvenement serviceEvenement = new ServiceEvenement();

    @FXML
    public void initialize() {
        // Définition de placeholders pour améliorer l'expérience utilisateur
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

        // Récupération et nettoyage des valeurs des champs
        String nom = nomtf.getText().trim();
        String prenom = prenomtf.getText().trim();
        String email = emailtf.getText().trim();
        String telephone = teltf.getText().trim();
        String confirmationStr = confimationtf.getText().trim();

        // Vérifier que l'utilisateur a sélectionné un événement
        String nomEvenement = evenementCB.getSelectionModel().getSelectedItem();
        if (nomEvenement == null) {
            afficherErreur("Veuillez sélectionner un événement !");
            return;
        }

        // Validation des champs obligatoires
        if(nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || telephone.isEmpty() || confirmationStr.isEmpty()){
            afficherErreur("Tous les champs doivent être renseignés !");
            return;
        }

        // Validation du téléphone: exactement 8 chiffres
        if (!telephone.matches("\\d{8}")) {
            afficherErreur("Le téléphone doit contenir exactement 8 chiffres.");
            return;
        }

        // Validation de l'email: doit contenir au moins un '@'
        if (!email.contains("@")) {
            afficherErreur("L'email doit contenir le caractère '@'.");
            return;
        }

        // Conversion et validation de la confirmation
        boolean confirmation;
        try {
            confirmation = Boolean.parseBoolean(confirmationStr);
        } catch (Exception e) {
            afficherErreur("Le champ 'Confirmation' doit être 'true' ou 'false'.");
            return;
        }

        try {
            // Récupérer l'ID de l'événement sélectionné
            int evenementId = serviceEvenement.getIdByName(nomEvenement);
            if(evenementId == -1) {
                afficherErreur("L'événement sélectionné est introuvable !");
                return;
            }

            // Création d'un nouvel objet Invite (id à 0 pour insertion auto)
            Invite invite = new Invite(0, nom, prenom, email, telephone, confirmation, evenementId);

            // Ajout de l'invité dans la base de données
            serviceInvite.ajouter(invite);

            // Affichage du message de succès
            afficherInformation("✅ L'invité a été ajouté avec succès !");
            reinitialiserChamps();

        } catch (SQLException e) {
            afficherErreur("❌ Une erreur est survenue : " + e.getMessage());
        }
    }

    // Réinitialiser les champs après un ajout réussi
    private void reinitialiserChamps() {
        nomtf.clear();
        prenomtf.clear();
        emailtf.clear();
        teltf.clear();
        confimationtf.clear();
        evenementCB.getSelectionModel().clearSelection();
    }

    // Méthode pour afficher une alerte d'erreur avec style personnalisé (CSS optionnel)
    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText("Problème de saisie ou de traitement");
        alert.setContentText(message);
        // Si besoin, appliquer un CSS personnalisé
        // alert.getDialogPane().getStylesheets().add(getClass().getResource("/styles/alert.css").toExternalForm());
        alert.showAndWait();
    }

    // Méthode pour afficher une alerte d'information avec style personnalisé (CSS optionnel)
    private void afficherInformation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText("Opération réussie");
        alert.setContentText(message);
        // Si besoin, appliquer un CSS personnalisé
        // alert.getDialogPane().getStylesheets().add(getClass().getResource("/styles/alert.css").toExternalForm());
        alert.showAndWait();
    }
}
