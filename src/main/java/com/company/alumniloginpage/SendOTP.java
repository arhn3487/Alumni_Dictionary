package com.company.alumniloginpage;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.Random;

public class SendOTP {
    public static Integer sendEmail(String name,String to) {
        final String username = "arafatsakibisbat@gmail.com";  // Change to your email
        final String password = "mbbt kffo caun txoe";     // Use App Password (not direct password)

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
            Random rand = new Random();
            int otp = 100000 + rand.nextInt(900000);
            // Create a simple email message
            Message message = new MimeMessage(session);
            message.setFrom(new  InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to)); // Change recipient
            message.setSubject("Test Email");
            //message.setText("This is a simple email without an attachment.");
            message.setContent("<h4>Dear " +name+"</br>"
                    +"<h3>Thank you for signing up for the <b>MIST Alumni Portal</b>.</h3>"
                    + "<p>To complete your registration, please use the verification code below:</p>"
                    + "<h2 style='color:blue;'>" + otp + "</h2>"
                    + "<p>Please do not share this code with anyone.</p>"
                    + "<p>If you did not request this verification, please ignore this email or contact our support team at <b>" + username + "</b>.</p>"
                    + "<br><p>Best regards,<br>MIST Alumni Team<br>mist.ac.bd<br>01540194651</p>", "text/html");

            // Send email

            // Send email
            Transport.send(message);
            System.out.println("Email sent successfully!");
            return otp;

        } catch (MessagingException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
