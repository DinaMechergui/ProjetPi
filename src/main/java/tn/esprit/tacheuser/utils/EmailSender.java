package tn.esprit.tacheuser.utils;  // Mets le bon package

import java.util.Properties;
import java.util.Random;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {

    private static final String SENDER_EMAIL = "zidiouayess0@gmail.com";  // Remplace par ton email
    private static final String SENDER_PASSWORD = "btck asqy etee oycp";    // Remplace par ton mot de passe

    public static boolean sendEmail(String recipient, String code) {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject("Code de vérification");
            message.setText("Votre code de vérification est : " + code);

            Transport.send(message);
            System.out.println("E-mail envoyé avec succès !");
            return true;  // ✅ Succès
        } catch (MessagingException e) {
            e.printStackTrace();
            return false; // ❌ Échec
        }
    }


    public static String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);  // Code 6 chiffres
        return String.valueOf(code);
    }
}
