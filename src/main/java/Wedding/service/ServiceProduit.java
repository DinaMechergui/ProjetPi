package Wedding.service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Wedding.entities.Produit;
import Wedding.utils.MyDatabase;

public class ServiceProduit {
    Connection connection = MyDatabase.getInstance().getConnection();

    public ServiceProduit() {
    }

    // Méthode pour ajouter un produit
    public int ajouter(Produit produit) throws SQLException {
        if (this.existeDeja(produit.getNom(), produit.getCategorie())) {
            System.out.println("⚠️ Le produit existe déjà !");
            return -1;
        } else {
            String req = "INSERT INTO produit (nom, description, prix, categorie, stock, imageUrl) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = this.connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, produit.getNom());
            preparedStatement.setString(2, produit.getDescription());
            preparedStatement.setDouble(3, produit.getPrix());
            preparedStatement.setString(4, produit.getCategorie());
            preparedStatement.setInt(5, produit.getStock());
            preparedStatement.setString(6, produit.getImageUrl()); // Ajout de l'URL de l'image

            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Échec de l'ajout du produit, aucun ID généré.");
            }
        }
    }

    // Vérifier si un produit existe déjà
    private boolean existeDeja(String nom, String categorie) throws SQLException {
        String req = "SELECT COUNT(*) FROM produit WHERE nom = ? AND categorie = ?";
        PreparedStatement preparedStatement = this.connection.prepareStatement(req);
        preparedStatement.setString(1, nom);
        preparedStatement.setString(2, categorie);
        ResultSet rs = preparedStatement.executeQuery();
        return rs.next() && rs.getInt(1) > 0;
    }

    // Modifier un produit
    public void modifier(Produit produit) throws SQLException {
        String req = "UPDATE produit SET nom=?, description=?, prix=?, categorie=?, stock=?, imageUrl=? WHERE id=?";
        PreparedStatement preparedStatement = this.connection.prepareStatement(req);
        preparedStatement.setString(1, produit.getNom());
        preparedStatement.setString(2, produit.getDescription());
        preparedStatement.setDouble(3, produit.getPrix());
        preparedStatement.setString(4, produit.getCategorie());
        preparedStatement.setInt(5, produit.getStock());
        preparedStatement.setString(6, produit.getImageUrl());
        preparedStatement.setInt(7, produit.getId());

        int rowsUpdated = preparedStatement.executeUpdate();
        if (rowsUpdated > 0) {
            System.out.println("✅ Produit modifié avec succès !");
        } else {
            System.out.println("❌ Aucune modification appliquée ! Assurez-vous que l'ID est correct.");
        }
    }

    // Vérifier si un produit existe par ID
    public boolean existeProduit(int id) throws SQLException {
        String req = "SELECT COUNT(*) FROM produit WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setInt(1, id);
        ResultSet rs = preparedStatement.executeQuery();
        return rs.next() && rs.getInt(1) > 0;
    }

    // Supprimer un produit
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM produit WHERE id=?";
        PreparedStatement preparedStatement = this.connection.prepareStatement(req);
        preparedStatement.setInt(1, id);
        int rowsDeleted = preparedStatement.executeUpdate();
        if (rowsDeleted > 0) {
            System.out.println("✅ Produit supprimé avec succès !");
        } else {
            System.out.println("❌ Échec de la suppression, produit introuvable !");
        }
    }

    // Récupérer tous les produits
    public List<Produit> afficher() throws SQLException {
        List<Produit> produits = new ArrayList<>();
        String req = "SELECT * FROM produit";
        Statement statement = this.connection.createStatement();
        ResultSet rs = statement.executeQuery(req);

        while (rs.next()) {
            Produit produit = new Produit(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("description"),
                    rs.getDouble("prix"),
                    rs.getString("categorie"),
                    rs.getInt("stock"),
                    rs.getString("imageUrl") // Ajout de l'URL de l'image
            );
            produits.add(produit);
        }
        System.out.println("📦 Produits affichés : " + produits);
        return produits;
    }
}
