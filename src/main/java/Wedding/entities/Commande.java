package Wedding.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Commande {
    private int id;
    private String utilisateur;
    private LocalDateTime date;
    private double total;
    private StatutCommande statut;
    private List<Reservation> reservations;

    // Constructeur par défaut
    public Commande() {
        this.id = 0;
        this.utilisateur = "";
        this.date = LocalDateTime.now();
        this.total = 0.0;
        this.statut = StatutCommande.RESERVE;
        this.reservations = new ArrayList<>();
    }

    // Constructeur avec paramètres
    public Commande(int id, String utilisateur, LocalDateTime date, String statut, List<Reservation> reservations) {
        this.id = id;
        this.utilisateur = utilisateur;
        this.date = date;
        this.reservations = (reservations != null) ? new ArrayList<>(reservations) : new ArrayList<>();

        // Convertir la chaîne en Enum avec gestion des erreurs
        try {
            this.statut = StatutCommande.valueOf(statut.toUpperCase());
        } catch (IllegalArgumentException e) {
            this.statut = StatutCommande.RESERVE; // Valeur par défaut
        }

        this.total = calculerTotal(); // Calculer le total initial
    }

    // Méthode pour calculer le total
    public double calculerTotal() {
        double total = 0;
        for (Reservation reservation : reservations) {
            total += reservation.getProduit().getPrix() * reservation.getQuantite();
        }
        return total;
    }

    // Getters et Setters
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUtilisateur() {
        return this.utilisateur;
    }

    public void setUtilisateur(String utilisateur) {
        this.utilisateur = utilisateur;
    }

    public LocalDateTime getDate() {
        return this.date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public double getTotal() {
        return this.total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public StatutCommande getStatut() {
        return this.statut;
    }

    public void setStatut(StatutCommande statut) {
        this.statut = statut;
    }

    public List<Reservation> getReservations() {
        return this.reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = (reservations != null) ? new ArrayList<>(reservations) : new ArrayList<>();
        this.total = calculerTotal(); // Recalculer le total après mise à jour des réservations
    }

    // Ajouter une réservation
    public void ajouterReservation(Reservation reservation) {
        if (reservation != null) {
            this.reservations.add(reservation);
            this.total = calculerTotal(); // Recalculer le total après ajout
        }
    }

    @Override
    public String toString() {
        return "Commande{id=" + this.id + ", utilisateur='" + this.utilisateur + "', total=" + this.total + ", statut=" + this.statut + "}";
    }

    // Enum pour le statut de la commande
    public enum StatutCommande {
        CONFIRME,
        ANNULE,
        RESERVE;
    }
}