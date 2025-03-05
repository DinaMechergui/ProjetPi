package services;

import entities.ServiceItem;
import Wedding.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        String req = "UPDATE service SET nom=?, description=?, prix=?, image_url=?, WHERE id=?";
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

    // Retrieve services added by a specific user
    public List<ServiceItem> getServicesByUser(String utilisateur) throws SQLException {
        List<ServiceItem> services = new ArrayList<>();
        String req = "SELECT * FROM service WHERE utilisateur=?";

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, utilisateur);
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

    // ✅ Get Services for a specific Event
    public List<ServiceItem> getServicesForEvent(int eventId) throws SQLException {
        List<ServiceItem> services = new ArrayList<>();
        String query = "SELECT * FROM service WHERE id IN (SELECT service_id FROM event_service WHERE event_id = ?)";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, eventId);
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
}
