package com.company.alumniloginpage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/company/alumniloginpage/scene1.fxml"));

        Scene scene = new Scene(root);
        primaryStage.setTitle("This is a program for alumni of MIST");

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
