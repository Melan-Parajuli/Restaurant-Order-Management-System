package file;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.sql.*;

public class Register extends Application {
    // Database connection parameters
    private static final String URL = "jdbc:mysql://localhost:3306/restaurant_app";
    private static final String USER = "root";
    private static final String PASSWORD = "hackerpcps@9812";

    // Get database connection
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Method to register new admin
    private boolean registerAdmin(String name, String username, String password, String phone) {
        String sql = "INSERT INTO admin_ (name, username, password, Phone) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setString(2, username);
            pstmt.setString(3, password);
            pstmt.setString(4, phone);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error registering admin: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Label lblTitle, lblusername, lblpassword, lblfullname, lblphone, lblStatus;
        TextField txtusername, txtpassword, txtfullname, txtphone;
        Button btn1, btn2;
        Font labelFont1 = new Font("Times New Roman", 24);
        Font labelFont2 = new Font("Times New Roman", 17);

        lblTitle = new Label("Welcome, New User");
        lblTitle.setFont(labelFont1);
        lblTitle.relocate(145, 20);

        lblfullname = new Label("fullname:");
        lblfullname.setFont(labelFont2);
        lblfullname.relocate(50, 100);
        txtfullname = new TextField();
        txtfullname.setPromptText("Enter Fullname");
        txtfullname.relocate(140, 100);

        lblusername = new Label("username:");
        lblusername.setFont(labelFont2);
        lblusername.relocate(50, 160);
        txtusername = new TextField();
        txtusername.setPromptText("Enter username");
        txtusername.relocate(140, 160);

        lblpassword = new Label("password:");
        lblpassword.relocate(50, 220);
        lblpassword.setFont(labelFont2);
        lblpassword.setTextFill(Color.BLACK);
        txtpassword = new PasswordField();
        txtpassword.relocate(140, 220);
        txtpassword.setPromptText("Enter password");

        lblphone = new Label("phone:");
        lblphone.setFont(labelFont2);
        lblphone.relocate(50, 280);
        txtphone = new TextField();
        txtphone.setPromptText("Enter phone number");
        txtphone.relocate(140, 280);

        // Status label to show registration result
        lblStatus = new Label("");
        lblStatus.setFont(labelFont2);
        lblStatus.relocate(140, 320);

        btn1 = new Button("Add");
        btn1.setFont(labelFont2);
        btn1.relocate(140, 350);
        btn1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String name = txtfullname.getText();
                String username = txtusername.getText();
                String password = txtpassword.getText();
                String phone = txtphone.getText();

                // Basic input validation
                if (name.isEmpty() || username.isEmpty() || password.isEmpty() || phone.isEmpty()) {
                    lblStatus.setText("All fields are required");
                    lblStatus.setTextFill(Color.RED);
                    return;
                }

                if (phone.length() != 10 || !phone.matches("\\d+")) {
                    lblStatus.setText("Phone must be 10 digits");
                    lblStatus.setTextFill(Color.RED);
                    return;
                }

                // Register the admin
                if (registerAdmin(name, username, password, phone)) {
                    lblStatus.setText("Registration successful!");
                    lblStatus.setTextFill(Color.GREEN);
                    // Clear fields after successful registration
                    txtfullname.clear();
                    txtusername.clear();
                    txtpassword.clear();
                    txtphone.clear();
                } else {
                    lblStatus.setText("Registration failed");
                    lblStatus.setTextFill(Color.RED);
                }
            }
        });

        btn2 = new Button("Back");
        btn2.setFont(labelFont2);
        btn2.relocate(200, 350);
        btn2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Stage Next = new Stage();
                try {
                    new Main().start(Next);
                    primaryStage.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Pane pane = new Pane();
        pane.setStyle("-fx-background-color: #ADD8E6; -fx-border-color: gray; -fx-border-width: 1;");
        Scene scene = new Scene(pane, 520, 420);
        primaryStage.setScene(scene);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setResizable(false);

        pane.getChildren().addAll(lblTitle, lblfullname, lblusername, lblpassword, lblphone, lblStatus);
        pane.getChildren().addAll(txtusername, txtpassword, txtfullname, txtphone);
        pane.getChildren().addAll(btn1, btn2);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}