package org.example.entities;


import java.util.Date;

public class ReservationHebergementDetail {
    private int id;
    private String client;
    private Date dateDebut;
    private Date dateFin;
    private double prixTotal;

    private int idheb;
    private String nomHebergement;
    private String adresse;
    private double prixParNuit;
    private boolean disponible;

    // Constructeur
    public ReservationHebergementDetail(int id, String client, Date dateDebut, Date dateFin, double prixTotal,
                                        int idheb, String nomHebergement, String adresse, double prixParNuit, boolean disponible) {
        this.id = id;
        this.client = client;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.prixTotal = prixTotal;
        this.idheb = idheb;
        this.nomHebergement = nomHebergement;
        this.adresse = adresse;
        this.prixParNuit = prixParNuit;
        this.disponible = disponible;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getClient() { return client; }
    public void setClient(String client) { this.client = client; }

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

    public double getPrixTotal() { return prixTotal; }
    public void setPrixTotal(double prixTotal) { this.prixTotal = prixTotal; }

    public int getIdheb() { return idheb; }
    public void setIdheb(int idheb) { this.idheb = idheb; }

    public String getNomHebergement() { return nomHebergement; }
    public void setNomHebergement(String nomHebergement) { this.nomHebergement = nomHebergement; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public double getPrixParNuit() { return prixParNuit; }
    public void setPrixParNuit(double prixParNuit) { this.prixParNuit = prixParNuit; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
}

