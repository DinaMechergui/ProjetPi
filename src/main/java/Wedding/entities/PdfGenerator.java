package Wedding.entities;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;

public class PdfGenerator {

    public static void generateInvoicePdf(Facture facture, String filePath) throws DocumentException, IOException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));

        document.open();

        // Titre
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
        Paragraph title = new Paragraph("Facture", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        // Espace
        document.add(new Paragraph(" "));

        // Détails de la facture
        Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
        document.add(new Paragraph("Date : " + facture.getDateFacture(), contentFont));
        document.add(new Paragraph("Total : " + facture.getTotal() + " TND", contentFont));

        // Espace
        document.add(new Paragraph(" "));

        // Message de remerciement
        Paragraph thanks = new Paragraph("Merci pour votre achat !", contentFont);
        thanks.setAlignment(Element.ALIGN_CENTER);
        document.add(thanks);

        document.close();
    }
}