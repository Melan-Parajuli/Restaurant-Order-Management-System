package Customer;

import Admin.Restaurant_Menu;
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

public class customer_interface extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		Label lblTitle, lbl1, lbl2, lbl3;
		Button btn1, btn2, btn3, btn4, btn5;
		Font font1 = new Font("Times New Roman", 24);
		Font font2 = new Font("Times New Roman", 17);
		String buttonStyle = "-fx-background-color: lightyellow; -fx-font-size: 14px; -fx-pref-width: 200px; "
				+ "-fx-pref-height: 40px; -fx-border-color: gray; -fx-border-width: 15;";

		lblTitle = new Label("    User View    ");
		lblTitle.setFont(font1);
		lblTitle.relocate(150, 5);

		lbl1 = new Label("Add New Order:");
		lbl1.relocate(10, 80);
		lbl1.setFont(font2);
		btn1 = new Button("Enter");
		btn1.setFont(font2);
		btn1.setStyle("-fx-background-radius: 5;");
		btn1.setOnAction(e -> new NewOrder().start(new Stage()));
		btn1.relocate(140, 80);

		btn4 = new Button("Logout");
		btn4.setFont(font2);
		btn4.setStyle("-fx-background-radius: 5;");
		btn4.relocate(180, 250);
		btn4.setOnAction(event -> {
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

		btn5 = new Button("Back");
		btn5.setFont(font2);
		btn5.setStyle("-fx-background-radius: 5;");
		btn5.relocate(260, 250);
		btn5.setOnAction(new EventHandler<ActionEvent>() {
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

		Pane pane = new Pane();
		pane.setStyle("-fx-background-color: #ADD8E6; -fx-border-color: gray; -fx-border-width: 1;");
		Scene scene = new Scene(pane, 500, 400);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.setAlwaysOnTop(true);

		pane.getChildren().add(lblTitle);
		pane.getChildren().addAll(lbl1);
		pane.getChildren().addAll(btn1,  btn4, btn5);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}