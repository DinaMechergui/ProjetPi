package tn.esprit.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class QRCodeGenerator {

    // Générer un code unique pour l'événement
    public static String generateUniqueCode() {
        return UUID.randomUUID().toString(); // Exemple de code unique
    }

    // Générer un QR Code à partir du code unique
    public static String generateQRCode(String data, String fileName) throws WriterException, IOException {
        int width = 300;
        int height = 300;
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height);

        // Chemin absolu pour le dossier qrcodes
        String directoryPath = System.getProperty("user.dir") + "/qrcodes/";
        File directory = new File(directoryPath);

        // Créer le dossier s'il n'existe pas
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                throw new IOException("Impossible de créer le dossier : " + directoryPath);
            }
        }

        // Chemin complet du fichier
        String filePath = directoryPath + fileName + ".png";
        Path path = FileSystems.getDefault().getPath(filePath);

        try {
            // Écrire le QR Code dans le fichier
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
        } catch (IOException e) {
            throw new IOException("Erreur lors de l'écriture du fichier : " + e.getMessage());
        }

        return filePath;
    }
}