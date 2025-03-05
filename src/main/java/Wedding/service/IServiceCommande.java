//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package Wedding.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import Wedding.entities.Commande;
import Wedding.entities.Produit;
import Wedding.entities.Reservation;
import entities.ServiceItem;
import javafx.util.Pair;
import tn.esprit.tacheuser.models.User;

public interface IServiceCommande {
    void removeProductFromCart(int commandeId, int produitId) throws SQLException;

    int ajouterOuMettreAJourReservation(String utilisateur, Produit produit, int quantite) throws SQLException;

    int ajouterReservation(Commande var1) throws SQLException;


    List<Pair<ServiceItem, LocalDate>> getServicesReserves(int commandeId) throws SQLException;

    void confirmerCommande(int idCommande, String utilisateur) throws SQLException;

    void annulerReservation(int var1) throws SQLException;

    List<Reservation> getAllReservations();

    void deleteReservation(int idReservation);

    boolean dateDejaReservee(int produitId, LocalDate date) throws SQLException;

    void ajouterServiceReserve(int commandeId, ServiceItem service, LocalDate dateReservation) throws SQLException;

    Commande getCommandeByUser(User user) throws SQLException;

    void ajouterCommande(Commande commande) throws SQLException;
}

