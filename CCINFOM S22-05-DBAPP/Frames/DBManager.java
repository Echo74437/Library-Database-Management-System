package Frames;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {


    private static DBManager instance;

    // Enter Credentials
    private final String URL = "jdbc:mysql://localhost:3306/WarehouseManagement";
    private final String USER = "root";
    private final String PASSWORD = "12345";

    private Connection conn;

    private DBManager() {
    }


    public static DBManager getInstance() {
        if (instance == null) {
            instance = new DBManager();
        }
        return instance;
    }


    public Connection getConnection() throws SQLException {

        if (conn == null || conn.isClosed()) {
            System.out.println("Establishing new database connection...");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return conn;
    }


    public void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
