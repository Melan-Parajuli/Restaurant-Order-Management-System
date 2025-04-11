package Staffs;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.beans.property.*;
import java.sql.*;

public class UpdateOrders extends Application {
    // Order model properties (remains unchanged)
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
        // Setup TableView columns (remains unchanged)
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

        // Buttons
        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> searchOrders(tableIdField.getText()));

        Button updateButton = new Button("Update");
        updateButton.setOnAction(e -> updateOrder(
            tableIdField.getText(),
            staffIdField.getText(),
            menuIdField.getText(),
            quantityField.getText(),
            statusCombo.getValue()
        ));

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> deleteOrder());

        Button clearButton = new Button("Clear");
        clearButton.setOnAction(e -> {
            tableIdField.clear();
            staffIdField.clear();
            menuIdField.clear();
            quantityField.clear();
            statusCombo.setValue("Pending");
        });

        // HBox for buttons
        HBox buttonBox = new HBox(10); // 10 is spacing between buttons
        buttonBox.getChildren().addAll(searchButton, updateButton, deleteButton, clearButton);

        // Layout - updated with HBox for buttons
        VBox vbox = new VBox(10, tableView, tableIdField, staffIdField, menuIdField, 
                           quantityField, statusCombo, buttonBox);
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

    private void searchOrders(String tableId) {
        try {
            Connection conn = getConnection();
            String sql = "SELECT * FROM orders WHERE Table_Id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, Integer.parseInt(tableId));
            ResultSet rs = pstmt.executeQuery();
            
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
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error searching orders: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void updateOrder(String tableId, String staffId, String menuId, String quantity, String status) {
        Order selectedOrder = tableView.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select an order to update");
            alert.showAndWait();
            return;
        }
        
        try {
            Connection conn = getConnection();
            String sql = "UPDATE orders SET Table_Id = ?, StaffID = ?, menuId = ?, Quantity = ?, Status = ? WHERE OrderID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, Integer.parseInt(tableId));
            pstmt.setInt(2, Integer.parseInt(staffId));
            pstmt.setInt(3, Integer.parseInt(menuId));
            pstmt.setInt(4, Integer.parseInt(quantity));
            pstmt.setString(5, status);
            pstmt.setInt(6, selectedOrder.getOrderId());
            
            pstmt.executeUpdate();
            conn.close();
            
            loadOrders();
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error updating order: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void deleteOrder() {
        Order selectedOrder = tableView.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select an order to delete");
            alert.showAndWait();
            return;
        }
        
        try {
            Connection conn = getConnection();
            String sql = "DELETE FROM orders WHERE OrderID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, selectedOrder.getOrderId());
            
            pstmt.executeUpdate();
            conn.close();
            
            loadOrders();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error deleting order: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/restaurant_app";
        String user = "root";
        String password = "hackerpcps@9812";
        return DriverManager.getConnection(url, user, password);
    }

    public static void main(String[] args) {
        launch(args);
    }
}