package com.company.alumniloginpage;

import java.time.LocalDate;

public class Event {
    private String id;
    private String title;
    private LocalDate date;
    private String location;
    private String description;

    public Event(String id, String title, LocalDate date, String location, String description) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.location = location;
        this.description = description;
    }

    // Constructor without ID for new events
    public Event(String title, LocalDate date, String location, String description) {
        this.title = title;
        this.date = date;
        this.location = location;
        this.description = description;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return title + " (" + date + ")";
    }
}