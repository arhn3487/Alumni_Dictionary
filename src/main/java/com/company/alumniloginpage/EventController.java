package com.company.alumniloginpage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EventController implements Initializable {

    @FXML
    private TableView<Event> eventTable;

    @FXML
    private TableColumn<Event, String> eventNameColumn;

    @FXML
    private TableColumn<Event, String> eventDateColumn;

    @FXML
    private TableColumn<Event, String> eventLocationColumn;

    @FXML
    private TableColumn<Event, String> eventDescriptionColumn;

    @FXML
    private Button backButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up the table columns
        eventNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        eventDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        eventLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        eventDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Load sample data
        loadSampleEvents();
    }

    private void loadSampleEvents() {
        // Add some sample events
        eventTable.getItems().add(new Event("Annual Alumni Reunion", "March 15, 2025", "Main Campus Auditorium", "Join us for our annual alumni gathering with food, networking, and special guests."));
        eventTable.getItems().add(new Event("Career Fair", "April 10, 2025", "Student Center", "Connect with employers and explore job opportunities in various industries."));
        eventTable.getItems().add(new Event("Homecoming Weekend", "May 20, 2025", "University Stadium", "Celebrate with fellow alumni during our annual homecoming festivities."));
        eventTable.getItems().add(new Event("Workshop: Networking Skills", "June 5, 2025", "Business School Room 203", "Learn effective networking techniques from industry professionals."));
    }

    @FXML
    private void switchToDashboard(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("dashboard.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void registerForEvent(ActionEvent event) {
        Event selectedEvent = eventTable.getSelectionModel().getSelectedItem();
        if (selectedEvent != null) {
            // Here you would typically open a registration form or confirmation dialog
            System.out.println("Registered for: " + selectedEvent.getName());

            // For now, just change the status in the table
            selectedEvent.setRegistered(true);
            eventTable.refresh();
        }
    }
}