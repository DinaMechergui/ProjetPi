package Wedding.service;

import Wedding.entities.Commande;
import Wedding.entities.Facture;
import Wedding.utils.MyDatabase;

import java.sql.*;

import static Wedding.service.ServiceCommande.getCommandeById;

public class ServiceFacture {
    private static Connection connection;

    public ServiceFacture() {
        connection = MyDatabase.getInstance().getConnection();
    }

    public static void ajouterFacture(Facture facture, String utilisateur) throws SQLException {
        String query = "INSERT INTO facture (commande_id, date_facture, total, utilisateur, code_promo) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, facture.getCommande().getId());
            ps.setTimestamp(2, Timestamp.valueOf(facture.getDateFacture()));
            ps.setDouble(3, facture.getTotal());
            ps.setString(4, utilisateur);
            ps.setString(5, facture.getCodePromo()); // Ajout du code promo

            int rowsInserted = ps.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Facture insérée avec succès.");
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        facture.setId(generatedKeys.getInt(1));
                        System.out.println("Facture créée avec ID : " + facture.getId());
                    } else {
                        System.out.println("Erreur : aucun ID généré pour la facture.");
                    }
                }
            } else {
                System.out.println("Échec de l'insertion de la facture.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion de la facture : " + e.getMessage());
            throw e;
        }
    }

    public Facture getFactureByCommandeId(int commandeId, String utilisateur) throws SQLException {
        String query = "SELECT * FROM facture WHERE commande_id = ? AND utilisateur = ?";
        Facture facture = null;

        try {
            Connection connection = MyDatabase.getInstance().getConnection();
            if (connection == null || connection.isClosed()) {
                System.err.println("La connexion à la base de données est fermée ou invalide lors de la tentative d'accès.");
                return null; // ou gérer autrement
            }

            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setInt(1, commandeId);
                ps.setString(2, utilisateur);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Commande commande = getCommandeById(commandeId); // Récupérer la commande associée
                        facture = new Facture(
                                rs.getInt("id"),
                                commande,
                                rs.getTimestamp("date_facture").toLocalDateTime(),
                                rs.getString("utilisateur"),
                                rs.getDouble("total"),
                                rs.getString("code_promo")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération de la facture : " + e.getMessage());
            throw e; // Propager l'exception pour une gestion appropriée
        }

        return facture;
    }
}
