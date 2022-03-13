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

public class WithdrawCashController implements Initializable {
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
    Button withdrawCashBtn;
    @FXML
    TextField accountNo;
    @FXML
    TextField withdrawAmount;
    @FXML
    Label wrongUser;
    Connection con;
    PreparedStatement ps = null;
    ResultSet rs = null;

    private String getConfirmation() {
        //this function display a confirmation box, so user can be sure to do a task.
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Alert");
        ButtonType type = new ButtonType("OK");
        alert.setContentText("This is a Confirmation alert");
        alert.showAndWait();
        return alert.getResult().getText();
    }

    private void withDrawCash(String accountNo, int NewBalance) throws SQLException {
        try {
            con = DbConnection.Connection();
            //executing the query, to update newBalance at the given account Number
            ps = con.prepareStatement("UPDATE Bank_Account SET Balance =? WHERE Account_No = '" + accountNo + "'");
            ps.setInt(1, NewBalance);
            ps.executeUpdate();
            wrongUser.setText("Cash Has Been Withdrawn\n New Balance: " + NewBalance);
        } catch (Exception e) {
            System.out.println(e);
        }
        con.close();
    }

    private boolean accountIsActive(String account) throws SQLException {
        //this function check if the account is active or not, like it checks the status of an accountNumber
        try {
            boolean bool = false; //if any error so it will become true and first we will close the connection and then return
            ps = con.prepareStatement("SELECT * FROM Bank_Account WHERE Account_No = ?");
            ps.setString(1, account);
            rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getString("Status").equals("Active")) {

                } else {
                    bool = true;
                }

            } else {
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

    private void addToTransactionHistory(String type, int amount, String id, String id1) throws SQLException {
        //this function would add the current transaction to the transaction history

        //setting current date and time
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
            while (rs.next()) { //extracting the latest transactionID from the history table, so we can increment it and insert the next record by using this ID, basically this is a trigger
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

    //setting all the information for the user, because its the same stage. So we need to display the same information as of previous screen
    public void GetUserID(String id, String Name, String phone, String email, String identifier, Image image) throws SQLException {
        UserID = id;
        userName.setText(Name);
        cnic.setText(identifier);
        emailAddress.setText(email);
        phoneNumber.setText(phone);
        System.out.println(image);
        userImage.setImage(image);


    }

    public void withdrawCashPressed(ActionEvent event) throws IOException, SQLException {
        //this function is called when the withdraw button is pressed.
        wrongUser.setText(""); //initially there is no error message
        if (getConfirmation().equals("OK")) { //if the user agreed on the confirmation as well
            try {
                //if any wrong input is given
                if (withdrawAmount.getText().isEmpty() || Integer.parseInt(withdrawAmount.getText()) < 0) {
                    wrongUser.setText("Please Enter A Valid Amount");
                } else {
                    con = DbConnection.Connection();
                    if (accountIsActive(accountNo.getText())) { //if the status of the account is active
                        ps = null;
                        rs = null;
                        ps = con.prepareStatement("SELECT * FROM Bank_Account WHERE Account_No = ?");
                        ps.setString(1, accountNo.getText());
                        rs = ps.executeQuery();
                        if (rs.next()) {
                            //calculation the new balance, by subtracting the original balance from the amount that is being withdraw
                            int NewBalance = (rs.getInt("Balance") - Integer.parseInt(withdrawAmount.getText()));
                            UserID1 = rs.getString("Cust_ID");
                            if (NewBalance >= 0) { //customer has balance
                                withDrawCash(accountNo.getText(), NewBalance);
                                addToTransactionHistory("Withdraw", Integer.parseInt(withdrawAmount.getText()), UserID1, UserID);
                            } else {
                                //customer donot have enough balance
                                wrongUser.setText("Do Not Have Enough Cash!!");
                            }
                        }
                    } else {
                        wrongUser.setText("The Account is not Available");
                    }

                }
            } catch (NumberFormatException | SQLException e) {
                wrongUser.setText("Invalid Account Number/Number Format");
            }
            con.close();
            withdrawAmount.setText("");//set the labels of the text fields to NULL
            accountNo.setText(""); //set the labels of the text fields to NULL
        }
    }

    public void backPressed(ActionEvent event) throws IOException, SQLException {

        try {
            con = DbConnection.Connection();
            ps = con.prepareStatement("SELECT * FROM Users WHERE User_ID = ?");
            ps.setString(1, UserID); //passing the user ID , so we correctly load the information for the current userID
            rs = ps.executeQuery();

            if (rs.next()) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/fxml/EmployeePage.fxml")); //loading the employee page GUI

                loader.load();
                Parent root = loader.getRoot();
                EmployeePageController upc = loader.getController();
                InputStream is = rs.getBinaryStream("User_Image");
                Image image = new Image("/icons/edituser.png");
                if (is != null) { //if user has image
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

                stage.setTitle("Employee Page");
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
