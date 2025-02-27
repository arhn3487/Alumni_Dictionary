package com.company.alumniloginpage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application

{
    public static void main(String[] args)
    {
        launch();
    }
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root  = FXMLLoader.load(getClass().getResource("login2.fxml"));

        Scene scene =new Scene(root);
        Stage stage=new Stage();

        stage.setTitle("MIST Alumni Portal");

        //stage.setFullScreen(true);
        stage.setResizable(false);

        stage.setScene(scene);
        stage.show();
    }
}
