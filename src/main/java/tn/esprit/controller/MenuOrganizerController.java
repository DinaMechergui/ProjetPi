package tn.esprit.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;

public class MenuOrganizerController {

    @FXML
    private AnchorPane contentPane;

    private void loadPage(String fxmlFile) {
        try {
            // Charge dynamiquement la page FXML demandée
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlFile));
            AnchorPane pane = loader.load();
            contentPane.getChildren().setAll(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showAjouterInvite() {
        loadPage("Ajouterinvite.fxml");
    }

    @FXML
    private void showAjouterCadeau() {
        loadPage("AjouterCadeau.fxml");
    }

    @FXML
    private void showAjouterEvenement() {
        loadPage("AjouterEvenement.fxml");
    }

    @FXML
    private void showAfficherEvenements() {
        loadPage("affichageEvenement.fxml");
    }

    @FXML
    private void showAfficherInvites() {
        loadPage("Afficherinvite.fxml");
    }

    @FXML
    private void showAfficherCadeaux() {
        loadPage("AfficherCadeau.fxml");
    }
}
