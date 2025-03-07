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
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

public class Controller
{
    @FXML private TextField name, studentId, batch, workplace, email, linkedin, phone, facebook, address, attachmentPath, imagePathField;
    @FXML private RadioButton male, female;
    @FXML private ComboBox<String> degree, userTypes, department, graduationYear, broad_batch, broad_dept, userType;
    @FXML private TextArea broadtext;
    @FXML private TextField broadsubject;
    @FXML private TableView<AlumniListController.Alumni> alumniTable;
    @FXML private Button helpbutton;
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

    @FXML
    private Label namelbl;

    @FXML
    private ImageView userImageView;

    Stage stage;
    Scene scene;

    private MongoDBConnection mongoDBConnection;
    @FXML
    public void initialize()
    {
        mongoDBConnection = new MongoDBConnection();

        String loggedInUserId = SharedData.getInstance().getLoggedInUserId();

        if (loggedInUserId != null)
        {
            try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017"))
            {
                MongoDatabase database = mongoClient.getDatabase("alumni");
                MongoCollection<Document> collection = database.getCollection("info");

                Document user = collection.find(new Document("studentId", loggedInUserId)).first();

                if (user != null)
                {
                    namelbl.setText(user.getString("name"));

                    String imagePath = user.getString("Image");
                    if (imagePath != null && !imagePath.isEmpty())
                    {
                        javafx.scene.image.Image image = new Image(new File(imagePath).toURI().toString());
                        userImageView.setImage(image);
                    }
                }
                else
                {
                    System.err.println("User not found in the database.");
                }
            }
            catch (Exception e)
            {
                System.err.println("Error loading user data: " + e.getMessage());
            }
        }
        else
        {
            System.err.println("No logged-in user ID found.");
        }

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
        Tooltip helpTooltip = new Tooltip("This is a alumni portal for MIST \n" +
                "If you need any type of help you may contruct with \nemail : arhasan3487@gmail.com\n phone : 01540194651");
        if(helpbutton!=null)
        {
            helpbutton.setTooltip(helpTooltip);
            //helpTooltip.setTooltipDuration(helpTooltip, 500, 10000, 500);
        }
    }

    private void initializeComboBox(ComboBox<String> comboBox, String name, String... values)
    {
        if (comboBox == null)
        {
            System.out.println("Not found any fx-id named " + name);
        }
        else
        {
            comboBox.setItems(FXCollections.observableArrayList(values));
            comboBox.setVisibleRowCount(5);
            System.out.println("Successfully initialized " + name);
        }
    }

    private void showAlert(String title, String message)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void saveinfo()
    {
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

        String dgender = "";
        if (male.isSelected())
        {
            dgender = "Male";
        }
        else if (female.isSelected())
        {
            dgender = "Female";
        }

        String ddegree = degree.getValue();
        String ddepartment = department.getValue();
        String dgraduationYear = graduationYear.getValue();

        //SendOTP sendtheOTP = new SendOTP();
        String generatedOTP = "1234";
        //sendtheOTP.sendOTP(demail, generatedOTP);

        // Prompt the user to enter the OTP
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Confirmation for create user");
        dialog.setHeaderText("An PIN has been assigned for this ID.");
        dialog.setContentText("Please enter the PIN:");

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

                    // Close the current stage (sign-up form)
                    Stage currentStage = (Stage) name.getScene().getWindow();
                    currentStage.close();
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
                        doc.getString("department"),
                        doc.getString("workplace"),
                        doc.getString("email")

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

    public void switchToAdminHome(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("Admin_dashboard.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.setFullScreen(true);
    }

    public void switchtoAlumniListAdmin(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("alumniListAdmin.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    public void switchBroadcast(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("broadcast.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.show();
    }

    @FXML
    public void switchToJobBoardAdmin(ActionEvent event) throws IOException
    {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("jobadmin.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    public void eventadmin(ActionEvent event) throws IOException
    {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("eventadmin.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint(""); // Hide the exit hint message
        stage.show();
    }


    public void switchToHome(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("home.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    public void switchalumniCard(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("alumniCard.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);

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

    public void switchToJobBoard(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("job.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    public void switchtoEvent(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("eventalumni.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.setFullScreen(true);
    }

    public void switchToStudentHome(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("student_home.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.setFullScreen(true);
    }

    public void switchtoStudentIdCard(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("student_id_card.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    public void switchtoAlumniListStudent(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("alumniListStudent.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    public void switchToJobBoardStudent(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("jobstudent.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    public void eventstudent(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("eventstudent.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    public void mistWebsite(ActionEvent event) throws URISyntaxException,IOException{
        Desktop.getDesktop().browse(new URI("https://mist.ac.bd/"));
    }

    @FXML
    public void logOut(ActionEvent event) throws IOException
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Log Out");
        alert.setHeaderText("You're about to logout");
        alert.setContentText("Do you want to save before exiting?");

        if (alert.showAndWait().get() == ButtonType.OK)
        {
            Parent root = FXMLLoader.load(getClass().getResource("login2.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
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


    private Parent loadFXML(String fxmlFileName) throws IOException {
        var resource = getClass().getResource(fxmlFileName);
        if (resource == null) {
            throw new IOException("FXML file not found: " + fxmlFileName);
        }
        return FXMLLoader.load(resource);
    }
}