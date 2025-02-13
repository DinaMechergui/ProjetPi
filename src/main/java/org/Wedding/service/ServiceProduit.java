//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.Wedding.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.Wedding.entities.Produit;
import org.Wedding.utils.MyDatabase;

public class ServiceProduit implements IService<Produit> {
    Connection connection = MyDatabase.getInstance().getConnection();

    public ServiceProduit() {
    }

    public int ajouter(Produit produit) throws SQLException {
        if (this.existeDeja(produit.getNom(), produit.getCategorie())) {
            System.out.println("⚠️ Le produit existe déjà !");
            return -1;
        } else {
            String req = "INSERT INTO produit (nom, description, prix, categorie, stock) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = this.connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, produit.getNom());
            preparedStatement.setString(2, produit.getDescription());
            preparedStatement.setDouble(3, produit.getPrix());
            preparedStatement.setString(4, produit.getCategorie());
            preparedStatement.setInt(5, produit.getStock());
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Échec de l'ajout du produit, aucun ID généré.");
            }
        }
    }

    private boolean existeDeja(String nom, String categorie) throws SQLException {
        String req = "SELECT COUNT(*) FROM produit WHERE nom = ? AND categorie = ?";
        PreparedStatement preparedStatement = this.connection.prepareStatement(req);
        preparedStatement.setString(1, nom);
        preparedStatement.setString(2, categorie);
        ResultSet rs = preparedStatement.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        } else {
            return false;
        }
    }

    public void modifier(Produit produit) throws SQLException {
        String req = "UPDATE produit SET nom=?, description=?, prix=?, categorie=?, stock=? WHERE id=?";
        PreparedStatement preparedStatement = this.connection.prepareStatement(req);
        preparedStatement.setString(1, produit.getNom());
        preparedStatement.setString(2, produit.getDescription());
        preparedStatement.setDouble(3, produit.getPrix());
        preparedStatement.setString(4, produit.getCategorie());
        preparedStatement.setInt(5, produit.getStock());
        preparedStatement.setInt(6, produit.getId());

        int rowsUpdated = preparedStatement.executeUpdate();  // Récupération du nombre de lignes mises à jour
        if (rowsUpdated > 0) {
            System.out.println("✅ Produit modifié avec succès !");
        } else {
            System.out.println("❌ Aucune modification appliquée ! Assurez-vous que l'ID est correct.");
        }
    }
    public boolean existeProduit(int id) throws SQLException {
        String req = "SELECT COUNT(*) FROM produit WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setInt(1, id);
        ResultSet rs = preparedStatement.executeQuery();
        return rs.next() && rs.getInt(1) > 0;  // Retourne vrai si le produit existe
    }


    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM produit WHERE id=?";
        PreparedStatement preparedStatement = this.connection.prepareStatement(req);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        System.out.println("Produit supprimé !");
    }

    public List<Produit> afficher() throws SQLException {
        List<Produit> produits = new ArrayList();
        String req = "SELECT * FROM produit";
        Statement statement = this.connection.createStatement();
        ResultSet rs = statement.executeQuery(req);

        while(rs.next()) {
            Produit produit = new Produit(rs.getInt("id"), rs.getString("nom"), rs.getString("description"), rs.getDouble("prix"), rs.getString("categorie"), rs.getInt("stock"));
            produits.add(produit);
        }
        System.out.println("Produits affichés : " + produits);

        return produits;
    }
}
