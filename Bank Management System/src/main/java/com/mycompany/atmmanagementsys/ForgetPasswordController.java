package com.mycompany.atmmanagementsys;

import com.email.durgesh.Email;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Random;
import java.util.ResourceBundle;

public class ForgetPasswordController implements Initializable {
    @FXML
    Button submit;
    @FXML
    Button back;
    @FXML
    private Label wrongUser;
    @FXML
    private TextField emailAddress;
    @FXML
    private TextField accountNo;
    String scr = "";
    String pass;
    String email;
    Connection con;
    PreparedStatement ps = null;
    ResultSet rs = null;

    @FXML
    public void submitPressed(ActionEvent event) throws IOException, SQLException {
        if (scr.equals("cus")) { //if  the email is submitted from Customer Page.
            if (checkEmailCustomer()) { //if email exist in the database
                pass = getRandomPassword(); //get random password
                updatePassword(email, pass); //update password in the database
                sendEmail(pass, emailAddress.getText()); //send the email to this email address about new password

            }
        } else { //if the email is submitted from admin/employee page
            if (checkEmailEmployee()) {
                pass = getRandomPassword();
                sendEmail(pass, emailAddress.getText());
                updatePassword(email, pass);

            }
        }
    }

    private String getRandomPassword() {
        //generate random password
        int min = 1;
        int max = 10000;
        int random_int1 = (int) Math.floor(Math.random() * (max - min + 1) + min);
        int random_int2 = (int) Math.floor(Math.random() * (max - min + 1) + min);

        return String.valueOf(random_int1) + String.valueOf(random_int2);
    }

    private void updatePassword(String email, String Password) throws SQLException {
        try {
            //updating the password in database
            con = DbConnection.Connection();
            ps = con.prepareStatement("UPDATE Users SET Password = ? WHERE Email = '" + email + "'");
            ps.setString(1, Password);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }
        con.close();
    }

    private boolean checkEmailCustomer() throws SQLException {

        try {
            boolean bool = false; //for properly closing connection
            con = DbConnection.Connection();
            ps = con.prepareStatement("SELECT * FROM Users WHERE Email = ? and User_Type = 'C' ");
            ps.setString(1, emailAddress.getText());
            rs = ps.executeQuery();
            if (rs.next()) { //if there is a email in the database
                wrongUser.setText("Please Check your email");
                email = emailAddress.getText();
            } else { //if email isn't found in database.
                wrongUser.setText("Sorry!! This Email Don't Exist");
                bool = true;

            }
            if (bool) {
                con.close();
                return false;
            } else {

                con.close();
                return true;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        con.close();
        return false;
    }

    private boolean checkEmailEmployee() throws SQLException {
        //same as checkEmailCustomer, only difference is this time we have employee email

        try {
            boolean bool = false;
            con = DbConnection.Connection();
            ps = con.prepareStatement("SELECT * FROM Users WHERE Email = ? and User_Type != 'C'  ");
            ps.setString(1, emailAddress.getText());
            rs = ps.executeQuery();
            if (rs.next()) {
                wrongUser.setText("Please Check your email");
                email = emailAddress.getText();

            } else {
                wrongUser.setText("Sorry!! This Email Don't Exist");
                bool = true;

            }
            if (bool) {

                con.close();
                return false;
            } else {

                con.close();
                return true;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        con.close();
        return false;
    }

    //setting the screen name, so we know whether its a customer or employee/manager
    public void setScreen(String name) {
        scr = name;
    }

    public void backPressed(ActionEvent event) throws IOException {
        if (scr.equals("emp")) { //if we have came from employee, then we have to go back to employee page
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/Scene.fxml")); //loading employee login page
            loader.load();
            Parent root = loader.getRoot();
            Scene scene = new Scene(root);
            scene.getStylesheets().add("/styles/Styles.css");
            Stage stage = (Stage) back.getScene().getWindow();
            stage.setMaximized(true); //always maximized
            FXMLController aic = loader.getController();
            aic.setScreen("emp"); //setting screen name
            stage.setResizable(false);//cannot be resized
            stage.setTitle("Employee Login Screen");
            stage.setScene(scene);
            stage.show();
        } else { //means we came here from customer page, so now we will go back to customer page.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/Scene1.fxml")); //loading customer page
            loader.load();
            Parent root = loader.getRoot();
            Scene scene = new Scene(root);
            scene.getStylesheets().add("/styles/Styles.css");
            Stage stage = (Stage) back.getScene().getWindow();
            stage.setMaximized(true);//always maximized
            FXMLController aic = loader.getController();
            aic.setScreen("cus");
            stage.setResizable(false); //cannot be resized
            stage.setTitle("Customer Login Screen");
            stage.setScene(scene);
            stage.show();
        }
    }

    public void sendEmail(String password, String recEmail) {
        try {
            Email email = new Email("softw697@gmail.com", "Nuisb123"); //sender email and passowrd
            email.setFrom("softw697@gmail.com", "Software Engineering");
            email.setSubject("New Password");
            System.out.println(password);
            System.out.println(recEmail);

            email.setContent(password, "text/html");
            email.addRecipient(recEmail); //recipient email
            email.send();

        } catch (MessagingException | UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
