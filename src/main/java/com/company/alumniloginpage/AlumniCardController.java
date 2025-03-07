package com.company.alumniloginpage;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.bson.Document;

import javax.imageio.ImageIO;
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.nio.file.Files;

// Remove this import: import javafx.embed.swing.SwingFXUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class AlumniCardController {
    private Stage stage;
    private Scene scene;
    private String selectedFormat = "PNG"; // Default format

    @FXML
    private AnchorPane alumniCardPane;

    @FXML
    private ImageView userImageView;

    @FXML
    private Label namelbl;

    @FXML
    private Label studentIdlbl;

    @FXML
    private Label departmentlbl;

    @FXML
    private Label batchlbl;

    @FXML
    private Label cardnamelbl;

    @FXML
    private ImageView carduserImageView;

    @FXML
    private Button pngFormatButton;

    @FXML
    private Button pdfFormatButton;

    @FXML
    private Button verifyInfoButton;

    @FXML
    public void initialize() {
        String loggedInUserId = SharedData.getInstance().getLoggedInUserId();

        if (loggedInUserId != null) {
            loadUserData(loggedInUserId);
        } else {
            System.err.println("No logged-in user ID found.");
        }

        // Set up button actions for verification
        verifyInfoButton.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Verified");
            alert.setHeaderText("ID Card Information Verified");
            alert.setContentText("Your ID card information has been verified. You can now download your ID card.");
            alert.showAndWait();
        });
    }

    private void loadUserData(String userId) {
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("alumni");
            MongoCollection<Document> collection = database.getCollection("info");

            Document user = collection.find(new Document("studentId", userId)).first();

            if (user != null) {
                namelbl.setText(user.getString("name"));
                cardnamelbl.setText(user.getString("name"));
                studentIdlbl.setText(user.getString("studentId"));
                departmentlbl.setText(user.getString("department"));
                batchlbl.setText(user.getString("batch"));

                String imagePath = user.getString("Image");
                if (imagePath != null && !imagePath.isEmpty()) {
                    Image image = new Image(new File(imagePath).toURI().toString());
                    userImageView.setImage(image);
                    carduserImageView.setImage(image);
                }
            } else {
                System.err.println("User not found in the database.");
            }
        } catch (Exception e) {
            System.err.println("Error loading user data: " + e.getMessage());
        }
    }

    @FXML
    public void selectPngFormat() {
        selectedFormat = "PNG";
        pngFormatButton.getStyleClass().add("format-active");
        pdfFormatButton.getStyleClass().remove("format-active");
    }

    @FXML
    public void selectPdfFormat() {
        selectedFormat = "PDF";
        pdfFormatButton.getStyleClass().add("format-active");
        pngFormatButton.getStyleClass().remove("format-active");
    }

    @FXML
    public void handleDownload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Student ID Card");

        // Set initial file name with student ID
        String fileName = "MIST_ID_" + studentIdlbl.getText();

        if ("PNG".equals(selectedFormat)) {
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PNG Files", "*.png"));
            fileChooser.setInitialFileName(fileName + ".png");

            File file = fileChooser.showSaveDialog(alumniCardPane.getScene().getWindow());
            if (file != null) {
                try {
                    // Take snapshot of the card pane
                    WritableImage writableImage = alumniCardPane.snapshot(new SnapshotParameters(), null);

                    // Save as PNG using pure JavaFX
                    saveImageToPng(writableImage, file);

                    showSuccessAlert("PNG");
                } catch (IOException e) {
                    showErrorAlert(e);
                }
            }
        } else {
            // PDF format
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            fileChooser.setInitialFileName(fileName + ".pdf");

            File file = fileChooser.showSaveDialog(alumniCardPane.getScene().getWindow());
            if (file != null) {
                try {
                    // Take snapshot of the card pane
                    WritableImage writableImage = alumniCardPane.snapshot(new SnapshotParameters(), null);

                    // Create a temporary PNG file
                    File tempFile = File.createTempFile("temp_id_card", ".png");
                    saveImageToPng(writableImage, tempFile);

                    // Create PDF using PDFBox
                    PDDocument document = new PDDocument();
                    PDPage page = new PDPage(PDRectangle.A4);
                    document.addPage(page);

                    // Use the temp file to create PDImageXObject
                    BufferedImage bufferedImage = ImageIO.read(tempFile);
                    PDImageXObject pdImage = LosslessFactory.createFromImage(document, bufferedImage);

                    // Calculate positioning to center image on page
                    float pageWidth = page.getMediaBox().getWidth();
                    float pageHeight = page.getMediaBox().getHeight();
                    float imageWidth = pdImage.getWidth();
                    float imageHeight = pdImage.getHeight();

                    // Scale to fit page width (80%)
                    float scale = (pageWidth * 0.8f) / imageWidth;
                    float scaledWidth = imageWidth * scale;
                    float scaledHeight = imageHeight * scale;

                    // Center on page
                    float x = (pageWidth - scaledWidth) / 2;
                    float y = (pageHeight - scaledHeight) / 2;

                    // Draw image on the page
                    PDPageContentStream contentStream = new PDPageContentStream(document, page);
                    contentStream.drawImage(pdImage, x, y, scaledWidth, scaledHeight);
                    contentStream.close();

                    // Save PDF
                    document.save(file);
                    document.close();

                    // Delete temporary file
                    Files.deleteIfExists(tempFile.toPath());

                    showSuccessAlert("PDF");
                } catch (IOException e) {
                    showErrorAlert(e);
                }
            }
        }
    }

    // Method to save JavaFX WritableImage to PNG without using Swing
    private void saveImageToPng(WritableImage writableImage, File file) throws IOException {
        // Get image dimensions
        int width = (int) writableImage.getWidth();
        int height = (int) writableImage.getHeight();

        // Create a BufferedImage manually
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // Get pixel reader from JavaFX image
        PixelReader pixelReader = writableImage.getPixelReader();

        // Copy pixels from JavaFX image to BufferedImage
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                javafx.scene.paint.Color color = pixelReader.getColor(x, y);

                // Convert JavaFX color to AWT compatible int
                int argb = ((int) (color.getOpacity() * 255) << 24) |
                        ((int) (color.getRed() * 255) << 16) |
                        ((int) (color.getGreen() * 255) << 8) |
                        ((int) (color.getBlue() * 255));

                bufferedImage.setRGB(x, y, argb);
            }
        }

        // Save the BufferedImage as PNG
        ImageIO.write(bufferedImage, "png", file);
    }

    private void showSuccessAlert(String format) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Download Successful");
        alert.setHeaderText("Student ID Card Downloaded");
        alert.setContentText("Your Student ID card has been successfully saved as " + format + " file.");
        alert.showAndWait();
    }

    private void showErrorAlert(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Download Error");
        alert.setHeaderText("Error Downloading ID Card");
        alert.setContentText("An error occurred: " + e.getMessage());
        e.printStackTrace();
        alert.showAndWait();
    }

    public void switchToAdminHome(ActionEvent event) throws IOException
    {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("Admin_dashboard.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.setFullScreen(true);
    }

    public void switchtoAlumniListAdmin(ActionEvent event) throws IOException
    {
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
        stage.show();
    }

    public void switchalumniCard(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("alumniCard.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    public void switchalumniList(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("alumniList.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToJobBoard(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("job.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.show();
    }

    public void switchtoEvent(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("eventalumni.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToStudentHome(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("student_home.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
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

    public void mistWebsite(ActionEvent event) throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI("https://mist.ac.bd/"));
    }

    public void switchToSignupForm(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("signupForm.fxml"))));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
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
}