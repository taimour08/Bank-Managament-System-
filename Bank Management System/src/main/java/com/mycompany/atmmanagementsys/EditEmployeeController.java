package com.mycompany.atmmanagementsys;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.jws.soap.SOAPBinding;

public class EditEmployeeController implements Initializable {
    @FXML
    private ImageView empImage;
    @FXML
    private Label empFirstName;
    @FXML
    private Label empLastName;
    @FXML
    private Label empCNIC;
    @FXML
    private Label empPhone;
    @FXML
    private Label empEmail;
    @FXML
    private TextField empDOB;
    @FXML
    private TextField empGender;
    @FXML
    private TextField empAddress;
    @FXML
    private TextField empFatherName;
    private File file;
    private FileInputStream fis;
    @FXML
    private Button chosepic;
    int cp;
    @FXML
    private Label confirmation;
    public Button back;
    String Emp_ID;
    String UserID;
    Connection con;
    PreparedStatement ps = null;
    ResultSet rs = null;

    //initializing the information of the employee
    public void getUserID(String userid, String empid) throws SQLException, IOException {
        UserID = userid;
        Emp_ID = empid;
        LoadEmpInfo();
    }

    public void backPressed(ActionEvent event) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/ManageEmployee.fxml")); //loading the manage employee screen
        loader.load();
        Parent root = loader.getRoot();
        ManageEmployeeController upc = loader.getController();
        upc.getUserID(UserID); //setting the userID , so we can reload all the information correctly.
        Stage stage = (Stage) back.getScene().getWindow();
        stage.setTitle("Admin Page");
        stage.setMaximized(true);
        stage.setResizable(false);
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/AdminPage.css");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    //uploading the picture
    public void ChoosePicture(ActionEvent event) {
        FileChooser fc = new FileChooser();
        file = fc.showOpenDialog(null);
        if (file != null) {
            Image image = new Image(file.toURI().toString());
            empImage.setImage(image);
            cp = 1;

        } else {
            cp = 0;
        }
    }

    @FXML
    public void LoadEmpInfo() throws SQLException, FileNotFoundException, IOException {
        try {
            con = DbConnection.Connection();
            ps = con.prepareStatement("Select * from Users where User_ID = ?");
            ps.setString(1, Emp_ID);
            rs = ps.executeQuery();
            //loading all the information of the employee
            if (rs.next()) {
                empFirstName.setText(rs.getString("First_Name"));
                empLastName.setText(rs.getString("Last_Name"));
                empCNIC.setText(rs.getString("CNIC"));
                empDOB.setText(rs.getString("DOB"));
                empGender.setText(rs.getString("Gender"));
                empFatherName.setText(rs.getString("Father_Name"));
                empPhone.setText(rs.getString("Phone_No"));
                empEmail.setText(rs.getString("Email"));
                empAddress.setText(rs.getString("Address"));
                InputStream img = rs.getBinaryStream("User_Image");
                OutputStream os = new FileOutputStream(new File("userimage.jpg"));
                byte[] content = new byte[1024];
                int s = 0;
                while ((s = img.read(content)) != -1) {
                    os.write(content, 0, s);
                }
                Image image = new Image("file:userimage.jpg");
                empImage.setImage(image);
            } else {
                confirmation.setText("Employee Not Found");
            }

        } catch (IOException | NumberFormatException | SQLException e) {
            confirmation.setText("Please Enter The ID Correctly");
            System.out.println(String.valueOf(e));
        }
        con.close();
    }

    public boolean checkDOB(String DOB) {
        //verifying correct format of DOB
        StringTokenizer st = new StringTokenizer(DOB, "-");
        String day = st.nextToken();
        String month = st.nextToken();
        String year = st.nextToken();

        if (day.length() >= 1 && day.length() <= 2 && Integer.parseInt(day) >= 1 && Integer.parseInt(day) <= 31) {
            if (month.length() >= 1 && month.length() <= 2 && Integer.parseInt(month) >= 1 && Integer.parseInt(month) <= 12) {
                if (year.length() == 4) {
                    return true;
                }
            }
        }
        return false;
    }

    public void UpdateEmployeeInfo(ActionEvent event) throws SQLException, FileNotFoundException {
        try { //if any invalid input
            if (empDOB.getText().isEmpty() || empGender.getText().isEmpty() || empFatherName.getText().isEmpty() || empAddress.getText().isEmpty()) {
                confirmation.setText("Please Fill Up Everything");
            } else if (checkDOB(empDOB.getText()) == false) {
                confirmation.setText("Please Enter The Date of Birth Correctly");
            } else if (!(empGender.getText().compareTo("F") == 0 || empGender.getText().compareTo("M") == 0 || empGender.getText().compareTo("-") == 0)) {
                confirmation.setText("Please Enter The Gender Correctly");
            } else {
                if (cp == 1) { //if valid input and new picture is uploaded
                    con = DbConnection.Connection();
                    ps = con.prepareStatement("UPDATE Users SET DOB = ? , Gender = ? , Father_Name = ? , Address = ?, User_Image = ? WHERE User_ID = ?");
                    ps.setString(1, empDOB.getText());
                    ps.setString(2, empGender.getText());
                    ps.setString(3, empFatherName.getText());
                    ps.setString(4, empAddress.getText());
                    fis = new FileInputStream(file);
                    ps.setBinaryStream(5, (InputStream) fis, (int) file.length());
                    ps.setString(6, Emp_ID);
                    int i = ps.executeUpdate();
                    if (i > 0) { //query completed successfully
                        confirmation.setText("Employee Info Updated Successfully");
                    } else { //query didn't completed successfully
                        confirmation.setText("Failed To Update Employee Info");
                    }
                    cp = 0;

                } else if (cp != 1) { //a valid input and no new picture uploaded
                    con = DbConnection.Connection();
                    ps = con.prepareStatement("UPDATE Users SET DOB = ? , Gender = ? , Father_Name = ? , Address = ? WHERE User_ID = ?");
                    ps.setString(1, empDOB.getText());
                    ps.setString(2, empGender.getText());
                    ps.setString(3, empFatherName.getText());
                    ps.setString(4, empAddress.getText());
                    ps.setString(5, Emp_ID);
                    int i = ps.executeUpdate();
                    if (i > 0) { //query completed successfully
                        confirmation.setText("Employee Info Updated Successfully");
                    } else { //query didn't completed successfully
                        confirmation.setText("Failed To Update Employee Info");
                    }
                    cp = 0;
                }
            }
        } catch (FileNotFoundException | NumberFormatException | SQLException e) {
            confirmation.setText("Please Enter Everything Correctly");
            System.out.println(String.valueOf(e));
        }
        con.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
}
