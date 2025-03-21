package file;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.sql.*;

public class UserCRUD extends Application {
    private Connection connectDB() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/menu", "root", "hackerpcps@9812");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        Label lblTitle, lblOrderName, lblTableId, lblSnacks, lblFoodPlatter, lblBeverages;
        TextField txtOrderName, txtTableId, txtSnacks, txtFoodPlatter, txtBeverages;
        Button btnAdd, btnDelete, btnSearch;

        Font font1 = new Font("Arial", 25);
        Font font2 = new Font("Arial", 18);

        lblTitle = new Label("Restaurant Menu");
        lblTitle.relocate(150, 5);
        lblTitle.setFont(font1);

        lblOrderName = new Label("Order Name:");
        lblOrderName.relocate(20, 80);
        lblOrderName.setFont(font2);
        txtOrderName = new TextField();
        txtOrderName.relocate(150, 80);

        lblTableId = new Label("Table Id:");
        lblTableId.relocate(20, 120);
        lblTableId.setFont(font2);
        txtTableId = new TextField();
        txtTableId.relocate(150, 120);

        lblSnacks = new Label("Snacks:");
        lblSnacks.relocate(20, 160);
        lblSnacks.setFont(font2);
        txtSnacks = new TextField();
        txtSnacks.relocate(150, 160);

        lblFoodPlatter = new Label("Food Platter:");
        lblFoodPlatter.relocate(20, 200);
        lblFoodPlatter.setFont(font2);
        txtFoodPlatter = new TextField();
        txtFoodPlatter.relocate(150, 200);

        lblBeverages = new Label("Beverages:");
        lblBeverages.relocate(20, 240);
        lblBeverages.setFont(font2);
        txtBeverages = new TextField();
        txtBeverages.relocate(150, 240);

        btnAdd = new Button("Add");
        btnAdd.relocate(60, 280);
        btnAdd.setOnAction(e -> addOrder(txtOrderName, txtTableId, txtSnacks, txtFoodPlatter, txtBeverages));

        btnDelete = new Button("Delete");
        btnDelete.relocate(140, 280);
        btnDelete.setOnAction(e -> deleteOrder(txtTableId));

        btnSearch = new Button("Search");
        btnSearch.relocate(250, 280);
        btnSearch.setOnAction(e -> searchOrder(txtTableId, txtOrderName, txtSnacks, txtFoodPlatter, txtBeverages));

        Pane pane = new Pane();
        pane.getChildren().addAll(lblTitle, lblOrderName, lblTableId, lblSnacks, lblFoodPlatter, lblBeverages,
                txtOrderName, txtTableId, txtSnacks, txtFoodPlatter, txtBeverages,
                btnAdd, btnDelete, btnSearch);
        
        Scene scene = new Scene(pane, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Restaurant Menu");
        primaryStage.show();
    }

    private void addOrder(TextField name, TextField table, TextField snacks, TextField food, TextField beverages) {
        String query = "INSERT INTO Restaurant_menu (order_name, table_id, snacks, foodplatter, beverages) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = connectDB(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name.getText());
            stmt.setInt(2, Integer.parseInt(table.getText()));
            stmt.setString(3, snacks.getText());
            stmt.setString(4, food.getText());
            stmt.setString(5, beverages.getText());
            stmt.executeUpdate();
            System.out.println("Order Added");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteOrder(TextField tableIdField) {
        String query = "DELETE FROM Restaurant_menu WHERE table_id = ?";
        try (Connection conn = connectDB(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, Integer.parseInt(tableIdField.getText()));
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Order Deleted");
            } else {
                System.out.println("Order Not Found");
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void searchOrder(TextField tableIdField, TextField name, TextField snacks, TextField food, TextField beverages) {
        String query = "SELECT * FROM Restaurant_menu WHERE table_id = ?";
        try (Connection conn = connectDB(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, Integer.parseInt(tableIdField.getText()));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                name.setText(rs.getString("order_name"));
                snacks.setText(rs.getString("snacks"));
                food.setText(rs.getString("foodplatter"));
                beverages.setText(rs.getString("beverages"));
                System.out.println("Order Found");
            } else {
                System.out.println("Order Not Found");
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
