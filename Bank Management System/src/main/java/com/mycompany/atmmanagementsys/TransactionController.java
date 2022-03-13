package com.mycompany.atmmanagementsys;

import com.itextpdf.html2pdf.HtmlConverter;
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
import javafx.scene.layout.Pane;
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

public class TransactionController implements Initializable {
    Connection con;
    PreparedStatement ps = null;
    ResultSet rs = null;
    String UserID;
    @FXML
    private TableView<TransactionData> TransactionTable;
    @FXML
    private TableColumn<TransactionData, String> transID;
    @FXML
    private TableColumn<TransactionData, String> transType;
    @FXML
    private TableColumn<TransactionData, String> transAmount;
    @FXML
    private ObservableList<TransactionData> data;
    @FXML
    Button back;
    @FXML
    Button loadBtn;
    @FXML
    Button print;
    @FXML
    Button searchBtn;
    @FXML
    TextField amount;
    @FXML
    Label confirmation;
    @FXML
    Pane messagePane;
    String accountNumber;

    public void getUserID(String Id, String account) {
        UserID = Id;
        accountNumber = account;
    } //setting the userID and account Number , so we can know
    //for which user and which account of the user we are viewing the transaction History

    public void backPressed(ActionEvent event) throws IOException, SQLException {


        try {
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
            ps = con.prepareStatement("SELECT * FROM Users WHERE User_ID = ?"); //extracting the userID, so we can use it to load All the information of the user from which we came here
            //information correctly
            ps.setString(1, UserID);

            rs = ps.executeQuery();

            if (rs.next()) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/fxml/UserPage.fxml")); //loading the UserPage GUI

                loader.load();
                Parent root = loader.getRoot();
                UserPageController upc = loader.getController();
                InputStream is = rs.getBinaryStream("User_Image");
                //Loading the picture of the user
                Image image = new Image("/icons/edituser.png");
                if (is != null) { //if there is a user image
                    OutputStream os = new FileOutputStream(new File("photo.jpg"));
                    byte[] content = new byte[1024];
                    int size = 0;
                    while ((size = is.read(content)) != -1) {
                        os.write(content, 0, size);
                    }
                    os.close();
                    is.close();
                    //loading the image
                    image = new Image("file:photo.jpg", 250, 250, true, true);
                }
                Stage stage = (Stage) back.getScene().getWindow();
                //passing all the info the User Page, so we can load all the information correctly
                upc.GetUserID(UserID, rs.getString("First_Name") + " " + rs.getString("Last_Name"), rs.getString("Phone_No"), rs.getString("Email"), rs.getString("CNIC"), image,accountBalance);

                stage.setTitle("User Page");
                stage.setMaximized(true);
                stage.setResizable(false);
                Scene scene = new Scene(root);
                scene.getStylesheets().add("/styles/AdminPage.css"); //adding style sheet
                stage.setScene(scene);
                stage.show();
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        con.close();
    }

    public void loadPressed(ActionEvent event) throws IOException, SQLException {
        messagePane.setVisible(false); //initially there is no message, so we won't display any thing
        confirmation.setText("");


        try {
            con = DbConnection.Connection();
            data = FXCollections.observableArrayList();
            //loading all the transactions that are done by the user with the specified accountNumber
            ps = con.prepareStatement("SELECT * FROM Transaction_History where Account_No_1 ='" + accountNumber + "' or Account_No_2 ='" + accountNumber + "'");
            rs = ps.executeQuery();
            while (rs.next()) {
                //adding all the transactions to the table
                data.add(new TransactionData(rs.getString("Transaction_ID"), rs.getString("Transaction_Type"), String.valueOf(rs.getInt("Amount"))));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println(data);
        transID.setCellValueFactory(new PropertyValueFactory<TransactionData, String>("TransID"));
        transType.setCellValueFactory(new PropertyValueFactory<TransactionData, String>("TransType"));
        transAmount.setCellValueFactory(new PropertyValueFactory<TransactionData, String>("TransAmount"));
        TransactionTable.setItems(data);
        con.close();

    }

    public void searchPressed(ActionEvent event) throws SQLException {
        //similar to loadPressed only difference is that this time data is being loaded with additional specification of amount that we extract from amount Text field.
        messagePane.setVisible(false);
        confirmation.setText("");

        try {
            con = DbConnection.Connection();
            data = FXCollections.observableArrayList();
            ps = con.prepareStatement("SELECT * FROM Transaction_History where (Account_No_1 ='" + accountNumber + "' or Account_No_2 ='" + accountNumber + "') and Amount = '" + amount.getText() + "'");
            int counter = 0;
            rs = ps.executeQuery();
            while (rs.next()) {
                counter += 1;
                data.add(new TransactionData(rs.getString("Transaction_ID"), rs.getString("Transaction_Type"), String.valueOf(rs.getInt("Amount"))));
            }
            if (counter > 0) {

            } else {
                messagePane.setVisible(true);
                confirmation.setText("No Record Found!!");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println(data);
        transID.setCellValueFactory(new PropertyValueFactory<TransactionData, String>("TransID"));
        transType.setCellValueFactory(new PropertyValueFactory<TransactionData, String>("TransType"));
        transAmount.setCellValueFactory(new PropertyValueFactory<TransactionData, String>("TransAmount"));
        TransactionTable.setItems(data);
        con.close();
    }

    public void printPressed(ActionEvent event) throws Exception {
        //printing the whole transaction table.

        int res = 0;
        System.out.println(UserID);
        try {
            con = DbConnection.Connection();
            ps = con.prepareStatement("SELECT * FROM Users where User_ID ='" + UserID + "'");
            rs = ps.executeQuery();
            rs.next();
            //making a html sequence to print in specific layout
            String html_start = "<body> <h1>ABC Bank</h1><h4>F9 Branch, Islamabad</h4>"
                    + "<p>" + rs.getString("First_Name") + " " + rs.getString("Last_Name") + "<br>Account Number: " + accountNumber + "<br>Current Account"
                    + "<br></p><h4>E Bank Statement</h4>"
                    + "<table border=1 ><tr>"
                    + "<th width=300px>Transaction ID</th><th width=300px >Account 1</th><th width=300px >Account 2</th><th width=300px >Transaction Type</th><th width=300px >Amount</th><th width=300px >Date</th><th width=300px >Time</th><th width=300px >Emp ID</th>"
                    + "</tr>";


            // make initial html code strings
            String html_end = "</table></body><style>table { text-align: center; }</style>";
            String html_data = "";

            System.out.println("About to execute SQL");
            // get all the transactions
            System.out.println("User: " + UserID);
            int rows = 0; // just to make sure that we get data
            ps = null;
            // ------------ actual code ------------------------------------------
            ps = con.prepareStatement("SELECT * FROM Transaction_History where Account_No_1 ='" + accountNumber + "' or Account_No_2 ='" + accountNumber + "'");
            rs = null;
            try {
                System.out.println("Here I am");
                rs = ps.executeQuery();
                System.out.println("Getting lines");
                while (rs.next()) {
                    rows++;
                    html_data += "<tr><td>" + rs.getString("Transaction_ID") + "</td>"
                            + "<td>" + rs.getString("Account_No_1") + "</td>"
                            + "<td>" + rs.getString("Account_No_2") + "</td>"
                            + "<td>" + rs.getString("Transaction_Type") + "</td>"
                            + "<td>" + rs.getString("Amount") + "</td>"
                            + "<td>" + rs.getString("T_Date") + "</td>"
                            + "<td>" + rs.getString("T_Time") + "</td>"
                            + "<td>" + rs.getString("User_ID") + "</td></tr>";
                }
                System.out.println("Got Data");
            } catch (SQLException throwables) {
                System.out.println("Unknown error");
            }


            // we get some transactions
            if (rows > 0) {
                String html = html_start + html_data + html_end;
                try {
                    String file_name = "E_Statement_" + java.time.LocalDateTime.now() + ".pdf";
                    file_name = file_name.replaceAll(":", "_");
                    HtmlConverter.convertToPdf(html, new FileOutputStream(file_name));
                    res = 1;
                    System.out.println("PDF is successfully printed");
                } catch (Exception e) {
                    System.out.println("Unknown error");
                }
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
