package tn.esprit.services;

import tn.esprit.entities.Cadeau;
import tn.esprit.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCadeau implements ICadeau {
    private final Connection connection;

    public ServiceCadeau() {
        this.connection = MyDatabase.getInstance().getConnection();
    }

    public void ajouter(Cadeau cadeau) throws SQLException {
        String req = "INSERT INTO cadeau (nom, description, disponibilite, invite_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = MyDatabase.getInstance().getConnection();
             PreparedStatement statement = conn.prepareStatement(req)) {

            statement.setString(1, cadeau.getNom());
            statement.setString(2, cadeau.getDescription());
            statement.setBoolean(3, cadeau.isDisponibilite());
            statement.setInt(4, cadeau.getInviteId()); // Utilisation de l'ID événement ici

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("✅ Cadeau ajouté avec succès !");
            } else {
                System.err.println("❌ Erreur : l'ajout du cadeau a échoué.");
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL : " + e.getMessage());
        }
    }


    // Modifier un cadeau
    @Override
    public void modifier(Cadeau cadeau) throws SQLException {
        String req = "UPDATE cadeau SET nom = ?, description = ?, disponibilite = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setString(1, cadeau.getNom());
            preparedStatement.setString(2, cadeau.getDescription());
            preparedStatement.setBoolean(3, cadeau.isDisponibilite());
            preparedStatement.setInt(4, cadeau.getId());

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Cadeau modifié avec succès !");
            } else {
                System.out.println("⚠ Aucun cadeau trouvé avec cet ID !");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification du cadeau : " + e.getMessage());
        }
    }

    // Supprimer un cadeau
    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM cadeau WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, id);

            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("✅ Cadeau supprimé avec succès !");
            } else {
                System.out.println("⚠ Aucun cadeau trouvé avec cet ID !");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression du cadeau : " + e.getMessage());
        }
    }

 /*   // Afficher tous les cadeaux
    @Override
    public List<Cadeau> afficher() throws SQLException {
        String req = "SELECT * FROM cadeau";
        List<Cadeau> cadeaux = new ArrayList<>();

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(req)) {

            while (rs.next()) {
                Cadeau cadeau = new Cadeau(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getBoolean("disponibilite"),
                        rs.getInt("idCadeau")
                );
                cadeaux.add(cadeau);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'affichage des cadeaux : " + e.getMessage());
        }

        return cadeaux;
    }

    // Trouver un cadeau par ID
    @Override
    public Cadeau trouverParId(int id) throws SQLException {
        String req = "SELECT * FROM cadeau WHERE id = ?";
        Cadeau cadeau = null;

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                cadeau = new Cadeau(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getBoolean("disponibilite")

                );
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche du cadeau : " + e.getMessage());
        }

        return cadeau;
    }

    // Chercher un cadeau par nom
    @Override
    public List<Cadeau> chercherParNom(String nom) throws SQLException {
        String req = "SELECT * FROM cadeau WHERE nom LIKE ?";
        List<Cadeau> cadeaux = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setString(1, "%" + nom + "%");
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Cadeau cadeau = new Cadeau(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getBoolean("disponibilite")
                );
                cadeaux.add(cadeau);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche des cadeaux : " + e.getMessage());
        }

        return cadeaux;
    }
    public List<Cadeau> getAllCadeaux() {
        List<Cadeau> cadeaux = new ArrayList<>();
        String query = "SELECT * FROM cadeau"; // Assure-toi que "cadeau" est bien le nom de la table

        try (Connection conn = MyDatabase.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Cadeau cadeau = new Cadeau(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getBoolean("disponibilite") // Vérifie le type en base de données
                );
                cadeaux.add(cadeau);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des cadeaux : " + e.getMessage());
        }

        return cadeaux;
    }

    public void afficherCadeaux() {
        List<Cadeau> cadeaux = getAllCadeau(); // Récupérer tous les cadeaux

        if (cadeaux.isEmpty()) {
            System.out.println("⚠ Aucun cadeau trouvé !");
        } else {
            System.out.println("🎁 Liste des cadeaux disponibles :");
            for (Cadeau cadeau : cadeaux) {
                System.out.println("ID: " + cadeau.getId() +
                        " | Nom: " + cadeau.getNom() +
                        " | Description: " + cadeau.getDescription() +
                        " | Disponibilité: " + (cadeau.isDisponibilite() ? "Disponible ✅" : "Indisponible ❌"));
            }
        }
    }

    private List<Cadeau> getAllCadeau() {
        List<Cadeau> cadeaux = new ArrayList<>();
        String query = "SELECT * FROM cadeau"; // Assure-toi que "cadeau" est bien le nom de la table

        try (Connection conn = MyDatabase.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Cadeau cadeau = new Cadeau(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getBoolean("disponibilite") // Vérifie le type en base de données
                );
                cadeaux.add(cadeau);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des cadeaux : " + e.getMessage());
        }

        return cadeaux;

    }*/

   /* public List<Cadeau> afficher() throws SQLException {
        // Implémentation pour récupérer la liste des cadeaux depuis la base de données
    }

    // Exemple de méthode pour modifier un cadeau
  /*  public void modifier(Cadeau cadeau) throws SQLException {
        // Implémentation pour mettre à jour la disponibilité du cadeau dans la base de données
    }*/
   public List<Cadeau> afficher() throws SQLException {
       List<Cadeau> cadeaux = new ArrayList<>();

       String query = "SELECT * FROM cadeau"; // Remplace "cadeau" par le nom de ta table
       try (Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query)) {

           while (rs.next()) {
               int id = rs.getInt("id");
               String nom = rs.getString("nom");
               String description = rs.getString("description");
               boolean disponibilite = rs.getBoolean("disponibilite");
               int inviteId = rs.getInt("invite_id");

               Cadeau cadeau = new Cadeau(id, nom, description, disponibilite, inviteId);
               cadeaux.add(cadeau);
           }
       }

       return cadeaux;
   }

    // Autres méthodes (par exemple pour modifier un cadeau)
  /*  public void modifier(Cadeau cadeau) throws SQLException {
        String query = "UPDATE cadeau SET nom = ?, description = ?, disponibilite = ?, invite_id = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, cadeau.getNom());
            stmt.setString(2, cadeau.getDescription());
            stmt.setBoolean(3, cadeau.isDisponibilite());
            stmt.setInt(4, cadeau.getInviteId());
            stmt.setInt(5, cadeau.getId());
            stmt.executeUpdate();
        }
    }*/
    public List<Cadeau> getCadeauxByEvenementCode(String codeUnique) throws SQLException {
        List<Cadeau> cadeaux = new ArrayList<>();
        String query = "SELECT c.* FROM cadeau c JOIN evenement e ON c.invite_id = e.id WHERE e.code_unique = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, codeUnique); // Utiliser code_unique comme paramètre
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Cadeau cadeau = new Cadeau(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getBoolean("disponibilite"),
                        rs.getInt("invite_id") // Utilise invite_id ici
                );
                cadeaux.add(cadeau);
            }
        }

        return cadeaux;
    }
    public String getNomInviteById(String inviteIdStr) {
        try {
            // Conversion de la chaîne en entier
            int inviteId = Integer.parseInt(inviteIdStr);

            // Requête SQL pour récupérer le nom de l'invité
            String req = "SELECT nom FROM invite WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(req)) {
                stmt.setInt(1, inviteId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return rs.getString("nom");
                }
            } catch (SQLException e) {
                System.err.println("Erreur lors de la récupération du nom de l'invité : " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            System.err.println("Format d'identifiant invalide : " + inviteIdStr);
        }
        return null; // Retourner null si l'invité n'est pas trouvé ou en cas d'erreur
    }
    public List<Cadeau> getAllCadeaux() {
        List<Cadeau> cadeaux = new ArrayList<>();
        String query = "SELECT * FROM cadeau";

        try (Connection conn = MyDatabase.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Cadeau cadeau = new Cadeau(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getBoolean("disponibilite"),
                        rs.getInt("invite_id") // Vérifie si invite_id est correct
                );
                cadeaux.add(cadeau);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des cadeaux : " + e.getMessage());
        }

        return cadeaux;
    }

}


