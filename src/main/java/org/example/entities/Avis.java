package org.example.entities;

import java.util.Date;

public class Avis {
    private int id;
    private int idheb;
    private int note;
    private String commentaire;
    private Date dateCreation;

    public Avis() {}

    public Avis(int idheb, int note, String commentaire, Date dateCreation ) {
        this.idheb = idheb;
        this.note = note;
        this.commentaire = commentaire;
        this.dateCreation = new Date();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdheb() { return idheb; }
    public void setIdheb(int idheb) { this.idheb = idheb; }

    public int getNote() { return note; }
    public void setNote(int note) { this.note = note; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    public Date getDateCreation() { return dateCreation; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }
}
