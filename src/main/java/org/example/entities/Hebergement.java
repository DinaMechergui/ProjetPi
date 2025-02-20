package org.example.entities;

public class Hebergement {
    private int idheb;
    private String nom;
    private String adresse;
    private double prixParNuit;
    private boolean disponible;

    public Hebergement() {}

    public Hebergement(int idheb,String nom, String adresse,  double prixParNuit, boolean disponible) {
        this.idheb = idheb;
        this.nom = nom;
        this.adresse = adresse;
        this.prixParNuit = prixParNuit;
        this.disponible = disponible;
    }

    public int getIdheb() {
        return idheb;
    }

    public void setIdheb(int idheb) {
        this.idheb = idheb;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
    public double getPrixParNuit() {
        return prixParNuit;
    }
    public void setPrixParNuit(double prixParNuit) {
        this.prixParNuit = prixParNuit;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
}
