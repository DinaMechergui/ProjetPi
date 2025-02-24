package entities;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Event {
    private int id;
    private String nom;
    private Date date;
    private String lieu;
    private String description;
    private Statut statut;
    private String imageUrl;
    private List<ServiceItem> ServiceItems = new ArrayList<>();
    private Connection connection;

    public Event(int eventId) {
        this.id = eventId; // Initialize ID directly
    }



    public enum Statut {
        EN_PREPARATION, CONFIRME, ANNULE;

        public static Statut fromString(String statut) {
            if (statut == null) return EN_PREPARATION;
            try {
                return valueOf(statut.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                return EN_PREPARATION;
            }
        }
    }

    // Constructors
    public Event() {}

    public Event(int id, String nom, Date date, String lieu, String description, String statut, String imageUrl) {
        this.id = id;
        this.nom = nom;
        this.date = date;
        this.lieu = lieu;
        this.description = description;
        this.statut = Statut.fromString(statut);
        this.imageUrl = imageUrl;
    }

    // Database loading constructor
    public Event(int id, Connection connection) throws SQLException {
        this.connection = connection;
        String req = "SELECT * FROM event WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                this.id = id;
                this.nom = rs.getString("nom");
                this.date = rs.getDate("date");
                this.lieu = rs.getString("lieu");
                this.description = rs.getString("description");
                this.statut = Statut.fromString(rs.getString("statut"));
                this.imageUrl = rs.getString("image_url");
            } else {
                throw new SQLException("Event not found with ID: " + id);
            }
        }
    }

    private List<ServiceItem> loadServices() throws SQLException {
        List<ServiceItem> ServiceItems = new ArrayList<>();
        String req = "SELECT s.* FROM service s " +
                "JOIN event_service es ON s.id = es.service_id " +
                "WHERE es.event_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, this.id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ServiceItems.add(new ServiceItem(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getDouble("prix"),
                        rs.getString("image_url")
                ));
            }
        }
        return ServiceItems;
    }

    // Getters/Setters
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public List<ServiceItem> getServices() {
        return ServiceItems;
    }

    public void setServices(List<ServiceItem> ServiceItems) {
        this.ServiceItems = ServiceItems;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    @Override
    public String toString() {
        return this.nom;  // Retourne seulement le nom de l'événement
    }

}