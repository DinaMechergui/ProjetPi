package Wedding.controllers;

import Wedding.entities.Produit;
import Wedding.service.ServiceProduit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatController {

    @FXML
    private Button CategorieButton;

    @FXML
    private Button ProduitButton;

    @FXML
    private Button frontButton;

    @FXML
    private Button homeButton;

    @FXML
    private AnchorPane home_form;

    @FXML
    private ImageView logoImageView;

    @FXML
    private Button logoutButton;

    @FXML
    private AnchorPane main_form;

    @FXML
    private Button profileButton;

    @FXML
    private Label usernameLabel;

    @FXML
    private PieChart productPieChart; // Graphique en secteurs pour les catégories

    @FXML
    private AreaChart<Number, Number> priceDistributionChart; // Graphique en aires pour la distribution des prix

    @FXML
    private NumberAxis xAxis; // Axe X pour le graphique en aires

    @FXML
    private NumberAxis yAxis; // Axe Y pour le graphique en aires

    @FXML
    private Button returnbutton; // Bouton de retour

    private ServiceProduit produitService = new ServiceProduit(); // Service pour interagir avec les produits

    @FXML
    public void initialize() {
        try {
            // Récupérer tous les produits depuis la base de données
            List<Produit> productList = produitService.afficher();

            // Statistique 1 : Répartition des produits par catégorie (PieChart)
            loadCategoryPieChartData(productList);

            // Statistique 2 : Distribution des prix des produits (AreaChart)
            loadPriceDistributionChartData(productList);

        } catch (SQLException e) {
            e.printStackTrace();
            // Gérer l'exception (par exemple, afficher un message d'erreur)
        }
    }

    /**
     * Charge les données pour le PieChart (répartition des produits par catégorie).
     *
     * @param productList La liste des produits.
     */
    private void loadCategoryPieChartData(List<Produit> productList) {
        // Calculer le nombre de produits par catégorie
        Map<String, Integer> categoryCounts = new HashMap<>();
        for (Produit product : productList) {
            String categorie = product.getCategorie();
            categoryCounts.put(categorie, categoryCounts.getOrDefault(categorie, 0) + 1);
        }

        // Créer les données pour le PieChart
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : categoryCounts.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        // Appliquer les données au PieChart
        productPieChart.setData(pieChartData);
    }

    /**
     * Charge les données pour l'AreaChart (distribution des prix des produits).
     *
     * @param productList La liste des produits.
     */
    private void loadPriceDistributionChartData(List<Produit> productList) {
        // Vérifier si la liste est vide
        if (productList.isEmpty()) {
            System.out.println("Aucun produit disponible pour générer la distribution des prix.");
            return;
        }

        // Extraire les prix des produits
        List<Double> productPrices = productList.stream()
                .map(Produit::getPrix)
                .collect(Collectors.toList());

        // Déterminer la plage de prix
        double minPrice = Collections.min(productPrices);
        double maxPrice = Collections.max(productPrices);
        double range = maxPrice - minPrice;
        if (range == 0) {
            range = 1;
            if (range < 1) {  // Si range est trop petit, on force une valeur minimale
                range = 1;
            }

        }


        // Vérifier si tous les produits ont le même prix
        if (range == 0) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.getData().add(new XYChart.Data<>(minPrice, productList.size())); // Un seul point pour représenter tous les produits
            priceDistributionChart.getData().add(series);
            return;
        }

        // Définir le nombre de tranches (bins) pour la distribution des prix
        int numBins = 10; // Vous pouvez ajuster ce nombre en fonction de vos besoins

        // Calculer la largeur de chaque tranche
        double binWidth = range / numBins;

        // Calculer la fréquence des produits dans chaque tranche
        int[] binCounts = new int[numBins];
        for (Double price : productPrices) {
            int binIndex = (int) ((price - minPrice) / binWidth);
            if (binIndex >= numBins) {
                binIndex = numBins - 1; // Ajuster pour la dernière tranche
            }
            binCounts[binIndex]++;
        }

        // Créer les données pour l'AreaChart
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        for (int i = 0; i < numBins; i++) {
            double binStart = minPrice + i * binWidth;
            double binEnd = minPrice + (i + 1) * binWidth;
            double binMidPoint = (binStart + binEnd) / 2.0; // Point médian de la tranche
            series.getData().add(new XYChart.Data<>(binMidPoint, binCounts[i]));
        }
        System.out.println("Min Price: " + minPrice);
        System.out.println("Max Price: " + maxPrice);
        System.out.println("Range: " + range);
        System.out.println("Bin Width: " + binWidth);

        // Appliquer les données à l'AreaChart
        priceDistributionChart.getData().add(series);
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

    public void goToStat(ActionEvent event) {
        try {
            // Charger le fichier FXML de la page des produits
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Stat.fxml"));
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
    /**
     * Gère l'événement du bouton de retour.
     *
     * @param event L'événement de clic.
     */
    @FXML
    void returnbuttonOnClick(ActionEvent event) {
        // Fermer la fenêtre actuelle
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}