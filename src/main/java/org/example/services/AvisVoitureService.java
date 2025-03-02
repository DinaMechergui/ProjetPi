package org.example.services;

import org.example.entities.AvisVoiture;
import org.example.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AvisVoitureService {
    private final Connection connection;

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
            }
            System.out.println("✅ Avis pour voiture ajouté avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout de l'avis : " + e.getMessage());
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
            System.err.println("❌ Erreur lors de la récupération des avis : " + e.getMessage());
            throw e;
        }
        return avisList;
    }

    // Supprimer un avis
    public void supprimerAvis(int id) throws SQLException {
        String req = "DELETE FROM avis WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, id);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Avis supprimé avec succès !");
            } else {
                System.out.println("⚠️ Aucun avis trouvé avec l'ID : " + id);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression de l'avis : " + e.getMessage());
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
            System.err.println("❌ Erreur lors du calcul de la note moyenne : " + e.getMessage());
            throw e;
        }
        return 0.0; // Retourner 0 si aucun avis n'est trouvé
    }
}