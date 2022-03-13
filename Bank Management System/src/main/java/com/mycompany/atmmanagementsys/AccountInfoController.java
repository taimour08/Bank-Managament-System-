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
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class AccountInfoController implements Initializable {

    String UserID;

    @FXML
    private PasswordField oldpass;
    @FXML
    private PasswordField newpass;
    @FXML
    private PasswordField passretype;
    @FXML
    private Label changepassconf;
    @FXML
    Button back;
    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    public void getUserID(String Id) {
        UserID = Id;
    }

    // When User Wants to Change to a new Password
    public void ChangePassword(ActionEvent event) throws SQLException {
        try {
            if (!newpass.getText().equals(passretype.getText()))  // Here we are checking if Password is correctly Entered Twice
            {
                changepassconf.setText("Password Confirmation Failed");
                passretype.setText("");
                passretype.setStyle("-fx-border-color:red;-fx-border-width:2;-fx-border-radius:20;-fx-background-radius:22;");
            } else if (oldpass.getText().isEmpty() || newpass.getText().isEmpty() || passretype.getText().isEmpty()) // Here we are checking if any field was left empty
            {
                changepassconf.setText("Please Fill Up Everything Correctly.");
            } else // Everything was correctly Entered and Now we are changing password to new password in DB
            {
                con = DbConnection.Connection();
                ps = con.prepareStatement("UPDATE Users SET Password = ? WHERE User_ID ='" + UserID + "' AND Password ='" + oldpass.getText() + "'");
                ps.setString(1, newpass.getText());
                int i = ps.executeUpdate();
                if (i > 0) // If old password Entered was correctly Verified from DB meaning it exists
                {
                    changepassconf.setText("Password Changed.");
                } else // Otherwise Wrong old Password Entered Msg is Displayed
                {
                    changepassconf.setText("Wrong Password.");
                }
                // Emptying the Input Field if User wants to Enter again a New password.
                oldpass.setText("");
                newpass.setText("");
                passretype.setText("");
                passretype.setStyle("-fx-border-color: #3b5998;-fx-border-width:2;-fx-border-radius:20;-fx-background-radius:22;");

            }
        }
        catch (Exception e){
            System.out.println(e);
        }
        con.close();
    }

    // If back Button pressed at this Page then we go to the previous Page and also send
    // Data like user information to be displayed on the Previous Page here
    public void backPressed(ActionEvent event) throws IOException, SQLException {
        try {


            if (UserID.charAt(0) == 'C') // If user was of Customer Type the collect data for Customer User ID
            {
                String accountBalance = "";
                con = DbConnection.Connection();
                ps = con.prepareStatement("SELECT b.Balance FROM Users u, Bank_Account b WHERE User_ID = ? and u.User_ID = b.Cust_ID");
                ps.setString(1, UserID);
                rs = ps.executeQuery();
                if(rs.next()){
                    accountBalance = rs.getString("Balance");
                }
                rs.close();
                ps.close();
                con.close();
                con = DbConnection.Connection();
                ps = null;
                ps = con.prepareStatement("SELECT * FROM Users WHERE User_ID = ?");
                ps.setString(1, UserID);
                rs = ps.executeQuery();
                if (rs.next()) // That Customer Exists
                {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/fxml/UserPage.fxml"));

                    loader.load();
                    Parent root = loader.getRoot();
                    UserPageController upc = loader.getController();
                    InputStream is = rs.getBinaryStream("User_Image");
                    Image image = new Image("/icons/edituser.png");
                    if (is != null) // if PNG image found then display that image
                    {
                        OutputStream os = new FileOutputStream(new File("photo.jpg"));
                        byte[] content = new byte[1024];
                        int size = 0;
                        while ((size = is.read(content)) != -1) {
                            os.write(content, 0, size);
                        }
                        os.close();
                        is.close();
                        image = new Image("file:photo.jpg", 250, 250, true, true);
                    }
                    Stage stage = (Stage) back.getScene().getWindow();
                    upc.GetUserID(UserID, rs.getString("First_Name") + " " + rs.getString("Last_Name"), rs.getString("Phone_No"), rs.getString("Email"), rs.getString("CNIC"), image,accountBalance);

                    // Setting that page Properties like title, Stylesheet etc.
                    stage.setTitle("Customer Page");
                    stage.setMaximized(true);
                    stage.setResizable(false);
                    Scene scene = new Scene(root);
                    scene.getStylesheets().add("/styles/UserPage.css");
                    stage.setScene(scene);
                    stage.show();
                }
            }
            if (UserID.charAt(0) == 'E') // if User was Employee then get Employee Data
            {
                con = DbConnection.Connection();
                ps = null;
                ps = con.prepareStatement("SELECT * FROM Users WHERE User_ID = ?");
                ps.setString(1, UserID);
                rs = ps.executeQuery();
                if (rs.next()) // Employee Exists in DB
                {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/fxml/EmployeePage.fxml"));
                    loader.load();
                    Parent root = loader.getRoot();
                    EmployeePageController upc = loader.getController();
                    InputStream is = rs.getBinaryStream("User_Image");
                    Image image = new Image("/icons/edituser.png");
                    if (is != null) // if PNG image found then display that image
                    {
                        OutputStream os = new FileOutputStream(new File("photo.jpg"));
                        byte[] content = new byte[1024];
                        int size = 0;
                        while ((size = is.read(content)) != -1) {
                            os.write(content, 0, size);
                        }
                        os.close();
                        is.close();
                        image = new Image("file:photo.jpg", 250, 250, true, true);
                    }
                    Stage stage = (Stage) back.getScene().getWindow();
                    upc.GetUserID(UserID, rs.getString("First_Name") + " " + rs.getString("Last_Name"), rs.getString("Phone_No"), rs.getString("Email"), rs.getString("CNIC"), image);

                    // Setting that page Properties like title, Stylesheet etc.
                    stage.setTitle("User Page");
                    stage.setMaximized(true);
                    stage.setResizable(false);
                    Scene scene = new Scene(root);
                    scene.getStylesheets().add("/styles/EmployeePage.css");
                    stage.setScene(scene);
                    stage.show();
                }
            } else // Else User was Definitely Manager
            {
                con = DbConnection.Connection();
                ps = null;
                ps = con.prepareStatement("SELECT * FROM Users WHERE User_ID = ?");
                ps.setString(1, UserID);
                rs = ps.executeQuery();
                if (rs.next()) // That Manager Exists in the DB
                {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/fxml/AdminPage.fxml"));
                    loader.load();
                    Parent root = loader.getRoot();
                    AdminPageController upc = loader.getController();
                    InputStream is = rs.getBinaryStream("User_Image");
                    Image image = new Image("/icons/edituser.png");
                    if (is != null) // if PNG image found then display that image
                    {
                        OutputStream os = new FileOutputStream(new File("photo.jpg"));
                        byte[] content = new byte[1024];
                        int size = 0;
                        while ((size = is.read(content)) != -1) {
                            os.write(content, 0, size);
                        }
                        os.close();
                        is.close();
                        image = new Image("file:photo.jpg", 250, 250, true, true);
                    }
                    Stage stage = (Stage) back.getScene().getWindow();
                    upc.GetUserID(UserID, rs.getString("First_Name") + " " + rs.getString("Last_Name"), rs.getString("Phone_No"), rs.getString("Email"), rs.getString("CNIC"), image);

                    // Setting that page Properties like title, Stylesheet etc.
                    stage.setTitle("Admin Page");
                    stage.setMaximized(true);
                    stage.setResizable(false);
                    Scene scene = new Scene(root);
                    scene.getStylesheets().add("/styles/AdminPage.css");
                    stage.setScene(scene);
                    stage.show();
                }
            }

        }
        catch (Exception e){
            System.out.println(e);
        }
        // Closing Connection with DB to not get Errors like DB Busy or any other errors like this.

        con.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
}
