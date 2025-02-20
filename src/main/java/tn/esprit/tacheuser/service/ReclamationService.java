package tn.esprit.tacheuser.service;

import tn.esprit.tacheuser.utils.MySQLConnection;
import tn.esprit.tacheuser.models.Reclamation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReclamationService {

    public boolean addReclamation(Reclamation reclamation) {
        String query = "INSERT INTO reclamations (user_id, sujet, description, statut) VALUES (?, ?, ?, ?)";
        try (Connection conn = MySQLConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, reclamation.getUserId());
            stmt.setString(2, reclamation.getSujet());
            stmt.setString(3, reclamation.getDescription());
            stmt.setString(4, reclamation.getStatut());
            stmt.executeUpdate();
            System.out.println("✅ Réclamation ajoutée !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout : " + e.getMessage());
        }
        return false;
    }

    public List<Reclamation> getAllReclamations() {
        List<Reclamation> reclamations = new ArrayList<>();
        String query = "SELECT id, user_id, sujet, description, statut FROM reclamations";

        try (Connection conn = MySQLConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                reclamations.add(new Reclamation(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("sujet"),
                        rs.getString("description"),
                        rs.getString("statut")
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'affichage : " + e.getMessage());
        }
        return reclamations;
    }

    public boolean updateReclamation(Reclamation reclamation) {
        String query = "UPDATE reclamations SET sujet = ?, description = ?, statut = ? WHERE id = ?";
        try (Connection conn = MySQLConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, reclamation.getSujet());
            stmt.setString(2, reclamation.getDescription());
            stmt.setString(3, reclamation.getStatut());
            stmt.setInt(4, reclamation.getId());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Réclamation mise à jour avec succès !");
            } else {
                System.out.println("⚠️ Aucune réclamation trouvée avec cet ID !");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour : " + e.getMessage());
        }
        return false;
    }
    public void deleteReclamation(int id) {
        String query = "DELETE FROM reclamations WHERE id = ?";
        try (Connection conn = MySQLConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("✅ Réclamation supprimée avec succès !");
            } else {
                System.out.println("⚠️ Aucune réclamation trouvée avec cet ID !");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression : " + e.getMessage());
        }
    }

}
