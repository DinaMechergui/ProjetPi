package tn.esprit.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import tn.esprit.entities.Evenement;
import tn.esprit.entities.Invite;
import tn.esprit.services.ServiceEvenement;
import tn.esprit.services.ServiceInvite;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

public class Ajouterinvite {

    @FXML
    private TextField nomtf;

    @FXML
    private TextField prenomtf;

    @FXML
    private TextField emailtf;

    @FXML
    private TextField teltf;

    @FXML
    private TextField confimationtf;

    @FXML
    private ComboBox<String> evenementCB;

    @FXML
    private Label evenementActuelLabel;

    private final ServiceEvenement serviceEvenement = new ServiceEvenement();
    private Evenement evenementActuel;

    public void setEvenementActuel(Evenement evenement) {
        this.evenementActuel = evenement;
        if (evenementActuelLabel != null) {
            evenementActuelLabel.setText("Événement : " + evenement.getNom());
        }
        if (evenementCB != null) {
            evenementCB.getSelectionModel().select(evenement.getNom());
        }
    }

    @FXML
    public void initialize() {
        nomtf.setPromptText("Entrez le nom");
        prenomtf.setPromptText("Entrez le prénom");
        emailtf.setPromptText("Entrez l'email");
        teltf.setPromptText("Entrez le téléphone (8 chiffres)");
        evenementCB.setPromptText("Sélectionnez un événement");

        Platform.runLater(() -> {
            List<Evenement> evenements = serviceEvenement.getAllEvenements();
            if (evenements == null || evenements.isEmpty()) {
                afficherErreur("⚠ Aucun événement trouvé !");
            } else {
                evenements.forEach(e -> evenementCB.getItems().add(e.getNom()));
            }
        });
    }

    @FXML
    void ajouterInvite(ActionEvent event) throws SQLException {
        ServiceInvite serviceInvite = new ServiceInvite();

        String nom = nomtf.getText().trim();
        String prenom = prenomtf.getText().trim();
        String email = emailtf.getText().trim();
        String telephone = teltf.getText().trim();

        String nomEvenement = evenementCB.getSelectionModel().getSelectedItem();
        if (nomEvenement == null) {
            afficherErreur("Veuillez sélectionner un événement !");
            return;
        }

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || telephone.isEmpty()) {
            afficherErreur("Tous les champs doivent être renseignés !");
            return;
        }

        if (!telephone.matches("\\d{8}")) {
            afficherErreur("Le téléphone doit contenir exactement 8 chiffres.");
            return;
        }

        if (!email.contains("@")) {
            afficherErreur("L'email doit contenir le caractère '@'.");
            return;
        }

        try {
            int evenementId;
            if (evenementActuel != null && evenementActuel.getNom().equals(nomEvenement)) {
                evenementId = evenementActuel.getId();
            } else {
                evenementId = serviceEvenement.getIdByName(nomEvenement);
            }
            if (evenementId == -1) {
                afficherErreur("L'événement sélectionné est introuvable !");
                return;
            }
            boolean confirmation = false;

            // Ajouter l'invité
            Invite invite = new Invite(0, nom, prenom, email, telephone, confirmation, evenementId);
            serviceInvite.ajouter(invite);

            // Récupérer le code unique de l'événement
           String codeUniqueEvenement = getCodeUniqueEvenement(evenementId);
            if (codeUniqueEvenement == null) {
                return; // Sortir de la méthode si le code unique est introuvable
            }

            // Générer un QR Code pour l'invité
            System.out.println("ID de l'invité après insertion : " + invite.getId());
            String dd = "1247";
            String codeUnique = codeUniqueEvenement + invite.getId(); // Exemple de code unique
            String qrCodeFilePath = generateQRCode(codeUnique, "invite_" + invite.getId());

            // Envoyer un e-mail avec le QR Code
            String subject = "Votre invitation à l'événement " + nomEvenement;
            String body = "Bonjour " + prenom + " " + nom + ",\n\n"
                    + "Vous êtes invité à l'événement : " + nomEvenement + ".\n"
                    + "Votre QR Code est joint à cet e-mail.\n\n"
                    + "Cordialement,\nL'équipe d'organisation.";
            EmailUtil.sendEmailWithAttachment(email, subject, body, qrCodeFilePath);

            // Afficher le QR Code dans une nouvelle fenêtre
            afficherQRCode(qrCodeFilePath);

            // Afficher un message de succès
            afficherInformation("✅ L'invité a été ajouté avec succès et un e-mail a été envoyé !");

            // Réinitialiser les champs
            reinitialiserChamps();
        } catch (SQLException e) {
            afficherErreur("❌ Une erreur est survenue : " + e.getMessage());
        } catch (WriterException | IOException e) {
            afficherErreur("❌ Erreur lors de la génération du QR Code : " + e.getMessage());
        }
    }
    private String getCodeUniqueEvenement(int evenementId) throws SQLException {
        String codeUniqueEvenement = serviceEvenement.getCodeUniqueByEventId(evenementId);
        if (codeUniqueEvenement == null) {
            afficherErreur("Le code unique de l'événement est introuvable !");
        }
        return codeUniqueEvenement;
    }

    private void reinitialiserChamps() {
        nomtf.clear();
        prenomtf.clear();
        emailtf.clear();
        teltf.clear();
        //confimationtf.clear();
        evenementCB.getSelectionModel().clearSelection();
    }

    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText("Problème de saisie ou de traitement");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void afficherInformation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText("Opération réussie");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void retour(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MenuOrganizer.fxml"));
            Parent root = loader.load();
            ((Button) event.getSource()).getScene().setRoot(root);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String generateQRCode(String data, String fileName) throws WriterException, IOException {
        int width = 300;
        int height = 300;
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height);

        String filePath = "src/qrcodes/" + fileName + ".png"; // Dossier où stocker les QR Codes
        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

        return filePath;
    }


    private void afficherQRCode(String filePath) {
        try {
            // Charger l'image du QR Code
            Image qrCodeImage = new Image(new File(filePath).toURI().toString());
            ImageView qrCodeView = new ImageView(qrCodeImage);
            qrCodeView.setFitWidth(200);
            qrCodeView.setFitHeight(200);

            // Créer une nouvelle fenêtre pour afficher le QR Code
            Stage qrCodeStage = new Stage();
            qrCodeStage.setTitle("QR Code de l'invité");

            // Créer un conteneur pour l'image
            VBox vbox = new VBox(qrCodeView);
            vbox.setAlignment(Pos.CENTER);
            vbox.setPadding(new Insets(20));

            // Configurer la scène et afficher la fenêtre
            Scene scene = new Scene(vbox, 250, 250);
            qrCodeStage.setScene(scene);
            qrCodeStage.show();
        } catch (Exception e) {
            afficherErreur("❌ Erreur lors de l'affichage du QR Code : " + e.getMessage());
        }
    }
}