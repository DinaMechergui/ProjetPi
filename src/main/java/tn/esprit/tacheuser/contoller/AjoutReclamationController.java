package tn.esprit.tacheuser.contoller;

import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import tn.esprit.tacheuser.models.Reclamation;
import tn.esprit.tacheuser.models.User;
import tn.esprit.tacheuser.service.ReclamationService;

import java.io.IOException;
import java.util.List;

public class AjoutReclamationController {

    @FXML
    private TextField titleField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private ListView<Reclamation> historyListView;

    private static User currentUser;

    // Méthode pour définir l'utilisateur connecté
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    // Méthode d'initialisation pour charger l'historique des réclamations
    @FXML
    private void initialize() {
        if (currentUser != null) {
            loadUserReclamations();
        } else {
            System.out.println("❌ L'utilisateur n'est pas connecté.");
        }
    }

    // Charger les réclamations spécifiques à l'utilisateur connecté
    private void loadUserReclamations() {
        ReclamationService reclamationService = new ReclamationService();
        List<Reclamation> reclamations = reclamationService.getReclamationsByUserId(currentUser.getId());

        // Afficher les réclamations dans le ListView avec un cell factory personnalisé
        historyListView.getItems().setAll(reclamations);
        historyListView.setCellFactory(param -> new ListCell<Reclamation>() {
            @Override
            protected void updateItem(Reclamation reclamation, boolean empty) {
                super.updateItem(reclamation, empty);
                if (empty || reclamation == null) {
                    setText(null);
                } else {
                    // Formater l'affichage des réclamations (Sujet, Description, Statut)
                    setText("Sujet: " + reclamation.getSujet() + "\nDescription: " + reclamation.getDescription() + "\nStatut: " + reclamation.getStatut());
                }
            }
        });
    }

    // Soumettre une nouvelle réclamation
    @FXML
    private void handleSubmitReclamation() {
        if (currentUser == null) {
            System.out.println("❌ L'utilisateur n'est pas connecté.");
            return;
        }

        String title = titleField.getText();
        String description = descriptionField.getText();

        if (title.isEmpty() || description.isEmpty()) {
            System.out.println("❌ Veuillez remplir tous les champs !");
            return;
        }

        // Créer un objet réclamation avec les données saisies
        Reclamation reclamation = new Reclamation();
        reclamation.setSujet(title);
        reclamation.setDescription(description);
        reclamation.setUserId(currentUser.getId()); // Lier l'utilisateur connecté à la réclamation
        reclamation.setStatut("En attente"); // Statut initial

        // Appeler la méthode addReclamation pour l'insertion dans la base de données
        ReclamationService reclamationService = new ReclamationService();
        if (reclamationService.addReclamation(reclamation)) {
            System.out.println("✅ Réclamation soumise avec succès !");
            clearForm();
            loadUserReclamations();  // Recharger l'historique des réclamations après soumission
        } else {
            System.out.println("❌ Erreur lors de l'ajout de la réclamation.");
        }
    }

    // Réinitialiser le formulaire après soumission
    private void clearForm() {
        titleField.clear();
        descriptionField.clear();
    }

    // Retourner au profil après soumission
    @FXML
    private void handleBackToProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/tacheuser/User/profil.fxml"));
            Scene profileScene = new Scene(loader.load());

            // Récupérer la scène actuelle et la remplacer
            Stage stage = (Stage) titleField.getScene().getWindow();
            stage.setScene(profileScene);
            stage.show();
        } catch (IOException e) {
            System.out.println("❌ Erreur lors du retour au profil : " + e.getMessage());
        }
    }
}
