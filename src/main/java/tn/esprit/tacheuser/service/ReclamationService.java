package tn.esprit.tacheuser.service;

import tn.esprit.tacheuser.utils.MySQLConnection;
import tn.esprit.tacheuser.models.Reclamation;
import tn.esprit.tacheuser.utils.SessionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import tn.esprit.tacheuser.models.User;

public class ReclamationService {

    // Ajouter une réclamation dans la base de données
    public boolean addReclamation(Reclamation reclamation) {
        if (!SessionManager.isUserLoggedIn()) {
            System.err.println("❌ L'utilisateur n'est pas connecté !");
            return false;
        }

        User user = SessionManager.getUser(); // Récupérer l'utilisateur connecté
        reclamation.setUserId(user.getId()); // Assigner l'ID de l'utilisateur à la réclamation

        String query = "INSERT INTO reclamations (user_id, sujet, description, statut) VALUES (?, ?, ?, ?)";
        try (Connection conn = MySQLConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, reclamation.getUserId());
            stmt.setString(2, reclamation.getSujet());
            stmt.setString(3, reclamation.getDescription());
            stmt.setString(4, reclamation.getStatut());

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("✅ Réclamation ajoutée pour " + user.getNom());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout de la réclamation : " + e.getMessage());
        }
        return false;
    }

    // Récupérer toutes les réclamations
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
            System.err.println("❌ Erreur lors de la récupération des réclamations : " + e.getMessage());
        }
        return reclamations;
    }

    // Mettre à jour une réclamation
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
                return true;
            } else {
                System.out.println("⚠️ Aucune réclamation trouvée avec cet ID !");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour : " + e.getMessage());
        }
        return false;
    }

    // Supprimer une réclamation par ID
    public boolean deleteReclamation(int id) {
        String query = "DELETE FROM reclamations WHERE id = ?";
        try (Connection conn = MySQLConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("✅ Réclamation supprimée avec succès !");
                return true;
            } else {
                System.out.println("⚠️ Aucune réclamation trouvée avec cet ID !");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression de la réclamation : " + e.getMessage());
        }
        return false;
    }

    // Récupérer les réclamations d'un utilisateur spécifique
    public List<Reclamation> getReclamationsByUserId(int userId) {
        List<Reclamation> reclamations = new ArrayList<>();
        String query = "SELECT id, user_id, sujet, description, statut FROM reclamations WHERE user_id = ?";

        try (Connection conn = MySQLConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

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
            System.err.println("❌ Erreur lors de la récupération des réclamations : " + e.getMessage());
        }
        return reclamations;
    }
    public void handleAddMotInterdit(String motInterdit) {
        if (motInterdit != null && !motInterdit.trim().isEmpty()) {
            String query = "INSERT INTO mots_interdits (mot) VALUES (?)";
            try (Connection conn = MySQLConnection.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, motInterdit);
                stmt.executeUpdate();
                System.out.println("✅ Mot interdit ajouté à la base de données.");
            } catch (SQLException e) {
                System.err.println("❌ Erreur lors de l'ajout du mot interdit : " + e.getMessage());
            }
        } else {
            System.err.println("❌ Le mot interdit ne peut pas être vide !");
        }
    }

    public class MotsInterditsService {

        // Méthode pour récupérer tous les mots interdits
        public List<String> getMotsInterdits() {
            List<String> motsInterdits = new ArrayList<>();
            String query = "SELECT mot FROM mots_interdits";

            try (Connection conn = MySQLConnection.getInstance().getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    motsInterdits.add(rs.getString("mot"));
                }

            } catch (SQLException e) {
                System.err.println("❌ Erreur lors de la récupération des mots interdits : " + e.getMessage());
            }

            return motsInterdits;
        }
    }
}
