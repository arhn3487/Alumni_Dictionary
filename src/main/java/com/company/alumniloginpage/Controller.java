package com.company.alumniloginpage;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import org.bson.Document;

import javax.mail.*;
import javax.mail.internet.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.stream.StreamSupport;
import java.util.List;
import java.util.ArrayList;


public class Controller {

    private Stage stage;
    private Scene scene;

    @FXML
    private TextField name;

    @FXML
    private TextField studentId;

    @FXML
    private TextField batch;

    @FXML
    private RadioButton male;

    @FXML
    private RadioButton female;

    @FXML
    private ComboBox<String> degree;

    @FXML
    private ComboBox<String> department;

    @FXML
    private ComboBox<String> graduationYear;

    @FXML
    private TextField workplace;

    @FXML
    private TextField email;

    @FXML
    private TextField linkedin;

    @FXML
    private TextField phone;

    @FXML
    private TextField facebook;

    @FXML
    private TextField address;

    //For Broadcast page
    @FXML
    private ComboBox<String> broad_batch;

    @FXML

    private ComboBox<String> broad_dept;

    @FXML
    private TextField attachmentPath;

    @FXML
    private TextField imagePathField;

    @FXML
    private TextArea broadtext;

    @FXML
    private TextField broadsubject;

    @FXML
    private Button choosefile;

    @FXML
    private Button sendmail;

    private File attachmentFile;

    @FXML
    private TableView<AlumniListController.Alumni> alumniTable;



    private MongoDBConnection mongoDBConnection;

    @FXML
    public void initialize() {

        mongoDBConnection = new MongoDBConnection();
        //If anything is null in this combobox this will not be initialized When I developed the frontend I try to initialize the department where there was not any fx-id named department that's why there always show an error
        if (degree == null) {
            System.out.println("Not found any fx-id name degree");
        } else {
            degree.setItems(FXCollections.observableArrayList("BSc", "MSc"));
            //degree.getSelectionModel().selectFirst(); // Automatically select the first option
            System.out.println("degree ComboBox initialized with options: BSc, MSc");
        }
        if (department== null) {
            System.out.println("Not found any fx-id name department");

        } else {
            department.setItems(FXCollections.observableArrayList("All", "CSE", "EECE","CE","ME","AE","EWCE","PME","NAME","IPE","BME","ARCH","NSE"));
            department.setVisibleRowCount(5);
            System.out.println("Successfully initialized department");
        }
        if (graduationYear== null) {
            System.out.println("Not found any fx-id name department");

        } else {
            graduationYear.setItems(FXCollections.observableArrayList("All", "2002", "2003","2004","2005","2006","2007","2008","2009","2010","2011","2012","2013","2014","2015","2016","2017","2018","2019","2020","2021","2022","2023"));
            graduationYear.setVisibleRowCount(5);
            System.out.println("Successfully initialized department");
        }
        if (broad_dept== null) {
            System.out.println("Not found any fx-id name department");

        } else {
            broad_dept.setItems(FXCollections.observableArrayList("CSE", "EECE","CE","ME","AE","EWCE","PME","NAME","IPE","BME","ARCH","NSE"));
            broad_dept.setVisibleRowCount(5);
            System.out.println("Successfully initialized department");
        }
        if (broad_batch== null) {
            System.out.println("Not found any fx-id name department");

        } else {
            broad_batch.setItems(FXCollections.observableArrayList("2002", "2003","2004","2005","2006","2007","2008","2009","2010","2011","2012","2013","2014","2015","2016","2017","2018","2019","2020","2021","2022","2023"));
            broad_batch.setVisibleRowCount(5);
            System.out.println("Successfully initialized department");
        }

        // Set up the TableView columns
//        alumniTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("name"));
//        alumniTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("studentId"));
//        alumniTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("batch"));
//        alumniTable.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("graduationYear"));
//        alumniTable.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("department"));
    }

    private String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Generates a 6-digit number
        return String.valueOf(otp);
    }

    private void sendOTP(String email, String otp) {
        // Sender's email credentials
        String from = "arafatsakibisbat@gmail.com"; // Replace with your email
        String password = "icde xfka vrxx jyxc"; // Replace with your email password

        // Setup mail server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Create a session with the email server
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            // Create a MimeMessage object
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            message.setSubject("OTP Verification");
            message.setText("Your OTP is: " + otp);

            // Send the email
            Transport.send(message);
            System.out.println("OTP sent successfully to " + email);
        } catch (MessagingException e) {
            System.err.println("Error sending OTP: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void saveinfo()
    {
        // Collect data from TextFields
        String dname = name.getText();
        String did = studentId.getText();
        String dbatch = batch.getText();
        String dworkplace = workplace.getText();
        String demail = email.getText();
        String dlinkedin = linkedin.getText();
        String dphone = phone.getText();
        String dfacebook = facebook.getText();
        String daddress = address.getText();
        String dImage=imagePathField.getText();

        // Collect data from RadioButtons
        String dgender = "";
        if (male.isSelected()) {
            dgender = "Male";
        } else if (female.isSelected()) {
            dgender = "Female";
        }

        // Collect data from ComboBoxes
        String ddegree = degree.getValue(); // Get selected value from ComboBox
        String ddepartment = department.getValue();
        String dgraduationYear = graduationYear.getValue();

        // Generate and send OTP
//        String generatedOTP = generateOTP();
//
//        SendOTP sendOTP = new SendOTP();
//
//        sendOTP.sendEmail(demail, generatedOTP);

        String generatedOTP = generateOTP();
        //SendOTP;
        sendOTP(demail, generatedOTP);

        // Prompt the user to enter the OTP
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("OTP Verification");
        dialog.setHeaderText("An OTP has been sent to your email.");
        dialog.setContentText("Please enter the OTP:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String enteredOTP = result.get();

            // Verify the OTP
            if (generatedOTP.equals(enteredOTP)) {
                // OTP matched, open the password form
                try {
                    // Load the password form
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("passwordform2.fxml"));
                    Parent root = loader.load();

                    // Get the controller and pass the user data
                    PasswordController passwordController = loader.getController();
                    Document userData = new Document()
                            .append("name", dname)
                            .append("studentId", did)
                            .append("batch", dbatch)
                            .append("gender", dgender)
                            .append("degree", ddegree)
                            .append("department", ddepartment)
                            .append("graduationYear", dgraduationYear)
                            .append("workplace", dworkplace)
                            .append("email", demail)
                            .append("linkedin", dlinkedin)
                            .append("phone", dphone)
                            .append("facebook", dfacebook)
                            .append("address", daddress);
                    userData.append("Image",dImage);

                    passwordController.setUserData(userData);

                    // Show the password form
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Create Password");
                    stage.show();
                } catch (IOException e) {
                    System.err.println("Error loading password form: " + e.getMessage());
                }
            } else {
                // OTP did not match, show error and allow resend
                showAlert("Error", "Invalid OTP. Please try again.");
            }
        }
    }

    @FXML
    private void handleSearch() {
        String year = graduationYear.getValue();
        String batch = department.getValue();

        // Fetch alumni based on the selected year and batch
        List<AlumniListController.Alumni> alumniList = fetchAlumni(year, batch);

        // Display the alumni list in the TableView
        ObservableList<AlumniListController.Alumni> data = FXCollections.observableArrayList(alumniList);
        alumniTable.setItems(data);
    }

    private List<AlumniListController.Alumni> fetchAlumni(String year, String batch) {
        List<AlumniListController.Alumni> alumniList = new ArrayList<>();

        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("alumni");
            MongoCollection<Document> collection = database.getCollection("info");

            // Build the query based on the selected year and batch
            Document query = new Document();

            if (!"All".equals(year)) {
                query.append("graduationYear", year); // Filter by year if not "All"
            }

            if (!"All".equals(batch)) {
                query.append("department", batch); // Filter by batch if not "All"
            }

            // Query to find alumni with the specified year and/or batch
            Iterable<Document> alumniDocuments = collection.find(query);

            // Convert MongoDB documents to Alumni objects
            for (Document doc : alumniDocuments) {
                AlumniListController.Alumni alumni = new AlumniListController.Alumni(
                        doc.getString("name"),
                        doc.getString("studentId"),
                        doc.getString("batch"),
                        doc.getString("graduationYear"),
                        doc.getString("department")
                );
                alumniList.add(alumni);
            }
        } catch (Exception e) {
            System.err.println("Error fetching alumni: " + e.getMessage());
        }

        return alumniList;
    }

    // Alumni class to represent a single alumni record
    public static class Alumni {
        private final String name;
        private final String studentId;
        private final String batch;
        private final String graduationYear;
        private final String department;

        public Alumni(String name, String studentId, String batch, String graduationYear, String department) {
            this.name = name;
            this.studentId = studentId;
            this.batch = batch;
            this.graduationYear = graduationYear;
            this.department = department;
        }

        public String getName() {
            return name;
        }

        public String getStudentId() {
            return studentId;
        }

        public String getBatch() {
            return batch;
        }

        public String getGraduationYear() {
            return graduationYear;
        }

        public String getDepartment() {
            return department;
        }
    }

    @FXML
    private void chooseAttachment() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File for Attachment");

        // Set file filter (optional)
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        Stage stage = new Stage();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            attachmentPath.setText(selectedFile.getAbsolutePath());
        }
    }

    public void uploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            imagePathField.setText(selectedFile.getAbsolutePath());
        }
    }


    public void switchBroadcast(ActionEvent event) throws IOException {
        //Parent root = loadFXML(load.(getClass().getResource("broadcast.fxml")));
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("broadcast.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void BroadcastMail(ActionEvent event) throws IOException {
        Broadcast test= new Broadcast();
        String year = graduationYear.getValue();
        String batch = department.getValue();
        String subject = broadsubject.getText();
        String message = broadtext.getText();
        String apath = attachmentPath.getText();

        if (year == null || batch == null || subject.isEmpty() || message.isEmpty()) {
            showAlert("Error", "Please fill all fields.");
            return;
        }

        String[] emails = fetchUsersByYearAndBatch(year, batch);

        if (emails.length == 0) {
            showAlert("Info", "No users found for the specified year and batch.");
            return;
        }

        test.sendEmail(emails,subject,message,apath);

        showAlert("Success", "Broadcast sent successfully!");

    }

    private String[] fetchUsersByYearAndBatch(String year, String batch) {
        List<String> emails = new ArrayList<String>(); // Create a list to store emails

        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("alumni");
            MongoCollection<Document> collection = database.getCollection("info");

            // Build the query based on the selected year and batch
            Document query = new Document();

            if (!"All".equals(year)) {
                query.append("graduationYear", year); // Filter by year if not "All"
            }

            if (!"All".equals(batch)) {
                query.append("department", batch); // Filter by batch if not "All"
            }

            // Query to find users with the specified year and/or batch
            Iterable<Document> users = collection.find(query);

            // Iterate through the users and extract emails
            for (Document user : users) {
                String email = user.getString("email");
                if (email != null && !email.isEmpty()) {
                    emails.add(email); // Add the email to the list
                    System.out.println("Fetched email: " + email);
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching users: " + e.getMessage());
        }

        return emails.toArray(new String[0]);
    }

    public void switchalumniCard(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("alumniCard.fxml"))));
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
    }

    public void switchTopersonalinfo(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("personalinfo.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);

        stage.show();
    }

    public void switchToLogin(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("login2.fxml"))));
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
