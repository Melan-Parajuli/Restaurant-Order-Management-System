package file;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {

		// Create Variables
		Label lblusername, lblpassword, lblmessage;
		TextField txtusername, txtpassword;
		Button btnlogin, btnclose;

		// Set Buttons and their Position

		Font font1 = new Font("Arial", 25);
		Font font2 = new Font("Arial", 18);

		// Title variable with initialization
		Label lbltitle = new Label("Restaurant Order Management System");
		lbltitle.relocate(30, 5);
		lbltitle.setFont(font1);

		lblusername = new Label("Username:");
		lblusername.relocate(50, 80);
		lblusername.setFont(font2);

		lblpassword = new Label("Password:");
		lblpassword.relocate(50, 160);
		lblpassword.setFont(font2);

		lblmessage = new Label("Error Message");
		lblmessage.relocate(50, 300);
		lblmessage.setFont(font2);

		txtusername = new TextField();
		txtusername.relocate(150, 80);

		txtpassword = new TextField();
		txtpassword.relocate(150, 160);

		// Action Event for Login Button

		btnlogin = new Button("login");
		btnlogin.setOnAction(event -> System.out.println("Login Successful!"));
		btnlogin.relocate(130, 220);
		btnlogin.setFont(font2);

		// Action Event for Close Button

		btnclose = new Button("close");
		btnclose.setOnAction(event -> primaryStage.close());
		btnclose.relocate(250, 220);
		btnclose.setFont(font2);
		
		// *Login Button Action*
        btnlogin.setOnAction(e -> {
            if (txtusername.getText().isEmpty() || txtpassword.getText().isEmpty()) {
                lblmessage.setText("Enter your username and password to proceed!");
                lblmessage.setTextFill(Paint.valueOf("red"));
            } else {
                lblmessage.setText("Login successful!");
                lblmessage.setTextFill(Paint.valueOf("green"));
            }
            lblmessage.setVisible(true);
        });
        
        
        btnlogin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Stage Next = new Stage();
                try   {
                new UserCRUD().start(Next);
                } catch (Exception e) {
                 e.printStackTrace();
                 
                }
            }
        });


		Pane pane = new Pane();
		Scene scene = new Scene(pane);

		// Scene setup

		primaryStage.setScene(scene);
		primaryStage.setWidth(500);
		primaryStage.setHeight(400);
		primaryStage.setResizable(false);
		primaryStage.setAlwaysOnTop(true);
		// Display all the Buttons in the Screen

		pane.getChildren().addAll(lbltitle, lblusername, lblpassword, lblmessage);
		pane.getChildren().addAll(txtusername, txtpassword);
		pane.getChildren().addAll(btnlogin, btnclose);

		// Close

		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
