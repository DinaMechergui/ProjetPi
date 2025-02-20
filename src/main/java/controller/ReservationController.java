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
    @FXML private ComboBox<Event> eventCombo;
    @FXML private ComboBox<ServiceItem> serviceCombo;
    @FXML private DatePicker datePicker;
    @FXML private TextField priceField;
    @FXML private GridPane cartGrid;
    @FXML private Label totalPriceLabel;
    @FXML private Label statutLabel; // Label pour afficher le statut de l'événement
    @FXML private ImageView serviceImage;

    private ObservableList<ReservationCartItem> cartItems = FXCollections.observableArrayList();
    private ServiceReservation serviceReservation = new ServiceReservation();
    private ServiceEvent serviceEvent = new ServiceEvent();
    private ServiceService serviceService = new ServiceService();

    @FXML
    public void initialize() {
        loadEvents();
        loadServices();
        setupPriceListener();
        updateCartGrid();

        serviceCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                displayImage(newVal);
            }
        });

        // Mettre à jour le statut lorsqu'un événement est sélectionné
        eventCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                statutLabel.setText("Statut : " + newVal.getStatut().toString());

                // Désactiver les réservations si l'événement est annulé
                if (newVal.getStatut() == Event.Statut.ANNULE) {
                    showAlert("Événement annulé", "Cet événement est annulé.", "Vous ne pouvez pas effectuer de réservation.");
                }
            } else {
                statutLabel.setText("Statut : -");
            }
        });
    }

    private void displayImage(ServiceItem service) {
        Image image = new Image(service.getImageUrl());
        serviceImage.setImage(image);
    }

    private void loadEvents() {
        try {
            List<Event> events = serviceEvent.afficher();
            eventCombo.setItems(FXCollections.observableArrayList(events));

            eventCombo.setCellFactory(param -> new ListCell<Event>() {
                @Override
                protected void updateItem(Event event, boolean empty) {
                    super.updateItem(event, empty);
                    if (empty || event == null) {
                        setText(null);
                    } else {
                        setText(event.getNom());
                    }
                }
            });

            eventCombo.setButtonCell(new ListCell<Event>() {
                @Override
                protected void updateItem(Event event, boolean empty) {
                    super.updateItem(event, empty);
                    if (empty || event == null) {
                        setText(null);
                    } else {
                        setText(event.getNom());
                    }
                }
            });

        } catch (SQLException e) {
            showAlert("Erreur", "Erreur de chargement des événements", e.getMessage());
        }
    }

    private void loadServices() {
        try {
            List<ServiceItem> services = serviceService.afficher();
            serviceCombo.setItems(FXCollections.observableArrayList(services));

        } catch (SQLException e) {
            showAlert("Erreur", "Erreur de chargement des services", e.getMessage());
        }
    }

    private void setupPriceListener() {
        serviceCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                priceField.setText(String.valueOf(newVal.getPrix()));
            }
        });
    }

    @FXML
    private void addToCart() {
        try {
            ServiceItem selectedService = serviceCombo.getValue();
            Event selectedEvent = eventCombo.getValue();

            if (selectedEvent == null || selectedService == null || datePicker.getValue() == null) {
                showAlert("Erreur", "Champs manquants", "Veuillez remplir tous les champs.");
                return;
            }

            // Vérifier si l'événement est annulé
            if (selectedEvent.getStatut() == Event.Statut.ANNULE) {
                showAlert("Événement annulé", "Vous ne pouvez pas ajouter un service pour un événement annulé.", "");
                return;
            }

            Date date = Date.from(datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

            // Créer un item de panier avec le statut de l'événement
            ReservationCartItem item = new ReservationCartItem(
                    selectedService.getId(),
                    selectedService.getNom(),
                    selectedService.getPrix(),
                    date,
                    selectedEvent.getStatut() // Ajouter le statut ici
            );

            cartItems.add(item);
            updateCartGrid();
            // Ne pas vider l'événement
            // clearForm(); // Ne pas effacer l'événement sélectionné
        } catch (Exception e) {
            showAlert("Erreur", "Erreur d'ajout au panier", e.getMessage());
        }
    }

    private void updateCartGrid() {
        cartGrid.getChildren().clear();
        cartGrid.addRow(0, new Label("Service"), new Label("Prix (TND)"), new Label("Date"), new Label("Statut"), new Label("Action"));

        int rowIndex = 1;
        for (ReservationCartItem item : cartItems) {
            Label serviceNameLabel = new Label(item.getServiceName());
            Label priceLabel = new Label(String.valueOf(item.getPrice()));
            Label dateLabel = new Label(item.getDate().toString());
            Label statutLabel = new Label(item.getEventStatut().toString()); // Afficher le statut ici
            Button deleteButton = new Button("Supprimer");

            deleteButton.setStyle("-fx-background-color: #FF5733; -fx-text-fill: white;");
            deleteButton.setOnAction(event -> {
                cartItems.remove(item);
                updateCartGrid();
            });

            cartGrid.addRow(rowIndex++, serviceNameLabel, priceLabel, dateLabel, statutLabel, deleteButton);
        }

        updateTotalPrice();
    }

    @FXML
    private void confirmReservation() {
        try {
            Event selectedEvent = eventCombo.getValue();
            if (selectedEvent == null) {
                showAlert("Erreur", "Événement non sélectionné", "Veuillez sélectionner un événement.");
                return;
            }

            if (selectedEvent.getStatut() == Event.Statut.ANNULE) {
                showAlert("Erreur", "Événement annulé", "Vous ne pouvez pas réserver un événement annulé.");
                return;
            }

            for (ReservationCartItem item : cartItems) {
                reserve reservation = new reserve();
                reservation.setEvent(selectedEvent);
                reservation.setService(new ServiceItem(item.getServiceId()));
                reservation.setDateReservation(item.getDate());
                reservation.setPrixTotal(item.getPrice());

                // Attribution du statut en fonction de certaines conditions
                if (item.getPrice() > 1000) {  // Exemple de critère basé sur le prix
                    reservation.setStatut(reserve.StatutReservation.CONFIRMEE);
                } else if (item.getPrice() <= 1000 && item.getPrice() > 0) {
                    reservation.setStatut(reserve.StatutReservation.EN_ATTENTE);
                } else {
                    reservation.setStatut(reserve.StatutReservation.ANNULEE);
                }

                // Ajouter la réservation à la base de données
                serviceReservation.ajouter(reservation);
            }

            cartItems.clear();
            updateCartGrid();
            showAlert("Réservation", "Réservation réussie", "Votre réservation a été confirmée.");

        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la réservation", e.getMessage());
        }
    }

    private void updateTotalPrice() {
        double totalPrice = 0;
        for (ReservationCartItem item : cartItems) {
            totalPrice += item.getPrice();
        }
        totalPriceLabel.setText("Prix Total: " + totalPrice + " TND");
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearForm() {
        // Réinitialiser uniquement les champs non liés à l'événement sélectionné
        serviceCombo.getSelectionModel().clearSelection();
        datePicker.setValue(null);
    }
}
