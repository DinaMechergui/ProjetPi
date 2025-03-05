package org.example;

import entities.Event;
import entities.ServiceItem;
import entities.reserve;
import services.ServiceEvent;
import services.ServiceReservation;
import services.ServiceService;
import org.Wedding.utils.MyDatabase;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/*public class Main {
   private static ServiceEvent eventService;
    private static ServiceService serviceService;
    private static ServiceReservation reservationService;

    public static void main(String[] args) {
        Connection connection = null;
        try {
            // 1. Initialize connection and services
            connection = MyDatabase.getInstance().getConnection();
            eventService = new ServiceEvent();
            serviceService = new ServiceService();
            reservationService = new ServiceReservation();

            System.out.println("✅ Database connection established");

            // 2. Test full CRUD cycle
            testEventCRUD();
            testServiceCRUD();
            testReservationCRUD();

        } catch (SQLException e) {
            System.err.println("🚨 Database Error:");
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) connection.close();
                System.out.println("\n🔌 Database connection closed");
            } catch (SQLException e) {
                System.err.println("🚨 Connection closure failed:");
                e.printStackTrace();
            }
        }
    }

    private static void testEventCRUD() throws SQLException {
        System.out.println("\n=== EVENT CRUD TEST ===");

        // Create
        Event newEvent = new Event(
                0, "Winter Wedding", new Date(),
                "Mountain Lodge", "Snow-themed wedding",
                "EN_PREPARATION", "https://example.com/winter.jpg"
        );
        int eventId = eventService.ajouter(newEvent);
        System.out.println("✅ Event created - ID: " + eventId);

        // Read
        Event createdEvent = getEventById(eventId);
        printEventDetails("Created Event", createdEvent);

        // Update
        createdEvent.setDescription("Luxury snow-themed wedding with ice sculptures");
        createdEvent.setStatut(Event.Statut.CONFIRME);
        eventService.modifier(createdEvent);
        Event updatedEvent = getEventById(eventId);
        printEventDetails("Updated Event", updatedEvent);

       // Delete
        eventService.supprimer(eventId);
        System.out.println("🗑️ Event deleted");
        verifyDeletion(() -> eventService.afficher(), "Events");
   }

    private static void testServiceCRUD() throws SQLException {
        System.out.println("\n=== SERVICE CRUD TEST ===");

        // Create
        ServiceItem newServiceItem = new ServiceItem(
                0, "Floral Arrangements",
                "Winter bouquet and venue decorations",
                1200.0, "https://example.com/flowers.jpg"
        );
        int serviceId = serviceService.ajouter(newServiceItem);
        System.out.println("✅ Service created - ID: " + serviceId);

        // Read
        ServiceItem createdServiceItem = getServiceById(serviceId);
        printServiceDetails("Created Service", createdServiceItem);

        // Update
        createdServiceItem.setPrix(1500.0);
        createdServiceItem.setDescription("Premium winter floral arrangements");
        serviceService.modifier(createdServiceItem);
        ServiceItem updatedServiceItem = getServiceById(serviceId);
        printServiceDetails("Updated Service", updatedServiceItem);

       // Delete
        serviceService.supprimer(serviceId);
        System.out.println("🗑️ Service deleted");
        verifyDeletion(() -> serviceService.afficher(), "Services");
    }

    private static void testReservationCRUD() throws SQLException {
        System.out.println("\n=== RESERVATION CRUD TEST ===");

        // Create dependencies
        Event event = createTestEvent();
        ServiceItem ServiceItem = createTestService();

        // Create
        reserve newReservation = new reserve();
        newReservation.setEvent(event);
        newReservation.setService(ServiceItem);
        newReservation.setDateReservation(new Date());
        newReservation.setPrixTotal(ServiceItem.getPrix());
        newReservation.setStatut(reserve.StatutReservation.EN_ATTENTE);

        int reservationId = reservationService.ajouter(newReservation);
        System.out.println("✅ Reservation created - ID: " + reservationId);

        // Read
        reserve createdReservation = getReservationById(reservationId);
        printReservationDetails("Created Reservation", createdReservation);

        // Update
        createdReservation.setStatut(reserve.StatutReservation.CONFIRMEE);
        createdReservation.setPrixTotal(2800.0); // Price adjustment
        reservationService.modifier(createdReservation);
        reserve updatedReservation = getReservationById(reservationId);
        printReservationDetails("Updated Reservation", updatedReservation);

        // Delete
        reservationService.supprimer(reservationId);
        System.out.println("🗑️ Reservation deleted");
        serviceService.supprimer(entities.ServiceItem.getId());
        eventService.supprimer(event.getId());
        verifyDeletion(() -> reservationService.afficher(), "Reservations");
    }

    // Helper methods
    private static Event getEventById(int id) throws SQLException {
        return eventService.afficher().stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElseThrow(() -> new SQLException("Event not found"));
    }

    private static ServiceItem getServiceById(int id) throws SQLException {
        return serviceService.afficher().stream()
                .filter(s -> s.getId() == id)
                .findFirst()
                .orElseThrow(() -> new SQLException("Service not found"));
    }

    private static reserve getReservationById(int id) throws SQLException {
        return reservationService.afficher().stream()
                .filter(r -> r.getId() == id)
                .findFirst()
                .orElseThrow(() -> new SQLException("Reservation not found"));
    }

    private static Event createTestEvent() throws SQLException {
        Event e = new Event(
                0, "Test Event", new Date(),
                "Test Location", "Test Description",
                "EN_PREPARATION", "https://test.com"
        );
        e.setId(eventService.ajouter(e));
        return e;
    }

    private static ServiceItem createTestService() throws SQLException {
        ServiceItem s = new ServiceItem(
                0, "Test Service", "Test Description",
                999.0, "https://test.com"
        );
        s.setId(serviceService.ajouter(s));
        return s;
    }

    private static void printEventDetails(String title, Event e) {
        System.out.println("\n📋 " + title + " Details:");
        System.out.println("ID: " + e.getId());
        System.out.println("Name: " + e.getNom());
        System.out.println("Date: " + e.getDate());
        System.out.println("Location: " + e.getLieu());
        System.out.println("Status: " + e.getStatut());
        System.out.println("Description: " + e.getDescription());
    }

    private static void printServiceDetails(String title, ServiceItem s) {
        System.out.println("\n📋 " + title + " Details:");
        System.out.println("ID: " + s.getId());
        System.out.println("Name: " + s.getNom());
        System.out.println("Price: " + s.getPrix());
        System.out.println("Description: " + s.getDescription());
    }

    private static void printReservationDetails(String title, reserve r) {
        System.out.println("\n📋 " + title + " Details:");
        System.out.println("ID: " + r.getId());
        System.out.println("Event ID: " + r.getEvent().getId());
        System.out.println("Service ID: " + r.getService().getId());
        System.out.println("Date: " + r.getDateReservation());
        System.out.println("Status: " + r.getStatut());
        System.out.println("Total Price: " + r.getPrixTotal());
    }

    private static void verifyDeletion(DataSupplier supplier, String entityName) throws SQLException {
        if (supplier.get().isEmpty()) {
            System.out.println("✅ " + entityName + " table is empty");
        } else {
            System.out.println("❌ " + entityName + " table not empty!");
        }
    }

    @FunctionalInterface
    private interface DataSupplier {
        List<?> get() throws SQLException;
    }
}*/