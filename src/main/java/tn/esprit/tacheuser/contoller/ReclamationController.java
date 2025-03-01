package tn.esprit.tacheuser.contoller;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import tn.esprit.tacheuser.models.Reclamation;
import tn.esprit.tacheuser.models.User;
import tn.esprit.tacheuser.service.ReclamationService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.FileOutputStream;
import java.util.List;
import java.io.IOException;
import javafx.scene.control.Alert;

public class ReclamationController {

    @FXML
    private ListView<Reclamation> reclamationsListView;
    @FXML
    private TextField sujetField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private TextField statutField;

    private final ReclamationService reclamationService = new ReclamationService();
    private ObservableList<Reclamation> reclamationsList;

    @FXML
    public void initialize() {
        System.out.println("🔄 Initialisation du contrôleur !");
        loadReclamations();
        setupListView();
    }

    private void loadReclamations() {
        List<Reclamation> reclamations = reclamationService.getAllReclamations();
        reclamationsList = FXCollections.observableArrayList(reclamations);
        reclamationsListView.setItems(reclamationsList);
        System.out.println("📢 Réclamations chargées : " + reclamationsList.size());
    }

    private void setupListView() {
        reclamationsListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Reclamation reclamation, boolean empty) {
                super.updateItem(reclamation, empty);
                if (empty || reclamation == null) {
                    setText(null);
                } else {
                    setText(reclamation.getSujet() + " - " + reclamation.getStatut());
                }
            }
        });
    }

    @FXML
    private void handleSelection() {
        Reclamation selected = reclamationsListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            sujetField.setText(selected.getSujet());
            descriptionField.setText(selected.getDescription());
            statutField.setText(selected.getStatut());
        }
    }

    @FXML
    private void handleAdd() {
        if (sujetField.getText().isEmpty() || descriptionField.getText().isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        Reclamation newReclamation = new Reclamation(0, 1, sujetField.getText(), descriptionField.getText(), "En attente");
        boolean success = reclamationService.addReclamation(newReclamation);

        if (success) {
            System.out.println("✅ Réclamation ajoutée avec succès !");
            loadReclamations();
            clearFields();
        } else {
            showAlert("Erreur", "Échec de l'ajout de la réclamation.");
        }
    }

    @FXML
    private void handleUpdate() {
        Reclamation selected = reclamationsListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setSujet(sujetField.getText());
            selected.setDescription(descriptionField.getText());

            boolean success = reclamationService.updateReclamation(selected);

            if (success) {
                System.out.println("✅ Réclamation mise à jour avec succès !");
                loadReclamations();
            } else {
                showAlert("Erreur", "Échec de la mise à jour de la réclamation.");
            }
        } else {
            showAlert("Sélection requise", "Veuillez sélectionner une réclamation à modifier.");
        }
    }

    @FXML
    private void goToUsers() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/tacheuser/User/users.fxml"));
            Scene usersScene = new Scene(loader.load());

            Stage stage = (Stage) sujetField.getScene().getWindow();
            stage.setScene(usersScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            //showAlert("Erreur", "Erreur lors du chargement de la page des utilisateurs.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleDelete() {
        Reclamation selected = reclamationsListView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Sélection requise", "Veuillez sélectionner une réclamation à supprimer.");
            return;
        }

        if (selected.getId() == 0) {
            showAlert("Erreur", "ID invalide pour la réclamation sélectionnée.");
            return;
        }

        reclamationService.deleteReclamation(selected.getId());
        loadReclamations();
        clearFields();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        sujetField.clear();
        descriptionField.clear();
        statutField.clear();
    }
    @FXML
    private void handleCloseStatus() {
        Reclamation selected = reclamationsListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setStatut("Fermée");
            boolean success = reclamationService.updateReclamation(selected);

            if (success) {
                System.out.println("✅ Réclamation fermée avec succès !");
                loadReclamations();
            } else {
                showAlert("Erreur", "Échec de la mise à jour du statut.");
            }
        } else {
            showAlert("Sélection requise", "Veuillez sélectionner une réclamation à fermer.");
        }
    }
    @FXML
    public void handleexportpdf(MouseEvent event) throws IOException {
        Document document = new Document();
        try {
            // Spécifier le fichier de sortie pour le PDF
            PdfWriter.getInstance(document, new FileOutputStream("reclamation_liste.pdf"));

            // Ouvrir le document pour écrire
            document.open();

            // Titre du document
            Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
            Paragraph title = new Paragraph("Liste des Réclamations", titleFont);
            document.add(title);

            // Espacement avant la liste
            document.add(new Paragraph("\n"));

            // Obtenir la liste des réclamations
            List<Reclamation> reclamations = reclamationService.getAllReclamations();

            // Style de texte pour les informations des réclamations
            Font reclamationFont = new Font(Font.FontFamily.TIMES_ROMAN, 12);

            // Ajouter chaque réclamation dans le PDF
            for (Reclamation reclamation : reclamations) {
                String reclamationDetails = "Sujet: " + reclamation.getSujet() + "\n" +
                        "Description: " + reclamation.getDescription() + "\n" +
                        "Statut: " + reclamation.getStatut() + "\n";
                document.add(new Paragraph(reclamationDetails, reclamationFont));
                document.add(new Paragraph("\n"));
            }

            // Fermer le document
            document.close();

            // Afficher un message de confirmation
            showAlert("Succès", "Le PDF des réclamations a été exporté avec succès!");
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'exportation du PDF.");
        }
    }


}
