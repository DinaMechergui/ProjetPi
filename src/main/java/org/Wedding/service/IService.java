//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.Wedding.service;

import java.sql.SQLException;
import java.util.List;

public interface IService<T> {
    int ajouter(T var1) throws SQLException;

    void modifier(T var1) throws SQLException;

    void supprimer(int var1) throws SQLException;

    List<T> afficher() throws SQLException;
}
