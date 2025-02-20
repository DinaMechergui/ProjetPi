package controller;

import entities.Event;
import services.ServiceEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.geometry.Pos;
import javafx.scene.image.*;

import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class EventAdminController {

    @FXML
    private GridPane gridPane;

    @FXML
    private Button btnAdd, btnEdit, btnDelete;

    private ServiceEvent serviceEvent = new ServiceEvent();
    private Event selectedEvent;

    @FXML
    private void initialize() {
        loadEvents();
        btnAdd.setOnAction(event -> addEvent());
        btnEdit.setOnAction(event -> editEvent());
        btnDelete.setOnAction(event -> deleteEvent());
    }

    private void loadEvents() {
        try {
            List<Event> events = serviceEvent.afficher();
            gridPane.getChildren().clear();
            gridPane.setVgap(20);
            gridPane.setHgap(20);
            gridPane.setAlignment(Pos.CENTER);

            int row = 0;
            for (Event event : events) {
                addEventToGrid(event, row++);
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les événements", e.getMessage());
        }
    }

    private void addEventToGrid(Event event, int row) {
        HBox eventBox = new HBox(10);
        eventBox.setAlignment(Pos.CENTER_LEFT);
        eventBox.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-background-color: lightgray;");

        Image image = new Image(event.getImageUrl());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);

        Label nameLabel = new Label(event.getNom());
        Label dateLabel = new Label(event.getDate().toString());
        Label lieuLabel = new Label(event.getLieu());

        eventBox.getChildren().addAll(imageView, nameLabel, dateLabel, lieuLabel);
        gridPane.add(eventBox, 0, row);

        eventBox.setOnMouseClicked(e -> {
            selectedEvent = event;
            btnEdit.setDisable(false);
            btnDelete.setDisable(false);
        });
    }

    private void addEvent() {
        Stage addEventStage = new Stage();
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setAlignment(Pos.CENTER);

        TextField nameField = new TextField();
        DatePicker datePicker = new DatePicker();
        TextField lieuField = new TextField();
        TextField imageField = new TextField();
        ComboBox<String> statutCombo = new ComboBox<>();
        statutCombo.getItems().addAll("EN_PREPARATION", "CONFIRME" ,"ANNULE");

        formGrid.add(new Label("Nom de l'événement:"), 0, 0);
        formGrid.add(nameField, 1, 0);
        formGrid.add(new Label("Date de l'événement:"), 0, 1);
        formGrid.add(datePicker, 1, 1);
        formGrid.add(new Label("Lieu:"), 0, 2);
        formGrid.add(lieuField, 1, 2);
        formGrid.add(new Label("Image URL:"), 0, 3);
        formGrid.add(imageField, 1, 3);
        formGrid.add(new Label("Statut:"), 0, 4);
        formGrid.add(statutCombo, 1, 4);

        Button saveButton = new Button("Ajouter");
        formGrid.add(saveButton, 1, 5);

        saveButton.setOnAction(event -> {
            String name = nameField.getText();
            String lieu = lieuField.getText();
            String imageUrl = imageField.getText();
            String statut = statutCombo.getValue();
            LocalDate localDate = datePicker.getValue();

            if (name.isEmpty() || lieu.isEmpty() || imageUrl.isEmpty() || statut == null || localDate == null) {
                showAlert("Erreur", "Tous les champs doivent être remplis.", "Veuillez remplir tous les champs.");
            } else {
                Event newEvent = new Event();
                newEvent.setNom(name);
                newEvent.setLieu(lieu);
                newEvent.setImageUrl(imageUrl);
                newEvent.setStatut(Event.Statut.fromString(statut));
                newEvent.setDate(Date.valueOf(localDate));

                try {
                    serviceEvent.ajouter(newEvent);
                    addEventStage.close();
                    loadEvents();
                    showAlert("Succès", "Événement ajouté", "L'événement a été ajouté avec succès.");
                } catch (SQLException e) {
                    showAlert("Erreur", "Impossible d'ajouter l'événement", e.getMessage());
                }
            }
        });

        Scene addEventScene = new Scene(formGrid, 400, 300);
        addEventStage.setScene(addEventScene);
        addEventStage.setTitle("Ajouter un événement");
        addEventStage.show();
    }

    private void editEvent() {
        if (selectedEvent == null) {
            showAlert("Erreur", "Aucun événement sélectionné", "Veuillez sélectionner un événement à modifier.");
            return;
        }

        Stage editEventStage = new Stage();
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setAlignment(Pos.CENTER);

        TextField nameField = new TextField(selectedEvent.getNom());
        DatePicker datePicker = new DatePicker();
        TextField lieuField = new TextField(selectedEvent.getLieu());
        TextField imageField = new TextField(selectedEvent.getImageUrl());
        ComboBox<String> statutCombo = new ComboBox<>();
        statutCombo.getItems().addAll("Actif", "Inactif");
        statutCombo.setValue(selectedEvent.getStatut().toString());

        formGrid.add(new Label("Nom de l'événement:"), 0, 0);
        formGrid.add(nameField, 1, 0);
        formGrid.add(new Label("Date de l'événement:"), 0, 1);
        formGrid.add(datePicker, 1, 1);
        formGrid.add(new Label("Lieu:"), 0, 2);
        formGrid.add(lieuField, 1, 2);
        formGrid.add(new Label("Image URL:"), 0, 3);
        formGrid.add(imageField, 1, 3);
        formGrid.add(new Label("Statut:"), 0, 4);
        formGrid.add(statutCombo, 1, 4);

        Button saveButton = new Button("Modifier");
        formGrid.add(saveButton, 1, 5);

        saveButton.setOnAction(event -> {
            String name = nameField.getText();
            String lieu = lieuField.getText();
            String imageUrl = imageField.getText();
            String statut = statutCombo.getValue();
            LocalDate localDate = datePicker.getValue();

            if (name.isEmpty() || lieu.isEmpty() || imageUrl.isEmpty() || statut == null || localDate == null) {
                showAlert("Erreur", "Tous les champs doivent être remplis.", "Veuillez remplir tous les champs.");
            } else {
                selectedEvent.setNom(name);
                selectedEvent.setLieu(lieu);
                selectedEvent.setImageUrl(imageUrl);
                selectedEvent.setStatut(Event.Statut.fromString(statut));
                selectedEvent.setDate(Date.valueOf(localDate));

                try {
                    serviceEvent.modifier(selectedEvent);
                    editEventStage.close();
                    loadEvents();
                    showAlert("Succès", "Événement modifié", "L'événement a été modifié avec succès.");
                } catch (SQLException e) {
                    showAlert("Erreur", "Impossible de modifier l'événement", e.getMessage());
                }
            }
        });

        Scene editEventScene = new Scene(formGrid, 400, 300);
        editEventStage.setScene(editEventScene);
        editEventStage.setTitle("Modifier l'événement");
        editEventStage.show();
    }

    private void deleteEvent() {
        if (selectedEvent == null) {
            showAlert("Erreur", "Aucun événement sélectionné", "Veuillez sélectionner un événement.");
            return;
        }
        try {
            serviceEvent.supprimer(selectedEvent.getId());
            loadEvents();
            showAlert("Succès", "Événement supprimé", "L'événement a été supprimé.");
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur de suppression", e.getMessage());
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
