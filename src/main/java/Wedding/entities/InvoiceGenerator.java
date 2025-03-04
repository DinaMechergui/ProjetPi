package Wedding.entities;

import javafx.util.Pair;
import tn.esprit.tacheuser.models.User;
import Wedding.service.ServiceCommande;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvoiceGenerator {

    public static void generateInvoice(User user, Facture facture, List<Produit> produits, String outputPath) throws IOException, SQLException {
        // Charger le template depuis les ressources
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

        // Ajouter les détails des produits
        StringBuilder produitsHtml = new StringBuilder();
        double totalHT = 0;
        List<Pair<Produit, Integer>> produitsEtQuantites = ServiceCommande.getProduitsEtQuantitesDansPanier(facture.getCommande().getId());
        if (produitsEtQuantites.isEmpty()) {
            System.out.println("❌ Aucun produit trouvé avec une réservation !");
        } else {
            for (Pair<Produit, Integer> pair : produitsEtQuantites) {
                System.out.println("Produit : " + pair.getKey().getNom() + " | Quantité réservée : " + pair.getValue());
            }
        }

        for (Pair<Produit, Integer> pair : produitsEtQuantites) {
            Produit produit = pair.getKey();
            int quantiteReservee = pair.getValue();

            produitsHtml.append("<tr>")
                    .append("<td>").append(produit.getNom()).append("</td>")
                    .append("<td>").append(quantiteReservee).append("</td>") // Utilisation de la quantité réservée
                    .append("<td>").append(String.format("%.2f", produit.getPrix())).append(" €</td>")
                    .append("<td>").append(String.format("%.2f", produit.getPrix() * quantiteReservee)).append(" €</td>")
                    .append("</tr>");

        totalHT += produit.getPrix() * produit.getStock();
        }
        System.out.println("✅ Vérification après remplissage : " + data.get("{{produits}}"));

        data.put("{{produits}}", produitsHtml.toString());
        data.put("{{total}}", String.format("%.2f", totalHT));
        data.put("{{sommeTotaleAvecTaxe}}", String.format("%.2f", totalHT * 1.2)); // Exemple de taxe de 20%
        data.put("{{taxe}}", String.format("%.2f", totalHT * 0.2));

        // Remplacer les placeholders dans le template
        // Remplacement des placeholders
        for (Map.Entry<String, String> entry : data.entrySet()) {
            System.out.println("Clé : " + entry.getKey() + " | Valeur : " + entry.getValue());

            template = template.replace(entry.getKey(), entry.getValue());
        }

// Vérification du contenu HTML avant l'écriture
        System.out.println("HTML généré : \n" + template);

// Écrire le fichier HTML final
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath), StandardCharsets.UTF_8))) {
            writer.write(template);
        }

    }

}