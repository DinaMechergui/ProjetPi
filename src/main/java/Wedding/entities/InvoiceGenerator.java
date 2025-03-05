package Wedding.entities;

import entities.ServiceItem;
import javafx.util.Pair;
import tn.esprit.tacheuser.models.User;
import Wedding.service.ServiceCommande;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvoiceGenerator {

    public static void generateInvoice(User user, Facture facture, List<Produit> produits, String outputPath)
            throws IOException, SQLException {

        // Charger le template HTML depuis les ressources
        InputStream inputStream = InvoiceGenerator.class.getClassLoader().getResourceAsStream("invoice_template.html");
        if (inputStream == null) {
            throw new FileNotFoundException("Le fichier invoice_template.html n'a pas été trouvé dans les ressources.");
        }
        String template = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        // Créer une map pour stocker les données dynamiques
        Map<String, String> data = new HashMap<>();
        data.put("{{factureId}}", String.valueOf(facture.getId()));
        data.put("{{dateFacture}}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        data.put("{{emetteurNom}}", "Wedding Planner");
        data.put("{{emetteurTelephone}}", "+123 456 789");
        data.put("{{emetteurEmail}}", "wedding@planner.com");
        data.put("{{destinataireNom}}", user.getNom());
        data.put("{{destinatairePrenom}}", user.getPrenom());
        data.put("{{destinataireTelephone}}", user.getTel());

        // 🛒 Récupérer les produits et services réservés depuis `reservation`
        List<Pair<Produit, Integer>> produitsEtQuantites = ServiceCommande.getProduitsEtQuantitesDansPanier(facture.getCommande().getId());
        List<Pair<ServiceItem, LocalDate>> servicesReserves = ServiceCommande.getServicesReserves(user.getPrenom());

        // 🛍️ Construire le HTML des produits
        StringBuilder produitsHtml = new StringBuilder();
        if (produitsEtQuantites.isEmpty()) {
            produitsHtml.append("<tr><td colspan='4' style='text-align:center;'>Aucun produit réservé</td></tr>");
        } else {
            for (Pair<Produit, Integer> pair : produitsEtQuantites) {
                Produit produit = pair.getKey();
                int quantiteReservee = pair.getValue();

                produitsHtml.append("<tr>")
                        .append("<td>").append(produit.getNom()).append("</td>")
                        .append("<td>").append(quantiteReservee).append("</td>")
                        .append("<td>").append(String.format("%.2f", produit.getPrix())).append(" €</td>")
                        .append("<td>").append(String.format("%.2f", produit.getPrix() * quantiteReservee)).append(" €</td>")
                        .append("</tr>");
            }
        }

        // 🛎️ Construire le HTML des services
        StringBuilder servicesHtml = new StringBuilder();
        if (servicesReserves.isEmpty()) {
            servicesHtml.append("<tr><td colspan='3' style='text-align:center;'>Aucun service réservé</td></tr>");
        } else {
            for (Pair<ServiceItem, LocalDate> pair : servicesReserves) {
                ServiceItem service = pair.getKey();
                LocalDate dateReservation = pair.getValue();

                servicesHtml.append("<tr>")
                        .append("<td>").append(service.getNom()).append("</td>")
                        .append("<td>").append(dateReservation.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("</td>")
                        .append("<td>").append(String.format("%.2f", service.getPrix())).append(" €</td>")
                        .append("</tr>");
            }
        }

        // 💰 Calcul du total avant taxes (HT)
        double totalProduits = produitsEtQuantites.stream().mapToDouble(pair -> pair.getKey().getPrix() * pair.getValue()).sum();
        double totalServices = servicesReserves.stream().mapToDouble(pair -> pair.getKey().getPrix()).sum();
        double totalHT = totalProduits + totalServices;

        // 📌 Ajouter les valeurs calculées à la map
        data.put("{{produits}}", produitsHtml.toString());
        data.put("{{services}}", servicesHtml.toString());
        data.put("{{total}}", String.format("%.2f", totalHT));
        data.put("{{sommeTotaleAvecTaxe}}", String.format("%.2f", totalHT * 1.2)); // TVA 20%
        data.put("{{taxe}}", String.format("%.2f", totalHT * 0.2)); // TVA 20%

        // 🔄 Remplacement des placeholders dans le template
        for (Map.Entry<String, String> entry : data.entrySet()) {
            template = template.replace(entry.getKey(), entry.getValue());
        }

        // 📝 Écrire le fichier HTML final
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath), StandardCharsets.UTF_8))) {
            writer.write(template);
        }

        System.out.println("✅ Facture générée avec succès : " + outputPath);
    }
}
