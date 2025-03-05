package Wedding.entities;

import entities.ServiceItem;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Commande {
    private int id;
    private String utilisateur;
    private LocalDateTime dateCommande;
    private String statut;
    private List<Reservation> reservations;
    private List<ServiceItem> servicesReserves;

    public Commande(int id, String utilisateur, LocalDateTime dateCommande, String statut,
                    List<Reservation> reservations, List<ServiceItem> servicesReserves) {
        this.id = id;
        this.utilisateur = utilisateur;
        this.dateCommande = dateCommande;
        this.statut = statut;
        this.reservations = reservations != null ? reservations : new ArrayList<>();
        this.servicesReserves = servicesReserves != null ? servicesReserves : new ArrayList<>();
    }

    public Commande() {
        this.reservations = new ArrayList<>();
        this.servicesReserves = new ArrayList<>();
    }
    public void ajouterReservation(Reservation reservation) {
        this.reservations.add(reservation);
    }

    public void ajouterService(ServiceItem service) {
        this.servicesReserves.add(service);
    }

    public double calculerTotal() {
        double totalProduits = reservations.stream().mapToDouble(r -> r.getProduit().getPrix() * r.getQuantite()).sum();
        double totalServices = servicesReserves.stream().mapToDouble(ServiceItem::getPrix).sum();
        return totalProduits + totalServices;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUtilisateur() { return utilisateur; }
    public void setUtilisateur(String utilisateur) { this.utilisateur = utilisateur; }
    public LocalDateTime getDateCommande() { return dateCommande; }
    public void setDateCommande(LocalDateTime dateCommande) { this.dateCommande = dateCommande; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public List<Reservation> getReservations() { return reservations; }
    public void setReservations(List<Reservation> reservations) { this.reservations = reservations; }
    public List<ServiceItem> getServicesReserves() { return servicesReserves; }
    public void setServicesReserves(List<ServiceItem> servicesReserves) { this.servicesReserves = servicesReserves; }



}
