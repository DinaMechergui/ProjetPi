package tn.esprit.entities;

public class Invite {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone ;
    private boolean confirmation ;
    private int evenementId;



    public Invite(int id, String nom, String prenom, String email, String telephone , boolean confirmation , int evenementId) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.confirmation = false;
        this.evenementId = evenementId;



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
        public String getPrenom() {
        return prenom;
        }
        public void setPrenom(String prenom) {
        this.prenom = prenom;

        }
        public String getEmail() {
        return email;
        }
        public void setEmail(String email) {
        this.email = email;
        }
        public String getTelephone() {
        return telephone;
        }
        public void setTelephone(String telephone) {
        this.telephone = telephone;
        }
        public boolean isConfirmation() {
        return confirmation;
        }
        public void setConfirmation(boolean confirmation) {
        this.confirmation = confirmation;
        }

    public int getEvenementId() {
        return evenementId;
    }

    public void setEvenementId(int evenementId) {
        this.evenementId = evenementId;
    }
}
