//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

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
    private List<Produit> produits = new ArrayList();

    public Commande(int id, String utilisateur, LocalDateTime date, String statut, List<Produit> produits) {
        this.id = id;
        this.utilisateur = utilisateur;
        this.date = date;
        this.total = calculerTotal(); // 🔥 Calcul automatique du total


        // Convertir la chaîne de statut en Enum
        try {
            this.statut = StatutCommande.valueOf(statut.toUpperCase());
        } catch (IllegalArgumentException e) {
            this.statut = StatutCommande.EN_ATTENTE; // Valeur par défaut si erreur
        }

        // Initialiser les listes pour éviter les erreurs NullPointerException
        this.reservations = new ArrayList<>();
        this.produits = new ArrayList<>(produits); // Copie pour éviter les modifications externes
    }

    private double calculerTotal() {
            double somme = 0;
            for (Produit p : produits) {
                somme += p.getPrix();  // Ajoute le prix de chaque produit
            }
            return somme;
        }


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
        this.reservations = reservations;
    }

    public void ajouterReservation(Reservation reservation) {
        this.reservations.add(reservation);
        this.total += reservation.getProduit().getPrix() * (double)reservation.getQuantite();
    }

    public List<Produit> getProduits() {
        return this.produits;
    }

    public String toString() {
        return "Commande{id=" + this.id + ", utilisateur='" + this.utilisateur + "', total=" + this.total + ", statut=" + this.statut + "}";
    }

    public static enum StatutCommande {
        EN_ATTENTE,
        CONFIRME,
        ANNULE;

        private StatutCommande() {
        }
    }
}
