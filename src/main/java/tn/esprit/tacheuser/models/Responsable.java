package tn.esprit.tacheuser.models;

public class Responsable {

    private int id;
    private String nom;
    private String prenom;
    private String description;
    private String identifiant;
    private String tel;

    // Constructor
    public Responsable(int id, String nom, String prenom, String description, String identifiant, String tel) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.description = description;
        this.identifiant = identifiant;
        this.tel = tel;
    }
    public Responsable(int id, String nom, String prenom, String description, String tel) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.description = description;
        this.tel = tel;
        this.identifiant = ""; // Laisser vide par défaut ou générer un identifiant
    }

    // Constructor without the identifiant (for registration before assignment by admin)
    public Responsable(String nom, String prenom, String description, String tel) {
        this.nom = nom;
        this.prenom = prenom;
        this.description = description;
        this.tel = tel;
    }

    // Getters and Setters
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

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(String identifiant) {
        this.identifiant = identifiant;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
}
