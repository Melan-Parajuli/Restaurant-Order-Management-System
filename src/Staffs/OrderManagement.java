package Staffs;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.beans.property.*;
import java.sql.*;

public class OrderManagement extends Application {
    // Order model properties
    public static class Order {
        private IntegerProperty orderId = new SimpleIntegerProperty();
        private IntegerProperty tableId = new SimpleIntegerProperty();
        private IntegerProperty staffId = new SimpleIntegerProperty();
        private IntegerProperty menuId = new SimpleIntegerProperty();
        private IntegerProperty quantity = new SimpleIntegerProperty();
        private StringProperty status = new SimpleStringProperty();

        public Order(int orderId, int tableId, int staffId, int menuId, int quantity, String status) {
            this.orderId.set(orderId);
            this.tableId.set(tableId);
            this.staffId.set(staffId);
            this.menuId.set(menuId);
            this.quantity.set(quantity);
            this.status.set(status);
        }

        public int getOrderId() { return orderId.get(); }
        public IntegerProperty orderIdProperty() { return orderId; }
        public int getTableId() { return tableId.get(); }
        public IntegerProperty tableIdProperty() { return tableId; }
        public int getStaffId() { return staffId.get(); }
        public IntegerProperty staffIdProperty() { return staffId; }
        public int getMenuId() { return menuId.get(); }
        public IntegerProperty menuIdProperty() { return menuId; }
        public int getQuantity() { return quantity.get(); }
        public IntegerProperty quantityProperty() { return quantity; }
        public String getStatus() { return status.get(); }
        public StringProperty statusProperty() { return status; }
    }

    private ObservableList<Order> orderData = FXCollections.observableArrayList();
    private TableView<Order> tableView = new TableView<>();

    @Override
    public void start(Stage primaryStage) {
        // Setup TableView columns
        TableColumn<Order, Integer> orderIdCol = new TableColumn<>("Order ID");
        orderIdCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));

        TableColumn<Order, Integer> tableIdCol = new TableColumn<>("Table ID");
        tableIdCol.setCellValueFactory(new PropertyValueFactory<>("tableId"));

        TableColumn<Order, Integer> staffIdCol = new TableColumn<>("Staff ID");
        staffIdCol.setCellValueFactory(new PropertyValueFactory<>("staffId"));

        TableColumn<Order, Integer> menuIdCol = new TableColumn<>("Menu ID");
        menuIdCol.setCellValueFactory(new PropertyValueFactory<>("menuId"));

        TableColumn<Order, Integer> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Order, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        tableView.getColumns().addAll(orderIdCol, tableIdCol, staffIdCol, menuIdCol, quantityCol, statusCol);
        tableView.setItems(orderData);

        // Input fields
        TextField tableIdField = new TextField();
        tableIdField.setPromptText("Table ID");
        TextField staffIdField = new TextField();
        staffIdField.setPromptText("Staff ID");
        TextField menuIdField = new TextField();
        menuIdField.setPromptText("Menu ID");
        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");
        
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Pending", "Complete");
        statusCombo.setValue("Pending");

        // Add button
        Button addButton = new Button("Add Order");
        addButton.setOnAction(e -> addOrder(
            tableIdField.getText(),
            staffIdField.getText(),
            menuIdField.getText(),
            quantityField.getText(),
            statusCombo.getValue()
        ));

        // Layout
        VBox vbox = new VBox(10, tableView, tableIdField, staffIdField, menuIdField, 
                           quantityField, statusCombo, addButton);
        Scene scene = new Scene(vbox, 600, 400);
        
        primaryStage.setTitle("Order Management");
        primaryStage.setScene(scene);
        primaryStage.show();

        loadOrders(); // Load initial data
    }

    private void loadOrders() {
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM orders");
            
            orderData.clear();
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
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addOrder(String tableId, String staffId, String menuId, String quantity, String status) {
        try {
            Connection conn = getConnection();
            String sql = "INSERT INTO orders (Table_Id, StaffID, menuId, Quantity, Status) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, Integer.parseInt(tableId));
            pstmt.setInt(2, Integer.parseInt(staffId));
            pstmt.setInt(3, Integer.parseInt(menuId));
            pstmt.setInt(4, Integer.parseInt(quantity));
            pstmt.setString(5, status);
            
            pstmt.executeUpdate();
            conn.close();
            
            // Clear fields and refresh table
            loadOrders();
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error adding order: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private Connection getConnection() throws SQLException {
        // Replace with your database connection details
        String url = "jdbc:mysql://localhost:3306/restaurant_app";
        String user = "root";
        String password = "hackerpcps@9812";
        return DriverManager.getConnection(url, user, password);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
