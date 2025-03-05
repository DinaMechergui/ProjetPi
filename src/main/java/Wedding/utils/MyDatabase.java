package Wedding.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {
    private static MyDatabase instance;
    private Connection connection;  // Non-static to avoid conflicts

    private static final String URL = "jdbc:mysql://localhost:3306/weddingplanner";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // 🔒 Private Constructor (Enforces Singleton)
    public MyDatabase() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Database Connected Successfully");
        } catch (SQLException e) {
            System.err.println("❌ Database Connection Failed: " + e.getMessage());
        }
    }

    // 🔄 Ensure only one instance exists
    public static synchronized MyDatabase getInstance() {
        if (instance == null) {
            instance = new MyDatabase();
        }
        return instance;
    }

    // 🛠 Get Database Connection (Auto-Reconnect if Closed)
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                System.out.println("🔄 Reconnecting to Database...");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error Reconnecting to Database: " + e.getMessage());
        }
        return connection;
    }

    // ❗ Properly Close Connection
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🚪 Database Connection Closed");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error Closing Connection: " + e.getMessage());
        }
    }
}
