package org.example.services;

import org.example.entities.ReservationHebergementDetail;
import org.Wedding.utils.MyDatabase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ServiceResHebergementDetail {
    private final Connection connection;

    public ServiceResHebergementDetail(Connection connection) {
        this.connection = MyDatabase.getInstance().getConnection();
    }

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
                        rs.getDate("datedebut"),
                        rs.getDate("datefin"),
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