package org.example.services;

import javafx.scene.image.Image;
import org.example.entities.Hebergement;
import Wedding.utils.MyDatabase;

import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceHebergement implements IHebergement {
    private final Connection connection;

    public ServiceHebergement() {
        this.connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Hebergement hebergement) throws SQLException {
        String req = "INSERT INTO hebergement (nom, adresse, prixParNuit, disponible, imageUrl) VALUES ('"
                + hebergement.getNom() + "', '"
                + hebergement.getAdresse() + "', "
                + hebergement.getPrixParNuit() + ", "
                + hebergement.isDisponible() + ", '"
                + hebergement.getImageUrl() + "')";

        Statement statement = this.connection.createStatement();
        statement.executeUpdate(req);
        System.out.println("✅ Hébergement ajouté avec succès !");
    }

    @Override
    public void modifier(Hebergement hebergement) throws SQLException {
        String req = "UPDATE hebergement SET nom = ?, adresse = ?, prixParNuit = ?, disponible = ?, imageUrl = ? WHERE idheb = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setString(1, hebergement.getNom());
            preparedStatement.setString(2, hebergement.getAdresse());
            preparedStatement.setDouble(3, hebergement.getPrixParNuit());
            preparedStatement.setBoolean(4, hebergement.isDisponible());
            preparedStatement.setString(5, hebergement.getImageUrl()); // Ajout du paramètre imageUrl
            preparedStatement.setInt(6, hebergement.getIdheb());

            preparedStatement.executeUpdate();
            System.out.println("✅ Hébergement modifié avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification de l'hébergement : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM hebergement WHERE idheb = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, id);

            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("✅ Hébergement supprimé avec succès !");
            } else {
                System.out.println("⚠ Aucun hébergement trouvé avec cet ID !");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression de l'hébergement : " + e.getMessage());
        }
    }

    public List<Hebergement> afficher() throws SQLException {
        List<Hebergement> hebergements = new ArrayList<>();
        String req = "SELECT * FROM hebergement"; // Ajout de imageUrl
        Statement statement = this.connection.createStatement();
        ResultSet rs = statement.executeQuery(req);

        while (rs.next()) {
            Hebergement hebergement = new Hebergement(
                    rs.getInt("idheb"),
                    rs.getString("nom"),
                    rs.getString("adresse"),
                    rs.getDouble("prixParNuit"),
                    rs.getBoolean("disponible"),
                    rs.getString("imageUrl") // Récupération de l'URL de l'image
            );
            hebergements.add(hebergement);
        }
        System.out.println("🏨 Hébergements affichés : " + hebergements);
        return hebergements;
    }

    public List<Hebergement> getAllHebergements() throws SQLException {
        List<Hebergement> hebergements = new ArrayList<>();
        String query = "SELECT idheb, nom, adresse, prixParNuit, disponible, imageUrl FROM hebergement"; // Ajout de imageUrl

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            hebergements.add(new Hebergement(
                    rs.getInt("idheb"),
                    rs.getString("nom"),
                    rs.getString("adresse"),
                    rs.getDouble("prixParNuit"),
                    rs.getBoolean("disponible"),
                    rs.getString("imageUrl") // Ajout de l'URL de l'image
            ));
        }
        return hebergements;
    }
}
