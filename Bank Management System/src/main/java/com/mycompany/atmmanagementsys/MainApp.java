/*
MIT License
Copyright (c) 2018 Guru
Only for Educational purposes and for reference.
*/
package com.mycompany.atmmanagementsys;

import javafx.application.Application;

import static javafx.application.Application.launch;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import com.email.durgesh.Email;

import javax.mail.MessagingException;


public class MainApp extends Application {


    @Override
    public void start(@org.jetbrains.annotations.NotNull Stage stage) throws Exception {

        System.out.println(getClass());
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainScreen.fxml"));  //Loading the main screen
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/MainScreen.css"); //applying stylesheet
        stage.setMaximized(true); //always maximize the screen
        stage.setResizable(false); //cannot resize the screen
        stage.setTitle("Main Screen"); //setting title to main screen.
        stage.setScene(scene); //setting the scene
        stage.show(); //displaying the stage.

    }

    public static void main(String[] args) {
        launch(args);
    }

}
