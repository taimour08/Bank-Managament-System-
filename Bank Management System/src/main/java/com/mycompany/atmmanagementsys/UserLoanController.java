package com.mycompany.atmmanagementsys;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
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

public class UserLoanController implements Initializable {
    Connection con;
    PreparedStatement ps = null;
    ResultSet rs = null;
    String UserID, accountNumber;
    @FXML
    private TableView<LoanData> LoanTableReq, LoanTableApp;
    @FXML
    private TableColumn<LoanData, String> requestID, loanID;
    @FXML
    private TableColumn<LoanData, String> requestAmount, loanAmount;
    @FXML
    private TableColumn<LoanData, String> requestStatus, loanStatus;
    @FXML
    private ObservableList<LoanData> data;
    @FXML
    Button back;
    @FXML
    Button reqLoan;
    @FXML
    Label wrongUsr;
    @FXML
    TextField requestIDField;
    @FXML
    TextField LoanIDField;

    public UserLoanController() throws SQLException {
    }

    //setting userID as well as account number, becuase a loan is requested from a account Number, and userID would serve as a purpose to go back to the same UserPage
    public void getUserID(String Id, String account) {
        UserID = Id;
        accountNumber = account;
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
            ps = null;
            rs = null;
            ps = con.prepareStatement("SELECT * FROM Users WHERE User_ID = ?");
            ps.setString(1, UserID);
            rs = ps.executeQuery();
            //if we found the user with this userID
            if (rs.next()) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/fxml/UserPage.fxml")); //loading the GUI of UserPage

                loader.load();
                Parent root = loader.getRoot();
                UserPageController upc = loader.getController();
                InputStream is = rs.getBinaryStream("User_Image");
                Image image = new Image("/icons/edituser.png");
                if (is != null) { //if there is a image for the user in the database
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

                stage.setTitle("User Page");
                stage.setMaximized(true); //always maximize
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

    private void loadDataReq() throws SQLException {
        //loading all the customer requests for loan, pending or approved
        wrongUsr.setText(""); //initially there is no message

        try {
            con = DbConnection.Connection();
            data = FXCollections.observableArrayList();
            ps = con.prepareStatement("SELECT * FROM Loan_Requests where Account_No = '" + accountNumber + "'");
            rs = ps.executeQuery();
            while (rs.next()) {
                //adding all the requests of the customer to the table, specified by customer ID.
                data.add(new LoanData(rs.getString("Request_ID"), " ", " ", rs.getString("Status"), rs.getString("Amount")));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println(data);
        requestID.setCellValueFactory(new PropertyValueFactory<LoanData, String>("LoanID"));
        requestStatus.setCellValueFactory(new PropertyValueFactory<LoanData, String>("LoanStatus"));
        requestAmount.setCellValueFactory(new PropertyValueFactory<LoanData, String>("LoanAmount"));
        LoanTableReq.setItems(data);
        con.close();
    }

    private void loadDataApp() throws SQLException {
        //loading all the loans that have been approved or paid for the specific customer, who have logged in
        wrongUsr.setText("");//initially there is no message

        try {
            con = DbConnection.Connection();
            data = FXCollections.observableArrayList();
            ps = con.prepareStatement("SELECT h.Loan_ID,h.Status,l.Amount FROM Loan_History h, Loan_Requests l where l.Request_ID = h.Request_ID and l.Account_No = '" + accountNumber + "'");
            rs = null;
            rs = ps.executeQuery();
            while (rs.next()) {
                //adding all the taken or paid loans, to the table.
                data.add(new LoanData(rs.getString("Loan_ID"), " ", " ", rs.getString("Status"), rs.getString("Amount")));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println(data);
        loanID.setCellValueFactory(new PropertyValueFactory<LoanData, String>("LoanID"));
        loanStatus.setCellValueFactory(new PropertyValueFactory<LoanData, String>("LoanStatus"));
        loanAmount.setCellValueFactory(new PropertyValueFactory<LoanData, String>("LoanAmount"));
        LoanTableApp.setItems(data);
        con.close();

    }

    public void loadPressedReq(ActionEvent event) throws IOException, SQLException {
        //this function is called when the load button is pressed for load requests.
        loadDataReq();
    }

    public void loadPressedApp(ActionEvent event) throws IOException, SQLException {
        //this function is called when the load button is pressed for load loans.
        loadDataApp();
    }

    public void reqLoanPressed() throws IOException, SQLException {
        //function to load request a new loan page.
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/UserLoanRequestController.fxml")); //loading request Loan GUI
        loader.load();
        Parent root = loader.getRoot();
        Scene scene = new Scene(root);
        Stage stage = (Stage) reqLoan.getScene().getWindow();
        stage.setMaximized(true); //setting screen to maximized every time.
        stage.setResizable(false); //cannot resize the screen
        LoanRequestController fxm = loader.getController();
        fxm.getUserID(UserID, accountNumber); //setting the userID and accountNumber for RequestLoan, so we can come back exactly on the same page of the same user
        //using userID and using accountNumber we know which user is asking for the loan request.
        stage.setTitle("Loan Request Screen");
        stage.setScene(scene);
        stage.show();
    }

    private void withDrawCash(String accountNo, int NewBalance) throws SQLException {
        //this function is called when user pay loan from his account so we deduct the balance from the account when loan is successfully paid.
        try {
            con = DbConnection.Connection();
            ps = con.prepareStatement("UPDATE Bank_Account SET Balance =? WHERE Account_No = '" + accountNo + "'");
            ps.setInt(1, NewBalance);
            ps.executeUpdate();
            wrongUsr.setText("Loan Has Been Paid Successfully");
        } catch (Exception e) {
            System.out.println(e);

        }
        con.close();
    }

    public void searchPressedReq(ActionEvent event) throws SQLException {
        wrongUsr.setText(""); //initially there is no message.
        //this is same as loadDataReq the only difference is that here we load the data based on the specific accountNumber extracted from accountNumber text field

        try {
            con = DbConnection.Connection();
            data = FXCollections.observableArrayList();
            ps = con.prepareStatement("SELECT * FROM Loan_Requests where Account_No = '" + accountNumber + "' and Request_ID = '" + requestIDField.getText() + "'");
            rs = ps.executeQuery();
            if (rs.next()) {
                data.add(new LoanData(rs.getString("Request_ID"), " ", " ", rs.getString("Status"), rs.getString("Amount")));
            } else {
                wrongUsr.setText("No Record Available!!");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println(data);
        requestID.setCellValueFactory(new PropertyValueFactory<LoanData, String>("LoanID"));
        requestStatus.setCellValueFactory(new PropertyValueFactory<LoanData, String>("LoanStatus"));
        requestAmount.setCellValueFactory(new PropertyValueFactory<LoanData, String>("LoanAmount"));
        LoanTableReq.setItems(data);
        con.close();

    }

    public void searchPressedApp(ActionEvent event) throws SQLException {
        wrongUsr.setText(""); //initially there is no message
        //this function is called when the search field of loans are inserted with a text and search button is pressed

        //this function is same as loadDataApp, the only difference is that  it load based on specific account number extracted from accountNumber Field.

        try {
            con = DbConnection.Connection();
            data = FXCollections.observableArrayList();
            ps = con.prepareStatement("SELECT h.Loan_ID,h.Status,l.Amount FROM Loan_History h, Loan_Requests l where l.Request_ID = h.Request_ID and l.Account_No = '" + accountNumber + "' and h.Loan_ID = '" + LoanIDField.getText() + "'");
            rs = ps.executeQuery();
            if (rs.next()) {
                data.add(new LoanData(rs.getString("Loan_ID"), " ", " ", rs.getString("Status"), rs.getString("Amount")));
            } else {
                wrongUsr.setText("No Record Available!!");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println(data);
        loanID.setCellValueFactory(new PropertyValueFactory<LoanData, String>("LoanID"));
        loanStatus.setCellValueFactory(new PropertyValueFactory<LoanData, String>("LoanStatus"));
        loanAmount.setCellValueFactory(new PropertyValueFactory<LoanData, String>("LoanAmount"));
        LoanTableApp.setItems(data);
        con.close();

    }

    public boolean updatePayStatus(String loanID, String loanAmount, String accountNumber) throws SQLException {
        //this function is called when we have to tell the DB that the amount has been paid, so now change the status and set tha paid date to the current date and time
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String todayDate = formatter.format(date);
        formatter = new SimpleDateFormat(" HH:mm:ss");
        String time = formatter.format(date);

        try {
            con = DbConnection.Connection();
            ps = con.prepareStatement("SELECT * FROM Loan_History WHERE Loan_ID = ?");
            ps.setString(1, loanID);
            rs = ps.executeQuery();
            boolean bool = false; //this variable is used to see if any thing is already done, so we donot repeat that.
            if (rs.next()) {
                System.out.println(rs.getString("Status"));
                if (!rs.getString("Status").equals("Pending")) { //means if the amount is already paid, not pending means paid.
                    bool = true;
                }
            }
            if (bool) { //means amount is already paid so return after closing the connection, and display already paid.

                con.close();
                wrongUsr.setText("Already Paid!!");
                return false;
            }
            bool = false;
            ps = con.prepareStatement("SELECT * FROM Bank_Account WHERE Account_No = ?");
            ps.setString(1, accountNumber);
            rs = ps.executeQuery();
            if (rs.next()) {
                int NewBalance = (rs.getInt("Balance") - Integer.parseInt(loanAmount));
                if (NewBalance >= 0) { //if there is balance in account so withdraw that and display that loan is successfully paid.
                    withDrawCash(accountNumber, NewBalance);
                } else { //if not then donot do any thing
                    bool = true;
                }
                if (bool) {
                    con.close();
                    wrongUsr.setText("Not Enough Amount!"); //display error if amount wasn't enough.

                    return false;
                }
                System.out.println(loanID);
                ps = con.prepareStatement("UPDATE Loan_History set Status = 'Paid', Loan_Return_Date = ? ,Loan_Return_Time =? where Loan_ID = ?");
                ps.setString(3, loanID);
                ps.setString(1, todayDate);
                ps.setString(2, time);

                ps.executeUpdate();
                con.close();
                loadDataApp();
                return true;
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        con.close();
        return false;
    }

    public void payPressed(ActionEvent event) throws SQLException {
        wrongUsr.setText(""); //initially there is no error
        //extracting loan amount and loanID from the selected row.
        try {


            LoanData loanData = LoanTableApp.getSelectionModel().getSelectedItem();
            String loanID = loanData.getLoanID();
            String loanAmount = loanData.getLoanAmount();
            updatePayStatus(loanID, loanAmount, accountNumber); //this function will updatePay in the database.
        }
        catch (Exception e){
            System.out.println(e);
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
