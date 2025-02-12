package org.Wedding.entities;

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
    private List<Produit> produits = new ArrayList<>();



    public enum StatutCommande {
        RESERVE, CONFIRME, ANNULE
    }

    // Constructeurs
    public Commande(int id, String utilisateur, LocalDateTime date, double total, String statut, List<Produit> produits) {
        this.id = id;
        this.utilisateur = utilisateur;
        this.date = date;
        this.total = total;
        this.statut = StatutCommande.valueOf(statut.toUpperCase()); // ✅ Convertir en majuscule
        this.produits = produits;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId (int id) { this.id = id; }

    public String getUtilisateur() { return utilisateur; }
    public void setUtilisateur(String utilisateur) { this.utilisateur = utilisateur; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public StatutCommande getStatut() { return statut; }
    public void setStatut(StatutCommande statut) { this.statut = statut; }

    public List<Reservation> getReservations() { return reservations; }
    public void setReservations(List<Reservation> reservations) { this.reservations = reservations; }

    public void ajouterReservation(Reservation reservation) {
        this.reservations.add(reservation);
        this.total += reservation.getProduit().getPrix() * reservation.getQuantite();
    }
    public List<Produit> getProduits() {
        return produits;
    }
    @Override
    public String toString() {
        return "Commande{id=" + id + ", utilisateur='" + utilisateur + "', total=" + total + ", statut=" + statut + "}";
    }
}
