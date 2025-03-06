package org.example.services;

import org.example.entities.Voiture;
import Wedding.utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceVoiture implements IService<Voiture> {
    private final Connection connection;

    public ServiceVoiture() {
        this.connection = MyDatabase.getInstance().getConnection();
    }
    @Override

    public void ajouter(Voiture voiture) throws SQLException {
        String req = "INSERT INTO voiture (marque, prix, disponible, imageUrl) VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setString(1, voiture.getMarque());
            preparedStatement.setFloat(2, voiture.getPrix());
            preparedStatement.setBoolean(3, voiture.isDisponible());
            preparedStatement.setString(4, voiture.getImageUrl());

            preparedStatement.executeUpdate();
            System.out.println("🚗 Voiture ajoutée avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout de la voiture : " + e.getMessage());
        }
    }




    @Override
    public void modifier(Voiture voiture) throws SQLException {

        String req = "UPDATE voiture SET marque = ?, prix = ?, disponible = ? WHERE idvoiture = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            // On définit les paramètres de la requête
            preparedStatement.setString(1, voiture.getMarque());
            preparedStatement.setDouble(2, voiture.getPrix());
            preparedStatement.setBoolean(3, voiture.isDisponible());
            preparedStatement.setInt(4, voiture.getIdvoiture());


            preparedStatement.executeUpdate();
            System.out.println("✅ Voiture modifiée avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification de la voiture : " + e.getMessage());
        }

    }
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM voiture WHERE idvoiture = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            // Définition du paramètre de l'ID
            preparedStatement.setInt(1, id);

            // Exécution de la requête
            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("✅ Voiture supprimée avec succès !");
            } else {
                System.out.println("⚠ Aucune voiture trouvée avec cet ID !");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression de la voiture : " + e.getMessage());
        }
    }

    public List<Voiture> getAllVoitures() throws SQLException {
        List<Voiture> voitures = new ArrayList<>();
        String query = "SELECT marque, prix, disponible FROM voiture";
        ;
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            voitures.add(new Voiture(0,(float) rs.getDouble("prix"),
                    rs.getString("marque"),  // 🔹 Conversion double → float
                                rs.getBoolean("disponible"),
                    rs.getString("imageUrl")
                        ));

        }
        return voitures;
    }
    public List<Voiture> afficher() throws SQLException {
        List<Voiture> voitures = new ArrayList<>();
        String req = "SELECT idvoiture, marque, prix, disponible , imageUrl FROM voiture";
        Statement statement = this.connection.createStatement();
        ResultSet rs = statement.executeQuery(req);

        while (rs.next()) {
            Voiture voiture = new Voiture(
                    rs.getInt("idvoiture"),
                    rs.getFloat("prix"),
                    rs.getString("marque"),

                    rs.getBoolean("disponible"),
                    rs.getString("imageUrl")
            );
            voitures.add(voiture);
        }
        System.out.println("🚗 Voitures affichées : " + voitures);
        return voitures;
    }
    // Méthode pour recommander des voitures en fonction de la marque, du prix et de la disponibilité
    public List<Voiture> recommanderVoitures(String marque, double prixMin, double prixMax, boolean disponible) throws SQLException {
        List<Voiture> recommandations = new ArrayList<>();
        String req = "SELECT * FROM voiture WHERE marque LIKE ? AND prix BETWEEN ? AND ? AND disponible = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setString(1, "%" + marque + "%"); // Recherche par marque (partielle)
            preparedStatement.setDouble(2, prixMin); // Prix minimum
            preparedStatement.setDouble(3, prixMax); // Prix maximum
            preparedStatement.setBoolean(4, disponible); // Disponibilité

            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Voiture voiture = new Voiture(
                        rs.getInt("idvoiture"),

                        rs.getFloat("prix"),
                        rs.getString("marque"),
                        rs.getBoolean("disponible"),
                        rs.getString("imageUrl") // Ajoutez d'autres champs si nécessaire
                );
                recommandations.add(voiture);
            }
        }

        return recommandations;
    }


}






