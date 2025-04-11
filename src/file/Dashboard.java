package file;

import Admin.Admin_Interface;
import Customer.customer_interface;
import Staffs.PlaceOrders;
import Staffs.staff_interface;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Dashboard extends Application {
	@Override
	public void start(Stage primaryStage) {
		Button btnAdmin = new Button("Admin");
		btnAdmin.relocate(205, 100);
		btnAdmin.setStyle("-fx-background-radius: 5;");
		btnAdmin.setPrefSize(55, 50);

		Button btnStaff = new Button("Staffs");
		btnStaff.relocate(205, 160);

		btnStaff.setStyle("-fx-background-radius: 5;");
		btnStaff.setPrefSize(55, 50);

		Button btnUser = new Button("Customer");
		btnUser.relocate(205, 220);
		btnUser.setStyle("-fx-background-radius: 5;");
		btnUser.setPrefSize(70, 50);

		btnAdmin.setOnAction(e -> openAdminPanel());
		btnStaff.setOnAction(e -> openStaffPanel());
		btnUser.setOnAction(e -> openCustomerPanel());

		Pane pane = new Pane(btnAdmin, btnStaff, btnUser);
		pane.setStyle("-fx-background-color: #ADD8E6; -fx-border-color: gray; -fx-border-width: 1;");

		Scene scene = new Scene(pane, 500, 400);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Dashboard");
		primaryStage.show();
	}

	private void openAdminPanel() {
		try {
			new Admin_Interface().start(new Stage());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void openStaffPanel() {
		try {
			new staff_interface().start(new Stage());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void openCustomerPanel() {
		try {
			new customer_interface().start(new Stage());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
