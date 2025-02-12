package org.Wedding.service;

import org.Wedding.entities.Produit;
import org.Wedding.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceProduit {
    Connection connection;

    public ServiceProduit() {
        connection = MyDatabase.getInstance().getConnection();
    }

    // Ajout d'un produit avec récupération de l'ID généré
    public int ajouter(Produit produit) throws SQLException {
        String req = "INSERT INTO produit (nom, description, prix, categorie, stock) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, produit.getNom());
        preparedStatement.setString(2, produit.getDescription());
        preparedStatement.setDouble(3, produit.getPrix());
        preparedStatement.setString(4, produit.getCategorie());
        preparedStatement.setInt(5, produit.getStock());

        preparedStatement.executeUpdate();
        ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

        if (generatedKeys.next()) {
            return generatedKeys.getInt(1); // Retourne l'ID généré
        } else {
            throw new SQLException("Échec de l'ajout du produit, aucun ID généré.");
        }
    }

    // Modifier un produit
    public void modifier(Produit produit) throws SQLException {
        String req = "UPDATE produit SET nom=?, description=?, prix=?, categorie=?, stock=? WHERE id=?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setString(1, produit.getNom());
        preparedStatement.setString(2, produit.getDescription());
        preparedStatement.setDouble(3, produit.getPrix());
        preparedStatement.setString(4, produit.getCategorie());
        preparedStatement.setInt(5, produit.getStock());
        preparedStatement.setInt(6, (int) produit.getId());

        preparedStatement.executeUpdate();
        System.out.println("Produit modifié !");
    }

    // Supprimer un produit
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM produit WHERE id=?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        System.out.println("Produit supprimé !");
    }

    // Afficher tous les produits
    public List<Produit> afficher() throws SQLException {
        List<Produit> produits = new ArrayList<>();
        String req = "SELECT * FROM produit";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(req);

        while (rs.next()) {
            Produit produit = new Produit(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("description"),
                    rs.getDouble("prix"),
                    rs.getString("categorie"),
                    rs.getInt("stock")
            );
            produits.add(produit);
        }
        return produits;
    }
}
