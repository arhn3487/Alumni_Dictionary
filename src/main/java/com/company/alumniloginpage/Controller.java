package com.company.alumniloginpage;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class Controller {

    private Stage stage;
    private Scene scene;
    @FXML
    private ComboBox<String> degree;
    @FXML
    private ComboBox<String> department;
    @FXML
    private ComboBox<String> graduationYear;

    @FXML
    public void initialize() {
        if (degree != null) {
            degree.setItems(FXCollections.observableArrayList("BSc", "MSc"));
            System.out.println("degree ComboBox initialized.");
        } else {
            System.err.println("Error: 'degree' ComboBox not found. Check fx:id in FXML.");
        }

        if (department != null) {
            department.setItems(FXCollections.observableArrayList(
                    "CSE", "EECE", "CE", "ME", "AE", "EWCE",
                    "PME", "NAME", "IPE", "BME", "ARCH", "NSE"
            ));
            department.setVisibleRowCount(5);
            System.out.println("department ComboBox initialized.");
        } else {
            System.err.println("Error: 'department' ComboBox not found. Check fx:id in FXML.");
        }

        if (graduationYear != null) {
            graduationYear.setItems(FXCollections.observableArrayList(
                    "2002", "2003", "2004", "2005", "2006", "2007",
                    "2008", "2009", "2010", "2011", "2012", "2013",
                    "2014", "2015", "2016", "2017", "2018", "2019",
                    "2020", "2021", "2022", "2023"
            ));
            graduationYear.setVisibleRowCount(5);
            System.out.println("graduationYear ComboBox initialized.");
        } else {
            System.err.println("Error: 'graduationYear' ComboBox not found. Check fx:id in FXML.");
        }
    }


    public void switchToCreateAccount(ActionEvent event) throws IOException {
        //Parent root = loadFXML(load.(getClass().getResource("SignUp.fxml")));
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("CreateAccount.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);

        stage.show();
    }

    public void switchToSignupForm(ActionEvent event) throws IOException {
        //Parent root = loadFXML(load.(getClass().getResource("SignUp.fxml")));
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("signupForm.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);

        stage.show();
    }

    public void switchToLogin(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("login.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToHome(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("home.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToUserProfile(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("userProfile.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void mistWebsite(ActionEvent event) throws URISyntaxException,IOException{
        Desktop.getDesktop().browse(new URI("https://mist.ac.bd/"));
    }

    public void logOut(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("home.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Log Out");
        alert.setHeaderText("You're about to logout");
        alert.setContentText("Do you want to save before exiting ?");

        if(alert.showAndWait().get() == ButtonType.OK) {
            stage.close();
        }
    }



    private Parent loadFXML(String fxmlFileName) throws IOException {
        var resource = getClass().getResource(fxmlFileName);
        if (resource == null) {
            throw new IOException("FXML file not found: " + fxmlFileName);
        }
        return FXMLLoader.load(resource);
    }
}
