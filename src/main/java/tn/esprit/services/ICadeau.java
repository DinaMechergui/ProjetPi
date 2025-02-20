package tn.esprit.services;

import tn.esprit.entities.Cadeau;

import java.sql.SQLException;

public interface ICadeau {
    // Ajouter un cadeau
    void ajouter(Cadeau cadeau) throws SQLException;

    // Modifier un cadeau
    void modifier(Cadeau cadeau) throws SQLException;

    // Supprimer un cadeau par ID
    void supprimer(int id) throws SQLException;

    // Afficher tous les cadeaux
   // List<Cadeau> afficher() throws SQLException;

    // Chercher un cadeau par ID
  //  Cadeau trouverParId(int id) throws SQLException;

    // Chercher des cadeaux par nom
 //   List<Cadeau> chercherParNom(String nom) throws SQLException;
}
