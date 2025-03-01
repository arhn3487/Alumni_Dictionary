package com.company.alumniloginpage;

public class Participant {
    private String name;
    private String studentId;
    private String batch;
    private String department;
    private String phoneNumber;

    public Participant(String name, String studentId, String batch, String department, String phoneNumber) {
        this.name = name;
        this.studentId = studentId;
        this.batch = batch;
        this.department = department;
        this.phoneNumber = phoneNumber;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return name + " (" + studentId + ")";
    }
}