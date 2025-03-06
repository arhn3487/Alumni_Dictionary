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
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.bson.Document;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class LoginController
{
    private Stage stage;
    private Scene scene;

    @FXML
    private TextField userIdField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private ComboBox<String> userType;

    @FXML
    private void handleLogin()
    {
        String userId = userIdField.getText();
        String password = passwordField.getText();
        String usertype = userType.getValue();

        // Connect to MongoDB
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017"))
        {
            MongoDatabase database = mongoClient.getDatabase("alumni");
            MongoCollection<Document> collection = database.getCollection("info");

            Document user = collection.find(new Document("studentId", userId)).first();

            if (user == null)
            {
                showAlert("Error", "User ID not found.");
                //clearFields();
            }
            else
            {
                String storedPassword = user.getString("password");
                String storedType = user.getString("usertype");

                // Hash the input password and compare with stored hash
                String hashedInputPassword = hashPassword(password);

                if (hashedInputPassword != null && storedPassword.equals(hashedInputPassword) && storedType.equals(usertype))
                {
                    SharedData.getInstance().setLoggedInUserId(userId);
                    //showAlert("Success", "Login successful!");

                    System.out.println("Logged in user ID: " + userId);

                    if(usertype.equals("Admin")) switchToAdminHome();
                    else if(usertype.equals("Student")) switchToStudenTHome();
                    else switchToHome();
                }
                else
                {
                    showAlert("Error", "Invalid credentials. Please try again.");
                    clearFields();
                }
            }
        }
        catch (Exception e)
        {
            System.err.println("Error during login: " + e.getMessage());
            showAlert("Error", "An error occurred. Please try again.");
        }
    }

    private String hashPassword(String password)
    {
        try
        {
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

        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private void showAlert(String title, String message)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields()
    {
        passwordField.clear();
    }

    public void switchToSignupForm(ActionEvent event) throws IOException
    {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("creatAccountFormAdmin.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    public void switchToForgotPassword(ActionEvent event) throws IOException
    {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("forgotPassword.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    public void switchToHome() throws IOException
    {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("home.fxml"))));
        stage = (Stage) loginButton.getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void switchToAdminHome()
    {
        try
        {
            Parent root = FXMLLoader.load(getClass().getResource("Admin_dashboard.fxml"));
            stage = (Stage) loginButton.getScene().getWindow(); // Use loginButton as reference
            stage.setScene(new Scene(root));
            stage.setFullScreen(true);
            stage.setFullScreenExitHint("");
            stage.show();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void switchToStudenTHome()
    {
        try
        {
            Parent root = FXMLLoader.load(getClass().getResource("student_home.fxml"));
            //Parent root = FXMLLoader.load(getClass().getResource("scene1.fxml"));
            stage = (Stage) loginButton.getScene().getWindow(); // Use loginButton as reference
            stage.setScene(new Scene(root));
            stage.show();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}