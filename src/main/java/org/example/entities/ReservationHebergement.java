package org.example.entities;


import java.util.Date;

public class ReservationHebergement {
    private int id;
    private int idheb;
    private String client;
    private Date dateDebut;
    private Date dateFin;
    private float prixTotal;

    public ReservationHebergement() {}

    public ReservationHebergement(int idheb, String client, Date dateDebut, Date dateFin, float prixTotal) {
        this.idheb = idheb;
        this.client = client;
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

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
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