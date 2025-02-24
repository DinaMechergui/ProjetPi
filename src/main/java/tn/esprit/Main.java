package tn.esprit;

import tn.esprit.entities.Cadeau;
import tn.esprit.entities.Evenement;
import tn.esprit.entities.Invite;
import tn.esprit.services.ServiceCadeau;
import tn.esprit.services.ServiceEvenement;
import tn.esprit.services.ServiceInvite;
import tn.esprit.utils.MyDatabase;

import java.sql.SQLException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
//click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws SQLException {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.

        MyDatabase db = new MyDatabase();
        ServiceInvite serviceInvite = new ServiceInvite();

        try {
           Invite invite = new Invite(1,"skander", "Jean", "jean.dupont@example.com","26731892",true,2);
           serviceInvite.ajouter(invite);
        } catch (SQLException e) {
           System.err.println("❌ Erreur lors de l'ajout de l'invité : " + e.getMessage());
       }
       // ServiceInvite serviceInvite = new ServiceInvite();

       // Création d'un objet Invite avec un ID existant
    /*    Invite invite = new Invite(1, "Sami", "BenFoulen", "sami@example.com", "987654321", false);
        invite.setId(1); // Assumer que l'ID de l'invité à modifier est 1

        try {
            // Modifier l'invité dans la base de données
            serviceInvite.modifier(invite);
            System.out.println("✅ Invité après modification !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification : " + e.getMessage());
            e.printStackTrace();
        }
        int idASupprimer = 1; // Remplace par l'ID de l'invité que tu veux supprimer

        try {
            // Supprimer l'invité dans la base de données
            serviceInvite.supprimer(idASupprimer);
            System.out.println("✅ Suppression terminée !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression : " + e.getMessage());
        }*/

      //  serviceInvite.afficherInvites();

     //   ServiceEvenement serviceEvenement = new ServiceEvenement();

   /*  try  {
            // Ajouter un événement
           Evenement evenement = new Evenement(1, "Mariage royal", "Paris", "2025-06-15");
           serviceEvenement.ajouter(evenement);
            System.out.println("✅ Événement ajouté !");
        } catch (SQLException e) {
           System.err.println("❌ Erreur lors de l'ajout de l'événement : " + e.getMessage());
        }*/

     /*   // Modification de l'événement
       Evenement evenementModifie = new Evenement(1, "Anniversaire", "Lyon", "2025-07-20");
       evenementModifie.setId(1); // Assumer que l'ID de l'événement à modifier est 1

       try {
          serviceEvenement.modifier(evenementModifie);
            System.out.println("✅ Événement après modification !");
       } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification : " + e.getMessage());
       }

        // Suppression de l'événement
       int idASupprimer = 1; // Remplace par l'ID de l'événement que tu veux supprimer

       try {
            serviceEvenement.supprimer(idASupprimer);
           System.out.println("✅ Suppression terminée !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression : " + e.getMessage());
        }*/
        // serviceEvenement.afficherEvenements();
      //  serviceEvenement.afficherEvenementsAvecInvitesEtCadeaux();

        ServiceCadeau serviceCadeau = new ServiceCadeau();

  /*   try {
            // Ajouter un cadeau
            Cadeau cadeau = new Cadeau(1, "golf", "syncro", true,2);
            serviceCadeau.ajouter(cadeau);
            System.out.println("✅ Cadeau ajouté !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout du cadeau : " + e.getMessage());
        }*/

    /*  // Modification du cadeau
        Cadeau cadeauModifie = new Cadeau(1, "Montre en argent", "Une montre élégante en argent", false);
        cadeauModifie.setId(1); // Assumer que l'ID du cadeau à modifier est 1

        try {
            serviceCadeau.modifier(cadeauModifie);
            System.out.println("✅ Cadeau après modification !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification : " + e.getMessage());
        }

        // Suppression du cadeau
       int idASupprimer = 1; // Remplace par l'ID du cadeau que tu veux supprimer

        try {
            serviceCadeau.supprimer(idASupprimer);
            System.out.println("✅ Suppression terminée !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression : " + e.getMessage());
        }
       serviceCadeau.afficherCadeaux();*/
    }
}
