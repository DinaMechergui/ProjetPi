package tn.esprit.tacheuser.models;

public class Reclamation {
    private int id;
    private int userId;
    private String sujet;
    private String description;
    private String statut;

    // Constructeur sans id, utilisé pour les nouvelles réclamations
    public Reclamation(String sujet, String description, String statut, int userId) {
        this.sujet = sujet;
        this.description = description;
        this.statut = statut;
        this.userId = userId;  // Lier la réclamation à l'utilisateur connecté
    }

    // Constructeur avec id, utilisé pour les réclamations existantes (pour récupération depuis la base de données)
    public Reclamation(int id, int userId, String sujet, String description, String statut) {
        this.id = id;
        this.userId = userId;
        this.sujet = sujet;
        this.description = description;
        this.statut = statut;
    }

    // Constructeur par défaut
    public Reclamation() {}

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSujet() {
        return sujet;
    }

    public void setSujet(String sujet) {
        this.sujet = sujet;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return "Reclamation{id=" + id + ", userId=" + userId + ", sujet='" + sujet + "', description='" + description + "', statut='" + statut + "'}";
    }
}
