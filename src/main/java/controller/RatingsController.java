package controller;

import entities.ServiceItem;
import entities.reserve;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import services.ServiceReservation;
import services.ServiceService;
import tn.esprit.entities.Invite;
import tn.esprit.tacheuser.models.User;
import tn.esprit.tacheuser.utils.SessionManager; // Import pour récupérer l'utilisateur

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static tn.esprit.tacheuser.utils.SessionManager.currentUser;

public class RatingsController {

    @FXML
    private ListView<ServiceItem> servicesList;

    private ServiceService serviceService;

    @FXML
    public void initialize() {
        System.out.println("⚡ Initialisation du contrôleur RatingsController");

        if (servicesList == null) {
            System.err.println("❌ ERREUR CRITIQUE: servicesList est NULL!");
            return;
        }

        System.out.println("✅ ListView trouvée, configuration...");

        // Configuration de base de la ListView
        servicesList.setStyle("-fx-background-color: white;");
        servicesList.setFixedCellSize(150);
        servicesList.setPrefHeight(400);
        servicesList.setMinHeight(400);
        servicesList.setMaxWidth(Double.MAX_VALUE);

        // Initialisation du service
        serviceService = new ServiceService();

        // Configuration de la cellule
        setupServiceListView();

        // Chargement des données
        loadReservedServices();

        // Force le rafraîchissement après un court délai
        javafx.application.Platform.runLater(() -> {
            servicesList.refresh();
            System.out.println("✅ Rafraîchissement forcé de la ListView");
        });
    }

    private void setupServiceListView() {
        System.out.println("⚡ Configuration du CellFactory pour la ListView");

        servicesList.setCellFactory(listView -> new ListCell<ServiceItem>() {
            private final HBox container = new HBox(20);
            private final ImageView imageView = new ImageView();
            private final VBox contentBox = new VBox(10);
            private final VBox serviceInfo = new VBox(5);
            private final HBox buttonsBox = new HBox(10);
            private final Label nameLabel = new Label();
            private final Label descriptionLabel = new Label();
            private final Label priceLabel = new Label();
            private final Button likeButton = new Button("👍 J'aime");
            private final Button dislikeButton = new Button("👎 Je n'aime pas");

            {
                // Configuration du conteneur principal
                container.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");
                container.setAlignment(Pos.CENTER_LEFT);
                container.setPrefWidth(900);
                container.setMaxWidth(900);

                // Configuration de l'image
                imageView.setFitHeight(120);
                imageView.setFitWidth(120);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);

                // Style des labels
                nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
                descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d; -fx-wrap-text: true;");
                priceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

                // Style des boutons
                String buttonStyle = "-fx-background-radius: 20; -fx-padding: 8 16; -fx-font-size: 14px; " +
                        "-fx-cursor: hand; -fx-background-insets: 0; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 1);";
                likeButton.setStyle(buttonStyle + "-fx-background-color: #2ecc71; -fx-text-fill: white;");
                dislikeButton.setStyle(buttonStyle + "-fx-background-color: #e74c3c; -fx-text-fill: white;");

                // Hover effects pour les boutons
                likeButton.setOnMouseEntered(e -> likeButton.setStyle(buttonStyle + "-fx-background-color: #27ae60; -fx-text-fill: white;"));
                likeButton.setOnMouseExited(e -> likeButton.setStyle(buttonStyle + "-fx-background-color: #2ecc71; -fx-text-fill: white;"));
                dislikeButton.setOnMouseEntered(e -> dislikeButton.setStyle(buttonStyle + "-fx-background-color: #c0392b; -fx-text-fill: white;"));
                dislikeButton.setOnMouseExited(e -> dislikeButton.setStyle(buttonStyle + "-fx-background-color: #e74c3c; -fx-text-fill: white;"));

                // Organisation des éléments
                serviceInfo.getChildren().addAll(nameLabel, descriptionLabel, priceLabel);
                buttonsBox.getChildren().addAll(likeButton, dislikeButton);
                buttonsBox.setAlignment(Pos.CENTER_LEFT);

                contentBox.getChildren().addAll(serviceInfo, buttonsBox);
                contentBox.setAlignment(Pos.CENTER_LEFT);
                HBox.setHgrow(contentBox, Priority.ALWAYS);

                // Ajout au conteneur principal
                container.getChildren().addAll(imageView, contentBox);
            }

            @Override
            protected void updateItem(ServiceItem item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    System.out.println("➖ Cellule vide");
                } else {
                    System.out.println("✨ Mise à jour cellule pour: " + item.getNom());

                    nameLabel.setText(item.getNom());
                    descriptionLabel.setText(item.getDescription() != null ? item.getDescription() : "Aucune description disponible");
                    priceLabel.setText(String.format("%.2f DT", item.getPrix()));

                    // Gestion de l'image depuis la base de données
                    try {
                        String imageUrl = item.getImageUrl();
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            if (imageUrl.startsWith("data:image")) {
                                // Pour les images en base64
                                String base64Image = imageUrl.split(",")[1];
                                byte[] imageData = java.util.Base64.getDecoder().decode(base64Image);
                                Image image = new Image(new java.io.ByteArrayInputStream(imageData));
                                imageView.setImage(image);
                            } else {
                                // Pour les URLs normales
                                Image image = new Image(imageUrl, true);
                                image.errorProperty().addListener((obs, oldValue, newValue) -> {
                                    if (newValue) {
                                        System.err.println("❌ Erreur de chargement de l'image pour: " + item.getNom());
                                        // En cas d'erreur, on peut mettre une image par défaut ou laisser vide
                                        imageView.setImage(null);
                                    }
                                });
                                imageView.setImage(image);
                            }
                        } else {
                            imageView.setImage(null);
                        }
                    } catch (Exception e) {
                        System.err.println("❌ Erreur lors du chargement de l'image pour: " + item.getNom());
                        e.printStackTrace();
                        imageView.setImage(null);
                    }

                    // Configuration des actions des boutons
                    likeButton.setOnAction(e -> handleFeedback(item, true));
                    dislikeButton.setOnAction(e -> handleFeedback(item, false));

                    setGraphic(container);
                }
            }
        });

        // Désactiver la sélection
        servicesList.setSelectionModel(null);

        System.out.println("✅ CellFactory configuré avec succès");
    }

    private void loadReservedServices() {
        try {
            String currentUserPrenom = SessionManager.getUser().getPrenom();
            System.out.println("⚡ Chargement des services pour: " + currentUserPrenom);

            List<ServiceItem> reservedServices = serviceService.getReservedServicesByPrenom(currentUserPrenom);

            if (reservedServices != null && !reservedServices.isEmpty()) {
                System.out.println("✅ Services trouvés: " + reservedServices.size());
                ObservableList<ServiceItem> observableServices = FXCollections.observableArrayList(reservedServices);

                Platform.runLater(() -> {
                    setReservedServices(observableServices);

                    // Afficher un message de succès
                    showAlert("Succès", "Services chargés",
                            "Nombre de services chargés: " + reservedServices.size());

                    // Afficher les détails de chaque service
                    for (ServiceItem service : reservedServices) {
                        System.out.println("📦 Service: " + service.getNom() + " - " + service.getPrix() + " DT");
                    }
                });
            } else {
                System.out.println("ℹ️ Aucun service trouvé");
                Platform.runLater(() -> {
                    showNoServicesMessage();
                    showAlert("Information", "Aucun service",
                            "Vous n'avez pas encore réservé de services à évaluer.");
                });
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur de chargement: " + e.getMessage());
            e.printStackTrace();
            Platform.runLater(() -> {
                showErrorMessage();
                showAlert("Erreur", "Erreur de chargement",
                        "Une erreur est survenue lors du chargement des services: " + e.getMessage());
            });
        }
    }

    private void showNoServicesMessage() {
        VBox messageBox = new VBox(10);
        messageBox.setAlignment(Pos.CENTER);

        Label messageLabel = new Label("Aucun service réservé");
        messageLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label subLabel = new Label("Réservez un service pour pouvoir l'évaluer");
        subLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

        messageBox.getChildren().addAll(messageLabel, subLabel);
        servicesList.setPlaceholder(messageBox);
    }

    private void showErrorMessage() {
        VBox errorBox = new VBox(10);
        errorBox.setAlignment(Pos.CENTER);

        Label errorLabel = new Label("Erreur de chargement");
        errorLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #f44336;");

        errorBox.getChildren().add(errorLabel);
        servicesList.setPlaceholder(errorBox);
    }

    private void handleFeedback(ServiceItem item, boolean like) {
        try {
            String currentUserPrenom = SessionManager.getUser().getPrenom();
            serviceService.addServiceFeedbackByPrenom(item.getId(), currentUserPrenom, like ? "like" : "dislike");

            showAlert("Succès", "Avis enregistré",
                    "Votre avis pour le service '" + item.getNom() + "' a été enregistré avec succès!");

            // Recharger les services pour mettre à jour l'affichage
            loadReservedServices();
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert("Erreur", "Erreur d'enregistrement",
                    "Une erreur est survenue lors de l'enregistrement de votre avis pour le service '" + item.getNom() + "'.");
        }
    }

    @FXML
    private void goToReservation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/reservation.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) servicesList.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ListView<ServiceItem> getReservedServicesList() {
        return servicesList;
    }

    public void setReservedServicesList(ListView<ServiceItem> reservedServicesList) {
        this.servicesList = reservedServicesList;
    }

    private void showAlert(String title, String header, String content) {
        // Exécuter sur le thread JavaFX
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    public void setReservedServices(ObservableList<ServiceItem> services) {
        if (servicesList != null) {
            Platform.runLater(() -> {
                try {
                    servicesList.setItems(null); // Clear the items first
                    servicesList.setItems(services); // Set new items
                    servicesList.refresh();  // Force refresh
                    System.out.println("✅ Services mis à jour dans la ListView: " + (services != null ? services.size() : 0) + " services");
                    if (services != null) {
                        for (ServiceItem service : services) {
                            System.out.println("📦 Service: " + service.getNom() + " - " + service.getPrix() + " DT");
                        }
                    }
                } catch (Exception e) {
                    System.err.println("❌ Erreur lors de la mise à jour des services: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } else {
            System.err.println("❌ ERREUR: servicesList est NULL lors de la mise à jour!");
            showAlert("Erreur", "Erreur d'affichage",
                    "Impossible d'afficher les services. La liste est invalide.");
        }
    }

}