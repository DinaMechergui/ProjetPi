package org.example;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.Wedding.entities.Commande;
import org.Wedding.entities.Produit;
import org.Wedding.service.ServiceCommande;
import org.Wedding.service.ServiceProduit;

public class Main {
    public Main() {
    }

    public static void main(String[] args) {
        try {
            ServiceProduit serviceProduit = new ServiceProduit();
            ServiceCommande serviceCommande = new ServiceCommande();

            // === Test Ajout Produit ===
            System.out.println("\n=== Test Ajout Produit ===");
            Produit produit1 = new Produit(1, "Robe", "Robe blanche avec dentelle", 1200.0, "Vêtements", 10);
            Produit produit2 = new Produit(2, "Costume Homme", "Costume bleu classique", 850.0, "Vêtements", 5);

            int idProduit1 = serviceProduit.ajouter(produit1);
            int idProduit2 = serviceProduit.ajouter(produit2);
            System.out.println("Produit ajouté avec ID : " + idProduit1);
            System.out.println("Produit ajouté avec ID : " + idProduit2);

            // === Test Affichage Produits ===
            System.out.println("\n=== Test Affichage Produits ===");
            for (Produit p : serviceProduit.afficher()) {
                System.out.println(p);
            }

            // === Test Modification Produit ===
            System.out.println("\n=== Test Modification Produit ===");

            Produit produitModifie = new Produit(1, "Robe Princesse", "Robe noire", 1300.0, "Vêtements", 8);
            serviceProduit.modifier(produitModifie);
            System.out.println("Produit modifié avec succès");

            // === Test Suppression Produit ===
            System.out.println("\n=== Test Suppression Produit ===");
            serviceProduit.supprimer(31);
            System.out.println("Produit supprimé avec succès");

            // === Produits après suppression ===
            System.out.println("\n=== Produits après suppression ===");
            for (Produit p : serviceProduit.afficher()) {
                System.out.println(p);
            }

           // === Test Ajout Produit dans la commande ===
           System.out.println("\n=== Test Ajout Produit dans la commande ===");
            serviceCommande.ajouterOuMettreAJourReservation("client2", produit1);
           System.out.println("Produit ajouté ou mis à jour dans la commande !");

            // === Test Ajout de la Commande Réservée ===
            System.out.println("\n=== Test Ajout Réservation Commande ===");
            List<Produit> produitsCommande = new ArrayList<>();
            produitsCommande.add(produit1);

            Commande commande = new Commande(1, "client2", LocalDateTime.now(), "reservé", produitsCommande);
           int idCommande = serviceCommande.ajouterReservation(commande);
            System.out.println("Commande réservée avec ID : " + idCommande);

            // === Test Confirmation Commande ===
            System.out.println("\n=== Test Confirmation Commande ===");
            serviceCommande.confirmerCommande(idCommande);
            System.out.println("Commande confirmée et stock mis à jour");

/*
         // === Test Annulation Commande ===
            System.out.println("\n=== Test Annulation Commande ===");
            int idCommandeAnnulee = serviceCommande.ajouterReservation(commande);
            serviceCommande.annulerReservation(idCommandeAnnulee);
            System.out.println("Commande annulée avec succès");
*/
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
