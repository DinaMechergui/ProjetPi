package entities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServiceItem {
    private int id;
    private String nom;
    private String description;
    private double prix;
    private String imageUrl;
    private String utilisateur;

    public ServiceItem() {}

    public ServiceItem(int id, String nom, String description, double prix, String imageUrl,String utilisateur) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.imageUrl = imageUrl;
        this.utilisateur = utilisateur;
    }

    public ServiceItem(int id, Connection connection) throws SQLException {
        String req = "SELECT * FROM service WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                this.id = id;
                this.nom = rs.getString("nom");
                this.description = rs.getString("description");
                this.prix = rs.getDouble("prix");
                this.imageUrl = rs.getString("image_url");
                this.utilisateur = rs.getString("utilisateur");
            } else {
                throw new SQLException("Service not found with ID: " + id);
            }
        }
    }
    public ServiceItem(int id) {
        this.id = id;
    }
    // Getters/Setters
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    @Override
    public String toString() {
        return this.nom;  // Retourne seulement le nom du service
    }
    public String getUtilisateur() { return utilisateur; }
    public void setUtilisateur(String utilisateur) { this.utilisateur = utilisateur; }

}