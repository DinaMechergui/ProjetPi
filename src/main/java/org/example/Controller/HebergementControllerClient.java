package org.example.Controller;

import com.sun.javafx.menu.MenuItemBase;
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
import javafx.scene.layout.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.concurrent.Worker;
import org.example.components.StarRatingInput;
import org.example.entities.Avis;
import org.example.entities.Hebergement;
import org.example.services.AvisService;
import org.example.services.ServiceHebergement;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class HebergementControllerClient {
    private final ServiceHebergement serviceHebergement = new ServiceHebergement();
    private final AvisService avisService = new AvisService();

    @FXML
    private GridPane gridPaneHebergements; // GridPane pour afficher les hébergements
    @FXML
    private Button loginButton; // Bouton de connexion pour navigation
    @FXML
    private HBox starRatingContainer; // Conteneur pour les étoiles
    @FXML
    private TextArea commentaireField; // Champ de commentaire
    @FXML
    private Button submitAvisButton; // Bouton pour poster l'avis
    @FXML
    private HBox topBar;
    @FXML
    private WebView mapView; // WebView pour afficher la carte OpenStreetMap

    private StarRatingInput starRatingInput; // Composant pour la notation
    private Hebergement currentHebergement; // Hébergement actuellement sélectionné

    // Méthode pour naviguer vers la page des produits
    @FXML
    private void goToStore() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Product.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de la page des produits.");
        }
    }

    // Méthode d'initialisation pour charger les hébergements à l'ouverture
    public void initialize() {
        try {
            // Initialiser le composant des étoiles
            starRatingInput = new StarRatingInput();
            starRatingContainer.getChildren().add(starRatingInput);

            // Désactiver la section de notation par défaut
            enableRatingSection(false);

            // Charger les hébergements
            loadHebergements();

            // Charger la carte OpenStreetMap
            if (mapView != null) {
                //loadOpenStreetMap();
            } else {
                System.err.println("Erreur : mapView n'est pas initialisé.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
        // Ajouter un écouteur pour les erreurs de la WebView

    @FXML
    private void goToMap() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MapView.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur de la carte
            MapViewController mapViewController = loader.getController();

            // Passer la liste des hébergements au contrôleur de la carte
            List<Hebergement> hebergements = serviceHebergement.afficher(); // Récupérer les hébergements
            mapViewController.setHebergements(hebergements);

            // Afficher la page de carte
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour charger les hébergements
    private void loadHebergements() throws SQLException {
        gridPaneHebergements.getChildren().clear(); // Réinitialiser l'affichage
        List<Hebergement> hebergements = serviceHebergement.afficher(); // Récupérer la liste des hébergements
        int row = 0;
        int col = 0;

        for (Hebergement hebergement : hebergements) {
            VBox hebergementCard = createHebergementCard(hebergement);
            gridPaneHebergements.add(hebergementCard, col, row);
            col++;
            if (col > 2) { // 3 colonnes par ligne
                col = 0;
                row++;
            }

        }
    }



    private VBox createHebergementCard(Hebergement hebergement) {
        VBox card = new VBox(10);
        card.getStyleClass().add("hebergement-card");

        // Image de l'hébergement
        ImageView hebergementImage = new ImageView();
        String imageUrl = hebergement.getImageUrl();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                Image image = new Image(imageUrl); // Si c'est une URL valide
                hebergementImage.setImage(image);
            } catch (Exception e) {
                hebergementImage.setImage(new Image("file:defaultImage.jpg")); // Image par défaut en cas d'erreur
                e.printStackTrace();
            }
        } else {
            hebergementImage.setImage(new Image("file:defaultImage.jpg")); // Image par défaut si l'URL est vide
        }

        // Paramètres pour l'affichage de l'image
        hebergementImage.setFitWidth(150);
        hebergementImage.setFitHeight(150);
        hebergementImage.setPreserveRatio(true);

        // Nom, adresse et prix
        Label hebergementNom = new Label(hebergement.getNom());
        hebergementNom.getStyleClass().add("hebergement-name");

        Label hebergementAdresse = new Label("Adresse : " + hebergement.getAdresse());
        hebergementAdresse.getStyleClass().add("hebergement-address");

        Label hebergementPrix = new Label("Prix/Nuit : " + String.format("%.2f", hebergement.getPrixParNuit()) + " TND");
        hebergementPrix.getStyleClass().add("hebergement-price");

        // Disponibilité
        Label hebergementDispo = new Label(hebergement.isDisponible() ? "Disponible" : "Indisponible");
        hebergementDispo.getStyleClass().add("hebergement-availability");
        hebergementDispo.setStyle(hebergement.isDisponible() ? "-fx-text-fill: green;" : "-fx-text-fill: red;");

        // Moyenne des avis sous forme d'étoiles
        HBox ratingStars = new HBox(5); // Conteneur pour les étoiles
        try {
            double moyenne = avisService.getAverageRating(hebergement.getIdheb());
            int moyenneArrondie = (int) Math.round(moyenne); // Arrondir la moyenne à l'entier le plus proche

            // Ajouter des étoiles en fonction de la moyenne
            for (int i = 0; i < 5; i++) {
                ImageView star = new ImageView();
                if (i < moyenneArrondie) {
                    star.setImage(new Image("file:star_full.png")); // Étoile pleine
                } else {
                    star.setImage(new Image("file:star_empty.png")); // Étoile vide
                }
                star.setFitWidth(15);
                star.setFitHeight(15);
                ratingStars.getChildren().add(star);
            }

            // Ajouter un label pour afficher la moyenne en texte
            Label moyenneLabel = new Label(String.format("(%.1f/5)", moyenne));
            ratingStars.getChildren().add(moyenneLabel);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Bouton de réservation
        Button reserverButton = new Button("Réserver");
        reserverButton.getStyleClass().add("button");
        if (!hebergement.isDisponible()) {
            reserverButton.setDisable(true); // Désactiver le bouton si l'hébergement est indisponible
        }
        reserverButton.setOnAction(event -> {
            currentHebergement = hebergement; // Définir l'hébergement actuel
            ouvrirFormulaireReservation(hebergement); // Ouvrir le formulaire de réservation
        });

        // Bouton pour donner un avis
        Button avisButton = new Button("Donner un avis");
        avisButton.getStyleClass().add("button");
        avisButton.setOnAction(event -> {
            currentHebergement = hebergement; // Définir l'hébergement actuel
            enableRatingSection(true); // Activer la section de notation
        });

        // Ajouter les éléments à la carte
        HBox buttonContainer = new HBox(10); // Conteneur pour les boutons
        buttonContainer.getChildren().addAll(reserverButton, avisButton); // Ajouter les boutons

        // Section des avis pour cet hébergement
        VBox avisContainer = new VBox(5);
        avisContainer.getStyleClass().add("avis-container");

        try {
            List<Avis> avisList = avisService.getAvisByHebergement(hebergement.getIdheb());
            for (Avis avis : avisList) {
                VBox avisCard = new VBox(5);
                avisCard.getStyleClass().add("avis-card");

                Label noteLabel = new Label("Note : " + avis.getNote() + "/5");
                Label commentaireLabel = new Label(avis.getCommentaire());

                avisCard.getChildren().addAll(noteLabel, commentaireLabel);
                avisContainer.getChildren().add(avisCard);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Ajouter tous les éléments à la carte
        card.getChildren().addAll(
                hebergementImage, hebergementNom, hebergementAdresse, hebergementPrix,
                hebergementDispo, ratingStars, buttonContainer, avisContainer
        );

        return card;
    }

    private void enableRatingSection(boolean enable) {
        starRatingContainer.setDisable(!enable); // Activer/désactiver les étoiles
        commentaireField.setDisable(!enable); // Activer/désactiver le champ de commentaire

        submitAvisButton.setDisable(!enable); // Activer/désactiver le bouton "Poster Avis"
    }

    // Méthode pour ouvrir un formulaire de réservation avec les détails d'un hébergement
    private void ouvrirFormulaireReservation(Hebergement hebergement) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterReservationHebergement.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur de la nouvelle page et passer les données de l'hébergement
            AjouterResHebergementController controller = loader.getController();
            controller.setHebergementData(hebergement);

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Réservation Hébergement");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
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
            Avis avis = new Avis();
            avis.setIdheb(currentHebergement.getIdheb()); // Hébergement actuel
            avis.setNote(note);
            avis.setCommentaire(commentaire);
            avis.setDateCreation(new Date());

            // Ajouter l'avis
            avisService.ajouterAvis(avis);

            // Recharger les avis après ajout
            loadAvis(currentHebergement.getIdheb());
        } catch (IllegalArgumentException | SQLException e) {
            afficherAlerte("Erreur", e.getMessage());
        }
    }

    // Méthode pour afficher une alerte
    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.show();
    }

    @FXML
    private VBox avisSection; // Section des avis

    // Méthode pour charger les avis d'un hébergement
    private void loadAvis(int idheb) {
        try {
            List<Avis> avisList = avisService.getAvisByHebergement(idheb);

            // Afficher les avis dans un VBox
            VBox avisContainer = new VBox(10);
            for (Avis avis : avisList) {
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
            e.printStackTrace();
        }
    }

    @FXML
    private TextField localisationField; // Champ de texte pour la localisation
    @FXML
    private Slider prixSlider; // Slider pour le budget

    // Méthode pour naviguer vers la page des recommandations
    @FXML
    private void goToRecommendation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Recommendation.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Recommandations");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de la page de recommandation.");
        }
    }

    // Méthode pour appliquer les préférences de recherche
    @FXML
    private void appliquerPreferences() {
        String localisation = localisationField.getText();
        double prixMin = prixSlider.getMin(); // Valeur minimale du slider
        double prixMax = prixSlider.getValue(); // Valeur actuelle du slider

        try {
            // Récupérer les recommandations basées sur les préférences
            List<Hebergement> recommandations = serviceHebergement.recommanderHebergements(localisation, prixMin, prixMax, true);

            if (recommandations.isEmpty()) {
                afficherAlerte("Information", "Aucun hébergement ne correspond à vos critères.");
                return;
            }

            // Charger la vue des recommandations
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Recommendation.fxml"));
            Parent root = loader.load();

            // Passer les recommandations au contrôleur des recommandations
            RecommendationController recommendationController = loader.getController();
            recommendationController.afficherRecommandations(recommandations);

            // Afficher la vue des recommandations
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Recommandations");
            stage.show();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            afficherAlerte("Erreur", "Une erreur s'est produite lors de la recherche des recommandations.");
        }
    }

    // Méthode pour naviguer vers la page des événements
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