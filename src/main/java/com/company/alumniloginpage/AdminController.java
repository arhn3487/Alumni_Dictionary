package com.company.alumniloginpage;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminController implements Initializable {
    @FXML
    private ComboBox<String> departmentComboBox;

    @FXML
    private ComboBox<String> batchComboBox;

    @FXML
    private Button searchButton;

    @FXML
    private TableView<AlumniData> alumniListView;

    // Updated column declarations to match FXML
    @FXML
    private TableColumn<AlumniData, String> studentIdColumn;

    @FXML
    private TableColumn<AlumniData, String> nameColumn;

    @FXML
    private TableColumn<AlumniData, String> batchColumn;

    @FXML
    private TableColumn<AlumniData, String> departmentColumn;

    @FXML
    private TableColumn<AlumniData, String> degreeColumn;

    @FXML
    private TableColumn<AlumniData, String> graduationYearColumn;

    @FXML
    private TableColumn<AlumniData, String> workplaceColumn;

    @FXML
    private TableColumn<AlumniData, String> emailColumn;

    @FXML
    private TableColumn<AlumniData, String> phoneColumn;

    @FXML
    private TableColumn<AlumniData, String> addressColumn;

    @FXML
    private TableColumn<AlumniData, ImageView> imageColumn;

    @FXML
    private TableColumn<AlumniData, Void> actionColumn;

    @FXML
    private Label totalCountLabel;

    @FXML
    private Label contactNumberLabel;

    @FXML
    private ImageView profileImageView;

    @FXML
    private ImageView bannerImageView;

    private ObservableList<AlumniData> alumniDataList = FXCollections.observableArrayList();

    private MongoDBConnection dbConnection;
    private MongoCollection<Document> collection;

    // Default image path
    private final String DEFAULT_IMAGE_PATH = "/photos/profile_holder.png";
    private final String BANNER_IMAGE_PATH = "/photos/campus_banner.jpg";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize database connection
        dbConnection = MongoDBConnection.getInstance();
        collection = dbConnection.getDatabase().getCollection("info");

        // Set up ComboBoxes
        setupComboBoxes();

        // Set up TableColumns
        setupTableColumns();

        // Set up contact information
        contactNumberLabel.setText("Contact: 01540194651");

        // Load banner image if available
        try {
            InputStream bannerIs = getClass().getResourceAsStream(BANNER_IMAGE_PATH);
            if (bannerIs != null) {
                bannerImageView.setImage(new Image(bannerIs));
            }
        } catch (Exception e) {
            System.err.println("Failed to load banner image: " + e.getMessage());
        }

        // Load initial data
        loadAlumniData();
    }

    private void setupComboBoxes() {
        // Populate department ComboBox
        departmentComboBox.getItems().addAll(
                "All Department", "CSE", "EECE", "CE", "ME", "AE", "EWCE", "PME", "NAME", "IPE", "BME", "ARCH", "NSE"
        );
        departmentComboBox.setValue("Department List");

        // Populate batch ComboBox
        batchComboBox.getItems().addAll(
                "All Batch", "2002", "2003", "2004", "2005", "2006", "2007", "2008", "2009", "2010",
                "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020",
                "2021", "2022", "2023"
        );
        batchComboBox.setValue("All Batches");
    }

    private void setupTableColumns() {
        // Setup all columns
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        batchColumn.setCellValueFactory(new PropertyValueFactory<>("batch"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
        degreeColumn.setCellValueFactory(new PropertyValueFactory<>("degree"));
        graduationYearColumn.setCellValueFactory(new PropertyValueFactory<>("graduationYear"));
        workplaceColumn.setCellValueFactory(new PropertyValueFactory<>("workplace"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("pictureView"));

        // Setup Action column with Remove button only
        setupActionColumn();
    }

    private void setupActionColumn() {
        Callback<TableColumn<AlumniData, Void>, TableCell<AlumniData, Void>> cellFactory =
                new Callback<TableColumn<AlumniData, Void>, TableCell<AlumniData, Void>>() {
                    @Override
                    public TableCell<AlumniData, Void> call(final TableColumn<AlumniData, Void> param) {
                        return new TableCell<AlumniData, Void>() {
                            private final Button removeBtn = new Button("Remove");
                            private final HBox actionBox = new HBox(10, removeBtn);

                            {
                                // Set button styles
                                removeBtn.getStyleClass().add("warning-button");

                                // Set remove button action
                                removeBtn.setOnAction(event -> {
                                    AlumniData data = getTableView().getItems().get(getIndex());
                                    removeUser(data);
                                });
                            }

                            @Override
                            protected void updateItem(Void item, boolean empty) {
                                super.updateItem(item, empty);

                                if (empty) {
                                    setGraphic(null);
                                } else {
                                    setGraphic(actionBox);
                                }
                            }
                        };
                    }
                };

        actionColumn.setCellFactory(cellFactory);
    }

    private void loadAlumniData() {
        alumniDataList.clear();

        FindIterable<Document> documents = collection.find();
        int count = 0;

        for (Document doc : documents) {
            // Extract all fields from the document
            String id = doc.getObjectId("_id").toString();
            String name = doc.getString("name");
            String batch = doc.getString("batch");
            String department = doc.getString("department");
            String degree = doc.getString("degree");
            String graduationYear = doc.getString("graduationYear");
            String workplace = doc.getString("workplace");
            String email = doc.getString("email");
            String phone = doc.getString("phone");
            String address = doc.getString("address");
            String picturePath = doc.getString("Image");
            String userType = doc.getString("type");

            // Create ImageView for the picture
            ImageView pictureView = createImageView(picturePath);

            AlumniData alumniData = new AlumniData(
                    id, name, batch, department, degree, graduationYear,
                    workplace, email, phone, address, pictureView, userType
            );

            alumniDataList.add(alumniData);
            count++;
        }

        alumniListView.setItems(alumniDataList);
        totalCountLabel.setText("Total Records: " + count);
    }

    private ImageView createImageView(String picturePath) {
        ImageView pictureView = new ImageView();
        Image image = null;

        // First try to load as resource
        if (picturePath != null && !picturePath.isEmpty()) {
            try {
                InputStream is = getClass().getResourceAsStream(picturePath);
                if (is != null) {
                    image = new Image(is);
                } else {
                    // Try to load as file if resource not found
                    File file = new File(picturePath);
                    if (file.exists()) {
                        image = new Image(file.toURI().toString());
                    }
                }
            } catch (Exception e) {
                System.err.println("Failed to load image from path: " + picturePath + ". Error: " + e.getMessage());
            }
        }

        // If image is still null, use default
        if (image == null) {
            try {
                InputStream defaultIs = getClass().getResourceAsStream(DEFAULT_IMAGE_PATH);
                if (defaultIs != null) {
                    image = new Image(defaultIs);
                } else {
                    // Create a placeholder image if all else fails
                    image = new Image(new ByteArrayInputStream(new byte[0]), 50, 50, true, true);
                }
            } catch (Exception e) {
                System.err.println("Failed to load default image. Error: " + e.getMessage());
                // Last resort - empty image using a data URI for a 1x1 transparent pixel
                image = new Image("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII=", 50, 50, true, true);
            }
        }

        pictureView.setImage(image);
        pictureView.setFitHeight(50);
        pictureView.setFitWidth(50);
        pictureView.setPreserveRatio(true);

        return pictureView;
    }

    private void removeUser(AlumniData data) {
        try {
            // Remove user from the database using _id
            collection.deleteOne(Filters.eq("_id", new ObjectId(data.getId())));

            // Remove from the table
            alumniDataList.remove(data);

            // Update total count
            int count = Integer.parseInt(totalCountLabel.getText().replace("Total Records: ", ""));
            totalCountLabel.setText("Total Records: " + (count - 1));

            // No alert box as requested
        } catch (Exception e) {
            System.err.println("Error removing user: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String department = departmentComboBox.getValue();
        String batch = batchComboBox.getValue();

        alumniDataList.clear();

        // Build the query based on selected filters
        Document filter = new Document();

        if (department != null && !department.equals("All") && !department.equals("Department List")) {
            filter.append("department", department);
        }

        if (batch != null && !batch.equals("All") && !batch.equals("All Batches")) {
            filter.append("batch", batch);
        }

        // Perform the search
        FindIterable<Document> documents = collection.find(filter);
        int count = 0;

        for (Document doc : documents) {
            // Extract all fields from the document
            String id = doc.getObjectId("_id").toString();
            String name = doc.getString("name");
            String batchVal = doc.getString("batch");
            String departmentVal = doc.getString("department");
            String degree = doc.getString("degree");
            String graduationYear = doc.getString("graduationYear");
            String workplace = doc.getString("workplace");
            String email = doc.getString("email");
            String phone = doc.getString("phone");
            String address = doc.getString("address");
            String picturePath = doc.getString("Image");
            String userTypeVal = doc.getString("type");

            // Create ImageView for the picture
            ImageView pictureView = createImageView(picturePath);

            AlumniData alumniData = new AlumniData(
                    id, name, batchVal, departmentVal, degree, graduationYear,
                    workplace, email, phone, address, pictureView, userTypeVal
            );

            alumniDataList.add(alumniData);
            count++;
        }

        alumniListView.setItems(alumniDataList);
        totalCountLabel.setText("Total Records: " + count);
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadAlumniData();
    }

    @FXML
    private void handleViewDetails(ActionEvent event) {
        AlumniData selectedData = alumniListView.getSelectionModel().getSelectedItem();
        if (selectedData != null) {
            // Implement view details functionality
            System.out.println("Viewing details for: " + selectedData.getId());
        }
    }

    @FXML
    private void switchToHome() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("admin-dashboard.fxml"));
            Stage stage = (Stage) totalCountLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void switchalumniList() {
        // Already on alumni list page
        System.out.println("Already on alumni list page");
    }

    @FXML
    private void switchBroadcast() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("admin-broadcast.fxml"));
            Stage stage = (Stage) totalCountLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void switchToEvents() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("admin-events.fxml"));
            Stage stage = (Stage) totalCountLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void switchToJobs() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("admin-jobs.fxml"));
            Stage stage = (Stage) totalCountLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void switchToWebsite() {
        try {
            // If you have an external browser opening capability, use that
            // This is a placeholder for website navigation
            System.out.println("Opening MIST website");

            // Example to open in browser if possible
            java.awt.Desktop.getDesktop().browse(new java.net.URI("https://mist.ac.bd"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void logOut() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            Stage stage = (Stage) totalCountLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}