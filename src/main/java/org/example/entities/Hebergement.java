package org.example.entities;

public class Hebergement {
    private int idheb;
    private String nom;
    private String adresse;
    private double prixParNuit;
    private boolean disponible;
    private String imageUrl;
    private double latitude; // Ajout de l'attribut latitude
    private double longitude; // Ajout de l'attribut longitude

    // Constructeur
    public Hebergement(int idheb, String nom, String adresse, double prixParNuit, boolean disponible, String imageUrl, double latitude, double longitude) {
        this.idheb = idheb;
        this.nom = nom;
        this.adresse = adresse;
        this.prixParNuit = prixParNuit;
        this.disponible = disponible;
        this.imageUrl = imageUrl;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Hebergement() {

    }

    // Getters et Setters
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Ajout des getters pour latitude et longitude
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}