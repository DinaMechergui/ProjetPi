package org.example.services;
import org.example.entities.ReservationVoiture;
import java.sql.SQLException;
import java.util.List;

public interface IRes <T>{
    void ajouter(ReservationVoiture reservation) throws SQLException;
   void modifier(ReservationVoiture reservation) throws SQLException;
    void supprimer(int id) throws SQLException;
   // List<ReservationVoiture> afficher() throws SQLException;
}
