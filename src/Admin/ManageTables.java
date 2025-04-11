package Admin;

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

public class ManageTables extends Application {
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
        
        // Initialize database connection
        initializeDatabase();
        
        // Create main layout
        VBox mainLayout = createMainLayout();
        Scene scene = new Scene(mainLayout, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            // Create tables if they don't exist
            stmt.execute("CREATE TABLE IF NOT EXISTS tables (" +
                         "TableID INT AUTO_INCREMENT PRIMARY KEY, " +
                         "Capacity INT NOT NULL, " +
                         "Status VARCHAR(20) DEFAULT 'Free')");
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to initialize database: " + e.getMessage());
        }
    }
    
    private VBox createMainLayout() {
        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(10));
        mainLayout.setBackground(new Background(new BackgroundFill(
            Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        
        // Set up table view
        setupTableView();
        refreshTableData();
        
        // Add form and buttons
        mainLayout.getChildren().addAll(
            tableView,
            createAddTableForm(),
            createActionButtons()
        );
        
        return mainLayout;
    }
    
    private void setupTableView() {
        tableView.getColumns().clear();
        
        TableColumn<Table, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("tableID"));
        
        TableColumn<Table, Integer> capacityCol = new TableColumn<>("Capacity");
        capacityCol.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        
        TableColumn<Table, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        tableView.getColumns().addAll(idCol, capacityCol, statusCol);
        tableView.setItems(tableData);
        
        // Add selection listener
        tableView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    System.out.println("Selected Table: " + newSelection.getTableID());
                }
            });
    }
    
    private GridPane createAddTableForm() {
        GridPane formPane = new GridPane();
        formPane.setHgap(10);
        formPane.setVgap(10);
        
        Label capacityLabel = new Label("Capacity:");
        TextField capacityField = new TextField();
        Button addButton = new Button("Add Table");
        addButton.setStyle("-fx-background-color: lightyellow;");
        
        addButton.setOnAction(e -> handleAddTable(capacityField));
        
        formPane.add(capacityLabel, 0, 0);
        formPane.add(capacityField, 1, 0);
        formPane.add(addButton, 2, 0);
        
        return formPane;
    }
    
    private HBox createActionButtons() {
        HBox actionBox = new HBox(10);
        
        Button occupyButton = createActionButton("Occupy Table", this::handleOccupyTable);
        Button freeButton = createActionButton("Free Table", this::handleFreeTable);
        Button deleteButton = createActionButton("Delete Table", this::handleDeleteTable);
        
        actionBox.getChildren().addAll(occupyButton, freeButton, deleteButton);
        return actionBox;
    }
    
    private Button createActionButton(String text, Runnable handler) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: lightyellow;");
        button.setOnAction(e -> handler.run());
        return button;
    }
    
    private void handleAddTable(TextField capacityField) {
        try {
            int capacity = Integer.parseInt(capacityField.getText().trim());
            if (capacity <= 0) {
                showAlert("Input Error", "Capacity must be a positive number.");
                return;
            }
            
            addTable(capacity);
            refreshTableData();
            capacityField.clear();
            showInformation("Success", "Table added successfully!");
        } catch (NumberFormatException ex) {
            showAlert("Input Error", "Please enter a valid number for capacity.");
        } catch (SQLException ex) {
            showAlert("Database Error", "Failed to add table: " + ex.getMessage());
        }
    }
    
    private void handleOccupyTable() {
        Table selectedTable = tableView.getSelectionModel().getSelectedItem();
        if (selectedTable == null) {
            showAlert("Selection Error", "Please select a table first.");
            return;
        }
        
        try {
            updateTableStatus(selectedTable.getTableID(), "Occupied");
            refreshTableData();
            showInformation("Success", "Table " + selectedTable.getTableID() + " is now occupied.");
        } catch (SQLException ex) {
            showAlert("Database Error", "Failed to update table status: " + ex.getMessage());
        }
    }
    
    private void handleFreeTable() {
        Table selectedTable = tableView.getSelectionModel().getSelectedItem();
        if (selectedTable == null) {
            showAlert("Selection Error", "Please select a table first.");
            return;
        }
        
        try {
            updateTableStatus(selectedTable.getTableID(), "Available");
            refreshTableData();
            showInformation("Success", "Table " + selectedTable.getTableID() + " is now free.");
        } catch (SQLException ex) {
            showAlert("Database Error", "Failed to update table status: " + ex.getMessage());
        }
    }
    
    private void handleDeleteTable() {
        Table selectedTable = tableView.getSelectionModel().getSelectedItem();
        if (selectedTable == null) {
            showAlert("Selection Error", "Please select a table first.");
            return;
        }
        
        try {
            deleteTable(selectedTable.getTableID());
            refreshTableData();
            showInformation("Success", "Table " + selectedTable.getTableID() + " deleted successfully.");
        } catch (SQLException ex) {
            showAlert("Database Error", "Failed to delete table: " + ex.getMessage());
        }
    }
    
    private void refreshTableData() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM tables ORDER BY TableID")) {
            
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
    
    private void addTable(int capacity) throws SQLException {
        String sql = "INSERT INTO tables (Capacity) VALUES (?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, capacity);
            stmt.executeUpdate();
        }
    }
    
    private void updateTableStatus(int tableID, String status) throws SQLException {
        String sql = "UPDATE tables SET Status = ? WHERE TableID = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, tableID);
            stmt.executeUpdate();
        }
    }
    
    private void deleteTable(int tableID) throws SQLException {
        String sql = "DELETE FROM tables WHERE TableID = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, tableID);
            stmt.executeUpdate();
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInformation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static class Table {
        private final int tableID;
        private final int capacity;
        private final String status;
        
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