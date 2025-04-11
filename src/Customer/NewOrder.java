package Customer;

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

public class NewOrder extends Application {
    public static class Order {
        private IntegerProperty orderId = new SimpleIntegerProperty();
        private IntegerProperty tableId = new SimpleIntegerProperty();
        private IntegerProperty menuId = new SimpleIntegerProperty();
        private IntegerProperty quantity = new SimpleIntegerProperty();
        private StringProperty status = new SimpleStringProperty();

        public Order(int orderId, int tableId, int menuId, int quantity, String status) {
            this.orderId.set(orderId);
            this.tableId.set(tableId);
            this.menuId.set(menuId);
            this.quantity.set(quantity);
            this.status.set(status);
        }

        public int getOrderId() { return orderId.get(); }
        public IntegerProperty orderIdProperty() { return orderId; }
        public int getTableId() { return tableId.get(); }
        public IntegerProperty tableIdProperty() { return tableId; }
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
        TableColumn<Order, Integer> orderIdCol = new TableColumn<>("Order ID");
        orderIdCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));

        TableColumn<Order, Integer> tableIdCol = new TableColumn<>("Table ID");
        tableIdCol.setCellValueFactory(new PropertyValueFactory<>("tableId"));

        TableColumn<Order, Integer> menuIdCol = new TableColumn<>("Menu ID");
        menuIdCol.setCellValueFactory(new PropertyValueFactory<>("menuId"));

        TableColumn<Order, Integer> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Order, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        tableView.getColumns().addAll(orderIdCol, tableIdCol, menuIdCol, quantityCol, statusCol);
        tableView.setItems(orderData);

        // Input fields
        TextField tableIdField = new TextField();
        tableIdField.setPromptText("Table ID");
        TextField menuIdField = new TextField();
        menuIdField.setPromptText("Menu ID");
        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");
        
        CheckBox statusCheck = new CheckBox("Pending");
        statusCheck.setSelected(true);

        // Buttons
        Button updateButton = new Button("Update");
        updateButton.setOnAction(e -> updateOrder(
            tableIdField.getText(),
            menuIdField.getText(),
            quantityField.getText(),
            statusCheck.isSelected() ? "Pending" : "Complete"
        ));

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> deleteOrder());

        Button clearButton = new Button("Clear");
        clearButton.setOnAction(e -> {
            tableIdField.clear();
            menuIdField.clear();
            quantityField.clear();
            statusCheck.setSelected(true);
        });

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(updateButton, deleteButton, clearButton);

        VBox vbox = new VBox(10, tableView, tableIdField, menuIdField, 
                           quantityField, statusCheck, buttonBox);
        Scene scene = new Scene(vbox, 600, 400);
        
        primaryStage.setTitle("Order Management");
        primaryStage.setScene(scene);
        primaryStage.show();

        loadOrders();
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

    private void updateOrder(String tableId, String menuId, String quantity, String status) {
        Order selectedOrder = tableView.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select an order to update");
            alert.showAndWait();
            return;
        }
        
        try {
            Connection conn = getConnection();
            String sql = "UPDATE orders SET Table_Id = ?, menuId = ?, Quantity = ?, Status = ? WHERE OrderID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, Integer.parseInt(tableId));
            pstmt.setInt(2, Integer.parseInt(menuId));
            pstmt.setInt(3, Integer.parseInt(quantity));
            pstmt.setString(4, status);
            pstmt.setInt(5, selectedOrder.getOrderId());
            
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