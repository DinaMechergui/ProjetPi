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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.entities.AvisVoiture;
import org.example.entities.Voiture;
import org.example.services.ServiceVoiture;
import org.example.services.AvisVoitureService;
import org.example.components.StarRatingInput;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class VoitureControllerClient {
    private final ServiceVoiture serviceVoiture = new ServiceVoiture();
    private final AvisVoitureService avisVoitureService = new AvisVoitureService();

    // Chemins des fichiers FXML
    private static final String RESERVATION_VOITURE_FXML = "/ajouterReservationVoiture.fxml";
    private static final String RECOMMENDATION_VOITURE_FXML = "/RecommendationVoiture.fxml";
    private static final String EVENT_FXML = "/reservation.fxml";
    private static final String MENU_ORGANIZER_FXML = "/menuOrganizer.fxml";
    private static final String HEBERGEMENT_CLIENT_FXML = "/hebergementclient.fxml";
    private static final String CART_FXML = "/Cart.fxml";

    @FXML
    private GridPane gridPaneVoitures; // GridPane pour afficher les voitures
    @FXML
    private Button loginButton; // Bouton de connexion pour navigation
    @FXML
    private HBox starRatingContainer; // Conteneur pour les étoiles
    @FXML
    private TextArea commentaireField; // Champ de commentaire
    @FXML
    private Button submitAvisButton; // Bouton pour poster l'avis
    @FXML
    private VBox avisSection; // Section des avis
    @FXML
    private TextField marqueField; // Champ de texte pour la marque
    @FXML
    private Slider prixSlider; // Slider pour le budget

    private StarRatingInput starRatingInput; // Composant pour la notation
    private Voiture currentVoiture; // Voiture actuellement sélectionnée

    // Méthode d'initialisation pour charger les voitures à l'ouverture
    public void initialize() {
        try {
            // Initialiser le composant des étoiles
            starRatingInput = new StarRatingInput();
            starRatingContainer.getChildren().add(starRatingInput);

            // Désactiver la section de notation par défaut
            enableRatingSection(false);

            // Charger les voitures
            loadVoitures();
        } catch (SQLException e) {
            afficherAlerte("Erreur", "Erreur lors du chargement des voitures : " + e.getMessage());
        }
    }

    // Méthode pour charger les voitures et afficher des cartes dans le GridPane
    private void loadVoitures() throws SQLException {
        gridPaneVoitures.getChildren().clear(); // Réinitialiser l'affichage
        List<Voiture> voitures = serviceVoiture.afficher(); // Récupérer la liste des voitures

        if (voitures.isEmpty()) {
            afficherAlerte("Information", "Aucune voiture disponible.");
            return;
        }

        int row = 0;
        int col = 0;

        for (Voiture voiture : voitures) {
            VBox voitureCard = createVoitureCard(voiture);
            gridPaneVoitures.add(voitureCard, col, row);
            col++;
            if (col > 2) { // 3 colonnes par ligne
                col = 0;
                row++;
            }
        }
    }

    // Méthode pour créer une carte de voiture
    private VBox createVoitureCard(Voiture voiture) {
        VBox card = new VBox(10);
        card.getStyleClass().add("voiture-card");

        // Image de la voiture
        ImageView voitureImage = new ImageView();
        String imageUrl = voiture.getImageUrl();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                Image image = new Image(imageUrl); // Si c'est une URL valide
                voitureImage.setImage(image);
            } catch (Exception e) {
                voitureImage.setImage(new Image("file:defaultCarImage.jpg")); // Image par défaut en cas d'erreur
                afficherAlerte("Erreur", "Erreur lors du chargement de l'image de la voiture : " + e.getMessage());
            }
        } else {
            voitureImage.setImage(new Image("file:defaultCarImage.jpg")); // Image par défaut si l'URL est vide
        }

        // Paramètres pour l'affichage de l'image
        voitureImage.setFitWidth(150);
        voitureImage.setFitHeight(150);
        voitureImage.setPreserveRatio(true);

        // Marque et prix
        Label voitureMarque = new Label(voiture.getMarque());
        voitureMarque.getStyleClass().add("voiture-name");

        Label voiturePrix = new Label("Prix/Jour : " + String.format("%.2f", voiture.getPrix()) + " TND");
        voiturePrix.getStyleClass().add("voiture-price");

        // Bouton de réservation
        Button reserverButton = new Button("Réserver");
        reserverButton.getStyleClass().add("button");
        reserverButton.setDisable(!voiture.isDisponible());
        reserverButton.setOnAction(event -> {
            currentVoiture = voiture; // Définir la voiture actuelle
            ouvrirPageReservation(voiture); // Ouvrir la page de réservation
        });

        // Bouton pour donner un avis
        Button avisButton = new Button("Donner un avis");
        avisButton.getStyleClass().add("button");
        avisButton.setOnAction(event -> {
            currentVoiture = voiture; // Définir la voiture actuelle
            enableRatingSection(true); // Activer la section de notation
        });

        // Ajouter les éléments à la carte
        HBox buttonContainer = new HBox(10); // Conteneur pour les boutons
        buttonContainer.getChildren().addAll(reserverButton, avisButton); // Ajouter les boutons

        card.getChildren().addAll(voitureImage, voitureMarque, voiturePrix, buttonContainer);

        return card;
    }

    // Méthode pour activer/désactiver la section de notation
    private void enableRatingSection(boolean enable) {
        starRatingContainer.setDisable(!enable); // Activer/désactiver les étoiles
        commentaireField.setDisable(!enable); // Activer/désactiver le champ de commentaire
        submitAvisButton.setDisable(!enable); // Activer/désactiver le bouton "Poster Avis"
    }

    // Méthode pour ouvrir la page de réservation
    private void ouvrirPageReservation(Voiture voiture) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(RESERVATION_VOITURE_FXML));
            Parent root = loader.load();

            // Récupérer le contrôleur de la nouvelle page et passer les données de la voiture
            AjouterResVoitureController controller = loader.getController();
            controller.setVoitureData(voiture);

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Réservation Voiture");
            stage.show();
        } catch (IOException e) {
            afficherAlerte("Erreur", "Erreur lors de l'ouverture de la page de réservation : " + e.getMessage());
        }
    }

    // Méthode pour poster un avis
    @FXML
    private void submitAvis() {
        try {
            int note = starRatingInput.getRating(); // Récupérer la note sélectionnée
            String commentaire = commentaireField.getText();

            if (note < 1 || note > 5) {
                throw new IllegalArgumentException("Veuillez sélectionner une note entre 1 et 5.");
            }

            // Créer un nouvel avis
            AvisVoiture avisVoiture = new AvisVoiture();
            avisVoiture.setIdvoiture(currentVoiture.getIdvoiture()); // Voiture actuelle
            avisVoiture.setNote(note);
            avisVoiture.setCommentaire(commentaire);
            avisVoiture.setDateCreation(new Date());

            // Ajouter l'avis
            avisVoitureService.ajouterAvis(avisVoiture);

            // Recharger les avis après ajout
            loadAvis(currentVoiture.getIdvoiture());
        } catch (IllegalArgumentException | SQLException e) {
            afficherAlerte("Erreur", e.getMessage());
        }
    }

    // Méthode pour charger les avis d'une voiture
    private void loadAvis(int idVoiture) {
        try {
            List<AvisVoiture> avisList = avisVoitureService.getAvisByVoiture(idVoiture);

            // Afficher les avis dans un VBox
            VBox avisContainer = new VBox(10);
            for (AvisVoiture avis : avisList) {
                VBox avisCard = new VBox(5);
                avisCard.getStyleClass().add("avis-card");

                Label noteLabel = new Label("Note : " + avis.getNote() + "/5");
                Label commentaireLabel = new Label(avis.getCommentaire());

                avisCard.getChildren().addAll(noteLabel, commentaireLabel);
                avisContainer.getChildren().add(avisCard);
            }

            // Ajouter le conteneur d'avis à la section des avis
            avisSection.getChildren().clear(); // Réinitialiser l'affichage
            avisSection.getChildren().add(avisContainer); // Ajouter les avis
        } catch (SQLException e) {
            afficherAlerte("Erreur", "Erreur lors du chargement des avis : " + e.getMessage());
        }
    }

    // Méthode pour appliquer les préférences de recherche
    @FXML
    private void appliquerPreferences() {
        String marque = marqueField.getText();
        double prixMin = prixSlider.getMin(); // Valeur minimale du slider
        double prixMax = prixSlider.getValue(); // Valeur actuelle du slider

        try {
            // Récupérer les recommandations basées sur les préférences
            List<Voiture> recommandations = serviceVoiture.recommanderVoitures(marque, prixMin, prixMax, true);

            if (recommandations.isEmpty()) {
                afficherAlerte("Information", "Aucune voiture ne correspond à vos critères.");
                return;
            }

            // Charger la vue des recommandations
            FXMLLoader loader = new FXMLLoader(getClass().getResource(RECOMMENDATION_VOITURE_FXML));
            Parent root = loader.load();

            // Passer les recommandations au contrôleur des recommandations
            RecommendationVoitureController recommendationController = loader.getController();
            recommendationController.afficherRecommandations(recommandations);

            // Afficher la vue des recommandations
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Recommandations Voitures");
            stage.show();
        } catch (IOException | SQLException e) {
            afficherAlerte("Erreur", "Erreur lors de la recherche des recommandations : " + e.getMessage());
        }
    }

    // Méthode pour afficher une alerte
    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.show();
    }

    // Méthodes de navigation
    @FXML
    private void goToEvent() {
        naviguerVersPage(EVENT_FXML, "Page des événements");
    }

    @FXML
    private void goToInvite() {
        naviguerVersPage(MENU_ORGANIZER_FXML, "Page des invités");
    }

    @FXML
    private void goToDriveAndStay() {
        naviguerVersPage(HEBERGEMENT_CLIENT_FXML, "Page des hébergements");
    }

    @FXML
    public void goToCart(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(CART_FXML));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            afficherAlerte("Erreur", "Erreur lors de l'ouverture du panier : " + e.getMessage());
        }
    }

    // Méthode utilitaire pour la navigation
    private void naviguerVersPage(String fxmlPath, String titre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(titre);
            stage.show();
        } catch (IOException e) {
            afficherAlerte("Erreur", "Erreur lors du chargement de la page : " + e.getMessage());
        }
    }
}