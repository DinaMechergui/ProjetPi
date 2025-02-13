//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.Wedding.service;

import java.sql.SQLException;
import org.Wedding.entities.Commande;

public interface IServiceCommande {
    int ajouterReservation(Commande var1) throws SQLException;

    void confirmerCommande(int var1) throws SQLException;

    void annulerReservation(int var1) throws SQLException;
}
