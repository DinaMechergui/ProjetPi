package tn.esprit.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import tn.esprit.entities.Evenement;
import tn.esprit.entities.Invite;
import tn.esprit.services.ServiceEvenement;
import tn.esprit.services.ServiceInvite;

import java.io.IOException;
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

    @FXML
    private Label evenementActuelLabel;

    private final ServiceEvenement serviceEvenement = new ServiceEvenement();

    // Variable pour stocker l'événement actuellement sélectionné
    private Evenement evenementActuel;

    // Setter pour recevoir l'événement sélectionné
    public void setEvenementActuel(Evenement evenement) {
        this.evenementActuel = evenement;
        if (evenementActuelLabel != null) {
            evenementActuelLabel.setText("Événement : " + evenement.getNom());
        }
        if (evenementCB != null) {
            evenementCB.getSelectionModel().select(evenement.getNom());
        }
    }

    @FXML
    public void initialize() {
        nomtf.setPromptText("Entrez le nom");
        prenomtf.setPromptText("Entrez le prénom");
        emailtf.setPromptText("Entrez l'email");
        teltf.setPromptText("Entrez le téléphone (8 chiffres)");
        //confimationtf.setPromptText("true / false"); // Champ masqué ou non utilisé si fixé par défaut
        evenementCB.setPromptText("Sélectionnez un événement");

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

        String nom = nomtf.getText().trim();
        String prenom = prenomtf.getText().trim();
        String email = emailtf.getText().trim();
        String telephone = teltf.getText().trim();
        // On ne lit pas le champ confirmation puisque sa valeur est forcée à false
        // String confirmationStr = confimationtf.getText().trim();

        String nomEvenement = evenementCB.getSelectionModel().getSelectedItem();
        if (nomEvenement == null) {
            afficherErreur("Veuillez sélectionner un événement !");
            return;
        }

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || telephone.isEmpty()) {
            afficherErreur("Tous les champs doivent être renseignés !");
            return;
        }

        if (!telephone.matches("\\d{8}")) {
            afficherErreur("Le téléphone doit contenir exactement 8 chiffres.");
            return;
        }

        if (!email.contains("@")) {
            afficherErreur("L'email doit contenir le caractère '@'.");
            return;
        }

        try {
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
            boolean confirmation = false;

            Invite invite = new Invite(0, nom, prenom, email, telephone, confirmation, evenementId);
            serviceInvite.ajouter(invite);
            afficherInformation("✅ L'invité a été ajouté avec succès !");
            reinitialiserChamps();
        } catch (SQLException e) {
            afficherErreur("❌ Une erreur est survenue : " + e.getMessage());
        }
    }

    private void reinitialiserChamps() {
        nomtf.clear();
        prenomtf.clear();
        emailtf.clear();
        teltf.clear();
        confimationtf.clear();
        evenementCB.getSelectionModel().clearSelection();
    }

    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText("Problème de saisie ou de traitement");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void afficherInformation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText("Opération réussie");
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Méthode pour revenir à la page AffichageEvenement.fxml
    @FXML
    void retour(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MenuOrganizer.fxml"));
            Parent root = loader.load();
            // Ici, on suppose que la scène actuelle est récupérable via un des composants (par exemple, le bouton)
            ((Button) event.getSource()).getScene().setRoot(root);
        } catch (IOException ex) {
            ex.printStackTrace();
            //afficherErreur("Erreur", "Impossible de charger la page AffichageEvenement.fxml : " + ex.getMessage());
        }
    }
}
