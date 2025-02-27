package com.company.alumniloginpage;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.bson.Document;

public class JobListingsController {

    @FXML
    private ListView<String> jobListView;

    @FXML
    public void initialize() {
        loadJobListings();
    }

    private void loadJobListings() {
        ObservableList<String> jobList = FXCollections.observableArrayList();

        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("alumni");
            MongoCollection<Document> collection = database.getCollection("jobs");

            FindIterable<Document> jobs = collection.find();

            for (Document job : jobs) {
                String jobDetails = "Job ID: " + job.getString("jobId") + "\n" +
                        "Title: " + job.getString("title") + "\n" +
                        "Company: " + job.getString("company") + "\n" +
                        "Location: " + job.getString("location") + "\n" +
                        "Posted Date: " + job.getString("postedDate") + "\n" +
                        "Description: " + job.getString("description") + "\n------------------";
                jobList.add(jobDetails);
            }
        } catch (Exception e) {
            System.err.println("Error fetching jobs: " + e.getMessage());
        }

        jobListView.setItems(jobList);
    }
}
