package org.example.services;

import java.sql.SQLException;
import java.util.List;

    public interface IResHebergement<T> {
        void ajouter(T entity) throws SQLException;
       // void modifier(T entity) throws SQLException;
        //void supprimer(int id) throws SQLException;
        //List<T> afficher() throws SQLException;
    }


