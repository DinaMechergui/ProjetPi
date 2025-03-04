package controller;

import Wedding.controllers.CartController;
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
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import services.ServiceEvent;
import services.ServiceReservation;
import services.ServiceService;
import tn.esprit.tacheuser.models.User;
import tn.esprit.tacheuser.utils.SessionManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;


public class ReservationController {

    @FXML private ComboBox<Event> eventCombo;
    @FXML private ComboBox<ServiceItem> serviceCombo;
    @FXML private DatePicker datePicker;
    @FXML private TextField priceField;
    @FXML private Label totalPriceLabel;
    @FXML private Label statutLabel;
    @FXML private ImageView serviceImage;
    @FXML private GridPane cartGrid;
    @FXML
    private DatePicker dateMariage;
    @FXML
    private Button btnVerifierMeteo;
    @FXML
    private Label labelMeteoMariage;
    @FXML
    private Label labelRecommandations;



    private ObservableList<ReservationCartItem> cartItems = FXCollections.observableArrayList();
    private  ServiceReservation serviceReservation = new ServiceReservation();
    private final ServiceEvent serviceEvent = new ServiceEvent();
    private final ServiceService serviceService = new ServiceService();
    private User currentUser;



    @FXML
    private void verifierMeteoMariage() {
        LocalDate dateChoisie = dateMariage.getValue();
        if (dateChoisie == null) {
            labelMeteoMariage.setText("❌ Veuillez choisir une date.");
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate maxDate = today.plusDays(14); // API limite à 14 jours
        if (dateChoisie.isAfter(maxDate)) {
            labelMeteoMariage.setText("❌ Les prévisions ne sont disponibles que jusqu'à " + maxDate);
            labelRecommandations.setText("📆 Essayez une date dans les 14 prochains jours.");
            return;
        }

        String ville = "Tunis";
        String apiKey = "3938ce9742174adfb75225109250303";
        String urlString = "http://api.weatherapi.com/v1/forecast.json?key=" + apiKey + "&q=" + ville + "&days=14&lang=fr";

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            System.out.println("HTTP Response Code: " + responseCode);

            if (responseCode != 200) {
                labelMeteoMariage.setText("❌ Erreur API: Code " + responseCode);
                return;
            }

            // ✅ Lecture de la réponse en UTF-8 pour éviter les erreurs d'affichage
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                output.append(line);
            }
            conn.disconnect();

            // 🔍 Debugging : Afficher la réponse API dans la console
            System.out.println("API Response: " + output.toString());

            // ✅ Analyse du JSON
            JSONObject json = new JSONObject(output.toString());
            JSONObject forecast = json.getJSONObject("forecast");
            JSONArray forecastDays = forecast.getJSONArray("forecastday");

            boolean found = false;
            for (int i = 0; i < forecastDays.length(); i++) {
                JSONObject day = forecastDays.getJSONObject(i);
                String dateAPI = day.getString("date");

                // Vérifier si la date demandée existe dans les prévisions
                if (dateAPI.equals(dateChoisie.toString())) {
                    double temperature = day.getJSONObject("day").getDouble("avgtemp_c");
                    String condition = day.getJSONObject("day").getJSONObject("condition").getString("text");

                    // ✅ Mise à jour de l'affichage
                    labelMeteoMariage.setText("🌡 Température: " + temperature + "°C");
                    labelRecommandations.setText("🌦 Condition: " + condition);
                    found = true;
                    break;
                }
            }

            if (!found) {
                labelMeteoMariage.setText("❌ Aucune donnée météo pour cette date.");
                labelRecommandations.setText("📆 Essayez une date plus proche.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            labelMeteoMariage.setText("❌ Erreur de connexion à l'API.");
        }
    }

    // ✅ JavaFX requires a default constructor, do not add a custom one!

    @FXML
    public void initialize() {
        currentUser = SessionManager.getUser();

        // 🔍 Debugging logs
        if (cartGrid == null) System.err.println("❌ cartGrid is NULL! Check fx:id in FXML.");
        if (eventCombo == null) System.err.println("❌ eventCombo is NULL! Check fx:id.");
        if (serviceCombo == null) System.err.println("❌ serviceCombo is NULL! Check fx:id.");

        loadEvents();
        updateCartGrid();


        // ✅ Prevent users from selecting past dates
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #FFBBBB;");
                }
            }
        });

        // ✅ Load services dynamically when event is selected
        eventCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadServicesForEvent(newVal.getId());
            } else {
                serviceCombo.getItems().clear();
            }
        });

        // ✅ Show service image & update price dynamically
        serviceCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                serviceImage.setImage(new Image(newVal.getImageUrl()));
                priceField.setText(String.valueOf(newVal.getPrix()));
            }
        });
    }

    private void updateCartGrid() {
    }

    private void loadEvents() {
        try {
            List<Event> events = serviceEvent.afficher();
            eventCombo.setItems(FXCollections.observableArrayList(events));
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur de chargement des événements", e.getMessage());
        }
    }

    private void loadServicesForEvent(int eventId) {
        try {
            List<ServiceItem> services = serviceService.getServicesByEventId(eventId);
            serviceCombo.setItems(FXCollections.observableArrayList(services));
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur de chargement des services", e.getMessage());
        }
    }

    @FXML
    private void addToCart() {
        try {
            Event selectedEvent = eventCombo.getValue();
            ServiceItem selectedService = serviceCombo.getValue();
            LocalDate selectedDate = datePicker.getValue();

            if (selectedEvent == null || selectedService == null || selectedDate == null) {
                showAlert("Erreur", "Champs manquants", "Veuillez remplir tous les champs.");
                return;
            }

            // ✅ Convert LocalDate to java.sql.Date
            Date utilDate = Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

            // ✅ Check if reservation exists
            if (serviceReservation.exists(selectedEvent.getId(), selectedService.getId(), currentUser.getPrenom(), sqlDate)) {
                showAlert("Erreur", "Déjà réservé", "Vous avez déjà réservé ce service.");
                return;
            }

            // ✅ Store total price
            double totalPrice = selectedService.getPrix();

            reserve reservation = new reserve();
            reservation.setEvent(selectedEvent);
            reservation.setService(selectedService);
            reservation.setDateReservation(utilDate);
            reservation.setPrixTotal(totalPrice);
            reservation.setUtilisateur(currentUser.getPrenom());

            serviceReservation.ajouter(reservation);

            showAlert("Succès", "Réservation ajoutée avec succès!", "Le service a été ajouté à votre panier.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ajouter au panier", e.getMessage());
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void goToCart(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Cart.fxml"));
            Parent root = loader.load();

            // ✅ Retrieve controller instance
            CartController cartController = loader.getController();
            cartController.setCurrentUser(currentUser);

            // ✅ Switch scenes
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToStore() {
        loadFXML("/Product.fxml");
    }

    @FXML
    private void goToEvent() {
        loadFXML("/reservation.fxml");
    }

    @FXML
    private void goToDriveAndStay() {
        loadFXML("/hebergementclient.fxml");
    }

    @FXML
    private void goToInvite() {
        loadFXML("/menuOrganizer.fxml");
    }

    private void loadFXML(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToPreviousPage(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Product.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void confirmReservation() {
        try {
            Event selectedEvent = eventCombo.getValue();
            if (selectedEvent == null) {
                showAlert("Erreur", "Événement non sélectionné", "Veuillez sélectionner un événement.");
                return;
            }

            for (ReservationCartItem item : cartItems) {
                reserve reservation = new reserve();
                reservation.setEvent(selectedEvent);
                reservation.setService(new ServiceItem(item.getServiceId()));
                reservation.setDateReservation(item.getDate());
                reservation.setPrixTotal(item.getPrice());
                reservation.setUtilisateur(currentUser.getPrenom());

                serviceReservation.ajouter(reservation);
            }

            cartItems.clear();
            updateCartGrid();
            showAlert("Réservation", "Réservation réussie", "Votre réservation a été confirmée.");
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la réservation", e.getMessage());
            e.printStackTrace();
        }
    }
}
