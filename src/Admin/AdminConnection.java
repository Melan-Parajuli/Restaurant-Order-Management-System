package Admin;

import java.sql.Connection; // Connection -> interface ( is not a class)
import java.sql.DriverManager; 


public class AdminConnection {
	
	 final  static String SERVER = "localhost";
	 final  static String USER = "root";
	 final  static String PASS = "hackerpcps@9812";
	 final   static Integer PORT = 3306;
	 final  static String DB_NAME = "restaurant_app";
	 final  static String DRIVER= "com.mysql.cj.jdbc.Driver";
	 final  static String URL = "jdbc:mysql://"+SERVER+":"+PORT+"/"+DB_NAME;
			 
	public static void main(String []args){
		
		// Declare 
		Connection conn;
		try {
			// Input -> Process -> Output
			
			conn = DriverManager.getConnection(URL , USER , PASS); // connect with db server
			conn.close(); // close the connection
			System.out.println("Connect database server successfully !");
			conn.close();
			System.out.print("Close database server successfully !");
			
			
		}catch (Exception ex) {
			// Error message
			System.out.println("Error : "+ex.getMessage());
		}
		
	}
}
