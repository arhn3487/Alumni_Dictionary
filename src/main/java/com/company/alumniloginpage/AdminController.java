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
    private ComboBox<String> userTypeComboBox;

    @FXML
    private Button searchButton;

    @FXML
    private TableView<AlumniData> alumniListView;

    @FXML
    private TableColumn<AlumniData, String> idColumn;

    @FXML
    private TableColumn<AlumniData, String> typeColumn;

    @FXML
    private TableColumn<AlumniData, ImageView> pictureColumn;

    @FXML
    private TableColumn<AlumniData, String> emailColumn;

    @FXML
    private TableColumn<AlumniData, Void> actionColumn;

    @FXML
    private Label totalCountLabel;

    @FXML
    private Button viewDetailsButton;

    @FXML
    private Button refreshButton;

    private ObservableList<AlumniData> alumniDataList = FXCollections.observableArrayList();

    private MongoDBConnection dbConnection;
    private MongoCollection<Document> collection;

    // Default image path
    private final String DEFAULT_IMAGE_PATH = "/photos/profile_holder.png";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize database connection
        dbConnection = MongoDBConnection.getInstance();
        collection = dbConnection.getDatabase().getCollection("info");

        // Set up ComboBoxes
        setupComboBoxes();

        // Set up TableColumns
        setupTableColumns();

        // Load initial data
        loadAlumniData();
    }

    private void setupComboBoxes() {
        // Populate department ComboBox
        departmentComboBox.getItems().addAll(
                "All Departments", "Computer Science", "Electrical Engineering",
                "Mechanical Engineering", "Civil Engineering", "Aerospace Engineering"
        );
        departmentComboBox.setValue("All Departments");

        // Populate batch ComboBox
        batchComboBox.getItems().addAll(
                "All Batches", "2022", "2021", "2020", "2019", "2018", "2017", "2016"
        );
        batchComboBox.setValue("All Batches");

        // Populate userType ComboBox
        userTypeComboBox.getItems().addAll(
                "All Types", "Alumni", "Faculty", "Student"
        );
        userTypeComboBox.setValue("All Types");
    }

    private void setupTableColumns() {
        // Setup ID column
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Setup Type column
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        // Setup Picture column
        pictureColumn.setCellValueFactory(new PropertyValueFactory<>("pictureView"));

        // Setup Email column
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Setup Action column with Remove and Email buttons
        setupActionColumn();
    }

    private void setupActionColumn() {
        Callback<TableColumn<AlumniData, Void>, TableCell<AlumniData, Void>> cellFactory =
                new Callback<TableColumn<AlumniData, Void>, TableCell<AlumniData, Void>>() {
                    @Override
                    public TableCell<AlumniData, Void> call(final TableColumn<AlumniData, Void> param) {
                        return new TableCell<AlumniData, Void>() {
                            private final Button removeBtn = new Button("Remove");
                            private final Button emailBtn = new Button("Email");
                            private final HBox actionBox = new HBox(10, removeBtn, emailBtn);

                            {
                                // Set button styles
                                removeBtn.getStyleClass().add("warning-button");
                                emailBtn.getStyleClass().add("secondary-button");

                                // Set remove button action
                                removeBtn.setOnAction(event -> {
                                    AlumniData data = getTableView().getItems().get(getIndex());
                                    removeUser(data);
                                });

                                // Set email button action
                                emailBtn.setOnAction(event -> {
                                    AlumniData data = getTableView().getItems().get(getIndex());
                                    sendEmail(data);
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
            String id = doc.getObjectId("_id").toString();
            String type = doc.getString("type");
            String email = doc.getString("email");
            String picturePath = doc.getString("picturePath");

            // Create ImageView for the picture
            ImageView pictureView = createImageView(picturePath);

            AlumniData alumniData = new AlumniData(id, type, email, pictureView);
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
                    //image = new Image(50, 50, true, true); // Empty image
                }
            } catch (Exception e) {
                System.err.println("Failed to load default image. Error: " + e.getMessage());
                // Last resort - empty image
                //image = new Image(50, 50, true, true);
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
            // Remove user from the database
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

    private void sendEmail(AlumniData data) {
        // Email functionality implementation
        System.out.println("Sending email to: " + data.getEmail());
        // This would typically open an email composition window or form
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String department = departmentComboBox.getValue();
        String batch = batchComboBox.getValue();
        String userType = userTypeComboBox.getValue();

        alumniDataList.clear();

        // Build the query based on selected filters
        Document filter = new Document();

        if (!department.equals("All Departments")) {
            filter.append("department", department);
        }

        if (!batch.equals("All Batches")) {
            filter.append("batch", batch);
        }

        if (!userType.equals("All Types")) {
            filter.append("type", userType);
        }

        // Perform the search
        FindIterable<Document> documents = collection.find(filter);
        int count = 0;

        for (Document doc : documents) {
            String id = doc.getObjectId("_id").toString();
            String type = doc.getString("type");
            String email = doc.getString("email");
            String picturePath = doc.getString("picturePath");

            // Create ImageView for the picture using the improved method
            ImageView pictureView = createImageView(picturePath);

            AlumniData alumniData = new AlumniData(id, type, email, pictureView);
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
        // Implement switching to home page
    }

    @FXML
    private void switchalumniList() {
        // Already on alumni list page
    }

    @FXML
    private void switchBroadcast() {
        // Implement switching to broadcast page
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