//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.Wedding.entities;

public class Reservation {
    private Long id;
    private Commande commande;
    private Produit produit;
    private int quantite;
    private StatutReservation statut;

    public Reservation() {
        this.statut = Reservation.StatutReservation.RESERVE;
    }

    public Reservation(Long id, Commande commande, Produit produit, int quantite) {
        this();
        this.id = id;
        this.commande = commande;
        this.produit = produit;
        this.quantite = quantite;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Commande getCommande() {
        return this.commande;
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
    }

    public Produit getProduit() {
        return this.produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public int getQuantite() {
        return this.quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public StatutReservation getStatut() {
        return this.statut;
    }

    public void setStatut(StatutReservation statut) {
        this.statut = statut;
    }

    public String toString() {
        Long var10000 = this.id;
        return "Reservation{id=" + var10000 + ", produit=" + this.produit.getNom() + ", quantite=" + this.quantite + ", statut=" + this.statut + "}";
    }

    public static enum StatutReservation {
        RESERVE,
        CONFIRME;

        private StatutReservation() {
        }
    }
}
