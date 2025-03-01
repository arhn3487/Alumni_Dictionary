package com.company.alumniloginpage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.bson.Document;
import org.bson.types.ObjectId;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class EventController {
    // FXML Injected Controls
    @FXML private TableView<Event> eventTableView;
    @FXML private TableColumn<Event, String> titleColumn;
    @FXML private TableColumn<Event, LocalDate> dateColumn;
    @FXML private TableColumn<Event, String> locationColumn;
    @FXML private TableColumn<Event, String> descriptionColumn;
    @FXML private TextField eventTitle;
    @FXML private DatePicker eventDate;
    @FXML private TextField eventLocation;
    @FXML private TextArea eventDescription;
    @FXML private Button addEventButton;
    @FXML private Button removeEventButton;
    @FXML private Button clearButton;

    // MongoDB Connection
    private MongoDBConnection mongoDBConnection;

    // Stage and Scene for navigation
    private Stage stage;
    private Scene scene;

    // Observable List to hold events
    private ObservableList<Event> eventList = FXCollections.observableArrayList();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Initialize method called when the FXML is loaded
    @FXML
    public void initialize() {
        // Initialize MongoDB connection
        mongoDBConnection = MongoDBConnection.getInstance();

        // Initialize event management features
        initializeEventManagement();
    }

    // Method to initialize event management functionality
    public void initializeEventManagement() {
        // Set up the TableView with columns
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Set up the TableView with data
        eventTableView.setItems(eventList);

        // Add event selection listener
        eventTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                displayEventDetails(newValue);
            }
        });

        // Add event handlers for buttons
        addEventButton.setOnAction(event -> addEvent());
        removeEventButton.setOnAction(event -> removeEvent());
        clearButton.setOnAction(event -> clearEventFields());

        // Load events from database
        loadEventsFromDatabase();
    }

    // Method to load events from MongoDB
    private void loadEventsFromDatabase() {
        try {
            MongoCollection<Document> eventsCollection = mongoDBConnection.getDatabase().getCollection("events");
            FindIterable<Document> events = eventsCollection.find();

            eventList.clear();
            for (Document doc : events) {
                ObjectId objectId = doc.getObjectId("_id");
                String id = objectId.toString();
                String title = doc.getString("title");
                LocalDate date = LocalDate.parse(doc.getString("date"), DATE_FORMATTER);
                String location = doc.getString("location");
                String description = doc.getString("description");

                Event event = new Event(id, title, date, location, description);
                eventList.add(event);
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to load events: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to add a new event
    private void addEvent() {
        String title = eventTitle.getText().trim();
        LocalDate date = eventDate.getValue();
        String location = eventLocation.getText().trim();
        String description = eventDescription.getText().trim();

        if (title.isEmpty() || date == null || location.isEmpty() || description.isEmpty()) {
            showAlert("Input Error", "Please fill all fields");
            return;
        }

        try {
            // Create a new document to insert into MongoDB
            Document eventDoc = new Document()
                    .append("title", title)
                    .append("date", date.format(DATE_FORMATTER))
                    .append("location", location)
                    .append("description", description);

            // Insert the document into the events collection
            MongoCollection<Document> eventsCollection = mongoDBConnection.getDatabase().getCollection("events");
            eventsCollection.insertOne(eventDoc);

            // Get the generated ID and create an Event object
            ObjectId objectId = eventDoc.getObjectId("_id");
            String id = objectId.toString();
            Event newEvent = new Event(id, title, date, location, description);

            // Add the event to the table view
            eventList.add(newEvent);
            clearEventFields();

            showAlert("Success", "Event added successfully");
        } catch (Exception e) {
            showAlert("Error", "Failed to add event: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to remove an event
    private void removeEvent() {
        Event selectedEvent = eventTableView.getSelectionModel().getSelectedItem();
        if (selectedEvent == null) {
            showAlert("Selection Error", "Please select an event to remove");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Remove Event");
        alert.setContentText("Are you sure you want to remove: " + selectedEvent.getTitle() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Remove from database - Convert string ID to ObjectId
                MongoCollection<Document> eventsCollection = mongoDBConnection.getDatabase().getCollection("events");
                ObjectId objectId = new ObjectId(selectedEvent.getId());
                eventsCollection.deleteOne(new Document("_id", objectId));

                // Remove from table view
                eventList.remove(selectedEvent);
                clearEventFields();

                showAlert("Success", "Event removed successfully");
            } catch (Exception e) {
                showAlert("Error", "Failed to remove event: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // Method to clear event form fields
    private void clearEventFields() {
        eventTitle.clear();
        eventDate.setValue(null);
        eventLocation.clear();
        eventDescription.clear();
        eventTableView.getSelectionModel().clearSelection();
    }

    // Method to display event details in form fields
    private void displayEventDetails(Event event) {
        eventTitle.setText(event.getTitle());
        eventDate.setValue(event.getDate());
        eventLocation.setText(event.getLocation());
        eventDescription.setText(event.getDescription());
    }

    // Helper method to show alerts
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Navigation methods with ActionEvent parameter for consistency with Controller class
    @FXML
    public void switchToHome(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("home.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void switchalumniCard(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("alumniCard.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void switchtoEvent(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("event.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.setFullScreen(true);
    }

    @FXML
    public void switchBroadcast(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("broadcast.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void logOut(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("home.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Log Out");
        alert.setHeaderText("You're about to logout");
        alert.setContentText("Do you want to save before exiting?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            stage.setScene(scene);
            stage.show();
        }
    }
}