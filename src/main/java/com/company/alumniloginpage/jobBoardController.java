package com.company.alumniloginpage;

import com.mongodb.client.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;

import org.bson.Document;
import com.mongodb.client.model.Filters;
import static com.mongodb.client.model.Filters.eq;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class jobBoardController implements Initializable
{

    Stage stage;
    Scene scene;

    // FXML UI Components
    @FXML private ImageView userImageView;
    @FXML private Label namelbl;
    @FXML private Button helpButton;

    // Job Table components
    @FXML private TableView<Job> jobTableView;
    @FXML private TableColumn<Job, String> titleColumn;
    @FXML private TableColumn<Job, String> companyColumn;
    @FXML private TableColumn<Job, String> locationColumn;
    @FXML private TableColumn<Job, String> salaryColumn;
    @FXML private TableColumn<Job, LocalDate> datePostedColumn;
    @FXML private TableColumn<Job, String> circularColumn;
    @FXML private TableColumn<Job, String> descriptionColumn;

    // Form components
    @FXML private TextField jobTitle;
    @FXML private TextField company;
    @FXML private TextField jobLocation;
    @FXML private TextField salary;
    @FXML private ComboBox<String> category;
    @FXML private DatePicker datePosted;
    @FXML private TextField circularUrl;
    @FXML private TextArea jobDescription;

    // Buttons
    @FXML private Button clearButton;
    @FXML private Button addJobButton;
    @FXML private Button removeJobButton;

    // Data model
    private ObservableList<Job> jobsList = FXCollections.observableArrayList();
    private int nextJobId = 1;
    private String loggedInUsername;

    // MongoDB connection
    private MongoCollection<Document> jobsCollection;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        String loggedInUserId = SharedData.getInstance().getLoggedInUserId();

        if (loggedInUserId != null)
        {
            try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017"))
            {
                MongoDatabase database = mongoClient.getDatabase("alumni");
                MongoCollection<Document> collection = database.getCollection("info");

                Document user = collection.find(new Document("studentId", loggedInUserId)).first();

                if (user != null)
                {
                    namelbl.setText(user.getString("name"));

                    String imagePath = user.getString("Image");
                    if (imagePath != null && !imagePath.isEmpty())
                    {
                        javafx.scene.image.Image image = new Image(new File(imagePath).toURI().toString());
                        userImageView.setImage(image);
                    }
                }
                else
                {
                    System.err.println("User not found in the database.");
                }
            }
            catch (Exception e)
            {
                System.err.println("Error loading user data: " + e.getMessage());
            }
        }
        else
        {
            System.err.println("No logged-in user ID found.");
        }



        // Initialize MongoDB connection
        initializeDatabase();

        // Initialize table columns
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        companyColumn.setCellValueFactory(new PropertyValueFactory<>("company"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));
        datePostedColumn.setCellValueFactory(new PropertyValueFactory<>("datePosted"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Setup the circular column with a "View" link
        circularColumn.setCellFactory(param -> new TableCell<>() {
            private final Hyperlink hyperlink = new Hyperlink("View");

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    Job job = getTableView().getItems().get(getIndex());
                    hyperlink.setOnAction(event -> openJobCircular(job.getCircularUrl()));
                    setGraphic(hyperlink);
                }
            }
        });

        // Populate category dropdown
        if(category!=null)category.getItems().addAll(
                "Engineering", "IT & Software", "Business", "Healthcare",
                "Education", "Marketing", "Finance", "Government", "Other"
        );

        // Set current date as default
        if(datePosted!=null)datePosted.setValue(LocalDate.now());

        // Load jobs from database
        loadJobsFromDatabase();

        // Set table data
        jobTableView.setItems(jobsList);

        // Add listener for job selection to populate form for editing
        jobTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateFormWithJob(newSelection);
            }
        });

        // Configure help button tooltip
        Tooltip helpTooltip = new Tooltip("Job Board Help:\n\n" +
                "• View job listings in the table above\n" +
                "• Post new job opportunities using the form below\n" +
                "• Click on job titles to view more details\n" +
                "• To remove a job, select it in the table and click 'Remove Job'\n" +
                "• Use the Clear Fields button to reset the form");
        helpTooltip.setShowDelay(javafx.util.Duration.millis(200));
        helpTooltip.setHideDelay(javafx.util.Duration.millis(500));
        helpTooltip.setShowDuration(javafx.util.Duration.seconds(20));
        Tooltip.install(helpButton, helpTooltip);
    }

    private void initializeDatabase() {
        try {
            // Get MongoDB connection
            MongoDBConnection connection = MongoDBConnection.getInstance();
            jobsCollection = connection.getDatabase().getCollection("jobs");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not connect to the database",
                    "Error: " + e.getMessage());
        }
    }

    private void loadJobsFromDatabase() {
        try {
            jobsList.clear();
            nextJobId = 1;

            // Get all jobs from MongoDB
            FindIterable<Document> jobs = jobsCollection.find();

            for (Document doc : jobs) {
                // Convert Document to Job object
                Job job = documentToJob(doc);
                jobsList.add(job);

                // Update nextJobId to be one more than the highest id
                if (job.getId() >= nextJobId) {
                    nextJobId = job.getId() + 1;
                }
            }

            // If database is empty, add sample jobs
            if (jobsList.isEmpty()) {
                //addSampleJobs();
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not load jobs from database",
                    "Error: " + e.getMessage());
            // Add sample jobs as fallback
            //addSampleJobs();
        }
    }

    private Job documentToJob(Document doc) {
        int id = doc.getInteger("id");
        String title = doc.getString("title");
        String companyName = doc.getString("company");
        String location = doc.getString("location");
        String jobCategory = doc.getString("category");
        String jobSalary = doc.getString("salary");

        // Convert Date to LocalDate
        Date date = doc.getDate("datePosted");
        LocalDate localDate = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        String url = doc.getString("circularUrl");
        String description = doc.getString("description");

        return new Job(id, title, companyName, location, jobCategory, jobSalary, localDate, url, description);
    }

    private Document jobToDocument(Job job) {
        return new Document("id", job.getId())
                .append("title", job.getTitle())
                .append("company", job.getCompany())
                .append("location", job.getLocation())
                .append("category", job.getCategory())
                .append("salary", job.getSalary())
                .append("datePosted", Date.from(job.getDatePosted().atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .append("circularUrl", job.getCircularUrl())
                .append("description", job.getDescription());
    }

//    private void addSampleJobs() {
//        jobsList.addAll(
//                new Job(nextJobId++, "Software Engineer", "TechCorp Ltd.", "Dhaka, Bangladesh",
//                        "IT & Software", "৳50,000 - ৳80,000", LocalDate.now().minusDays(5),
//                        "https://example.com/jobs/software-engineer",
//                        "We are looking for a skilled Software Engineer to join our development team."),
//
//                new Job(nextJobId++, "Mechanical Engineer", "MIST Engineering Solutions", "Mirpur, Dhaka",
//                        "Engineering", "৳45,000 - ৳65,000", LocalDate.now().minusDays(10),
//                        "https://example.com/jobs/mechanical-engineer",
//                        "Mechanical Engineer with 3+ years experience needed for our defense projects."),
//
//                new Job(nextJobId++, "Data Analyst", "Analytics BD", "Remote",
//                        "IT & Software", "৳40,000 - ৳60,000", LocalDate.now().minusDays(2),
//                        "https://example.com/jobs/data-analyst",
//                        "Seeking a data analyst with strong SQL and visualization skills.")
//        );
//
//        // Save sample jobs to database
//        for (Job job : jobsList) {
//            Document jobDoc = jobToDocument(job);
//            jobsCollection.insertOne(jobDoc);
//        }
//    }

    private void populateFormWithJob(Job job) {
        jobTitle.setText(job.getTitle());
        company.setText(job.getCompany());
        jobLocation.setText(job.getLocation());
        salary.setText(job.getSalary());
        category.setValue(job.getCategory());
        datePosted.setValue(job.getDatePosted());
        circularUrl.setText(job.getCircularUrl());
        jobDescription.setText(job.getDescription());
    }

    @FXML
    private void handleAddJob(ActionEvent event) {
        if (validateJobForm()) {
            // Check if editing existing job or adding new one
            Job selectedJob = jobTableView.getSelectionModel().getSelectedItem();

            if (selectedJob != null) {
                // Update existing job
                selectedJob.setTitle(jobTitle.getText());
                selectedJob.setCompany(company.getText());
                selectedJob.setLocation(jobLocation.getText());
                selectedJob.setSalary(salary.getText());
                selectedJob.setCategory(category.getValue());
                selectedJob.setDatePosted(datePosted.getValue());
                selectedJob.setCircularUrl(circularUrl.getText());
                selectedJob.setDescription(jobDescription.getText());

                // Update in database
                Document filter = new Document("id", selectedJob.getId());
                Document update = jobToDocument(selectedJob);
                jobsCollection.replaceOne(filter, update);

                // Refresh table
                jobTableView.refresh();

                showAlert(Alert.AlertType.INFORMATION, "Success", "Job Updated Successfully",
                        "The job has been updated in the system.");
            } else {
                // Add new job
                Job newJob = new Job(
                        nextJobId++,
                        jobTitle.getText(),
                        company.getText(),
                        jobLocation.getText(),
                        category.getValue(),
                        salary.getText(),
                        datePosted.getValue(),
                        circularUrl.getText(),
                        jobDescription.getText()
                );

                jobsList.add(newJob);

                // Add to database
                Document jobDoc = jobToDocument(newJob);
                jobsCollection.insertOne(jobDoc);

                showAlert(Alert.AlertType.INFORMATION, "Success", "Job Posted Successfully",
                        "The new job opportunity has been added to the system.");
            }

            // Clear the form after adding/updating
            clearForm();

            // Deselect row in table
            jobTableView.getSelectionModel().clearSelection();
        }
    }

    @FXML
    private void handleRemoveJob(ActionEvent event) {
        Job selectedJob = jobTableView.getSelectionModel().getSelectedItem();

        if (selectedJob != null) {
            // Confirm before removing
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Removal");
            confirmAlert.setHeaderText("Remove Job Posting");
            confirmAlert.setContentText("Are you sure you want to remove the selected job posting?");

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Remove from database
                jobsCollection.deleteOne(eq("id", selectedJob.getId()));

                // Remove from list
                jobsList.remove(selectedJob);
                clearForm();

                showAlert(Alert.AlertType.INFORMATION, "Success", "Job Removed",
                        "The job posting has been removed from the system.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "No Job Selected",
                    "Please select a job from the table to remove.");
        }
    }

    @FXML
    private void handleClearFields(ActionEvent event) {
        clearForm();
        jobTableView.getSelectionModel().clearSelection();
    }

    @FXML
    private void showHelp() {
        Alert helpAlert = new Alert(Alert.AlertType.INFORMATION);
        helpAlert.setTitle("Help");
        helpAlert.setHeaderText("Alumni Portal Help");

        // Change this content based on the current page
        String currentPage = "Job Opportunities"; // This could be dynamic based on current view

        switch(currentPage) {
            case "Job Opportunities":
                helpAlert.setContentText("Job Board Help:\n\n" +
                        "• View job listings in the table above\n" +
                        "• Post new job opportunities using the form below\n" +
                        "• Click on job titles to view more details\n" +
                        "• To remove a job, select it in the table and click 'Remove Job'\n" +
                        "• Use the Clear Fields button to reset the form");
                break;
            // Add cases for other pages
            default:
                helpAlert.setContentText("Welcome to the MIST Alumni Portal. Navigate using the sidebar menu.");
        }

        helpAlert.showAndWait();
    }

    private void clearForm() {
        jobTitle.clear();
        company.clear();
        jobLocation.clear();
        salary.clear();
        category.setValue(null);
        datePosted.setValue(LocalDate.now());
        circularUrl.clear();
        jobDescription.clear();
    }

    private boolean validateJobForm() {
        StringBuilder errorMessage = new StringBuilder();

        if (jobTitle.getText().trim().isEmpty()) {
            errorMessage.append("- Job title cannot be empty\n");
        }

        if (company.getText().trim().isEmpty()) {
            errorMessage.append("- Company name cannot be empty\n");
        }

        if (jobLocation.getText().trim().isEmpty()) {
            errorMessage.append("- Job location cannot be empty\n");
        }

        if (salary.getText().trim().isEmpty()) {
            errorMessage.append("- Salary cannot be empty\n");
        }

        if (category.getValue() == null) {
            errorMessage.append("- Please select a job category\n");
        }

        if (datePosted.getValue() == null) {
            errorMessage.append("- Please select a posting date\n");
        }

        if (circularUrl.getText().trim().isEmpty()) {
            errorMessage.append("- Circular URL cannot be empty\n");
        } else if (!isValidUrl(circularUrl.getText().trim())) {
            errorMessage.append("- Please enter a valid URL (e.g., https://example.com)\n");
        }

        if (jobDescription.getText().trim().isEmpty()) {
            errorMessage.append("- Job description cannot be empty\n");
        }

        if (errorMessage.length() > 0) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please correct the following errors:",
                    errorMessage.toString());
            return false;
        }

        return true;
    }

    private boolean isValidUrl(String url) {
        try {
            new URI(url);
            return url.startsWith("http://") || url.startsWith("https://");
        } catch (URISyntaxException e) {
            return false;
        }
    }

    private void openJobCircular(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open URL",
                    "There was an error opening the job circular URL: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Navigation methods
    @FXML
    public void switchToHome(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("home.fxml"))));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    @FXML
    private void logOut(ActionEvent event) {
        try {
            // Load the login scene
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Logout Error", "Could not log out",
                    "There was an error during logout: " + e.getMessage());
        }
    }

    // Method to pass username from login
    public void setUsername(String username) {
        this.loggedInUsername = username;
        namelbl.setText(username);

        // Here you could load user image if available
        // userImageView.setImage(new Image(getClass().getResourceAsStream("path/to/user/image.png")));
    }

    // Filter jobs by category
    public void filterJobsByCategory(String categoryFilter) {
        if (categoryFilter == null || categoryFilter.equals("All Categories")) {
            loadJobsFromDatabase(); // Reload all jobs
        } else {
            try {
                jobsList.clear();

                // Query MongoDB for filtered jobs
                FindIterable<Document> filteredJobs = jobsCollection.find(eq("category", categoryFilter));

                for (Document doc : filteredJobs) {
                    jobsList.add(documentToJob(doc));
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Error filtering jobs",
                        "Error: " + e.getMessage());

                // Fallback to in-memory filtering
                ObservableList<Job> filteredList = FXCollections.observableArrayList();
                for (Job job : jobsList) {
                    if (job.getCategory().equals(categoryFilter)) {
                        filteredList.add(job);
                    }
                }
                jobTableView.setItems(filteredList);
            }
        }
    }

    // Search jobs by keyword (search in title, company, and description)
    public void searchJobs(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            loadJobsFromDatabase(); // Reload all jobs
        } else {
            try {
                jobsList.clear();
                keyword = keyword.toLowerCase();

                // MongoDB query with OR condition for multiple fields
                Document query = new Document("$or", FXCollections.observableArrayList(
                        new Document("title", new Document("$regex", keyword).append("$options", "i")),
                        new Document("company", new Document("$regex", keyword).append("$options", "i")),
                        new Document("description", new Document("$regex", keyword).append("$options", "i"))
                ));

                FindIterable<Document> searchResults = jobsCollection.find(query);

                for (Document doc : searchResults) {
                    jobsList.add(documentToJob(doc));
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Error searching jobs",
                        "Error: " + e.getMessage());

                // Fallback to in-memory search
                keyword = keyword.toLowerCase();
                ObservableList<Job> searchResults = FXCollections.observableArrayList();
                for (Job job : jobsList) {
                    if (job.getTitle().toLowerCase().contains(keyword) ||
                            job.getCompany().toLowerCase().contains(keyword) ||
                            job.getDescription().toLowerCase().contains(keyword)) {
                        searchResults.add(job);
                    }
                }
                jobTableView.setItems(searchResults);
            }
        }
    }

    public void switchBroadcast(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("broadcast.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.show();
    }

    public void switchtoEvent(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("event.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.setFullScreen(true);
    }

    public void switchalumniCard(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("alumniCard.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);

        stage.setFullScreen(true);
    }

    public void switchalumniList(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("alumniList.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.setFullScreen(true);
    }

    public void mistWebsite(ActionEvent event) throws URISyntaxException,IOException{
        Desktop.getDesktop().browse(new URI("https://mist.ac.bd/"));
    }

    public void switchtoStudentIdCard(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("student_id_card.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    public void switchToJobBoardStudent(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("jobstudent.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    public void eventstudent(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("eventstudent.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    public void switchtoAlumniListStudent(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("alumniListStudent.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    public void switchToStudentHome(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("student_home.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.setFullScreen(true);
    }


}