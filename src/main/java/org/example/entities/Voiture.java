package org.example.entities;

public class Voiture {
    private int idvoiture ;
    private float prix;
    private String marque ;

    private boolean  disponible ;
    private static String imageUrl;

    public Voiture(int idvoiture, float prix, String marque, boolean disponible, String imageUrl) {
        this.idvoiture = idvoiture;
        this.prix = prix;
        this.marque = marque;
        this.disponible = disponible;
        this.imageUrl = imageUrl;

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

    public static String getImageUrl() {
        return imageUrl;
    }

    public static void setImageUrl(String imageUrl) {
        Voiture.imageUrl = imageUrl;
    }
}
