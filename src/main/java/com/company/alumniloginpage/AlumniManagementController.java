package com.company.alumniloginpage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

public class AlumniManagementController implements Initializable
{

    // FXML components for the Alumni Table
    @FXML private TableView<AlumniModel> alumniTableView;
    @FXML private TableColumn<AlumniModel, ImageView> photoColumn;
    @FXML private TableColumn<AlumniModel, String> nameColumn;
    @FXML private TableColumn<AlumniModel, String> studentIdColumn;
    @FXML private TableColumn<AlumniModel, String> batchColumn;
    @FXML private TableColumn<AlumniModel, String> departmentColumn;
    @FXML private TableColumn<AlumniModel, String> emailColumn;
    @FXML private TableColumn<AlumniModel, String> phoneColumn;
    @FXML private TableColumn<AlumniModel, Button> actionsColumn;

    // FXML components for search/filter
    @FXML private ComboBox<String> userTypeComboBox;
    @FXML private ComboBox<String> departmentComboBox;
    @FXML private ComboBox<String> batchComboBox;
    @FXML private Button searchButton;

    // FXML components for adding a new user
    @FXML private TextField nameField;
    @FXML private TextField studentIdField;
    @FXML private TextField batchField;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private ComboBox<String> degreeComboBox;
    @FXML private ComboBox<String> addDepartmentComboBox;
    @FXML private TextField graduationYearField;
    @FXML private TextField workplaceField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField linkedinField;
    @FXML private TextField facebookField;
    @FXML private TextField addressField;
    @FXML private TextField passwordField;
    @FXML private TextField photoPathField;
    @FXML private Button browsePhotoButton;
    @FXML private Button clearButton;
    @FXML private Button addUserButton;

    // MongoDB Collection
    private MongoCollection<Document> alumniCollection;
    private ObservableList<AlumniModel> alumniList = FXCollections.observableArrayList();
    private File selectedPhotoFile;
    private static final String PHOTO_DIRECTORY = "src/main/resources/com/company/alumniloginpage/photos/profiles/";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize MongoDB connection
        MongoDBConnection dbConnection = MongoDBConnection.getInstance();
        alumniCollection = dbConnection.getDatabase().getCollection("info");

        // Initialize ComboBoxes
        initializeComboBoxes();

        // Initialize TableView
        initializeTableView();

        // Load data from MongoDB
        loadAlumniData();
    }

    private void initializeComboBoxes() {
        // Check if userTypeComboBox is not null
        if (userTypeComboBox != null) {
            userTypeComboBox.setItems(FXCollections.observableArrayList("All Users", "Alumni", "Student", "Admin"));
            userTypeComboBox.getSelectionModel().selectFirst();
        }

        // Check if departmentComboBox is not null
        if (departmentComboBox != null) {
            List<String> departments = List.of("All", "CSE", "EEE", "ME", "CE", "IPE", "AE", "NAME", "BME", "EWCE", "NSE");
            departmentComboBox.setItems(FXCollections.observableArrayList(departments));
            departmentComboBox.getSelectionModel().selectFirst();
        }

        // Check if addDepartmentComboBox is not null
        if (addDepartmentComboBox != null) {
            addDepartmentComboBox.setItems(FXCollections.observableArrayList(
                    "CSE", "EEE", "ME", "CE", "IPE", "AE", "NAME", "BME", "EWCE", "NSE"));
        }

        // Check if batchComboBox is not null
        if (batchComboBox != null) {
            List<String> batches = new ArrayList<>();
            batches.add("All Batches");
            for (int i = 2002; i <= 2005; i++) {
                batches.add(String.format("%02d", i));
            }
            batchComboBox.setItems(FXCollections.observableArrayList(batches));
            batchComboBox.getSelectionModel().selectFirst();
        }

        // Check if genderComboBox is not null
        if (genderComboBox != null) {
            genderComboBox.setItems(FXCollections.observableArrayList("Male", "Female", "Other"));
        }

        // Check if degreeComboBox is not null
        if (degreeComboBox != null) {
            degreeComboBox.setItems(FXCollections.observableArrayList("BSc", "MSc", "PhD"));
        }
    }

    private void initializeTableView() {
        // Configure table columns with proper cell factories
        if(photoColumn!=null) photoColumn.setCellValueFactory(new PropertyValueFactory<>("photoView"));
        if(photoColumn!=null) photoColumn.setCellFactory(column -> {
            return new TableCell<AlumniModel, ImageView>() {
                @Override
                protected void updateItem(ImageView item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        setGraphic(item);
                    }
                }
            };
        });

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        batchColumn.setCellValueFactory(new PropertyValueFactory<>("batch"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

        actionsColumn.setCellValueFactory(new PropertyValueFactory<>("actionsButton"));
        actionsColumn.setCellFactory(column -> {
            return new TableCell<AlumniModel, Button>() {
                @Override
                protected void updateItem(Button item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        setGraphic(item);
                    }
                }
            };
        });

        // Set items to the table
        alumniTableView.setItems(alumniList);
    }

    private void loadAlumniData() {
        try {
            // Clear existing data
            alumniList.clear();
            System.out.println("Loading alumni data...");

            // Get all documents from MongoDB
            FindIterable<Document> documents = alumniCollection.find();
            try (MongoCursor<Document> cursor = documents.iterator()) {
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    System.out.println("Processing document: " + doc.toJson());

                    AlumniModel alumni = createAlumniModelFromDocument(doc);
                    alumniList.add(alumni);
                }
            }

            System.out.println("Loaded " + alumniList.size() + " alumni records");

            // Refresh the table
            alumniTableView.refresh();

        } catch (Exception e) {
            System.err.println("Error loading alumni data: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load alumni data: " + e.getMessage());
        }
    }

    private AlumniModel createAlumniModelFromDocument(Document doc) {
        // Get MongoDB _id (important for delete operations)
        ObjectId mongoId = doc.getObjectId("_id");

        // Get other fields from document
        String name = doc.getString("name");
        String studentId = doc.getString("studentId");
        String batch = doc.getString("batch");
        String department = doc.getString("department");
        String email = doc.getString("email");
        String phone = doc.getString("phone");
        String photoPath = doc.getString("photoPath");

        // Create the alumni model
        AlumniModel alumni = new AlumniModel(name, studentId, batch, department, email, phone);
        alumni.setMongoId(mongoId); // Store MongoDB _id for future operations

        // Set photo if available
        if (photoPath != null && !photoPath.isEmpty()) {
            try {
                Image image = new Image(new FileInputStream(photoPath));
                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(50);
                imageView.setFitWidth(50);
                imageView.setPreserveRatio(true);
                alumni.setPhotoView(imageView);
                alumni.setPhotoPath(photoPath);
            } catch (FileNotFoundException e) {
                System.err.println("Photo not found: " + photoPath);
                // Set a default image
                setDefaultPhoto(alumni);
            }
        } else {
            // Set a default image
            setDefaultPhoto(alumni);
        }

        // Create action buttons
        Button viewEditButton = new Button("View/Edit");
        viewEditButton.setOnAction(event -> handleViewEditAlumni(alumni));

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(event -> handleDeleteAlumni(alumni));

        // Use a container for buttons (HBox or similar in real implementation)
        // For simplicity, just using one button in this example
        alumni.setActionsButton(viewEditButton);

        return alumni;
    }

    private void setDefaultPhoto(AlumniModel alumni) {
        try {
            Image defaultImage = new Image(new FileInputStream("src/main/resources/com/company/alumniloginpage/photos/profile_holder.png"));
            ImageView defaultImageView = new ImageView(defaultImage);
            defaultImageView.setFitHeight(50);
            defaultImageView.setFitWidth(50);
            defaultImageView.setPreserveRatio(true);
            alumni.setPhotoView(defaultImageView);
        } catch (FileNotFoundException e) {
            System.err.println("Default profile photo not found");
        }
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String userType = userTypeComboBox.getValue();
        String department = departmentComboBox.getValue();
        String batch = batchComboBox.getValue();

        // Clear current list
        alumniList.clear();

        // Build filter query
        Document filter = new Document();

        if (userType != null && !userType.equals("All Users")) {
            filter.append("userType", userType);
        }

        if (department != null && !department.equals("All")) {
            filter.append("department", department);
        }

        if (batch != null && !batch.equals("All Batches")) {
            filter.append("batch", batch);
        }

        // Execute query
        FindIterable<Document> documents;
        if (filter.isEmpty()) {
            documents = alumniCollection.find();
        } else {
            documents = alumniCollection.find(filter);
        }

        // Process results
        try (MongoCursor<Document> cursor = documents.iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                AlumniModel alumni = createAlumniModelFromDocument(doc);
                alumniList.add(alumni);
            }
        }
    }

    @FXML
    private void handleBrowsePhoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Photo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        selectedPhotoFile = fileChooser.showOpenDialog(browsePhotoButton.getScene().getWindow());

        if (selectedPhotoFile != null) {
            photoPathField.setText(selectedPhotoFile.getAbsolutePath());
        }
    }

    @FXML
    private void handleClearFields(ActionEvent event) {
        nameField.clear();
        studentIdField.clear();
        batchField.clear();
        genderComboBox.getSelectionModel().clearSelection();
        degreeComboBox.getSelectionModel().clearSelection();
        addDepartmentComboBox.getSelectionModel().clearSelection();
        graduationYearField.clear();
        workplaceField.clear();
        emailField.clear();
        phoneField.clear();
        linkedinField.clear();
        facebookField.clear();
        addressField.clear();
        passwordField.clear();
        photoPathField.clear();
        selectedPhotoFile = null;
    }

    @FXML
    private void handleAddUser(ActionEvent event) {
        // Validate required fields
        if (!validateRequiredFields()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all required fields (marked with *)");
            return;
        }

        // Create a new document for MongoDB
        Document alumniDoc = new Document();
        alumniDoc.append("name", nameField.getText().trim())
                .append("studentId", studentIdField.getText().trim())
                .append("batch", batchField.getText().trim())
                .append("gender", genderComboBox.getValue())
                .append("degree", degreeComboBox.getValue())
                .append("department", addDepartmentComboBox.getValue())
                .append("graduationYear", graduationYearField.getText().trim())
                .append("email", emailField.getText().trim())
                .append("phone", phoneField.getText().trim())
                .append("password", passwordField.getText().trim())
                .append("userType", "Alumni");  // Default to Alumni for this form

        // Add optional fields if present
        if (workplaceField.getText() != null && !workplaceField.getText().isEmpty()) {
            alumniDoc.append("workplace", workplaceField.getText().trim());
        }

        if (linkedinField.getText() != null && !linkedinField.getText().isEmpty()) {
            alumniDoc.append("linkedin", linkedinField.getText().trim());
        }

        if (facebookField.getText() != null && !facebookField.getText().isEmpty()) {
            alumniDoc.append("facebook", facebookField.getText().trim());
        }

        if (addressField.getText() != null && !addressField.getText().isEmpty()) {
            alumniDoc.append("address", addressField.getText().trim());
        }

        // Handle photo upload if selected
        if (selectedPhotoFile != null) {
            try {
                // Create directory if it doesn't exist
                Path directoryPath = Paths.get(PHOTO_DIRECTORY);
                if (!Files.exists(directoryPath)) {
                    Files.createDirectories(directoryPath);
                }

                // Generate unique filename
                String uniqueFileName = UUID.randomUUID().toString() + "_" + selectedPhotoFile.getName();
                Path targetPath = Paths.get(PHOTO_DIRECTORY + uniqueFileName);

                // Copy file to target directory
                Files.copy(selectedPhotoFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                // Store path in document
                alumniDoc.append("photoPath", targetPath.toString());

            } catch (IOException e) {
                System.err.println("Error copying photo file: " + e.getMessage());
                showAlert(Alert.AlertType.ERROR, "File Error", "Could not save the profile photo.");
                return;
            }
        }

        // Insert into MongoDB
        try {
            MongoDBConnection.insertinfo(alumniDoc);
            showAlert(Alert.AlertType.INFORMATION, "Success", "User added successfully!");

            // Clear fields
            handleClearFields(null);

            // Refresh alumni list
            loadAlumniData();

        } catch (Exception e) {
            System.err.println("Error inserting document: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not add user to the database.");
        }
    }

    private boolean validateRequiredFields() {
        return nameField.getText() != null && !nameField.getText().isEmpty() &&
                studentIdField.getText() != null && !studentIdField.getText().isEmpty() &&
                batchField.getText() != null && !batchField.getText().isEmpty() &&
                genderComboBox.getValue() != null &&
                degreeComboBox.getValue() != null &&
                addDepartmentComboBox.getValue() != null &&
                graduationYearField.getText() != null && !graduationYearField.getText().isEmpty() &&
                emailField.getText() != null && !emailField.getText().isEmpty() &&
                phoneField.getText() != null && !phoneField.getText().isEmpty() &&
                passwordField.getText() != null && !passwordField.getText().isEmpty();
    }

    private void handleViewEditAlumni(AlumniModel alumni) {
        // This would typically open a new dialog or screen to view/edit the alumni details
        showAlert(Alert.AlertType.INFORMATION, "View/Edit Alumni",
                "This would open a detail view for: " + alumni.getName() +
                        " (ID: " + alumni.getStudentId() + ")");

        // In a real implementation, you would load a new FXML or dialog here
    }

    // New method to handle alumni deletion
    private void handleDeleteAlumni(AlumniModel alumni) {
        // Confirm deletion with the user
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Alumni Record");
        confirmAlert.setContentText("Are you sure you want to delete " + alumni.getName() + " (" + alumni.getStudentId() + ")?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Delete from database using MongoDB _id
                    alumniCollection.deleteOne(Filters.eq("_id", alumni.getMongoId()));

                    // Remove from the observable list
                    alumniList.remove(alumni);

                    // Delete photo file if it exists
                    if (alumni.getPhotoPath() != null && !alumni.getPhotoPath().isEmpty()) {
                        try {
                            File photoFile = new File(alumni.getPhotoPath());
                            if (photoFile.exists() && !photoFile.isDirectory()) {
                                photoFile.delete();
                            }
                        } catch (Exception e) {
                            System.err.println("Error deleting photo file: " + e.getMessage());
                        }
                    }

                    showAlert(Alert.AlertType.INFORMATION, "Success", "Alumni record deleted successfully!");
                } catch (Exception e) {
                    System.err.println("Error deleting document: " + e.getMessage());
                    showAlert(Alert.AlertType.ERROR, "Database Error", "Could not delete the alumni record.");
                }
            }
        });
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Navigation Methods
    @FXML
    private void switchToHome(ActionEvent event) {
        navigateTo("dashboard.fxml", event);
    }

    @FXML
    private void switchalumniCard(ActionEvent event) {
        navigateTo("alumni-card.fxml", event);
    }

    @FXML
    private void switchalumniList(ActionEvent event) {
        // Already on this page, do nothing or refresh data
        loadAlumniData();
    }

    @FXML
    private void switchBroadcast(ActionEvent event) {
        navigateTo("broadcast.fxml", event);
    }

    @FXML
    private void switchtoEvent(ActionEvent event) {
        navigateTo("event.fxml", event);
    }

    @FXML
    private void logOut(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load login page.");
        }
    }

    private void navigateTo(String fxmlFile, ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load " + fxmlFile);
        }
    }

    public void mistWebsite(ActionEvent event) throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI("https://mist.ac.bd/"));
    }
}