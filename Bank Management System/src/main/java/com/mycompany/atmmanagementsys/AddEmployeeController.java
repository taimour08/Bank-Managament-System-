package com.mycompany.atmmanagementsys;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AddEmployeeController implements Initializable {
    @FXML
    private TextField empFirstName;
    @FXML
    private TextField empLastName;
    @FXML
    private TextArea empAddress;
    @FXML
    private TextField empPhone;
    @FXML
    private TextField empGender;
    @FXML
    private TextField empCNIC;
    @FXML
    private TextField empFatherName;
    @FXML
    private Button Add;
    @FXML
    private Button back;
    @FXML
    private Label Addempconfirm;
    @FXML
    private TextField empEmail;
    @FXML
    private TextField UserName;
    @FXML
    private CheckBox ShowPasswordcbox;
    @FXML
    private Button browse;
    private File file;
    @FXML
    private DatePicker DOB;
    @FXML
    private ImageView userimage;
    @FXML
    private PasswordField empPass;
    private FileInputStream fis;
    int cf;
    String UserID;
    String Password;
    Connection con;
    PreparedStatement ps;
    ResultSet rs;
    @FXML
    // Setting Image for Customer if Found
    public void ChooseFile(ActionEvent event) {
        FileChooser fc = new FileChooser();
        file = fc.showOpenDialog(null);
        if (file != null) {
            Image image = new Image(file.toURI().toString());
            userimage.setImage(image);
            cf = 1;
        } else {
            cf = 0;
        }
    }

    public void getUserID(String Id) {
        UserID = Id;
    }

    // If back Button pressed at this Page then we go to the previous Page and also send
    // Data like user information to be displayed on the Previous Page here
    public void backPressed(ActionEvent event) throws IOException, SQLException
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/ManageEmployee.fxml"));
        loader.load();
        Parent root = loader.getRoot();
        ManageEmployeeController upc = loader.getController();
        upc.getUserID(UserID);
        Stage stage = (Stage) back.getScene().getWindow();
        stage.setTitle("Admin Page");
        stage.setMaximized(true);
        stage.setResizable(false);
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/AdminPage.css");
        stage.setScene(scene);
        stage.show();
    }

    // to Toggle Password Field Visibility On/Off
    public void ShowPassword(ActionEvent event) throws SQLException, FileNotFoundException {
        if (ShowPasswordcbox.isSelected()) {
            Password = empPass.getText();
            empPass.clear();
            empPass.setPromptText(Password);
        } else {
            empPass.setText(Password);
            empPass.setPromptText("Password");
        }
    }

    // If Email Does not Contain a '@' the this Text is not considered as an Email
    public boolean EmailValidation(String email) {
        for (int i = 0; email.charAt(i) != '\0'; i++) {
            if (email.charAt(i) == '@') {
                return true;
            }
        }
        return false;
    }

    // utility Function to Tokenize Data string to a certain Format i.e dd-mm-yyyy
    public String setDateStraight(String dob) {
        StringTokenizer st = new StringTokenizer(dob, "-");
        String year = st.nextToken();
        String month = st.nextToken();
        String day = st.nextToken();

        return day + "-" + month + "-" + year;
    }

    @FXML
    // Add Employee Button pressed and if all checks are ok then Employee is Created and
    //  DB is Updated
    public void AddEmployee(ActionEvent event) throws SQLException, FileNotFoundException
    {
        try {
            // Printing Fields Values for Debugging purposes.
            System.out.println("First Name: " + empFirstName.getText());
            System.out.println("Last Name: " + empLastName.getText());
            System.out.println("DOB: " + DOB.getValue());
            System.out.println("Gender: " + empGender.getText());
            System.out.println("CNIC: " + empCNIC.getText());
            System.out.println("Father Name: " + empFatherName.getText());
            System.out.println("Phone no: " + empPhone.getText());
            System.out.println("Email: " + empEmail.getText());
            System.out.println("Address: " + empAddress.getText());
            System.out.println("User Name: " + UserName.getText());
            System.out.println("Password: " + empPass.getText());

            String dob = DOB.getValue() + "";
            empPass.setText(Password);
            empPass.setPromptText("Password");
            ShowPasswordcbox.setSelected(false);

            // Below Check is if any Field was left empty then Display Error Msg
            if (dob == "" || empPass.getText().isEmpty() || empFirstName.getText().isEmpty() || empLastName.getText().isEmpty() || empAddress.getText().isEmpty() || empEmail.getText().isEmpty() || empPhone.getText().isEmpty() || empGender.getText().isEmpty() || empCNIC.getText().isEmpty() || UserName.getText().isEmpty() || empFatherName.getText().isEmpty())
            {
                Addempconfirm.setText("Please Fill Up Everything");
            }
            else if (!((empGender.getText().equals("M")) || (empGender.getText().equals("F")) || (empGender.getText().equals("-")))) // Input for Gender Should be Either M, F or -
            {
                Addempconfirm.setText("Please enter gender as F, M or - ");
            }
            else if (EmailValidation(empEmail.getText()) == false) // If Email is Not Correctly Entered.
            {
                Addempconfirm.setText("Please Enter Correct Email Address");
            }
            else // All was fine
            {
                if (cf != 1) // if User has not uploaded an Image then we won't enter his Information in DB
                {
                    Addempconfirm.setText("Please Upload An Image To Add New Employee");
                }
                else if (cf == 1)  // Image Uploaded
                {
                    try {
                        int ID = 0;
                        con = DbConnection.Connection();
                        String query = "select * from Users";
                        try (Statement stmt = con.createStatement())
                        {
                            rs = stmt.executeQuery(query);
                            while (rs.next()) // Here we are getting Last ID in DB to Make New ID for New Employee
                            {
                                String userId = rs.getString("User_ID");
                                StringTokenizer st = new StringTokenizer(userId, "-");
                                String str = st.nextToken();
                                ID = Integer.parseInt(st.nextToken());
                            }
                        }
                        catch (SQLException e)
                        {
                            System.out.printf(String.valueOf(e));
                            System.out.printf("Get ID Query didn't work");
                        }

                        // Insertion into Users Table here
                        ps = con.prepareStatement("INSERT INTO Users VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                        ps.setString(1, "E-000" + String.valueOf(ID + 1));
                        ps.setString(2, "E");
                        ps.setString(3, empFirstName.getText());
                        ps.setString(4, empLastName.getText());
                        ps.setString(5, setDateStraight(dob));
                        ps.setString(6, empGender.getText());
                        ps.setString(7, empCNIC.getText());
                        ps.setString(8, empFatherName.getText());
                        ps.setString(9, empPhone.getText());
                        ps.setString(10, empEmail.getText());
                        ps.setString(11, empAddress.getText());
                        ps.setString(12, UserName.getText());
                        ps.setString(13, empPass.getText());

                        fis = new FileInputStream(file);
                        ps.setBinaryStream(14, (InputStream) fis, (int) file.length());
                        int i = ps.executeUpdate();

                        if (i > 0) // successful in inserting customer to user table
                        { // clearing the txt fields
                            empFirstName.setText("");
                            empLastName.setText("");
                            DOB.setValue(LocalDate.now());
                            empGender.setText("");
                            empEmail.setText("");
                            empCNIC.setText("");
                            UserName.setText("");
                            empPhone.setText("");
                            empPass.setText("");
                            empFatherName.setText("");
                            empAddress.setText("");
                            ShowPasswordcbox.setSelected(false);
                            Addempconfirm.setText("New Employee Added Successfully");
                            cf = 0;
                            Image image = new Image("/icons/usericon1.png");
                            userimage.setImage(image);
                        }
                        else
                        {
                            Addempconfirm.setText("Failed To Add New Employee");
                        }

                    }
                    catch (FileNotFoundException | NumberFormatException | SQLException e)
                    {
                        System.out.println(e);
                        Addempconfirm.setText("Invalid Employee ID or ID Is Not Available");
                    }
                }
            }
        }
        catch (NumberFormatException e)
        {
            System.out.println(e);
            Addempconfirm.setText("Please Enter Everything Correctly");
        }

        con.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) { }
}
