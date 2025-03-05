package Wedding.controllers;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class PaymentController {

    @FXML
    private TextField cardNumberField;

    @FXML
    private TextField cvcField;

    @FXML
    private TextField expMonthField;

    @FXML
    private TextField expYearField;

    @FXML
    private Button paybutton;

    private double totalAmount; // Pour stocker le montant total

    public void initialize() {
        Stripe.apiKey = "sk_test_51QwrETHVQtB3o7gAN5KpeXn8Nk4evLMz07sC0h0BUfb2db27xheYwbnrPt2OiRajWG3lhn1b8qj0i3FBOa38HXhW00A9QFJUr0";
    }

    @FXML
    private void processPayment() {
        // Vérifier si les champs sont vides
        if (cardNumberField.getText().isEmpty() || expMonthField.getText().isEmpty() ||
                expYearField.getText().isEmpty() || cvcField.getText().isEmpty()) {
            showAlert("Avertissement", "Tous les champs sont obligatoires. Veuillez entrer des détails de carte valides.");
            return;
        }

        // Récupérer les informations de la carte
        String cardNumber = cardNumberField.getText().replaceAll("\\s+", "");
        int expMonth;
        int expYear;
        String cvc = cvcField.getText();

        try {
            expMonth = Integer.parseInt(expMonthField.getText());
            expYear = Integer.parseInt(expYearField.getText());
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le mois et l'année d'expiration doivent être des nombres valides.");
            return;
        }

        // Valider les informations de la carte
        if (!isCardValid(cardNumber, expMonth, expYear, cvc)) {
            showAlert("Erreur", "Les informations de la carte sont invalides.");
            return;
        }

        // Créer les paramètres de la charge
        Map<String, Object> params = new HashMap<>();
        params.put("amount", (int) (totalAmount * 100)); // Convertir en cents
        params.put("currency", "usd");
        params.put("source", "tok_visa"); // Utiliser un token de test Stripe
        params.put("description", "Paiement pour la commande");

        try {
            // Effectuer le paiement
            Charge charge = Charge.create(params);
            showAlert("Paiement réussi", "Paiement réussi! ID de la charge : " + charge.getId());

            // Fermer la fenêtre de paiement après un paiement réussi
            Stage stage = (Stage) paybutton.getScene().getWindow();
            stage.close();

        } catch (StripeException e) {
            showAlert("Erreur", "Une erreur est survenue : " + e.getMessage());
        }
    }

    // Méthode pour valider les informations de la carte
    private boolean isCardValid(String cardNumber, int expMonth, int expYear, String cvc) {
        // Vérifier que le numéro de carte est valide (algorithme de Luhn)
        if (!cardNumber.matches("\\d{13,19}")) {
            showAlert("Erreur", "Le numéro de carte est invalide.");
            return false;
        }

        // Vérifier que la date d'expiration est dans le futur
        if (expYear < 2023 || (expYear == 2023 && expMonth < 10)) {
            showAlert("Erreur", "La date d'expiration est invalide.");
            return false;
        }

        // Vérifier que le CVC est valide
        if (!cvc.matches("\\d{3,4}")) {
            showAlert("Erreur", "Le CVC est invalide.");
            return false;
        }

        return true;
    }

    // Méthode utilitaire pour afficher des alertes
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR); // Utiliser AlertType.ERROR pour les erreurs
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void initData(double totalAmount) {
        this.totalAmount = totalAmount;
        System.out.println("Montant total à payer : " + totalAmount);
    }
}