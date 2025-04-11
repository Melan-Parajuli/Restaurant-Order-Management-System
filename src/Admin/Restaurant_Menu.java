package Admin;

import javafx.application.Application;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.sql.*;

public class Restaurant_Menu extends Application {

	private TextField idField, nameField, categoryField, priceField;

	@Override
	public void start(Stage primaryStage) {
		VBox mainContainer = new VBox(10);
		mainContainer.setAlignment(Pos.TOP_CENTER);
		mainContainer.setPadding(new Insets(20));

		// Set light blue background
		mainContainer
				.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));

		Label lblTitle = new Label("Menu Items");
		lblTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

		GridPane inputGrid = createInputGrid();

		mainContainer.getChildren().addAll(lblTitle, inputGrid);

		Scene scene = new Scene(mainContainer, 500, 400);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Restaurant Menu System");
		primaryStage.show();
	}

	private GridPane createInputGrid() {
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(10));
		grid.setAlignment(Pos.CENTER);

		// Set white background for the form area
		grid.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(5), Insets.EMPTY)));

		// Text Fields
		idField = new TextField();
		idField.setPromptText("ID");
		nameField = new TextField();
		nameField.setPromptText("Name");
		categoryField = new TextField();
		categoryField.setPromptText("Category (Food/Beverage)");
		priceField = new TextField();
		priceField.setPromptText("Price");

		// Buttons with light yellow color
		Button btnAdd = new Button("Add");
		btnAdd.setStyle("-fx-background-color: lightyellow;");
		btnAdd.setOnAction(e -> handleAdd());

		Button btnUpdate = new Button("Update");
		btnUpdate.setStyle("-fx-background-color: lightyellow;");
		btnUpdate.setOnAction(e -> handleUpdate());

		Button btnDelete = new Button("Delete");
		btnDelete.setStyle("-fx-background-color: lightyellow;");
		btnDelete.setOnAction(e -> handleDelete());

		Button btnSearch = new Button("Search");
		btnSearch.setStyle("-fx-background-color: lightyellow;");
		btnSearch.setOnAction(e -> handleSearch());

		// Layout
		grid.add(new Label("ID:"), 0, 0);
		grid.add(idField, 1, 0);
		grid.add(btnSearch, 2, 0);
		grid.add(new Label("Name:"), 0, 1);
		grid.add(nameField, 1, 1);
		grid.add(new Label("Category:"), 0, 2);
		grid.add(categoryField, 1, 2);
		grid.add(new Label("Price:"), 0, 3);
		grid.add(priceField, 1, 3);

		HBox buttonBox = new HBox(10, btnAdd, btnUpdate, btnDelete);
		buttonBox.setAlignment(Pos.CENTER);
		grid.add(buttonBox, 1, 4);

		return grid;
	}

	private void handleAdd() {
		try {
			MenuItem item = new MenuItem(Integer.parseInt(idField.getText()), nameField.getText(),
					categoryField.getText(), Double.parseDouble(priceField.getText()));
			if (addItem(item)) {
				clearFields();
			}
		} catch (NumberFormatException e) {
			DatabaseConnection.showAlert("Error", "Please enter valid numbers for ID and Price");
		}
	}

	private void handleUpdate() {
		try {
			MenuItem item = new MenuItem(Integer.parseInt(idField.getText()), nameField.getText(),
					categoryField.getText(), Double.parseDouble(priceField.getText()));
			if (updateItem(item)) {
				clearFields();
			}
		} catch (NumberFormatException e) {
			DatabaseConnection.showAlert("Error", "Please enter valid numbers for ID and Price");
		}
	}

	private void handleDelete() {
		try {
			int id = Integer.parseInt(idField.getText());
			if (deleteItem(id)) {
				clearFields();
			}
		} catch (NumberFormatException e) {
			DatabaseConnection.showAlert("Error", "Please enter a valid ID");
		}
	}

	private void handleSearch() {
		try {
			int id = Integer.parseInt(idField.getText());
			String query = "SELECT * FROM restaurant_menu WHERE MenuItems_Id = ?";

			try (Connection conn = DatabaseConnection.connectDB();
					PreparedStatement stmt = conn.prepareStatement(query)) {
				stmt.setInt(1, id);
				ResultSet rs = stmt.executeQuery();

				if (rs.next()) {
					nameField.setText(rs.getString("Item_Name"));
					categoryField.setText(rs.getString("Category"));
					priceField.setText(String.valueOf(rs.getDouble("Price")));
				} else {
					DatabaseConnection.showAlert("Info", "No item found with ID: " + id);
					clearFieldsExceptId();
				}
			} catch (SQLException e) {
				DatabaseConnection.showAlert("Error", "Search failed: " + e.getMessage());
			}
		} catch (NumberFormatException e) {
			DatabaseConnection.showAlert("Error", "Please enter a valid ID");
		}
	}

	private boolean addItem(MenuItem item) {
		String query = "INSERT INTO restaurant_menu (MenuItems_Id, Item_Name, Category, Price) VALUES (?, ?, ?, ?)";
		try (Connection conn = DatabaseConnection.connectDB(); PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setInt(1, item.getMenuItemsId());
			stmt.setString(2, item.getItemName());
			stmt.setString(3, item.getCategory());
			stmt.setDouble(4, item.getPrice());
			stmt.executeUpdate();

			DatabaseConnection.showAlert("Success", "Item added successfully");
			return true;
		} catch (SQLException e) {
			DatabaseConnection.showAlert("Error", "Failed to add item: " + e.getMessage());
			return false;
		}
	}

	private boolean updateItem(MenuItem item) {
		String query = "UPDATE restaurant_menu SET Item_Name = ?, Category = ?, Price = ? WHERE MenuItems_Id = ?";
		try (Connection conn = DatabaseConnection.connectDB(); PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setString(1, item.getItemName());
			stmt.setString(2, item.getCategory());
			stmt.setDouble(3, item.getPrice());
			stmt.setInt(4, item.getMenuItemsId());
			int rowsAffected = stmt.executeUpdate();

			if (rowsAffected > 0) {
				DatabaseConnection.showAlert("Success", "Item updated successfully");
				return true;
			} else {
				DatabaseConnection.showAlert("Error", "No item found with ID: " + item.getMenuItemsId());
				return false;
			}
		} catch (SQLException e) {
			DatabaseConnection.showAlert("Error", "Failed to update item: " + e.getMessage());
			return false;
		}
	}

	private boolean deleteItem(int id) {
		String query = "DELETE FROM restaurant_menu WHERE MenuItems_Id = ?";
		try (Connection conn = DatabaseConnection.connectDB(); PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setInt(1, id);
			int rowsAffected = stmt.executeUpdate();

			if (rowsAffected > 0) {
				DatabaseConnection.showAlert("Success", "Item deleted successfully");
				return true;
			} else {
				DatabaseConnection.showAlert("Error", "No item found with ID: " + id);
				return false;
			}
		} catch (SQLException e) {
			DatabaseConnection.showAlert("Error", "Failed to delete item: " + e.getMessage());
			return false;
		}
	}

	private void clearFields() {
		idField.clear();
		nameField.clear();
		categoryField.clear();
		priceField.clear();
	}

	private void clearFieldsExceptId() {
		nameField.clear();
		categoryField.clear();
		priceField.clear();
	}

	public static void main(String[] args) {
		launch(args);
	}
}

class MenuItem {
	private final IntegerProperty menuItemsId;
	private final StringProperty itemName;
	private final StringProperty category;
	private final DoubleProperty price;

	public MenuItem(int menuItemsId, String itemName, String category, double price) {
		this.menuItemsId = new SimpleIntegerProperty(menuItemsId);
		this.itemName = new SimpleStringProperty(itemName);
		this.category = new SimpleStringProperty(category);
		this.price = new SimpleDoubleProperty(price);
	}

	public IntegerProperty menuItemsIdProperty() {
		return menuItemsId;
	}

	public StringProperty itemNameProperty() {
		return itemName;
	}

	public StringProperty categoryProperty() {
		return category;
	}

	public DoubleProperty priceProperty() {
		return price;
	}

	public int getMenuItemsId() {
		return menuItemsId.get();
	}

	public String getItemName() {
		return itemName.get();
	}

	public String getCategory() {
		return category.get();
	}

	public double getPrice() {
		return price.get();
	}
}

class DatabaseConnection {
	public static Connection connectDB() {
		try {
			return DriverManager.getConnection("jdbc:mysql://localhost:3306/restaurant_app", "root", "hackerpcps@9812");
		} catch (SQLException e) {
			showAlert("Database Error", "Failed to connect: " + e.getMessage());
			return null;
		}
	}

	public static void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}