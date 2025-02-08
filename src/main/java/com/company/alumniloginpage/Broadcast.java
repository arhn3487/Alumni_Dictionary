package com.company.alumniloginpage;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.util.Properties;
import javax.activation.*;

public class Broadcast {
    public static Integer sendEmail(String[] addresses, String subject, String text, String attachmentPath) {
        final String username = "arafatsakibisbat@gmail.com";  // Change to your email
        final String password = "mbbt kffo caun txoe";         // Use App Password

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Create session with authentication
        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            // Create email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));

            // Convert array of addresses to InternetAddress array
            InternetAddress[] recipientAddresses = new InternetAddress[addresses.length];
            for (int i = 0; i < addresses.length; i++) {
                recipientAddresses[i] = new InternetAddress(addresses[i]);
            }
            message.setRecipients(Message.RecipientType.TO, recipientAddresses); // Set recipients
            message.setSubject(subject);

            // Create email body (text part)
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(text);

            // Create multipart message
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            // Add attachment if provided
            if (attachmentPath != null && !attachmentPath.isEmpty()) {
                File file = new File(attachmentPath);
                if (file.exists()) {
                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    DataSource source = new FileDataSource(file);
                    attachmentPart.setDataHandler(new DataHandler(source));
                    attachmentPart.setFileName(file.getName()); // Set file name
                    multipart.addBodyPart(attachmentPart);
                } else {
                    System.out.println("Attachment file not found: " + attachmentPath);
                }
            }

            // Set the complete message content
            message.setContent(multipart);

            // Send email
            Transport.send(message);
            System.out.println("Email sent successfully to " + addresses.length + " recipients!");
            return 1;

        } catch (MessagingException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
