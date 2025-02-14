package entities;
import java.util.Date;

public class reserve {
        private int id;
        private event event;
        private service service;
        private Date dateReservation;
        private StatutReservation statut;
        private double prixTotal;

        public enum StatutReservation {
            CONFIRMEE, EN_ATTENTE, ANNULEE
        }

        // Constructeurs
        public reserve() {}

        public reserve(int id, event event, service service, Date dateReservation, String statut, double prixTotal) {
            this.id = id;
            this.event = event;
            this.service = service;
            this.dateReservation = dateReservation;
            this.statut = StatutReservation.valueOf(statut.toUpperCase());
            this.prixTotal = prixTotal;
        }

        // Getters et Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public event getEvent() { return event; }
        public void setEvent(event event) { this.event = event; }

        public service getService() { return service; }
        public void setService(service service) { this.service = service; }

        public Date getDateReservation() { return dateReservation; }
        public void setDateReservation(Date dateReservation) { this.dateReservation = dateReservation; }

        public StatutReservation getStatut() { return statut; }
        public void setStatut(StatutReservation statut) { this.statut = statut; }

        public double getPrixTotal() { return prixTotal; }
        public void setPrixTotal(double prixTotal) { this.prixTotal = prixTotal; }

        @Override
        public String toString() {
            return "Reservation{id=" + id + ", event=" + event.getNom() + ", service=" + service.getNom() + ", statut=" + statut + "}";
        }
    }


