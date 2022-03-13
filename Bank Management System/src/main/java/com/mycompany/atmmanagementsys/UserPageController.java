package com.mycompany.atmmanagementsys;

import java.io.IOException;
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
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class UserPageController implements Initializable {
    @FXML
    private Label emailAddress;
    @FXML
    private Label userName;
    @FXML
    private Label cnic;
    @FXML
    private Label accountBalance;
    @FXML
    private Label phoneNumber;
    @FXML
    String name;
    String Cnic;
    String userPhone;
    String userEmail;
    Image img;
    String bal;
    Connection con;
    PreparedStatement ps = null;
    ResultSet rs = null;
    @FXML
    String UserID;
    @FXML
    ImageView userImage;
    @FXML
    Button back;
    @FXML
    Button chngPass;
    @FXML
    Button balanceTransferBtn;
    @FXML
    Button transactionButton;
    @FXML
    Button loanBtn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    //setting all the information for the next page, so we can display the information there.
    public void GetUserID(String id, String Name, String phone, String email, String identifier, Image image, String balance) throws SQLException {
        UserID = id;
        name = Name;
        Cnic = identifier;
        userEmail = email;
        userPhone = phone;
        img = image;
        bal = balance;
        userName.setText(Name);
        cnic.setText(identifier);
        emailAddress.setText(email);
        phoneNumber.setText(phone);
        System.out.println(image);
        userImage.setImage(image);
        accountBalance.setText("Rs. "+bal);

    }

    public void backPressed(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/Scene1.fxml")); //loading Customer login screen
        loader.load();
        Parent root = loader.getRoot();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");
        Stage stage = (Stage) back.getScene().getWindow();
        stage.setResizable(false); //cannot resize
        stage.setMaximized(true); //always maximized
        FXMLController fxm = loader.getController();
        fxm.setScreen("cus"); //telling previous screen that we have came back from customer screen. So we can use it for forgetPassword GUI
        stage.setTitle("Customer Login Screen");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void ChangePassword(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/ChangePassword.fxml")); //loading the change password screen
        loader.load();
        Parent root = loader.getRoot();
        AccountInfoController aic = loader.getController();
        aic.getUserID(UserID); //setting the userId, so we know for which user we are changing password
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/ChangePassword.css");
        Stage stage = (Stage) chngPass.getScene().getWindow();
        stage.setResizable(false);//cannot resize
        stage.setMaximized(true); //always on maximized
        stage.setTitle("Change Password");
        stage.setScene(scene);
        stage.show();
    }

    public void balanceTransfer(ActionEvent event) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/BalanceTransfer.fxml")); //loading the balance transfer screen
        loader.load();
        Parent root = loader.getRoot();
        Scene scene = new Scene(root);
        Stage stage = (Stage) balanceTransferBtn.getScene().getWindow();
        stage.setMaximized(true);
        stage.setResizable(false);
        BalanceTransferController fxm = loader.getController();
        fxm.GetUserID(UserID, name, userPhone, userEmail, Cnic, img,bal); //since its on the same stage, so we have to display the same information on the next screen as well
        stage.setTitle("Balance Transfer Screen");
        stage.setScene(scene);
        stage.show();
    }

    public void transactionPressed(ActionEvent event) throws IOException, SQLException {
        String account = "";

        try {
            con = DbConnection.Connection();
            System.out.println(UserID);
            //extracting the accountNumber for the specific user ID, so we will perform all the operations based on the accountNumber
            ps = con.prepareStatement("SELECT * FROM Bank_Account where Cust_ID = '" + UserID + "'");
            rs = ps.executeQuery();
            rs.next();
            account = rs.getString("Account_No");

        } catch (Exception e) {
            System.out.println(e);
        }
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/ShowTransactions.fxml")); //loading the transaction GUI,, it will display all the transactions for specific customer
        loader.load();
        Parent root = loader.getRoot();
        Scene scene = new Scene(root);
        Stage stage = (Stage) transactionButton.getScene().getWindow();
        stage.setMaximized(true); //always maximized
        TransactionController fxm = loader.getController();
        fxm.getUserID(UserID, account); //setting userID and account number, so we can get back to this screen and we can view all the transaction for this accountNumber
        stage.setResizable(false);//cannot resize
        stage.setTitle("Transaction Screen");
        stage.setScene(scene);
        stage.show();
    }

    public void loanPressed(ActionEvent event) throws IOException, SQLException {
        String account = "";
        System.out.println(UserID);

        try {
            con = DbConnection.Connection();
            ps = con.prepareStatement("SELECT * FROM Bank_Account where Cust_ID = '" + UserID + "'");
            rs = ps.executeQuery();
            rs.next();
            account = rs.getString("Account_No");

        } catch (Exception e) {
            System.out.println(e);
        }
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/UserLoanController.fxml"));
        loader.load();
        Parent root = loader.getRoot();
        Scene scene = new Scene(root);
        Stage stage = (Stage) loanBtn.getScene().getWindow();
        stage.setMaximized(true);
        stage.setResizable(false);
        UserLoanController fxm = loader.getController();
        System.out.println(account);
        fxm.getUserID(UserID, account);
        stage.setTitle("Loan Screen");
        stage.setScene(scene);
        stage.show();
        rs.close();
        ps.close();
        con.close();
    }

}







