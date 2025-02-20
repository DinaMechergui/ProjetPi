package tn.esprit.tacheuser.models;

import java.sql.Timestamp;

public class Reponse {
    private int id;
    private int reclamationId;
    private String message;
    private Timestamp dateReponse;

    // ✅ Constructeur sans ID (pour l'ajout)
    public Reponse(int reclamationId, String message) {
        this.reclamationId = reclamationId;
        this.message = message;
    }

    // ✅ Constructeur avec ID (pour la récupération depuis la base)
    public Reponse(int id, int reclamationId, String message, Timestamp dateReponse) {
        this.id = id;
        this.reclamationId = reclamationId;
        this.message = message;
        this.dateReponse = dateReponse;
    }

    // ✅ Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getReclamationId() { return reclamationId; }
    public void setReclamationId(int reclamationId) { this.reclamationId = reclamationId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Timestamp getDateReponse() { return dateReponse; }
    public void setDateReponse(Timestamp dateReponse) { this.dateReponse = dateReponse; }

    @Override
    public String toString() {
        return "Reponse{" +
                "id=" + id +
                ", reclamationId=" + reclamationId +
                ", message='" + message + '\'' +
                ", dateReponse=" + dateReponse +
                '}';
    }
}
