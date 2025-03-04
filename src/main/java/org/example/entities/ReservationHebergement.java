package org.example.entities;

import java.util.Date;

public class ReservationHebergement {
    private int id;
    private int idheb;
    private String utilisateur; // Changement de "client" à "utilisateur"
    private Date dateDebut;
    private Date dateFin;
    private float prixTotal;

    public ReservationHebergement() {}

    // Constructeur mis à jour pour utiliser "utilisateur" au lieu de "client"
    public ReservationHebergement(int idheb, String utilisateur, Date dateDebut, Date dateFin, float prixTotal) {
        this.idheb = idheb;
        this.utilisateur = utilisateur;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.prixTotal = prixTotal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdheb() {
        return idheb;
    }

    public void setIdheb(int idheb) {
        this.idheb = idheb;
    }

    // Getter et Setter mis à jour pour "utilisateur"
    public String getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(String utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public float getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(float prixTotal) {
        this.prixTotal = prixTotal;
    }
}