package com.company.alumniloginpage;

import javafx.scene.image.ImageView;

public class AlumniData {
    private String id;
    private String type;
    private String email;
    private ImageView pictureView;

    public AlumniData(String id, String type, String email, ImageView pictureView) {
        this.id = id;
        this.type = type;
        this.email = email;
        this.pictureView = pictureView;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ImageView getPictureView() {
        return pictureView;
    }

    public void setPictureView(ImageView pictureView) {
        this.pictureView = pictureView;
    }
}