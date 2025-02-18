package Wedding.service;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import Wedding.entities.Commande;
import Wedding.entities.Produit;
import Wedding.entities.Reservation;
import Wedding.utils.MyDatabase;

public class ServiceCommande implements IServiceCommande {
    private Connection connection = MyDatabase.getInstance().getConnection();

    public ServiceCommande() {
        System.out.println("Connexion à la base de données : " + this.connection);
    }

    /**
     * Ajouter ou mettre à jour une réservation dans une commande.
     * Si une commande en attente existe, on met à jour les produits réservés.
     * Sinon, on crée une nouvelle commande.
     */

    public void removeProductFromCart(int commandeId, int produitId) throws SQLException {
        String sqlDelete = "DELETE FROM reservation WHERE commande_id = ? AND produit_id = ? AND statut = 'RESERVE'";
        try (PreparedStatement stmt = connection.prepareStatement(sqlDelete)) {
            stmt.setInt(1, commandeId);
            stmt.setInt(2, produitId);
            stmt.executeUpdate();
        }
    }

    public int ajouterOuMettreAJourReservation(String utilisateur, Produit produit) throws SQLException {
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
                stmt.setDouble(3, produit.getPrix());
                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    idCommande = rs.getInt(1);
                }
            }
        }

        String sqlCheckProduit = "SELECT quantite FROM reservation WHERE commande_id = ? AND produit_id = ?";
        boolean produitExiste = false;
        int nouvelleQuantite = 1;

        try (PreparedStatement stmt = connection.prepareStatement(sqlCheckProduit)) {
            stmt.setInt(1, idCommande);
            stmt.setInt(2, produit.getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                produitExiste = true;
                nouvelleQuantite = rs.getInt("quantite") + 1;
            }
        }

        if (produitExiste) {
            String sqlUpdateQuantite = "UPDATE reservation SET quantite = ? WHERE commande_id = ? AND produit_id = ?";
            try (PreparedStatement updateStmt = connection.prepareStatement(sqlUpdateQuantite)) {
                updateStmt.setInt(1, nouvelleQuantite);
                updateStmt.setInt(2, idCommande);
                updateStmt.setInt(3, produit.getId());
                updateStmt.executeUpdate();
            }
        } else {
            String sqlInsertProduit = "INSERT INTO reservation (commande_id, produit_id, quantite, statut) VALUES (?, ?, ?, 'RESERVE')";
            try (PreparedStatement insertStmt = connection.prepareStatement(sqlInsertProduit)) {
                insertStmt.setInt(1, idCommande);
                insertStmt.setInt(2, produit.getId());
                insertStmt.setInt(3, 1);
                insertStmt.executeUpdate();
            }
        }

        return idCommande;
    }
        @Override

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
                    preparedStatement.setTimestamp(2, Timestamp.valueOf(commande.getDate()));
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

                String sqlCheckProduit = "SELECT quantite FROM reservation WHERE commande_id = ? AND produit_id = ?";
                try (PreparedStatement stmtCheckProduit = this.connection.prepareStatement(sqlCheckProduit)) {
                    stmtCheckProduit.setInt(1, commandeId);
                    stmtCheckProduit.setInt(2, produit.getId());
                    ResultSet rsProduit = stmtCheckProduit.executeQuery();

                    if (rsProduit.next()) {
                        // Si le produit existe déjà, mettre à jour la quantité
                        int nouvelleQuantite = rsProduit.getInt("quantite") + quantite;
                        String sqlUpdateProduit = "UPDATE reservation SET quantite = ? WHERE commande_id = ? AND produit_id = ?";
                        try (PreparedStatement updateProduitStmt = this.connection.prepareStatement(sqlUpdateProduit)) {
                            updateProduitStmt.setInt(1, nouvelleQuantite);
                            updateProduitStmt.setInt(2, commandeId);
                            updateProduitStmt.setInt(3, produit.getId());
                            updateProduitStmt.executeUpdate();
                        }
                    } else {
                        // Si le produit n'existe pas encore, l'ajouter avec statut "RESERVE"
                        String reqReservation = "INSERT INTO reservation (commande_id, produit_id, quantite, statut) VALUES (?, ?, ?, 'RESERVE')";
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
    public void confirmerCommande(int idCommande) throws SQLException {
        // Récupérer les produits réservés dans la commande
        String sqlGetProduits = "SELECT produit_id, quantite FROM reservation WHERE commande_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sqlGetProduits)) {
            stmt.setInt(1, idCommande);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int idProduit = rs.getInt("produit_id");
                int quantite = rs.getInt("quantite");

                // Réduire le stock du produit
                String sqlUpdateStock = "UPDATE produit SET stock = stock - ? WHERE id = ?";
                try (PreparedStatement updateStmt = connection.prepareStatement(sqlUpdateStock)) {
                    updateStmt.setInt(1, quantite);
                    updateStmt.setInt(2, idProduit);
                    updateStmt.executeUpdate();
                }
            }
        }

        // Mettre à jour le statut de la commande à "confirmée"
        String sqlUpdateCommande = "UPDATE commande SET statut = 'confirmée' WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sqlUpdateCommande)) {
            stmt.setInt(1, idCommande);
            stmt.executeUpdate();
        }
    }
    public List<Produit> getProduitsDansPanier(int commandeId) throws SQLException {
        List<Produit> produits = new ArrayList<>();
        String query = "SELECT p.id, p.nom, p.description, p.prix, p.categorie, p.stock, p.imageUrl " +
                "FROM produit p " +
                "JOIN reservation r ON p.id = r.produit_id " +
                "WHERE r.commande_id = ? AND r.statut = 'RESERVE'";

        PreparedStatement statement = connection.prepareStatement(query);
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
                    resultSet.getString("imageUrl") // Récupérer l'URL de l'image
            );
            produits.add(produit);
        }
        return produits;
    }
    /**
     * Annuler une réservation, en supprimant les produits réservés et la commande correspondante.
     */
    public void annulerReservation(int commandeId) throws SQLException {
        // Supprimer les produits réservés dans la commande
        String reqDeleteReservation = "DELETE FROM reservation WHERE commande_id = ?";
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
}
