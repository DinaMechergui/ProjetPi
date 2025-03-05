package tn.esprit.tacheuser.contoller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.tacheuser.models.User;
import tn.esprit.tacheuser.utils.MySQLConnection;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javafx.scene.image.PixelReader;

public class Profile {

    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField ageField;
    @FXML
    private TextField genderField;
    @FXML
    private ImageView profileImageView;  // ImageView pour afficher la photo de profil

    private static User currentUser;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    @FXML
    private void initialize() {
        if (currentUser != null) {
            nameField.setText(currentUser.getNom() + " " + currentUser.getPrenom());
            emailField.setText(currentUser.getMail());
            phoneField.setText(currentUser.getTel());
            ageField.setText(currentUser.getAge());
            genderField.setText(currentUser.getGender());

            // Charger l'image depuis la base de données
            loadImageFromDatabase(currentUser.getId());
        }
    }

    private void loadImageFromDatabase(int userId) {
        String query = "SELECT profile_image FROM User WHERE id = ?";

        try (Connection connection = MySQLConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    byte[] imageBytes = resultSet.getBytes("profile_image");
                    if (imageBytes != null) {
                        // Convertir les bytes en Image JavaFX et l'afficher dans le ImageView
                        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
                        Image image = new Image(byteArrayInputStream);
                        profileImageView.setImage(image);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL lors du chargement de l'image depuis la base de données : " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/tacheuser/User/login.fxml"));
            Scene loginScene = new Scene(loader.load());
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(loginScene);
            stage.show();
        } catch (IOException e) {
            System.out.println("❌ Erreur lors de la déconnexion : " + e.getMessage());
        }
    }

    @FXML
    private void handleGoToReclamation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/tacheuser/User/ajoutreclamtion.fxml"));
            Scene reclamationScene = new Scene(loader.load());
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(reclamationScene);
            stage.show();
        } catch (IOException e) {
            System.out.println("❌ Erreur lors de la navigation vers la réclamation : " + e.getMessage());
        }
    }

    // Gérer le téléchargement de la photo
    @FXML
    private void handleUploadPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try (FileInputStream fileInputStream = new FileInputStream(selectedFile)) {
                Image image = new Image(fileInputStream);
                profileImageView.setImage(image);
            } catch (IOException e) {
                System.out.println("❌ Erreur lors du chargement de l'image : " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleSavePhoto() {
        Image image = profileImageView.getImage();
        if (image != null) {
            try {
                BufferedImage bufferedImage = convertToBufferedImage(image);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                javax.imageio.ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();

                saveProfileImageToDatabase(currentUser.getId(), imageBytes);
                System.out.println("✅ Photo sauvegardée dans la base de données !");
            } catch (IOException | SQLException e) {
                System.out.println("❌ Erreur lors de la sauvegarde de l'image : " + e.getMessage());
            }
        } else {
            System.out.println("Aucune image à sauvegarder !");
        }
    }

    private BufferedImage convertToBufferedImage(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        PixelReader pixelReader = image.getPixelReader();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                javafx.scene.paint.Color color = pixelReader.getColor(x, y);
                int argb = (new java.awt.Color((float) color.getRed(), (float) color.getGreen(), (float) color.getBlue(), (float) color.getOpacity())).getRGB();
                bufferedImage.setRGB(x, y, argb);
            }
        }

        return bufferedImage;
    }

    private void saveProfileImageToDatabase(int userId, byte[] imageBytes) throws SQLException {
        String query = "UPDATE User SET profile_image = ? WHERE id = ?";

        try (Connection connection = MySQLConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setBytes(1, imageBytes);
            statement.setInt(2, userId);
            statement.executeUpdate();
        }
    }
}
