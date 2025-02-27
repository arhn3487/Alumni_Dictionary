package com.company.alumniloginpage;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.Random;

public class SendOTP
{
    public String generateOTP()
    {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Generates a 6-digit number
        return String.valueOf(otp);
    }

    public void sendOTP(String email, String otp)
    {
        // Sender's email credentials
        String from = "arafatsakibisbat@gmail.com";
        String password = "icde xfka vrxx jyxc";

        // Setup mail server properties
         Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Create a session with the email server
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try
        {
            // Create a MimeMessage object
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            message.setSubject("OTP Verification");
            //message.setText("Your OTP is: " + otp);
            message.setContent("<h3>Thank you for signing up for the <b>MIST Alumni Portal</b>.</h3>"
                    + "<p>To complete your registration, please use the verification code below:</p>"
                    + "<h2 style='color:blue;'>" + otp + "</h2>"
                    + "<p>Please do not share this code with anyone.</p>"
                    + "<p>If you did not request this verification, please ignore this email or contact our support team at <b>arafatsakibisbat@gmail.com</b>.</p>"
                    + "<br><p>Best regards,<br>MIST Alumni Team<br>mist.ac.bd<br>01540194651</p>", "text/html");

            // Send the email
            Transport.send(message);
            System.out.println("OTP sent successfully to " + email);
        }
        catch (MessagingException e)
        {
            System.err.println("Error sending OTP: " + e.getMessage());
        }
    }
}
