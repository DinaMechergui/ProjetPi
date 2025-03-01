package tn.esprit.tacheuser.service;

import tn.esprit.tacheuser.models.Reponse;
import tn.esprit.tacheuser.utils.MySQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.sql.Statement;
import java.sql.ResultSet;

public class ReponseService {
    private final Connection connection;

    public ReponseService() {
        this.connection = MySQLConnection.getInstance().getConnection();
    }

    public void addReponse(Reponse reponse) {
        String query = "INSERT INTO reponse (reclamation_id, message) VALUES (?, ?)";
        String updateReclamationQuery = "UPDATE reclamation SET statut = 'traité' WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query);
             PreparedStatement updateStatement = connection.prepareStatement(updateReclamationQuery)) {

            // Insérer la réponse
            statement.setInt(1, reponse.getReclamationId());
            statement.setString(2, reponse.getMessage());
            statement.executeUpdate();

            // Mettre à jour le statut de la réclamation
            updateStatement.setInt(1, reponse.getReclamationId());
            updateStatement.executeUpdate();

            System.out.println("✅ Réponse ajoutée et réclamation traitée !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout de la réponse : " + e.getMessage());
        }
    }
    public List<Reponse> getAllReponses() {
        List<Reponse> reponses = new ArrayList<>();
        String query = "SELECT * FROM reponse";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Reponse reponse = new Reponse(
                        resultSet.getInt("reclamation_id"),
                        resultSet.getString("message")
                );
                reponses.add(reponse);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des réponses : " + e.getMessage());
        }

        return reponses;
    }
    public void updateReponse(int id, String message) {
        String query = "UPDATE reponse SET message = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, message);
            statement.setInt(2, id);
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("✅ Réponse mise à jour !");
            } else {
                System.out.println("⚠ Aucune réponse trouvée avec cet ID.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour de la réponse : " + e.getMessage());
        }
    }
    public void deleteReponse(int id) {
        String query = "DELETE FROM reponse WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("✅ Réponse supprimée !");
            } else {
                System.out.println("⚠ Aucune réponse trouvée avec cet ID.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression de la réponse : " + e.getMessage());
        }
    }

}
