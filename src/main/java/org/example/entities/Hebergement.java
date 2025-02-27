package org.example.entities;

public class Hebergement {
    private int idheb;
    private String nom;
    private String adresse;
    private double prixParNuit;
    private boolean disponible;
    private static String imageUrl; // URL de l'image

    public Hebergement() {}

    public Hebergement(int idheb, String nom, String adresse, double prixParNuit, boolean disponible, String imageUrl) {
        this.idheb = idheb;
        this.nom = nom;
        this.adresse = adresse;
        this.prixParNuit = prixParNuit;
        this.disponible = disponible;
        this.imageUrl = imageUrl; // Initialisation de l'URL de l'image
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

    public static String getImageUrl() {
        return imageUrl; // Retourner l'URL de l'image
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl; // Définir l'URL de l'image
    }
}
