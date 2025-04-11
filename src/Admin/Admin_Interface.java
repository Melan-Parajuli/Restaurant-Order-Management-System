package Admin;

import Staffs.PlaceOrders;
import file.Dashboard;
import file.Main;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Admin_Interface extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Making Variables for Scenes
		Label lblTitle, lbl1, lbl2, lbl3, lbl4;
		TextField txt1, txt2, txt3, txt4;
		Button btn1, btn2, btn3, btn4, btn5, btn6;
		Font labelFont1 = new Font("Times New Roman", 24);
		Font labelFont2 = new Font("Times New Roman", 17);

		// Relocate of the variables
		lblTitle = new Label(" Weclome Back , Admin ");
		lblTitle.setFont(labelFont1);
		lblTitle.relocate(120, 5);

		lbl1 = new Label("Add New Users:");
		lbl1.relocate(10, 80);
		lbl1.setFont(labelFont2);
		btn1 = new Button("Enter");
		btn1.setFont(labelFont2);
		btn1.setStyle("-fx-background-radius: 5;");
		btn1.setOnAction(e -> new AdminManagement().start(new Stage()));
		btn1.relocate(140, 80);

		lbl2 = new Label("Edit Menu:");
		lbl2.relocate(10, 130);
		lbl2.setFont(labelFont2);
		btn2 = new Button("Enter");
		btn2.setFont(labelFont2);
		btn2.setStyle("-fx-background-radius: 5;");
		btn2.setOnAction(e -> new Restaurant_Menu().start(new Stage()));
		btn2.relocate(140, 130);

		lbl3 = new Label("Manage Tables:");
		lbl3.relocate(10, 180);
		lbl3.setFont(labelFont2);
		btn3 = new Button("Enter");
		btn3.setFont(labelFont2);
		btn3.setStyle("-fx-background-radius: 5;");
		btn3.setOnAction(e -> new ManageTables().start(new Stage()));
		btn3.relocate(140, 180);

		lbl4 = new Label("Take Payments:");
		lbl4.relocate(10, 230);
		lbl4.setFont(labelFont2);
		btn4 = new Button("Enter");
		btn4.setFont(labelFont2);
		btn4.setStyle("-fx-background-radius: 5;");
		btn4.setOnAction(e -> new Payments().start(new Stage()));
		btn4.relocate(140, 230);

		// Button for back
		btn5 = new Button("Logout");
		btn5.setFont(labelFont2);
		btn5.setStyle("-fx-background-radius: 5;");
		btn5.relocate(180, 300);
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

		btn6 = new Button("Back");
		btn6.setFont(labelFont2);
		btn6.setStyle("-fx-background-radius: 5;");
		btn6.relocate(260, 300);
		btn6.setOnAction(new EventHandler<ActionEvent>() {
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

		// Edits for Scene with rounded corners
		Pane pane = new Pane();
		pane.setStyle("-fx-background-color: #ADD8E6; -fx-border-color: gray; -fx-border-width: 1;");

		Scene scene = new Scene(pane);

		primaryStage.setScene(scene);
		primaryStage.setWidth(520);
		primaryStage.setHeight(420);
		primaryStage.setResizable(false);
		primaryStage.setAlwaysOnTop(true);

		// Output of the variables
		pane.getChildren().add(lblTitle);
		pane.getChildren().addAll(lbl1, lbl2, lbl3, lbl4);
		pane.getChildren().addAll(btn1, btn2, btn3, btn4, btn5, btn6);

		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}