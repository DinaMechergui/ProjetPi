package org.example.utils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {
    final String URL = "jdbc:mysql://localhost:3306/weddingplanner";
    final String USER = "root";
    final String PASSWORD = "";
    Connection connection;
    static MyDatabase instance;

    public MyDatabase() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to MyDatabase");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public static MyDatabase getInstance() {
        if (instance == null) {
            instance = new MyDatabase();
        }

        return instance;
    }

    public Connection getConnection() {
        return this.connection;
    }

}
