package Wedding.utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {
    private static MyDatabase instance;
    private static Connection connection;

    public MyDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/weddingplanner", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static MyDatabase getInstance() {
        if (instance == null) {
            instance = new MyDatabase();
        }
        return instance;
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                System.out.println("🔄 Réouverture de la connexion...");
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/weddingplanner", "root", "");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

}
