package com.company.alumniloginpage;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class BroadcastController implements Initializable {
    @FXML
    private ComboBox<String> broad_batch;
    @FXML
    private ComboBox<String> broad_dept;
    @FXML
    private TextField broadsubject;
    @FXML
    private TextArea broadtext;
    @FXML
    private TextField attachmentPath;

    private Stage stage;
    private Scene scene;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeComboBoxes();
    }

    private void initializeComboBoxes() {
        // Initialize department ComboBox
        broad_dept.setItems(FXCollections.observableArrayList(
                "CSE", "EECE", "CE", "ME", "AE", "EWCE", "PME",
                "NAME", "IPE", "BME", "ARCH", "NSE"
        ));

        // Initialize batch ComboBox
        List<String> batchYears = new ArrayList<>();
        for (int year = 2002; year <= 2023; year++) {
            batchYears.add(String.valueOf(year));
        }
        broad_batch.setItems(FXCollections.observableArrayList(batchYears));
    }

    @FXML
    private void chooseAttachment() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File for Attachment");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            attachmentPath.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private void BroadcastMail() {
        String year = broad_batch.getValue();
        String dept = broad_dept.getValue();
        String subject = broadsubject.getText();
        String message = broadtext.getText();
        String attachmentPath = this.attachmentPath.getText();

        // Validation
        if (year == null || dept == null || subject.isEmpty() || message.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill all required fields!");
            return;
        }

        // Fetch email addresses
        String[] emails = fetchUsersByYearAndBatch(year, dept);

        if (emails.length == 0) {
            showAlert(Alert.AlertType.WARNING, "Warning", "No recipients found for selected criteria!");
            return;
        }

        // Send broadcast email
        Integer result = Broadcast.sendEmail(emails, subject, message, attachmentPath);

        if (result == 1) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Broadcast sent successfully!");
            clearFields();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to send broadcast!");
        }
    }

    private String[] fetchUsersByYearAndBatch(String year, String batch) {
        List<String> emails = new ArrayList<>();

        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("alumni");
            MongoCollection<Document> collection = database.getCollection("info");

            Document query = new Document()
                    .append("graduationYear", year)
                    .append("department", batch);

            collection.find(query).forEach(doc -> {
                String email = doc.getString("email");
                if (email != null && !email.isEmpty()) {
                    emails.add(email);
                }
            });
        } catch (Exception e) {
            System.err.println("Error fetching emails: " + e.getMessage());
        }

        return emails.toArray(new String[0]);
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearFields() {
        broadsubject.clear();
        broadtext.clear();
        attachmentPath.clear();
        broad_batch.setValue(null);
        broad_dept.setValue(null);
    }

    // Navigation methods
    @FXML
    public void switchToHome(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("home.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void switchalumniCard(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("alumniCard.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void switchtoEvent(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("event.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void logOut(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("home.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Log Out");
        alert.setHeaderText("You're about to logout");
        alert.setContentText("Do you want to save before exiting?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            stage.close();
        }
    }
}