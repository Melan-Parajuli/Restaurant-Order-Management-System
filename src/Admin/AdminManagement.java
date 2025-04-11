package Admin;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.sql.*;

public class AdminManagement extends Application {

    private TextField txtId, txtName, txtUsername, txtPassword, txtPhone;
    private Button btnFirst, btnPrevious, btnNext, btnLast;
    private Button btnAdd, btnUpdate, btnDelete, btnClear;
    private Connection connection;
    private ResultSet resultSet;
    private int currentId = 0;
    private int totalRecords = 0;

    @Override
    public void start(Stage primaryStage) {
        // Initialize database connection
        connectToDatabase();

        // Create form fields
        GridPane formPane = createFormPane();

        // Create navigation buttons
        HBox navButtons = createNavigationButtons();

        // Create action buttons
        HBox actionButtons = createActionButtons();

        // Main layout
        VBox mainPane = new VBox(20);
        mainPane.setPadding(new Insets(20));
        mainPane.setAlignment(Pos.CENTER);
        
        // Set light blue background
        mainPane.setBackground(new Background(new BackgroundFill(
            Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        
        mainPane.getChildren().addAll(formPane, navButtons, actionButtons);

        // Load first record
        loadFirstRecord();

        // Set up the scene
        Scene scene = new Scene(mainPane, 500, 400);
        primaryStage.setTitle("Admin Management");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private GridPane createFormPane() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        grid.setBackground(new Background(new BackgroundFill(
            Color.WHITE, new CornerRadii(5), Insets.EMPTY)));

        // ID Field (read-only)
        txtId = new TextField();
        txtId.setEditable(false);
        grid.add(new Label("ID:"), 0, 0);
        grid.add(txtId, 1, 0);

        // Name Field
        txtName = new TextField();
        grid.add(new Label("Name:"), 0, 1);
        grid.add(txtName, 1, 1);

        // Username Field
        txtUsername = new TextField();
        grid.add(new Label("Username:"), 0, 2);
        grid.add(txtUsername, 1, 2);

        // Password Field
        txtPassword = new PasswordField();
        grid.add(new Label("Password:"), 0, 3);
        grid.add(txtPassword, 1, 3);

        // Phone Field
        txtPhone = new TextField();
        grid.add(new Label("Phone:"), 0, 4);
        grid.add(txtPhone, 1, 4);

        return grid;
    }

    private HBox createNavigationButtons() {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER);

        btnFirst = new Button("<<");
        btnFirst.setStyle("-fx-background-color: lightyellow;");
        btnFirst.setOnAction(e -> loadFirstRecord());

        btnPrevious = new Button("<");
        btnPrevious.setStyle("-fx-background-color: lightyellow;");
        btnPrevious.setOnAction(e -> loadPreviousRecord());

        btnNext = new Button(">");
        btnNext.setStyle("-fx-background-color: lightyellow;");
        btnNext.setOnAction(e -> loadNextRecord());

        btnLast = new Button(">>");
        btnLast.setStyle("-fx-background-color: lightyellow;");
        btnLast.setOnAction(e -> loadLastRecord());

        hbox.getChildren().addAll(btnFirst, btnPrevious, btnNext, btnLast);
        return hbox;
    }

    private HBox createActionButtons() {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER);

        btnAdd = new Button("Add");
        btnAdd.setStyle("-fx-background-color: lightyellow;");
        btnAdd.setOnAction(e -> addAdmin());

        btnUpdate = new Button("Update");
        btnUpdate.setStyle("-fx-background-color: lightyellow;");
        btnUpdate.setOnAction(e -> updateAdmin());

        btnDelete = new Button("Delete");
        btnDelete.setStyle("-fx-background-color: lightyellow;");
        btnDelete.setOnAction(e -> deleteAdmin());

        btnClear = new Button("Clear");
        btnClear.setStyle("-fx-background-color: lightyellow;");
        btnClear.setOnAction(e -> clearFields());

        hbox.getChildren().addAll(btnAdd, btnUpdate, btnDelete, btnClear);
        return hbox;
    }

    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/restaurant_app", "root",
                    "hackerpcps@9812");
            Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            resultSet = stmt.executeQuery("SELECT * FROM admin_");

            // Get total records count
            resultSet.last();
            totalRecords = resultSet.getRow();
            resultSet.beforeFirst();
        } catch (SQLException e) {
            showAlert("Database Error", e.getMessage());
        }
    }

    private void loadFirstRecord() {
        try {
            if (resultSet.first()) {
                currentId = 1;
                displayRecord();
            }
        } catch (SQLException e) {
            showAlert("Error", e.getMessage());
        }
    }

    private void loadPreviousRecord() {
        try {
            if (resultSet.previous()) {
                currentId--;
                displayRecord();
            } else {
                resultSet.first();
                currentId = 1;
            }
        } catch (SQLException e) {
            showAlert("Error", e.getMessage());
        }
    }

    private void loadNextRecord() {
        try {
            if (resultSet.next()) {
                currentId++;
                displayRecord();
            } else {
                resultSet.last();
                currentId = totalRecords;
            }
        } catch (SQLException e) {
            showAlert("Error", e.getMessage());
        }
    }

    private void loadLastRecord() {
        try {
            if (resultSet.last()) {
                currentId = totalRecords;
                displayRecord();
            }
        } catch (SQLException e) {
            showAlert("Error", e.getMessage());
        }
    }

    private void displayRecord() {
        try {
            txtId.setText(String.valueOf(resultSet.getInt("AdminId")));
            txtName.setText(resultSet.getString("name"));
            txtUsername.setText(resultSet.getString("username"));
            txtPassword.setText(resultSet.getString("password"));
            txtPhone.setText(resultSet.getString("Phone"));
        } catch (SQLException e) {
            showAlert("Error", e.getMessage());
        }
    }

    private void addAdmin() {
        try {
            // Validate inputs
            if (txtName.getText().isEmpty() || txtUsername.getText().isEmpty() || txtPassword.getText().isEmpty()
                    || txtPhone.getText().isEmpty()) {
                showAlert("Error", "All fields are required");
                return;
            }

            if (!txtPhone.getText().matches("\\d{10}")) {
                showAlert("Error", "Phone must be 10 digits");
                return;
            }

            // Create new record
            resultSet.moveToInsertRow();
            resultSet.updateString("name", txtName.getText());
            resultSet.updateString("username", txtUsername.getText());
            resultSet.updateString("password", txtPassword.getText());
            resultSet.updateString("Phone", txtPhone.getText());
            resultSet.insertRow();

            // Refresh result set
            Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            resultSet = stmt.executeQuery("SELECT * FROM admin_");
            resultSet.last();
            totalRecords = resultSet.getRow();

            showAlert("Success", "Admin added successfully");
            loadLastRecord();
        } catch (SQLException e) {
            showAlert("Error", e.getMessage());
        }
    }

    private void updateAdmin() {
        try {
            if (txtId.getText().isEmpty()) {
                showAlert("Error", "No record selected");
                return;
            }

            // Validate inputs
            if (txtName.getText().isEmpty() || txtUsername.getText().isEmpty() || txtPassword.getText().isEmpty()
                    || txtPhone.getText().isEmpty()) {
                showAlert("Error", "All fields are required");
                return;
            }

            if (!txtPhone.getText().matches("\\d{10}")) {
                showAlert("Error", "Phone must be 10 digits");
                return;
            }

            // Update current record
            resultSet.updateString("name", txtName.getText());
            resultSet.updateString("username", txtUsername.getText());
            resultSet.updateString("password", txtPassword.getText());
            resultSet.updateString("Phone", txtPhone.getText());
            resultSet.updateRow();

            showAlert("Success", "Admin updated successfully");
        } catch (SQLException e) {
            showAlert("Error", e.getMessage());
        }
    }

    private void deleteAdmin() {
        try {
            if (txtId.getText().isEmpty()) {
                showAlert("Error", "No record selected");
                return;
            }

            // Confirm deletion
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Delete");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete this admin?");

            if (alert.showAndWait().get() == ButtonType.OK) {
                resultSet.deleteRow();

                // Refresh result set
                Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                resultSet = stmt.executeQuery("SELECT * FROM admin_");
                resultSet.last();
                totalRecords = resultSet.getRow();

                if (totalRecords > 0) {
                    loadFirstRecord();
                } else {
                    clearFields();
                }

                showAlert("Success", "Admin deleted successfully");
            }
        } catch (SQLException e) {
            showAlert("Error", e.getMessage());
        }
    }

    private void clearFields() {
        txtId.clear();
        txtName.clear();
        txtUsername.clear();
        txtPassword.clear();
        txtPhone.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void stop() {
        try {
            if (resultSet != null)
                resultSet.close();
            if (connection != null)
                connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}