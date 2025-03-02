package org.example.services;

import org.example.entities.Avis;
import org.example.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AvisService {
    private  final Connection connection ;

    public AvisService() {
        this.connection = MyDatabase.getInstance().getConnection();
    }

    // Ajouter un avis
    public Avis ajouterAvis(Avis avis) throws SQLException {
        String req = "INSERT INTO avis (idheb, note, commentaire, date_creation) VALUES ( ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, avis.getIdheb());
            preparedStatement.setInt(2, avis.getNote());
            preparedStatement.setString(3, avis.getCommentaire());
            preparedStatement.setTimestamp(4, new Timestamp(avis.getDateCreation().getTime()));

            preparedStatement.executeUpdate();
            System.out.println("✅ Avis ajouté avec succès !");
        }
        return avis;
    }

    // Récupérer les avis d'un hébergement
    public List<Avis> getAvisByHebergement(int idheb) throws SQLException {
        List<Avis> avisList = new ArrayList<>();
        String req = "SELECT * FROM avis WHERE idheb = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, idheb);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Avis avis = new Avis(
                        rs.getInt("idheb"),
                        rs.getInt("note"),
                        rs.getString("commentaire"),
                        rs.getTimestamp("date_creation")
                );
                avisList.add(avis);
            }
        }
        return avisList;
    }

    // Supprimer un avis
    public void supprimerAvis(int id) throws SQLException {
        String req = "DELETE FROM avis WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            System.out.println("✅ Avis supprimé avec succès !");
        }
    }
    public double getAverageRating(int idheb) throws SQLException {
        String req = "SELECT AVG(note) as average FROM avis WHERE idheb = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, idheb);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                return rs.getDouble("average");
            }
        }
        return 0.0; // Retourner 0 si aucun avis n'est trouvé
    }
}