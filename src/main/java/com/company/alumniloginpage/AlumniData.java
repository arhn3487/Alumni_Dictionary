package com.company.alumniloginpage;

import javafx.scene.image.ImageView;

public class AlumniData {
    private String id;
    private String studentId;
    private String name;
    private String batch;
    private String department;
    private String degree;
    private String graduationYear;
    private String workplace;
    private String email;
    private String phone;
    private String address;
    private ImageView pictureView;
    private String userType;

    public AlumniData(String id, String studentId, String name, String batch, String department, String degree,
                      String graduationYear, String workplace, String email, String phone,
                      String address, ImageView pictureView, String userType) {
        this.id = id;
        this.studentId = studentId;
        this.name = name;
        this.batch = batch;
        this.department = department;
        this.degree = degree;
        this.graduationYear = graduationYear;
        this.workplace = workplace;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.pictureView = pictureView;
        this.userType = userType;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(String graduationYear) {
        this.graduationYear = graduationYear;
    }

    public String getWorkplace() {
        return workplace;
    }

    public void setWorkplace(String workplace) {
        this.workplace = workplace;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ImageView getPictureView() {
        return pictureView;
    }

    public void setPictureView(ImageView pictureView) {
        this.pictureView = pictureView;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}