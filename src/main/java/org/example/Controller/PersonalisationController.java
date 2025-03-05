package org.example.Controller;

import org.example.entities.Personalisation;
import org.example.services.PersonalisationService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.sql.SQLException;
import java.util.List;

public class PersonalisationController {

    @FXML
    private TextField reservationIdField;

    @FXML
    private CheckBox decorationCheckBox;

    @FXML
    private CheckBox breakfastCheckBox;

    @FXML
    private CheckBox spaCheckBox;

    @FXML
    private GridPane personalisationGrid;

    private final PersonalisationService personalisationService = new PersonalisationService();

    public PersonalisationController() throws SQLException {
    }

    @FXML
    public void initialize() throws SQLException {
        loadPersonalisationData();
    }

    // Charger les personnalisations sous forme de cartes dans GridPane
    private void loadPersonalisationData() throws SQLException {
        personalisationGrid.getChildren().clear(); // Vider le GridPane
        List<Personalisation> personalisations = personalisationService.getAllPersonalisation();
        int column = 0;
        int row = 0;

        for (Personalisation p : personalisations) {
            //VBox card = createPersonalisationCard(p);
            //personalisationGrid.add(card, column, row);
            column++;
            if (column == 3) { // Max 3 cartes par ligne
                column = 0;
                row++;
            }
        }
    }

    // Créer une carte pour afficher une personnalisation
    /*private VBox createPersonalisationCard(Personalisation p) {
        VBox card = new VBox();
        card.setStyle("-fx-border-color: black; -fx-padding: 10px; -fx-background-color: #f0f0f0; -fx-spacing: 5px;");

        Text reservationText = new Text("Réservation: " + p.getReservationId());
        Text decorationText = new Text("Décoration: " + (p.isDecoration() ? "Oui" : "Non"));
        Text breakfastText = new Text("Petit-déj: " + (p.isBreakfast() ? "Oui" : "Non"));
        Text spaText = new Text("Spa: " + (p.isSpa() ? "Oui" : "Non"));

        card.getChildren().addAll(reservationText, decorationText, breakfastText, spaText);
        return card;
    }*/

    // Ajouter une personnalisation
    @FXML
    private void handleAddPersonalisation() {
        try {
            boolean decoration = decorationCheckBox.isSelected();
            boolean breakfast = breakfastCheckBox.isSelected();
            boolean spa = spaCheckBox.isSelected();

            Personalisation personalisation = new Personalisation();
            personalisation.setDecoration(decoration);
            personalisation.setBreakfast(breakfast);
            personalisation.setSpa(spa);

            personalisationService.createPersonalisation(personalisation);
            loadPersonalisationData(); // Recharger l'affichage
        } catch (NumberFormatException | SQLException e) {
            showAlert("Erreur", "Veuillez saisir un ID de réservation valide.");
        }
    }
    public void setReservationId(int reservationId) {
        reservationIdField.setText(String.valueOf(reservationId));
    }

    private AjouterResHebergementController reservationController;

    public void setReservationController(AjouterResHebergementController controller) {
        this.reservationController = controller;
    }


    // Afficher une alerte
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
