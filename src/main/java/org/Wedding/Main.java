package org.Wedding;

import org.Wedding.entities.Commande;
import org.Wedding.entities.Produit;
import org.Wedding.service.ServiceCommande;
import org.Wedding.service.ServiceProduit;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            // Initialiser les services
            ServiceProduit serviceProduit = new ServiceProduit();
            ServiceCommande serviceCommande = new ServiceCommande();

            // Ajouter un produit
            Produit produit = new Produit(0, "Robe de Mariée", "Robe blanche élégante", 1200.00, "Vêtements", 10);
            int produitId = serviceProduit.ajouter(produit);
            produit.setId(produitId);

            // Créer une commande (réservation)
            List<Produit> produits = new ArrayList<>();
            produits.add(produit);
            Commande commande = new Commande(0, "client123", LocalDateTime.now(), 1200.00, "reserve", produits);
            int commandeId = serviceCommande.ajouterReservation(commande);
            commande.setId(commandeId);

            System.out.println("✅ Réservation ajoutée avec succès ! ID de commande : " + commandeId);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
