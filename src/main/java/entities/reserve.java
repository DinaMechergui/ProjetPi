package entities;

import tn.esprit.tacheuser.models.User;
import java.util.Date;

public class reserve {
    private int id;
    private Event event;
    private ServiceItem service;
    private Date dateReservation;
    private double prixTotal;
    private String utilisateur; // Track which user made the reservation

    public enum StatutReservation {
        CONFIRMEE, EN_ATTENTE, ANNULEE;

        public static StatutReservation fromString(String statut) {
            try {
                return valueOf(statut.toUpperCase());
            } catch (IllegalArgumentException e) {
                return EN_ATTENTE;
            }
        }
    }

    public reserve() {}

    public reserve(int id, Event event, ServiceItem service, Date dateReservation,  double prixTotal, String utilisateur) {
        this.id = id;
        this.event = event;
        this.service = service;
        this.dateReservation = dateReservation;
        this.prixTotal = prixTotal;
        this.utilisateur = utilisateur;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }

    public ServiceItem getService() { return service; }
    public void setService(ServiceItem service) { this.service = service; }

    public Date getDateReservation() { return dateReservation; }
    public void setDateReservation(Date dateReservation) { this.dateReservation = dateReservation; }



    public double getPrixTotal() { return prixTotal; }
    public void setPrixTotal(double prixTotal) { this.prixTotal = prixTotal; }

    public String getUtilisateur() { return utilisateur; }
    public void setUtilisateur(String utilisateur) { this.utilisateur = utilisateur; }
}
