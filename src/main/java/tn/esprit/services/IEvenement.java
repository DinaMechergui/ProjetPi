package tn.esprit.services;

import tn.esprit.entities.Evenement;

import java.sql.SQLException;
import java.util.List;

public interface IEvenement {
    // Ajouter un événement
    void ajouter(Evenement evenement) throws SQLException;

    // Modifier un événement
    void modifier(Evenement evenement) throws SQLException;

    // Supprimer un événement par ID
    void supprimer(int id) throws SQLException;

    // Afficher tous les événements
    List<Evenement> afficher() throws SQLException;

    // Autres méthodes spécifiques aux événements (exemples)
    Evenement trouverParId(int id) throws SQLException;
    List<Evenement> chercherParNom(String nom) throws SQLException;
}
