package org.example.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.entities.Voiture;
import org.example.services.ServiceVoiture;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class VoitureControllerClient {
    private final ServiceVoiture serviceVoiture = new ServiceVoiture();

    @FXML
    private GridPane gridPaneVoitures;
    @FXML
    private Button retourButton;
    @FXML
    private Button loginButton;

    public void initialize() {
        try {
            loadVoitures();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadVoitures() throws SQLException {
        gridPaneVoitures.getChildren().clear();
        List<Voiture> voitures = serviceVoiture.afficher();
        int row = 0, col = 0;

        for (Voiture voiture : voitures) {
            VBox voitureCard = new VBox(10);
            voitureCard.getStyleClass().add("voiture-card");


            Label voitureMarque = new Label(voiture.getMarque());
            voitureMarque.getStyleClass().add("voiture-name");

            Label voiturePrix = new Label("Prix/Jour : " + String.format("%.2f", voiture.getPrix()) + " TND");
            voiturePrix.getStyleClass().add("voiture-price");

            Button reserverButton = new Button("Réserver");
            reserverButton.getStyleClass().add("button");
            reserverButton.setDisable(!voiture.isDisponible());
            reserverButton.setOnAction(event -> ouvrirPageReservation(voiture));

            voitureCard.getChildren().addAll( voitureMarque, voiturePrix, reserverButton);
            gridPaneVoitures.add(voitureCard, col, row);

            col++;
            if (col > 2) {
                col = 0;
                row++;
            }
        }
    }

    private void ouvrirPageReservation(Voiture voiture) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterReservationVoiture.fxml"));
            Parent root = loader.load();

            AjouterResVoitureController controller = loader.getController();
            if (controller != null) {
                controller.setVoitureData(voiture);
            }

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToEvent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/reservation.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de la page des événements.");
        }
    }

    // Méthode pour naviguer vers la page des invités
    @FXML
    private void goToInvite() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/menuOrganizer.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de la page des invités.");
        }
    }

    // Méthode pour revenir à la page des hébergements
    @FXML
    private void goToDriveAndStay() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hebergementclient.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de la page des hébergements.");
        }
    }

    // Méthode pour ouvrir la page du panier
    @FXML
    public void goToCart(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Cart.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
