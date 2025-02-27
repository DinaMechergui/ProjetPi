package tn.esprit.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MenuOrganizerController {
    @FXML
    private StackPane contentArea; // Assurez-vous que cela correspond bien au FXML

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("contentArea: " + contentArea);
        Platform.runLater(() -> loadPage("")); // Exécute après l'initialisation
    }

    @FXML
    private void showAjouterEvenement() {
        loadPage("AjouterEvenement.fxml");
    }

    @FXML
    private void showAfficherInvite() {
        loadPage("Afficherinvite.fxml");
    }

    @FXML
    private void showAfficherCadeau() {
        loadPage("AfficherCadeau.fxml");
    }

    @FXML
    private void showAfficherevenement() {
        loadPage("AffichageEvenement.fxml");
    }


    private void loadPage(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlFile));
            Parent root = loader.load();
            contentArea.getChildren().setAll(root); // Remplace le contenu affiché
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
