package Wedding.entities;

import com.itextpdf.text.DocumentException;
import org.xhtmlrenderer.pdf.ITextRenderer;
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

public class PdfGenerator {

    /**
     * Génère une facture PDF à partir d'un template HTML.
     *
     * @param facture  La facture à générer.
     * @param user     L'utilisateur (client) de la facture.
     * @param filePath Le chemin où enregistrer le fichier PDF.
     * @throws DocumentException Si une erreur survient lors de la génération du PDF.
     * @throws IOException       Si une erreur survient lors de la lecture du fichier HTML.
     */
    public static void generateInvoicePdf(Facture facture, User user, String filePath) throws DocumentException, IOException, com.lowagie.text.DocumentException, SQLException {
        // Charger le template HTML depuis les ressources
        String htmlTemplate = loadHtmlTemplate("invoice_template.html");

        // Remplacer les placeholders dans le template HTML
        Map<String, String> data = new HashMap<>();
        data.put("{{factureId}}", String.valueOf(facture.getId()));
        data.put("{{dateFacture}}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        data.put("{{emetteurNom}}", "Wedding Planner");
        data.put("{{emetteurTelephone}}", "+123 456 789");
        data.put("{{emetteurEmail}}", "wedding@planner.com");

        // Ajouter les informations de l'utilisateur (client)
        data.put("{{destinataireNom}}", user.getNom());
        data.put("{{destinatairePrenom}}", user.getPrenom());
        data.put("{{destinataireTelephone}}", user.getTel());

        // Ajouter les détails des produits réservés
        StringBuilder produitsHtml = new StringBuilder();
        double totalHT = 0;

        // Utilisation de la méthode ServiceCommande pour obtenir la liste des produits et des quantités réservées
        List<Pair<Produit, Integer>> produitsEtQuantites = ServiceCommande.getProduitsEtQuantitesDansPanier(facture.getCommande().getId());
        if (produitsEtQuantites.isEmpty()) {
            System.out.println("❌ Aucun produit trouvé avec une réservation !");
        } else {
            for (Pair<Produit, Integer> pair : produitsEtQuantites) {
                Produit produit = pair.getKey();
                int quantiteReservee = pair.getValue();

                produitsHtml.append("<tr>")
                        .append("<td>").append(produit.getNom()).append("</td>")
                        .append("<td>").append(quantiteReservee).append("</td>") // Quantité réservée
                        .append("<td>").append(String.format("%.2f", produit.getPrix())).append(" €</td>")
                        .append("<td>").append(String.format("%.2f", produit.getPrix() * quantiteReservee)).append(" €</td>")
                        .append("</tr>");

                totalHT += produit.getPrix() * quantiteReservee;
            }
        }

        // Ajout des données à la facture
        data.put("{{produits}}", produitsHtml.toString());
        data.put("{{total}}", String.format("%.2f", totalHT));
        data.put("{{sommeTotaleAvecTaxe}}", String.format("%.2f", totalHT * 1.2)); // Exemple de taxe de 20%
        data.put("{{taxe}}", String.format("%.2f", totalHT * 0.2));

        // Remplacer les placeholders dans le template
        String htmlContent = replacePlaceholders(htmlTemplate, data);

        // Générer le PDF à partir du HTML
        try (OutputStream outputStream = new FileOutputStream(filePath)) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);
            System.out.println("Facture PDF générée avec succès : " + filePath);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du PDF : " + e.getMessage());
            throw e;
        }
    }

    /**
     * Remplace les placeholders dans le template HTML par les données dynamiques.
     *
     * @param template Le contenu du template HTML.
     * @param data     Les données à insérer dans le template.
     * @return Le contenu HTML avec les placeholders remplacés.
     */
    private static String replacePlaceholders(String template, Map<String, String> data) {
        for (Map.Entry<String, String> entry : data.entrySet()) {
            template = template.replace(entry.getKey(), entry.getValue());
        }
        return template;
    }

    /**
     * Charge le template HTML depuis les ressources.
     *
     * @param templatePath Le chemin du fichier template.
     * @return Le contenu du template sous forme de chaîne.
     * @throws IOException Si une erreur survient lors du chargement du fichier.
     */
    private static String loadHtmlTemplate(String templatePath) throws IOException {
        InputStream inputStream = PdfGenerator.class.getClassLoader().getResourceAsStream(templatePath);
        if (inputStream == null) {
            throw new FileNotFoundException("Le fichier " + templatePath + " n'a pas été trouvé dans les ressources.");
        }
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }
}
