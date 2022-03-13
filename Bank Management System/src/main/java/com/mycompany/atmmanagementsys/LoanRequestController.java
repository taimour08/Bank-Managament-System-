package com.mycompany.atmmanagementsys;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.sqlite.core.DB;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class LoanRequestController implements Initializable {
    String UserID, accountNumber;

    Connection con;
    PreparedStatement ps = null;
    ResultSet rs = null;
    @FXML
    javafx.scene.control.Button back;
    @FXML
    javafx.scene.control.Button reqLoan;
    @FXML
    private TextArea reason;
    @FXML
    private TextField amount;
    @FXML
    private Label wrongUsr;


    //setting userID and accountNumber, so we can get back on the screen from which we came here, and request a loan based on the account number
    public void getUserID(String Id, String account) {
        UserID = Id;
        accountNumber = account;
    }

    public void backPressed(ActionEvent event) throws IOException, SQLException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/UserLoanController.fxml")); //loading userLoanController GUI
        loader.load();
        Parent root = loader.getRoot();
        UserLoanController upc = loader.getController();
        upc.getUserID(UserID, accountNumber); //sending the information back, so we display correct information there, i.e, display the info same as it was before coming to this page
        Stage stage = (Stage) back.getScene().getWindow();
        stage.setTitle("Loan Page");
        stage.setMaximized(true); //always maximized
        stage.setResizable(false); //cannot resize
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void reqLoanPressed() throws SQLException {
        wrongUsr.setText("");//initially there is no message
        updateLoanRequestStatus(amount.getText(), reason.getText(), accountNumber); //this function will insert some entries in the database regarding the request

    }

    public boolean updateLoanRequestStatus(String amount, String reason, String accountNumber) throws SQLException {
        try {


            boolean bool = false; //this will tell us, if there isn't alreay request or loan payment pending or not
            if (amount.isEmpty() || reason.isEmpty()) { //invalid input
            } else {
                if (Integer.parseInt(amount) < 0) { //amount mustn't be less than zero
                    bool = true;
                }
                if (bool) { //if amount was zero

                    con.close();
                    return false;
                }
                con = DbConnection.Connection();
                ps = con.prepareStatement("SELECT * FROM Loan_Requests WHERE Account_No = ?");
                ps.setString(1, accountNumber);
                rs = ps.executeQuery();
                bool = false;
                while (rs.next()) { //if the account number has some loan requests
                    if (rs.getString("Status").equals("-")) { //if there is any pending loan request
                        bool = true;
                    }
                }
                if (bool) {//already pending request, you cannot request new loan.
                    con.close();
                    wrongUsr.setText("Already Pending Request!!");

                    return false;
                }

                ps = con.prepareStatement("SELECT h.Loan_ID,h.Status,l.Amount FROM Loan_History h, Loan_Requests l where l.Request_ID = h.Request_ID and l.Account_No = '" + accountNumber + "'");
                rs = null;

                rs = ps.executeQuery();
                bool = false;
                while (rs.next()) {
                    if (rs.getString("Status").equals("Pending")) { //if already pending loan
                        bool = true;
                    }
                }
                if (bool) { //if already pending loan, you cannot request a new loan

                    con.close();
                    wrongUsr.setText("Already Pending Loan!!");
                    return false;
                }
                //if everything is fine, then we are good to go, to request a new loan
                // we are setting up the current date and time, which will tell the data and time
                //that when the loan was requested
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                Date date = new Date();
                String todayDate = formatter.format(date);
                formatter = new SimpleDateFormat(" HH:mm:ss");
                String time = formatter.format(date);
                int requestId = 0;
                ps = con.prepareStatement("SELECT * FROM Loan_Requests");
                rs = ps.executeQuery();
                while (rs.next()) {
                    requestId = rs.getInt("Request_ID"); //extracting the latest requestID, we will increase it by 1, we will use this as trigger
                }
                PreparedStatement ps = con.prepareStatement("INSERT INTO Loan_Requests VALUES (?,?,?,?,?,?,?)");
                ps.setInt(1, (requestId + 1));
                ps.setString(2, accountNumber);
                ps.setString(3, amount);
                ps.setString(4, reason);
                ps.setString(5, "-");
                ps.setString(6, todayDate);
                ps.setString(7, time);
                ps.executeUpdate();
                con.close();
                return true;
            }
            return false;
        }
        catch (Exception e){
            System.out.println(e);
        }
        con.close();
        return  false;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
