package com.company.alumniloginpage;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class Event {
    private final SimpleStringProperty name;
    private final SimpleStringProperty date;
    private final SimpleStringProperty location;
    private final SimpleStringProperty description;
    private final SimpleBooleanProperty registered;

    public Event(String name, String date, String location, String description) {
        this.name = new SimpleStringProperty(name);
        this.date = new SimpleStringProperty(date);
        this.location = new SimpleStringProperty(location);
        this.description = new SimpleStringProperty(description);
        this.registered = new SimpleBooleanProperty(false);
    }

    // Getters and setters
    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getDate() {
        return date.get();
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public String getLocation() {
        return location.get();
    }

    public void setLocation(String location) {
        this.location.set(location);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public boolean isRegistered() {
        return registered.get();
    }

    public void setRegistered(boolean registered) {
        this.registered.set(registered);
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public SimpleStringProperty dateProperty() {
        return date;
    }

    public SimpleStringProperty locationProperty() {
        return location;
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public SimpleBooleanProperty registeredProperty() {
        return registered;
    }
}