package com.company.alumniloginpage;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import org.bson.Document;

import java.time.LocalDate;

public class JobPostsController {

    @FXML
    private TextField jobIdField;

    @FXML
    private TextField titleField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private TextField companyField;

    @FXML
    private TextField locationField;

    @FXML
    private TextField postedDateField;

    @FXML
    private void handleAddJob() {
        String jobId = jobIdField.getText();
        String title = titleField.getText();
        String description = descriptionField.getText();
        String company = companyField.getText();
        String location = locationField.getText();
        String postedDate = postedDateField.getText();

        if (jobId.isEmpty() || title.isEmpty() || description.isEmpty() || company.isEmpty() || location.isEmpty() || postedDate.isEmpty()) {
            showAlert("Error", "Please fill all fields.");
            return;
        }

        // Create a new job document
        Document job = new Document("jobId", jobId)
                .append("title", title)
                .append("description", description)
                .append("company", company)
                .append("location", location)
                .append("postedDate", postedDate);

        // Insert the job into the database
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("alumni");
            MongoCollection<Document> collection = database.getCollection("jobs");

            collection.insertOne(job);
            showAlert("Success", "Job added successfully!");
        } catch (Exception e) {
            System.err.println("Error adding job: " + e.getMessage());
            showAlert("Error", "Failed to add job.");
        }
    }

    @FXML
    private void handleDeleteJob() {
        String jobId = jobIdField.getText();

        if (jobId.isEmpty()) {
            showAlert("Error", "Please enter a Job ID.");
            return;
        }

        // Delete the job from the database
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("alumni");
            MongoCollection<Document> collection = database.getCollection("jobs");

            DeleteResult result = collection.deleteOne(new Document("jobId", jobId));

            if (result.getDeletedCount() > 0) {
                showAlert("Success", "Job deleted successfully!");
            } else {
                showAlert("Error", "Job not found.");
            }
        } catch (Exception e) {
            System.err.println("Error deleting job: " + e.getMessage());
            showAlert("Error", "Failed to delete job.");
        }
    }

    @FXML
    private void handleModifyJob() {
        String jobId = jobIdField.getText();
        String title = titleField.getText();
        String description = descriptionField.getText();
        String company = companyField.getText();
        String location = locationField.getText();
        String postedDate = postedDateField.getText();

        if (jobId.isEmpty()) {
            showAlert("Error", "Please enter a Job ID.");
            return;
        }

        // Create an update document
        Document update = new Document();
        if (!title.isEmpty()) update.append("title", title);
        if (!description.isEmpty()) update.append("description", description);
        if (!company.isEmpty()) update.append("company", company);
        if (!location.isEmpty()) update.append("location", location);
        if (!postedDate.isEmpty()) update.append("postedDate", postedDate);

        if (update.isEmpty()) {
            showAlert("Error", "No fields to update.");
            return;
        }

        // Update the job in the database
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("alumni");
            MongoCollection<Document> collection = database.getCollection("jobs");

            UpdateResult result = collection.updateOne(
                    new Document("jobId", jobId),
                    new Document("$set", update)
            );

            if (result.getModifiedCount() > 0) {
                showAlert("Success", "Job updated successfully!");
            } else {
                showAlert("Error", "Job not found.");
            }
        } catch (Exception e) {
            System.err.println("Error updating job: " + e.getMessage());
            showAlert("Error", "Failed to update job.");
        }
    }

    // Method to show alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}