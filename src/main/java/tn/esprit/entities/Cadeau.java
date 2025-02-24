package tn.esprit.entities;

public class Cadeau {
    private int id;
    private String nom;
    private String description;
    private boolean disponibilite ;
    private int inviteId;

    public Cadeau(int id, String nom, String description, boolean disponibilite, int inviteId) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.disponibilite = disponibilite;
        this.inviteId = inviteId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDisponibilite() {
        return disponibilite;
    }

    public void setDisponibilite(boolean disponibilite) {
        this.disponibilite = disponibilite;
    }

    public int getInviteId() {
        return inviteId;
    }

    public void setInviteId(int inviteId) {
        this.inviteId = inviteId;
    }
}
