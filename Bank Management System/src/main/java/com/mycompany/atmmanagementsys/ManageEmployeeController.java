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
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;


public class ManageEmployeeController implements Initializable {
    @FXML
    private TableView<EmployeeData> employeeTable;
    @FXML
    private TableColumn<EmployeeData, String> empID;
    @FXML
    private TableColumn<EmployeeData, String> empName;
    @FXML
    private TableColumn<EmployeeData, String> empDOB;
    @FXML
    private TableColumn<EmployeeData, String> empGender;
    @FXML
    private TableColumn<EmployeeData, String> empCNIC;
    @FXML
    private TableColumn<EmployeeData, String> empFName;
    @FXML
    private TableColumn<EmployeeData, String> empPhone;
    @FXML
    private TableColumn<EmployeeData, String> empEmail;
    @FXML
    private TableColumn<EmployeeData, String> empAddress;
    @FXML
    private TableColumn<EmployeeData, String> empPassword;
    @FXML

    private ObservableList<EmployeeData> data;
    String UserID;
    @FXML
    Button back;
    @FXML
    Button addEmpBtn;
    @FXML
    Button editEmpBtn;
    @FXML
    Label confirmation;
    @FXML
    Button delEmpBtn;
    @FXML
    TextField UserIDField;
    Connection con;
    PreparedStatement ps = null;
    ResultSet rs = null;

    public void getUserID(String Id) {
        UserID = Id;
    }//this function would be used by the previous login
    //screen that would tell this that which Admin has loged in, so everything would be performed by his Admin ID.

    public void backPressed(ActionEvent event) throws IOException, SQLException {

        try {
            con = DbConnection.Connection(); //making connection to DB
            ps = con.prepareStatement("SELECT * FROM Users WHERE User_ID = ?");  //Finding the Admin complete information by using his admin ID.
            ps.setString(1, UserID);
            rs = ps.executeQuery();
            if (rs.next()) { //If The admin Exist, means there isn't any run time problem occured so then load the admin page
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/fxml/AdminPage.fxml")); //loading the admin page

                loader.load();
                Parent root = loader.getRoot();
                AdminPageController upc = loader.getController(); //loading its controller to pass all the information, so the information would be available when we go back on admin page
                InputStream is = rs.getBinaryStream("User_Image");
                Image image = new Image("/icons/edituser.png");
                if (is != null) { //if admin has a image, then display it
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
                //passing all the information to the admin page
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
        //closing the connection, so we don't get any DB busy error

        con.close();
    }

    public void addEmployee(ActionEvent event) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/AddEmployee.fxml")); //loading AddEmployee GUI
        loader.load();
        Parent root = loader.getRoot();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/AdminPage.css"); //setting style sheet
        Stage stage = (Stage) addEmpBtn.getScene().getWindow();
        AddEmployeeController aic = loader.getController(); //Passing the information to EmployeeAdder so we know which admin has added which employee.
        aic.getUserID(UserID);
        stage.setResizable(false);
        stage.setFullScreen(true);
        stage.setTitle("Add Employee");
        stage.setScene(scene);
        stage.show();
    }

    public void loadEmployeeData(ActionEvent event) throws SQLException {

        try {
            confirmation.setText("");//initially there is no error/message so setting the message to empty
            con = DbConnection.Connection();
            data = FXCollections.observableArrayList();
            ps = con.prepareStatement("SELECT * FROM Users WHERE User_Type = ? "); //loading all the employees Data
            ps.setString(1, "E"); //here E identify that we should load only employee from the user, becuase all the Users are only differentiated by there type and are in the same table, like E- employee C- customer M- Manager
            rs = ps.executeQuery();
            while (rs.next()) {
                //adding the data to table
                data.add(new EmployeeData(rs.getString("User_ID"), rs.getString("First_Name"), rs.getString("Last_Name"), rs.getString("User_Name"), rs.getString("Password"), rs.getString("DOB"), rs.getString("Gender"), rs.getString("CNIC"), rs.getString("Father_Name"), rs.getString("Phone_No"), rs.getString("Email"), rs.getString("Address")));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println(data);
        //managing the table
        empID.setCellValueFactory(new PropertyValueFactory<EmployeeData, String>("Id"));
        empName.setCellValueFactory(new PropertyValueFactory<EmployeeData, String>("FirstName"));
        empPassword.setCellValueFactory(new PropertyValueFactory<EmployeeData, String>("Password"));
        empAddress.setCellValueFactory(new PropertyValueFactory<EmployeeData, String>("Address"));
        empDOB.setCellValueFactory(new PropertyValueFactory<EmployeeData, String>("DOB"));
        empGender.setCellValueFactory(new PropertyValueFactory<EmployeeData, String>("Gender"));
        empCNIC.setCellValueFactory(new PropertyValueFactory<EmployeeData, String>("CNIC"));
        empFName.setCellValueFactory(new PropertyValueFactory<EmployeeData, String>("FatherName"));
        empPhone.setCellValueFactory(new PropertyValueFactory<EmployeeData, String>("Phone"));
        empEmail.setCellValueFactory(new PropertyValueFactory<EmployeeData, String>("Email"));
        empAddress.setCellValueFactory(new PropertyValueFactory<EmployeeData, String>("Address"));


        employeeTable.setItems(data);
        con.close();
    }

    public void searchEmployee() throws SQLException {
        //its similar to loadEmployeeData, only difference is that we load specific employee this time, based on employeeId which we get from UserIDField

        try {
            con = DbConnection.Connection();
            data = FXCollections.observableArrayList();
            ps = con.prepareStatement("SELECT * FROM Users WHERE User_Type = ? and User_ID = '" + UserIDField.getText() + "'");
            ps.setString(1, "E");
            rs = ps.executeQuery();
            if (rs.next()) {
                data.add(new EmployeeData(rs.getString("User_ID"), rs.getString("First_Name"), rs.getString("Last_Name"), rs.getString("User_Name"), rs.getString("Password"), rs.getString("DOB"), rs.getString("Gender"), rs.getString("CNIC"), rs.getString("Father_Name"), rs.getString("Phone_No"), rs.getString("Email"), rs.getString("Address")));
            } else {
                confirmation.setText("No Record Found!!!");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println(data);
        empID.setCellValueFactory(new PropertyValueFactory<EmployeeData, String>("Id"));
        empName.setCellValueFactory(new PropertyValueFactory<EmployeeData, String>("FirstName"));
        empPassword.setCellValueFactory(new PropertyValueFactory<EmployeeData, String>("Password"));
        empAddress.setCellValueFactory(new PropertyValueFactory<EmployeeData, String>("Address"));
        empDOB.setCellValueFactory(new PropertyValueFactory<EmployeeData, String>("DOB"));
        empGender.setCellValueFactory(new PropertyValueFactory<EmployeeData, String>("Gender"));
        empCNIC.setCellValueFactory(new PropertyValueFactory<EmployeeData, String>("CNIC"));
        empFName.setCellValueFactory(new PropertyValueFactory<EmployeeData, String>("FatherName"));
        empPhone.setCellValueFactory(new PropertyValueFactory<EmployeeData, String>("Phone"));
        empEmail.setCellValueFactory(new PropertyValueFactory<EmployeeData, String>("Email"));
        empAddress.setCellValueFactory(new PropertyValueFactory<EmployeeData, String>("Address"));


        employeeTable.setItems(data);

        con.close();
    }

    public void editEmployee(ActionEvent event) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/EditEmployee.fxml")); //loading EditEmployee GUI
        loader.load();
        Parent root = loader.getRoot();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/EditCustomer.css"); //adding style sheet
        Stage stage = (Stage) editEmpBtn.getScene().getWindow();
        stage.setResizable(false);
        stage.setMaximized(true);
        try {


            EditEmployeeController aic = loader.getController();
            EmployeeData employeeData = employeeTable.getSelectionModel().getSelectedItem(); //extracting the selected employee
            aic.getUserID(UserID, employeeData.getId());//passing the ID of the selected employee, as well as the Admin, So we can comeback on this page by using adminID.
            stage.setTitle("Edit Employee");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void deleteEmployee(ActionEvent event) throws IOException, SQLException {

        try {
            con = DbConnection.Connection();
            System.out.println("Delete Employee");
            EmployeeData employeeData = employeeTable.getSelectionModel().getSelectedItem(); //extracting the selected Employee
            String Emp_ID = employeeData.getId(); //getting the employeeID of the selected employee
            employeeTable.getItems().removeAll(employeeData); //removing the entry from the tabel not the database
            ps = con.prepareStatement("DELETE FROM Users WHERE User_ID = ?");
            ps.setString(1, Emp_ID);
            int rs = ps.executeUpdate();
            if (rs >= 1) {
                System.out.println("Employee Deleted");
                confirmation.setText("Employee Deleted Successfully");
            } else {
                System.out.println("Employee Not Deleted");
                confirmation.setText("Employee  Not Deleted Successfully");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        con.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
}
