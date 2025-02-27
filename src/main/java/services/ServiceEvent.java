package services;

// Importation des classes nécessaires
import entities.Event;  // Importation de la classe Event qui représente un événement
import Wedding.utils.MyDatabase;

import java.sql.*;  // Importation des classes SQL nécessaires (Connection, PreparedStatement, ResultSet, etc.)
import java.util.ArrayList;  // Importation de la classe ArrayList pour stocker les événements
import java.util.List;  // Importation de la classe List pour manipuler des listes d'événements

// Déclaration de la classe ServiceEvent qui implémente l'interface IService<Event>
public class ServiceEvent implements IService<Event> {
    private Connection connection; // Déclaration d'un objet Connection pour interagir avec la base de données

    // Constructeur de la classe ServiceEvent
    public ServiceEvent() {
        this.connection = MyDatabase.getInstance().getConnection(); // Initialisation de la connexion à la base de données
    }

    // Méthode pour ajouter un nouvel événement à la base de données
    @Override
    public int ajouter(Event event) throws SQLException {
        // Requête SQL pour insérer un événement dans la table event
        String req = "INSERT INTO event (nom, date, lieu, description, statut, image_url) VALUES (?, ?, ?, ?, ?, ?)";

        // Utilisation d'un PreparedStatement pour exécuter la requête SQL en toute sécurité
        try (PreparedStatement ps = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, event.getNom()); // Remplacement du premier "?" par le nom de l'événement
            ps.setDate(2, new java.sql.Date(event.getDate().getTime())); // Conversion de la date et affectation au deuxième "?"
            ps.setString(3, event.getLieu()); // Remplacement du troisième "?" par le lieu de l'événement
            ps.setString(4, event.getDescription()); // Remplacement du quatrième "?" par la description de l'événement
            ps.setString(5, event.getStatut().name()); // Remplacement du cinquième "?" par le statut de l'événement
            ps.setString(6, event.getImageUrl()); // Remplacement du sixième "?" par l'URL de l'image associée à l'événement

            ps.executeUpdate(); // Exécution de la requête pour insérer l'événement dans la base de données

            // Récupération de l'ID généré automatiquement après l'insertion
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) { // Vérifie si un ID a été généré
                    return generatedKeys.getInt(1); // Retourne l'ID de l'événement inséré
                }
            }
            return -1; // Retourne -1 si l'ajout a échoué
        }
    }

    // Méthode pour modifier un événement existant dans la base de données
    @Override
    public void modifier(Event event) throws SQLException {
        // Requête SQL pour mettre à jour un événement en fonction de son ID
        String req = "UPDATE event SET nom=?, date=?, lieu=?, description=?, statut=?, image_url=? WHERE id=?";

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, event.getNom()); // Mise à jour du nom de l'événement
            ps.setDate(2, new java.sql.Date(event.getDate().getTime())); // Mise à jour de la date
            ps.setString(3, event.getLieu()); // Mise à jour du lieu
            ps.setString(4, event.getDescription()); // Mise à jour de la description
            ps.setString(5, event.getStatut().name()); // Mise à jour du statut
            ps.setString(6, event.getImageUrl()); // Mise à jour de l'image URL
            ps.setInt(7, event.getId()); // Spécification de l'ID de l'événement à modifier

            ps.executeUpdate(); // Exécution de la requête pour mettre à jour l'événement
        }
    }

    // Méthode pour supprimer un événement en fonction de son ID
    @Override
    public void supprimer(int id) throws SQLException {
        // Requête SQL pour supprimer un événement en fonction de son ID
        String req = "DELETE FROM event WHERE id=?";

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, id); // Spécification de l'ID de l'événement à supprimer
            ps.executeUpdate(); // Exécution de la requête pour supprimer l'événement
        }
    }

    // Méthode pour récupérer la liste de tous les événements depuis la base de données
    @Override
    public List<Event> afficher() throws SQLException {
        List<Event> events = new ArrayList<>(); // Création d'une liste vide pour stocker les événements
        String req = "SELECT * FROM event"; // Requête SQL pour récupérer tous les événements

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(req)) {
            while (rs.next()) { // Parcourt chaque ligne du résultat de la requête
                events.add(new Event( // Création d'un nouvel objet Event à partir des données récupérées
                        rs.getInt("id"), // Récupération de l'ID de l'événement
                        rs.getString("nom"), // Récupération du nom de l'événement
                        rs.getDate("date"), // Récupération de la date
                        rs.getString("lieu"), // Récupération du lieu
                        rs.getString("description"), // Récupération de la description
                        rs.getString("statut"), // Récupération du statut
                        rs.getString("image_url") // Récupération de l'URL de l'image
                ));
            }
        }
        return events; // Retourne la liste des événements récupérés
    }
}
