package tn.esprit.tacheuser.contoller;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import tn.esprit.tacheuser.utils.MySQLConnection;

public class Stat {

    @FXML
    private PieChart agePieChart;
    @FXML
    private Label totalUsersLabel;
    @FXML
    private Label maleUsersLabel;
    @FXML
    private Label femaleUsersLabel;
    @FXML
    private Label ageRangeLabel;

    @FXML
    public void initialize() {
        // Charger les statistiques des utilisateurs
        loadUserStatistics();
    }

    private void loadUserStatistics() {
        int maleUsers = 0;
        int femaleUsers = 0;
        int totalUsers = 0;
        int age18_25 = 0, age26_35 = 0, age36_45 = 0, age46Plus = 0;

        // Connexion à la base de données via la classe MySQLConnection
        try (Connection connection = MySQLConnection.getInstance().getConnection()) {
            // 1. Récupérer le total des utilisateurs
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM `user`");
            if (rs.next()) {
                totalUsers = rs.getInt(1);
            }

            // 2. Récupérer le nombre d'hommes et de femmes
            rs = statement.executeQuery("SELECT gender, COUNT(*) FROM `user` GROUP BY gender");
            while (rs.next()) {
                String gender = rs.getString("gender");
                int count = rs.getInt(2);
                if ("Homme".equals(gender)) {
                    maleUsers = count;
                } else if ("Femme".equals(gender)) {
                    femaleUsers = count;
                }
            }

            // 3. Récupérer la répartition par âge
            rs = statement.executeQuery("SELECT age FROM `user`");
            while (rs.next()) {
                int age = rs.getInt("age");
                if (age >= 18 && age <= 25) {
                    age18_25++;
                } else if (age >= 26 && age <= 35) {
                    age26_35++;
                } else if (age >= 36 && age <= 45) {
                    age36_45++;
                } else if (age >= 46) {
                    age46Plus++;
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des statistiques : " + e.getMessage());
            e.printStackTrace();
        }

        // 4. Graphiques
        PieChart.Data slice1 = new PieChart.Data("18-25", age18_25);
        PieChart.Data slice2 = new PieChart.Data("26-35", age26_35);
        PieChart.Data slice3 = new PieChart.Data("36-45", age36_45);
        PieChart.Data slice4 = new PieChart.Data("46+", age46Plus);

        agePieChart.getData().addAll(slice1, slice2, slice3, slice4);

        // 5. Mise à jour des labels
        totalUsersLabel.setText("Total utilisateurs : " + totalUsers);
        maleUsersLabel.setText("Hommes : " + maleUsers);
        femaleUsersLabel.setText("Femmes : " + femaleUsers);
        ageRangeLabel.setText("Répartition par âge :");
    }
}
