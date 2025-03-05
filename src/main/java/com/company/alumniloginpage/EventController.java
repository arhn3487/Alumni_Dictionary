package com.company.alumniloginpage;

import com.mongodb.client.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

public class EventController {
    // FXML Injected Controls for Events
    @FXML private TableView<Event> eventTableView;
    @FXML private TableColumn<Event, String> titleColumn;
    @FXML private TableColumn<Event, LocalDate> dateColumn;
    @FXML private TableColumn<Event, String> locationColumn;
    @FXML private TableColumn<Event, String> descriptionColumn;
    @FXML private TableColumn<Event, Void> actionColumn;

    // FXML Injected Controls for Participants
    @FXML private TableView<Participant> participantsTableView;
    @FXML private TableColumn<Participant, String> participantNameColumn;
    @FXML private TableColumn<Participant, String> participantIdColumn;
    @FXML private TableColumn<Participant, String> participantBatchColumn;
    @FXML private TableColumn<Participant, String> participantDeptColumn;
    @FXML private TableColumn<Participant, String> participantPhoneColumn;
    @FXML private Button viewParticipantsButton;

    // Other FXML fields
    @FXML private TextField eventTitle;
    @FXML private DatePicker eventDate;
    @FXML private TextField eventLocation;
    @FXML private TextArea eventDescription;
    @FXML private Button addEventButton;
    @FXML private Button removeEventButton;
    @FXML private Button clearButton;
    @FXML private Label namelbl;
    @FXML private ImageView userImageView;

    // MongoDB Connection
    private MongoDBConnection mongoDBConnection;

    // Stage and Scene for navigation
    private Stage stage;
    private Scene scene;

    // Observable Lists to hold events and participants
    private ObservableList<Event> eventList = FXCollections.observableArrayList();
    private ObservableList<Participant> participantsList = FXCollections.observableArrayList();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Currently selected event
    private Event currentEvent;

    @FXML
    public void initialize() {
        mongoDBConnection = MongoDBConnection.getInstance();

        // Load user information
        loadUserInfo();

        // Initialize event management
        initializeEventManagement();

        // Initialize participant management
        initializeParticipantManagement();
    }

    private void loadUserInfo() {
        String loggedInUserId = SharedData.getInstance().getLoggedInUserId();

        if (loggedInUserId != null) {
            try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
                MongoDatabase database = mongoClient.getDatabase("alumni");
                MongoCollection<Document> collection = database.getCollection("info");

                Document user = collection.find(new Document("studentId", loggedInUserId)).first();

                if (user != null) {
                    namelbl.setText(user.getString("name"));

                    String imagePath = user.getString("Image");
                    if (imagePath != null && !imagePath.isEmpty()) {
                        javafx.scene.image.Image image = new Image(new File(imagePath).toURI().toString());
                        userImageView.setImage(image);
                    }
                } else {
                    System.err.println("User not found in the database.");
                }
            } catch (Exception e) {
                System.err.println("Error loading user data: " + e.getMessage());
            }
        } else {
            System.err.println("No logged-in user ID found.");
        }
    }

    // Method to initialize event management functionality
    public void initializeEventManagement() {
        // Set up the TableView with columns
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Add register/unregister buttons to action column
        setupActionColumn();

        // Set up the TableView with data
        eventTableView.setItems(eventList);

        // Add event selection listener
        eventTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                currentEvent = newValue;
                displayEventDetails(newValue);
            }
        });

        // Add event handlers for buttons
        if (addEventButton != null) addEventButton.setOnAction(event -> addEvent());
        if (removeEventButton != null) removeEventButton.setOnAction(event -> removeEvent());
        if (clearButton != null) clearButton.setOnAction(event -> clearEventFields());

        // Load events from database
        loadEventsFromDatabase();
    }

    // Method to initialize participant management functionality
    private void initializeParticipantManagement() {
        // Set up the participant TableView with columns
        if(participantNameColumn==null) return;
        participantNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        participantIdColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        participantBatchColumn.setCellValueFactory(new PropertyValueFactory<>("batch"));
        participantDeptColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
        participantPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        // Set up the TableView with data
        participantsTableView.setItems(participantsList);

        // Set action for view participants button
        if (viewParticipantsButton != null) {
            viewParticipantsButton.setOnAction(event -> {
                if (currentEvent != null) {
                    loadParticipantsForEvent(currentEvent.getId());
                } else {
                    showAlert("Selection Error", "Please select an event to view participants");
                }
            });
        }
    }

    // Method to setup the action column with register/unregister buttons
    private void setupActionColumn() {
        if (actionColumn != null) {
            actionColumn.setCellFactory(param -> new TableCell<>() {
                private final Button registerButton = new Button("Register");

                {
//                    registerButton.setOnAction(event -> {
//                        Event event = getTableView().getItems().get(getIndex());
//                        registerForEvent(event);
//                    });

                    registerButton.setOnAction(event -> {
                        Event selectedEvent = getTableView().getItems().get(getIndex());
                        registerForEvent(selectedEvent);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        String loggedInUserId = SharedData.getInstance().getLoggedInUserId();
                        Event event = getTableView().getItems().get(getIndex());

                        // Check if user is already registered
                        boolean isRegistered = checkIfRegistered(event.getId(), loggedInUserId);

                        if (isRegistered) {
                            registerButton.setText("Unregister");
                            registerButton.setStyle("-fx-background-color: #ff6666;");
                        } else {
                            registerButton.setText("Register");
                            registerButton.setStyle("-fx-background-color: #66b3ff;");
                        }

                        setGraphic(registerButton);
                    }
                }
            });
        }
    }

    // Check if user is registered for an event
    private boolean checkIfRegistered(String eventId, String userId) {
        try {
            MongoCollection<Document> participantsCollection = mongoDBConnection.getDatabase().getCollection("event_participants");
            Document query = new Document("eventId", eventId).append("studentId", userId);
            return participantsCollection.find(query).first() != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Register or unregister user for an event
    private void registerForEvent(Event event) {
        String loggedInUserId = SharedData.getInstance().getLoggedInUserId();
        if (loggedInUserId == null) {
            showAlert("Error", "You must be logged in to register for events");
            return;
        }

        boolean isRegistered = checkIfRegistered(event.getId(), loggedInUserId);

        if (isRegistered) {
            // Unregister
            try {
                MongoCollection<Document> participantsCollection = mongoDBConnection.getDatabase().getCollection("event_participants");
                Document query = new Document("eventId", event.getId()).append("studentId", loggedInUserId);
                participantsCollection.deleteOne(query);

                showAlert("Success", "You have been unregistered from the event");
                // Refresh the table
                eventTableView.refresh();

                // If this is the current event, refresh participants list
                if (currentEvent != null && currentEvent.getId().equals(event.getId())) {
                    loadParticipantsForEvent(event.getId());
                }
            } catch (Exception e) {
                showAlert("Error", "Failed to unregister: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            // Register
            try {
                // Get user info
                MongoCollection<Document> usersCollection = mongoDBConnection.getDatabase().getCollection("info");
                Document userDoc = usersCollection.find(new Document("studentId", loggedInUserId)).first();

                if (userDoc == null) {
                    showAlert("Error", "User information not found");
                    return;
                }

                // Create participant document
                Document participantDoc = new Document()
                        .append("eventId", event.getId())
                        .append("studentId", loggedInUserId)
                        .append("name", userDoc.getString("name"))
                        .append("batch", userDoc.getString("batch"))
                        .append("department", userDoc.getString("department"))
                        .append("phoneNumber", userDoc.getString("phone"));

                // Insert into database
                MongoCollection<Document> participantsCollection = mongoDBConnection.getDatabase().getCollection("event_participants");
                participantsCollection.insertOne(participantDoc);

                showAlert("Success", "You have been registered for the event");
                // Refresh the table
                eventTableView.refresh();

                // If this is the current event, refresh participants list
                if (currentEvent != null && currentEvent.getId().equals(event.getId())) {
                    loadParticipantsForEvent(event.getId());
                }
            } catch (Exception e) {
                showAlert("Error", "Failed to register: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // Load participants for a specific event
    private void loadParticipantsForEvent(String eventId) {
        try {
            MongoCollection<Document> participantsCollection = mongoDBConnection.getDatabase().getCollection("event_participants");
            FindIterable<Document> participants = participantsCollection.find(new Document("eventId", eventId));

            participantsList.clear();
            for (Document doc : participants) {
                String name = doc.getString("name");
                String studentId = doc.getString("studentId");
                String batch = doc.getString("batch");
                String department = doc.getString("department");
                String phoneNumber = doc.getString("phoneNumber");

                Participant participant = new Participant(name, studentId, batch, department, phoneNumber);
                participantsList.add(participant);
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to load participants: " + e.getMessage());
            e.printStackTrace();
        }
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

                // Also remove all participants for this event
                MongoCollection<Document> participantsCollection = mongoDBConnection.getDatabase().getCollection("event_participants");
                participantsCollection.deleteMany(new Document("eventId", selectedEvent.getId()));

                // Remove from table view
                eventList.remove(selectedEvent);
                participantsList.clear();
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
        if (eventTitle != null) eventTitle.clear();
        if (eventDate != null) eventDate.setValue(null);
        if (eventLocation != null) eventLocation.clear();
        if (eventDescription != null) eventDescription.clear();
        eventTableView.getSelectionModel().clearSelection();
    }

    // Method to display event details in form fields
    private void displayEventDetails(Event event) {
        if (eventTitle != null) eventTitle.setText(event.getTitle());
        if (eventDate != null) eventDate.setValue(event.getDate());
        if (eventLocation != null) eventLocation.setText(event.getLocation());
        if (eventDescription != null) eventDescription.setText(event.getDescription());
    }

    // Helper method to show alerts
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Navigation methods
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

    public void switchalumniList(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("alumniList.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.setFullScreen(true);
    }

    public void switchToJobBoard(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("job.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
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

    public void mistWebsite(ActionEvent event) throws URISyntaxException, IOException {
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