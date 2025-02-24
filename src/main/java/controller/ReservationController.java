package controller;

import entities.Event;
import entities.ServiceItem;
import entities.ReservationCartItem;
import entities.reserve;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import services.ServiceEvent;
import services.ServiceReservation;
import services.ServiceService;

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
        loadServices(); // Charge les services dans la ComboBox
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
    private void loadServices() {
        try {
            List<ServiceItem> services = serviceService.afficher(); // Récupère la liste des services
            serviceCombo.setItems(FXCollections.observableArrayList(services)); // Ajoute les services à la ComboBox
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur de chargement des services", e.getMessage()); // Affiche une alerte en cas d'erreur
        }
    }

    // Méthode pour configurer un écouteur sur la sélection d'un service
    private void setupPriceListener() {
        serviceCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                priceField.setText(String.valueOf(newVal.getPrix())); // Met à jour le champ de prix
            }
        });
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
}