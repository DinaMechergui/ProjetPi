package org.example.services;

import org.example.entities.Avis;
import Wedding.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AvisService {
    private final Connection connection;

    public AvisService() {
        this.connection = MyDatabase.getInstance().getConnection();
        if (this.connection == null) {
            throw new RuntimeException("❌ La connexion à la base de données est null.");
        }
    }

    // Ajouter un avis
    public Avis ajouterAvis(Avis avis) throws SQLException {
        String req = "INSERT INTO avis (idheb, note, commentaire, date_creation, iduser) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, avis.getIdheb());
            preparedStatement.setInt(2, avis.getNote());
            preparedStatement.setString(3, avis.getCommentaire());
            preparedStatement.setDate(4, new java.sql.Date(avis.getDateCreation().getTime()));
            preparedStatement.setInt(5, avis.getIduser());

            preparedStatement.executeUpdate();
            System.out.println("✅ Avis ajouté avec succès !");
        }
        return avis;
    }

    // Récupérer les avis d'un hébergement
    public List<Avis> getAvisByHebergement(int idheb) throws SQLException {
        List<Avis> avisList = new ArrayList<>();
        String req = "SELECT a.*, u.nom, u.prenom FROM avis a " +
                "JOIN user u ON a.iduser = u.id " +
                "WHERE a.idheb = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, idheb);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Avis avis = new Avis(
                        rs.getInt("idheb"),
                        rs.getInt("note"),
                        rs.getString("commentaire"),
                        rs.getDate("date_creation"),
                        rs.getInt("iduser")
                );
                // Récupérer le nom et le prénom de l'utilisateur
                avis.setNomUtilisateur(rs.getString("nom"));
                avis.setPrenomUtilisateur(rs.getString("prenom"));
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

    // Calculer la note moyenne d'un hébergement
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