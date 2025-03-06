package org.example.services;

import javafx.scene.image.Image;
import org.example.entities.Hebergement;
import org.Wedding.utils.MyDatabase;

import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceHebergement implements IHebergement {
    private final Connection connection;

    public ServiceHebergement() {
        this.connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Hebergement hebergement) throws SQLException {
        String query = "INSERT INTO Hebergement (nom, adresse, prixParNuit, disponible, imageUrl, latitude, longitude) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, hebergement.getNom());
            preparedStatement.setString(2, hebergement.getAdresse());
            preparedStatement.setDouble(3, hebergement.getPrixParNuit());
            preparedStatement.setBoolean(4, hebergement.isDisponible());
            preparedStatement.setString(5, hebergement.getImageUrl());
            preparedStatement.setDouble(6, hebergement.getLatitude());
            preparedStatement.setDouble(7, hebergement.getLongitude());
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void modifier(Hebergement hebergement) throws SQLException {
        String req = "UPDATE hebergement SET nom = ?, adresse = ?, prixParNuit = ?, disponible = ?, imageUrl = ? WHERE idheb = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setString(1, hebergement.getNom());
            preparedStatement.setString(2, hebergement.getAdresse());
            preparedStatement.setDouble(3, hebergement.getPrixParNuit());
            preparedStatement.setBoolean(4, hebergement.isDisponible());
            preparedStatement.setString(5, hebergement.getImageUrl()); // Ajout du paramètre imageUrl
            preparedStatement.setInt(6, hebergement.getIdheb());

            preparedStatement.executeUpdate();
            System.out.println("✅ Hébergement modifié avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification de l'hébergement : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM hebergement WHERE idheb = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, id);

            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("✅ Hébergement supprimé avec succès !");
            } else {
                System.out.println("⚠ Aucun hébergement trouvé avec cet ID !");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression de l'hébergement : " + e.getMessage());
        }
    }

    public List<Hebergement> afficher() throws SQLException {
        List<Hebergement> hebergements = new ArrayList<>();
        String req = "SELECT * FROM hebergement"; // Ajout de imageUrl
        Statement statement = this.connection.createStatement();
        ResultSet rs = statement.executeQuery(req);

        while (rs.next()) {
            Hebergement hebergement = new Hebergement(
                    rs.getInt("idheb"),
                    rs.getString("nom"),
                    rs.getString("adresse"),
                    rs.getDouble("prixParNuit"),
                    rs.getBoolean("disponible"),
                    rs.getString("imageUrl"), // Récupération de l'URL de l'image
                    rs.getDouble("latitude"),
                    rs.getDouble("longitude")

            );
            hebergements.add(hebergement);
        }
        System.out.println("🏨 Hébergements affichés : " + hebergements);
        return hebergements;
    }

    public List<Hebergement> getAllHebergements() throws SQLException {
        List<Hebergement> hebergements = new ArrayList<>();
        String query = "SELECT idheb, nom, adresse, prixParNuit, disponible, imageUrl,latitude,longitude FROM hebergement"; // Ajout de imageUrl

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            hebergements.add(new Hebergement(
                    rs.getInt("idheb"),
                    rs.getString("nom"),
                    rs.getString("adresse"),
                    rs.getDouble("prixParNuit"),
                    rs.getBoolean("disponible"),
                    rs.getString("imageUrl"), // Ajout de l'URL de l'image
                    rs.getDouble("latitude"),
                    rs.getDouble("longitude")
            ));
        }
        return hebergements;
    }

    public List<Hebergement> recommanderHebergements(String localisation, double prixMin, double prixMax, boolean disponible) throws SQLException {
        // Construction dynamique de la requête
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM Hebergement WHERE 1=1");
        List<Object> parameters = new ArrayList<>();

        // Filtre par localisation
        if (localisation != null && !localisation.isEmpty()) {
            queryBuilder.append(" AND adresse LIKE ?");
            parameters.add("%" + localisation + "%");
        }

        // Filtre par prix
        if (prixMin >= 0 && prixMax >= 0) {
            if (prixMin > prixMax) {
                throw new IllegalArgumentException("Le prix minimum ne peut pas être supérieur au prix maximum.");
            }
            queryBuilder.append(" AND prixParNuit BETWEEN ? AND ?");
            parameters.add(prixMin);
            parameters.add(prixMax);
        }

        // Filtre par disponibilité
        if (disponible) {
            queryBuilder.append(" AND disponible = ?");
            parameters.add(1);
        }

        // Exécuter la requête avec les paramètres
        return executeQueryAndMapToHebergements(queryBuilder.toString(), parameters);
    }

    private List<Hebergement> executeQueryAndMapToHebergements(String query, List<Object> parameters) throws SQLException {
        List<Hebergement> hebergements = new ArrayList<>();

        try (
             PreparedStatement stmt = connection.prepareStatement(query)) {

            // Ajouter les paramètres à la requête
            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }

            // Exécuter la requête et mapper les résultats
            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Hebergement hebergement = new Hebergement();
                    hebergement.setIdheb(rs.getInt("idheb"));
                    hebergement.setNom(rs.getString("nom"));
                    hebergement.setAdresse(rs.getString("adresse"));
                    hebergement.setPrixParNuit(rs.getDouble("prixParNuit"));
                    hebergement.setDisponible(rs.getBoolean("disponible"));
                    hebergement.setImageUrl(rs.getString("imageUrl"));
                    hebergement.setLatitude(rs.getDouble("latitude"));
                    hebergement.setLongitude(rs.getDouble("longitude"));

                    hebergements.add(hebergement);
                }
            }
            System.out.println("Requête SQL : " + query);
            System.out.println("Paramètres : " + parameters);

            return hebergements;
        }

      /*  public List<Hebergement> getHebergementsWithValidCoordinates () throws SQLException {
            List<Hebergement> hebergements = new ArrayList<>();
            String query = "SELECT idheb, nom, adresse, prixParNuit, disponible, imageUrl, latitude, longitude " +
                    "FROM hebergement WHERE latitude != 0 AND longitude != 0";

            try (Connection connection = MyDatabase.getInstance().getConnection();
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {

                while (resultSet.next()) {
                    Hebergement hebergement = new Hebergement();
                    hebergement.setIdheb(resultSet.getInt("idheb"));
                    hebergement.setNom(resultSet.getString("nom"));
                    hebergement.setAdresse(resultSet.getString("adresse"));
                    hebergement.setPrixParNuit(resultSet.getDouble("prixParNuit"));
                    hebergement.setDisponible(resultSet.getBoolean("disponible"));
                    hebergement.setImageUrl(resultSet.getString("imageUrl"));
                    hebergement.setLatitude(resultSet.getDouble("latitude"));
                    hebergement.setLongitude(resultSet.getDouble("longitude"));

                    hebergements.add(hebergement);
                }
            }

            return hebergements;
        }
    }*/
    }
}
