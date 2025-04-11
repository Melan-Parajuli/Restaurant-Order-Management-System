package Staffs;

import Admin.AdminManagement;
import file.Dashboard;
import file.Main;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class staff_interface extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {

		// Take customer orders, edit or update orders, view table status

		Label lblTitle, lbltakeorders, lbleditorders, lblviewtable;
		Button btn1, btn2, btn3, btn4, btn5;
		Font labelFont1 = new Font("Times New Roman", 24);
		Font labelFont2 = new Font("Times New Roman", 17);

		lblTitle = new Label("	Welcome,  Staff	");
		lblTitle.setFont(labelFont1);
		lblTitle.relocate(100, 20);

		lbltakeorders = new Label("Take Orders:");
		lbltakeorders.setFont(labelFont2);
		lbltakeorders.relocate(20, 80);
		btn1 = new Button("Enter");
		btn1.setFont(labelFont2);
		btn1.setStyle("-fx-background-radius: 5;");
		btn1.setOnAction(e -> new OrderManagement().start(new Stage()));
		btn1.relocate(140, 80);

		lbleditorders = new Label("Edit Orders:");
		lbleditorders.setFont(labelFont2);
		lbleditorders.relocate(20, 130);
		btn2 = new Button("Enter");
		btn2.setFont(labelFont2);
		btn2.setStyle("-fx-background-radius: 5;");
		btn2.setOnAction(e -> new UpdateOrders().start(new Stage()));
		btn2.relocate(140, 130);

		lblviewtable = new Label("View Table:");
		lblviewtable.setFont(labelFont2);
		lblviewtable.relocate(20, 180);
		btn3 = new Button("Enter");
		btn3.setFont(labelFont2);
		btn3.setStyle("-fx-background-radius: 5;");
		btn3.setOnAction(e -> new ViewTables().start(new Stage()));
		btn3.relocate(140, 180);

		// Button for back
		btn5 = new Button("Logout");
		btn5.setFont(labelFont2);
		btn5.setStyle("-fx-background-radius: 5;");
		btn5.relocate(150, 300);
		btn5.setOnAction(event -> {
			try {
				// Close the current Admin_Interface stage
				primaryStage.close();

				// Create a new stage for the login screen
				Stage loginStage = new Stage();
				Main main = new Main();
				main.start(loginStage); // Start the Main application
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		


		btn4 = new Button("Back");
		btn4.setFont(labelFont2);
		btn4.setStyle("-fx-background-radius: 5;");
		btn4.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				Stage Next = new Stage();
				try {
					new Dashboard().start(Next);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		btn4.relocate(230, 300);
		
		
		Pane pane = new Pane();
		pane.setStyle("-fx-background-color: #ADD8E6;"); // Light blue background
		Scene scene = new Scene(pane, 500, 400);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.setAlwaysOnTop(true);

		pane.getChildren().addAll(lblTitle, lbltakeorders, lbleditorders, lblviewtable);
		pane.getChildren().addAll(btn1, btn2, btn3, btn4, btn5);
		primaryStage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}

}
