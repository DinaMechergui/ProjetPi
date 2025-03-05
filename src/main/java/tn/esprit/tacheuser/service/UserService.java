package tn.esprit.tacheuser.service;

import tn.esprit.tacheuser.models.User;
import tn.esprit.tacheuser.utils.MySQLConnection;
import tn.esprit.tacheuser.utils.SessionManager;
import tn.esprit.tacheuser.utils.EmailSender;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.sql.Statement;
import java.sql.ResultSet;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserService {
    private Connection conn;

    public UserService() {
        conn = MySQLConnection.getInstance().getConnection();
    }

    // Method to check and reconnect if connection is closed
    private void checkConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                System.out.println("🔴 Connexion fermée ! Reconnexion en cours...");
                conn = MySQLConnection.getInstance().getConnection(); // Reconnect if necessary
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur de connexion : " + e.getMessage());
        }
    }

    public void addUser(User user) {
        checkConnection();  // Ensure connection is open before performing operations
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
        checkConnection();  // Ensure connection is open before performing operations
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
        checkConnection();  // Ensure connection is open before performing operations
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
        checkConnection();  // Ensure connection is open before performing operations
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM user"; // Remplace "users" par le nom réel de ta table

        try (Statement stmt = conn.createStatement();
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
    public int getTotalUsers() {
        checkConnection();
        String query = "SELECT COUNT(*) FROM user"; // Assurez-vous que votre table s'appelle bien 'user'
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1); // Retourne le nombre total d'utilisateurs
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération du nombre d'utilisateurs : " + e.getMessage());
        }
        return 0;
    }
    public int getMaleUsers() {
        checkConnection();
        String query = "SELECT COUNT(*) FROM user WHERE gender = 'Male'";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1); // Retourne le nombre d'utilisateurs masculins
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération du nombre d'utilisateurs masculins : " + e.getMessage());
        }
        return 0;
    }

    // Method to get number of female users
    public int getFemaleUsers() {
        checkConnection();
        String query = "SELECT COUNT(*) FROM user WHERE gender = 'Female'";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1); // Retourne le nombre d'utilisateurs féminins
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération du nombre d'utilisateurs féminins : " + e.getMessage());
        }
        return 0;
    }

    // Method to get number of users in a specific age range
    public int getUsersByAgeRange(int minAge, int maxAge) {
        checkConnection();
        String query = "SELECT COUNT(*) FROM user WHERE age BETWEEN ? AND ?";
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, minAge);
            pst.setInt(2, maxAge);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1); // Retourne le nombre d'utilisateurs dans la tranche d'âge
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération du nombre d'utilisateurs par tranche d'âge : " + e.getMessage());
        }
        return 0;
    }
    public User authenticate(String mail, String password) {
        checkConnection();  // Ensure connection is open before performing operations
        String query = "SELECT * FROM user WHERE mail = ? AND password = ?";
        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, mail);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                User user = new User(
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
                SessionManager.setUser(user); // Stocke l'utilisateur connecté
                System.out.println("✅ Connexion réussie pour : " + user.getNom());
                return user;
            } else {
                System.out.println("❌ Identifiants incorrects !");
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de l'authentification : " + e.getMessage());
        }
        return null;
    }

    public User getUserById(int id) {
        checkConnection();  // Ensure connection is open before performing operations
        try {
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
    public boolean updatePassword(String email, String newPassword) {
        String query = "UPDATE user SET password = ? WHERE mail = ?";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, newPassword);  // Remplace l'ancien mot de passe
            statement.setString(2, email);  // Identifie l'utilisateur
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;  // Retourne vrai si la mise à jour a réussi
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password;  // Return plain password if hashing fails (not recommended)
        }
    }
    public boolean sendVerificationCode(String email) {
        String verificationCode = EmailSender.generateVerificationCode(); // Générer un code
        EmailSender.sendEmail(email, verificationCode); // Envoyer l'email
        SessionManager.setVerificationCode(verificationCode); // Stocker le code pour la vérification
        SessionManager.setResetEmail(email); // Stocker l'email temporairement

        System.out.println("✅ Code envoyé à : " + email);
        return true;
    }
    public User getUserByEmail(String email) {
        try {
            String query = "SELECT * FROM user WHERE mail = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("mail"),
                        rs.getString("tel"),
                        rs.getString("gender"),
                        rs.getString("password"),
                        rs.getString("age")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
