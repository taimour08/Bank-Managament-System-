package com.mycompany.atmmanagementsys;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class BalanceTransferController implements Initializable {
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
    private Label accountBalance;
    @FXML
    private Label phoneNumber;
    @FXML
    String UserID;
    @FXML
    ImageView userImage;
    @FXML
    Button back;
    @FXML
    Button deposit;
    @FXML
    Button withdrawCashBtn;
    @FXML
    TextField accountNo;
    @FXML
    TextField transferAmount;
    @FXML
    Label wrongUser;
    Connection con;
    PreparedStatement ps;
    ResultSet rs;
    String UserID1 = "";
    String bal ;

    // This is Used when Button for Transfer is Pressed
    private String getConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Alert");
        ButtonType type = new ButtonType("OK");
        alert.setContentText("This is a Confirmation alert");
        alert.showAndWait();
        return alert.getResult().getText();
    }

    // Checking in DB if a given Account is Active or Not
    private boolean accountIsActive(String account) throws SQLException {
        try {
            boolean bool = false; //closing connection smoothely
            con = DbConnection.Connection();
            ps = con.prepareStatement("SELECT * FROM Bank_Account WHERE Account_No = ?");
            ps.setString(1, account);
            rs = ps.executeQuery();
            if (rs.next()) // Account is Found
            {
                System.out.println(rs.getString("Status"));
                if (rs.getString("Status").equals("Active")) // Account was Active
                {
                } else // Account was Blocked
                {
                    bool = true;
                }
            } else // Account was not Found
            {
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

    // Deduct Cash From Account of person who is Transferring Cash
    private void deductCash(String id, int amount) throws SQLException {
        try {
            con = DbConnection.Connection();
            ps = con.prepareStatement("SELECT * FROM Bank_Account WHERE Cust_ID = ?");
            ps.setString(1, id);
            rs = ps.executeQuery();
            if (rs.next()) // Customer is Found
            {
                amount = (rs.getInt("Balance") - (amount));
                ps = con.prepareStatement("UPDATE Bank_Account SET Balance =? WHERE Cust_ID = '" + id + "'");
                ps.setInt(1, amount);
                ps.executeUpdate();  // That new Amount is Set to That Customer Account in DB
                wrongUser.setText("Cash Has Been Transfered\n New Balance: " + amount); // Displaying Confirmation Msg for New Balance
            } else {
                wrongUser.setText("Some Technical Problems, Please Try Again");
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        con.close();
    }

    // Adding cash to Account who was Transferred Money
    private void addCash(String account, int amount) throws SQLException {
        try {
            con = DbConnection.Connection();
            ps = con.prepareStatement("SELECT * FROM Bank_Account WHERE Account_No = ?");
            ps.setString(1, account);
            rs = ps.executeQuery();
            if (rs.next()) // That Account Exists
            {
                amount = (rs.getInt("Balance") + (amount));
                UserID1 = rs.getString("Cust_ID");
                ps = con.prepareStatement("UPDATE Bank_Account SET Balance =? WHERE Account_No = '" + account + "'");
                ps.setInt(1, amount);
                ps.executeUpdate(); // Setting New Balance to That Account
            } else {
                wrongUser.setText("Some Technical Problems, Please Try Again");
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        con.close();
    }

    // Add this Transfer
    private void addToTransactionHistory(String type, int amount, String id, String id1) throws SQLException {
        //extracting current date and time
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String todayDate = formatter.format(date);
        formatter = new SimpleDateFormat(" HH:mm:ss");
        String time = formatter.format(date);
        int transId = 0;
        try {
            con = DbConnection.Connection();
            ps = con.prepareStatement("SELECT * FROM Transaction_History");
            rs = ps.executeQuery();
            while (rs.next()) { //extracting the latest transaction ID, and we increment it by 1 to insert a new record this act as a auto trigger
                transId = rs.getInt("Transaction_ID");
            }
            if (type.equals("Withdraw")) {
                PreparedStatement ps = con.prepareStatement("INSERT INTO Transaction_History VALUES (?,?,?,?,?,?,?,?)");
                ps.setInt(1, (transId + 1));
                ps.setString(2, id);
                ps.setString(3, "-");
                ps.setString(4, "Withdraw");
                ps.setInt(5, (amount));
                ps.setString(6, todayDate);
                ps.setString(7, time);
                ps.setString(8, id1);
                ps.executeUpdate();

            } else if (type.equals("Deposit")) {
                PreparedStatement ps = con.prepareStatement("INSERT INTO Transaction_History VALUES (?,?,?,?,?,?,?,?)");
                ps.setInt(1, (transId + 1));
                ps.setString(2, id);
                ps.setString(3, "-");
                ps.setString(4, "Deposit");
                ps.setInt(5, (amount));
                ps.setString(6, todayDate);
                ps.setString(7, time);
                ps.setString(8, id1);
                ps.executeUpdate();
            } else {
                PreparedStatement ps = con.prepareStatement("INSERT INTO Transaction_History VALUES (?,?,?,?,?,?,?,?)");
                ps.setInt(1, (transId + 1));
                ps.setString(2, id);
                ps.setString(3, id1);
                ps.setString(4, "Transfer");
                ps.setInt(5, (amount));
                ps.setString(6, todayDate);
                ps.setString(7, time);
                ps.setString(8, "-");
                ps.executeUpdate();
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        con.close();
    }

    private boolean userHasAmount(String id, int amount) throws SQLException {

        try {
            boolean bool = false;
            con = DbConnection.Connection();
            ps = con.prepareStatement("SELECT * FROM Bank_Account WHERE Cust_ID = ?");
            ps.setString(1, id);
            rs = ps.executeQuery();
            if (rs.next()) { // if there is a customer with this customerID
                int NewBalance = (rs.getInt("Balance") - amount);
                if (NewBalance >= 0) { //calculate the new balance, if new balance is within his account balance, so return true

                } else {
                    bool = true;
                }
            }
            else{
                bool = true;
            }
            if(bool){

                con.close();
                return false;
            }
            else{

                con.close();
                return true;
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        con.close();
        return false;
    }

    //setting all the information so we can use it to display the information on this page, as well as preivous or next page.
    public void GetUserID(String id, String Name, String phone, String email, String identifier, Image image,String balance) throws SQLException {
        UserID = id;
        userName.setText(Name);
        cnic.setText(identifier);
        emailAddress.setText(email);
        phoneNumber.setText(phone);
        System.out.println(image);
        userImage.setImage(image);
        accountBalance.setText("Rs. "+balance);


    }

    public void transferCashPressed(ActionEvent event) throws IOException, SQLException {
        //this function is called when user press transfer button
        wrongUser.setText(""); //initially there is no message
        if (getConfirmation().equals("OK")) {
            try {
                //if its not a valid input
                if (transferAmount.getText().isEmpty() || Integer.parseInt(transferAmount.getText()) < 0) {
                    wrongUser.setText("Please Enter A Valid Amount");
                } else {
                    if (accountIsActive(accountNo.getText())) { //if the account is active
                        if (userHasAmount(UserID, Integer.parseInt(transferAmount.getText()))) { //if user has the available amount in his account
                            //perform these three functions to complete a balance transfer,
                            //we deduct the cash from the user that have transfered the amount
                            deductCash(UserID, Integer.parseInt(transferAmount.getText()));
                            //we add the cash to the user for which accountNo is given.
                            addCash(accountNo.getText(), Integer.parseInt(transferAmount.getText()));
                            //then add this transaction to transanction history
                            addToTransactionHistory("Transfer", Integer.parseInt(transferAmount.getText()), UserID, UserID1);
                        } else {
                            wrongUser.setText("You Do Not Have Enough Cash!!");
                        }
                    } else {
                        wrongUser.setText("The Account is not Available");
                    }

                }
            } catch (NumberFormatException | SQLException e) {
                wrongUser.setText("Invalid Account Number/Number Format");
            }
            con.close();
            transferAmount.setText("");
            accountNo.setText("");
        }
    }

    public void backPressed(ActionEvent event) throws IOException, SQLException {

        try {
            String accountBalance = null;
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
            ps = con.prepareStatement("SELECT * FROM Users WHERE User_ID = ?");
            ps.setString(1, UserID);
            rs = ps.executeQuery();

            if (rs.next()) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/fxml/UserPage.fxml")); //loading the User Page GUI
                loader.load();
                Parent root = loader.getRoot();
                UserPageController upc = loader.getController();
                InputStream is = rs.getBinaryStream("User_Image");
                Image image = new Image("/icons/edituser.png");
                if (is != null) { //if there is a image for the user
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
                //passing all the infromation to previous screen, so we can display all the information correctly there.
                upc.GetUserID(UserID, rs.getString("First_Name") + " " + rs.getString("Last_Name"), rs.getString("Phone_No"), rs.getString("Email"), rs.getString("CNIC"), image,accountBalance);

                stage.setTitle("User Page");
                stage.setMaximized(true); //always maximized
                stage.setResizable(false); //cannot resize
                Scene scene = new Scene(root);
                scene.getStylesheets().add("/styles/AdminPage.css");
                stage.setScene(scene);
                stage.show();

            }
        } catch (Exception e) {
            System.out.println(e);
        }
        con.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
