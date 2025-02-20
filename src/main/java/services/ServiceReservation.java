package services;

import entities.reserve;
import entities.Event;
import entities.ServiceItem;
import org.Wedding.utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceReservation implements IService<reserve> {
    private Connection connection;

    public ServiceReservation() {
        this.connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public int ajouter(reserve reservation) throws SQLException {
        // Validate foreign keys first
        validateEventExists(reservation.getEvent().getId());
        validateServiceExists(reservation.getService().getId());

        String req = "INSERT INTO reservation (event_id, service_id, date_reservation, statut, prix_total) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, reservation.getEvent().getId());
            ps.setInt(2, reservation.getService().getId());
            ps.setDate(3, new java.sql.Date(reservation.getDateReservation().getTime()));
            ps.setString(4, reservation.getStatut().name());
            ps.setDouble(5, reservation.getPrixTotal());

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
            return -1;
        }
    }

    private void validateEventExists(int eventId) throws SQLException {
        String checkEvent = "SELECT id FROM event WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(checkEvent)) {
            ps.setInt(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Event with ID " + eventId + " does not exist");
                }
            }
        }
    }

    private void validateServiceExists(int serviceId) throws SQLException {
        String checkService = "SELECT id FROM service WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(checkService)) {
            ps.setInt(1, serviceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Service with ID " + serviceId + " does not exist");
                }
            }
        }
    }

    @Override
    public void modifier(reserve reservation) throws SQLException {
        validateEventExists(reservation.getEvent().getId());
        validateServiceExists(reservation.getService().getId());

        String req = "UPDATE reservation SET event_id=?, service_id=?, date_reservation=?, statut=?, prix_total=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, reservation.getEvent().getId());
            ps.setInt(2, reservation.getService().getId());
            ps.setDate(3, new java.sql.Date(reservation.getDateReservation().getTime()));
            ps.setString(4, reservation.getStatut().name());
            ps.setDouble(5, reservation.getPrixTotal());
            ps.setInt(6, reservation.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM reservation WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }


    @Override
    public List<reserve> afficher() throws SQLException {
        List<reserve> reservations = new ArrayList<>();
        String req = "SELECT * FROM reservation";  // Vérifiez que ce nom correspond à la table dans la base
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(req)) {
            while (rs.next()) {
                reserve res = new reserve();
                res.setId(rs.getInt("id"));
                // Chargement complet des événements et services associés
                res.setEvent(loadEvent(rs.getInt("event_id")));
                res.setService(loadService(rs.getInt("service_id")));
                res.setDateReservation(rs.getDate("date_reservation"));
                res.setStatut(reserve.StatutReservation.fromString(rs.getString("statut")));
                res.setPrixTotal(rs.getDouble("prix_total"));
                reservations.add(res);
            }
        }
        return reservations;
    }

    private Event loadEvent(int eventId) throws SQLException {
        String req = "SELECT * FROM event WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Event(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getDate("date"),
                            rs.getString("lieu"),
                            rs.getString("description"),
                            rs.getString("statut"),
                            rs.getString("image_url")
                    );
                }
            }
        }
        throw new SQLException("Event not found with ID: " + eventId);
    }

    private ServiceItem loadService(int serviceId) throws SQLException {
        String req = "SELECT * FROM service WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, serviceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ServiceItem(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("description"),
                            rs.getDouble("prix"),
                            rs.getString("image_url")
                    );
                }
            }
        }
        throw new SQLException("Service not found with ID: " + serviceId);
    }

    public reserve getClass(String newVal) {
        return null;
    }
}