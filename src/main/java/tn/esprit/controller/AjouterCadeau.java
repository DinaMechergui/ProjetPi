package tn.esprit.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import tn.esprit.entities.Cadeau;
import tn.esprit.entities.Evenement;
import tn.esprit.services.ServiceCadeau;
import tn.esprit.services.ServiceEvenement;

import java.sql.SQLException;
import java.util.List;

public class AjouterCadeau {

    @FXML
    private Button boutontf;

    @FXML
    private TextArea descriptiontf;

    @FXML
    private TextField diponibilitetf;

    @FXML
    private ComboBox<String> idenementtf; // ComboBox contenant les noms des événements

    @FXML
    private TextField nomtf;

    private final ServiceEvenement serviceEvenement = new ServiceEvenement();
    private final ServiceCadeau serviceCadeau = new ServiceCadeau();

    @FXML
    public void initialize() {
        // Appliquer des styles dès l'initialisation (si besoin, par exemple pour des placeholders)
        nomtf.setPromptText("Entrez le nom du cadeau");
        descriptiontf.setPromptText("Entrez la description du cadeau");
        diponibilitetf.setPromptText("true / false");
        idenementtf.setPromptText("Choisissez un événement");

        // Remplissage du ComboBox dans le fil d'exécution JavaFX
        Platform.runLater(() -> {
            List<Evenement> evenements = serviceEvenement.getAllEvenements();
            if (evenements == null || evenements.isEmpty()) {
                afficherErreur("Aucun événement n'est disponible pour l'instant.");
            } else {
                evenements.forEach(e -> idenementtf.getItems().add(e.getNom()));
            }
        });
    }

    @FXML
    void ajouterCadeau(ActionEvent event) {
        // Récupération et nettoyage des valeurs des champs
        String nom = nomtf.getText().trim();
        String description = descriptiontf.getText().trim();
        String disponibiliteStr = diponibilitetf.getText().trim();
        String nomEvenement = idenementtf.getSelectionModel().getSelectedItem();

        // Validation des champs obligatoires
        if (nom.isEmpty() || description.isEmpty() || disponibiliteStr.isEmpty() || nomEvenement == null) {
            afficherErreur("Veuillez remplir tous les champs !");
            return;
        }

        // Conversion et validation du champ de disponibilité
        boolean disponibilite;
        try {
            disponibilite = Boolean.parseBoolean(disponibiliteStr);
        } catch (Exception e) {
            afficherErreur("Le champ 'Disponibilité' doit être 'true' ou 'false'.");
            return;
        }

        try {
            // Récupération de l'ID de l'événement sélectionné
            int evenementId = serviceEvenement.getIdByName(nomEvenement);
            if (evenementId == -1) {
                afficherErreur("L'événement sélectionné est introuvable.");
                return;
            }

            // Création d'un nouvel objet Cadeau
            Cadeau cadeau = new Cadeau(0, nom, description, disponibilite, evenementId);

            // Ajout du cadeau dans la base de données
            serviceCadeau.ajouter(cadeau);

            afficherInformation("Cadeau ajouté avec succès !");
            reinitialiserChamps();

        } catch (SQLException e) {
            afficherErreur("Une erreur est survenue lors de l'ajout : " + e.getMessage());
        }
    }

    // Méthode pour réinitialiser les champs après l'ajout
    private void reinitialiserChamps() {
        nomtf.clear();
        descriptiontf.clear();
        diponibilitetf.clear();
        idenementtf.getSelectionModel().clearSelection();
    }

    // Méthode pour afficher une alerte d'erreur avec un style amélioré
    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText("Oups ! Il y a une erreur.");
        alert.setContentText(message);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/styles/alert.css").toExternalForm());
        alert.showAndWait();
    }

    // Méthode pour afficher une alerte d'information avec un style amélioré
    private void afficherInformation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText("Opération réussie");
        alert.setContentText(message);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/styles/alert.css").toExternalForm());
        alert.showAndWait();
    }
}
