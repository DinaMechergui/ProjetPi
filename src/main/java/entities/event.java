package entities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

public class event {
    private int id;
    private String nom;
    private Date date;
    private String lieu;
    private String description;
    private Statut statut;
    private List<service> services = new ArrayList<>();
    private Connection connection;  // Assuming you have a connection object available

    public event() {

    }

    public event(int eventId) {
    }

    public enum Statut {
        EN_PREPARATION, CONFIRME, ANNULE;

        public static Statut fromString(String statut) {
            if (statut == null) {
                throw new IllegalArgumentException("Statut invalide : null");
            }
            try {
                return Statut.valueOf(statut.trim().replace(" ", "_").toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Statut inconnu : " + statut);
            }
        }

    }

    // Constructor for initializing event with details
    public event(int id, String nom, Date date, String lieu, String description, String statut) {
        this.id = id;
        this.nom = nom;
        this.date = date;
        this.lieu = lieu;
        this.description = description;
        this.statut = Statut.fromString(statut);
    }

    public event(int id, Connection connection) throws SQLException {
        this.id = id;
        this.connection = connection; // Initialize connection
        String req = "SELECT * FROM event WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            this.nom = rs.getString("nom");
            this.date = rs.getDate("date");
            this.lieu = rs.getString("lieu");
            this.description = rs.getString("description");
            this.statut = Statut.fromString(rs.getString("statut"));
        } else {
            System.out.println("Event with ID " + id + " not found.");
        }
    }


    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }

    public List<service> getServices() { return services; }
    public void setServices(List<service> services) { this.services = services; }

    public void ajouterService(service service) { this.services.add(service); }

    @Override
    public String toString() {
        return "Event{id=" + id + ", nom='" + nom + "', date=" + date + ", lieu='" + lieu + "', statut=" + statut + "}";
    }
}
