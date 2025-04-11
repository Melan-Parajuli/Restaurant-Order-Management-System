package file;

import java.sql.*;

public class RegisterConnection {
    // Database connection parameters - adjust these according to your setup
    private static final String URL = "jdbc:mysql://localhost:3306/restaurant_app";
    private static final String USER = "root";
    private static final String PASSWORD = "hackerpcps@9812";

    // Get database connection
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Create a new Admin
    public void createAdmin(String name, String username, String password, String phone) {
        String sql = "INSERT INTO admin_ (name, username, password, Phone) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setString(2, username);
            pstmt.setString(3, password);
            pstmt.setString(4, phone);
            
            pstmt.executeUpdate();
            System.out.println("Admin created successfully");
            
        } catch (SQLException e) {
            System.out.println("Error creating admin: " + e.getMessage());
        }
    }

    // Read admin by ID
    public void getAdminById(int adminId) {
        String sql = "SELECT * FROM admin_ WHERE AdminId = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, adminId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                System.out.println("Admin ID: " + rs.getInt("AdminId"));
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Username: " + rs.getString("username"));
                System.out.println("Phone: " + rs.getString("Phone"));
            } else {
                System.out.println("Admin not found");
            }
            
        } catch (SQLException e) {
            System.out.println("Error retrieving admin: " + e.getMessage());
        }
    }

    // Update admin
    public void updateAdmin(int adminId, String name, String username, String password, String phone) {
        String sql = "UPDATE admin_ SET name = ?, username = ?, password = ?, Phone = ? WHERE AdminId = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setString(2, username);
            pstmt.setString(3, password);
            pstmt.setString(4, phone);
            pstmt.setInt(5, adminId);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Admin updated successfully");
            } else {
                System.out.println("Admin not found");
            }
            
        } catch (SQLException e) {
            System.out.println("Error updating admin: " + e.getMessage());
        }
    }

    // Delete admin
    public void deleteAdmin(int adminId) {
        String sql = "DELETE FROM admin_ WHERE AdminId = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, adminId);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Admin deleted successfully");
            } else {
                System.out.println("Admin not found");
            }
            
        } catch (SQLException e) {
            System.out.println("Error deleting admin: " + e.getMessage());
        }
    }

}