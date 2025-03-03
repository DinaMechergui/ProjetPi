// Déclare le package où se trouve la classe
package services;

// Importe les classes nécessaires
import entities.ServiceItem; // Entité représentant un service
import Wedding.utils.MyDatabase;

import java.sql.*; // API JDBC pour les opérations SQL
import java.util.ArrayList; // Liste dynamique
import java.util.List; // Interface pour les listes

// Implémente l'interface générique IService pour le type ServiceItem
public class ServiceService implements IService<ServiceItem> {
    private Connection connection; // Objet de connexion à la base

    // Constructeur qui initialise la connexion à la base
    public ServiceService() {
        this.connection = MyDatabase.getInstance().getConnection();
    }

    // Méthode pour ajouter un ServiceItem à la base
    @Override
    public int ajouter(ServiceItem ServiceItem) throws SQLException {
        // Requête SQL paramétrée avec RETURN_GENERATED_KEYS pour récupérer l'ID généré
        String req = "INSERT INTO service (nom, description, prix, image_url) VALUES (?, ?, ?, ?)";

        // Try-with-resources pour fermer automatiquement le PreparedStatement
        try (PreparedStatement ps = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            // Remplissage des paramètres
            ps.setString(1, ServiceItem.getNom());
            ps.setString(2, ServiceItem.getDescription());
            ps.setDouble(3, ServiceItem.getPrix());
            ps.setString(4, ServiceItem.getImageUrl());

            ps.executeUpdate(); // Exécute l'insertion

            // Récupère l'ID auto-généré après l'insertion
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Retourne l'ID
                }
            }
            return -1; // Retourne -1 si échec
        }
    }

    // Méthode pour mettre à jour un ServiceItem
    @Override
    public void modifier(ServiceItem ServiceItem) throws SQLException {
        String req = "UPDATE service SET nom=?, description=?, prix=?, image_url=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            // Paramètres de la requête
            ps.setString(1, ServiceItem.getNom());
            ps.setString(2, ServiceItem.getDescription());
            ps.setDouble(3, ServiceItem.getPrix());
            ps.setString(4, ServiceItem.getImageUrl());
            ps.setInt(5, ServiceItem.getId()); // ID pour la clause WHERE

            ps.executeUpdate(); // Exécute la mise à jour
        }
    }

    // Méthode pour supprimer un ServiceItem par son ID
    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM service WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, id); // Paramètre ID
            ps.executeUpdate(); // Exécute la suppression
        }
    }

    // Méthode pour récupérer tous les ServiceItems de la base
    public List<ServiceItem> afficher() throws SQLException {
        List<ServiceItem> services = new ArrayList<>();
        String req = "SELECT * FROM service";

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ResultSet rs = ps.executeQuery(); // Exécute la requête

            // Parcourt les résultats
            while (rs.next()) {
                // Crée un objet ServiceItem à partir des données de la base
                services.add(new ServiceItem(
                        rs.getInt("id"), // Récupère l'ID
                        rs.getString("nom"), // Nom
                        rs.getString("description"), // Description
                        rs.getDouble("prix"), // Prix
                        rs.getString("image_url") // URL de l'image
                ));
            }
        }
        return services; // Retourne la liste complète
    }
}