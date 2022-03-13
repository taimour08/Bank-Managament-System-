package com.mycompany.atmmanagementsys;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AdminPageController implements Initializable {
    @FXML
    private Button loadcusinfo;
    @FXML
    private Image image;
    @FXML
    private TextField num;
    @FXML
    private Label emailAddress;
    @FXML
    private Label userName;
    @FXML
    private Label cnic;
    @FXML
    private Label phoneNumber;
    @FXML
    String UserID;
    @FXML
    ImageView userImage;
    @FXML
    Button back;
    @FXML
    Button chngPass;
    @FXML
    Button mngEmpBtn;
    @FXML
    Button mngCusBtn;
    @FXML
    Button loanReqBtn;

    // Information is Given From either Login Page to this Page or from a Page That was clicked from this Page
    public void GetUserID(String id, String Name, String phone, String email, String identifier, Image image) throws SQLException {
        UserID = id;
        userName.setText(Name);
        cnic.setText(identifier);
        emailAddress.setText(email);
        phoneNumber.setText(phone);
        System.out.println(image);
        userImage.setImage(image);
    }

    // If back Button pressed at this Page then we go to the previous Page and also send
    // Data like user information to be displayed on the Previous Page here
    public void backPressed(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/Scene.fxml"));
        loader.load();
        Parent root = loader.getRoot();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");
        FXMLController fxm = loader.getController();
        fxm.setScreen("emp");
        Stage stage = (Stage) back.getScene().getWindow();

        // Setting that page Properties like title, Stylesheet etc.
        stage.setResizable(false);
        stage.setMaximized(true);
        stage.sizeToScene();
        stage.setTitle("Employee/Admin Login Screen");
        stage.setScene(scene);
        stage.show();
    }

    // Loan Request button pressed so that Admin can see All the Requests of Loans Made by Customers
    public void loanReqPressed(ActionEvent event) throws IOException
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/LoanController.fxml"));
        loader.load();
        Parent root = loader.getRoot();
        Scene scene = new Scene(root);
        ManagerLoanController aic = loader.getController();
        aic.getUserID(UserID);
        Stage stage = (Stage) loanReqBtn.getScene().getWindow();
        stage.setMaximized(true);
        stage.setResizable(false);
        stage.setTitle("Manage Loans");
        stage.setScene(scene);
        stage.show();
    }

    // Change Password Button Pressed so we will change page so that we can input new password to change
    public void changePassword(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/ChangePassword.fxml"));
        loader.load();
        Parent root = loader.getRoot();
        AccountInfoController aic = loader.getController();
        aic.getUserID(UserID);
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/ChangePassword.css");
        Stage stage = (Stage) chngPass.getScene().getWindow();
        stage.setMaximized(true);
        stage.setResizable(false);
        stage.setTitle("Change Password");
        stage.setScene(scene);
        stage.show();
    }

    // Manage Employee Pressed so we will change page to Show Admin, Table of Current Employees and Buttons to Add, Delete, Edit Employee
    public void manageEmployee(ActionEvent event) throws IOException
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/ManageEmployee.fxml"));
        loader.load();
        Parent root = loader.getRoot();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/AdminPage.css");
        ManageEmployeeController aic = loader.getController();
        aic.getUserID(UserID);
        Stage stage = (Stage) mngEmpBtn.getScene().getWindow();
        stage.setMaximized(true);
        stage.setResizable(false);
        stage.setTitle("Manage Employee");
        stage.setScene(scene);
        stage.show();
    }

    // Manage Customer Pressed so we will change page to Show Admin, Table of Current Customers and Buttons to Add, Delete, Edit Customers
    public void manageCustomer(ActionEvent event) throws IOException
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/ManageCustomer.fxml"));
        loader.load();
        Parent root = loader.getRoot();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/AdminPage.css");
        ManageCustomerController aic = loader.getController();
        aic.getUserID(UserID);
        Stage stage = (Stage) mngCusBtn.getScene().getWindow();
        stage.setResizable(false);
        stage.setFullScreen(true);
        stage.setTitle("Manage Employee");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
}
