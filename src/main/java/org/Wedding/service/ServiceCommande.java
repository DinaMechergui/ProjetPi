package org.Wedding.service;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import org.Wedding.entities.Commande;
import org.Wedding.entities.Produit;
import org.Wedding.utils.MyDatabase;

public class ServiceCommande implements IServiceCommande {
    private Connection connection = MyDatabase.getInstance().getConnection();

    public ServiceCommande() {
        System.out.println("Connexion à la base de données : " + this.connection);
    }

    public int ajouterReservation(Commande commande) throws SQLException {
        double total = 0;

        // nehsbou total mtaa les produits
        for (Produit p : commande.getProduits()) {
            total += p.getPrix();
        }

        int commandeId = -1;

        // nshoufou esq utilisateur hedha aandou commande en attente wela le
        String sqlCheckCommande = "SELECT id FROM commande WHERE utilisateur = ? AND statut = 'EN_ATTENTE'";
        try (PreparedStatement stmtCheck = this.connection.prepareStatement(sqlCheckCommande)) {
            stmtCheck.setString(1, commande.getUtilisateur());
            ResultSet rs = stmtCheck.executeQuery();
            if (rs.next()) {
                // Si une commande "EN_ATTENTE" existe, on récupère son ID
                commandeId = rs.getInt("id");
            }
        }

        // ken mawjouda naamloulha mise ajour
        if (commandeId != -1) {
            // naamloulha mise ajour twaly reserve
            String sqlUpdateCommande = "UPDATE commande SET statut = 'reservé' WHERE id = ?";
            try (PreparedStatement updateCommandeStmt = this.connection.prepareStatement(sqlUpdateCommande)) {
                updateCommandeStmt.setInt(1, commandeId);
                updateCommandeStmt.executeUpdate();
            }

            // nzidou wela naamlou update ll commande ely mawjouda
            for (Produit produit : commande.getProduits()) {
                String sqlCheckProduit = "SELECT quantite FROM reservation WHERE commande_id = ? AND produit_id = ? AND statut = 'EN_ATTENTE'";
                try (PreparedStatement stmtCheckProduit = this.connection.prepareStatement(sqlCheckProduit)) {
                    stmtCheckProduit.setInt(1, commandeId);
                    stmtCheckProduit.setInt(2, produit.getId());
                    ResultSet rsProduit = stmtCheckProduit.executeQuery();

                    if (rsProduit.next()) {
                        // ken produit mawjoud naamlou updzate ll quantite tzid o statut twali reserve
                        int nouvelleQuantite = rsProduit.getInt("quantite") + 1;
                        String sqlUpdateProduit = "UPDATE reservation SET quantite = ?, statut = 'reservé' WHERE commande_id = ? AND produit_id = ?";
                        try (PreparedStatement updateProduitStmt = this.connection.prepareStatement(sqlUpdateProduit)) {
                            updateProduitStmt.setInt(1, nouvelleQuantite);
                            updateProduitStmt.setInt(2, commandeId);
                            updateProduitStmt.setInt(3, produit.getId());
                            updateProduitStmt.executeUpdate();
                        }
                    } else {
                        // Si le produit n'existe pas encore, on l'ajoute avec statut "RESERVE"
                        String reqReservation = "INSERT INTO reservation (commande_id, produit_id, quantite, statut) VALUES (?, ?, ?, 'reservé')";
                        try (PreparedStatement psInsert = this.connection.prepareStatement(reqReservation)) {
                            psInsert.setInt(1, commandeId);
                            psInsert.setInt(2, produit.getId());
                            psInsert.setInt(3, 1);
                            psInsert.executeUpdate();
                        }
                    }
                }
            }
        } else {
            // Si aucune commande "EN_ATTENTE" n'existe, on crée une nouvelle commande en statut "EN_ATTENTE"
            String req = "INSERT INTO commande (utilisateur, date, total, statut) VALUES (?, ?, ?, 'EN_ATTENTE')";
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

        return commandeId;
    }

    public void ajouterOuMettreAJourReservation(String utilisateur, Produit produit) throws SQLException {
        // nshoufou statut reserve wela en attente
        String sqlCheckCommande = "SELECT id, total, statut FROM commande WHERE utilisateur = ? AND (statut = 'reservé' OR statut = 'EN_ATTENTE')";
        int idCommande = -1;
        double totalCommande = 0;
        String statutCommande = "";

        try (PreparedStatement stmt = connection.prepareStatement(sqlCheckCommande)) {
            stmt.setString(1, utilisateur);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                idCommande = rs.getInt("id");
                totalCommande = rs.getDouble("total");
                statutCommande = rs.getString("statut");
            }
        }

        // ken mafama hata wahda naamlou wahda nhotou statut mteeha en attente
        if (idCommande == -1) {
            String sqlInsertCommande = "INSERT INTO commande (utilisateur, date, total, statut) VALUES (?, ?, ?, 'EN_ATTENTE')";
            try (PreparedStatement stmt = connection.prepareStatement(sqlInsertCommande, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, utilisateur);
                stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                stmt.setDouble(3, produit.getPrix()); // Premier produit ajouté, total = son prix
                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    idCommande = rs.getInt(1);
                    totalCommande = produit.getPrix();
                }
                statutCommande = "EN_ATTENTE"; // Nouvelle commande = statut "en_attente"
            }
        }

        // nshoufou esq produit deja mawjoud f commande
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
            // ken mawjoud naammlou mise a jour ll quantite o total flous
            String sqlUpdateQuantite = "UPDATE reservation SET quantite = ? WHERE commande_id = ? AND produit_id = ?";
            try (PreparedStatement updateStmt = connection.prepareStatement(sqlUpdateQuantite)) {
                updateStmt.setInt(1, nouvelleQuantite);
                updateStmt.setInt(2, idCommande);
                updateStmt.setInt(3, produit.getId());
                updateStmt.executeUpdate();
            }
        } else {
            // sinon msh mawjoud nzidouh reservation
            String sqlInsertProduit = "INSERT INTO reservation (commande_id, produit_id, quantite, statut) VALUES (?, ?, ?, 'EN_ATTENTE')";
            try (PreparedStatement insertStmt = connection.prepareStatement(sqlInsertProduit)) {
                insertStmt.setInt(1, idCommande);
                insertStmt.setInt(2, produit.getId());
                insertStmt.setInt(3, 1); // Ajout avec une quantité initiale de 1
                insertStmt.executeUpdate();
            }
        }

        // 🔹 Mettre à jour le total de la commande
        totalCommande += produit.getPrix();
        String sqlUpdateTotal = "UPDATE commande SET total = ? WHERE id = ?";
        try (PreparedStatement updateTotalStmt = connection.prepareStatement(sqlUpdateTotal)) {
            updateTotalStmt.setDouble(1, totalCommande);
            updateTotalStmt.setInt(2, idCommande);
            updateTotalStmt.executeUpdate();
        }

        // lcommande ki tebda en attente doub manzidou produit twali reserve
        if ("EN_ATTENTE".equals(statutCommande)) {
            String sqlUpdateStatut = "UPDATE commande SET statut = 'reservé' WHERE id = ?";
            try (PreparedStatement updateStmt = connection.prepareStatement(sqlUpdateStatut)) {
                updateStmt.setInt(1, idCommande);
                updateStmt.executeUpdate();
            }
        }
    }

    public void confirmerCommande(int idCommande) throws SQLException {
        String sqlGetProduits = "SELECT produit_id, quantite FROM reservation WHERE commande_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sqlGetProduits)) {
            stmt.setInt(1, idCommande);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int idProduit = rs.getInt("produit_id");
                int quantite = rs.getInt("quantite");

                String sqlUpdateStock = "UPDATE produit SET stock = stock - ? WHERE id = ?";
                try (PreparedStatement updateStmt = connection.prepareStatement(sqlUpdateStock)) {
                    updateStmt.setInt(1, quantite);
                    updateStmt.setInt(2, idProduit);
                    updateStmt.executeUpdate();
                }
            }
        }

        String sqlUpdateCommande = "UPDATE commande SET statut = 'confirmée' WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sqlUpdateCommande)) {
            stmt.setInt(1, idCommande);
            stmt.executeUpdate();
        }
    }

    public void annulerReservation(int commandeId) throws SQLException {
        String reqDeleteReservation = "DELETE FROM reservation WHERE commande_id = ?";
        try (PreparedStatement psDelete = this.connection.prepareStatement(reqDeleteReservation)) {
            psDelete.setInt(1, commandeId);
            psDelete.executeUpdate();
        }

        String reqDeleteCommande = "DELETE FROM commande WHERE id = ? AND statut = 'reservé'";
        try (PreparedStatement psDeleteCommande = this.connection.prepareStatement(reqDeleteCommande)) {
            psDeleteCommande.setInt(1, commandeId);
            psDeleteCommande.executeUpdate();
        }
    }
}
