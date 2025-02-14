package services;

import java.util.Date;
import entities.event;
import org.Wedding.utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceEvent implements IService<event> {
    Connection connection;

    public ServiceEvent() {
        connection = MyDatabase.getInstance().getConnection();
    }



    @Override
    public void ajouter(event event) throws SQLException {
        String req = "INSERT INTO event (nom, date, lieu, description) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, event.getNom());
            ps.setDate(2, new java.sql.Date(event.getDate().getTime()));
            ps.setString(3, event.getLieu());
            ps.setString(4, event.getDescription());

            int rowsInserted = ps.executeUpdate(); // Exécuter d'abord l'insertion
            if (rowsInserted > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    event.setId(generatedKeys.getInt(1)); // Récupérer l'ID généré
                    System.out.println("Événement ajouté avec ID : " + event.getId());
                }
            } else {
                System.out.println("Erreur lors de l'ajout de l'événement.");
            }
        }
    }





    @Override
    public void modifier(event event) throws SQLException {
        String req = "UPDATE event SET nom=?, date=?, lieu=?, description=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, event.getNom());
            ps.setDate(2, new java.sql.Date(event.getDate().getTime()));
            ps.setString(3, event.getLieu());
            ps.setString(4, event.getDescription());
            ps.setInt(5, event.getId());
            ps.executeUpdate();
            System.out.println("Événement mis à jour !");
        }
    }


    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM event WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Événement supprimé !");
        }
    }

    @Override
    public List<event> afficher() throws SQLException {
        List<event> events = new ArrayList<>();
        String req = "SELECT * FROM event";
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(req)) {

            while (rs.next()) {
                event event = new event(rs.getInt("id"), rs.getString("nom"), rs.getDate("date"),
                        rs.getString("lieu"), rs.getString("description"),
                        rs.getString("statut"));
                events.add(event);
            }
        }
        return events;
    }

}

