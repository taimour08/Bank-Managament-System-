package com.mycompany.atmmanagementsys;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

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
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

public class ManageCustomerController implements Initializable {
    @FXML
    private TableView<CustomerData> CustomerTable;
    @FXML
    private TableColumn<CustomerData, String> cusID;
    @FXML
    private TableColumn<CustomerData, String> cusFirstName;
    @FXML
    private TableColumn<CustomerData, String> cusDOB;
    @FXML
    private TableColumn<CustomerData, String> cusGender;
    @FXML
    private TableColumn<CustomerData, String> cusCNIC;
    @FXML
    private TableColumn<CustomerData, String> cusFatherName;
    @FXML
    private TableColumn<CustomerData, String> cusPhone;
    @FXML
    private TableColumn<CustomerData, String> cusEmail;
    @FXML
    private TableColumn<CustomerData, String> cusAddress;
    @FXML
    private TableColumn<CustomerData, String> cusUserName;
    @FXML
    private TableColumn<CustomerData, String> cusPassword;
    @FXML
    private TableColumn<CustomerData, String> Accountno;
    @FXML

    private TableColumn<CustomerData, String> cusStatus;
    @FXML
    private TableColumn<CustomerData, String> cusCreationDate;
    @FXML
    private TableColumn<CustomerData, String> cusCreationTime;
    private ObservableList<CustomerData> data;
    @FXML
    Button back;
    @FXML
    Button Searchbtn;
    @FXML
    Button Loadbtn;
    @FXML
    Button AddCustomerbtn;
    @FXML
    Button EditCustomerbtn;
    @FXML
    Button DeleteCustomerbtn;
    @FXML
    Label confirmation;
    @FXML
    String UserID;
    @FXML
    TextField AccountNoField;

    Connection con;
    PreparedStatement ps = null;
    ResultSet rs = null;

    public void getUserID(String Id) {
        UserID = Id;
    } //this function would be used by the previous login
    //screen that would tell this that which Employee has loged in, so everything would be performed by his Employee ID.

    public void backPressed(ActionEvent event) throws IOException, SQLException {

        try {
            con = DbConnection.Connection(); //Making a DB connection to perform a DB related task.
            ps = con.prepareStatement("SELECT * FROM Users WHERE User_ID = ?"); //extracting complete row that contain all
            //the user(with the userID) information.
            ps.setString(1, UserID);
            rs = ps.executeQuery(); //executing query

            if (rs.next()) { //if ther query has returned us something means the Employee is still in the DB. any other process could have changed it.
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/fxml/AdminPage.fxml")); // we would again load the previous FXML page from which we came here
                loader.load();
                Parent root = loader.getRoot();
                AdminPageController upc = loader.getController();
                //image reading from the database and displaying it in this application
                InputStream is = rs.getBinaryStream("User_Image");
                Image image = new Image("/icons/edituser.png");
                if (is != null) { //means there is a image for the user.
                    OutputStream os = new FileOutputStream(new File("photo.jpg"));
                    byte[] content = new byte[1024];
                    int size = 0;
                    while ((size = is.read(content)) != -1) {
                        os.write(content, 0, size);
                    }
                    os.close();
                    is.close();
                    image = new Image("file:photo.jpg", 250, 250, true, true); //loading the obtained info to the image
                }
                Stage stage = (Stage) back.getScene().getWindow();
                //we are sending all the information about the employee back to the employee Page, so it would be loaded as it is as it was loaded for the first time, when the employee logged in
                upc.GetUserID(UserID, rs.getString("First_Name") + " " + rs.getString("Last_Name"), rs.getString("Phone_No"), rs.getString("Email"), rs.getString("CNIC"), image);
                stage.setTitle("Employee Page"); //setting title
                stage.setMaximized(true);
                stage.setResizable(false);
                Scene scene = new Scene(root);
                scene.getStylesheets().add("/styles/AdminPage.css"); //loading style sheet
                stage.setScene(scene);
                stage.show();
            }
        } catch (Exception e) {
            System.out.println(e);
        }


        con.close();
    }

    @FXML
    public void LoadCustomerData(ActionEvent event) throws SQLException {

        try {
            confirmation.setText("");//we first initialize our error displaying label with empty string, becuase at the start there is no error
            con = DbConnection.Connection();
            data = FXCollections.observableArrayList(); //managing table
            ps = con.prepareStatement("SELECT * FROM Users u, Bank_Account b WHERE u.User_Type = ? and u.User_ID = b.Cust_ID");
            ps.setString(1, "C");
            rs = ps.executeQuery();
            while (rs.next()) { //till we have data for the user, if we donot have than the data would be empty for us.
                System.out.println("ID: " + rs.getString("Cust_ID"));
                data.add(new CustomerData(rs.getString("Cust_ID"), rs.getString("First_Name"), rs.getString("Last_Name"), rs.getString("User_Name"), rs.getString("Password"), rs.getString("DOB"), rs.getString("Gender"), rs.getString("CNIC"), rs.getString("Father_Name"), rs.getString("Phone_No"), rs.getString("Email"), rs.getString("Address"), rs.getString("Account_No"), rs.getString("Status"), rs.getString("Account_Create_Date"), rs.getString("Account_Create_Time")));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println(data);
        //again managing the table
        cusID.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("Id"));
        cusFirstName.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("FirstName"));
        cusUserName.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("UserName"));
        cusPassword.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("Password"));
        cusAddress.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("Address"));
        cusDOB.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("DOB"));
        cusGender.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("Gender"));
        cusCNIC.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("CNIC"));
        cusFatherName.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("FatherName"));
        cusPhone.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("Phone"));
        cusEmail.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("Email"));
        cusAddress.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("Address"));
        Accountno.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("AccountNo"));
        cusStatus.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("Status"));
        cusCreationDate.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("CreationDate"));
        cusCreationTime.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("CreationTime"));
        CustomerTable.setItems(data); //setting all the data to the table

        con.close();
    }

    public void SearchCustomer(ActionEvent event) throws IOException, SQLException {
        //searching is only different from LoadCustomerData(), that it loads only for s specified customer, that would be extracter from the accountNumber Textfield.
        //same as the LoadCustomerData

        try {
            con = DbConnection.Connection();
            data = FXCollections.observableArrayList();
            ps = con.prepareStatement("SELECT * FROM Users u, Bank_Account b WHERE u.User_Type = ? and u.User_ID = b.Cust_ID and b.Account_No ='" + AccountNoField.getText() + "'");
            ps.setString(1, "C");
            rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("ID: " + rs.getString("Cust_ID"));
                data.add(new CustomerData(rs.getString("Cust_ID"), rs.getString("First_Name"), rs.getString("Last_Name"), rs.getString("User_Name"), rs.getString("Password"), rs.getString("DOB"), rs.getString("Gender"), rs.getString("CNIC"), rs.getString("Father_Name"), rs.getString("Phone_No"), rs.getString("Email"), rs.getString("Address"), rs.getString("Account_No"), rs.getString("Status"), rs.getString("Account_Create_Date"), rs.getString("Account_Create_Time")));
            } else {
                confirmation.setText("No Record Exist!!");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println(data);
        cusID.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("Id"));
        cusFirstName.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("FirstName"));
        cusUserName.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("UserName"));
        cusPassword.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("Password"));
        cusAddress.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("Address"));
        cusDOB.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("DOB"));
        cusGender.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("Gender"));
        cusCNIC.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("CNIC"));
        cusFatherName.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("FatherName"));
        cusPhone.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("Phone"));
        cusEmail.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("Email"));
        cusAddress.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("Address"));
        Accountno.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("AccountNo"));
        cusStatus.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("Status"));
        cusCreationDate.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("CreationDate"));
        cusCreationTime.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("CreationTime"));

        CustomerTable.setItems(data);

        con.close();
    }

    public void AddCustomer(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/AddCustomer.fxml")); //setting up the FXML for the interface of AddCustomer
        loader.load();
        Parent root = loader.getRoot();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/AddCustomer.css"); //stylesheet
        Stage stage = (Stage) back.getScene().getWindow();
        AddCustomerController aic = loader.getController();
        aic.getUserID(UserID); //we are setting the the EmployeeID only to know which employee was responsible for adding this customer.
        stage.setResizable(false);
        stage.setMaximized(true);
        stage.setTitle("Add Customer");
        stage.setScene(scene);
        stage.show();
    }

    public void DeleteCustomer(ActionEvent event) throws IOException, SQLException {

        try {
            con = DbConnection.Connection();
            System.out.println("Delete Customer");
            //extracting the selected row
            CustomerData customerData = CustomerTable.getSelectionModel().getSelectedItem();
            String Cust_ID = customerData.getId(); //getting the Id of the selected customer
            CustomerTable.getItems().removeAll(customerData);
            //we will remove the account of the customer which is located in BankAccount table, as well as the customer himself which was present in User table.
            ps = con.prepareStatement("DELETE FROM Users WHERE User_ID = ?");
            ps.setString(1, Cust_ID);
            int rs = ps.executeUpdate();
            if (rs >= 1) {
                ps = con.prepareStatement("DELETE FROM Bank_Account WHERE Cust_ID = ?");
                ps.setString(1, Cust_ID);
                int rs2 = ps.executeUpdate();
                if (rs2 >= 1) { //it means that the customer has been deleted succeffully, means it was already present,like it could happen that we try to delete a customer which already have been deleted.
                    System.out.println("Customer Deleted");
                    confirmation.setText("Customer Deleted Successfully");
                } else {
                    System.out.println("Customer Delete from Users table not the Bank_Account table");
                    confirmation.setText("Customer Not Deleted Successfully from both Table");
                }
            } else {
                System.out.println("Customer Not Deleted");
                confirmation.setText("Customer Not Deleted at all");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        con.close();
    }

    public void EditCustomer(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/EditCustomer.fxml")); //setting up the EditCustomer GUI
        loader.load();
        Parent root = loader.getRoot();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/EditCustomer.css"); //Applying the stylesheet.
        Stage stage = (Stage) back.getScene().getWindow();
        EditCustomerController aic = loader.getController();
        try {

            //extracting the selected row
            CustomerData customerData = CustomerTable.getSelectionModel().getSelectedItem();
            String Cust_ID = customerData.getId(); //getting the userID of selected row
            aic.getUserID(UserID, Cust_ID); //giving the userID to EditCustomer, so we can easily load the information for the customer that is being selected and also the adminID, so we can comeback on this page
        } catch (Exception e) {
            System.out.println(e);
        }

        stage.setResizable(false);
        stage.setMaximized(true);
        stage.setTitle("Edit Customer");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
}
