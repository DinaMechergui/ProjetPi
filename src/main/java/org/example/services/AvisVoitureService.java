package org.example.services;

import org.example.entities.AvisVoiture;
import org.example.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AvisVoitureService {
    private final Connection connection;
    private static final Logger logger = Logger.getLogger(AvisVoitureService.class.getName());

    public AvisVoitureService() {
        this.connection = MyDatabase.getInstance().getConnection();
    }

    // Ajouter un avis pour une voiture
    public AvisVoiture ajouterAvis(AvisVoiture avisVoiture) throws SQLException {

        String req = "INSERT INTO avis (idvoiture, note, commentaire, date_creation) VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, avisVoiture.getIdvoiture());
            preparedStatement.setInt(2, avisVoiture.getNote());
            preparedStatement.setString(3, avisVoiture.getCommentaire());
            preparedStatement.setTimestamp(4, new Timestamp(avisVoiture.getDateCreation().getTime()));

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        avisVoiture.setId(generatedKeys.getInt(1)); // Récupérer l'ID généré
                    }
                }
                logger.log(Level.INFO, "✅ Avis pour voiture ajouté avec succès !");
            } else {
                logger.log(Level.WARNING, "⚠️ Aucune ligne affectée lors de l'ajout de l'avis.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Erreur lors de l'ajout de l'avis : " + e.getMessage(), e);
            throw e;
        }
        return avisVoiture;
    }

    // Récupérer les avis d'une voiture
    public List<AvisVoiture> getAvisByVoiture(int idvoiture) throws SQLException {
        List<AvisVoiture> avisList = new ArrayList<>();
        String req = "SELECT * FROM avis WHERE idvoiture = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, idvoiture);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    AvisVoiture avisVoiture = new AvisVoiture(
                            rs.getInt("idvoiture"),
                            rs.getInt("note"),
                            rs.getString("commentaire"),
                            rs.getTimestamp("date_creation")
                    );
                    avisList.add(avisVoiture);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Erreur lors de la récupération des avis : " + e.getMessage(), e);
            throw e;
        }
        return avisList;
    }

    // Supprimer un avis
    public boolean supprimerAvis(int id) throws SQLException {
        String req = "DELETE FROM avis WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, id);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                logger.log(Level.INFO, "✅ Avis supprimé avec succès !");
                return true;
            } else {
                logger.log(Level.WARNING, "⚠️ Aucun avis trouvé avec l'ID : " + id);
                return false;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Erreur lors de la suppression de l'avis : " + e.getMessage(), e);
            throw e;
        }
    }

    // Calculer la note moyenne d'une voiture
    public double getAverageRating(int idvoiture) throws SQLException {
        String req = "SELECT AVG(note) as average FROM avis WHERE idvoiture = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, idvoiture);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("average");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Erreur lors du calcul de la note moyenne : " + e.getMessage(), e);
            throw e;
        }
        return 0.0; // Retourner 0 si aucun avis n'est trouvé
    }
}