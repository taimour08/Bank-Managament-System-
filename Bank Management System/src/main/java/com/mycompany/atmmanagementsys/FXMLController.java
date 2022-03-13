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
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

public class FXMLController implements Initializable {
    @FXML
    private TextField userId;
    @FXML
    private PasswordField password;
    @FXML
    private Label wrongUser;
    @FXML
    private Button loginb;
    @FXML
    private Hyperlink forgetPass;
    @FXML
    private Button back;
    private String scr = "";
    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    @FXML
    private boolean isUserExist(String userId) throws SQLException {
        try {
            con = DbConnection.Connection();
            //checking if ther user exist in DB
            ps = con.prepareStatement("SELECT * FROM Users WHERE User_ID = ? ");
            ps.setString(1, userId);
            rs = ps.executeQuery();
            if (rs.next()) //if user exist
                return true;
            return false;//if user donot exist
        } catch (Exception e) {
            System.out.println(e);
        }
        rs.close();
        ps.close();
        con.close();
        return false;
    }

    @FXML
    private void Login(ActionEvent event) throws SQLException, IOException {
        wrongUser.setText(""); //initially there is no message
        con = DbConnection.Connection();
        String str = userId.getText();
        if (str.charAt(0) == 'C') { //if the userID starts with C, it means that its a customer
            if (scr.equals("cus")) //if we are logging in from customer page, we cannot login from employee page
                if (isUserExist(str)) //if user exist in database
                    CustomerPage(); //then load the customer page
                else { //display error
                    wrongUser.setText("Sorry This User Don't Exist");
                }

            else {//we are trying to log in from another login page, like employeee
                wrongUser.setText("Sorry This User Don't Exist");
            }
        } else if (str.charAt(0) == 'M') { //if the id starts from M, it means its manager
            if (scr.equals("emp")) //we are trying to log in from employee/admin page
                if (isUserExist(str)) //user also exist
                    AdminPage(); //load admin page
                else {
                    wrongUser.setText("Sorry This User Don't Exist");

                }
            else {
                wrongUser.setText("Sorry This User Don't Exist");

            }
        } else { //means its employee
            if (scr.equals("emp"))//also trying to log in from employee page
                if (isUserExist(str))  //user also exist
                    EmployeePage(); //load employee page
                else { //display error
                    wrongUser.setText("Sorry This User Don't Exist");

                }
            else {
                wrongUser.setText("Sorry This User Don't Exist");

            }
        }
    }

    private boolean accountIsActive(String user) throws SQLException {
        try {


            con = DbConnection.Connection();
            ps = con.prepareStatement("SELECT * FROM Bank_Account WHERE Cust_ID = ?");
            ps.setString(1, user);
            rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println(rs.getString("Status"));
                if (rs.getString("Status").equals("Active")) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println();
        }
        rs.close();
        ps.close();
        con.close();
        return false;
    }

    @FXML
    //setting the screen name
    public void setScreen(String name) {
        scr = name;
    }

    public void backPressed(ActionEvent event) throws IOException {

        Stage stage = (Stage) back.getScene().getWindow();
        System.out.println(getClass());
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainScreen.fxml")); //loading the Main screen
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/MainScreen.css");
        stage.setMaximized(true);
        stage.setResizable(false);
        stage.setTitle("Main Screen"); //set title
        stage.setScene(scene);
        stage.show();
    }

    private void CustomerPage() throws SQLException, IOException {

        if (accountIsActive(userId.getText())) { //if account is active, then load the customer page
            try {
                String accountBalance = "";
                con = DbConnection.Connection();
                ps = con.prepareStatement("SELECT b.Balance FROM Users u, Bank_Account b WHERE User_ID = ? and u.User_ID = b.Cust_ID");
                ps.setString(1, userId.getText());
                rs = ps.executeQuery();
                if(rs.next()){
                    accountBalance = rs.getString("Balance");
                }
                rs.close();
                ps.close();
                con.close();
                con = DbConnection.Connection();
                ps = con.prepareStatement("SELECT * FROM Users WHERE User_ID = ? and Password = ?");
                ps.setString(1, userId.getText());
                ps.setString(2, password.getText());
                rs = ps.executeQuery();
                if (rs.next()) { //means the user also exist and provided correct password
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/fxml/UserPage.fxml")); //loading the user page
                    loader.load();
                    Parent root = loader.getRoot();
                    UserPageController upc = loader.getController();
                    InputStream is = rs.getBinaryStream("User_Image");
                    Image image = new Image("/icons/edituser.png");
                    if (is != null) { //if there is a picture of the user
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
                    Stage stage = (Stage) loginb.getScene().getWindow();
                    //pass all the information to the next screen, so there it can be used to display all the information about customer
                    upc.GetUserID(userId.getText(), rs.getString("First_Name") + " " + rs.getString("Last_Name"), rs.getString("Phone_No"), rs.getString("Email"), rs.getString("CNIC"), image,accountBalance);
                    stage.setTitle("User Page");
                    stage.setMaximized(true); //always maximized
                    stage.setResizable(false); //cannot resize
                    Scene scene = new Scene(root);
                    scene.getStylesheets().add("/styles/UserPage.css");
                    stage.setScene(scene);
                    stage.show();
                    wrongUser.setText("");
                } else {
                    wrongUser.setText("Wrong Password Or UserID");
                }
            } catch (Exception e) {
                System.out.println(e);
            }

        } else {
            wrongUser.setText("You Are Blocked!!");

        }

        con.close();
    }

    private void AdminPage() throws SQLException, IOException {
        //same as customerPage(), only laoding Admin page instead of customer

        try {
            con = DbConnection.Connection();
            ps = con.prepareStatement("SELECT * FROM Users WHERE User_ID = ? and Password = ?");
            ps.setString(1, userId.getText());
            ps.setString(2, password.getText());
            rs = ps.executeQuery();
            if (rs.next()) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/fxml/AdminPage.fxml")); //loading employee page
                loader.load();
                Parent root = loader.getRoot();
                AdminPageController upc = loader.getController();
                InputStream is = rs.getBinaryStream("User_Image");
                Image image = new Image("/icons/edituser.png");
                if (is != null) { //if there is image of the admin in the database
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
                Stage stage = (Stage) loginb.getScene().getWindow();

                upc.GetUserID(userId.getText(), rs.getString("First_Name") + " " + rs.getString("Last_Name"), rs.getString("Phone_No"), rs.getString("Email"), rs.getString("CNIC"), image);
                stage.setTitle("Admin Page");
                stage.setMaximized(true);//always maximized
                stage.setResizable(false); //cannot resize
                Scene scene = new Scene(root);
                scene.getStylesheets().add("/styles/AdminPage.css");
                stage.setScene(scene);
                stage.show();
                wrongUser.setText("");

            } else {
                wrongUser.setText("Wrong Password Or UserID");
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        con.close();
    }

    private void EmployeePage() throws SQLException, IOException {
        //same as customerPage(), here we are only loading employee page instead of userpage

        try {
            con = DbConnection.Connection();
            ps = con.prepareStatement("SELECT * FROM Users WHERE User_ID = ? and Password = ?");
            ps.setString(1, userId.getText());
            ps.setString(2, password.getText());
            rs = ps.executeQuery();
            if (rs.next()) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/fxml/EmployeePage.fxml")); //loading employee page
                loader.load();
                Parent root = loader.getRoot();
                EmployeePageController upc = loader.getController();
                InputStream is = rs.getBinaryStream("User_Image");
                Image image = new Image("/icons/edituser.png");
                if (is != null) {
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
                Stage stage = (Stage) loginb.getScene().getWindow();
                //passing all the information to the next screen, so it can be used there
                upc.GetUserID(userId.getText(), rs.getString("First_Name") + " " + rs.getString("Last_Name"), rs.getString("Phone_No"), rs.getString("Email"), rs.getString("CNIC"), image);

                stage.setTitle("Employee Page");
                stage.setMaximized(true);
                stage.setResizable(false);
                Scene scene = new Scene(root);
                scene.getStylesheets().add("/styles/EmployeePage.css");
                stage.setScene(scene);
                stage.show();
                wrongUser.setText("");

            } else {
                wrongUser.setText("Wrong Password Or UserID");
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        con.close();
    }

    public void requestNewPassword(ActionEvent event) throws IOException {
        if (scr.equals("cus")) {
            customerForgetPassword(); //if customer screen then load customer Forget password controller
        } else {
            employeeForgetPassword(); //if employee screen then load employee/admin forger password controller
        }

    }

    private void customerForgetPassword() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/forgetPasswordCustomer.fxml")); //loading customer password controller GUI
        loader.load();
        Parent root = loader.getRoot();
        ForgetPasswordController aic = loader.getController();
        aic.setScreen("cus"); //setting screen that we have loaded from customer
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/MainScreen.css");
        Stage stage = (Stage) forgetPass.getScene().getWindow();
        stage.setMaximized(true); //always maximized
        stage.setResizable(false);//cannot resize
        stage.setTitle("Forgot Password"); //setting title
        stage.setScene(scene);
        stage.show();
    }

    private void employeeForgetPassword() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/forgetPasswordEmployee.fxml")); //loading employee forget password
        loader.load();
        Parent root = loader.getRoot();
        ForgetPasswordController aic = loader.getController();
        aic.setScreen("emp"); //setting screen, so we know that we came here from employee forgot password
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/MainScreen.css");
        Stage stage = (Stage) forgetPass.getScene().getWindow();
        stage.setMaximized(true);
        stage.setResizable(false);
        stage.setTitle("Forgot Password");
        stage.setScene(scene);
        stage.show();

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
}
