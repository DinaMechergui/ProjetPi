package controller;

import entities.ServiceItem;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import services.ServiceService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.geometry.Pos;
import javafx.scene.image.*;

import java.io.IOException;
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
        btnEdit.setDisable(true);
        btnDelete.setDisable(true);
        btnAdd.setOnAction(event -> addService());
        btnEdit.setOnAction(event -> editService());
        btnDelete.setOnAction(event -> deleteService());
    }

    private void loadServices() {
        try {
            List<ServiceItem> services = serviceService.afficher();
            gridPane.getChildren().clear();
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
        HBox serviceBox = new HBox(50);
        serviceBox.setAlignment(Pos.BASELINE_RIGHT);
        serviceBox.setStyle("-fx-border-color: black;" +
                "-fx-border-radius: 10;" +
                "-fx-background-color: #ffffff;" +
                "-fx-padding: 9;" +
                "-fx-border-width: 1;");

        Image image;
        try {
            image = new Image(service.getImageUrl(), 200, 100, true, false);
        } catch (Exception e) {
            image = new Image("/images/placeholder.png", 200, 100, true, false);
        }

        ImageView imageView = new ImageView(image);
        imageView.setSmooth(false);
        imageView.setFitWidth(200);
        imageView.setFitHeight(100);
        imageView.setStyle("-fx-border-color: black; -fx-border-width: 0;");

        VBox textContainer = new VBox(30);
        textContainer.setAlignment(Pos.BASELINE_CENTER);

        Label nameLabel = new Label("Nom: " + service.getNom());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        Label descriptionLabel = new Label("Description: " + service.getDescription());
        descriptionLabel.setStyle("-fx-text-fill: gray;");

        Label priceLabel = new Label("Prix: " + service.getPrix() + " dt");
        priceLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");

        textContainer.getChildren().addAll(nameLabel, descriptionLabel, priceLabel);
        serviceBox.getChildren().addAll(imageView, textContainer);

        gridPane.add(serviceBox,0, row);

        serviceBox.setOnMouseClicked(e -> {
            selectedService = service;
            btnEdit.setDisable(false);
            btnDelete.setDisable(false);
        });
    }

    private void addService() {
        Stage addServiceStage = new Stage();  // Crée une nouvelle fenêtre pour ajouter un service
        GridPane formGrid = new GridPane();  // Crée une grille pour organiser le formulaire
        formGrid.setHgap(10);  // Définit l'espacement horizontal dans la grille
        formGrid.setVgap(10);  // Définit l'espacement vertical dans la grille
        formGrid.setAlignment(Pos.CENTER);  // Centre les éléments dans la grille

        // Crée les champs de texte pour saisir les informations du service
        TextField nameField = new TextField();
        TextField descriptionField = new TextField();
        TextField priceField = new TextField();
        TextField imageField = new TextField();

        // Ajoute les labels et champs de saisie dans la grille
        formGrid.add(new Label("Nom du service:"), 0, 0);
        formGrid.add(nameField, 1, 0);
        formGrid.add(new Label("Description:"), 0, 1);
        formGrid.add(descriptionField, 1, 1);
        formGrid.add(new Label("Prix:"), 0, 2);
        formGrid.add(priceField, 1, 2);
        formGrid.add(new Label("Image URL:"), 0, 3);
        formGrid.add(imageField, 1, 3);

        Button saveButton = new Button("Ajouter");  // Crée un bouton "Ajouter"
        formGrid.add(saveButton, 1, 4);

        saveButton.setOnAction(event -> {
            String name = nameField.getText();
            String description = descriptionField.getText();
            String priceStr = priceField.getText();
            String imageUrl = imageField.getText();

            // Vérifie si tous les champs sont remplis
            if (name.isEmpty() || description.isEmpty() || priceStr.isEmpty() || imageUrl.isEmpty()) {
                showAlert("Erreur", "Tous les champs doivent être remplis.", "Veuillez remplir tous les champs.");
            } else {
                double price = Double.parseDouble(priceStr);  // Convertit le prix en nombre

                // Crée un nouvel objet ServiceItem avec les données saisies
                ServiceItem newService = new ServiceItem();
                newService.setNom(name);
                newService.setDescription(description);
                newService.setPrix(price);
                newService.setImageUrl(imageUrl);

                try {
                    serviceService.ajouter(newService);  // Ajoute le service dans la base de données
                    addServiceStage.close();  // Ferme la fenêtre d'ajout
                    loadServices();  // Recharge la liste des services
                    showAlert("Succès", "Service ajouté", "Le service a été ajouté avec succès.");
                } catch (SQLException e) {
                    showAlert("Erreur", "Impossible d'ajouter le service", e.getMessage());
                }
            }
        });

        // Affiche la fenêtre d'ajout
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

        Stage editServiceStage = new Stage();  // Crée une nouvelle fenêtre pour modifier un service
        GridPane formGrid = new GridPane();  // Crée une grille pour organiser le formulaire
        formGrid.setHgap(10);  // Définit l'espacement horizontal dans la grille
        formGrid.setVgap(10);  // Définit l'espacement vertical dans la grille
        formGrid.setAlignment(Pos.CENTER);  // Centre les éléments dans la grille

        // Prend les valeurs du service sélectionné pour les préremplir dans les champs
        TextField nameField = new TextField(selectedService.getNom());
        TextField descriptionField = new TextField(selectedService.getDescription());
        TextField priceField = new TextField(String.valueOf(selectedService.getPrix()));
        TextField imageField = new TextField(selectedService.getImageUrl());

        // Ajoute les labels et champs de saisie dans la grille
        formGrid.add(new Label("Nom du service:"), 0, 0);
        formGrid.add(nameField, 1, 0);
        formGrid.add(new Label("Description:"), 0, 1);
        formGrid.add(descriptionField, 1, 1);
        formGrid.add(new Label("Prix:"), 0, 2);
        formGrid.add(priceField, 1, 2);
        formGrid.add(new Label("Image URL:"), 0, 3);
        formGrid.add(imageField, 1, 3);

        Button saveButton = new Button("Modifier");  // Crée un bouton "Modifier"
        formGrid.add(saveButton, 1, 4);

        saveButton.setOnAction(event -> {
            String name = nameField.getText();
            String description = descriptionField.getText();
            String priceStr = priceField.getText();
            String imageUrl = imageField.getText();

            // Vérifie si tous les champs sont remplis
            if (name.isEmpty() || description.isEmpty() || priceStr.isEmpty() || imageUrl.isEmpty()) {
                showAlert("Erreur", "Tous les champs doivent être remplis.", "Veuillez remplir tous les champs.");
            } else {
                double price = Double.parseDouble(priceStr);  // Convertit le prix en nombre

                // Met à jour les informations du service sélectionné
                selectedService.setNom(name);
                selectedService.setDescription(description);
                selectedService.setPrix(price);
                selectedService.setImageUrl(imageUrl);

                try {
                    serviceService.modifier(selectedService);  // Modifie le service dans la base de données
                    editServiceStage.close();  // Ferme la fenêtre de modification
                    loadServices();  // Recharge la liste des services
                    showAlert("Succès", "Service modifié", "Le service a été modifié avec succès.");
                } catch (SQLException e) {
                    showAlert("Erreur", "Impossible de modifier le service", e.getMessage());
                }
            }
        });

        // Affiche la fenêtre de modification
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
            serviceService.supprimer(selectedService.getId());  // Supprime le service de la base de données
            loadServices();  // Recharge la liste des services
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

    public void goToReservation(ActionEvent event) {
        navigateToPage(event, "/ServiceAdmin.fxml");
    }

    public void goToHotel(ActionEvent event) {
        navigateToPage(event, "/AfficherHebergement.fxml");
    }

    public void goToProduit(ActionEvent event) {
        navigateToPage(event, "/AdminDashboardProduit.fxml");
    }

    private void navigateToPage(ActionEvent event, String resourcePath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resourcePath));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de la page: " + resourcePath);
        }
    }
}