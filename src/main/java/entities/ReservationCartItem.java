package entities;

import java.time.LocalDate;
import java.util.Date;

public class ReservationCartItem {
    private int serviceId;
    private String serviceName;
    private double price;
    private Date date;
    private Event.Statut eventStatut; // Ajout du statut de l'événement

    // Constructeur
    public ReservationCartItem(int serviceId, String serviceName, double price, Date date, Event.Statut eventStatut) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.price = price;
        this.date = date;
        this.eventStatut = eventStatut;
    }

    // Getters et setters
    public int getServiceId() {
        return serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getPrice() {
        return price;
    }

    public Date getDate() {
        return date;
    }

    public Event.Statut getEventStatut() {
        return eventStatut;
    }

    public void setEventStatut(Event.Statut eventStatut) {
        this.eventStatut = eventStatut;
    }
}
