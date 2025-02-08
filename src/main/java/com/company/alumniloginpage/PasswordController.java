package com.company.alumniloginpage;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.bson.Document;

public class PasswordController {

    @FXML
    private TextField passwordField;

    @FXML
    private TextField confirmPasswordField;

    private Document userData; // To store the user data from the previous form

    // Method to set the user data
    public void setUserData(Document userData) {
        this.userData = userData;
    }

    @FXML
    private void handleSubmitPassword() {
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (password.equals(confirmPassword)) {
            // Add the password to the user data
            userData.append("password", password);

            // Insert the data into MongoDB
            MongoDBConnection.insertinfo(userData);

            // Show success message
            showAlert("Success", "Account created successfully!");
        } else {
            // Show error message if passwords do not match
            showAlert("Error", "Passwords do not match. Please try again.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
