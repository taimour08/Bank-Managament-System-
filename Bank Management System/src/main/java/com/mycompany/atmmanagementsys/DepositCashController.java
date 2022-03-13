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

import java.awt.image.DataBuffer;
import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class DepositCashController implements Initializable {
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
    String UserID, UserID1;
    @FXML
    ImageView userImage;
    @FXML
    Button back;
    @FXML
    Button deposit;
    @FXML
    Button depositCashBtn;
    @FXML
    TextField accountNo;
    @FXML
    TextField depositAmount;
    @FXML
    Label wrongUser;
    Connection con;
    ResultSet rs;
    PreparedStatement ps;

    private String getConfirmation() {
        //display confirmation box, so user can be sure of what is he doing
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Alert");
        ButtonType type = new ButtonType("OK");
        alert.setContentText("This is a Confirmation alert");
        alert.showAndWait();
        return alert.getResult().getText();
    }

    private void depositCash(String accountNo, int NewBalance) throws SQLException {
        try {
            //updating the account balance of the customer with the account number
            con = DbConnection.Connection();
            ps = con.prepareStatement("UPDATE Bank_Account SET Balance =? WHERE Account_No = '" + accountNo + "'");
            ps.setInt(1, NewBalance);
            ps.executeUpdate();
            wrongUser.setText("Cash Has Been Withdrawn");
        } catch (Exception e) {
            System.out.println(e);
        }
        con.close();
    }

    private void addToTransactionHistory(String type, int amount, String id, String id1) throws SQLException {
        //getting current data and time
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
            while (rs.next()) { //extracting the latest transaction ID, and we will increment it by 1, to insert a new record, basically it is acting as a trigger
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

    private boolean accountIsActive(String account) throws SQLException {
        try {
            //this will check the status of the account, whether active or blocked.
            con = DbConnection.Connection();
            ps = con.prepareStatement("SELECT * FROM Bank_Account WHERE Account_No = ?");
            ps.setString(1, account);
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
            System.out.println(e);
        }

        con.close();
        return false;
    }

    //setting all the information, so it can be used to display everything related to the customer
    public void GetUserID(String id, String Name, String phone, String email, String identifier, Image image) throws SQLException {
        UserID = id;
        userName.setText(Name);
        cnic.setText(identifier);
        emailAddress.setText(email);
        phoneNumber.setText(phone);
        System.out.println(image);
        userImage.setImage(image);


    }

    public void depositCashPressed(ActionEvent event) throws IOException, SQLException {
        wrongUser.setText(""); //initially there is no  message
        if (getConfirmation().equals("OK")) { //if user confirms in the confirmation box
            try {
                //if there is invalid input
                if (depositAmount.getText().isEmpty() || Integer.parseInt(depositAmount.getText()) < 0) {
                    wrongUser.setText("Please Enter A Valid Amount");
                } else {
                    con = DbConnection.Connection();

                    //if account is active
                    if (accountIsActive(accountNo.getText())) {
                        ps = con.prepareStatement("SELECT * FROM Bank_Account WHERE Account_No = ?");
                        ps.setString(1, accountNo.getText());
                        rs = ps.executeQuery();
                        if (rs.next()) { //if this account number exist
                            int NewBalance = (rs.getInt("Balance") + Integer.parseInt(depositAmount.getText()));
                            UserID1 = rs.getString("Cust_ID");
                            System.out.println(NewBalance);
                            depositCash(accountNo.getText(), NewBalance); //adding the amount to the customer with this account number
                            wrongUser.setText("Cash Has Been Deposited\n New Balance: " + NewBalance);
                            //adding the transaction to transaction history

                            addToTransactionHistory("Deposit", Integer.parseInt(depositAmount.getText()), UserID1, UserID);


                        }
                    } else {
                        wrongUser.setText("The Account is not Available");
                    }


                }
            } catch (NumberFormatException | SQLException e) {
                wrongUser.setText("Invalid Account Number/Number Format");
            }
            con.close();
            depositAmount.setText(""); //emptying the field depositAmount
            accountNo.setText(""); //emptying the field accountNumber
        }

    }

    public void backPressed(ActionEvent event) throws IOException, SQLException {

        try {
            con = DbConnection.Connection();
            ps = con.prepareStatement("SELECT * FROM Users WHERE User_ID = ?");
            ps.setString(1, UserID);
            rs = ps.executeQuery();

            if (rs.next()) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/fxml/EmployeePage.fxml"));//loadinng the GUI page of employee

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
                Stage stage = (Stage) back.getScene().getWindow();
                //passing all the information to the previous screen so it can display that information.
                upc.GetUserID(UserID, rs.getString("First_Name") + " " + rs.getString("Last_Name"), rs.getString("Phone_No"), rs.getString("Email"), rs.getString("CNIC"), image);

                stage.setTitle("Employee Page");
                stage.setMaximized(true);//always maximized
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
