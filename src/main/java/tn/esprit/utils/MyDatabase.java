package tn.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {
    private static MyDatabase instance;
    private Connection connection;

    // Informations de connexion (à externaliser dans un fichier de configuration)
    private static final String URL = "jdbc:mysql://localhost:3306/weddingplanner";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // Constructeur privé pour empêcher l'instanciation directe
    private MyDatabase() {
        try {
            // Charger le pilote JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Établir la connexion
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connexion à la base de données établie avec succès.");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Pilote JDBC introuvable : " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion à la base de données : " + e.getMessage());
        }
    }

    // Méthode pour obtenir l'instance unique (Singleton)
    public static synchronized MyDatabase getInstance() {
        if (instance == null) {
            instance = new MyDatabase();
        }
        return instance;
    }

    // Méthode pour obtenir la connexion
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                System.out.println("🔄 Réouverture de la connexion...");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la réouverture de la connexion : " + e.getMessage());
        }
        return connection;
    }

    // Méthode pour fermer la connexion
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("✅ Connexion fermée avec succès.");
            } catch (SQLException e) {
                System.err.println("❌ Erreur lors de la fermeture de la connexion : " + e.getMessage());
            }
        }
    }
}