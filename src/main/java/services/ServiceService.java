package services;

import entities.ServiceItem;
import org.Wedding.utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceService implements IService<ServiceItem> {
    private Connection connection;

    public ServiceService() {
        this.connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public int ajouter(ServiceItem ServiceItem) throws SQLException {
        String req = "INSERT INTO service (nom, description, prix, image_url) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, ServiceItem.getNom());
            ps.setString(2, ServiceItem.getDescription());
            ps.setDouble(3, ServiceItem.getPrix());
            ps.setString(4, ServiceItem.getImageUrl());

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
            return -1;
        }
    }

    @Override
    public void modifier(ServiceItem ServiceItem) throws SQLException {
        String req = "UPDATE service SET nom=?, description=?, prix=?, image_url=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, ServiceItem.getNom());
            ps.setString(2, ServiceItem.getDescription());
            ps.setDouble(3, ServiceItem.getPrix());
            ps.setString(4, ServiceItem.getImageUrl());
            ps.setInt(5, ServiceItem.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM service WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }


    public List<ServiceItem> afficher() throws SQLException {
        List<ServiceItem> services = new ArrayList<>();
        String req = "SELECT * FROM service";  // Requête à adapter selon ta base
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                services.add(new ServiceItem(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getDouble("prix"),
                        rs.getString("image_url")  // Assure-toi que l'URL est bien récupérée
                ));
            }
        }
        return services;
    }

}