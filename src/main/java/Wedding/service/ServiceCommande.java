package Wedding.service;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import Wedding.entities.Commande;
import Wedding.entities.Produit;
import Wedding.entities.Reservation;
import Wedding.utils.MyDatabase;
import entities.ServiceItem;
import javafx.util.Pair;
import tn.esprit.tacheuser.models.User;

public class ServiceCommande implements IServiceCommande {

    private static Connection connection;

    public ServiceCommande() {
        this.connection = MyDatabase.getInstance().getConnection();
        System.out.println("Connexion à la base de données : " + this.connection);
    }

    @Override
    public void removeProductFromCart(int commandeId, int produitId) throws SQLException {
        String sqlDelete = "DELETE FROM reservation1 WHERE commande_id = ? AND produit_id = ? AND statut = 'RESERVE'";
        try (PreparedStatement stmt = connection.prepareStatement(sqlDelete)) {
            stmt.setInt(1, commandeId);
            stmt.setInt(2, produitId);
            stmt.executeUpdate();
        }
    }

    @Override
    public int ajouterOuMettreAJourReservation(String utilisateur, Produit produit, int quantite) throws SQLException {
        if (produit.getStock() <= 0) {
            throw new SQLException("Le produit " + produit.getNom() + " est en rupture de stock.");
        }

        int idCommande = -1;
        double totalCommande = 0;

        // Check if a RESERVE command exists for the user
        String sqlCheckCommande = "SELECT id, total FROM commande WHERE utilisateur = ? AND statut = 'RESERVE'";
        try (PreparedStatement stmt = connection.prepareStatement(sqlCheckCommande)) {
            stmt.setString(1, utilisateur);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                idCommande = rs.getInt("id");
                totalCommande = rs.getDouble("total");
            }
        }

        // If no RESERVE command exists, create a new one
        if (idCommande == -1) {
            String sqlInsertCommande = "INSERT INTO commande (utilisateur, date, total, statut) VALUES (?, ?, ?, 'RESERVE')";
            try (PreparedStatement stmt = connection.prepareStatement(sqlInsertCommande, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, utilisateur);
                stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                stmt.setDouble(3, produit.getPrix() * quantite);
                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    idCommande = rs.getInt(1);
                }
            }
        }

        // Check if the product already exists in the reservation
        String sqlCheckProduit = "SELECT quantite FROM reservation1 WHERE commande_id = ? AND produit_id = ?";
        boolean produitExiste = false;
        int nouvelleQuantite = quantite;

        try (PreparedStatement stmt = connection.prepareStatement(sqlCheckProduit)) {
            stmt.setInt(1, idCommande);
            stmt.setInt(2, produit.getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                produitExiste = true;
                nouvelleQuantite = rs.getInt("quantite") + quantite;
            }
        }

        // Update or insert the product in the reservation
        if (produitExiste) {
            String sqlUpdateQuantite = "UPDATE reservation1 SET quantite = ? WHERE commande_id = ? AND produit_id = ?";
            try (PreparedStatement updateStmt = connection.prepareStatement(sqlUpdateQuantite)) {
                updateStmt.setInt(1, nouvelleQuantite);
                updateStmt.setInt(2, idCommande);
                updateStmt.setInt(3, produit.getId());
                updateStmt.executeUpdate();
            }
        } else {
            String sqlInsertProduit = "INSERT INTO reservation1 (commande_id, produit_id, quantite, statut) VALUES (?, ?, ?, 'RESERVE')";
            try (PreparedStatement insertStmt = connection.prepareStatement(sqlInsertProduit)) {
                insertStmt.setInt(1, idCommande);
                insertStmt.setInt(2, produit.getId());
                insertStmt.setInt(3, quantite);
                insertStmt.executeUpdate();
            }
        }

        // Update the total of the command
        String sqlUpdateTotal = "UPDATE commande SET total = total + ? WHERE id = ?";
        try (PreparedStatement updateTotalStmt = connection.prepareStatement(sqlUpdateTotal)) {
            updateTotalStmt.setDouble(1, produit.getPrix() * quantite);
            updateTotalStmt.setInt(2, idCommande);
            updateTotalStmt.executeUpdate();
        }

        return idCommande;
    }

    @Override
    public int ajouterReservation(Commande commande) throws SQLException {
        double total = 0;
        for (Reservation reservation : commande.getReservations()) {
            total += reservation.getProduit().getPrix() * reservation.getQuantite();
        }

        int commandeId = -1;

        // Check if a RESERVE command exists for the user
        String sqlCheckCommande = "SELECT id FROM commande WHERE utilisateur = ? AND statut = 'RESERVE'";
        try (PreparedStatement stmtCheck = connection.prepareStatement(sqlCheckCommande)) {
            stmtCheck.setString(1, commande.getUtilisateur());
            ResultSet rs = stmtCheck.executeQuery();
            if (rs.next()) {
                commandeId = rs.getInt("id");
            }
        }

        // If no RESERVE command exists, create a new one
        if (commandeId == -1) {
            String req = "INSERT INTO commande (utilisateur, date, total, statut) VALUES (?, ?, ?, 'RESERVE')";
            try (PreparedStatement preparedStatement = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, commande.getUtilisateur());
                preparedStatement.setTimestamp(2, Timestamp.valueOf(commande.getDateCommande()));
                preparedStatement.setDouble(3, total);
                preparedStatement.executeUpdate();

                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    commandeId = generatedKeys.getInt(1);
                }
            }
        }

        // Add or update products in the reservation
        for (Reservation reservation : commande.getReservations()) {
            Produit produit = reservation.getProduit();
            int quantite = reservation.getQuantite();

            String sqlCheckProduit = "SELECT quantite FROM reservation1 WHERE commande_id = ? AND produit_id = ?";
            try (PreparedStatement stmtCheckProduit = connection.prepareStatement(sqlCheckProduit)) {
                stmtCheckProduit.setInt(1, commandeId);
                stmtCheckProduit.setInt(2, produit.getId());
                ResultSet rsProduit = stmtCheckProduit.executeQuery();

                if (rsProduit.next()) {
                    int nouvelleQuantite = rsProduit.getInt("quantite") + quantite;
                    String sqlUpdateProduit = "UPDATE reservation1 SET quantite = ? WHERE commande_id = ? AND produit_id = ?";
                    try (PreparedStatement updateProduitStmt = connection.prepareStatement(sqlUpdateProduit)) {
                        updateProduitStmt.setInt(1, nouvelleQuantite);
                        updateProduitStmt.setInt(2, commandeId);
                        updateProduitStmt.setInt(3, produit.getId());
                        updateProduitStmt.executeUpdate();
                    }
                } else {
                    String reqReservation = "INSERT INTO reservation1 (commande_id, produit_id, quantite, statut) VALUES (?, ?, ?, 'RESERVE')";
                    try (PreparedStatement psInsert = connection.prepareStatement(reqReservation)) {
                        psInsert.setInt(1, commandeId);
                        psInsert.setInt(2, produit.getId());
                        psInsert.setInt(3, quantite);
                        psInsert.executeUpdate();
                    }
                }
            }
        }

        return commandeId;
    }

    public static Commande getCommandeById(int commandeId) throws SQLException {
        String query = "SELECT * FROM commande WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, commandeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Commande commande = new Commande();
                commande.setId(rs.getInt("id"));
                commande.setUtilisateur(rs.getString("utilisateur"));
                commande.setDateCommande(rs.getTimestamp("date").toLocalDateTime());
                commande.setStatut(rs.getString("statut"));
                commande.setTotal(rs.getDouble("total"));
                return commande;
            }
        }
        return null;
    }

    @Override
    public List<Pair<ServiceItem, LocalDate>> getServicesReserves(int commandeId) throws SQLException {
        List<Pair<ServiceItem, LocalDate>> servicesReserves = new ArrayList<>();
        String sql = "SELECT s.*, sr.date FROM service_reserve sr JOIN service s ON sr.service_id = s.id WHERE sr.commande_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, commandeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ServiceItem service = new ServiceItem(rs.getInt("id"), connection);
                LocalDate date = rs.getDate("date").toLocalDate();
                servicesReserves.add(new Pair<>(service, date));
            }
        }
        return servicesReserves;
    }

    @Override
    public void confirmerCommande(int idCommande, String utilisateur) throws SQLException {
        try {
            connection.setAutoCommit(false);

            // Retrieve reserved products in the command
            String sqlGetProduits = "SELECT produit_id, quantite FROM reservation1 WHERE commande_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sqlGetProduits)) {
                stmt.setInt(1, idCommande);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    int idProduit = rs.getInt("produit_id");
                    int quantite = rs.getInt("quantite");

                    // Check if stock is sufficient
                    String sqlCheckStock = "SELECT stock FROM produit WHERE id = ?";
                    try (PreparedStatement checkStmt = connection.prepareStatement(sqlCheckStock)) {
                        checkStmt.setInt(1, idProduit);
                        ResultSet stockRs = checkStmt.executeQuery();
                        if (stockRs.next()) {
                            int stock = stockRs.getInt("stock");
                            if (stock < quantite) {
                                throw new SQLException("Stock insuffisant pour le produit ID : " + idProduit);
                            }
                        }
                    }

                    // Reduce product stock
                    String sqlUpdateStock = "UPDATE produit SET stock = stock - ? WHERE id = ?";
                    try (PreparedStatement updateStmt = connection.prepareStatement(sqlUpdateStock)) {
                        updateStmt.setInt(1, quantite);
                        updateStmt.setInt(2, idProduit);
                        updateStmt.executeUpdate();
                    }
                }
            }

            // Update command status to "confirmée"
            String sqlUpdateCommande = "UPDATE commande SET statut = 'confirmée' WHERE id = ? AND utilisateur = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sqlUpdateCommande)) {
                stmt.setInt(1, idCommande);
                stmt.setString(2, utilisateur);
                stmt.executeUpdate();
            }

            connection.commit(); // Commit the transaction
        } catch (SQLException e) {
            connection.rollback(); // Rollback in case of error
            throw e;
        } finally {
            connection.setAutoCommit(true); // Re-enable auto-commit
        }
    }

    public static List<Pair<Produit, Integer>> getProduitsEtQuantitesDansPanier(int commandeId) throws SQLException {
        List<Pair<Produit, Integer>> produitsEtQuantites = new ArrayList<>();
        String query = "SELECT p.id, p.nom, p.description, p.prix, p.categorie, p.stock, p.imageUrl, r.quantite " +
                "FROM produit p " +
                "JOIN reservation1 r ON p.id = r.produit_id " +
                "WHERE r.commande_id = ? AND r.statut = 'RESERVE'";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, commandeId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Produit produit = new Produit(
                        resultSet.getInt("id"),
                        resultSet.getString("nom"),
                        resultSet.getString("description"),
                        resultSet.getDouble("prix"),
                        resultSet.getString("categorie"),
                        resultSet.getInt("stock"),
                        resultSet.getString("imageUrl")
                );
                int quantite = resultSet.getInt("quantite");
                produitsEtQuantites.add(new Pair<>(produit, quantite));
            }
        }
        return produitsEtQuantites;
    }

    @Override
    public void annulerReservation(int commandeId) throws SQLException {
        // Delete reserved products in the command
        String reqDeleteReservation = "DELETE FROM reservation1 WHERE commande_id = ?";
        try (PreparedStatement psDelete = connection.prepareStatement(reqDeleteReservation)) {
            psDelete.setInt(1, commandeId);
            psDelete.executeUpdate();
        }

        // Delete the reserved command
        String reqDeleteCommande = "DELETE FROM commande WHERE id = ? AND statut = 'reservé'";
        try (PreparedStatement psDeleteCommande = connection.prepareStatement(reqDeleteCommande)) {
            psDeleteCommande.setInt(1, commandeId);
            psDeleteCommande.executeUpdate();
        }
    }

    @Override
    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT r.id, r.commande_id, r.produit_id, r.quantite, r.statut, " +
                "p.nom AS produit_nom, c.id AS commande_id " +
                "FROM reservation1 r " +
                "JOIN produit p ON r.produit_id = p.id " +
                "JOIN commande c ON r.commande_id = c.id";

        try (PreparedStatement pst = connection.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Produit produit = new Produit();
                produit.setId(rs.getInt("produit_id"));
                produit.setNom(rs.getString("produit_nom"));

                Commande commande = new Commande();
                commande.setId(rs.getInt("commande_id"));

                String statutStr = rs.getString("statut");
                Reservation.StatutReservation statut = Reservation.StatutReservation.valueOf(statutStr);

                Reservation reservation = new Reservation(
                        rs.getLong("id"),
                        commande,
                        produit,
                        rs.getInt("quantite")
                );
                reservation.setStatut(statut);

                reservations.add(reservation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    @Override
    public void deleteReservation(int idReservation) {
        String query = "DELETE FROM reservation1 WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, idReservation);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean dateDejaReservee(int produitId, LocalDate date) throws SQLException {
        String query = "SELECT COUNT(*) FROM reservation1 r JOIN commande c ON r.commande_id = c.id WHERE r.produit_id = ? AND c.date = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, produitId);
            ps.setDate(2, java.sql.Date.valueOf(date));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    @Override
    public void ajouterServiceReserve(int commandeId, ServiceItem service, LocalDate dateReservation) throws SQLException {
        String sql = "INSERT INTO service_reserve (commande_id, service_id, date_reservation) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, commandeId);
            stmt.setInt(2, service.getId());
            stmt.setDate(3, java.sql.Date.valueOf(dateReservation));
            stmt.executeUpdate();
        }
    }

    @Override
    public Commande getCommandeByUser(User user) throws SQLException {
        String sql = "SELECT * FROM commande WHERE utilisateur = ? AND statut = 'RESERVE'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getPrenom());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Commande commande = new Commande();
                commande.setId(rs.getInt("id"));
                commande.setUtilisateur(rs.getString("utilisateur"));
                commande.setDateCommande(rs.getTimestamp("date").toLocalDateTime());
                commande.setStatut(rs.getString("statut"));
                commande.setTotal(rs.getDouble("total"));
                return commande;
            }
        }
        return null;
    }

    @Override
    public void ajouterCommande(Commande commande) throws SQLException {
        String sql = "INSERT INTO commande (utilisateur, date, total, statut) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, commande.getUtilisateur());
            stmt.setTimestamp(2, Timestamp.valueOf(commande.getDateCommande()));
            stmt.setDouble(3, commande.getTotal());
            stmt.setString(4, commande.getStatut());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                commande.setId(rs.getInt(1));
            }
        }
    }


    public void updateCommande(Commande commande) throws SQLException {
        String query = "UPDATE commande SET total = ?, statut = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setDouble(1, commande.getTotal());
            ps.setString(2, commande.getStatut());
            ps.setInt(3, commande.getId());
            ps.executeUpdate();
        }
    }
}