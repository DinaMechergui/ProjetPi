package controller;

import entities.ServiceItem;
import services.ServiceService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.geometry.Pos;
import javafx.scene.image.*;

import java.sql.SQLException;
import java.util.List;

public class AdminServiceController {

    @FXML
    private GridPane gridPane;

    @FXML
    private Button btnAdd, btnEdit, btnDelete;

    private ServiceService serviceService = new ServiceService();
    private ServiceItem selectedService;

    @FXML
    private void initialize() {
        loadServices();
        btnAdd.setOnAction(event -> addService());
        btnEdit.setOnAction(event -> editService());
        btnDelete.setOnAction(event -> deleteService());
    }

    private void loadServices() {
        try {
            List<ServiceItem> services = serviceService.afficher();
            gridPane.getChildren().clear();
            gridPane.setVgap(20);
            gridPane.setHgap(20);
            gridPane.setAlignment(Pos.CENTER);

            int row = 0;
            for (ServiceItem service : services) {
                addServiceToGrid(service, row++);
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les services", e.getMessage());
        }
    }

    private void addServiceToGrid(ServiceItem service, int row) {
        HBox serviceBox = new HBox(10);
        serviceBox.setAlignment(Pos.CENTER_LEFT);
        serviceBox.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-background-color: lightgray;");

        Image image = new Image(service.getImageUrl());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);

        Label nameLabel = new Label(service.getNom());
        Label descriptionLabel = new Label(service.getDescription());
        Label priceLabel = new Label(String.valueOf(service.getPrix()));

        serviceBox.getChildren().addAll(imageView, nameLabel, descriptionLabel, priceLabel);
        gridPane.add(serviceBox, 0, row);

        serviceBox.setOnMouseClicked(e -> {
            selectedService = service;
            btnEdit.setDisable(false);
            btnDelete.setDisable(false);
        });
    }

    private void addService() {
        Stage addServiceStage = new Stage();
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setAlignment(Pos.CENTER);

        TextField nameField = new TextField();
        TextField descriptionField = new TextField();
        TextField priceField = new TextField();
        TextField imageField = new TextField();

        formGrid.add(new Label("Nom du service:"), 0, 0);
        formGrid.add(nameField, 1, 0);
        formGrid.add(new Label("Description:"), 0, 1);
        formGrid.add(descriptionField, 1, 1);
        formGrid.add(new Label("Prix:"), 0, 2);
        formGrid.add(priceField, 1, 2);
        formGrid.add(new Label("Image URL:"), 0, 3);
        formGrid.add(imageField, 1, 3);

        Button saveButton = new Button("Ajouter");
        formGrid.add(saveButton, 1, 4);

        saveButton.setOnAction(event -> {
            String name = nameField.getText();
            String description = descriptionField.getText();
            String priceStr = priceField.getText();
            String imageUrl = imageField.getText();

            if (name.isEmpty() || description.isEmpty() || priceStr.isEmpty() || imageUrl.isEmpty()) {
                showAlert("Erreur", "Tous les champs doivent être remplis.", "Veuillez remplir tous les champs.");
            } else {
                double price = Double.parseDouble(priceStr);

                ServiceItem newService = new ServiceItem();
                newService.setNom(name);
                newService.setDescription(description);
                newService.setPrix(price);
                newService.setImageUrl(imageUrl);

                try {
                    serviceService.ajouter(newService);
                    addServiceStage.close();
                    loadServices();
                    showAlert("Succès", "Service ajouté", "Le service a été ajouté avec succès.");
                } catch (SQLException e) {
                    showAlert("Erreur", "Impossible d'ajouter le service", e.getMessage());
                }
            }
        });

        Scene addServiceScene = new Scene(formGrid, 400, 300);
        addServiceStage.setScene(addServiceScene);
        addServiceStage.setTitle("Ajouter un service");
        addServiceStage.show();
    }

    private void editService() {
        if (selectedService == null) {
            showAlert("Erreur", "Aucun service sélectionné", "Veuillez sélectionner un service à modifier.");
            return;
        }

        Stage editServiceStage = new Stage();
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setAlignment(Pos.CENTER);

        TextField nameField = new TextField(selectedService.getNom());
        TextField descriptionField = new TextField(selectedService.getDescription());
        TextField priceField = new TextField(String.valueOf(selectedService.getPrix()));
        TextField imageField = new TextField(selectedService.getImageUrl());

        formGrid.add(new Label("Nom du service:"), 0, 0);
        formGrid.add(nameField, 1, 0);
        formGrid.add(new Label("Description:"), 0, 1);
        formGrid.add(descriptionField, 1, 1);
        formGrid.add(new Label("Prix:"), 0, 2);
        formGrid.add(priceField, 1, 2);
        formGrid.add(new Label("Image URL:"), 0, 3);
        formGrid.add(imageField, 1, 3);

        Button saveButton = new Button("Modifier");
        formGrid.add(saveButton, 1, 4);

        saveButton.setOnAction(event -> {
            String name = nameField.getText();
            String description = descriptionField.getText();
            String priceStr = priceField.getText();
            String imageUrl = imageField.getText();

            if (name.isEmpty() || description.isEmpty() || priceStr.isEmpty() || imageUrl.isEmpty()) {
                showAlert("Erreur", "Tous les champs doivent être remplis.", "Veuillez remplir tous les champs.");
            } else {
                double price = Double.parseDouble(priceStr);

                selectedService.setNom(name);
                selectedService.setDescription(description);
                selectedService.setPrix(price);
                selectedService.setImageUrl(imageUrl);

                try {
                    serviceService.modifier(selectedService);
                    editServiceStage.close();
                    loadServices();
                    showAlert("Succès", "Service modifié", "Le service a été modifié avec succès.");
                } catch (SQLException e) {
                    showAlert("Erreur", "Impossible de modifier le service", e.getMessage());
                }
            }
        });

        Scene editServiceScene = new Scene(formGrid, 400, 300);
        editServiceStage.setScene(editServiceScene);
        editServiceStage.setTitle("Modifier le service");
        editServiceStage.show();
    }

    private void deleteService() {
        if (selectedService == null) {
            showAlert("Erreur", "Aucun service sélectionné", "Veuillez sélectionner un service.");
            return;
        }
        try {
            serviceService.supprimer(selectedService.getId());
            loadServices();
            showAlert("Succès", "Service supprimé", "Le service a été supprimé.");
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
