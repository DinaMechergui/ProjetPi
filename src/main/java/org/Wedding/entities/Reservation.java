package org.Wedding.entities;

public class Reservation {
    private Long id;
    private Commande commande;
    private Produit produit;
    private int quantite;
    private StatutReservation statut;

    public enum StatutReservation {
        RESERVE, CONFIRME
    }

    // Constructeurs
    public Reservation() {
        this.statut = StatutReservation.RESERVE;
    }

    public Reservation(Long id, Commande commande, Produit produit, int quantite) {
        this();
        this.id = id;
        this.commande = commande;
        this.produit = produit;
        this.quantite = quantite;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Commande getCommande() { return commande; }
    public void setCommande(Commande commande) { this.commande = commande; }

    public Produit getProduit() { return produit; }
    public void setProduit(Produit produit) { this.produit = produit; }

    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }

    public StatutReservation getStatut() { return statut; }
    public void setStatut(StatutReservation statut) { this.statut = statut; }

    @Override
    public String toString() {
        return "Reservation{id=" + id + ", produit=" + produit.getNom() + ", quantite=" + quantite + ", statut=" + statut + "}";
    }
}
