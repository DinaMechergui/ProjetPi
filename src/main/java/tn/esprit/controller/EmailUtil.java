package tn.esprit.controller;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailUtil {

    public static void sendEmailWithAttachment(String toEmail, String subject, String body, String filePath) {
        // Informations d'identification pour l'envoi d'e-mails
        final String fromEmail = "skanderbensalah10@gmail.com"; // Remplacez par votre adresse e-mail
        final String password = "yxrn iduf apxa gxoh "; // Remplacez par votre mot de passe

        // Configuration des propriétés pour le serveur SMTP
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); // Serveur SMTP de Gmail
        props.put("mail.smtp.port", "587"); // Port SMTP
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // Activation de TLS

        // Création d'une session avec authentification
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            // Création du message
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);

            // Corps du message
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(body);

            // Pièce jointe (QR Code)
            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.attachFile(filePath);

            // Combinaison du texte et de la pièce jointe
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(attachmentPart);

            // Ajout du contenu au message
            message.setContent(multipart);

            // Envoi de l'e-mail
            Transport.send(message);
            System.out.println("E-mail envoyé avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'envoi de l'e-mail : " + e.getMessage());
        }
    }
}