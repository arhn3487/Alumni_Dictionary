package com.company.alumniloginpage;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import org.bson.Document;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class AlumniCardController {

    @FXML
    private AnchorPane alumniCardPane; // The main AnchorPane for the alumni card

    @FXML
    private TextField nameField;

    @FXML
    private TextField studentIdField;

    @FXML
    private TextField departmentField;

    @FXML
    private TextField sessionField;

    @FXML
    private ImageView userImageView; // ImageView to display the user's image

    private String loggedInUserId; // Store the logged-in user's ID

    @FXML
    public void initialize() {
        // Get the logged-in user ID from the shared data model
        String loggedInUserId = SharedData.getInstance().getLoggedInUserId();

        if (loggedInUserId != null) {
            loadUserData(loggedInUserId);
        } else {
            System.err.println("No logged-in user ID found.");
        }
    }

    // Method to set the logged-in user's ID
//    public void setLoggedInUserId(String userId) {
//        System.out.println("Logged in user ID: " + userId);
//        this.loggedInUserId = userId;
//        System.out.println("Logged in user ID: " + userId);
//        //loadUserData(); // Load user data when the ID is set
//    }

    // Method to load user data from MongoDB
    private void loadUserData(String userId) {
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("alumni");
            MongoCollection<Document> collection = database.getCollection("info");

            // Fetch the user's data using the logged-in user ID
            System.out.println("Ekhane Logged in user ID: " + userId);
            Document user = collection.find(new Document("studentId", userId)).first();

            if (user != null) {
                // Populate the fields with the fetched data
                nameField.setText(user.getString("name"));
                System.out.println("Ear ekhane Logged in user ID: " + nameField.getText());
                studentIdField.setText(user.getString("studentId"));
                departmentField.setText(user.getString("department"));
                sessionField.setText(user.getString("batch"));

                // Load and display the user's image
                String imagePath = user.getString("Image");
                if (imagePath != null && !imagePath.isEmpty()) {
                    Image image = new Image(new File(imagePath).toURI().toString());
                    userImageView.setImage(image);
                }
            } else {
                System.err.println("User not found in the database.");
            }
        } catch (Exception e) {
            System.err.println("Error loading user data: " + e.getMessage());
        }
    }

    // Method to handle the download button click
    @FXML
    private void handleDownload() {
        // Create a snapshot of the alumni card pane
        WritableImage writableImage = alumniCardPane.snapshot(new SnapshotParameters(), null);

        // Convert WritableImage to BufferedImage
        int width = (int) writableImage.getWidth();
        int height = (int) writableImage.getHeight();
        java.awt.image.BufferedImage bufferedImage = new java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bufferedImage.setRGB(x, y, writableImage.getPixelReader().getArgb(x, y));
            }
        }

        // Open a file chooser to save the image
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Alumni Card");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", "*.png"));
        File file = fileChooser.showSaveDialog(alumniCardPane.getScene().getWindow());

        if (file != null) {
            try {
                // Save the BufferedImage to the selected file
                ImageIO.write(bufferedImage, "png", file);
                System.out.println("Alumni card saved successfully!");
            } catch (IOException e) {
                System.err.println("Error saving alumni card: " + e.getMessage());
            }
        }
    }
}