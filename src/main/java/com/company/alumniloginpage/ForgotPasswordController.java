package com.company.alumniloginpage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class ForgotPasswordController {

    @FXML
    private VBox emailPane, otpPane, resetPasswordPane, successPane;

    @FXML
    private TextField emailField, otpField;

    @FXML
    private PasswordField newPasswordField, confirmPasswordField;

    @FXML
    private Label emailErrorLabel, otpErrorLabel, passwordErrorLabel;

    @FXML
    private Button sendOTPButton, verifyOTPButton, resetPasswordButton, resendButton;

    private String generatedOTP;
    private String userEmail;
    private MongoCollection<Document> collection;
    private final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public void initialize() {
        // Initialize MongoDB connection
        MongoDBConnection connection = MongoDBConnection.getInstance();
        MongoDatabase database = connection.getDatabase();
        collection = database.getCollection("info");

        // Add listener to clear error labels when user types
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (emailErrorLabel.isVisible()) {
                emailErrorLabel.setVisible(false);
            }
        });

        otpField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (otpErrorLabel.isVisible()) {
                otpErrorLabel.setVisible(false);
            }
        });

        newPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (passwordErrorLabel.isVisible()) {
                passwordErrorLabel.setVisible(false);
            }
        });

        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (passwordErrorLabel.isVisible()) {
                passwordErrorLabel.setVisible(false);
            }
        });
    }

    @FXML
    private void handleSendOTP(ActionEvent event) {
        userEmail = emailField.getText().trim();

        // Validate email format
        if (!isValidEmail(userEmail)) {
            emailErrorLabel.setText("Please enter a valid email address");
            emailErrorLabel.setVisible(true);
            return;
        }

        // Check if email exists in database
        Document user = collection.find(Filters.eq("email", userEmail)).first();
        if (user == null) {
            emailErrorLabel.setText("Email not found in our records");
            emailErrorLabel.setVisible(true);
            return;
        }

        // Generate and send OTP
        SendOTP otpSender = new SendOTP();
        generatedOTP = otpSender.generateOTP();

        // Disable button and show loading state
        sendOTPButton.setDisable(true);
        sendOTPButton.setText("Sending...");

        // Send OTP in background thread
        Thread sendOTPThread = new Thread(() -> {
            try {
                otpSender.sendOTP(userEmail, generatedOTP);

                // Update UI on JavaFX thread
                javafx.application.Platform.runLater(() -> {
                    emailPane.setVisible(false);
                    otpPane.setVisible(true);
                    sendOTPButton.setDisable(false);
                    sendOTPButton.setText("Send Verification Code");
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    emailErrorLabel.setText("Failed to send OTP. Please try again.");
                    emailErrorLabel.setVisible(true);
                    sendOTPButton.setDisable(false);
                    sendOTPButton.setText("Send Verification Code");
                });
            }
        });

        sendOTPThread.setDaemon(true);
        sendOTPThread.start();
    }

    @FXML
    private void handleVerifyOTP(ActionEvent event) {
        String enteredOTP = otpField.getText().trim();

        // Validate OTP
        if (enteredOTP.isEmpty()) {
            otpErrorLabel.setText("Please enter the verification code");
            otpErrorLabel.setVisible(true);
            return;
        }

        if (!enteredOTP.equals(generatedOTP)) {
            otpErrorLabel.setText("Invalid verification code");
            otpErrorLabel.setVisible(true);
            return;
        }

        // OTP is valid, proceed to reset password
        otpPane.setVisible(false);
        resetPasswordPane.setVisible(true);
    }

    @FXML
    private void handleResetPassword(ActionEvent event) {
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validate passwords
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            passwordErrorLabel.setText("Please fill in both password fields");
            passwordErrorLabel.setVisible(true);
            return;
        }

        if (newPassword.length() < 6) {
            passwordErrorLabel.setText("Password must be at least 6 characters");
            passwordErrorLabel.setVisible(true);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            passwordErrorLabel.setText("Passwords do not match");
            passwordErrorLabel.setVisible(true);
            return;
        }

        // Hash the new password using SHA-256
        String hashedPassword = hashPassword(newPassword);

        // Update the password in the database
        try {
            collection.updateOne(
                    Filters.eq("email", userEmail),
                    Updates.set("password", hashedPassword)
            );

            // Show success screen
            resetPasswordPane.setVisible(false);
            successPane.setVisible(true);

        } catch (Exception e) {
            passwordErrorLabel.setText("Failed to update password. Please try again.");
            passwordErrorLabel.setVisible(true);
        }
    }

    @FXML
    private void handleResendOTP(ActionEvent event) {
        // Disable resend button temporarily
        resendButton.setDisable(true);

        // Generate and send new OTP
        SendOTP otpSender = new SendOTP();
        generatedOTP = otpSender.generateOTP();

        Thread sendOTPThread = new Thread(() -> {
            try {
                otpSender.sendOTP(userEmail, generatedOTP);

                // Set a timer to re-enable the button after 30 seconds
                ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                scheduler.schedule(() -> {
                    javafx.application.Platform.runLater(() -> {
                        resendButton.setDisable(false);
                    });
                }, 30, TimeUnit.SECONDS);

            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    otpErrorLabel.setText("Failed to resend OTP. Please try again.");
                    otpErrorLabel.setVisible(true);
                    resendButton.setDisable(false);
                });
            }
        });

        sendOTPThread.setDaemon(true);
        sendOTPThread.start();
    }

    @FXML
    private void handleBackToEmail(ActionEvent event) {
        otpPane.setVisible(false);
        emailPane.setVisible(true);
    }

    @FXML
    private void handleBackToOTP(ActionEvent event) {
        resetPasswordPane.setVisible(false);
        otpPane.setVisible(true);
    }

    @FXML
    private void switchToLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("login2.fxml"));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && !email.isEmpty() && EMAIL_PATTERN.matcher(email).matches();
    }

    private String hashPassword(String password) {
        try {
            // Add the salt "arafat" to the password before hashing
            String saltedPassword = password + "arafat";

            // Create SHA-256 hash
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));

            // Convert byte array to hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}