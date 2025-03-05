package tn.esprit.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.entities.Cadeau;
import tn.esprit.services.ServiceCadeau;

import javax.swing.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class CodeInputController {
    @FXML
    private TextField codeInput;



    private final ServiceCadeau serviceCadeau = new ServiceCadeau();
    private String codePrefix;
    private String codeSuffix;

    @FXML
    public void initialize() {
        // Ajouter un écouteur d'événements pour détecter les modifications du champ codeInput
        codeInput.textProperty().addListener((observable, oldValue, newValue) -> {
            // Vérifier que le texte est suffisamment long pour extraire les sous-chaînes
            if (newValue.length() >= 6) {
                // Extraire les 6 premiers caractères
                codePrefix = newValue.substring(0, 6);
                // Extraire les 6 derniers caractères
                codeSuffix = newValue.substring(6);
            } else {
                // Si le texte est trop court, initialiser les variables avec des chaînes vides
                codePrefix = newValue;
                codeSuffix = "";
            }
            // Afficher les valeurs pour vérification (facultatif)
            System.out.println("Code Prefix: " + codePrefix);
            System.out.println("Code Suffix: " + codeSuffix);
        });
    }





    @FXML
    public void onValider() {
        System.out.println("Bouton Valider cliqué !"); // Log pour vérifier que la méthode est appelée

        String codeQR = codePrefix;
        System.out.println("Code saisi : " + codeQR); // Log pour vérifier le code saisi

        if (codeQR.isEmpty()) {
            System.out.println("Code vide !"); // Log pour vérifier la validation
            afficherErreur("Veuillez entrer un code.");
            return;
        }

        try {
            System.out.println("Récupération des cadeaux..."); // Log pour vérifier l'appel au service
            List<Cadeau> cadeaux = serviceCadeau.getCadeauxByEvenementCode(codeQR);
            System.out.println("Cadeaux récupérés : " + cadeaux.size()); // Log pour vérifier les résultats

            if (cadeaux.isEmpty()) {
                afficherErreur("Aucun cadeau trouvé pour ce code.");
            } else {
                afficherCadeaux(cadeaux);
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL : " + e.getMessage()); // Log pour vérifier les erreurs SQL
            afficherErreur("Erreur lors de la récupération des cadeaux : " + e.getMessage());
        }
    }

    /* void afficherCadeaux(List<Cadeau> cadeaux) {
        // Afficher les cadeaux dans une nouvelle interface
    }*/

    private void afficherErreur(String message) {
        // Afficher une alerte d'erreur
    }
    private void afficherCadeaux(List<Cadeau> cadeaux) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CadeauList.fxml"));
            Parent root = loader.load();

            CadeauListController controller = loader.getController();
            controller.setCadeaux(cadeaux);
            controller.setCodeSuffix(codeSuffix);

            // Afficher la nouvelle interface
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            afficherErreur("Erreur lors du chargement de l'interface : " + e.getMessage());
        }
    }
    @FXML
    public void retour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MenuOrganizer.fxml")); // Remplace par le bon fichier FXML
            Parent root = loader.load();
            Stage stage = (Stage) codeInput.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            afficherErreur("Erreur lors du chargement de l'interface : " + e.getMessage());
        }
    }
}