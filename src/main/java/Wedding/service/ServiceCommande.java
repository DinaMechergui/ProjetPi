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
    private static Connection connection = MyDatabase.getInstance().getConnection();

    public ServiceCommande() {
        System.out.println("Connexion à la base de données : " + this.connection);
    }

    /**
     * Ajouter ou mettre à jour une réservation dans une commande.
     * Si une commande en attente existe, on met à jour les produits réservés.
     * Sinon, on crée une nouvelle commande.
     */

    public void removeProductFromCart(int commandeId, int produitId) throws SQLException {
        String sqlDelete = "DELETE FROM reservation1 WHERE commande_id = ? AND produit_id = ? AND statut = 'RESERVE'";
        try (PreparedStatement stmt = connection.prepareStatement(sqlDelete)) {
            stmt.setInt(1, commandeId);
            stmt.setInt(2, produitId);
            stmt.executeUpdate();
        }
    }

    public int ajouterOuMettreAJourReservation(String utilisateur, Produit produit, int quantite) throws SQLException {
        // Vérifier si le produit est en stock
        if (produit.getStock() <= 0) {
            throw new SQLException("Le produit " + produit.getNom() + " est en rupture de stock.");
        }

        // Vérifier si une commande "RESERVE" existe pour cet utilisateur
        String sqlCheckCommande = "SELECT id, total FROM commande WHERE utilisateur = ? AND statut = 'RESERVE'";
        int idCommande = -1;
        double totalCommande = 0;

        try (PreparedStatement stmt = connection.prepareStatement(sqlCheckCommande)) {
            stmt.setString(1, utilisateur);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                idCommande = rs.getInt("id");
                totalCommande = rs.getDouble("total");
            }
        }

        if (idCommande == -1) {
            String sqlInsertCommande = "INSERT INTO commande (utilisateur, date, total, statut) VALUES (?, ?, ?, 'RESERVE')";
            try (PreparedStatement stmt = connection.prepareStatement(sqlInsertCommande, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, utilisateur);
                stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                stmt.setDouble(3, produit.getPrix() * quantite); // Calculer le total initial
                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    idCommande = rs.getInt(1);
                }
            }
        }


        String sqlCheckProduit = "SELECT quantite FROM reservation1 WHERE commande_id = ? AND produit_id = ?";
        boolean produitExiste = false;
        int nouvelleQuantite = quantite; // Utiliser la quantité passée en paramètre

        try (PreparedStatement stmt = connection.prepareStatement(sqlCheckProduit)) {
            stmt.setInt(1, idCommande);
            stmt.setInt(2, produit.getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                produitExiste = true;
                nouvelleQuantite = rs.getInt("quantite") + quantite; // Ajouter la nouvelle quantité à l'existante
            }
        }

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
                insertStmt.setInt(3, quantite); // Utiliser la quantité passée en paramètre
                insertStmt.executeUpdate();
            }
        }

        // Mettre à jour le total de la commande
        String sqlUpdateTotal = "UPDATE commande SET total = total + ? WHERE id = ?";
        try (PreparedStatement updateTotalStmt = connection.prepareStatement(sqlUpdateTotal)) {
            updateTotalStmt.setDouble(1, produit.getPrix() * quantite); // Ajouter le prix du produit multiplié par la quantité
            updateTotalStmt.setInt(2, idCommande);
            updateTotalStmt.executeUpdate();
        }

        return idCommande;
    }

    public int ajouterReservation(Commande commande) throws SQLException {
        double total = 0;

        // Calculer le total en fonction des réservations
        for (Reservation reservation : commande.getReservations()) {
            total += reservation.getProduit().getPrix() * reservation.getQuantite();
        }

        int commandeId = -1;

        // Vérifier si une commande "RESERVE" existe déjà pour cet utilisateur
        String sqlCheckCommande = "SELECT id FROM commande WHERE utilisateur = ? AND statut = 'RESERVE'";
        try (PreparedStatement stmtCheck = this.connection.prepareStatement(sqlCheckCommande)) {
            stmtCheck.setString(1, commande.getUtilisateur());
            ResultSet rs = stmtCheck.executeQuery();
            if (rs.next()) {
                commandeId = rs.getInt("id");
            }
        }

        if (commandeId == -1) {
            // Si aucune commande "RESERVE" n'existe, créer une nouvelle commande directement en "RESERVE"
            String req = "INSERT INTO commande (utilisateur, date, total, statut) VALUES (?, ?, ?, 'RESERVE')";
            try (PreparedStatement preparedStatement = this.connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, commande.getUtilisateur());
                preparedStatement.setTimestamp(2, Timestamp.valueOf(commande.getDateCommande()));
                preparedStatement.setDouble(3, total);
                preparedStatement.executeUpdate();

                // Récupérer l'ID de la nouvelle commande
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    commandeId = generatedKeys.getInt(1);
                }
            }
        }

        // Ajouter ou mettre à jour les produits dans la table reservation
        for (Reservation reservation : commande.getReservations()) {
            Produit produit = reservation.getProduit();
            int quantite = reservation.getQuantite();

            String sqlCheckProduit = "SELECT quantite FROM reservation1 WHERE commande_id = ? AND produit_id = ?";
            try (PreparedStatement stmtCheckProduit = this.connection.prepareStatement(sqlCheckProduit)) {
                stmtCheckProduit.setInt(1, commandeId);
                stmtCheckProduit.setInt(2, produit.getId());
                ResultSet rsProduit = stmtCheckProduit.executeQuery();

                if (rsProduit.next()) {
                    // Si le produit existe déjà, mettre à jour la quantité
                    int nouvelleQuantite = rsProduit.getInt("quantite") + quantite;
                    String sqlUpdateProduit = "UPDATE reservation1 SET quantite = ? WHERE commande_id = ? AND produit_id = ?";
                    try (PreparedStatement updateProduitStmt = this.connection.prepareStatement(sqlUpdateProduit)) {
                        updateProduitStmt.setInt(1, nouvelleQuantite);
                        updateProduitStmt.setInt(2, commandeId);
                        updateProduitStmt.setInt(3, produit.getId());
                        updateProduitStmt.executeUpdate();
                    }
                } else {
                    // Si le produit n'existe pas encore, l'ajouter avec statut "RESERVE"
                    String reqReservation = "INSERT INTO reservation1 (commande_id, produit_id, quantite, statut) VALUES (?, ?, ?, 'RESERVE')";
                    try (PreparedStatement psInsert = this.connection.prepareStatement(reqReservation)) {
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




    /**
     * Confirmer une commande, en réduisant le stock des produits réservés et en changeant le statut de la commande.
     */

    public List<Pair<ServiceItem, LocalDate>> getServicesReserves(int commandeId) throws SQLException {
        List<Pair<ServiceItem, LocalDate>> servicesReserves = new ArrayList<>();
        String sql = "SELECT s.*, sr.date FROM reserve sr JOIN service s ON sr.service_id = s.id WHERE sr.commande_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, commandeId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                // Créer un ServiceItem à partir de l'ID
                ServiceItem service = new ServiceItem(rs.getInt("id"), connection);  // Utilize the constructor to fetch from DB
                LocalDate date = rs.getDate("date").toLocalDate();
                servicesReserves.add(new Pair<>(service, date));
            }
        }
        return servicesReserves;
    }




    public void confirmerCommande(int idCommande, String utilisateur) throws SQLException {
        try (Connection localConnection = MyDatabase.getInstance().getConnection()) {
            localConnection.setAutoCommit(false);

            // Step 1: Fetch reserved products in the order
            String sqlGetProduits = "SELECT produit_id, quantite FROM reservation1 WHERE commande_id = ?";
            try (PreparedStatement stmt = localConnection.prepareStatement(sqlGetProduits)) {
                stmt.setInt(1, idCommande);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    int idProduit = rs.getInt("produit_id");
                    int quantite = rs.getInt("quantite");

                    // Step 2: Check stock availability
                    String sqlCheckStock = "SELECT stock FROM produit WHERE id = ?";
                    try (PreparedStatement checkStmt = localConnection.prepareStatement(sqlCheckStock)) {
                        checkStmt.setInt(1, idProduit);
                        ResultSet stockRs = checkStmt.executeQuery();
                        if (stockRs.next()) {
                            int stock = stockRs.getInt("stock");
                            if (stock < quantite) {
                                throw new SQLException("Stock insuffisant pour le produit ID : " + idProduit);
                            }
                        }
                    }

                    // Step 3: Reduce product stock
                    String sqlUpdateStock = "UPDATE produit SET stock = stock - ? WHERE id = ?";
                    try (PreparedStatement updateStmt = localConnection.prepareStatement(sqlUpdateStock)) {
                        updateStmt.setInt(1, quantite);
                        updateStmt.setInt(2, idProduit);
                        updateStmt.executeUpdate();
                    }
                }
            }

            // Step 4: Update order status to 'confirmée'
            String sqlUpdateCommande = "UPDATE commande SET statut = 'confirmée' WHERE id = ? AND utilisateur = ?";
            try (PreparedStatement stmt = localConnection.prepareStatement(sqlUpdateCommande)) {
                stmt.setInt(1, idCommande);
                stmt.setString(2, utilisateur);
                stmt.executeUpdate();
            }

            // Commit the transaction
            localConnection.commit();
        } catch (SQLException e) {
            throw e; // Let the caller handle exceptions
        }
    }



    public List<Pair<Produit, Integer>> getProduitsEtQuantitesDansPanier(int commandeId) throws SQLException {
        List<Pair<Produit, Integer>> produitsEtQuantites = new ArrayList<>();

        // 🔍 Vérifier et rouvrir la connexion si nécessaire
        connection = MyDatabase.getInstance().getConnection();

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


    /**
     * Annuler une réservation, en supprimant les produits réservés et la commande correspondante.
     */
    public void annulerReservation(int commandeId) throws SQLException {
        // Supprimer les produits réservés dans la commande
        String reqDeleteReservation = "DELETE FROM reservation1 WHERE commande_id = ?";
        try (PreparedStatement psDelete = this.connection.prepareStatement(reqDeleteReservation)) {
            psDelete.setInt(1, commandeId);
            psDelete.executeUpdate();
        }

        // Supprimer la commande réservée
        String reqDeleteCommande = "DELETE FROM commande WHERE id = ? AND statut = 'reservé'";
        try (PreparedStatement psDeleteCommande = this.connection.prepareStatement(reqDeleteCommande)) {
            psDeleteCommande.setInt(1, commandeId);
            psDeleteCommande.executeUpdate();
        }
    }

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
                // Créer l'objet Produit
                Produit produit = new Produit();
                produit.setId(rs.getInt("produit_id"));
                produit.setNom(rs.getString("produit_nom"));

                // Créer l'objet Commande
                Commande commande = new Commande();
                commande.setId(rs.getInt("commande_id"));

                // Convertir le statut en Enum
                String statutStr = rs.getString("statut");
                Reservation.StatutReservation statut = Reservation.StatutReservation.valueOf(statutStr);

                // Créer l'objet Reservation
                Reservation reservation = new Reservation(
                        rs.getLong("id"),
                        commande,
                        produit,
                        rs.getInt("quantite")
                );
                reservation.setStatut(statut);

                // Ajouter à la liste
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    public void deleteReservation(int idReservation) {
        String query = "DELETE FROM reservation1 WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, idReservation);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean dateDejaReservee(int produitId, LocalDate date) throws SQLException {
        String query = """
        SELECT COUNT(*) 
        FROM reservation1 r
        JOIN commande c ON r.commande_id = c.id
        WHERE r.produit_id = ? AND c.date = ?
    """;

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





    /**
     * Récupère la commande "RESERVE" pour l'utilisateur donné.
     * Retourne null si aucune commande n'est trouvée.
     */
    public Commande getCommandeByUser(User user) throws SQLException {
        String sql = "SELECT * FROM commande WHERE utilisateur = ? AND statut = 'RESERVE'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // Ici, on suppose que l'attribut 'utilisateur' stocke le prénom ou un identifiant unique
            stmt.setString(1, user.getPrenom());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Commande commande = new Commande();
                commande.setId(rs.getInt("id"));
                commande.setUtilisateur(rs.getString("utilisateur"));
                commande.setDateCommande(rs.getTimestamp("date").toLocalDateTime());
                // Assurez-vous que la classe Commande possède un attribut 'statut' et 'total'
                commande.setStatut(rs.getString("statut"));
                commande.setTotal(rs.getDouble("total"));
                return commande;
            }
        }
        return null;
    }

    /**
     * Ajoute une nouvelle commande dans la base de données et met à jour son ID.
     */
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
    public void ajouterServiceReserve(int commandeId, ServiceItem service, LocalDate dateReservation) throws SQLException {
        String sql = "INSERT INTO reservation (commande_id, service_id, date_reservation) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, commandeId);
            stmt.setInt(2, service.getId());
            stmt.setDate(3, java.sql.Date.valueOf(dateReservation));
            stmt.executeUpdate();
            ServiceCommande.updateTotalPrice(String.valueOf(commandeId));

        }
    }
    /*  public List<Pair<ServiceItem, LocalDate>> getServicesReserves(int commandeId) throws SQLException {
          List<Pair<ServiceItem, LocalDate>> servicesReserves = new ArrayList<>();
          String sql = "SELECT s.*, sr.date FROM reserve sr JOIN service s ON sr.service_id = s.id WHERE sr.commande_id = ?";
          try (PreparedStatement stmt = connection.prepareStatement(sql)) {
              stmt.setInt(1, commandeId);
              ResultSet rs = stmt.executeQuery();
              while (rs.next()) {
                  // Créer un ServiceItem à partir de l'ID
                  ServiceItem service = new ServiceItem(rs.getInt("id"), connection);  // Utilize the constructor to fetch from DB
                  LocalDate date = rs.getDate("date").toLocalDate();
                  servicesReserves.add(new Pair<>(service, date));
              }
          }
          return servicesReserves;
      }

      */
    // ✅ Update the total price of a Commande
    public static void updateTotalPrice(String utilisateur) throws SQLException {
        double total = calculateTotalForCommande(utilisateur);

        // 🔹 Trouver l'ID de la commande RESERVE de cet utilisateur
        String findCommandeQuery = "SELECT id FROM commande WHERE utilisateur = ? AND statut = 'RESERVE'";

        try (PreparedStatement stmtFind = connection.prepareStatement(findCommandeQuery)) {
            stmtFind.setString(1, utilisateur);
            ResultSet rs = stmtFind.executeQuery();
            if (rs.next()) {
                int commandeId = rs.getInt("id");

                // 🔹 Mettre à jour le total de cette commande
                String updateQuery = "UPDATE commande SET total = ? WHERE id = ?";
                try (PreparedStatement stmtUpdate = connection.prepareStatement(updateQuery)) {
                    stmtUpdate.setDouble(1, total);
                    stmtUpdate.setInt(2, commandeId);

                    int rowsUpdated = stmtUpdate.executeUpdate();
                    if (rowsUpdated > 0) {
                        System.out.println("✅ Total mis à jour pour la commande ID : " + commandeId + " | Nouveau total: " + total);
                    } else {
                        System.out.println("⚠️ Erreur : Total non mis à jour.");
                    }
                }
            } else {
                System.out.println("⚠️ Aucun panier trouvé pour l'utilisateur : " + utilisateur);
            }
        }
    }



    public static double calculateTotalForCommande(String utilisateur) throws SQLException {
        double totalProduits = 0.0;
        double totalServices = 0.0;

        // 🔹 Requête pour récupérer le total des produits réservés dans reservation1
        String queryProduits = """
        SELECT COALESCE(SUM(p.prix * r1.quantite), 0) 
        FROM reservation1 r1 
        JOIN produit p ON r1.produit_id = p.id 
        JOIN commande c ON r1.commande_id = c.id
        WHERE c.utilisateur = ? AND c.statut = 'RESERVE'
    """;

        // 🔹 Requête pour récupérer le total des services réservés dans reservation
        String queryServices = """
        SELECT COALESCE(SUM(prix_total), 0) 
        FROM reservation 
        WHERE utilisateur = ?
    """;

        try (PreparedStatement stmtProduits = connection.prepareStatement(queryProduits);
             PreparedStatement stmtServices = connection.prepareStatement(queryServices)) {

            stmtProduits.setString(1, utilisateur);
            ResultSet rsProduits = stmtProduits.executeQuery();
            if (rsProduits.next()) {
                totalProduits = rsProduits.getDouble(1);
            }

            stmtServices.setString(1, utilisateur);
            ResultSet rsServices = stmtServices.executeQuery();
            if (rsServices.next()) {
                totalServices = rsServices.getDouble(1);
            }

            double totalFinal = totalProduits + totalServices;

            System.out.println("🛒 Total Produits: " + totalProduits + " | 🛎️ Total Services: " + totalServices + " | 💰 Total Final: " + totalFinal);
            return totalFinal;
        }
    }

}
