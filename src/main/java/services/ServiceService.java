package services;

import entities.ServiceItem;
import Wedding.utils.MyDatabase;

import java.sql.*;
import java.util.*;

public class ServiceService implements IService<ServiceItem> {
    private Connection connection;

    public ServiceService() {
        this.connection = MyDatabase.getInstance().getConnection();
    }

    // Method to add a new service with a user reference
    @Override
    public int ajouter(ServiceItem serviceItem) throws SQLException {
        String req = "INSERT INTO service (nom, description, prix, image_url) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, serviceItem.getNom());
            ps.setString(2, serviceItem.getDescription());
            ps.setDouble(3, serviceItem.getPrix());
            ps.setString(4, serviceItem.getImageUrl());
            // Store the user who added the service

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
            return -1;
        }
    }

    // Update a service
    @Override
    public void modifier(ServiceItem serviceItem) throws SQLException {
        String req = "UPDATE service SET nom=?, description=?, prix=?, image_url=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, serviceItem.getNom());
            ps.setString(2, serviceItem.getDescription());
            ps.setDouble(3, serviceItem.getPrix());
            ps.setString(4, serviceItem.getImageUrl());
            ps.setInt(5, serviceItem.getId());

            ps.executeUpdate();
        }
    }

    // Delete a service
    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM service WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
    public List<ServiceItem> getServicesByEventId(int limit) throws SQLException {
        List<ServiceItem> services = new ArrayList<>();
        String query = "SELECT * FROM service ORDER BY RAND() LIMIT ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                services.add(new ServiceItem(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getDouble("prix"),
                        rs.getString("image_url")
                ));
            }
        }
        return services;
    }


    // Retrieve all services
    public List<ServiceItem> afficher() throws SQLException {
        List<ServiceItem> services = new ArrayList<>();
        String req = "SELECT * FROM service";

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                services.add(new ServiceItem(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getDouble("prix"),
                        rs.getString("image_url")

                ));
            }
        }
        return services;
    }
    // Ajouter un like ou dislike avec prénom
    public void addServiceFeedbackByPrenom(int serviceId, String userPrenom, String type) throws SQLException {
        String query = "INSERT INTO service_feedback (service_id, user_prenom, like_type) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, serviceId);
            ps.setString(2, userPrenom);
            ps.setString(3, type);
            ps.executeUpdate();
        }
    }

    // Récupérer les likes/dislikes pour un service en utilisant le prénom
    public Map<String, Integer> countFeedbackByPrenom(int serviceId) throws SQLException {
        String query = "SELECT like_type, COUNT(*) AS count FROM service_feedback WHERE service_id = ? GROUP BY like_type";
        Map<String, Integer> results = new HashMap<>();
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, serviceId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                results.put(rs.getString("like_type"), rs.getInt("count"));
            }
        }
        return results;
    }

    // Récupérer les détails des likes/dislikes avec les noms des clients
    public List<Map<String, String>> getFeedbackDetails(int serviceId) throws SQLException {
        String query = "SELECT user_prenom, like_type FROM service_feedback WHERE service_id = ? ORDER BY user_prenom";
        List<Map<String, String>> feedbackList = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, serviceId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, String> feedback = new HashMap<>();
                feedback.put("user", rs.getString("user_prenom"));
                feedback.put("type", rs.getString("like_type"));
                feedbackList.add(feedback);
            }
        }
        return feedbackList;
    }

    public List<ServiceItem> getReservedServicesByPrenom(String prenom) throws SQLException {
        List<ServiceItem> services = new ArrayList<>();
        String query = "SELECT DISTINCT s.* FROM service s " +
                "INNER JOIN reservation r ON r.service_id = s.id " +
                "WHERE r.utilisateur = ? AND r.service_id IS NOT NULL " +
                "ORDER BY s.id DESC";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, prenom);
            System.out.println("✅ Exécution de la requête SQL: " + query.replace("?", "'" + prenom + "'"));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ServiceItem service = new ServiceItem(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getDouble("prix"),
                        rs.getString("image_url")
                );
                services.add(service);
                System.out.println("✅ Service chargé: " +
                        "\n   ID=" + service.getId() +
                        "\n   Nom=" + service.getNom() +
                        "\n   Description=" + service.getDescription() +
                        "\n   Prix=" + service.getPrix() +
                        "\n   Image=" + service.getImageUrl());
            }

            System.out.println("✅ Total des services trouvés: " + services.size());
            return services;
        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL: " + e.getMessage());
            throw e;
        }
    }

    // Méthode pour compter le nombre total de réservations
    public int countTotalReservations() throws SQLException {
        String query = "SELECT COUNT(*) as total FROM reservation WHERE service_id IS NOT NULL";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    // Méthode pour compter le nombre total d'évaluations (likes + dislikes)
    public int countTotalRatings() throws SQLException {
        String query = "SELECT COUNT(*) as total FROM service_feedback";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

}