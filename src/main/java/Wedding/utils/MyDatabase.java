package Wedding.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {
    // Paramètres de connexion à la base de données
    private final String URL = "jdbc:mysql://localhost:3307/weddingplanner";
    private final String USERNAME = "root";
    private final String PASSWORD = "";

    // Instance unique de la classe (Singleton)
    private static MyDatabase instance;

    // Connexion partagée
    private static Connection connection;

    // Constructeur privé pour empêcher l'instanciation directe
    private MyDatabase() {
        connect();
    }

    // Méthode pour établir la connexion à la base de données
    private void connect() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Connexion établie");
            }
        } catch (SQLException e) {
            System.out.println("Erreur de connexion : " + e.getMessage());
            throw new RuntimeException("Impossible de se connecter à la base de données.", e);
        }
    }

    // Méthode publique synchronisée pour obtenir l'instance unique (thread-safe)
    public static synchronized MyDatabase getInstance() {
        if (instance == null) {
            instance = new MyDatabase();
        }
        return instance;
    }

    // Méthode pour obtenir la connexion. Vérifie si la connexion est valide avant de la retourner
    public static Connection getConnection() {
        try {
            if (instance == null) {
                instance = getInstance();
            }
            if (connection == null || connection.isClosed()) {
                instance.connect();
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la vérification de l'état de la connexion : " + e.getMessage());
            throw new RuntimeException("La connexion à la base de données a été perdue.", e);
        }
        return connection;
    }
}