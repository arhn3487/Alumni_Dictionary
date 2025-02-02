package com.company.alumniloginpage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application
{
    public static void main(String[] args){
        launch();
    }
    @Override
    public void start(Stage primaryStage) throws Exception{
        //Group root=new Group();
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));

        Scene scene=new Scene(root, Color.BLACK);
        Stage stage=new Stage();
        //Image icone = new Image("login_page1.png");
        stage.setTitle("This is a programe for allumni of MIST");
        //stage.setFullScreen(true);

        stage.setScene(scene);
        stage.show();
    }
}
