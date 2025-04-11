package file;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {

	private static final String DB_URL = "jdbc:mysql://localhost:3306/restaurant_app";
	private static final String DB_USER = "root";
	private static final String DB_PASSWORD = "hackerpcps@9812";

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Create Variables
		Label lblTitle, lblUsername, lblPassword, lblMessage;
		TextField txtUsername;
		PasswordField txtPassword;
		Button btnLogin, btnClose, btnRegister;

		// Set Fonts
		Font titleFont = new Font("Times New Roman", 25);
		Font labelFont = new Font("Times New Roman", 18);

		// Title
		lblTitle = new Label("Restaurant Order Management System");
		lblTitle.relocate(30, 5);
		lblTitle.setFont(titleFont);
		lblTitle.setTextFill(Color.BLACK);

		// Username Label and Field
		lblUsername = new Label("Username:");
		lblUsername.relocate(50, 80);
		lblUsername.setFont(labelFont);
		lblUsername.setTextFill(Color.BLACK);

		txtUsername = new TextField();
		txtUsername.relocate(150, 80);
		// txtUsername.setFont(labelFont);
		txtUsername.setPromptText("Enter username");

		// Password Label and Field
		lblPassword = new Label("Password:");
		lblPassword.relocate(50, 160);
		lblPassword.setFont(labelFont);
		lblPassword.setTextFill(Color.BLACK);

		txtPassword = new PasswordField();
		txtPassword.relocate(150, 160);
		txtPassword.setPromptText("Enter password");

		// Message Label
		lblMessage = new Label();
		lblMessage.relocate(50, 300);
		lblMessage.setFont(labelFont);
		lblMessage.setVisible(false);

		// Login Button - Changed to light yellow
		btnLogin = new Button("Login");
		btnLogin.relocate(130, 220);
		btnLogin.setFont(labelFont);
		btnLogin.setPrefWidth(100);

		// Close Button - Changed to light yellow
		btnClose = new Button("Close");
		btnClose.relocate(250, 220);
		btnClose.setFont(labelFont);
		btnClose.setPrefWidth(100);
		btnClose.setOnAction(event -> primaryStage.close());

		btnRegister = new Button("Register...");
		btnRegister.relocate(20, 350);
		btnRegister.setFont(labelFont);
		btnRegister.setPrefWidth(100);
		btnRegister.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				Stage Next = new Stage();
				try {
					new Register().start(Next);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		// Login Button Action
		btnLogin.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				String username = txtUsername.getText().trim();
				String password = txtPassword.getText().trim();

				if (username.isEmpty() || password.isEmpty()) {
					showMessage(lblMessage, "Enter your username and password to proceed!", Color.RED);
				} else {
					try {
						if (authenticateUser(username, password)) {
							showMessage(lblMessage, "Login successful!", Color.GREEN);
							openDashboard(primaryStage);
						} else {
							showMessage(lblMessage, "Invalid username or password!", Color.RED);
						}
					} catch (SQLException e) {
						showMessage(lblMessage, "Database error. Please try again.", Color.RED);
						e.printStackTrace();
					}
				}
			}
		});

		// Create Pane and Scene
		Pane pane = new Pane();
		pane.setStyle("-fx-background-color: #ADD8E6;"); // Light blue background
		pane.getChildren().addAll(lblTitle, lblUsername, lblPassword, lblMessage, txtUsername, txtPassword, btnLogin,
				btnClose, btnRegister);

		Scene scene = new Scene(pane, 500, 400);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Login");
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	private boolean authenticateUser(String username, String password) throws SQLException {
		String query = "SELECT * FROM admin_ WHERE username = ? AND password = ?";

		try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
				PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, username);
			pstmt.setString(2, password);

			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next(); // Returns true if user exists
			}
		}
	}

	private void openDashboard(Stage primaryStage) {
		Stage dashboardStage = new Stage();
		try {
			new Dashboard().start(dashboardStage);
			primaryStage.close();
		} catch (Exception e) {
			showAlert("Error", "Failed to open dashboard: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void showMessage(Label label, String message, Color color) {
		label.setText(message);
		label.setTextFill(color);
		label.setVisible(true);
	}

	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	public static void main(String[] args) {
		launch(args);
	}
}