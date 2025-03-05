package org.example.services;

import org.example.entities.ReservationHebergement;
import org.example.entities.ReservationHebergementDetail;
import org.Wedding.utils.MyDatabase;

import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class ServiceResHebergement implements IResHebergement<ReservationHebergement> {
    private final Connection connection;

    public ServiceResHebergement() {
        this.connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(ReservationHebergement reservation) throws SQLException {
        String req = "INSERT INTO reservation_hebergement (idheb, utilisateur, datedebut, datefin, prixtotal) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(req)) {
            // Convertir java.util.Date en java.sql.Date
            java.sql.Date dateDebut = new java.sql.Date(reservation.getDateDebut().getTime());
            java.sql.Date dateFin = new java.sql.Date(reservation.getDateFin().getTime());

            // Définir les paramètres de la requête
            statement.setInt(1, reservation.getIdheb());
            statement.setString(2, reservation.getUtilisateur());
            statement.setDate(3, dateDebut);
            statement.setDate(4, dateFin);
            statement.setFloat(5, reservation.getPrixTotal());

            // Exécuter la requête
            statement.executeUpdate();
            System.out.println("✅ Réservation d'hébergement ajoutée avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout de la réservation : " + e.getMessage());
            throw e; // Propager l'exception pour une gestion centralisée
        }
    }

    public List<ReservationHebergementDetail> getReservationsAvecHebergement() throws SQLException {
        List<ReservationHebergementDetail> reservations = new ArrayList<>();

        String req = "SELECT rh.id AS res_id, rh.utilisateur, rh.datedebut, rh.datefin, rh.prixtotal, " +
                "h.idheb AS hebergement_id, h.nom, h.adresse, h.prixParNuit, h.disponible " +
                "FROM reservation_hebergement rh " +
                "JOIN hebergement h ON rh.idheb = h.idheb";

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(req)) {

            while (rs.next()) {
                // Convertir java.sql.Date en java.util.Date
                java.util.Date dateDebut = new java.util.Date(rs.getDate("datedebut").getTime());
                java.util.Date dateFin = new java.util.Date(rs.getDate("datefin").getTime());

                ReservationHebergementDetail reservation = new ReservationHebergementDetail(
                        rs.getInt("res_id"),
                        rs.getString("utilisateur"), // Utilisateur au lieu de client
                        dateDebut,
                        dateFin,
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
            throw e;
        }

        return reservations;
    }

    public boolean estReserve(int idHebergement, LocalDate dateDebut, LocalDate dateFin) throws SQLException {
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

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true; // L'hébergement est déjà réservé
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la vérification de la disponibilité : " + e.getMessage());
            throw e;
        }
        return false; // L'hébergement est disponible
    }
}