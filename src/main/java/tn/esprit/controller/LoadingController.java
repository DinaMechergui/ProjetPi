package tn.esprit.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.animation.RotateTransition;
import javafx.util.Duration;
import javafx.scene.shape.Circle;
import java.util.List;
import java.util.Random;
import javafx.scene.control.Alert;

public class LoadingController {

    @FXML
    private VBox wheelBox;  // Contient la roue
    @FXML
    private Label resultLabel;  // Affiche le résultat après la rotation
    @FXML
    private Circle wheelCircle;  // Représente la roue (cercle)

    private List<String> cadeauNames;

    public void setCadeauNames(List<String> cadeauNames) {
        this.cadeauNames = cadeauNames;
        displayCadeauNames();
        startWheel();
    }

    private void displayCadeauNames() {
        for (String name : cadeauNames) {
            System.out.println("Cadeau disponible : " + name);
        }
    }

    private void startWheel() {
        // Démarrer l'animation de la roue avec le cercle
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(3), wheelCircle);
        rotateTransition.setByAngle(360 * 5);  // Faire tourner la roue 5 fois
        rotateTransition.setCycleCount(1);
        rotateTransition.setAutoReverse(false);
        rotateTransition.play();

        // Lorsque la rotation est terminée, afficher un cadeau aléatoire
        rotateTransition.setOnFinished(event -> showRandomCadeau());
    }

    private void showRandomCadeau() {
        Random random = new Random();
        String selectedCadeau = cadeauNames.get(random.nextInt(cadeauNames.size()));

        resultLabel.setText("Félicitations ! Vous avez gagné : " + selectedCadeau);
        afficherAlerte("🎁 Cadeau sélectionné", "Félicitations ! Vous avez gagné : " + selectedCadeau);
    }

    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
