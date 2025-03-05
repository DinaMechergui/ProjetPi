package org.example.services;


import org.example.entities.ReservationHebergement;
import org.example.entities.ReservationHebergementDetail;
import Wedding.utils.MyDatabase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServiceResHebergement implements IResHebergement<ReservationHebergement> {
    private final Connection connection;

    public ServiceResHebergement() {
        this.connection = MyDatabase.getInstance().getConnection();
    }



    @Override
    public void ajouter(ReservationHebergement reservation) throws SQLException {
        String req = "INSERT INTO reservation_hebergement (idheb, client, datedebut, datefin, prixtotal) VALUES ('"
                + reservation.getIdheb() + "', '"
                + reservation.getClient() + "', '"
                + reservation.getDateDebut() + "', '"
                + reservation.getDateFin() + "', "
                + reservation.getPrixTotal() + ")";

        Statement statement = this.connection.createStatement();
        statement.executeUpdate(req);
        System.out.println("✅ Réservation d'hébergement ajoutée avec succès !");
    }
    public boolean estReserve(int idHebergement, LocalDate dateDebut, LocalDate dateFin) {
        String requete = "SELECT COUNT(*) FROM reservation_hebergement WHERE idheb = ? " +
                "AND ((datedebut BETWEEN ? AND ?) OR (datefin BETWEEN ? AND ?) " +
                "OR (? BETWEEN datedebut AND datefin) OR (? BETWEEN datedebut AND datefin))";

        try (PreparedStatement pst = connection.prepareStatement(requete)) {
            pst.setInt(1, idHebergement);
            pst.setDate(2, java.sql.Date.valueOf(dateDebut));
            pst.setDate(3, java.sql.Date.valueOf(dateFin));
            pst.setDate(4, java.sql.Date.valueOf(dateDebut));
            pst.setDate(5, java.sql.Date.valueOf(dateFin));
            pst.setDate(6, java.sql.Date.valueOf(dateDebut));
            pst.setDate(7, java.sql.Date.valueOf(dateFin));

            ResultSet rs = pst.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true; // L'hébergement est déjà réservé
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // L'hébergement est disponible
    }

    /*public void modifier(ReservationHebergement reservation) throws SQLException {
        String req = "UPDATE reservation_hebergement SET idheb = ?, client = ?, datedebut = ?, datefin = ?, prixtotal = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            // On définit les paramètres de la requête
            preparedStatement.setInt(1, reservation.getIdheb());
            preparedStatement.setString(2, reservation.getClient());
            preparedStatement.setString(3, reservation.getDateDebut());
            preparedStatement.setString(4, reservation.getDateFin());
            preparedStatement.setFloat(5, reservation.getPrixTotal());
            preparedStatement.setInt(6, reservation.getId());

            // Exécution de la requête de mise à jour
            preparedStatement.executeUpdate();
            System.out.println("✅ Réservation d'hébergement modifiée avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification de la réservation : " + e.getMessage());
        }
    }

    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM reservation_hebergement WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            // Définition du paramètre de l'ID
            preparedStatement.setInt(1, id);

            // Exécution de la requête
            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("✅ Réservation d'hébergement supprimée avec succès !");
            } else {
                System.out.println("⚠ Aucune réservation trouvée avec cet ID !");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression de la réservation : " + e.getMessage());
        }
    }

    public void afficherReservationHebergement() {
        List<ReservationHebergement> reservations = getAllReservation(); // Récupérer toutes les réservations

        if (reservations.isEmpty()) {
            System.out.println("⚠ Aucun hébergement trouvé !");
        } else {
            System.out.println("🏨 Liste des hébergements réservés :");
            for (ReservationHebergement reservationHebergement : reservations) {
                System.out.println("ID: " + reservationHebergement.getId() +
                        " | Hebergement ID: " + reservationHebergement.getIdheb() +
                        " | Client: " + reservationHebergement.getClient() +
                        " | Date début: " + reservationHebergement.getDateDebut() +
                        " | Date fin: " + reservationHebergement.getDateFin() +
                        " | Prix total: " + reservationHebergement.getPrixTotal());
            }
        }
    }

    private List<ReservationHebergement> getAllReservation() {
        List<ReservationHebergement> reservations = new ArrayList<>();
        String query = "SELECT * FROM reservation_hebergement"; // Vérifie que "reservation_hebergement" est bien le nom de ta table

        try (Connection conn = MyDatabase.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                ReservationHebergement reservationHebergement = new ReservationHebergement(
                        rs.getInt("hebergement_id"),
                        rs.getString("client"),
                        rs.getString("date_debut"),
                        rs.getString("date_fin"),
                        rs.getFloat("prix_total")
                );
                reservationHebergement.setId(rs.getInt("id"));
                reservations.add(reservationHebergement);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des réservations d'hébergement : " + e.getMessage());
        }

        return reservations;
    }

*/



    public List<ReservationHebergementDetail> getReservationsAvecHebergement() throws SQLException {
        List<ReservationHebergementDetail> reservations = new ArrayList<>();

        String req = "SELECT rh.id AS res_id, rh.client, rh.datedebut, rh.datefin, rh.prixtotal, " +
                "h.idheb AS hebergement_id, h.nom, h.adresse, h.prixParNuit, h.disponible " +
                "FROM reservation_hebergement rh " +
                "JOIN hebergement h ON rh.idheb = h.idheb";

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(req)) {

            while (rs.next()) {
                ReservationHebergementDetail reservation = new ReservationHebergementDetail(
                        rs.getInt("res_id"),
                        rs.getString("client"),
                        rs.getString("datedebut"),
                        rs.getString("datefin"),
                        rs.getDouble("prixtotal"),
                        rs.getInt("hebergement_id"),
                        rs.getString("nom"),
                        rs.getString("adresse"),
                        rs.getDouble("prixParNuit"),
                        rs.getBoolean("disponible")
                );
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des réservations : " + e.getMessage());
        }

        return reservations;
    }
}


