package org.example.services;

import javafx.fxml.FXML;
import org.example.entities.Hebergement;
import org.example.entities.ReservationVoiture;
import org.example.entities.ReservationVoitureDetail;

import org.Wedding.utils.MyDatabase;




import java.awt.event.ActionEvent;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServiceResVoiture implements IRes<ReservationVoiture> {
    private final Connection connection;

    public ServiceResVoiture() {
        this.connection = MyDatabase.getInstance().getConnection();
    }
  /*  public String getMarqueById(int idVoiture) throws SQLException {
        String marque = null;
        String query = "SELECT marque FROM voiture WHERE idvoiture = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, idVoiture);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    marque = rs.getString("marque");
                } else {
                    System.out.println("❌ Aucune voiture trouvée avec l'ID : " + idVoiture);
                }
            }
        }
        return marque;
    }*/


  /*  public void ajouter(ReservationVoiture reservation) throws SQLException {
        // Récupérer la marque de la voiture à partir de son ID
        String marqueVoiture = "vw";

        if (marqueVoiture == null) {
            throw new SQLException("❌ Aucune voiture trouvée avec l'ID " + reservation.getIdvoiture());
        }

        String req = "INSERT INTO reservation_voiture (voiture, client, datedebut, datefin, prixtotal, voiture_id) VALUES ('"
                + reservation.getVoiture() + "', '"
                + reservation.getClient() + "', '"
                + reservation.getDatedebut() + "', '"
                + reservation.getDatefin() + "', "
                + reservation.getPrixtotal() + ","
                + reservation.getIdvoiture() + ")";
        System.out.println("🚗 ID voiture enregistré dans l'objet ReservationVoiture : " + reservation.getIdvoiture());

        System.out.println("🔍 Requête SQL exécutée : " + req);

    }*/
 /* @FXML
void ajouterResVoiture(ActionEvent event) {
      try {
          // Récupérer les valeurs des champs du formulaire
       /*   String client = nompretf.getText();
          LocalDate datedebut = ddtf.getValue();
          LocalDate datefin = dftf.getValue();
          float prixtotal = Float.parseFloat(prixttf.getText());

          // Vérifier si les champs sont bien remplis
          if (client.isEmpty() || datedebut == null || datefin == null || prixtotal <= 0) {
              System.out.println("⚠️ Veuillez remplir tous les champs correctement !");
              return;
          }

          // Création de l'objet ReservationVoiture (avec une valeur fictive pour voiture et voiture_id)
          ReservationVoiture reservation = new ReservationVoiture("VoitureTest", client, java.sql.Date.valueOf(datedebut), java.sql.Date.valueOf(datefin), prixtotal);
          reservation.setIdvoiture(1); // Pour l'instant, voiture_id est mis à 1 manuellement

          // Ajouter la réservation à la base de données via le service
          ServiceResVoiture service = new ServiceResVoiture();
          service.ajouter(reservation);

          System.out.println("✅ Réservation ajoutée avec succès !");
      } catch (NumberFormatException e) {
          System.out.println("❌ Erreur : Prix total invalide !");
      } catch (SQLException e) {
          System.out.println("❌ Problème lors de l'ajout à la base de données : " + e.getMessage());
          e.printStackTrace(); // Afficher plus de détails sur l'erreur
      }
  }*/


    public void ajouter(ReservationVoiture reservation) throws SQLException {
        String req = "INSERT INTO reservation_voiture (voiture, client, datedebut, datefin, prixtotal, voiture_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = MyDatabase.getInstance().getConnection();
             PreparedStatement pst = con.prepareStatement(req)) {

            // Vérifier les valeurs avant exécution
            System.out.println("🔍 Tentative d'insertion avec :");
            System.out.println("Voiture : " + reservation.getVoiture());
            System.out.println("Client : " + reservation.getClient());
            System.out.println("Date début : " + reservation.getDatedebut());
            System.out.println("Date fin : " + reservation.getDatefin());
            System.out.println("Prix total : " + reservation.getPrixtotal());
            System.out.println("Voiture ID : " + reservation.getIdvoiture());

            // Vérification des valeurs nulles
            if (reservation.getVoiture() == null || reservation.getClient() == null ||
                    reservation.getDatedebut() == null || reservation.getDatefin() == null ||
                    reservation.getIdvoiture() == 0) {
                throw new SQLException("❌ Certains champs sont nulls ou invalides !");
            }

            // Affecter les paramètres à la requête
            pst.setString(1, reservation.getVoiture());
            pst.setString(2, reservation.getClient());
            pst.setDate(3, new java.sql.Date(reservation.getDatedebut().getTime()));
            pst.setDate(4, new java.sql.Date(reservation.getDatefin().getTime()));
            pst.setFloat(5, reservation.getPrixtotal());
            pst.setInt(6, reservation.getIdvoiture());

            System.out.println("🔍 Requête SQL exécutée : " + pst.toString());

            pst.executeUpdate();
            System.out.println("✅ Réservation insérée avec succès !");
        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL lors de l'insertion : " + e.getMessage());
            throw e;
        }
    }


    public void modifier(ReservationVoiture reservation) throws SQLException {
        String req = "UPDATE reservation_voiture SET voiture = ?, client = ?, datedebut = ?, datefin = ?, prixtotal = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            // On définit les paramètres de la requête
            preparedStatement.setString(1, reservation.getVoiture());
            preparedStatement.setString(2, reservation.getClient());

            // Conversion de LocalDate en java.sql.Date
            preparedStatement.setDate(3, new java.sql.Date(reservation.getDatedebut().getTime())); // Pour datedebut
            preparedStatement.setDate(4, new java.sql.Date(reservation.getDatefin().getTime())); // Pour datefin

            preparedStatement.setFloat(5, reservation.getPrixtotal());
            preparedStatement.setInt(6, reservation.getId());


            // Exécution de la requête de mise à jour
            preparedStatement.executeUpdate();
            System.out.println("✅ Réservation modifiée avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification de la réservation : " + e.getMessage());
        }
    }
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM reservation_voiture WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            // Définition du paramètre de l'ID
            preparedStatement.setInt(1, id);

            // Exécution de la requête
            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("✅ Réservation supprimée avec succès !");
            } else {
                System.out.println("⚠ Aucune réservation trouvée avec cet ID !");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression de la réservation : " + e.getMessage());
        }
    }

    public void afficherReservationVoiture() {
        List<ReservationVoiture> reservations = getAllResevation(); // Récupérer tous les hébergements

        if (reservations.isEmpty()) {
            System.out.println("⚠ Aucun hébergement trouvé !");
        } else {
            System.out.println("🏨 Liste des hébergements disponibles :");
            for (ReservationVoiture reservationVoiture : reservations) {
                System.out.println("ID: " + reservationVoiture.getId() +
                        " | voiture: " + reservationVoiture.getVoiture() +
                        " | client: " + reservationVoiture.getClient() +
                        " | date debut: " + reservationVoiture.getDatedebut()  +
                        " | date fin: " + reservationVoiture.getDatefin()  +
                        " | prix totale: " + reservationVoiture.getPrixtotal() );
            }
        }
    }

    private List<ReservationVoiture> getAllResevation() {
        List<ReservationVoiture> reservations  = new ArrayList<>();
        String query = "SELECT * FROM reservation_voiture"; // Vérifie que "hebergement" est bien le nom de ta table

        try (Connection conn = MyDatabase.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                ReservationVoiture reservationVoiture= new ReservationVoiture(
                        rs.getString("voiture"),
                        rs.getString("client"),
                        rs.getDate("date debut"),
                        rs.getDate("date fin"),
                        rs.getInt("prixtotal")
                );
                reservationVoiture.setId(rs.getInt("id"));
                // reservationVoiture.add(reservationVoiture);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des hébergements : " + e.getMessage());
        }

        return reservations;

    }
    public List<ReservationVoitureDetail> getReservationsAvecVoiture() throws SQLException {
        List<ReservationVoitureDetail> reservations = new ArrayList<>();

        String req = "SELECT rv.id AS res_id, rv.client, rv.datedebut, rv.datefin, rv.prixtotal, " +
                "v.idvoiture AS voiture_id, v.marque, v.prix, v.disponible " +
                "FROM reservation_voiture rv " +
                "JOIN voiture v ON rv.voiture_id = v.idvoiture";

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(req)) {

            while (rs.next()) {
                ReservationVoitureDetail reservation = new ReservationVoitureDetail(
                        rs.getInt("res_id"),
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


    public boolean estReservee(int idVoiture, LocalDate debut, LocalDate fin) throws SQLException {
        String query = "SELECT COUNT(*) FROM reservation_voiture WHERE voiture_id = ? AND " +
                "(? BETWEEN datedebut AND datefin OR ? BETWEEN datedebut AND datefin OR " +
                "datedebut BETWEEN ? AND ? OR datefin BETWEEN ? AND ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idVoiture);
            pstmt.setDate(2, java.sql.Date.valueOf(debut));
            pstmt.setDate(3, java.sql.Date.valueOf(fin));
            pstmt.setDate(4, java.sql.Date.valueOf(debut));
            pstmt.setDate(5, java.sql.Date.valueOf(fin));
            pstmt.setDate(6, java.sql.Date.valueOf(debut));
            pstmt.setDate(7, java.sql.Date.valueOf(fin));

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

}