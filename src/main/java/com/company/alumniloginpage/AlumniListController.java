package com.company.alumniloginpage;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class AlumniListController {

    @FXML
    private ComboBox<String> yearComboBox;

    @FXML
    private ComboBox<String> batchComboBox;

    @FXML
    private TableView<Alumni> alumniTable;

    @FXML
    private void initialize() {
        // Populate the year and batch ComboBoxes
        yearComboBox.getItems().addAll( "2002", "2021", "2022", "2023");
        batchComboBox.getItems().addAll( "CSE", "EEE", "ME", "CE");

        // Set up the TableView columns
        alumniTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("name"));
        alumniTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("studentId"));
        alumniTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("batch"));
        alumniTable.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("graduationYear"));
        alumniTable.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("department"));
    }

    @FXML
    private void handleSearch() {
        String year = yearComboBox.getValue();
        String batch = batchComboBox.getValue();

        // Fetch alumni based on the selected year and batch
        List<Alumni> alumniList = fetchAlumni(year, batch);

        // Display the alumni list in the TableView
        ObservableList<Alumni> data = FXCollections.observableArrayList(alumniList);
        alumniTable.setItems(data);
    }

    private List<Alumni> fetchAlumni(String year, String batch) {
        List<Alumni> alumniList = new ArrayList<>();

        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("alumni");
            MongoCollection<Document> collection = database.getCollection("info");

            // Build the query based on the selected year and batch
            Document query = new Document();

            if (!"All".equals(year)) {
                query.append("graduationYear", year); // Filter by year if not "All"
            }

            if (!"All".equals(batch)) {
                query.append("department", batch); // Filter by batch if not "All"
            }

            // Query to find alumni with the specified year and/or batch
            Iterable<Document> alumniDocuments = collection.find(query);

            // Convert MongoDB documents to Alumni objects
            for (Document doc : alumniDocuments) {
                Alumni alumni = new Alumni(
                        doc.getString("name"),
                        doc.getString("studentId"),
                        doc.getString("batch"),
                        doc.getString("graduationYear"),
                        doc.getString("department")
                );
                alumniList.add(alumni);
            }
        } catch (Exception e) {
            System.err.println("Error fetching alumni: " + e.getMessage());
        }

        return alumniList;
    }

    // Alumni class to represent a single alumni record
    public static class Alumni {
        private final String name;
        private final String studentId;
        private final String batch;
        private final String graduationYear;
        private final String department;

        public Alumni(String name, String studentId, String batch, String graduationYear, String department) {
            this.name = name;
            this.studentId = studentId;
            this.batch = batch;
            this.graduationYear = graduationYear;
            this.department = department;
        }

        public String getName() {
            return name;
        }

        public String getStudentId() {
            return studentId;
        }

        public String getBatch() {
            return batch;
        }

        public String getGraduationYear() {
            return graduationYear;
        }

        public String getDepartment() {
            return department;
        }
    }
}
