package controller;

import entities.Event;
import entities.ServiceItem;
import entities.ReservationCartItem;
import entities.reserve;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import services.ServiceEvent;
import services.ServiceReservation;
import services.ServiceService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class ReservationController {
    // Composants FXML liés à l'interface utilisateur
    @FXML private ComboBox<Event> eventCombo; // Liste déroulante pour les événements
    @FXML private ComboBox<ServiceItem> serviceCombo; // Liste déroulante pour les services
    @FXML private DatePicker datePicker; // Sélecteur de date
    @FXML private TextField priceField; // Champ pour afficher le prix
    @FXML private GridPane cartGrid; // Grille pour afficher le panier
    @FXML private Label totalPriceLabel; // Label pour afficher le prix total
    @FXML private Label statutLabel; // Label pour afficher le statut de l'événement
    @FXML private ImageView serviceImage; // ImageView pour afficher l'image du service
    @FXML
    private GridPane serviceGrid;
    @FXML
    private Button loginButton;
    // Liste observable pour stocker les éléments du panier
    private ObservableList<ReservationCartItem> cartItems = FXCollections.observableArrayList();

    // Services pour interagir avec la base de données
    private ServiceReservation serviceReservation = new ServiceReservation();
    private ServiceEvent serviceEvent = new ServiceEvent();
    private ServiceService serviceService = new ServiceService();

    // Méthode d'initialisation appelée automatiquement par JavaFX
    @FXML
    public void initialize() {
        loadEvents(); // Charge les événements dans la ComboBox
        loadServicesGrid(); // Charge les services dans la ComboBox
        setupPriceListener(); // Configure un écouteur pour mettre à jour le prix
        updateCartGrid(); // Met à jour la grille du panier

        // Écouteur pour afficher l'image du service sélectionné
        serviceCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                displayImage(newVal); // Affiche l'image du service
            }
        });

        // Écouteur pour mettre à jour le statut de l'événement sélectionné
        eventCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                statutLabel.setText("Statut : " + newVal.getStatut().toString()); // Affiche le statut

                // Désactive les réservations si l'événement est annulé
                if (newVal.getStatut() == Event.Statut.ANNULE) {
                    showAlert("Événement annulé", "Cet événement est annulé.", "Vous ne pouvez pas effectuer de réservation.");
                }
            } else {
                statutLabel.setText("Statut : -"); // Réinitialise le statut si aucun événement n'est sélectionné
            }
        });
    }

    // Méthode pour afficher l'image d'un service
    private void displayImage(ServiceItem service) {
        Image image = new Image(service.getImageUrl()); // Charge l'image depuis l'URL
        serviceImage.setImage(image); // Affiche l'image dans l'ImageView
    }

    // Méthode pour charger les événements dans la ComboBox
    private void loadEvents() {
        try {
            List<Event> events = serviceEvent.afficher(); // Récupère la liste des événements
            eventCombo.setItems(FXCollections.observableArrayList(events)); // Ajoute les événements à la ComboBox

            // Personnalise l'affichage des éléments dans la ComboBox
            eventCombo.setCellFactory(param -> new ListCell<Event>() {
                @Override
                protected void updateItem(Event event, boolean empty) {
                    super.updateItem(event, empty);
                    if (empty || event == null) {
                        setText(null);
                    } else {
                        setText(event.getNom()); // Affiche le nom de l'événement
                    }
                }
            });

            // Personnalise l'affichage de l'élément sélectionné dans la ComboBox
            eventCombo.setButtonCell(new ListCell<Event>() {
                @Override
                protected void updateItem(Event event, boolean empty) {
                    super.updateItem(event, empty);
                    if (empty || event == null) {
                        setText(null);
                    } else {
                        setText(event.getNom()); // Affiche le nom de l'événement
                    }
                }
            });

        } catch (SQLException e) {
            showAlert("Erreur", "Erreur de chargement des événements", e.getMessage()); // Affiche une alerte en cas d'erreur
        }
    }

    // Méthode pour charger les services dans la ComboBox
    private void loadServicesGrid() {
        try {
            List<ServiceItem> services = serviceService.afficher(); // Charger les services
            serviceGrid.getChildren().clear(); // Vider le grid avant d'ajouter les nouveaux éléments

            int row = 0;
            int col = 0;

            for (ServiceItem service : services) {
                VBox serviceCard = new VBox(10);
                serviceCard.getStyleClass().add("service-card");

                // Image du service
                ImageView serviceImage = new ImageView(new Image(service.getImageUrl()));
                serviceImage.setFitWidth(150);
                serviceImage.setFitHeight(150);
                serviceImage.setPreserveRatio(true);

                // Nom du service
                Label serviceName = new Label(service.getNom());
                serviceName.getStyleClass().add("service-name");

                // Prix du service
                Label servicePrice = new Label("Prix: " + service.getPrix() + " TND");
                servicePrice.getStyleClass().add("service-price");

                // Bouton "Réserver"
                Button reserveButton = new Button("Réserver");
                reserveButton.getStyleClass().addAll("button", "reserve-button");

                reserveButton.setOnAction(event -> {
                    serviceCombo.setValue(service); // Sélectionner le service dans la ComboBox
                });

                // Ajouter les éléments dans la carte
                serviceCard.getChildren().addAll(serviceImage, serviceName, servicePrice, reserveButton);

                // Ajouter la carte dans le GridPane
                serviceGrid.add(serviceCard, col, row);

                col++;
                if (col == 3) { // Passer à la ligne suivante après 3 colonnes
                    col = 0;
                    row++;
                }
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur de chargement des services", e.getMessage());
        }
    }

    // Méthode pour configurer un écouteur sur la sélection d'un service
    private void setupPriceListener() {

    }

    // Méthode pour ajouter un service au panier
    @FXML
    private void addToCart() {
        try {
            ServiceItem selectedService = serviceCombo.getValue(); // Récupère le service sélectionné
            Event selectedEvent = eventCombo.getValue(); // Récupère l'événement sélectionné

            // Vérifie que tous les champs sont remplis
            if (selectedEvent == null || selectedService == null || datePicker.getValue() == null) {
                showAlert("Erreur", "Champs manquants", "Veuillez remplir tous les champs.");
                return;
            }

            // Vérifie si l'événement est annulé
            if (selectedEvent.getStatut() == Event.Statut.ANNULE) {
                showAlert("Événement annulé", "Vous ne pouvez pas ajouter un service pour un événement annulé.", "");
                return;
            }

            // Convertit la date sélectionnée en objet Date
            Date date = Date.from(datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

            // Crée un nouvel élément de panier
            ReservationCartItem item = new ReservationCartItem(
                    selectedService.getId(),
                    selectedService.getNom(),
                    selectedService.getPrix(),
                    date,
                    selectedEvent.getStatut() // Ajoute le statut de l'événement
            );

            cartItems.add(item); // Ajoute l'élément au panier
            updateCartGrid(); // Met à jour la grille du panier
        } catch (Exception e) {
            showAlert("Erreur", "Erreur d'ajout au panier", e.getMessage()); // Affiche une alerte en cas d'erreur
        }
    }

    // Méthode pour mettre à jour la grille du panier
    private void updateCartGrid() {
        cartGrid.getChildren().clear(); // Vide la grille
        cartGrid.addRow(0, new Label("Service"), new Label("Prix (TND)"), new Label("Date"), new Label("Statut"), new Label("Action")); // Ajoute les en-têtes

        // Ajoute chaque élément du panier à la grille
        int rowIndex = 1;
        for (ReservationCartItem item : cartItems) {
            Label serviceNameLabel = new Label(item.getServiceName());
            Label priceLabel = new Label(String.valueOf(item.getPrice()));
            Label dateLabel = new Label(item.getDate().toString());
            Label statutLabel = new Label(item.getEventStatut().toString()); // Affiche le statut
            Button deleteButton = new Button("Supprimer");

            deleteButton.setStyle("-fx-background-color: #FF5733; -fx-text-fill: white;"); // Style du bouton
            deleteButton.setOnAction(event -> {
                cartItems.remove(item); // Supprime l'élément du panier
                updateCartGrid(); // Met à jour la grille
            });

            cartGrid.addRow(rowIndex++, serviceNameLabel, priceLabel, dateLabel, statutLabel, deleteButton); // Ajoute une nouvelle ligne
        }

        updateTotalPrice(); // Met à jour le prix total
    }

    // Méthode pour confirmer la réservation
    @FXML
    private void confirmReservation() {
        try {
            Event selectedEvent = eventCombo.getValue(); // Récupère l'événement sélectionné
            if (selectedEvent == null) {
                showAlert("Erreur", "Événement non sélectionné", "Veuillez sélectionner un événement.");
                return;
            }

            // Vérifie si l'événement est annulé
            if (selectedEvent.getStatut() == Event.Statut.ANNULE) {
                showAlert("Erreur", "Événement annulé", "Vous ne pouvez pas réserver un événement annulé.");
                return;
            }

            // Ajoute chaque élément du panier à la base de données
            for (ReservationCartItem item : cartItems) {
                reserve reservation = new reserve();
                reservation.setEvent(selectedEvent);
                reservation.setService(new ServiceItem(item.getServiceId()));
                reservation.setDateReservation(item.getDate());
                reservation.setPrixTotal(item.getPrice());

                // Définit le statut de la réservation en fonction du prix
                if (item.getPrice() > 1000) {
                    reservation.setStatut(reserve.StatutReservation.CONFIRMEE);
                } else if (item.getPrice() <= 1000 && item.getPrice() > 0) {
                    reservation.setStatut(reserve.StatutReservation.EN_ATTENTE);
                } else {
                    reservation.setStatut(reserve.StatutReservation.ANNULEE);
                }

                serviceReservation.ajouter(reservation); // Ajoute la réservation à la base de données
            }

            cartItems.clear(); // Vide le panier
            updateCartGrid(); // Met à jour la grille
            showAlert("Réservation", "Réservation réussie", "Votre réservation a été confirmée."); // Affiche un message de succès
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la réservation", e.getMessage()); // Affiche une alerte en cas d'erreur
        }
    }

    // Méthode pour mettre à jour le prix total
    private void updateTotalPrice() {
        double totalPrice = 0;
        for (ReservationCartItem item : cartItems) {
            totalPrice += item.getPrice(); // Calcule le prix total
        }
        totalPriceLabel.setText("Prix Total: " + totalPrice + " TND"); // Affiche le prix total
    }

    // Méthode pour afficher une alerte
    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Méthode pour réinitialiser le formulaire
    private void clearForm() {
        serviceCombo.getSelectionModel().clearSelection(); // Réinitialise la sélection du service
        datePicker.setValue(null); // Réinitialise la date
    }

    @FXML
    private void goToStore() {
        try {
            // Charger le fichier FXML de la page des produits
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Product.fxml"));
            Parent root = loader.load();

            // Récupérer la scène actuelle
            Stage stage = (Stage) loginButton.getScene().getWindow();

            // Changer la scène pour afficher la page des produits
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de la page des produits.");
        }
    }

    public void goToEvent(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/reservation.fxml"));
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

    public void goToDriveAndStay(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hebergementclient.fxml"));
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

    public void goToGuest(ActionEvent actionEvent) {
    }

    @FXML
    public void goToCart(MouseEvent event) {
        try {
            // Charger la page du panier
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Cart.fxml"));
            Parent root = loader.load();

            // Récupérer la scène actuelle pour accéder à la fenêtre (Stage)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root)); // Changer la scène vers la page du panier
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToInvite() {
        try {
            // Charger le fichier FXML de la page des produits
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/menuOrganizer.fxml"));
            Parent root = loader.load();

            // Récupérer la scène actuelle
            Stage stage = (Stage) loginButton.getScene().getWindow();

            // Changer la scène pour afficher la page des produits
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de la page des produits.");
        }
    }
}