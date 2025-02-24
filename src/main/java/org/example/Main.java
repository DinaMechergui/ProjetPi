package org.example;

import org.example.entities.*;
import org.example.services.*;
import org.example.utils.MyDatabase;


import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException {


        // Connexion à la base de données
       // MyDatabase db = new MyDatabase();
              /*  ServiceVoiture serviceVoiture = new ServiceVoiture();

                try {
                    // Ajout d'une nouvelle voiture
                    Voiture voiture = new Voiture(1,500,"Toyota", true);
                    serviceVoiture.ajouter(voiture);
                    System.out.println("✅ Voiture ajoutée avec succès !");
                } catch (SQLException e) {
                    System.err.println("❌ Erreur lors de l'ajout de la voiture : " + e.getMessage());
                }

                // Modifier une voiture existante
                Voiture voitureModifiee = new Voiture(20,12,"Peugeot", false);
                voitureModifiee.setId(21); // Assumer que l'ID de la voiture à modifier est 1

                try {
                    serviceVoiture.modifier(voitureModifiee);
                    System.out.println("✅ Voiture après modification !");
                } catch (SQLException e) {
                    System.err.println("❌ Erreur lors de la modification : " + e.getMessage());
                    e.printStackTrace();
                }

                // Supprimer une voiture par ID
                int idASupprimer = 21; // Remplace par l'ID de la voiture que tu veux supprimer

               try {
                    serviceVoiture.supprimer(idASupprimer);
                   System.out.println("✅ Voiture supprimée avec succès !");
                } catch (SQLException e) {
                   System.err.println("❌ Erreur lors de la suppression : " + e.getMessage());
                }
            } */
        // ServiceResVoiture serviceResVoiture = new ServiceResVoiture();

     /*   try {
            // Création d'un objet ReservationVoiture
            ReservationVoiture reservation = new ReservationVoiture(
                    "Toyota Corolla", // Voiture
                    "Jean Dupont",    // Client
                    "2025-03-01",     // Date de début
                    "2025-03-10",     // Date de fin
                    750.0f            // Prix total
            );

            // Ajout de la réservation à la base de données
            serviceResVoiture.ajouter(reservation);
            System.out.println("✅ Réservation ajoutée avec succès !");

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout de la réservation : " + e.getMessage());
            e.printStackTrace();
        }
        ReservationVoiture reservation = new ReservationVoiture("Toyota Corolla", "Sami BenFoulen", "2025-03-01", "2025-03-07", 500);
        reservation.setId(1); // Assumer que l'ID de la réservation à modifier est 1

        try {
            // Modifier la réservation de voiture dans la base de données
            serviceResVoiture.modifier(reservation);
            System.out.println("✅ Réservation modifiée avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification de la réservation : " + e.getMessage());
        }
        int idASupprimer = 1; // Remplace par l'ID de la réservation que tu veux supprimer

        try {
            // Supprimer la réservation de voiture dans la base de données
            serviceResVoiture.supprimer(idASupprimer);
            System.out.println("✅ Suppression terminée !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression : " + e.getMessage());
        }*/


        ServiceHebergement serviceHebergement = new ServiceHebergement();

             /*   try {
                    // Création d'un objet Hebergement
                    Hebergement hebergement = new Hebergement(
                            "Hotel Lux", "123 Rue Paradis, Paris", 120.0,true
                    );

                    // Ajout de l'hébergement à la base de données
                    serviceHebergement.ajouter(hebergement);
                    System.out.println("✅ Hébergement ajouté avec succès !");
                } catch (SQLException e) {
                    System.err.println("❌ Erreur lors de l'ajout de l'hébergement : " + e.getMessage());
                    e.printStackTrace();
                }*/

            /*   // Modification de l'hébergement
                Hebergement hebergementModifie = new Hebergement("Hotel Royal", "456 Avenue Élégance, Lyon", 150.0, false);
                hebergementModifie.setId(1); // Supposons que l'ID de l'hébergement à modifier est 1

                try {
                    serviceHebergement.modifier(hebergementModifie);
                    System.out.println("✅ Hébergement modifié avec succès !");
                } catch (SQLException e) {
                    System.err.println("❌ Erreur lors de la modification de l'hébergement : " + e.getMessage());
                }

               // Suppression d'un hébergement
                int idASupprimer = 1; // Remplace par l'ID de l'hébergement que tu veux supprimer

                try {
                    serviceHebergement.supprimer(idASupprimer);
                    System.out.println("✅ Suppression terminée !");
                } catch (SQLException e) {
                    System.err.println("❌ Erreur lors de la suppression : " + e.getMessage());
                }*/

        // Crée une instance de la classe qui gère les réservations d'hébergement
        ServiceResHebergement manager = new ServiceResHebergement();

        // Création d'une nouvelle réservation d'hébergement
       /* ReservationHebergement reservation1 = new ReservationHebergement(1, "Client1", "2025-05-01", "2025-05-10", 500.0f);

        try {
            // Ajout de la réservation
            manager.ajouter(reservation1);

            // Affichage des réservations actuelles
            manager.afficherReservationHebergement();

            // Modification de la réservation
            reservation1.setPrixTotal(550.0f);  // Mise à jour du prix
            reservation1.setDateFin("2025-05-12");  // Mise à jour de la date de fin
            manager.modifier(reservation1);

            // Affichage des réservations après modification
            manager.afficherReservationHebergement();

            // Suppression de la réservation
            manager.supprimer(reservation1.getId());

            // Affichage des réservations après suppression
            manager.afficherReservationHebergement();
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL: " + e.getMessage());
        }*/

        ServiceResVoiture serviceResVoiture = new ServiceResVoiture();

        try {
            List<ReservationVoitureDetail> reservations = serviceResVoiture.getReservationsAvecVoiture();

            if (reservations.isEmpty()) {
                System.out.println("⚠ Aucune réservation trouvée !");
            } else {
                for (ReservationVoitureDetail reservation : reservations) {
                    System.out.println("📌 Réservation ID: " + reservation.getId());
                    System.out.println("👤 Client: " + reservation.getClient());
                    System.out.println("📅 Date Début: " + reservation.getDateDebut());
                    System.out.println("📅 Date Fin: " + reservation.getDateFin());
                    System.out.println("💰 Prix Total: " + reservation.getPrixTotal() + "€");
                    System.out.println("🚗 Voiture: " + reservation.getMarque() + " | 💵 Prix: " + reservation.getPrixTotal() + "€/jour | " +
                            (reservation.isDisponible() ? "✅ Disponible" : "❌ Indisponible"));
                    System.out.println("------------------------------------");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur : " + e.getMessage());
        }

        ServiceResHebergement serviceResHebergement = new ServiceResHebergement();

        try {
            List<ReservationHebergementDetail> reservations = serviceResHebergement.getReservationsAvecHebergement();

            if (reservations.isEmpty()) {
                System.out.println("⚠ Aucune réservation trouvée !");
            } else {
                for (ReservationHebergementDetail res : reservations) {
                    System.out.println("📌 Réservation ID: " + res.getId());
                    System.out.println("👤 Client: " + res.getClient());
                    System.out.println("📅 Date Début: " + res.getDateDebut());
                    System.out.println("📅 Date Fin: " + res.getDateFin());
                    System.out.println("💰 Prix Total: " + res.getPrixTotal() + "€");
                    System.out.println("🏨 Hébergement: " + res.getNomHebergement() +
                            " | 📍 Adresse: " + res.getAdresse() +
                            " | 💵 Prix: " + res.getPrixParNuit() + "€/nuit" +
                            " | " + (res.isDisponible() ? "✅ Disponible" : "❌ Indisponible"));
                    System.out.println("--------------------------------------------------");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'affichage des réservations : " + e.getMessage());
        }


    }
}

