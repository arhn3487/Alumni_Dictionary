package com.company.alumniloginpage;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import org.bson.Document;

import javax.mail.*;
import javax.mail.internet.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;


public class Controller {

    @FXML private TextField name, studentId, batch, workplace, email, linkedin, phone, facebook, address, attachmentPath, imagePathField;
    @FXML private RadioButton male, female;
    @FXML private ComboBox<String> degree, userTypes, department, graduationYear, broad_batch, broad_dept, userType;
    @FXML private TextArea broadtext;
    @FXML private TextField broadsubject;
    @FXML private TableView<AlumniListController.Alumni> alumniTable;
    @FXML
    private ListView<Event> eventListView;

    @FXML
    private TextField eventTitle;

    @FXML
    private DatePicker eventDate;

    @FXML
    private TextField eventLocation;

    @FXML
    private TextArea eventDescription;

    @FXML
    private Button addEventButton;

    @FXML
    private Button removeEventButton;

    @FXML
    private Button clearButton;

    Stage stage;
    Scene scene;

    private MongoDBConnection mongoDBConnection;
    @FXML
    public void initialize() {
        mongoDBConnection = new MongoDBConnection();

        initializeComboBox(degree, "degree", "BSc", "MSc");
        initializeComboBox(department, "department", "All", "CSE", "EECE", "CE", "ME", "AE", "EWCE", "PME", "NAME", "IPE", "BME", "ARCH", "NSE");
        initializeComboBox(graduationYear, "graduationYear", "All", "2002", "2003", "2004", "2005", "2006", "2007", "2008", "2009", "2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022", "2023");
        initializeComboBox(broad_dept, "broad_dept", "CSE", "EECE", "CE", "ME", "AE", "EWCE", "PME", "NAME", "IPE", "BME", "ARCH", "NSE");
        initializeComboBox(broad_batch, "broad_batch", "2002", "2003", "2004", "2005", "2006", "2007", "2008", "2009", "2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022", "2023");
        initializeComboBox(userTypes, "userTypes", "2002", "2003", "2004", "2005", "2006", "2007", "2008", "2009", "2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022", "2023");

        // Uncomment if needed
        // initializeComboBox(userType, "userType", "Admin", "Alumni", "Student");

        // Uncomment if TableView columns need to be set up
        // setupTableView();
    }

    private void initializeComboBox(ComboBox<String> comboBox, String name, String... values) {
        if (comboBox == null) {
            System.out.println("Not found any fx-id named " + name);
        } else {
            comboBox.setItems(FXCollections.observableArrayList(values));
            comboBox.setVisibleRowCount(5);
            System.out.println("Successfully initialized " + name);
        }
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
        String duserType=userType.getValue();

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
                    userData.append("usertype",duserType);

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
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("broadcast.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.setFullScreen(true);
    }

    public void switchtoEvent(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("event.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.setFullScreen(true);
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
        stage.setFullScreen(true);
    }

    public void switchToSignupForm(ActionEvent event) throws IOException
    {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("signupForm.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.setFullScreen(true);
    }

    public void switchalumniList(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("alumniList.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.setFullScreen(true);
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
        userTypes.getItems().addAll("Admin", "Student", "Alumni");
        stage.show();
    stage.setFullScreen(true);
    }

    public void switchToHome(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("home.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    public void switchToStudentHome(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("student_home.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.setFullScreen(true);
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