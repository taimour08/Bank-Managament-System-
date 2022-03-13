package com.mycompany.atmmanagementsys;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

public class MainScreenController implements Initializable {
    @FXML
    Button user;
    @FXML
    Button admin;

    public void LoadCustomerLogin(ActionEvent event) throws SQLException, IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/Scene1.fxml")); //Loading Customer Screen FXML file
        loader.load();
        Parent root = loader.getRoot();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");
        Stage stage = (Stage) user.getScene().getWindow(); //getting the stage from the button, so we donot made more stages, we would use previous one
        FXMLController upc = loader.getController();
        upc.setScreen("cus"); //here we are telling our next screen that which was our previous screen from which we have loged in
        stage.setMaximized(true);
        stage.setResizable(false);
        stage.setTitle("Customer Login Screen");
        stage.setScene(scene);
        stage.show();
    }

    public void LoadAdminLogin(ActionEvent event) throws SQLException, IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/Scene.fxml")); //loading our employee page
        loader.load();
        Parent root = loader.getRoot();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css"); //applying style sheet
        FXMLController upc = loader.getController();
        upc.setScreen("emp"); //telling our next screen that we have loged in from admin or employee.
        Stage stage = (Stage) admin.getScene().getWindow();
        stage.setResizable(false);
        stage.setMaximized(true);
        stage.setTitle("Employee/Admin Login Screen"); //setting title
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
}
