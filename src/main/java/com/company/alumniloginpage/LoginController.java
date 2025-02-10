package com.company.alumniloginpage;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.IOException;
import java.util.Objects;

public class LoginController {

    private Stage stage;
    private Scene scene;

    @FXML
    private TextField userIdField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private void handleLogin() {
        String userId = userIdField.getText();
        String password = passwordField.getText();

        // Connect to MongoDB
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("alumni");
            MongoCollection<Document> collection = database.getCollection("info");

            // Check if the user ID exists
            Document user = collection.find(new Document("studentId", userId)).first();

            if (user == null) {
                // User ID not found
                showAlert("Error", "User ID not found.");
                clearFields();
            } else {
                // Verify password
                String storedPassword = user.getString("password");

                if (storedPassword.equals(password)) {
                    // Password matches, login successful
                    showAlert("Success", "Login successful!");
                    // Open the user profile or dashboard
                    switchToHome();
                    //openUserProfile(user);
                } else {
                    // Password does not match
                    showAlert("Error", "Password does not match.");
                    clearFields();
                }
            }
        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
            showAlert("Error", "An error occurred. Please try again.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        userIdField.clear();
        passwordField.clear();
    }

    public void switchToSignupForm(ActionEvent event) throws IOException {
        //Parent root = loadFXML(load.(getClass().getResource("SignUp.fxml")));
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("signupForm.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);

        stage.show();
    }

    public void switchToHome() throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("home.fxml"))));
        stage = (Stage) loginButton.getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

//    private void openUserProfile(Document user) {
//        // Load the user profile or dashboard
//        // Example: Load the user profile FXML and pass the user data
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("user_profile.fxml"));
//            Parent root = loader.load();
//
//            UserProfileController controller = loader.getController();
//            controller.setUserData(
//                    user.getString("name"),
//                    user.getString("email"),
//                    user.getString("phone"),
//                    user.getString("address"),
//                    user.getString("batch"),
//                    user.getString("degree"),
//                    user.getString("department")
//            );
//
//            Stage stage = new Stage();
//            stage.setScene(new Scene(root));
//            stage.setTitle("User Profile");
//            stage.show();
//        } catch (IOException e) {
//            System.err.println("Error loading user profile: " + e.getMessage());
//        }
//    }
}
