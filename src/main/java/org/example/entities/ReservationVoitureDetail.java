package org.example.entities;


import org.example.utils.MyDatabase;

import java.sql.Connection;

public class ReservationVoitureDetail {
    private int id;
    private String client;
    private String dateDebut;
    private String dateFin;
    private double prixTotal;
    private int idvoiture;
    private String marque;
    private double prixVoiture;
    private boolean disponible;
    private Connection connection;

    public ReservationVoitureDetail() {
        this.connection = MyDatabase.getInstance().getConnection();

    }

    public ReservationVoitureDetail(int id, String client, String dateDebut, String dateFin, double prixTotal,
                                    int voitureId, String marque, double prixVoiture, boolean disponible) {
        this.id = id;
        this.client = client;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.prixTotal = prixTotal;
        this.idvoiture = voitureId;
        this.marque = marque;
        this.prixVoiture = prixVoiture;
        this.disponible = disponible;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(String dateDebut) {
        this.dateDebut = dateDebut;
    }

    public String getDateFin() {
        return dateFin;
    }

    public void setDateFin(String dateFin) {
        this.dateFin = dateFin;
    }

    public double getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(double prixTotal) {
        this.prixTotal = prixTotal;
    }

    public int getIdvoiture() {
        return idvoiture;
    }

    public void setIdvoiture(int idvoiture) {
        this.idvoiture = idvoiture;
    }

    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public double getPrixVoiture() {
        return prixVoiture;
    }

    public void setPrixVoiture(double prixVoiture) {
        this.prixVoiture = prixVoiture;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    @Override
    public String toString() {
        return "📌 Réservation ID: " + id +
                "\n👤 Client: " + client +
                "\n📅 Date Début: " + dateDebut +
                "\n📅 Date Fin: " + dateFin +
                "\n💰 Prix Total: " + prixTotal + "€" +
                "\n🚗 Voiture: " + marque + " | 💵 Prix: " + prixVoiture + "€/jour | " +
                (disponible ? "✅ Disponible" : "❌ Indisponible");
    }



}


