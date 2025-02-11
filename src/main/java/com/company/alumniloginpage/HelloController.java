package com.company.alumniloginpage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class HelloController {

    @FXML
    private void loginbuttononaction(ActionEvent e) {
        System.out.println("Login button clicked!");

        try {
            // Load the new scene
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/company/alumniloginpage/dashboard.fxml")));
            Scene scene = new Scene(root);

            // Get the current stage
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
