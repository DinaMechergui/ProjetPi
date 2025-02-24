package services;

// Importation des classes nécessaires
import entities.reserve; // Importation de la classe reserve qui représente une réservation
import entities.Event; // Importation de la classe Event qui représente un événement
import entities.ServiceItem; // Importation de la classe ServiceItem qui représente un service
import org.Wedding.utils.MyDatabase; // Importation de la classe MyDatabase pour la connexion à la base de données
import java.sql.*; // Importation des classes SQL nécessaires (Connection, PreparedStatement, ResultSet, etc.)
import java.util.ArrayList; // Importation de la classe ArrayList pour stocker les réservations
import java.util.List; // Importation de la classe List pour manipuler des listes d'objets

// Déclaration de la classe ServiceReservation qui implémente l'interface IService<reserve>
public class ServiceReservation implements IService<reserve> {
    private Connection connection; // Déclaration d'un objet Connection pour gérer la connexion à la base de données

    // Constructeur de la classe qui initialise la connexion à la base de données
    public ServiceReservation() {
        this.connection = MyDatabase.getInstance().getConnection();
    }

    // Méthode pour ajouter une nouvelle réservation
    @Override
    public int ajouter(reserve reservation) throws SQLException {
        // Vérifie si l'événement et le service existent avant d'ajouter la réservation
        validateEventExists(reservation.getEvent().getId());
        validateServiceExists(reservation.getService().getId());

        // Requête SQL pour insérer une réservation dans la table reservation
        String req = "INSERT INTO reservation (event_id, service_id, date_reservation, statut, prix_total) VALUES (?, ?, ?, ?, ?)";

        // Utilisation d'un PreparedStatement pour sécuriser l'insertion
        try (PreparedStatement ps = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, reservation.getEvent().getId()); // Associe l'ID de l'événement à la réservation
            ps.setInt(2, reservation.getService().getId()); // Associe l'ID du service à la réservation
            ps.setDate(3, new java.sql.Date(reservation.getDateReservation().getTime())); // Convertit et insère la date de réservation
            ps.setString(4, reservation.getStatut().name()); // Insère le statut de la réservation
            ps.setDouble(5, reservation.getPrixTotal()); // Insère le prix total de la réservation

            ps.executeUpdate(); // Exécute l'insertion

            // Récupération de l'ID généré automatiquement
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Retourne l'ID de la réservation insérée
                }
            }
            return -1; // Retourne -1 si l'insertion a échoué
        }
    }

    // Méthode pour vérifier si un événement existe dans la base de données
    private void validateEventExists(int eventId) throws SQLException {
        String checkEvent = "SELECT id FROM event WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(checkEvent)) {
            ps.setInt(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Event with ID " + eventId + " does not exist");
                }
            }
        }
    }

    // Méthode pour vérifier si un service existe dans la base de données
    private void validateServiceExists(int serviceId) throws SQLException {
        String checkService = "SELECT id FROM service WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(checkService)) {
            ps.setInt(1, serviceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Service with ID " + serviceId + " does not exist");
                }
            }
        }
    }

    // Méthode pour modifier une réservation existante
    @Override
    public void modifier(reserve reservation) throws SQLException {
        // Vérifie si l'événement et le service existent avant modification
        validateEventExists(reservation.getEvent().getId());
        validateServiceExists(reservation.getService().getId());

        // Requête SQL pour mettre à jour une réservation
        String req = "UPDATE reservation SET event_id=?, service_id=?, date_reservation=?, statut=?, prix_total=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, reservation.getEvent().getId()); // Mise à jour de l'ID de l'événement
            ps.setInt(2, reservation.getService().getId()); // Mise à jour de l'ID du service
            ps.setDate(3, new java.sql.Date(reservation.getDateReservation().getTime())); // Mise à jour de la date de réservation
            ps.setString(4, reservation.getStatut().name()); // Mise à jour du statut
            ps.setDouble(5, reservation.getPrixTotal()); // Mise à jour du prix total
            ps.setInt(6, reservation.getId()); // Spécification de l'ID de la réservation à modifier
            ps.executeUpdate(); // Exécute la mise à jour
        }
    }

    // Méthode pour supprimer une réservation en fonction de son ID
    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM reservation WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, id); // Spécifie l'ID de la réservation à supprimer
            ps.executeUpdate(); // Exécute la suppression
        }
    }

    // Méthode pour récupérer la liste de toutes les réservations
    @Override
    public List<reserve> afficher() throws SQLException {
        List<reserve> reservations = new ArrayList<>();
        String req = "SELECT * FROM reservation"; // Requête SQL pour récupérer toutes les réservations
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(req)) {
            while (rs.next()) {
                reserve res = new reserve();
                res.setId(rs.getInt("id"));
                // Chargement complet des événements et services associés
                res.setEvent(loadEvent(rs.getInt("event_id")));
                res.setService(loadService(rs.getInt("service_id")));
                res.setDateReservation(rs.getDate("date_reservation"));
                res.setStatut(reserve.StatutReservation.fromString(rs.getString("statut")));
                res.setPrixTotal(rs.getDouble("prix_total"));
                reservations.add(res);
            }
        }
        return reservations; // Retourne la liste des réservations
    }

    // Méthode pour charger un événement en fonction de son ID
    private Event loadEvent(int eventId) throws SQLException {
        String req = "SELECT * FROM event WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Event(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getDate("date"),
                            rs.getString("lieu"),
                            rs.getString("description"),
                            rs.getString("statut"),
                            rs.getString("image_url")
                    );
                }
            }
        }
        throw new SQLException("Event not found with ID: " + eventId);
    }

    // Méthode pour charger un service en fonction de son ID
    private ServiceItem loadService(int serviceId) throws SQLException {
        String req = "SELECT * FROM service WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, serviceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ServiceItem(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("description"),
                            rs.getDouble("prix"),
                            rs.getString("image_url")
                    );
                }
            }
        }
        throw new SQLException("Service not found with ID: " + serviceId);
    }

}
