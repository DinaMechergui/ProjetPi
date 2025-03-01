package tn.esprit.tacheuser.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection {
    private static MySQLConnection instance;
    private Connection connection;
    private final String URL = "jdbc:mysql://localhost:3306/tache user"; // ✅ Correction du nom de la base
    private final String USER = "root";
    private final String PASSWORD = "";

    private MySQLConnection() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connexion à la base de données établie !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion à la base de données : " + e.getMessage());
            e.printStackTrace(); // ✅ Afficher le détail de l'erreur
        }
    }

    public static MySQLConnection getInstance() {
        try {
            if (instance == null || instance.connection == null || instance.connection.isClosed()) {
                instance = new MySQLConnection();
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la vérification de la connexion : " + e.getMessage());
            e.printStackTrace();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération de la connexion : " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }
}
