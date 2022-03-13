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

public class ManagerLoanController implements Initializable {
    @FXML
    private TableView<LoanData> LoanTable;
    @FXML
    private TableColumn<LoanData, String> loanID;
    @FXML
    private TableColumn<LoanData, String> accountNo;
    @FXML
    private TableColumn<LoanData, String> reason;
    @FXML
    private TableColumn<LoanData, String> status;
    @FXML
    private TableColumn<LoanData, String> loanAmount;
    @FXML
    private Label wrongUsr;
    @FXML
    private ObservableList<LoanData> data;
    String UserID;
    Connection con;
    PreparedStatement ps = null;
    ResultSet rs = null;
    @FXML
    Button back;
    @FXML
    Button loadBtn;
    @FXML
    TextField accountNoField;

    public void getUserID(String Id) {
        UserID = Id;
    }  //setting the adminID, so specific admin can view the records, and we can get back on the screen by using this id.

    public void backPressed(ActionEvent event) throws IOException, SQLException {

        try {
            con = DbConnection.Connection();
            ps = con.prepareStatement("SELECT * FROM Users WHERE User_ID = ?"); //getting back on admin page, so extracting the userID, so we can return to correct admin page, not any other admin page.
            ps.setString(1, UserID);
            rs = ps.executeQuery();
            if (rs.next()) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/fxml/AdminPage.fxml")); //loading the admin page GUI

                loader.load();
                Parent root = loader.getRoot();
                AdminPageController upc = loader.getController(); //will set all the admin information for the previous page, to load the correct information after going back
                InputStream is = rs.getBinaryStream("User_Image");
                Image image = new Image("/icons/edituser.png"); //loading the image
                if (is != null) { //if the admin has the image
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
                //passing the information to the previous page
                upc.GetUserID(UserID, rs.getString("First_Name") + " " + rs.getString("Last_Name"), rs.getString("Phone_No"), rs.getString("Email"), rs.getString("CNIC"), image);
                stage.setTitle("Admin Page");
                stage.setMaximized(true);
                stage.setResizable(false);
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

    private void loadData() throws SQLException {
        //loading All the Loan Requests

        try {
            con = DbConnection.Connection();
            data = FXCollections.observableArrayList(); //managing table
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Loan_Requests");
            rs = ps.executeQuery();
            while (rs.next()) {
                //adding all the obtained info from the database to tables
                data.add(new LoanData(rs.getString("Request_ID"), rs.getString("Account_No"), rs.getString("Purpose"), rs.getString("Status"), rs.getString("Amount")));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        //managing table
        loanID.setCellValueFactory(new PropertyValueFactory<LoanData, String>("LoanID"));
        accountNo.setCellValueFactory(new PropertyValueFactory<LoanData, String>("AccountNo"));
        loanAmount.setCellValueFactory(new PropertyValueFactory<LoanData, String>("LoanAmount"));
        reason.setCellValueFactory(new PropertyValueFactory<LoanData, String>("LoanReason"));
        status.setCellValueFactory(new PropertyValueFactory<LoanData, String>("LoanStatus"));
        LoanTable.setItems(data);
        con.close();

    }

    public void loadPressed(ActionEvent event) throws IOException, SQLException {
        wrongUsr.setText(""); //initially there are no messages so setting to NULL
        loadData();//loading the loan requests
    }

    public void searchPressed(ActionEvent event) throws SQLException {
        wrongUsr.setText(""); //initailly no messages
        //same as loadData, the only difference is that this time we are loading based on accountNumber which we get form accountNumber text field

        try {
            con = DbConnection.Connection();
            data = FXCollections.observableArrayList();
            ps = con.prepareStatement("SELECT * FROM Loan_Requests where Account_No = '" + accountNoField.getText() + "'");
            rs = ps.executeQuery();
            int count = 0;
            while (rs.next()) {
                count += 1;
                data.add(new LoanData(rs.getString("Request_ID"), rs.getString("Account_No"), rs.getString("Purpose"), rs.getString("Status"), rs.getString("Amount")));
            }
            if (count > 0) {
            } else {
                wrongUsr.setText("No Record Found!!");
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        loanID.setCellValueFactory(new PropertyValueFactory<LoanData, String>("LoanID"));
        accountNo.setCellValueFactory(new PropertyValueFactory<LoanData, String>("AccountNo"));
        loanAmount.setCellValueFactory(new PropertyValueFactory<LoanData, String>("LoanAmount"));
        reason.setCellValueFactory(new PropertyValueFactory<LoanData, String>("LoanReason"));
        status.setCellValueFactory(new PropertyValueFactory<LoanData, String>("LoanStatus"));
        LoanTable.setItems(data);
        con.close();

    }

    public void rejectPressed(ActionEvent event) throws SQLException {
        wrongUsr.setText("");//initailly no messages
        LoanData loanData = LoanTable.getSelectionModel().getSelectedItem();
        String loanID = loanData.getLoanID(); //extracting the selected request.
        updateRejectStatus(loanID); //updating the status to Reject by passing the requestId, here loanID denote the requestID

    }

    public boolean updateAcceptStatus(String loanID) throws SQLException {
        //updating the accept status

        try {
            con = DbConnection.Connection();
            ps = con.prepareStatement("SELECT * FROM Loan_Requests WHERE Request_ID = ?");
            ps.setString(1, loanID);
            rs = ps.executeQuery();
            boolean bool = false; //this will help us to close the connection  properly before returning
            if (rs.next()) {
                if (!rs.getString("Status").equals("-")) { //means the request is already dealed,
                    bool = true;

                }
            }
            if (bool) { //if the request is dealt then properly close the connection and return from here.
                con.close();
                wrongUsr.setText("Already Dealt");
                return false;
            }
            ps = con.prepareStatement("UPDATE Loan_Requests set Status = 'A' where Request_ID = ?");
            ps.setString(1, loanID);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }
        con.close();
        addToLoanHistory(loanID, "Pending"); //if we reach here it means that we have successfully accepted the loan, so now make the loan available for
        //user so we add this to loan table, where there is a track of all the loans
        loadData(); //refresh the table
        return true;

    }

    public boolean updateRejectStatus(String loanID) throws SQLException {
        //similar to updateAcceptStatus, only differnece is we donot addToLoanHistory, becuase a rejected request means no Loan, so we only tell the user that its request is rejected.
        try {
            con = DbConnection.Connection();
            ps = con.prepareStatement("SELECT * FROM Loan_Requests WHERE Request_ID = ?");
            ps.setString(1, loanID);
            rs = ps.executeQuery();
            boolean bool = false;
            if (rs.next()) {
                if (!rs.getString("Status").equals("-")) {
                    bool = true;

                }
            }
            if (bool) {
                con.close();
                wrongUsr.setText("Already Dealt!!");
                return false;
            }
            ps = con.prepareStatement("UPDATE Loan_Requests set Status = 'R' where Request_ID = ?");
            ps.setString(1, loanID);
            ps.executeUpdate();
            con.close();
            loadData();
            return true;

        } catch (Exception e) {
            System.out.println(e);
        }
        con.close();
        return false;
    }

    private void addToLoanHistory(String id, String status) throws SQLException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String todayDate = formatter.format(date); //getting latest Date in the form of string
        formatter = new SimpleDateFormat(" HH:mm:ss");
        String time = formatter.format(date); //getting the latest time
        int loanID = 0;
        //loan ids would be sequentially increased for every entry
        try {
            con = DbConnection.Connection();
            ps = con.prepareStatement("SELECT * FROM Loan_History"); //first we will extract the most recent loan ID, and increment it by 1, basically we are doinng the work of a DB trigger here
            rs = ps.executeQuery();
            while (rs.next()) {
                loanID = rs.getInt("Loan_ID");
            }
            //we insert some values to the loan history table, here todayDate and time denote that the loan has been started from today date and time
            PreparedStatement ps = con.prepareStatement("INSERT INTO Loan_History VALUES (?,?,?,?,?,?,?)");
            ps.setInt(1, (loanID + 1));
            ps.setString(2, id);
            ps.setString(3, status);
            ps.setString(4, todayDate);
            ps.setString(5, time);
            ps.setString(6, "-"); //denote the ending time and date of the loan, "-" denote the loan hasn't paid yet
            ps.setString(7, "-");
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }

        con.close();

    }

    public void acceptPressed(ActionEvent event) throws SQLException {
        //similar to reject pressed
        wrongUsr.setText("");
        LoanData loanData = LoanTable.getSelectionModel().getSelectedItem();
        String loanID = loanData.getLoanID();
        updateAcceptStatus(loanID);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
