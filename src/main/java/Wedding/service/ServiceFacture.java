package Wedding.service;

import Wedding.entities.Facture;
import Wedding.utils.MyDatabase;


import java.sql.*;

public class ServiceFacture {
    private Connection connection;

    public ServiceFacture() {
        connection = MyDatabase.getInstance().getConnection();
    }

    public void ajouterFacture(Facture facture) throws SQLException {
        System.out.println("Tentative d'ajout de la facture pour la commande ID : " + facture.getCommande().getId());
        String query = "INSERT INTO facture (commande_id, date_facture, total) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, facture.getCommande().getId());
            ps.setTimestamp(2, Timestamp.valueOf(facture.getDateFacture()));
            ps.setDouble(3, facture.getTotal());

            int rowsInserted = ps.executeUpdate();
            System.out.println("Facture insérée ? " + (rowsInserted > 0));

            // Récupérer l'ID généré
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    facture.setId(generatedKeys.getInt(1));
                    System.out.println("Facture créée avec ID : " + facture.getId());
                } else {
                    System.out.println("Aucun ID généré pour la facture.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion de la facture : " + e.getMessage());
            throw e; // Relancer l'exception pour la gérer ailleurs
        }
    }    public Facture getFactureByCommandeId(int commandeId) throws SQLException {
        String query = "SELECT * FROM facture WHERE commande_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, commandeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Facture trouvée pour la commande ID : " + commandeId);
                    return new Facture(
                            rs.getInt("id"),
                            null,  // La commande sera récupérée ailleurs si nécessaire
                            rs.getTimestamp("date_facture").toLocalDateTime(),
                            rs.getDouble("total")
                    );
                } else {
                    System.out.println("Aucune facture trouvée pour la commande ID : " + commandeId);
                    return null;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la facture : " + e.getMessage());
            throw e; // Relancer l'exception pour la gérer ailleurs
        }
    }}
