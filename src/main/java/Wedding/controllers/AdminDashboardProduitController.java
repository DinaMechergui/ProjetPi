package Wedding.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import Wedding.entities.Produit;
import Wedding.service.ServiceProduit;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class AdminDashboardProduitController implements Initializable {

    @FXML
    private GridPane productGrid; // GridPane pour afficher les produits

    @FXML
    private Button addProductButton; // Bouton "Ajouter Produit"

    private ServiceProduit serviceProduit = new ServiceProduit();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Lier le bouton "Ajouter" à la méthode handleAddProduct
        addProductButton.setOnAction(event -> handleAddProduct());

        try {
            loadProducts();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les produits.", Alert.AlertType.ERROR);
        }
    }

    private void loadProducts() throws SQLException {
        List<Produit> produits = serviceProduit.afficher();
        int row = 0;
        int col = 0;

        for (Produit produit : produits) {
            // Créer une carte pour le produit
            VBox productCard = createProductCard(produit);

            // Ajouter la carte au GridPane
            productGrid.add(productCard, col, row);

            // Passer à la colonne suivante
            col++;
            if (col > 2) { // 3 colonnes par ligne
                col = 0;
                row++;
            }
        }
    }

    private VBox createProductCard(Produit produit) {
        VBox card = new VBox(10);
        card.getStyleClass().add("product-card"); // Appliquer le style CSS

        // Image du produit
        ImageView productImage = new ImageView(new Image(produit.getImageUrl()));
        productImage.setFitWidth(150);
        productImage.setFitHeight(150);
        productImage.setPreserveRatio(true);

        // Nom du produit
        Label productName = new Label(produit.getNom());
        productName.getStyleClass().add("product-name"); // Appliquer le style CSS

        // Prix du produit
        Label productPrice = new Label("Prix: " + String.format("%.2f", produit.getPrix()) + " TND");
        productPrice.getStyleClass().add("product-price"); // Appliquer le style CSS

        // Description du produit
        Label productDescription = new Label("Description: " + produit.getDescription());
        productDescription.getStyleClass().add("product-description");

        // Stock du produit
        Label productStock = new Label("Stock: " + produit.getStock());
        productStock.getStyleClass().add("product-stock"); // Appliquer le style CSS

        // Boutons d'action
        Button editButton = new Button("Modifier");
        editButton.getStyleClass().add("reserve-button"); // Appliquer le style CSS
        editButton.setOnAction(event -> handleEditProduct(produit));

        Button deleteButton = new Button("Supprimer");
        deleteButton.getStyleClass().add("reserve-button"); // Appliquer le style CSS
        deleteButton.setOnAction(event -> handleDeleteProduct(produit));

        // Ajouter les éléments à la carte
        card.getChildren().addAll(productImage, productName, productPrice, productDescription, productStock, editButton, deleteButton);

        return card;
    }



    private void handleAddProduct() {
        Dialog<Produit> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un produit");
        dialog.setHeaderText("Remplissez les détails du produit");

        ButtonType addButton = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField nomField = new TextField();
        TextField descriptionField = new TextField();
        TextField prixField = new TextField();
        TextField categorieField = new TextField();
        TextField stockField = new TextField();
        TextField imageUrlField = new TextField();

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(new Label("Prix:"), 0, 2);
        grid.add(prixField, 1, 2);
        grid.add(new Label("Catégorie:"), 0, 3);
        grid.add(categorieField, 1, 3);
        grid.add(new Label("Stock:"), 0, 4);
        grid.add(stockField, 1, 4);
        grid.add(new Label("Image URL:"), 0, 5);
        grid.add(imageUrlField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == addButton) {
                try {
                    String nom = nomField.getText().trim();
                    String description = descriptionField.getText().trim();
                    String categorie = categorieField.getText().trim();
                    String imageUrl = imageUrlField.getText().trim();
                    double prix = Double.parseDouble(prixField.getText().trim());
                    int stock = Integer.parseInt(stockField.getText().trim());

                    if (nom.isEmpty() || description.isEmpty() || categorie.isEmpty() || imageUrl.isEmpty()) {
                        showAlert("Erreur", "Tous les champs doivent être remplis.", Alert.AlertType.ERROR);
                        return null;
                    }

                    return new Produit(0, nom, description, prix, categorie, stock, imageUrl);

                } catch (NumberFormatException e) {
                    showAlert("Erreur", "Le prix et le stock doivent être des nombres valides.", Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });

        Optional<Produit> result = dialog.showAndWait();
        result.ifPresent(produit -> {
            try {
                serviceProduit.ajouter(produit);
                productGrid.getChildren().clear();
                loadProducts();
                showAlert("Succès", "Produit ajouté avec succès.", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Erreur SQL", "Impossible d'ajouter le produit.\nDétail : " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private void handleEditProduct(Produit produit) {
        Dialog<Produit> dialog = new Dialog<>();
        dialog.setTitle("Modifier un produit");
        dialog.setHeaderText("Modifiez les détails du produit");

        ButtonType saveButton = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField nomField = new TextField(produit.getNom());
        TextField descriptionField = new TextField(produit.getDescription());
        TextField prixField = new TextField(String.valueOf(produit.getPrix()));
        TextField categorieField = new TextField(produit.getCategorie());
        TextField stockField = new TextField(String.valueOf(produit.getStock()));
        TextField imageUrlField = new TextField(produit.getImageUrl());

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(new Label("Prix:"), 0, 2);
        grid.add(prixField, 1, 2);
        grid.add(new Label("Catégorie:"), 0, 3);
        grid.add(categorieField, 1, 3);
        grid.add(new Label("Stock:"), 0, 4);
        grid.add(stockField, 1, 4);
        grid.add(new Label("Image URL:"), 0, 5);
        grid.add(imageUrlField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == saveButton) {
                try {
                    String nom = nomField.getText().trim();
                    String description = descriptionField.getText().trim();
                    String categorie = categorieField.getText().trim();
                    String imageUrl = imageUrlField.getText().trim();
                    double prix = Double.parseDouble(prixField.getText().trim());
                    int stock = Integer.parseInt(stockField.getText().trim());

                    if (nom.isEmpty() || description.isEmpty() || categorie.isEmpty() || imageUrl.isEmpty()) {
                        showAlert("Erreur", "Tous les champs doivent être remplis.", Alert.AlertType.ERROR);
                        return null;
                    }

                    return new Produit(produit.getId(), nom, description, prix, categorie, stock, imageUrl);

                } catch (NumberFormatException e) {
                    showAlert("Erreur", "Tous les champs doivent être rempli.", Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });

        Optional<Produit> result = dialog.showAndWait();
        result.ifPresent(updatedProduit -> {
            try {
                serviceProduit.modifier(updatedProduit);
                productGrid.getChildren().clear();
                loadProducts();
                showAlert("Succès", "Produit modifié avec succès.", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Erreur SQL", "Impossible de modifier le produit.\nDétail : " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private void handleDeleteProduct(Produit produit) {
        try {
            serviceProduit.supprimer(produit.getId());
            productGrid.getChildren().clear();
            loadProducts();
            showAlert("Succès", "Produit supprimé avec succès.", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            showAlert("Erreur SQL", "Impossible de supprimer le produit.\nDétail : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void goToReservation(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ReservationDashboard.fxml"));
            Parent root = loader.load();

            // Récupérer la fenêtre actuelle à partir de l'événement
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de la page des produits.");
        }
    }
}