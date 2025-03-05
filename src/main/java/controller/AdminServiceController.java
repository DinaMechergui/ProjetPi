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
    private Button loginButton;
    @FXML
    private Button btnAdd, btnEdit, btnDelete;

    private ServiceService serviceService = new ServiceService();
    private ServiceItem selectedService;

    public AdminServiceController() throws SQLException {
    }

    @FXML
    private void initialize() {
        loadServices();  // Charge la liste des services lors de l'initialisation de la vue
        btnAdd.setOnAction(event -> addService());  // Action du bouton "Ajouter" qui appelle la méthode addService
        btnEdit.setOnAction(event -> editService());  // Action du bouton "Modifier" qui appelle la méthode editService
        btnDelete.setOnAction(event -> deleteService());  // Action du bouton "Supprimer" qui appelle la méthode deleteService
    }

    private void loadServices() {
        try {
            List<ServiceItem> services = serviceService.afficher();  // Récupère la liste des services depuis la base de données
            gridPane.getChildren().clear();  // Efface les éléments précédents dans la grille
            gridPane.setVgap(20);  // Définit l'espacement vertical entre les éléments de la grille
            gridPane.setHgap(20);  // Définit l'espacement horizontal entre les éléments de la grille
            gridPane.setAlignment(Pos.CENTER);  // Centre les éléments dans la grille

            int row = 0;
            for (ServiceItem service : services) {
                addServiceToGrid(service, row++);  // Ajoute chaque service à la grille
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les services", e.getMessage());  // Affiche une alerte en cas d'erreur
        }
    }

    private void addServiceToGrid(ServiceItem service, int row) {
        HBox serviceBox = new HBox(10);  // Crée une boîte horizontale pour contenir les informations d'un service
        serviceBox.setAlignment(Pos.CENTER_LEFT);  // Aligne les éléments à gauche dans la boîte
        serviceBox.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-background-color: lightgray;");  // Style CSS pour la boîte

        Image image = new Image(service.getImageUrl());  // Crée une image à partir de l'URL fournie par le service
        ImageView imageView = new ImageView(image);  // Crée une vue d'image
        imageView.setFitWidth(50);  // Définit la largeur de l'image
        imageView.setFitHeight(50);  // Définit la hauteur de l'image

        Label nameLabel = new Label(service.getNom());  // Crée un label pour le nom du service
        Label descriptionLabel = new Label(service.getDescription());  // Crée un label pour la description du service
        Label priceLabel = new Label(String.valueOf(service.getPrix()));  // Crée un label pour le prix du service

        serviceBox.getChildren().addAll(imageView, nameLabel, descriptionLabel, priceLabel);  // Ajoute les éléments à la boîte
        gridPane.add(serviceBox, 0, row);  // Ajoute la boîte à la grille à la ligne correspondante

        serviceBox.setOnMouseClicked(e -> {
            selectedService = service;  // Définit le service sélectionné
            btnEdit.setDisable(false);  // Active le bouton "Modifier"
            btnDelete.setDisable(false);  // Active le bouton "Supprimer"
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
        Alert alert = new Alert(Alert.AlertType.INFORMATION);  // Crée une alerte d'information
        alert.setTitle(title);  // Définit le titre de l'alerte
        alert.setHeaderText(header);  // Définit le texte d'en-tête
        alert.setContentText(content);  // Définit le contenu de l'alerte
        alert.showAndWait();  // Affiche l'alerte et attend que l'utilisateur la ferme
    }
    public void goToReservation(ActionEvent event) {
        try {
            // Charger le fichier FXML de la page des produits
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ServiceAdmin.fxml"));
            Parent root = loader.load();

            // Récupérer la scène actuelle
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Changer la scène pour afficher la page des produits
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de la page des produits.");
        }
    }

    public void goToHotel(ActionEvent event) {
        try {
            // Charger le fichier FXML de la page des produits
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherHebergement.fxml"));
            Parent root = loader.load();

            // Récupérer la scène actuelle
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Changer la scène pour afficher la page des produits
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de la page des produits.");
        }
    }

    public void goTStore(ActionEvent actionEvent) {
    }

    public void goToProduit(ActionEvent event) {
        try {
            // Charger le fichier FXML de la page des produits
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminDashboardProduit.fxml"));
            Parent root = loader.load();

            // Récupérer la scène actuelle
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

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
