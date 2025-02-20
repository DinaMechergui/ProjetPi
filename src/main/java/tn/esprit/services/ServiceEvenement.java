package tn.esprit.services;

import tn.esprit.entities.Evenement;
import tn.esprit.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceEvenement {
    private final Connection connection;

    public ServiceEvenement() {
        this.connection = MyDatabase.getInstance().getConnection();
    }

    // Ajouter un événement
    public void ajouter(Evenement evenement) throws SQLException {
        String req = "INSERT INTO evenement (nom, lieu, date) VALUES ('"
                + evenement.getNom() + "', '"
                + evenement.getLieu() + "', '"
                + evenement.getDate() + "')";


        Statement statement = this.connection.createStatement();
        statement.executeUpdate(req);
        System.out.println("🚗 Voiture ajoutée avec succès !");

    }

    // Modifier un événement
    public void modifier(int id, String nom, String lieu, String date) throws SQLException {
        String req = "UPDATE evenement SET nom = ?, lieu = ?, date = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, nom);
            ps.setString(2, lieu);
            ps.setString(3, date);
            ps.setInt(4, id);
            ps.executeUpdate();
        }
    }


    // Supprimer un événement
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM evenement WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, id);

            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("✅ Événement supprimé avec succès !");
            } else {
                System.out.println("⚠ Aucun événement trouvé avec cet ID !");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression de l'événement : " + e.getMessage());
        }
    }


    // Afficher tous les événements
    public List<Evenement> afficher() throws SQLException {
        String req = "SELECT * FROM evenement";
        List<Evenement> evenements = new ArrayList<>();

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(req)) {

            while (rs.next()) {
                Evenement evenement = new Evenement(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("lieu"),
                        rs.getString("date") // Correction : Date est une String
                );
                evenements.add(evenement);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'affichage des événements : " + e.getMessage());
        }

        return evenements;
    }
    public void afficherEvenements() {
        List<Evenement> evenements = getAllEvenements(); // 🔥 Maintenant, cette méthode est reconnue !

        if (evenements.isEmpty()) {
            System.out.println("⚠ Aucun événement trouvé !");
        } else {
            System.out.println("📅 Liste des événements :");
            for (Evenement evenement : evenements) {
                System.out.println("ID: " + evenement.getId() +
                        " | Nom: " + evenement.getNom() +
                        " | Lieu: " + evenement.getLieu() +
                        " | Date: " + evenement.getDate());
            }
        }
    }

    public List<Evenement> getAllEvenements() {

        List<Evenement> evenements = new ArrayList<>();
        String query = "SELECT * FROM evenement";

        try (Connection conn = MyDatabase.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Evenement evenement = new Evenement(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("lieu"),
                        rs.getString("date")
                );
                evenements.add(evenement);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des événements : " + e.getMessage());
        }

        return evenements;
    }
    public int getIdByName(String nomEvenement) {
        int id = -1;
        String query = "SELECT id FROM evenement WHERE nom = ?";

        try {
            Connection conn = MyDatabase.getInstance().getConnection();
            if (conn == null || conn.isClosed()) {
                System.err.println("❌ La connexion est fermée. Impossible d'exécuter la requête.");
                return id;
            }

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, nomEvenement);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    id = rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération de l'ID : " + e.getMessage());
        }

        return id;
    }


    public boolean existeDeja(String nom, String lieu) throws SQLException {
        String req = "SELECT COUNT(*) FROM evenement WHERE nom = ? AND lieu = ?";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setString(1, nom);
            pst.setString(2, lieu);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // S'il y a au moins 1 événement, retourne true
            }
        }
        return false;
    }

}




