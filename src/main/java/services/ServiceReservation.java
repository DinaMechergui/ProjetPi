package services;

import entities.reserve;
import entities.Event;
import entities.ServiceItem;
import Wedding.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceReservation {
    private Connection connection;

    public ServiceReservation() {
        this.connection = MyDatabase.getInstance().getConnection();
    }
    public boolean exists(int eventId, int serviceId, String utilisateur, java.sql.Date sqlDate) throws SQLException {
        String query = "SELECT COUNT(*) FROM reservation WHERE event_id=? AND service_id=? AND utilisateur=? AND date_reservation=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, eventId);
            ps.setInt(2, serviceId);
            ps.setString(3, utilisateur);
            ps.setDate(4, sqlDate);  // ✅ Now uses java.sql.Date instead of java.util.Date

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;  // ✅ Returns true if at least 1 matching reservation exists
            }
        }
        return false;
    }


    // ✅ Ajouter une réservation avec l'utilisateur (nom)
    public int ajouter(reserve reservation) throws SQLException {
        String req = "INSERT INTO reservation (event_id, service_id, date_reservation,  prix_total, utilisateur) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, reservation.getEvent().getId());
            ps.setInt(2, reservation.getService().getId());
            ps.setDate(3, new java.sql.Date(reservation.getDateReservation().getTime()));
            ps.setDouble(4, reservation.getPrixTotal());
            ps.setString(5, reservation.getUtilisateur());

            System.out.println("🔍 Trying to insert into reservation:");
            System.out.println("Event ID: " + reservation.getEvent().getId());
            System.out.println("Service ID: " + reservation.getService().getId());
            System.out.println("Date: " + reservation.getDateReservation());
            System.out.println("Total Price: " + reservation.getPrixTotal());
            System.out.println("User: " + reservation.getUtilisateur());

            int rowsInserted = ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    System.out.println("✅ Reservation added successfully with ID: " + generatedKeys.getInt(1));
                    return generatedKeys.getInt(1);
                }
            }
            return -1;
        }
    }

    public void removeServiceFromCart(String utilisateur, int serviceId) throws SQLException {
        String query = "DELETE FROM reservation WHERE utilisateur = ? AND service_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, utilisateur.toLowerCase());  // Enforce consistent casing
            ps.setInt(2, serviceId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No reservation was deleted. User: " + utilisateur + ", Service ID: " + serviceId);
            }
        } catch (SQLException e) {
            // Log securely (avoid exposing sensitive data in production)
            System.err.println("SQL Error: " + e.getMessage());
            throw e;  // Rethrow to propagate the error
        }
    }

    // ✅ Récupérer toutes les réservations d'un utilisateur (par nom)
    public List<reserve> getReservedServicesByUser(String utilisateur) throws SQLException {
        List<reserve> reservations = new ArrayList<>();
        String req = "SELECT r.*, s.nom AS service_nom, s.description AS service_description, s.prix AS service_prix, s.image_url AS service_image " +
                "FROM reservation r JOIN service s ON r.service_id = s.id WHERE r.utilisateur = ?";

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, utilisateur);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                reserve res = new reserve();
                res.setId(rs.getInt("id"));
                res.setEvent(new Event(rs.getInt("event_id")));
                res.setService(new ServiceItem(
                        rs.getInt("service_id"),
                        rs.getString("service_nom"),
                        rs.getString("service_description"),
                        rs.getDouble("service_prix"),
                        rs.getString("service_image"),
                        utilisateur
                ));
                res.setDateReservation(rs.getDate("date_reservation"));
                res.setPrixTotal(rs.getDouble("prix_total"));
                res.setUtilisateur(utilisateur);
                System.out.println("🔄 Loaded service: " + rs.getString("service_nom") + " for user: " + utilisateur);

                reservations.add(res);
            }
        }
        return reservations;
    }
}
