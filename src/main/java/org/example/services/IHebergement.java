package org.example.services;
import org.example.entities.Hebergement;

import java.sql.SQLException;
import java.util.List;

public interface IHebergement {
    void ajouter(Hebergement hebergement) throws SQLException;
    void modifier(Hebergement hebergement) throws SQLException;
    void supprimer(int id) throws SQLException;
    List<Hebergement> afficher() throws SQLException;
}
