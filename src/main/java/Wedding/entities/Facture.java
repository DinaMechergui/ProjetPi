package Wedding.entities;

import java.time.LocalDateTime;

public class Facture {
    private int id;
    private Commande commande;
    private LocalDateTime dateFacture;
    private String utilisateur; // ou un objet User
    private double total;

    public Facture(int id, Commande commande, LocalDateTime dateFacture, String utilisateur, double total) {
        this.id = id;
        this.commande = commande;
        this.dateFacture = dateFacture;
        this.utilisateur = utilisateur;
        this.total = total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Commande getCommande() {
        return commande;
    }

    public String getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(String utilisateur) {
        this.utilisateur = utilisateur;
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
    }

    public LocalDateTime getDateFacture() {
        return dateFacture;
    }

    public void setDateFacture(LocalDateTime dateFacture) {
        this.dateFacture = dateFacture;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}