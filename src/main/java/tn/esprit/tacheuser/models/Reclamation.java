package tn.esprit.tacheuser.models;

public class Reclamation {
    private int id;
    private int userId;
    private String sujet;
    private String description;
    private String statut;

    public Reclamation(String description, String statut) {
        this.description = description;
        this.statut = statut;
    }

    public Reclamation() {}

    public Reclamation(int id, int userId, String sujet, String description, String statut) {
        this.id = id;
        this.userId = userId;
        this.sujet = sujet;
        this.description = description;
        this.statut = statut;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getSujet() { return sujet; }
    public void setSujet(String sujet) { this.sujet = sujet; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    @Override
    public String toString() {
        return "Reclamation{id=" + id + ", userId=" + userId + ", sujet='" + sujet + "', description='" + description + "', statut='" + statut + "'}";
    }
}
