package tn.esprit.tacheuser.utils;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.io.IOException;

public class ExportPDF {
    public static void generatePDF(String filePath, String content) {
        try {
            File file = new File(filePath);
            file.getParentFile().mkdirs(); // Crée le dossier s'il n'existe pas

            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph(content));

            document.close();
            System.out.println("PDF généré avec succès : " + filePath);
        } catch (IOException e) {
            System.err.println("Erreur lors de la création du PDF : " + e.getMessage());
        }
    }
}
