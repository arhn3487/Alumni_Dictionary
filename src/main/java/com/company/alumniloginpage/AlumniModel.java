package com.company.alumniloginpage;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

public class AlumniModel {
    private final StringProperty name;
    private final StringProperty studentId;
    private final StringProperty batch;
    private final StringProperty department;
    private final StringProperty email;
    private final StringProperty phone;
    private final ObjectProperty<ImageView> photoView;
    private final ObjectProperty<Button> actionsButton;
    private String photoPath;

    public AlumniModel(String name, String studentId, String batch, String department, String email, String phone) {
        this.name = new SimpleStringProperty(name);
        this.studentId = new SimpleStringProperty(studentId);
        this.batch = new SimpleStringProperty(batch);
        this.department = new SimpleStringProperty(department);
        this.email = new SimpleStringProperty(email);
        this.phone = new SimpleStringProperty(phone);
        this.photoView = new SimpleObjectProperty<>();
        this.actionsButton = new SimpleObjectProperty<>();
    }

    // Name Property
    public StringProperty nameProperty() {
        return name;
    }
    public String getName() {
        return name.get();
    }
    public void setName(String name) {
        this.name.set(name);
    }

    // Student ID Property
    public StringProperty studentIdProperty() {
        return studentId;
    }
    public String getStudentId() {
        return studentId.get();
    }
    public void setStudentId(String studentId) {
        this.studentId.set(studentId);
    }

    // Batch Property
    public StringProperty batchProperty() {
        return batch;
    }
    public String getBatch() {
        return batch.get();
    }
    public void setBatch(String batch) {
        this.batch.set(batch);
    }

    // Department Property
    public StringProperty departmentProperty() {
        return department;
    }
    public String getDepartment() {
        return department.get();
    }
    public void setDepartment(String department) {
        this.department.set(department);
    }

    // Email Property
    public StringProperty emailProperty() {
        return email;
    }
    public String getEmail() {
        return email.get();
    }
    public void setEmail(String email) {
        this.email.set(email);
    }

    // Phone Property
    public StringProperty phoneProperty() {
        return phone;
    }
    public String getPhone() {
        return phone.get();
    }
    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    // Photo View Property
    public ObjectProperty<ImageView> photoViewProperty() {
        return photoView;
    }
    public ImageView getPhotoView() {
        return photoView.get();
    }
    public void setPhotoView(ImageView photoView) {
        this.photoView.set(photoView);
    }

    // Actions Button Property
    public ObjectProperty<Button> actionsButtonProperty() {
        return actionsButton;
    }
    public Button getActionsButton() {
        return actionsButton.get();
    }
    public void setActionsButton(Button button) {
        this.actionsButton.set(button);
    }

    // Photo Path (regular field, not a property as it's not displayed in the TableView)
    public String getPhotoPath() {
        return photoPath;
    }
    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    @Override
    public String toString() {
        return name.get() + " (" + studentId.get() + ")";
    }
}