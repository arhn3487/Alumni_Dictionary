package com.company.alumniloginpage;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class Broadcast {
    public static Integer sendEmail(String[] addresses, String subject,String text) {
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
            // Create a simple email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));

            // Convert array of addresses to InternetAddress array
            InternetAddress[] recipientAddresses = new InternetAddress[addresses.length];
            for (int i = 0; i < addresses.length; i++) {
                recipientAddresses[i] = new InternetAddress(addresses[i]);
            }
            message.setRecipients(Message.RecipientType.TO, recipientAddresses); // Set recipients

            message.setSubject(subject);
            message.setText(text);  // Set the plain text message

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
