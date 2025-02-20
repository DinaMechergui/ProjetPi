package tn.esprit.services;



import java.sql.SQLException;
import java.util.List;

public interface IService<T> {
    // Méthode pour ajouter une entité
    void ajouter(T t) throws SQLException;

    // Méthode pour modifier une entité
    void modifier(T t) throws SQLException;

    // Méthode pour supprimer une entité
    void supprimer(int id) throws SQLException;

    // Méthode pour afficher toutes les entités
    List<T> afficher() throws SQLException;
}