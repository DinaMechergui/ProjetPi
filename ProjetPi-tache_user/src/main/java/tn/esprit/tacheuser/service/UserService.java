package tn.esprit.tacheuser.service;

import tn.esprit.tacheuser.models.User;
import tn.esprit.tacheuser.utils.MySQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.sql.Statement;
import java.sql.ResultSet;


public class UserService {
    private Connection conn;

    public UserService() {
        conn = MySQLConnection.getInstance().getConnection();
    }

    public void addUser(User user) {
        String query = "INSERT INTO user (nom, prenom, mail, tel, gender, password, age, confirmpassword, status, role) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, user.getNom());
            pst.setString(2, user.getPrenom());
            pst.setString(3, user.getMail());
            pst.setString(4, user.getTel());
            pst.setString(5, user.getGender());
            pst.setString(6, user.getPassword());
            pst.setString(7, user.getAge());
            pst.setString(8, user.getConfirmpassword());
            pst.setString(9, user.getStatus());
            pst.setString(10, user.getRole());

            int rowsInserted = pst.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("✅ Utilisateur ajouté avec succès !");
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de l'ajout de l'utilisateur : " + e.getMessage());
        }
    }
    public void deleteUser(int userId) {
        String query = "DELETE FROM user WHERE id = ?";

        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, userId);

            int rowsDeleted = pst.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("✅ Utilisateur supprimé avec succès !");
            } else {
                System.out.println("⚠️ Aucun utilisateur trouvé avec cet ID !");
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la suppression de l'utilisateur : " + e.getMessage());
        }
    }
    public void updateUser(User user) {
        String query = "UPDATE user SET nom = ?, prenom = ?, mail = ?, tel = ?, gender = ?, age = ?, role = ? WHERE id = ?";

        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, user.getNom());
            pst.setString(2, user.getPrenom());
            pst.setString(3, user.getMail());
            pst.setString(4, user.getTel());
            pst.setString(5, user.getGender());
            pst.setString(6, user.getAge());
            pst.setString(7, user.getRole());
            pst.setInt(8, user.getId());

            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Utilisateur mis à jour avec succès !");
            } else {
                System.out.println("⚠️ Aucun utilisateur trouvé avec cet ID !");
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la mise à jour de l'utilisateur : " + e.getMessage());
        }
    }
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM user"; // Remplace "users" par le nom réel de ta table

        try (Connection conn = MySQLConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("mail"),
                        rs.getString("tel"),
                        rs.getString("gender"),
                        rs.getString("age"),
                        rs.getString("password")
                );
                users.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public User authenticate(String mail, String password) {
        String query = "SELECT * FROM user WHERE mail = ? AND password = ?";
        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, mail);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("mail"),
                        rs.getString("tel"),
                        rs.getString("gender"),
                        rs.getString("age"),
                        rs.getString("password")
                );
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de l'authentification : " + e.getMessage());
        }
        return null;
    }

    public User getUserById(int id) {
        try {
            if (conn == null || conn.isClosed()) {
                System.out.println("🔴 Connexion fermée ! Reconnexion en cours...");
                conn = MySQLConnection.getInstance().getConnection(); // Assurez-vous que DatabaseConnection gère bien l'instance
            }

            String query = "SELECT * FROM user WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("mail"),
                        rs.getString("tel"),
                        rs.getString("gender"),
                        rs.getString("age"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
