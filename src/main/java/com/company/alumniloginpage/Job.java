package com.company.alumniloginpage;

import java.time.LocalDate;

public class Job {
    private int id;
    private String title;
    private String company;
    private String location;
    private String category;
    private String salary;
    private LocalDate datePosted;
    private String circularUrl;
    private String description;

    public Job(int id, String title, String company, String location, String category,
               String salary, LocalDate datePosted, String circularUrl, String description) {
        this.id = id;
        this.title = title;
        this.company = company;
        this.location = location;
        this.category = category;
        this.salary = salary;
        this.datePosted = datePosted;
        this.circularUrl = circularUrl;
        this.description = description;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCompany() {
        return company;
    }

    public String getLocation() {
        return location;
    }

    public String getCategory() {
        return category;
    }

    public String getSalary() {
        return salary;
    }

    public LocalDate getDatePosted() {
        return datePosted;
    }

    public String getCircularUrl() {
        return circularUrl;
    }

    public String getDescription() {
        return description;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public void setDatePosted(LocalDate datePosted) {
        this.datePosted = datePosted;
    }

    public void setCircularUrl(String circularUrl) {
        this.circularUrl = circularUrl;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return title + " at " + company;
    }
}