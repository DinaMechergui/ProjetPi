package org.example.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.example.entities.Hebergement;

import java.io.IOException;
import java.util.List;

public class MapViewController {

    @FXML
    private WebView mapView; // WebView pour afficher la carte

    private List<Hebergement> hebergements; // Liste des hébergements à afficher

    // Méthode pour initialiser les données des hébergements
    public void setHebergements(List<Hebergement> hebergements) {
        this.hebergements = hebergements;
        loadOpenStreetMap(hebergements); // Charger la carte avec les hébergements
    }

    // Méthode pour charger la carte OpenStreetMap
    private void loadOpenStreetMap(List<Hebergement> hebergements) {
        if (mapView == null) {
            System.err.println("WebView (mapView) n'est pas initialisée !");
            return;
        }

        WebEngine webEngine = mapView.getEngine();

        // HTML et JavaScript pour afficher la carte avec Leaflet
        String html = """
        <html>
            <head>
                <link rel="stylesheet" href="https://unpkg.com/leaflet@1.7.1/dist/leaflet.css"/>
                <script src="https://unpkg.com/leaflet@1.7.1/dist/leaflet.js"></script>
            </head>
            <body>
                <div id="map" style="width: 100%; height: 100%;"></div>
                <script>
                    var map = L.map('map').setView([36.8065, 10.1815], 13); // Coordonnées de Tunis

                    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                        attribution: '© OpenStreetMap contributors'
                    }).addTo(map);

                    // Ajouter des marqueurs pour chaque hébergement
                    """;

        // Ajouter les marqueurs pour chaque hébergement
        if (hebergements != null && !hebergements.isEmpty()) {
            for (Hebergement hebergement : hebergements) {
                // Formater les coordonnées avec un point comme séparateur décimal
                String markerScript = String.format(
                        "L.marker([%.6f, %.6f]).addTo(map)\n" +
                                "    .bindPopup('<b>%s</b><br>%s<br>Prix/Nuit: %.2f TND');\n",
                        hebergement.getLatitude(),  // Latitude (format décimal avec un point)
                        hebergement.getLongitude(), // Longitude (format décimal avec un point)
                        hebergement.getNom(),       // Nom de l'hébergement
                        hebergement.getAdresse(),   // Adresse de l'hébergement
                        hebergement.getPrixParNuit() // Prix par nuit (format décimal avec un point)
                );
                html += markerScript; // Ajouter le script du marqueur au HTML
            }
        }

        // Ajouter un écouteur d'événements pour le clic sur la carte
        html += """
                    // Écouter le clic sur la carte pour ajouter une épingle
                    map.on('click', function(e) {
                        var lat = e.latlng.lat;
                        var lng = e.latlng.lng;
                        
                        // Ajouter une nouvelle épingle à la position cliquée
                        L.marker([lat, lng]).addTo(map)
                            .bindPopup('Nouvelle épingle: ' + lat.toFixed(6) + ', ' + lng.toFixed(6))
                            .openPopup();
                    });
                </script>
            </body>
        </html>
        """;

        // Afficher le code généré dans la console
        System.out.println(html);

        // Charger le contenu HTML dans la WebView
        webEngine.loadContent(html);
    }

    // Méthode pour retourner à la page précédente
    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hebergementclient.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) mapView.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}