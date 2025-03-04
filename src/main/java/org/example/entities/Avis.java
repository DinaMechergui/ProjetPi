package org.example.entities;

import java.util.Date;

public class Avis {
    private int id;
    private int idheb;
    private int note;
    private String commentaire;
    private Date dateCreation;
    private int iduser; // ID de l'utilisateur
    private String nomUtilisateur; // Nom de l'utilisateur
    private String prenomUtilisateur; // Prénom de l'utilisateur

    // Constructeur par défaut
    public Avis() {}

    // Constructeur avec tous les attributs
    public Avis(int idheb, int note, String commentaire, Date dateCreation, int iduser) {
        this.idheb = idheb;
        this.note = note;
        this.commentaire = commentaire;
        this.dateCreation = dateCreation;
        this.iduser = iduser;
    }

    // Getters et setters
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

    public int getIduser() { return iduser; }
    public void setIduser(int iduser) { this.iduser = iduser; }

    // Ajoutez les getters et setters pour nomUtilisateur et prenomUtilisateur
    public String getNomUtilisateur() { return nomUtilisateur; }
    public void setNomUtilisateur(String nomUtilisateur) { this.nomUtilisateur = nomUtilisateur; }

    public String getPrenomUtilisateur() { return prenomUtilisateur; }
    public void setPrenomUtilisateur(String prenomUtilisateur) { this.prenomUtilisateur = prenomUtilisateur; }
}