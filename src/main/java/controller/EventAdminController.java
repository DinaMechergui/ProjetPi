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
    private GridPane gridPane;  // Le conteneur principal pour afficher les événements dans une grille

    @FXML
    private Button btnAdd, btnEdit, btnDelete;  // Les boutons pour ajouter, modifier et supprimer les événements

    private ServiceEvent serviceEvent = new ServiceEvent();  // Service pour gérer les opérations sur les événements
    private Event selectedEvent;  // L'événement sélectionné par l'utilisateur

    // Initialisation du contrôleur
    @FXML
    private void initialize() {
        loadEvents();  // Charger et afficher la liste des événements
        btnAdd.setOnAction(event -> addEvent());  // Ajouter un événement
        btnEdit.setOnAction(event -> editEvent());  // Modifier un événement
        btnDelete.setOnAction(event -> deleteEvent());  // Supprimer un événement
    }

    // Charger et afficher les événements dans la grille
    private void loadEvents() {
        try {
            List<Event> events = serviceEvent.afficher();  // Récupérer la liste des événements depuis le service
            gridPane.getChildren().clear();  // Effacer les événements existants de la grille
            gridPane.setVgap(20);  // Espacement vertical
            gridPane.setHgap(20);  // Espacement horizontal
            gridPane.setAlignment(Pos.CENTER);  // Centrer le contenu dans la grille

            int row = 0;
            // Ajouter chaque événement à la grille
            for (Event event : events) {
                addEventToGrid(event, row++);  // Ajouter un événement à une ligne spécifique
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les événements", e.getMessage());  // Gérer les erreurs de chargement des événements
        }
    }

    // Ajouter un événement à la grille à une ligne donnée
    private void addEventToGrid(Event event, int row) {
        HBox eventBox = new HBox(10);  // Conteneur horizontal pour chaque événement
        eventBox.setAlignment(Pos.CENTER_LEFT);  // Aligner le contenu à gauche
        eventBox.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-background-color: lightgray;");  // Appliquer un style CSS à chaque événement

        // Ajouter l'image de l'événement
        Image image = new Image(event.getImageUrl());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);

        // Ajouter des informations textuelles de l'événement (nom, date, lieu)
        Label nameLabel = new Label(event.getNom());
        Label dateLabel = new Label(event.getDate().toString());
        Label lieuLabel = new Label(event.getLieu());

        // Ajouter l'image et les labels à l'eventBox
        eventBox.getChildren().addAll(imageView, nameLabel, dateLabel, lieuLabel);
        gridPane.add(eventBox, 0, row);  // Ajouter l'eventBox à la grille dans la ligne spécifiée

        // Ajouter un événement au clic sur l'événement
        eventBox.setOnMouseClicked(e -> {
            selectedEvent = event;  // L'événement sélectionné devient celui sur lequel on a cliqué
            btnEdit.setDisable(false);  // Activer le bouton Modifier
            btnDelete.setDisable(false);  // Activer le bouton Supprimer
        });
    }

    // Ajouter un nouvel événement
    private void addEvent() {
        Stage addEventStage = new Stage();  // Nouvelle fenêtre pour ajouter un événement
        GridPane formGrid = new GridPane();  // Grille pour les champs de formulaire
        formGrid.setHgap(10);  // Espacement horizontal
        formGrid.setVgap(10);  // Espacement vertical
        formGrid.setAlignment(Pos.CENTER);  // Centrer le contenu dans la grille

        // Définir les champs du formulaire
        TextField nameField = new TextField();
        DatePicker datePicker = new DatePicker();
        TextField lieuField = new TextField();
        TextField imageField = new TextField();
        ComboBox<String> statutCombo = new ComboBox<>();
        statutCombo.getItems().addAll("EN_PREPARATION", "CONFIRME" ,"ANNULE");  // Ajouter des options de statut

        // Ajouter les champs et les labels à la grille
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

        // Bouton pour ajouter un événement
        Button saveButton = new Button("Ajouter");
        formGrid.add(saveButton, 1, 5);

        // Gérer l'action du bouton "Ajouter"
        saveButton.setOnAction(event -> {
            String name = nameField.getText();
            String lieu = lieuField.getText();
            String imageUrl = imageField.getText();
            String statut = statutCombo.getValue();
            LocalDate localDate = datePicker.getValue();

            // Vérification des champs vides
            if (name.isEmpty() || lieu.isEmpty() || imageUrl.isEmpty() || statut == null || localDate == null) {
                showAlert("Erreur", "Tous les champs doivent être remplis.", "Veuillez remplir tous les champs.");
            } else {
                // Créer un nouvel événement et le sauvegarder
                Event newEvent = new Event();
                newEvent.setNom(name);
                newEvent.setLieu(lieu);
                newEvent.setImageUrl(imageUrl);
                newEvent.setStatut(Event.Statut.fromString(statut));
                newEvent.setDate(Date.valueOf(localDate));

                try {
                    serviceEvent.ajouter(newEvent);  // Ajouter l'événement via le service
                    addEventStage.close();  // Fermer la fenêtre
                    loadEvents();  // Recharger la liste des événements
                    showAlert("Succès", "Événement ajouté", "L'événement a été ajouté avec succès.");
                } catch (SQLException e) {
                    showAlert("Erreur", "Impossible d'ajouter l'événement", e.getMessage());
                }
            }
        });

        // Configurer la scène et afficher la fenêtre
        Scene addEventScene = new Scene(formGrid, 400, 300);
        addEventStage.setScene(addEventScene);
        addEventStage.setTitle("Ajouter un événement");
        addEventStage.show();
    }

    // Modifier un événement sélectionné
    private void editEvent() {
        if (selectedEvent == null) {
            showAlert("Erreur", "Aucun événement sélectionné", "Veuillez sélectionner un événement à modifier.");
            return;
        }

        // Ouvrir une nouvelle fenêtre pour modifier l'événement
        Stage editEventStage = new Stage();
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setAlignment(Pos.CENTER);

        // Remplir les champs avec les valeurs actuelles de l'événement sélectionné
        TextField nameField = new TextField(selectedEvent.getNom());
        DatePicker datePicker = new DatePicker();
        TextField lieuField = new TextField(selectedEvent.getLieu());
        TextField imageField = new TextField(selectedEvent.getImageUrl());
        ComboBox<String> statutCombo = new ComboBox<>();
        statutCombo.setValue(selectedEvent.getStatut().toString());

        // Ajouter les champs et les labels à la grille
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

        // Bouton pour sauvegarder les modifications
        Button saveButton = new Button("Modifier");
        formGrid.add(saveButton, 1, 5);

        // Gérer l'action du bouton "Modifier"
        saveButton.setOnAction(event -> {
            String name = nameField.getText();
            String lieu = lieuField.getText();
            String imageUrl = imageField.getText();
            String statut = statutCombo.getValue();
            LocalDate localDate = datePicker.getValue();

            // Vérification des champs vides
            if (name.isEmpty() || lieu.isEmpty() || imageUrl.isEmpty() || statut == null || localDate == null) {
                showAlert("Erreur", "Tous les champs doivent être remplis.", "Veuillez remplir tous les champs.");
            } else {
                // Mettre à jour l'événement et le sauvegarder
                selectedEvent.setNom(name);
                selectedEvent.setLieu(lieu);
                selectedEvent.setImageUrl(imageUrl);
                selectedEvent.setStatut(Event.Statut.fromString(statut));
                selectedEvent.setDate(Date.valueOf(localDate));

                try {
                    serviceEvent.modifier(selectedEvent);  // Modifier l'événement via le service
                    editEventStage.close();  // Fermer la fenêtre
                    loadEvents();  // Recharger la liste des événements
                    showAlert("Succès", "Événement modifié", "L'événement a été modifié avec succès.");
                } catch (SQLException e) {
                    showAlert("Erreur", "Impossible de modifier l'événement", e.getMessage());
                }
            }
        });

        // Configurer la scène et afficher la fenêtre
        Scene editEventScene = new Scene(formGrid, 400, 300);
        editEventStage.setScene(editEventScene);
        editEventStage.setTitle("Modifier l'événement");
        editEventStage.show();
    }

    // Supprimer l'événement sélectionné
    private void deleteEvent() {
        if (selectedEvent == null) {
            showAlert("Erreur", "Aucun événement sélectionné", "Veuillez sélectionner un événement.");
            return;
        }
        try {
            serviceEvent.supprimer(selectedEvent.getId());  // Supprimer l'événement via le service
            loadEvents();  // Recharger la liste des événements
            showAlert("Succès", "Événement supprimé", "L'événement a été supprimé.");
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur de suppression", e.getMessage());
        }
    }

    // Afficher une alerte (message d'erreur ou de succès)
    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
