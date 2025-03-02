package com.company.alumniloginpage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Node;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Filters;
import com.mongodb.client.AggregateIterable;
import org.bson.Document;

public class DashboardController implements Initializable {

    @FXML
    private PieChart userDistributionChart;

    @FXML
    private BarChart<String, Number> departmentChart;

    @FXML
    private CategoryAxis departmentAxis;

    @FXML
    private NumberAxis percentageAxis;

    @FXML
    private ImageView userImageView;

    @FXML
    private Label namelbl;

    @FXML
    private TextArea emailContent;

    @FXML
    private Button sendEmailButton;

    @FXML
    Stage stage;
     @FXML Scene scene;

    // MongoDB connection
    private MongoDBConnection mongoConnection;
    private String currentUserId; // To store logged-in user ID
    private String userEmail; // To store the user's email address

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize MongoDB connection
        mongoConnection = MongoDBConnection.getInstance();

        // For testing, set a user ID (in production would come from login)
        // This should be replaced with the actual logged-in user's ID
        currentUserId = "user123"; // Example ID

        // Set the stage to maximize (will be done when shown)
        javafx.application.Platform.runLater(this::maximizeStage);

        // Load user data first
        loadUserData();

        // Initialize the charts with real data from MongoDB with delays
        // Use a sequential loading approach with delays
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {
                    // Start by showing empty charts
                    initializeEmptyCharts();
                }),
                new KeyFrame(Duration.seconds(0.5), e -> {
                    // Load the first chart after 0.5 seconds
                    loadUserDistributionData();
                }),
                new KeyFrame(Duration.seconds(1.5), e -> {
                    // Load the third chart after 1.5 seconds
                    loadDepartmentData();
                })
        );
        timeline.play();
    }

    private void maximizeStage() {
        // Find the stage from any control
        if (namelbl != null && namelbl.getScene() != null && namelbl.getScene().getWindow() != null) {
            Stage stage = (Stage) namelbl.getScene().getWindow();
            stage.setMaximized(true);
        }
    }

    private void initializeEmptyCharts() {
        // Initialize empty charts that will be populated with data later
        userDistributionChart.setData(FXCollections.observableArrayList());
        departmentChart.setData(FXCollections.observableArrayList());

        // Remove any titles
        userDistributionChart.setTitle("");
        departmentChart.setTitle("");
    }

    private void loadUserData() {
        try {
            // Get user information from database based on logged-in user
            MongoCollection<Document> userCollection = mongoConnection.getDatabase().getCollection("info");
            Document userDoc = userCollection.find(Filters.eq("_id", currentUserId)).first();

            // If user data found, update UI
            if (userDoc != null) {
                // Set username label
                String fullName = userDoc.getString("name"); // Changed from firstName/lastName to match your schema
                if (fullName == null) {
                    // Try alternate fields if name is not available
                    fullName = userDoc.getString("firstName") + " " + userDoc.getString("lastName");
                }

                final String displayName = (fullName != null && !fullName.trim().isEmpty()) ? fullName : "User";

                // Get user's email if available
                userEmail = userDoc.getString("email");
                if (userEmail == null || userEmail.isEmpty()) {
                    userEmail = ""; // Default to empty if not found
                }

                // Update UI on JavaFX thread
                javafx.application.Platform.runLater(() -> {
                    namelbl.setText(displayName);
                });

                // Load profile image from the document
                if (userDoc.containsKey("Image")) {
                    String profileImagePath = userDoc.getString("Image");
                    if (profileImagePath != null && !profileImagePath.isEmpty()) {
                        // Load image on JavaFX thread
                        javafx.application.Platform.runLater(() -> {
                            try {
                                userImageView.setImage(new Image(profileImagePath));
                            } catch (Exception e) {
                                System.err.println("Error loading profile image: " + e.getMessage());
                                // Could set a default image here
                            }
                        });
                    }
                }
            } else {
                // Fallback to default if user not found
                javafx.application.Platform.runLater(() -> {
                    namelbl.setText("User");
                });
                System.out.println("User data not found for ID: " + currentUserId);
            }
        } catch (Exception e) {
            System.err.println("Error loading user data: " + e.getMessage());
            e.printStackTrace();

            // Fallback to default name in case of error
            javafx.application.Platform.runLater(() -> {
                namelbl.setText("User");
            });
        }
    }

    private void loadUserDistributionData() {
        try {
            // Get MongoDB collection
            MongoCollection<Document> userCollection = mongoConnection.getDatabase().getCollection("info");

            // Aggregate users by userType
            AggregateIterable<Document> userTypeResults = userCollection.aggregate(
                    Arrays.asList(
                            Aggregates.group("$usertype", Accumulators.sum("count", 1))
                    )
            );

            // Process the results
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

            for (Document doc : userTypeResults) {
                String userType = doc.getString("_id");
                int count = doc.getInteger("count");

                // Handle null userType (if any)
                if (userType == null) {
                    userType = "Unknown";
                }

                pieChartData.add(new PieChart.Data(userType, count));
            }

            // If no data was found, add placeholder data
            if (pieChartData.isEmpty()) {
                System.out.println("No user distribution data found, using placeholder data");
                pieChartData.add(new PieChart.Data("Students", 50));
                pieChartData.add(new PieChart.Data("Alumni", 35));
                pieChartData.add(new PieChart.Data("Admin", 15));
            }

            // Update the chart with animated data loading
            animateChartDataLoading(userDistributionChart, pieChartData);

        } catch (Exception e) {
            System.err.println("Error loading user distribution data: " + e.getMessage());
            e.printStackTrace();

            // Use placeholder data in case of error
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                    new PieChart.Data("Students", 50),
                    new PieChart.Data("Alumni", 35),
                    new PieChart.Data("Admin", 15)
            );
            animateChartDataLoading(userDistributionChart, pieChartData);
        }
    }

    private void animateChartDataLoading(PieChart chart, ObservableList<PieChart.Data> finalData) {
        // Execute on JavaFX thread
        javafx.application.Platform.runLater(() -> {
            // Create a timeline for adding each data piece with delay
            Timeline timeline = new Timeline();

            for (int i = 0; i < finalData.size(); i++) {
                final int index = i;
                final PieChart.Data data = finalData.get(i);

                KeyFrame keyFrame = new KeyFrame(
                        Duration.seconds(0.5 * i), // 0.5 second delay for each item
                        event -> {
                            // Add this data point to the chart
                            chart.getData().add(data);

                            // Also animate the node once it's created
                            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.millis(50));
                            pause.setOnFinished(e -> {
                                Node node = data.getNode();
                                if (node != null) {
                                    node.setOpacity(0);

                                    FadeTransition ft = new FadeTransition(Duration.millis(800), node);
                                    ft.setFromValue(0);
                                    ft.setToValue(1);
                                    ft.play();
                                }
                            });
                            pause.play();
                        }
                );

                timeline.getKeyFrames().add(keyFrame);
            }

            timeline.play();
        });
    }

    private void loadDepartmentData() {
        try {
            // Get MongoDB collection
            MongoCollection<Document> userCollection = mongoConnection.getDatabase().getCollection("info");

            // Aggregate users by department
            AggregateIterable<Document> departmentResults = userCollection.aggregate(
                    Arrays.asList(
                            Aggregates.group("$department", Accumulators.sum("count", 1))
                    )
            );

            // Process results and calculate percentages
            Map<String, Integer> departmentCounts = new HashMap<>();
            int totalCount = 0;

            // First pass: collect counts
            for (Document doc : departmentResults) {
                String department = doc.getString("_id");
                int count = doc.getInteger("count");

                if (department != null) {
                    departmentCounts.put(department, count);
                    totalCount += count;
                }
            }

            // Define department series
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Percentage");

            // Calculate percentages and create chart data
            for (Map.Entry<String, Integer> entry : departmentCounts.entrySet()) {
                String department = entry.getKey();
                int count = entry.getValue();

                // Calculate percentage
                double percentage = (totalCount > 0) ? (count * 100.0 / totalCount) : 0;

                // Add data to series
                XYChart.Data<String, Number> data = new XYChart.Data<>(department, percentage);
                series.getData().add(data);
            }

            // If no data was found, use placeholder departments
            if (series.getData().isEmpty()) {
                System.out.println("No department data found, using placeholder data");
                String[] departments = {"CSE", "EEE", "ME", "CE", "EECE", "AE", "NAME", "IPE"};
                double[] percentages = {25, 18, 15, 12, 10, 8, 7, 5}; // Example percentages

                for (int i = 0; i < departments.length; i++) {
                    series.getData().add(new XYChart.Data<>(departments[i], percentages[i]));
                }
            }

            // Add the series to the chart with animation
            animateBarChartDataLoading(series);

        } catch (Exception e) {
            System.err.println("Error loading department data: " + e.getMessage());
            e.printStackTrace();

            // Use placeholder data in case of error
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Percentage");

            String[] departments = {"CSE", "EEE", "ME", "CE", "EECE", "AE", "NAME", "IPE"};
            double[] percentages = {25, 18, 15, 12, 10, 8, 7, 5}; // Example percentages

            for (int i = 0; i < departments.length; i++) {
                series.getData().add(new XYChart.Data<>(departments[i], percentages[i]));
            }

            animateBarChartDataLoading(series);
        }
    }

    private void animateBarChartDataLoading(XYChart.Series<String, Number> series) {
        // Execute on JavaFX thread
        javafx.application.Platform.runLater(() -> {
            // Clear existing data
            departmentChart.getData().clear();
            departmentChart.setTitle("");
            departmentChart.setAnimated(false); // We'll handle animations manually

            // Create a timeline for adding bars with delay
            Timeline timeline = new Timeline();

            // First, add the series to the chart (with empty data)
            XYChart.Series<String, Number> displaySeries = new XYChart.Series<>();
            displaySeries.setName(series.getName());
            departmentChart.getData().add(displaySeries);

            // Then, add each data point with a delay
            for (int i = 0; i < series.getData().size(); i++) {
                final int index = i;
                final XYChart.Data<String, Number> originalData = series.getData().get(i);

                KeyFrame keyFrame = new KeyFrame(
                        Duration.seconds(0.5 * i), // 0.5 second delay for each bar
                        event -> {
                            // Create a new data point
                            XYChart.Data<String, Number> newData = new XYChart.Data<>(
                                    originalData.getXValue(),
                                    originalData.getYValue()
                            );

                            // Add this data point to the display series
                            displaySeries.getData().add(newData);

                            // Animate the node once it's created
                            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.millis(50));
                            pause.setOnFinished(e -> {
                                Node node = newData.getNode();
                                if (node != null) {
                                    node.setOpacity(0);

                                    FadeTransition ft = new FadeTransition(Duration.millis(800), node);
                                    ft.setFromValue(0);
                                    ft.setToValue(1);
                                    ft.play();
                                }
                            });
                            pause.play();
                        }
                );

                timeline.getKeyFrames().add(keyFrame);
            }

            timeline.play();
        });
    }

    @FXML
    private void sendEmail() {
        String messageContent = emailContent.getText().trim();
        if (messageContent.isEmpty()) {
            showAlert(AlertType.WARNING, "Empty Message", "Please enter a message before sending.");
            return;
        }

        // Get the user's name to include in the email
        String userName = namelbl.getText();

        // Create email subject
        String subject = "Message from Alumni Portal User: " + userName;

        // Create email body with user information
        String emailBody = "From: " + userName + "\n";
        if (userEmail != null && !userEmail.isEmpty()) {
            emailBody += "User Email: " + userEmail + "\n\n";
        }
        emailBody += "Message:\n" + messageContent;

        // Send the email to admin
        String[] recipients = {"arhasan3487@gmail.com"};

        try {
            Integer result = Broadcast.sendEmail(recipients, subject, emailBody, null);

            if (result == 1) {
                showAlert(AlertType.INFORMATION, "Success", "Your message has been sent successfully.");
                emailContent.clear();
            } else {
                showAlert(AlertType.ERROR, "Error", "Failed to send your message. Please try again later.");
            }
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Navigation methods
    @FXML
    private void switchToStudentHome() {
        // Navigation logic
        System.out.println("Switching to Student Home...");
    }

    @FXML
    public void switchtoStudentIdCard(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("student_id_card.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
    }

    @FXML
    public void switchtoAlumniListStudent(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("alumniListStudent.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    @FXML
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

    @FXML
    private void mistWebsite() {
        try {
            Desktop.getDesktop().browse(new URI("https://mist.ac.bd"));
        } catch (IOException | URISyntaxException e) {
            System.err.println("Error opening MIST website: " + e.getMessage());
        }
    }

    @FXML
    public void logOut(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Log Out");
        alert.setHeaderText("You're about to logout");
        alert.setContentText("Do you want to log out?");

        // Get the user's response
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Proceed with logout only if user confirms
            Parent root = FXMLLoader.load(getClass().getResource("student_home.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.close();
        } else {
            // User clicked Cancel, so do nothing
            System.out.println("Logout canceled");
        }
    }

}