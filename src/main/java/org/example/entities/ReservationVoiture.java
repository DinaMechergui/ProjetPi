package org.example.entities;

import java.util.Date;

public class ReservationVoiture {
    private int id;
    private int idvoiture;
    private String voiture;
    private String client;
    private Date datedebut;
    private Date datefin;
    private float prixtotal;

    public ReservationVoiture(String voiture, String client, Date datedebut, Date datefin, float prixtotal) {
        this.voiture = voiture;
        this.client = client;
        this.datedebut = datedebut;
        this.datefin = datefin;
        this.prixtotal = prixtotal;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdvoiture() { return idvoiture; }
    public void setIdvoiture(int idvoiture) { this.idvoiture = idvoiture; }

    public String getVoiture() { return voiture; }
    public void setVoiture(String voiture) { this.voiture = voiture; }

    public String getClient() { return client; }
    public void setClient(String client) { this.client = client; }

    public Date getDatedebut() { return datedebut; }
    public void setDatedebut(Date datedebut) { this.datedebut = datedebut; }

    public Date getDatefin() { return datefin; }
    public void setDatefin(Date datefin) { this.datefin = datefin; }

    public float getPrixtotal() { return prixtotal; }
    public void setPrixtotal(float prixtotal) { this.prixtotal = prixtotal; }

    @Override
    public String toString() {
        return "ReservationVoiture{" +
                "id=" + id +
                ", voiture='" + voiture + '\'' +
                ", client='" + client + '\'' +
                ", datedebut=" + datedebut +
                ", datefin=" + datefin +
                ", prixtotal=" + prixtotal +
                '}';
    }
}