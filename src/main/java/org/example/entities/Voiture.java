package org.example.entities;

public class Voiture {
    private int idvoiture ;
    private float prix;
    private String marque ;

    private boolean  disponible ;

    public Voiture(int idvoiture, float prix, String marque, boolean disponible) {
        this.idvoiture = idvoiture;
        this.prix = prix;
        this.marque = marque;
        this.disponible = disponible;

    }

    public Voiture(String text, String text1, String text2) {
    }


    public int getIdvoiture() {
        return idvoiture;
    }

    public void setIdvoiture(int idvoiture) {
        this.idvoiture = idvoiture;
    }

    public float getPrix() {
        return prix;
    }

    public void setPrix(float prix) {
        this.prix = prix;
    }

    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
}
