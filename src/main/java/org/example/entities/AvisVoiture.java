package org.example.entities;

import java.util.Date;

public class AvisVoiture {
    private int id;
    private int idvoiture; // ID de la voiture
    private int note;
    private String commentaire;
    private Date dateCreation;

    // Constructeur par défaut
    public AvisVoiture() {}

    // Constructeur pour les avis sur les voitures
    public AvisVoiture(int idvoiture, int note, String commentaire, Date dateCreation) {
        this.idvoiture = idvoiture;
        this.note = note;
        this.commentaire = commentaire;
        this.dateCreation = dateCreation;
    }

    // Getters et setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdvoiture() {
        return idvoiture;
    }

    public void setIdvoiture(int idvoiture) {
        this.idvoiture = idvoiture;
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = note;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    @Override
    public String toString() {
        return "Avis{" +
                "id=" + id +
                ", idvoiture=" + idvoiture +
                ", note=" + note +
                ", commentaire='" + commentaire + '\'' +
                ", dateCreation=" + dateCreation +
                '}';
    }
}