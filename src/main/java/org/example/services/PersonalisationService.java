package org.example.services;

import org.example.entities.Personalisation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonalisationService {

    private Connection connection;

    public PersonalisationService() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/weddingplanner";
        String user = "root";
        String password = "";
        connection = DriverManager.getConnection(url, user, password);
    }

    // Helper method to map ResultSet to Personalisation
    private Personalisation mapResultSetToPersonalisation(ResultSet rs) throws SQLException {
        Personalisation personalisation = new Personalisation();
        personalisation.setId(rs.getInt("id"));
        personalisation.setReservationId(rs.getInt("reservation_id"));
        personalisation.setDecoration(rs.getBoolean("decoration"));
        personalisation.setBreakfast(rs.getBoolean("breakfast"));
        personalisation.setSpa(rs.getBoolean("spa"));
        return personalisation;
    }

    // Create a new personalisation
    public void createPersonalisation(Personalisation personalisation) throws SQLException {
        String query = "INSERT INTO Personalisation ( decoration, breakfast, spa) VALUES ( ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setBoolean(1, personalisation.isDecoration());
            stmt.setBoolean(2, personalisation.isBreakfast());
            stmt.setBoolean(3, personalisation.isSpa());
            stmt.executeUpdate();
        }
    }

    // Fetch all personalisations
    public List<Personalisation> getAllPersonalisation() throws SQLException {
        List<Personalisation> personalisations = new ArrayList<>();
        String query = "SELECT * FROM Personalisation";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                personalisations.add(mapResultSetToPersonalisation(rs));
            }
        }
        return personalisations;
    }

    // Fetch a personalisation by ID
    public Personalisation getPersonalisationById(int id) throws SQLException {
        String query = "SELECT * FROM Personalisation WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToPersonalisation(rs);
            }
        }
        return null;
    }

    // Update a personalisation
    public void updatePersonalisation(int id, Personalisation personalisation) throws SQLException {
        String query = "UPDATE Personalisation SET reservation_id = ?, decoration = ?, breakfast = ?, spa = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, personalisation.getReservationId());
            stmt.setBoolean(2, personalisation.isDecoration());
            stmt.setBoolean(3, personalisation.isBreakfast());
            stmt.setBoolean(4, personalisation.isSpa());
            stmt.setInt(5, id);
            stmt.executeUpdate();
        }
    }

    // Delete a personalisation
    public void deletePersonalisation(int id) throws SQLException {
        String query = "DELETE FROM Personalisation WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // Close the database connection
    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}