package org.example.services;

import org.example.entities.ReservationVoitureDetail;
import org.example.utils.MyDatabase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ServiceResVoitureDetail {
    private final Connection connection;

    public ServiceResVoitureDetail() {
        this.connection = MyDatabase.getInstance().getConnection();
    }

    public List<ReservationVoitureDetail> getReservationsAvecVoiture() throws SQLException {
        List<ReservationVoitureDetail> reservations = new ArrayList<>();

        String req = "SELECT rv.id AS id, rv.client, rv.datedebut, rv.datefin, rv.prixtotal, \n" +
                "       v.idvoiture AS voiture_id, v.marque, v.prix, v.disponible \n" +
                "FROM reservation_voiture rv \n" +
                "JOIN voiture v ON rv.voiture_id = v.idvoiture;\n"; // Jointure avec la table voiture

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(req)) {

            while (rs.next()) {
                ReservationVoitureDetail reservation = new ReservationVoitureDetail(
                        rs.getInt("id"),
                        rs.getString("client"),
                        rs.getString("datedebut"),
                        rs.getString("datefin"),
                        rs.getDouble("prixtotal"),
                        rs.getInt("voiture_id"),
                        rs.getString("marque"),
                        rs.getDouble("prix"),
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
