package Admin;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.*;
import java.util.Optional;

public class Payments extends Application {
    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/restaurant_app?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "hackerpcps@9812";

    // UI Components
    private final TextField tableIdField = new TextField();
    private final TextField amountField = new TextField();
    private final ComboBox<String> methodComboBox = new ComboBox<>();
    private final ComboBox<String> statusComboBox = new ComboBox<>();
    private final TextArea outputArea = new TextArea();
    private final Button addButton = new Button("Add Payment");
    private final Button updateButton = new Button("Update Payment");
    private final Button deleteButton = new Button("Delete Payment");
    private final Button clearButton = new Button("Clear");
    private final Button findButton = new Button("Find Payment");

    private Connection connection;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize database connection
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Create UI
            VBox root = new VBox(10);
            root.setPadding(new Insets(10));
            root.setStyle("-fx-background-color: lightblue;");

            // Form layout
            GridPane form = new GridPane();
            form.setHgap(10);
            form.setVgap(10);
            form.setPadding(new Insets(10));
            form.setStyle("-fx-background-color: aliceblue; -fx-border-color: lightblue; -fx-border-width: 2;");

            form.add(new Label("Table ID:"), 0, 0);
            form.add(tableIdField, 1, 0);
            form.add(new Label("Amount:"), 0, 1);
            form.add(amountField, 1, 1);
            form.add(new Label("Payment Method:"), 0, 2);
            form.add(methodComboBox, 1, 2);
            form.add(new Label("Status:"), 0, 3);
            form.add(statusComboBox, 1, 3);

            // Initialize combo boxes
            methodComboBox.setItems(FXCollections.observableArrayList("Cash", "Credit Card", "UPI", "Wallet"));
            statusComboBox.setItems(FXCollections.observableArrayList("Pending", "Complete"));

            // Button panel
            HBox buttonPanel = new HBox(10);
            buttonPanel.getChildren().addAll(addButton, updateButton, deleteButton, findButton, clearButton);
            String buttonStyle = "-fx-background-color: lightyellow; -fx-border-color: gray; -fx-border-width: 1;";
            addButton.setStyle(buttonStyle);
            updateButton.setStyle(buttonStyle);
            deleteButton.setStyle(buttonStyle);
            findButton.setStyle(buttonStyle);
            clearButton.setStyle(buttonStyle);

            // Output area
            outputArea.setEditable(false);
            outputArea.setPrefHeight(150);
            outputArea.setStyle("-fx-control-inner-background: aliceblue; -fx-background-color: lightblue;");

            // Add components to root
            root.getChildren().addAll(form, buttonPanel, outputArea);

            // Set up event handlers
            addButton.setOnAction(e -> handleAddPayment());
            updateButton.setOnAction(e -> handleUpdatePayment());
            deleteButton.setOnAction(e -> handleDeletePayment());
            findButton.setOnAction(e -> handleFindPayment());
            clearButton.setOnAction(e -> clearFields());

            // Enable find button based on tableIdField
            findButton.setDisable(true);
            tableIdField.textProperty().addListener((obs, oldVal, newVal) -> {
                findButton.setDisable(newVal.trim().isEmpty());
            });

           

            // Set up scene and stage
            Scene scene = new Scene(root, 500, 400);
            primaryStage.setTitle("Payment Management System");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to connect to database: " + e.getMessage());
        }
    }

    private void handleAddPayment() {
        if (!validateInput()) return;

        try {
            String query = "INSERT INTO payment (TableID, Amount, Payment_Methods, Payment_Status) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, Integer.parseInt(tableIdField.getText()));
            pstmt.setString(2, amountField.getText());
            pstmt.setString(3, methodComboBox.getValue());
            pstmt.setString(4, statusComboBox.getValue());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                outputArea.appendText("Payment added successfully!\n");
                clearFields();
            } else {
                outputArea.appendText("Failed to add payment.\n");
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to add payment: " + e.getMessage());
        }
    }

    private void handleUpdatePayment() {
        if (!validateInput()) return;

        try {
            String query = "UPDATE payment SET Amount = ?, Payment_Methods = ?, Payment_Status = ? WHERE TableID = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, amountField.getText());
            pstmt.setString(2, methodComboBox.getValue());
            pstmt.setString(3, statusComboBox.getValue());
            pstmt.setInt(4, Integer.parseInt(tableIdField.getText()));

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                outputArea.appendText("Payment updated successfully!\n");
                clearFields();
                updateButton.setDisable(true);
                deleteButton.setDisable(true);
            } else {
                outputArea.appendText("No payment found for Table ID: " + tableIdField.getText() + "\n");
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to update payment: " + e.getMessage());
        }
    }

    private void handleDeletePayment() {
        if (tableIdField.getText().isEmpty()) {
            showAlert("Input Error", "Table ID is required");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Payment");
        alert.setContentText("Are you sure you want to delete this payment record?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                String query = "DELETE FROM payment WHERE TableID = ?";
                PreparedStatement pstmt = connection.prepareStatement(query);
                pstmt.setInt(1, Integer.parseInt(tableIdField.getText()));

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    outputArea.appendText("Payment deleted successfully!\n");
                    clearFields();
                    updateButton.setDisable(true);
                    deleteButton.setDisable(true);
                } else {
                    outputArea.appendText("No payment found for Table ID: " + tableIdField.getText() + "\n");
                }
            } catch (SQLException e) {
                showAlert("Database Error", "Failed to delete payment: " + e.getMessage());
            }
        }
    }

    private void handleFindPayment() {
        if (tableIdField.getText().isEmpty()) {
            showAlert("Input Error", "Table ID is required");
            return;
        }

        try {
            String query = "SELECT * FROM payment WHERE TableID = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, Integer.parseInt(tableIdField.getText()));

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                amountField.setText(rs.getString("Amount"));
                methodComboBox.setValue(rs.getString("Payment_Methods"));
                statusComboBox.setValue(rs.getString("Payment_Status"));
                outputArea.appendText("Payment found for Table ID: " + tableIdField.getText() + "\n");
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
            } else {
                outputArea.appendText("No payment found for Table ID: " + tableIdField.getText() + "\n");
                clearFieldsExceptTableId();
                updateButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to find payment: " + e.getMessage());
        }
    }

    private void clearFields() {
        tableIdField.clear();
        amountField.clear();
        methodComboBox.getSelectionModel().clearSelection();
        statusComboBox.getSelectionModel().clearSelection();
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        findButton.setDisable(true);
    }

    private void clearFieldsExceptTableId() {
        amountField.clear();
        methodComboBox.getSelectionModel().clearSelection();
        statusComboBox.getSelectionModel().clearSelection();
    }

    private boolean validateInput() {
        if (tableIdField.getText().isEmpty()) {
            showAlert("Input Error", "Table ID is required");
            return false;
        }

        try {
            int tableId = Integer.parseInt(tableIdField.getText());
            if (!validateTableIdExists(tableId)) {
                showAlert("Input Error", "Table ID does not exist in the tables table");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Table ID must be a number");
            return false;
        }

        if (amountField.getText().isEmpty()) {
            showAlert("Input Error", "Amount is required");
            return false;
        }

        try {
            Double.parseDouble(amountField.getText());
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Amount must be a valid number");
            return false;
        }

        if (methodComboBox.getValue() == null) {
            showAlert("Input Error", "Payment method is required");
            return false;
        }

        if (statusComboBox.getValue() == null) {
            showAlert("Input Error", "Status is required");
            return false;
        }

        return true;
    }

    private boolean validateTableIdExists(int tableId) {
        try {
            String query = "SELECT 1 FROM tables WHERE TableID = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, tableId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            showAlert("Database Error", "Error validating Table ID: " + e.getMessage());
            return false;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void stop() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        super.stop();
    }
}