package tn.esprit.tacheuser.contoller;

import tn.esprit.tacheuser.utils.MySQLConnection;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
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
    @FXML
    private TextField motInterditField;
    @FXML
    private ListView<String> motsInterditsListView;


    private final ReclamationService reclamationService = new ReclamationService();
    private ObservableList<Reclamation> reclamationsList;

    @FXML
    public void initialize() {
        System.out.println("🔄 Initialisation du contrôleur !");
        loadReclamations();
        setupListView();
        loadMotsInterdits();  // Charge les mots interdits depuis la base de données
    }

    private void loadReclamations() {
        List<Reclamation> reclamations = reclamationService.getAllReclamations();
        reclamationsList = FXCollections.observableArrayList(reclamations);
        reclamationsListView.setItems(reclamationsList);
        System.out.println("📢 avis chargées : " + reclamationsList.size());
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
            System.out.println("✅ avis ajoutée avec succès !");
            loadReclamations();
            clearFields();
        } else {
            showAlert("Erreur", "Échec de l'ajout de l avis.");
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
                System.out.println("✅ avis mise à jour avec succès !");
                loadReclamations();
            } else {
                showAlert("Erreur", "Échec de la mise à jour de la avis.");
            }
        } else {
            showAlert("Sélection requise", "Veuillez sélectionner une avis à modifier.");
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
        }
    }

    @FXML
    private void handleDelete() {
        Reclamation selected = reclamationsListView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Sélection requise", "Veuillez sélectionner un avis à supprimer.");
            return;
        }

        if (selected.getId() == 0) {
            showAlert("Erreur", "ID invalide pour l avis sélectionnée.");
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
                System.out.println("✅ avis fermée avec succès !");
                loadReclamations();
            } else {
                showAlert("Erreur", "Échec de la mise à jour du statut.");
            }
        } else {
            showAlert("Sélection requise", "Veuillez sélectionner un avis à fermer.");
        }
    }

    @FXML
    public void handleexportpdf(MouseEvent event) throws IOException {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream("avis_liste.pdf"));
            document.open();
            Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
            Paragraph title = new Paragraph("Liste avis", titleFont);
            document.add(title);
            document.add(new Paragraph("\n"));

            List<Reclamation> reclamations = reclamationService.getAllReclamations();
            Font reclamationFont = new Font(Font.FontFamily.TIMES_ROMAN, 12);
            for (Reclamation reclamation : reclamations) {
                String reclamationDetails = "Sujet: " + reclamation.getSujet() + "\n" +
                        "Description: " + reclamation.getDescription() + "\n" +
                        "Statut: " + reclamation.getStatut() + "\n";
                document.add(new Paragraph(reclamationDetails, reclamationFont));
                document.add(new Paragraph("\n"));
            }
            document.close();
            showAlert("Succès", "Le PDF des avis a été exporté avec succès!");
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'exportation du PDF.");
        }
    }

    // Méthode pour charger les mots interdits depuis la base de données
    private void loadMotsInterdits() {
        try (Connection conn = MySQLConnection.getInstance().getConnection()) {
            String sql = "SELECT mot FROM mots_interdits";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                ObservableList<String> motsInterditsList = FXCollections.observableArrayList();
                while (rs.next()) {
                    motsInterditsList.add(rs.getString("mot"));
                }
                motsInterditsListView.setItems(motsInterditsList); // Met à jour la ListView
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement des mots interdits.");
        }
    }

    // Méthode pour ajouter un mot interdit à la base de données
    @FXML
    private void handleAddMotInterdit() {
        String motInterdit = motInterditField.getText();
        if (motInterdit != null && !motInterdit.isEmpty()) {
            try (Connection conn = MySQLConnection.getInstance().getConnection()) {
                String sql = "INSERT INTO mots_interdits (mot) VALUES (?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, motInterdit);
                    stmt.executeUpdate();

                    // Recharger les mots interdits après l'ajout
                    loadMotsInterdits();
                    motInterditField.clear(); // Effacer le champ de texte
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors de l'ajout du mot interdit à la base de données.");
            }
        }
    }
}
