package services;

import entities.event;
import entities.reserve;
import entities.service;
import org.Wedding.utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
// Assurez-vous que cet import est correct

public class ServiceReservation implements IService<reserve> {
    Connection connection;

    public ServiceReservation() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(reserve reservation) throws SQLException {
        // Vérifier si l'event_id existe
        String checkEvent = "SELECT COUNT(*) FROM event WHERE id = ?";
        try (PreparedStatement psEvent = connection.prepareStatement(checkEvent)) {
            psEvent.setInt(1, reservation.getEvent().getId());
            try (ResultSet rsEvent = psEvent.executeQuery()) {
                rsEvent.next();
                if (rsEvent.getInt(1) == 0) {
                    System.out.println(" Erreur : L'ID de l'événement n'existe pas !");
                    return;
                }
            }
        }

        // Vérifier si le service_id existe
        String checkService = "SELECT COUNT(*) FROM service WHERE id = ?";
        try (PreparedStatement psService = connection.prepareStatement(checkService)) {
            psService.setInt(1, reservation.getService().getId());
            try (ResultSet rsService = psService.executeQuery()) {
                rsService.next();
                if (rsService.getInt(1) == 0) {
                    System.out.println(" Erreur : L'ID du service n'existe pas !");
                    return;
                }
            }
        }

        // Si les IDs existent, on ajoute la réservation
        String req = "INSERT INTO reserve (event_id, service_id, date_reservation, statut, prix_total) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, reservation.getEvent().getId());
            ps.setInt(2, reservation.getService().getId());
            ps.setDate(3, new java.sql.Date(reservation.getDateReservation().getTime()));
            ps.setString(4, reservation.getStatut().toString());
            ps.setDouble(5, reservation.getPrixTotal());
            ps.executeUpdate();
            System.out.println(" Réservation ajoutée !");
        }
    }



    @Override
    public void modifier(reserve reservation) throws SQLException {
        String req = "UPDATE reserve SET event_id=?, service_id=?, date_reservation=?, statut=?, prix_total=? WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setInt(1, reservation.getEvent().getId());
        ps.setInt(2, reservation.getService().getId());
        ps.setDate(3, new java.sql.Date(reservation.getDateReservation().getTime()));
        ps.setString(4, reservation.getStatut().toString());
        ps.setDouble(5, reservation.getPrixTotal());
        ps.setInt(6, reservation.getId());
        ps.executeUpdate();
        System.out.println("Réservation mise à jour !");
    }



    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM reserve WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setInt(1, id);
        ps.executeUpdate();
        System.out.println("Réservation supprimée !");
    }

    @Override
    public List<reserve> afficher() throws SQLException {
        List<reserve> reservations = new ArrayList<>();
        String req = "SELECT * FROM reserve";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(req);

        while (rs.next()) {
            reserve reservation = new reserve();
            reservation.setId(rs.getInt("id"));
            reservation.setDateReservation(rs.getDate("date_reservation"));
            reservation.setStatut(reserve.StatutReservation.valueOf(rs.getString("statut").toUpperCase()));
            reservation.setPrixTotal(rs.getDouble("prix_total"));

            // Handle potential null values
            if (reservation.getEvent() != null) {
                reservation.setEvent(new event(rs.getInt("event_id")));
            }

            reservations.add(reservation);
        }

        return reservations;
    }

    public void ajouterService(service myService) throws SQLException {
        String req = "INSERT INTO service (nom, description, prix) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, myService.getNom());
            ps.setString(2, myService.getDescription());
            ps.setDouble(3, myService.getPrix());
            int rowsInserted = ps.executeUpdate();
            if (rowsInserted > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    myService.setId(generatedKeys.getInt(1));  // Récupère l'ID généré
                    System.out.println(" Service ajouté avec ID : " + myService.getId());
                }
            } else {
                System.out.println(" Erreur lors de l'ajout du service.");
            }
        }
    }

}