package Staffs;

import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class PlaceOrders extends Application {
    // Database connection details
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/restaurant_app";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "hackerpcps@9812";
    
    // UI Components
    private TextField orderIdField = new TextField();
    private TextField tableIdField = new TextField();
    private TextField staffIdField = new TextField();
    private TextField menuIdField = new TextField();
    private TextField quantityField = new TextField();
    private ComboBox<String> statusComboBox = new ComboBox<>();
    
    private TableView<Order> orderTable = new TableView<>();
    private ObservableList<Order> orderData = FXCollections.observableArrayList();
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Order Management System");
        
        // Create form
        GridPane form = createForm();
        
        // Create buttons
        HBox buttonBox = createButtons();
        
        // Create table
        createOrderTable();
        
        // Main layout
        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(10));
        mainLayout.getChildren().addAll(form, buttonBox, orderTable);
        
        // Load data from database
        loadOrders();
        
        // Set scene with light blue background
        Scene scene = new Scene(mainLayout, 500, 400);
        scene.setFill(javafx.scene.paint.Color.LIGHTBLUE);
        
        // Apply CSS styling for buttons
        String buttonStyle = "-fx-background-color: lightyellow; -fx-border-color: gray; -fx-border-width: 1;";
        for (javafx.scene.Node node : buttonBox.getChildren()) {
            if (node instanceof Button) {
                node.setStyle(buttonStyle);
            }
        }
        
        // Set table background to light blue
        orderTable.setStyle("-fx-control-inner-background: aliceblue; -fx-background-color: lightblue;");
        
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private GridPane createForm() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        
        // Style form with light blue background
        grid.setStyle("-fx-background-color: aliceblue; -fx-padding: 10; -fx-border-color: lightblue; -fx-border-width: 2;");
        
        // Add form elements
        grid.add(new Label("Order ID:"), 0, 0);
        grid.add(orderIdField, 1, 0);
        orderIdField.setDisable(true);
        
        grid.add(new Label("Table ID:"), 0, 1);
        grid.add(tableIdField, 1, 1);
        
        grid.add(new Label("Staff ID:"), 0, 2);
        grid.add(staffIdField, 1, 2);
        
        grid.add(new Label("Menu ID:"), 0, 3);
        grid.add(menuIdField, 1, 3);
        
        grid.add(new Label("Quantity:"), 0, 4);
        grid.add(quantityField, 1, 4);
        
        grid.add(new Label("Status:"), 0, 5);
        statusComboBox.getItems().addAll("Pending", "Complete");
        statusComboBox.setValue("Pending");
        grid.add(statusComboBox, 1, 5);
        
        return grid;
    }
    
    private HBox createButtons() {
        Button addButton = new Button("Add");
        Button updateButton = new Button("Update");
        Button deleteButton = new Button("Delete");
        Button clearButton = new Button("Clear");
        
        // Button actions
        addButton.setOnAction(e -> addOrder());
        updateButton.setOnAction(e -> updateOrder());
        deleteButton.setOnAction(e -> deleteOrder());
        clearButton.setOnAction(e -> clearForm());
        
        // Select row from table
        orderTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                fillFormWithOrder(newSelection);
            }
        });
        
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(addButton, updateButton, deleteButton, clearButton);
        buttonBox.setPadding(new Insets(10, 0, 10, 0));
        return buttonBox;
    }
    
    private void createOrderTable() {
        TableColumn<Order, Integer> orderIdCol = new TableColumn<>("Order ID");
        orderIdCol.setCellValueFactory(cellData -> cellData.getValue().orderIdProperty().asObject());
        
        TableColumn<Order, Integer> tableIdCol = new TableColumn<>("Table ID");
        tableIdCol.setCellValueFactory(cellData -> cellData.getValue().tableIdProperty().asObject());
        
        TableColumn<Order, Integer> staffIdCol = new TableColumn<>("Staff ID");
        staffIdCol.setCellValueFactory(cellData -> cellData.getValue().staffIdProperty().asObject());
        
        TableColumn<Order, Integer> menuIdCol = new TableColumn<>("Menu ID");
        menuIdCol.setCellValueFactory(cellData -> cellData.getValue().menuIdProperty().asObject());
        
        TableColumn<Order, Integer> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        
        TableColumn<Order, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        
        orderTable.getColumns().addAll(orderIdCol, tableIdCol, staffIdCol, menuIdCol, quantityCol, statusCol);
        orderTable.setItems(orderData);
        
        // Style the table headers
        orderTable.setStyle("-fx-table-cell-border-color: transparent;");
    }
    
    private void loadOrders() {
        orderData.clear();
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM orders")) {
            
            while (rs.next()) {
                orderData.add(new Order(
                    rs.getInt("OrderID"),
                    rs.getInt("Table_Id"),
                    rs.getInt("StaffID"),
                    rs.getInt("menuId"),
                    rs.getInt("Quantity"),
                    rs.getString("Status")
                ));
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Error loading orders: " + e.getMessage());
        }
    }
    
    private void addOrder() {
        try {
            int tableId = Integer.parseInt(tableIdField.getText());
            int staffId = Integer.parseInt(staffIdField.getText());
            int menuId = Integer.parseInt(menuIdField.getText());
            int quantity = Integer.parseInt(quantityField.getText());
            String status = statusComboBox.getValue();
            
            String sql = "INSERT INTO orders (Table_Id, StaffID, menuId, Quantity, Status) VALUES (?, ?, ?, ?, ?)";
            
            try (Connection conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                
                pstmt.setInt(1, tableId);
                pstmt.setInt(2, staffId);
                pstmt.setInt(3, menuId);
                pstmt.setInt(4, quantity);
                pstmt.setString(5, status);
                
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int orderId = generatedKeys.getInt(1);
                            orderData.add(new Order(orderId, tableId, staffId, menuId, quantity, status));
                            clearForm();
                        }
                    }
                }
            }
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please enter valid numbers for all ID fields and quantity");
        } catch (SQLException e) {
            showAlert("Database Error", "Error adding order: " + e.getMessage());
        }
    }
    
    private void updateOrder() {
        Order selectedOrder = orderTable.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showAlert("Selection Error", "Please select an order to update");
            return;
        }
        
        try {
            int orderId = selectedOrder.getOrderId();
            int tableId = Integer.parseInt(tableIdField.getText());
            int staffId = Integer.parseInt(staffIdField.getText());
            int menuId = Integer.parseInt(menuIdField.getText());
            int quantity = Integer.parseInt(quantityField.getText());
            String status = statusComboBox.getValue();
            
            String sql = "UPDATE orders SET Table_Id = ?, StaffID = ?, menuId = ?, Quantity = ?, Status = ? WHERE OrderID = ?";
            
            try (Connection conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setInt(1, tableId);
                pstmt.setInt(2, staffId);
                pstmt.setInt(3, menuId);
                pstmt.setInt(4, quantity);
                pstmt.setString(5, status);
                pstmt.setInt(6, orderId);
                
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows > 0) {
                    selectedOrder.setTableId(tableId);
                    selectedOrder.setStaffId(staffId);
                    selectedOrder.setMenuId(menuId);
                    selectedOrder.setQuantity(quantity);
                    selectedOrder.setStatus(status);
                    orderTable.refresh();
                    clearForm();
                }
            }
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please enter valid numbers for all ID fields and quantity");
        } catch (SQLException e) {
            showAlert("Database Error", "Error updating order: " + e.getMessage());
        }
    }
    
    private void deleteOrder() {
        Order selectedOrder = orderTable.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showAlert("Selection Error", "Please select an order to delete");
            return;
        }
        
        int orderId = selectedOrder.getOrderId();
        String sql = "DELETE FROM orders WHERE OrderID = ?";
        
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, orderId);
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                orderData.remove(selectedOrder);
                clearForm();
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Error deleting order: " + e.getMessage());
        }
    }
    
    private void clearForm() {
        orderIdField.clear();
        tableIdField.clear();
        staffIdField.clear();
        menuIdField.clear();
        quantityField.clear();
        statusComboBox.setValue("Pending");
        orderTable.getSelectionModel().clearSelection();
    }
    
    private void fillFormWithOrder(Order order) {
        orderIdField.setText(String.valueOf(order.getOrderId()));
        tableIdField.setText(String.valueOf(order.getTableId()));
        staffIdField.setText(String.valueOf(order.getStaffId()));
        menuIdField.setText(String.valueOf(order.getMenuId()));
        quantityField.setText(String.valueOf(order.getQuantity()));
        statusComboBox.setValue(order.getStatus());
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Order model class
    public static class Order {
        private final IntegerProperty orderId;
        private final IntegerProperty tableId;
        private final IntegerProperty staffId;
        private final IntegerProperty menuId;
        private final IntegerProperty quantity;
        private final StringProperty status;
        
        public Order(int orderId, int tableId, int staffId, int menuId, int quantity, String status) {
            this.orderId = new SimpleIntegerProperty(orderId);
            this.tableId = new SimpleIntegerProperty(tableId);
            this.staffId = new SimpleIntegerProperty(staffId);
            this.menuId = new SimpleIntegerProperty(menuId);
            this.quantity = new SimpleIntegerProperty(quantity);
            this.status = new SimpleStringProperty(status);
        }
        
        // Getters
        public int getOrderId() { return orderId.get(); }
        public int getTableId() { return tableId.get(); }
        public int getStaffId() { return staffId.get(); }
        public int getMenuId() { return menuId.get(); }
        public int getQuantity() { return quantity.get(); }
        public String getStatus() { return status.get(); }
        
        // Setters
        public void setTableId(int value) { tableId.set(value); }
        public void setStaffId(int value) { staffId.set(value); }
        public void setMenuId(int value) { menuId.set(value); }
        public void setQuantity(int value) { quantity.set(value); }
        public void setStatus(String value) { status.set(value); }
        
        // Property getters
        public IntegerProperty orderIdProperty() { return orderId; }
        public IntegerProperty tableIdProperty() { return tableId; }
        public IntegerProperty staffIdProperty() { return staffId; }
        public IntegerProperty menuIdProperty() { return menuId; }
        public IntegerProperty quantityProperty() { return quantity; }
        public StringProperty statusProperty() { return status; }
    }
}