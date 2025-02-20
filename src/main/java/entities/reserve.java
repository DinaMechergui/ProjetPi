package entities;

import java.util.Date;

public class reserve {
    private int id;
    private Event event;
    private ServiceItem ServiceItem;
    private Date dateReservation;
    private StatutReservation statut;
    private double prixTotal;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(double prixTotal) {
        this.prixTotal = prixTotal;
    }

    public StatutReservation getStatut() {
        return statut;
    }

    public void setStatut(StatutReservation statut) {
        this.statut = statut;
    }

    public Date getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(Date dateReservation) {
        this.dateReservation = dateReservation;
    }

    public ServiceItem getService() {
        return ServiceItem;
    }

    public void setService(ServiceItem ServiceItem) {
        this.ServiceItem = ServiceItem;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}