package com.company.alumniloginpage;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.IOException;
import java.util.Objects;
import javafx.event.ActionEvent;

public class PasswordController {

    private Stage stage;
    private Scene scene;
    
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
    private void handleSubmitPassword(ActionEvent event) throws IOException {
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (password.equals(confirmPassword)) {
            // Add the password to the user data
            userData.append("password", password);

            // Insert the data into MongoDB
            MongoDBConnection.insertinfo(userData);

            // Show success message
            showAlert("Success", "Account created successfully!");

            Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("login2.fxml"))));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
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
