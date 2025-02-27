package Wedding.controllers;

import Wedding.entities.Reservation;
import Wedding.service.ServiceCommande;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ReservationAdminController {
    @FXML
    private Button loginButton;
    @FXML
    private GridPane reservationGrid;

    @FXML
    private Button btnActualiser;

    private final ServiceCommande serviceCommande = new ServiceCommande();

    @FXML
    public void initialize() {
        loadReservations();

        btnActualiser.setOnAction(e -> loadReservations());
    }

    private void loadReservations() {
        reservationGrid.getChildren().clear();

        List<Reservation> reservations = serviceCommande.getAllReservations();
        int row = 0, col = 0;

        for (Reservation reservation : reservations) {
            VBox card = createReservationCard(reservation);
            reservationGrid.add(card, col, row);

            col++;
            if (col == 3) {  // 3 colonnes par ligne
                col = 0;
                row++;
            }
        }
    }

    private VBox createReservationCard(Reservation reservation) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: #ffffff; -fx-padding: 10; -fx-border-color: #6d8c7a; -fx-border-radius: 10;");
        card.setPrefWidth(250);

        Label produitLabel = new Label("Produit: " + reservation.getProduit().getNom());
        Label utilisateurLabel = new Label("Utilisateur: " + reservation.getUtilisateur().trim());
        Label quantiteLabel = new Label("Quantité: " + reservation.getQuantite());
        Label statutLabel = new Label("Statut: " + reservation.getStatut());

        Button btnSupprimer = new Button("Supprimer");
        btnSupprimer.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white;");
        btnSupprimer.setOnAction(e -> supprimerReservation(reservation.getId().intValue()));

        card.getChildren().addAll(produitLabel, utilisateurLabel, quantiteLabel, statutLabel, btnSupprimer);
        return card;
    }

    private void supprimerReservation(int idReservation) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous vraiment supprimer cette réservation ?", ButtonType.YES, ButtonType.NO);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                serviceCommande.deleteReservation(idReservation);
                loadReservations();
            }
        });
    }

    public void goToProduit(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminDashboardProduit.fxml"));
            Parent root = loader.load();

            // Récupérer la fenêtre actuelle à partir de l'événement
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de la page des produits.");
        }
    }


}

