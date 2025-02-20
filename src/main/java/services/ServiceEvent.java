package services;

import entities.Event;
import org.Wedding.utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceEvent implements IService<Event> {
    private Connection connection;

    public ServiceEvent() {
        this.connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public int ajouter(Event event) throws SQLException {
        String req = "INSERT INTO event (nom, date, lieu, description, statut, image_url) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, event.getNom());
            ps.setDate(2, new java.sql.Date(event.getDate().getTime()));
            ps.setString(3, event.getLieu());
            ps.setString(4, event.getDescription());
            ps.setString(5, event.getStatut().name());
            ps.setString(6, event.getImageUrl());

            ps.executeUpdate();
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
            return -1;
        }
    }

    @Override
    public void modifier(Event event) throws SQLException {
        String req = "UPDATE event SET nom=?, date=?, lieu=?, description=?, statut=?, image_url=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, event.getNom());
            ps.setDate(2, new java.sql.Date(event.getDate().getTime()));
            ps.setString(3, event.getLieu());
            ps.setString(4, event.getDescription());
            ps.setString(5, event.getStatut().name());
            ps.setString(6, event.getImageUrl());
            ps.setInt(7, event.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM event WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override

    public List<Event> afficher() throws SQLException {
        List<Event> events = new ArrayList<>();
        String req = "SELECT * FROM event";  // Vérifiez que ce nom correspond à la table dans la base
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(req)) {
            while (rs.next()) {
                events.add(new Event(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getDate("date"),
                        rs.getString("lieu"),
                        rs.getString("description"),
                        rs.getString("statut"),
                        rs.getString("image_url")
                ));
            }
        }
        return events;
    }
}