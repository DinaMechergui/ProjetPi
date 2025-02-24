package tn.esprit.services;


import tn.esprit.entities.Invite;
import tn.esprit.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceInvite {
    private final Connection connection;

    public ServiceInvite() {
        this.connection = MyDatabase.getInstance().getConnection();
    }

    public void ajouter(Invite invite) throws SQLException {
        String req = "INSERT INTO invite (nom, prenom, email, telephone, confirmation,evenement_id) VALUES ('"
                + invite.getNom() + "', '"
                + invite.getPrenom() + "', '"
                + invite.getEmail() + "', '"
                + invite.getTelephone() + "', "
                + (invite.isConfirmation() ? "1" : "0") +", "
                + invite.getEvenementId()  +  ")";



        Statement statement = this.connection.createStatement();
        statement.executeUpdate(req);
        System.out.println("🚗 Voiture ajoutée avec succès !");
    }
    public void modifier(Invite invite) throws SQLException {
        String query = "UPDATE invite SET nom = ?, prenom = ?, email = ?, telephone = ?, confirmation = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, invite.getNom());
            stmt.setString(2, invite.getPrenom());
            stmt.setString(3, invite.getEmail());
            stmt.setString(4, invite.getTelephone());
            stmt.setBoolean(5, invite.isConfirmation());  // Vérifie bien que c'est ici que la confirmation est prise en compte
            stmt.setInt(6, invite.getId());

            stmt.executeUpdate();
        }
    }


    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM invite WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            // Définition du paramètre de l'ID
            preparedStatement.setInt(1, id);

            // Exécution de la requête
            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("✅ Invité supprimé avec succès !");
            } else {
                System.out.println("⚠ Aucun invité trouvé avec cet ID !");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression de l'invité : " + e.getMessage());
        }
    }

    public List<Invite> afficher() throws SQLException {
        List<Invite> invites = new ArrayList<>();
        String query = "SELECT id, nom, prenom, email, telephone, confirmation, evenement_id FROM invite";

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            Invite invite = new Invite(
            rs.getInt("id"), // L'ID est récupéré mais pas affiché
            rs.getString("nom"),
                    rs.getString("prenom"),
             rs.getString("email"),
            rs.getString("telephone"),
            rs.getBoolean("confirmation"),
            rs.getInt("evenement_id")
            );

            // Ajouter l'invité à la liste
                    invites.add(invite);
        }

        return invites;
    }


    // Méthode pour afficher les invités en console
    public void afficherInvites() {
        List<Invite> invites;
        try {
            invites = afficher();
            if (invites.isEmpty()) {
                System.out.println("⚠ Aucun invité trouvé !");
            } else {
                System.out.println("📜 Liste des invités :");
                for (Invite invite : invites) {
                    System.out.println("ID: " + invite.getId() +
                            " | Nom: " + invite.getNom() +
                            " | Prénom: " + invite.getPrenom() +
                            " | Email: " + invite.getEmail() +
                            " | Téléphone: " + invite.getTelephone() +
                            " | Confirmation: " + (invite.isConfirmation() ? "✅ Oui" : "❌ Non")
                    +"id evenement"+ invite.getEvenementId());
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'affichage des invités : " + e.getMessage());
        }
    }


        }




