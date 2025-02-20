package tn.esprit.tacheuser.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import tn.esprit.tacheuser.models.Reclamation;
import tn.esprit.tacheuser.service.ReclamationService;
import tn.esprit.tacheuser.utils.SessionManager;

public class AjouterReclamationController {

    @FXML
    private TextField sujetField;

    @FXML
    private TextArea descriptionField;

    private final ReclamationService reclamationService = new ReclamationService();

    @FXML
    private void handleAjouterReclamation() {
        int userId = SessionManager.getCurrentUser().getId();
        String sujet = sujetField.getText();
        String description = descriptionField.getText();

        if (sujet.isEmpty() || description.isEmpty()) {
            System.out.println("Veuillez remplir tous les champs.");
            return;
        }

        Reclamation reclamation = new Reclamation(userId, sujet, description, "En attente");
        reclamationService.addReclamation(reclamation);
        System.out.println("Réclamation ajoutée avec succès !");
    }
}
