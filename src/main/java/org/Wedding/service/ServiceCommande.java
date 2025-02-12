package org.Wedding.service;

import org.Wedding.entities.Commande;
import org.Wedding.entities.Produit;
import org.Wedding.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCommande {
    private Connection connection;

    public ServiceCommande() {
        connection = MyDatabase.getInstance().getConnection();
    }

    // Ajouter une commande avec jointure sur `panier`
    public int ajouterReservation(Commande commande) throws SQLException {
        String req = "INSERT INTO commande (utilisateur, date, total, statut) VALUES (?, ?, ?, 'reservé')";
        PreparedStatement preparedStatement = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, commande.getUtilisateur());
        preparedStatement.setTimestamp(2, Timestamp.valueOf(commande.getDate())); // ✅ Correction ici
        preparedStatement.setDouble(3, commande.getTotal());

        int rowsInserted = preparedStatement.executeUpdate();
        if (rowsInserted == 0) {
            throw new SQLException("Échec de la réservation.");
        }

        ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
        int commandeId = -1;
        if (generatedKeys.next()) {
            commandeId = generatedKeys.getInt(1);
        }

        String reqReservation = "INSERT INTO reservation (commande_id, produit_id, quantite, statut) VALUES (?, ?, ?, 'reservé')";
        for (Produit produit : commande.getProduits()) {
            PreparedStatement psInsert = connection.prepareStatement(reqReservation);
            psInsert.setInt(1, commandeId);
            psInsert.setInt(2, produit.getId());
            psInsert.setInt(3, 1);
            psInsert.executeUpdate();
        }

        return commandeId;
    }

    // Confirmer une commande et ajuster le stock
    public void confirmerCommande(int commandeId) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ton_database", "root", "");

            // 1. Mettre à jour la commande en "confirmé"
            String reqUpdateCommande = "UPDATE commande SET statut = 'confirmé' WHERE id = ?";
            PreparedStatement psUpdateCommande = connection.prepareStatement(reqUpdateCommande);
            psUpdateCommande.setInt(1, commandeId);
            psUpdateCommande.executeUpdate();

            // 2. Récupérer les produits de la réservation
            String reqProduits = "SELECT produit_id, quantite FROM reservation WHERE commande_id = ?";
            PreparedStatement psProduits = connection.prepareStatement(reqProduits);
            psProduits.setInt(1, commandeId);
            ResultSet rsProduits = psProduits.executeQuery();

            // 3. Mettre à jour le stock des produits
            while (rsProduits.next()) {
                int produitId = rsProduits.getInt("produit_id");
                int quantite = rsProduits.getInt("quantite");

                String reqUpdateStock = "UPDATE produit SET stock = stock - ? WHERE id = ?";
                PreparedStatement psUpdateStock = connection.prepareStatement(reqUpdateStock);
                psUpdateStock.setInt(1, quantite);
                psUpdateStock.setInt(2, produitId);
                psUpdateStock.executeUpdate();
            }

            // 4. Mettre à jour la réservation en "confirmé"
            String reqUpdateReservation = "UPDATE reservation SET statut = 'confirmé' WHERE commande_id = ?";
            PreparedStatement psUpdateReservation = connection.prepareStatement(reqUpdateReservation);
            psUpdateReservation.setInt(1, commandeId);
            psUpdateReservation.executeUpdate();

            System.out.println("Commande confirmée avec succès !");
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Annuler une réservation
    public void annulerReservation(int commandeId) throws SQLException {
        String reqDeleteReservation = "DELETE FROM reservation WHERE commande_id = ?";
        PreparedStatement psDelete = connection.prepareStatement(reqDeleteReservation);
        psDelete.setInt(1, commandeId);
        psDelete.executeUpdate();

        String reqDeleteCommande = "DELETE FROM commande WHERE id = ? AND statut = 'reservé'";
        PreparedStatement psDeleteCommande = connection.prepareStatement(reqDeleteCommande);
        psDeleteCommande.setInt(1, commandeId);
        psDeleteCommande.executeUpdate();
    }
}