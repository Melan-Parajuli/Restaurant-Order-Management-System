package Staffs;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.sql.*;

public class ViewTables extends Application {
    private TableView<Table> tableView = new TableView<>();
    private ObservableList<Table> tableData = FXCollections.observableArrayList();
    
    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/restaurant_app";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "hackerpcps@9812";
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Restaurant Table Management");
        
        // Create UI components
        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(10));
        
        // Set light blue background
        mainLayout.setBackground(new Background(new BackgroundFill(
            Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        
        // Table View setup
        setupTableView();
        refreshTableData();
        
        // Add components to main layout
        mainLayout.getChildren().add(tableView);
        
        // Create and show scene
        Scene scene = new Scene(mainLayout, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void setupTableView() {
        // Create columns
        TableColumn<Table, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("tableID"));
        
        TableColumn<Table, Integer> capacityCol = new TableColumn<>("Capacity");
        capacityCol.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        
        TableColumn<Table, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        tableView.getColumns().addAll(idCol, capacityCol, statusCol);
        tableView.setItems(tableData);
    }
    
    private void refreshTableData() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM tables")) {
            
            tableData.clear();
            while (rs.next()) {
                tableData.add(new Table(
                    rs.getInt("TableID"),
                    rs.getInt("Capacity"),
                    rs.getString("Status")
                ));
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load table data: " + e.getMessage());
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Inner class for table data
    public static class Table {
        private int tableID;
        private int capacity;
        private String status;
        
        public Table(int tableID, int capacity, String status) {
            this.tableID = tableID;
            this.capacity = capacity;
            this.status = status;
        }
        
        public int getTableID() { return tableID; }
        public int getCapacity() { return capacity; }
        public String getStatus() { return status; }
    }
}