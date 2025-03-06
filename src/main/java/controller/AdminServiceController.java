package controller;

import entities.ServiceItem;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.stage.FileChooser;
import java.io.File;
import java.nio.file.Files;
import java.util.Base64;
import java.io.ByteArrayInputStream;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javafx.scene.control.Alert.AlertType;

public class AdminServiceController {

    @FXML
    private FlowPane servicesContainer;
    @FXML
    private Button btnAdd, btnEdit, btnDelete;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> filterComboBox;
    @FXML
    private Label activeServicesCount;
    @FXML
    private Label totalReservationsCount;
    @FXML
    private Label totalRatingsCount;
    @FXML
    private Pagination servicesPagination;

    private ServiceService serviceService = new ServiceService();
    private ServiceItem selectedService;
    private ObservableList<ServiceItem> allServices;
    private FilteredList<ServiceItem> filteredServices;

    private static final int ITEMS_PER_PAGE = 6; // Nombre d'éléments par page

    private String imageUrl_temp;

    @FXML
    private void initialize() {
        // Configuration initiale
        setupControls();
        loadServices();
        setupSearch();
        setupFilter();
        updateStatistics();
    }

    private void setupControls() {
        btnEdit.setDisable(true);
        btnDelete.setDisable(true);

        // Configuration de la pagination
        servicesPagination.setPageCount(1);
        servicesPagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            loadServicesForPage(newIndex.intValue());
        });
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterServices();
        });
    }

    private void setupFilter() {
        // Ajouter les catégories de filtrage
        filterComboBox.setItems(FXCollections.observableArrayList(
                "Tous les services",
                "Prix croissant",
                "Prix décroissant",
                "Plus évalués"
        ));

        filterComboBox.getSelectionModel().selectFirst();
        filterComboBox.setOnAction(e -> filterServices());
    }

    private void loadServices() {
        try {
            allServices = FXCollections.observableArrayList(serviceService.afficher());
            filteredServices = new FilteredList<>(allServices);
            updateGridView();
            updateStatistics();
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Erreur", "Impossible de charger les services", e.getMessage());
        }
    }

    private void updateStatistics() {
        try {
            // Mettre à jour les statistiques
            activeServicesCount.setText(String.valueOf(allServices.size()));

            // Vous pouvez ajouter ici la logique pour compter les réservations et évaluations
            int totalReservations = serviceService.countTotalReservations();
            totalReservationsCount.setText(String.valueOf(totalReservations));

            int totalRatings = serviceService.countTotalRatings();
            totalRatingsCount.setText(String.valueOf(totalRatings));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void filterServices() {
        String searchText = searchField.getText().toLowerCase();
        String filterCategory = filterComboBox.getValue();

        // D'abord, filtrer par texte de recherche
        filteredServices.setPredicate(service ->
                service.getNom().toLowerCase().contains(searchText) ||
                        service.getDescription().toLowerCase().contains(searchText)
        );

        // Ensuite, trier selon la catégorie sélectionnée
        ObservableList<ServiceItem> sortedList = FXCollections.observableArrayList(filteredServices);

        switch (filterCategory) {
            case "Prix croissant":
                sortedList.sort((s1, s2) -> Double.compare(s1.getPrix(), s2.getPrix()));
                break;

            case "Prix décroissant":
                sortedList.sort((s1, s2) -> Double.compare(s2.getPrix(), s1.getPrix()));
                break;

            case "Plus évalués":
                sortedList.sort((s1, s2) -> {
                    try {
                        Map<String, Integer> feedback1 = serviceService.countFeedbackByPrenom(s1.getId());
                        Map<String, Integer> feedback2 = serviceService.countFeedbackByPrenom(s2.getId());

                        int total1 = feedback1.getOrDefault("like", 0) + feedback1.getOrDefault("dislike", 0);
                        int total2 = feedback2.getOrDefault("like", 0) + feedback2.getOrDefault("dislike", 0);

                        return Integer.compare(total2, total1); // Ordre décroissant
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return 0;
                    }
                });
                break;
        }

        // Mettre à jour la liste filtrée
        filteredServices = new FilteredList<>(sortedList);

        // Mettre à jour l'affichage
        updateGridView();
    }

    private void updateGridView() {
        // Mettre à jour la pagination et charger la première page
        int pageCount = (int) Math.ceil((double) filteredServices.size() / ITEMS_PER_PAGE);
        servicesPagination.setPageCount(Math.max(1, pageCount));
        loadServicesForPage(0);
    }

    private void loadServicesForPage(int pageIndex) {
        try {
            servicesContainer.getChildren().clear();

            int startIndex = pageIndex * ITEMS_PER_PAGE;
            int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, filteredServices.size());

            // Mettre à jour le nombre total de pages
            int pageCount = (int) Math.ceil((double) filteredServices.size() / ITEMS_PER_PAGE);
            servicesPagination.setPageCount(pageCount);

            // Afficher les services de la page courante
            for (int i = startIndex; i < endIndex; i++) {
                ServiceItem service = filteredServices.get(i);
                addServiceToContainer(service);
            }

        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Erreur", "Impossible de charger la page", e.getMessage());
        }
    }

    private void addServiceToContainer(ServiceItem service) throws SQLException {
        // Création du conteneur principal pour le service
        VBox serviceBox = new VBox(10);
        serviceBox.setStyle("-fx-background-color: white; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2); " +
                "-fx-padding: 15; " +
                "-fx-background-radius: 10; " +
                "-fx-min-width: 300; " +
                "-fx-max-width: 300;");

        // Image du service
        ImageView imageView = new ImageView();
        try {
            Image image = new Image(service.getImageUrl(), 270, 150, true, true);
            imageView.setImage(image);
        } catch (Exception e) {
            // Image par défaut en cas d'erreur
            imageView.setImage(new Image("/images/placeholder.png", 270, 150, true, true));
        }
        imageView.setStyle("-fx-background-radius: 5;");

        // Informations du service
        Label nameLabel = new Label(service.getNom());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        Label descriptionLabel = new Label(service.getDescription());
        descriptionLabel.setStyle("-fx-text-fill: #666; -fx-wrap-text: true;");

        Label priceLabel = new Label(String.format("%.2f DT", service.getPrix()));
        priceLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold; -fx-font-size: 14px;");

        // Statistiques des likes/dislikes
        Map<String, Integer> feedback = serviceService.countFeedbackByPrenom(service.getId());
        int likes = feedback.getOrDefault("like", 0);
        int dislikes = feedback.getOrDefault("dislike", 0);

        HBox statsBox = new HBox(15);
        statsBox.setAlignment(Pos.CENTER);

        Label likesLabel = new Label("👍 " + likes);
        likesLabel.setStyle("-fx-text-fill: #4CAF50;");

        Label dislikesLabel = new Label("👎 " + dislikes);
        dislikesLabel.setStyle("-fx-text-fill: #f44336;");

        statsBox.getChildren().addAll(likesLabel, dislikesLabel);

        // Derniers avis
        VBox feedbackBox = new VBox(5);
        feedbackBox.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 10; -fx-background-radius: 5;");

        Label feedbackTitle = new Label("Derniers avis");
        feedbackTitle.setStyle("-fx-font-weight: bold;");
        feedbackBox.getChildren().add(feedbackTitle);

        List<Map<String, String>> feedbackList = serviceService.getFeedbackDetails(service.getId());
        for (Map<String, String> feedbackItem : feedbackList) {
            String emoji = feedbackItem.get("type").equals("like") ? "👍" : "👎";
            Label userFeedback = new Label(emoji + " " + feedbackItem.get("user"));
            userFeedback.setStyle("-fx-font-size: 12px;");
            feedbackBox.getChildren().add(userFeedback);
        }

        // Ajout de tous les éléments au conteneur
        serviceBox.getChildren().addAll(
                imageView,
                nameLabel,
                descriptionLabel,
                priceLabel,
                statsBox,
                feedbackBox
        );

        // Gestion de la sélection
        serviceBox.setOnMouseClicked(e -> {
            selectedService = service;
            btnEdit.setDisable(false);
            btnDelete.setDisable(false);

            // Effet visuel de sélection
            servicesContainer.getChildren().forEach(node ->
                    node.setStyle(node.getStyle().replace("-fx-border-color: #3498db;", ""))
            );
            serviceBox.setStyle(serviceBox.getStyle() + "-fx-border-color: #3498db; -fx-border-width: 2;");
        });

        servicesContainer.getChildren().add(serviceBox);
    }

    @FXML
    private void addService() {
        Stage addServiceStage = new Stage();
        GridPane formGrid = createServiceForm(null);
        Scene addServiceScene = new Scene(formGrid, 400, 300);
        addServiceStage.setScene(addServiceScene);
        addServiceStage.setTitle("Ajouter un service");
        addServiceStage.show();
    }

    @FXML
    private void editService() {
        if (selectedService == null) {
            showAlert(AlertType.WARNING, "Attention", "Aucun service sélectionné", "Veuillez sélectionner un service à modifier.");
            return;
        }

        Stage editServiceStage = new Stage();
        GridPane formGrid = createServiceForm(selectedService);
        Scene editServiceScene = new Scene(formGrid, 400, 300);
        editServiceStage.setScene(editServiceScene);
        editServiceStage.setTitle("Modifier le service");
        editServiceStage.show();
    }

    private GridPane createServiceForm(ServiceItem service) {
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setPadding(new Insets(20));
        formGrid.setAlignment(Pos.CENTER);

        // Style du formulaire
        formGrid.setStyle("-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        TextField nameField = new TextField(service != null ? service.getNom() : "");
        TextField descriptionField = new TextField(service != null ? service.getDescription() : "");
        TextField priceField = new TextField(service != null ? String.valueOf(service.getPrix()) : "");

        // Prévisualisation de l'image
        ImageView imagePreview = new ImageView();
        imagePreview.setFitHeight(150);
        imagePreview.setFitWidth(150);
        imagePreview.setPreserveRatio(true);

        if (service != null && service.getImageUrl() != null) {
            try {
                if (service.getImageUrl().startsWith("data:image")) {
                    String base64Image = service.getImageUrl().split(",")[1];
                    byte[] imageData = Base64.getDecoder().decode(base64Image);
                    Image image = new Image(new ByteArrayInputStream(imageData));
                    imagePreview.setImage(image);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Bouton pour upload d'image
        Button uploadButton = new Button("Choisir une image");
        uploadButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 8 15;");

        // Label pour afficher le nom du fichier
        Label imageLabel = new Label("Aucune image sélectionnée");
        imageLabel.setStyle("-fx-text-fill: #666;");

        // Conteneur pour l'upload d'image
        VBox imageContainer = new VBox(10);
        imageContainer.setAlignment(Pos.CENTER);
        imageContainer.getChildren().addAll(imagePreview, uploadButton, imageLabel);

        // Gestionnaire d'événement pour l'upload
        uploadButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();

            // Configuration du FileChooser
            fileChooser.setTitle("Choisir une image");

            // Définir un filtre pour afficher toutes les images
            FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter(
                    "Images (*.png, *.jpg, *.jpeg, *.gif, *.bmp)", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"
            );
            fileChooser.getExtensionFilters().add(imageFilter);

            // Essayer différents dossiers de départ
            File startingDir = new File(System.getProperty("user.home") + File.separator + "Downloads");
            if (!startingDir.exists() || !startingDir.isDirectory()) {
                startingDir = new File(System.getProperty("user.home") + File.separator + "Pictures");
            }
            if (!startingDir.exists() || !startingDir.isDirectory()) {
                startingDir = new File(System.getProperty("user.home"));
            }

            // Appliquer le dossier initial si valide
            if (startingDir.exists() && startingDir.isDirectory()) {
                fileChooser.setInitialDirectory(startingDir);
            }

            // Ouvrir le sélecteur de fichiers
            File selectedFile = fileChooser.showOpenDialog(formGrid.getScene().getWindow());

            if (selectedFile != null) {
                try {
                    System.out.println("📂 Fichier sélectionné: " + selectedFile.getAbsolutePath());

                    // Vérifier que le fichier existe et est lisible
                    if (!selectedFile.exists() || !selectedFile.canRead()) {
                        throw new IOException("Le fichier n'est pas accessible.");
                    }

                    // Lire l'image et la convertir en base64
                    byte[] imageBytes = Files.readAllBytes(selectedFile.toPath());
                    String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                    String mimeType = determineMimeType(selectedFile);
                    String imageUrl = "data:" + mimeType + ";base64," + base64Image;

                    // Mise à jour de l'aperçu
                    Image testImage = new Image(selectedFile.toURI().toString());
                    imagePreview.setImage(testImage);
                    imageLabel.setText(selectedFile.getName());

                    // Stockage de l'URL
                    if (service != null) {
                        service.setImageUrl(imageUrl);
                    } else {
                        imageUrl_temp = imageUrl;
                    }

                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Image chargée", "L'image a été chargée avec succès.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Échec du chargement", "Impossible de charger l'image.");
                }
            }
        });

        styleFormField(nameField, "Nom du service");
        styleFormField(descriptionField, "Description");
        styleFormField(priceField, "Prix");

        formGrid.add(createFormLabel("Nom du service:"), 0, 0);
        formGrid.add(nameField, 1, 0);
        formGrid.add(createFormLabel("Description:"), 0, 1);
        formGrid.add(descriptionField, 1, 1);
        formGrid.add(createFormLabel("Prix:"), 0, 2);
        formGrid.add(priceField, 1, 2);
        formGrid.add(createFormLabel("Image:"), 0, 3);
        formGrid.add(imageContainer, 1, 3);

        Button saveButton = new Button(service != null ? "Modifier" : "Ajouter");
        styleButton(saveButton);
        formGrid.add(saveButton, 1, 4);

        saveButton.setOnAction(event -> {
            handleServiceSave(
                    nameField.getText(),
                    descriptionField.getText(),
                    priceField.getText(),
                    service != null ? service.getImageUrl() : null,
                    service,
                    (Stage) formGrid.getScene().getWindow()
            );
        });

        return formGrid;
    }

    private void styleFormField(TextField field, String promptText) {
        field.setPromptText(promptText);
        field.setStyle("-fx-padding: 8 15; -fx-background-radius: 5; -fx-border-radius: 5; " +
                "-fx-border-color: #ddd; -fx-border-width: 1; -fx-font-size: 14px;");
        field.setPrefWidth(250);
    }

    private Label createFormLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50;");
        return label;
    }

    private void styleButton(Button button) {
        button.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-padding: 10 20; -fx-background-radius: 5; " +
                "-fx-cursor: hand;");
    }

    private void handleServiceSave(String name, String description, String priceStr, String imageUrl,
                                   ServiceItem existingService, Stage stage) {
        if (name.isEmpty() || description.isEmpty() || priceStr.isEmpty()) {
            showAlert(AlertType.WARNING, "Attention", "Champs incomplets", "Veuillez remplir tous les champs.");
            return;
        }

        // Utiliser l'URL temporaire pour un nouveau service
        if (imageUrl == null && imageUrl_temp != null) {
            imageUrl = imageUrl_temp;
        }

        if (imageUrl == null || imageUrl.isEmpty()) {
            showAlert(AlertType.WARNING, "Attention", "Image manquante", "Veuillez sélectionner une image.");
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            ServiceItem service = existingService != null ? existingService : new ServiceItem();
            service.setNom(name);
            service.setDescription(description);
            service.setPrix(price);
            service.setImageUrl(imageUrl);

            if (existingService == null) {
                serviceService.ajouter(service);
            } else {
                serviceService.modifier(service);
            }

            stage.close();
            loadServices();
            showAlert(AlertType.INFORMATION, "Succès",
                    existingService != null ? "Service modifié" : "Service ajouté",
                    "L'opération a été effectuée avec succès.");
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Erreur", "Prix invalide", "Veuillez entrer un prix valide.");
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Erreur", "Erreur de base de données", e.getMessage());
        }
    }

    @FXML
    private void deleteService() {
        if (selectedService == null) {
            showAlert(AlertType.WARNING, "Attention", "Aucun service sélectionné", "Veuillez sélectionner un service à supprimer.");
            return;
        }

        Alert confirmDialog = new Alert(AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation");
        confirmDialog.setHeaderText("Supprimer le service");
        confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer ce service ?");

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    serviceService.supprimer(selectedService.getId());
                    loadServices();
                    showAlert(AlertType.INFORMATION, "Succès", "Service supprimé", "Le service a été supprimé avec succès.");
                } catch (SQLException e) {
                    showAlert(AlertType.ERROR, "Erreur", "Erreur de suppression", e.getMessage());
                }
            }
        });
    }

    private void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Navigation methods
    @FXML
    public void goToReservation(ActionEvent event) {
        navigateToPage(event, "/ServiceAdmin.fxml");
    }

    @FXML
    public void goToHotel(ActionEvent event) {
        navigateToPage(event, "/AfficherHebergement.fxml");
    }

    @FXML
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
            showAlert(AlertType.ERROR, "Erreur", "Erreur de navigation",
                    "Impossible de charger la page: " + resourcePath);
        }
    }

    // Ajouter ces méthodes utilitaires à la classe
    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // Pas d'extension
        }
        return name.substring(lastIndexOf + 1);
    }

    private String determineMimeType(File file) {
        String extension = getFileExtension(file).toLowerCase();
        switch (extension) {
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            default:
                return "image/jpeg"; // Par défaut
        }
    }
}
