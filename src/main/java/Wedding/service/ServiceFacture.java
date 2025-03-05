package Wedding.service;

import Wedding.entities.Commande;
import Wedding.entities.Facture;
import org.Wedding.utils.MyDatabase;


import java.sql.*;

public class ServiceFacture {
    private Connection connection;

    public ServiceFacture()
    {
        connection = MyDatabase.getInstance().getConnection();
    }

    public void ajouterFacture(Facture facture, String utilisateur) throws SQLException {
        System.out.println("Tentative d'ajout de la facture pour la commande ID : " + facture.getCommande().getId());
        String query = "INSERT INTO facture (commande_id, date_facture, total, utilisateur) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, facture.getCommande().getId());
            ps.setTimestamp(2, Timestamp.valueOf(facture.getDateFacture()));
            ps.setDouble(3, facture.getTotal());
            ps.setString(4, utilisateur);

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
    }
    public Facture getFactureByCommandeId(int commandeId, String utilisateur) throws SQLException {
        String query = "SELECT * FROM facture WHERE commande_id = ? AND utilisateur = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, commandeId);
            ps.setString(2, utilisateur);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Facture trouvée pour la commande ID : " + commandeId);

                    // Récupérer la commande associée (si nécessaire)
                    Commande commande = null; // Vous pouvez récupérer la commande ici si nécessaire

                    // Créer l'objet Facture avec tous les paramètres requis
                    return new Facture(
                            rs.getInt("id"),
                            commande,  // Passer la commande (ou null si non disponible)
                            rs.getTimestamp("date_facture").toLocalDateTime(),
                            rs.getString("utilisateur"),  // Passer l'utilisateur
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