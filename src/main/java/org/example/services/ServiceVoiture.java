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
    public  void ajouter(Voiture voiture) throws SQLException {
        String req = "INSERT INTO voiture (marque, prix, disponible) VALUES ('"
                + voiture.getMarque() + "', "
                + voiture.getPrix() + ", "
                + voiture.isDisponible() + ")";

        Statement statement = this.connection.createStatement();
        statement.executeUpdate(req);
        System.out.println("🚗 Voiture ajoutée avec succès !");
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
                                rs.getBoolean("disponible")
                        ));

        }
        return voitures;
    }
    public List<Voiture> afficher() throws SQLException {
        List<Voiture> voitures = new ArrayList<>();
        String req = "SELECT idvoiture, marque, prix, disponible FROM voiture";
        Statement statement = this.connection.createStatement();
        ResultSet rs = statement.executeQuery(req);

        while (rs.next()) {
            Voiture voiture = new Voiture(
                    rs.getInt("idvoiture"),
                    rs.getFloat("prix"),
                    rs.getString("marque"),

                    rs.getBoolean("disponible")
            );
            voitures.add(voiture);
        }
        System.out.println("🚗 Voitures affichées : " + voitures);
        return voitures;
    }

}






