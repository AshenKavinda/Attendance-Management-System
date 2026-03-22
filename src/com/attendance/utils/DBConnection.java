package com.attendance.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection - Manages a single MySQL connection (Singleton pattern).
 * Uses pure JDBC - no ORM.
 *
 * Usage:
 *   Connection conn = DBConnection.getConnection();
 *   boolean ok      = DBConnection.testConnection();
 *   DBConnection.closeConnection();
 */
public class DBConnection {

    private static Connection connection = null;

    // -------------------------------------------------------
    // Get (or create) the database connection
    // -------------------------------------------------------
    public static Connection getConnection() {

        // Return existing open connection
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    return connection;
                }
            } catch (SQLException e) {
                System.err.println("[DBConnection] WARNING: Existing connection check failed: " + e.getMessage());
                connection = null;
            }
        }

        // Read config from .env (EnvLoader must be called before this)
        String host     = EnvLoader.get("DB_HOST",     "localhost");
        String port     = EnvLoader.get("DB_PORT",     "3306");
        String dbName   = EnvLoader.get("DB_NAME");
        String user     = EnvLoader.get("DB_USER");
        String password = EnvLoader.get("DB_PASSWORD");

        // Validate required fields
        if (dbName.isEmpty() || user.isEmpty()) {
            System.err.println("[DBConnection] ERROR: DB_NAME or DB_USER is missing in .env file.");
            System.err.println("[DBConnection] Please check your .env file and fill in all required values.");
            return null;
        }

        // Build JDBC URL
        String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName
                   + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

        System.out.println("[DBConnection] Connecting to: " + host + ":" + port + "/" + dbName);

        try {
            // Load JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Create connection
            connection = DriverManager.getConnection(url, user, password);

            System.out.println("[DBConnection] Connected to MySQL successfully!");

        } catch (ClassNotFoundException e) {
            System.err.println("[DBConnection] ERROR: MySQL JDBC Driver class not found.");
            System.err.println("[DBConnection] Fix: Download mysql-connector-j-x.x.x.jar and place it in the 'lib' folder.");
            System.err.println("[DBConnection] Details: " + e.getMessage());
            connection = null;

        } catch (SQLException e) {
            System.err.println("[DBConnection] ERROR: Cannot connect to the MySQL database.");
            System.err.println("[DBConnection] Common causes:");
            System.err.println("   1. MySQL server is not running.");
            System.err.println("   2. Wrong DB_USER or DB_PASSWORD in .env.");
            System.err.println("   3. Database '" + dbName + "' does not exist - create it first.");
            System.err.println("   4. Wrong DB_HOST or DB_PORT in .env.");
            System.err.println("[DBConnection] SQL State : " + e.getSQLState());
            System.err.println("[DBConnection] Error Code: " + e.getErrorCode());
            System.err.println("[DBConnection] Details   : " + e.getMessage());
            connection = null;
        }

        return connection;
    }

    // -------------------------------------------------------
    // Test the connection - returns true if connected
    // -------------------------------------------------------
    public static boolean testConnection() {
        Connection conn = getConnection();

        if (conn == null) {
            System.err.println("[DBConnection] Connection test FAILED.");
            return false;
        }

        try {
            // Run a lightweight query to verify the connection is live
            conn.createStatement().execute("SELECT 1");
            System.out.println("[DBConnection] Connection test PASSED.");
            return true;

        } catch (SQLException e) {
            System.err.println("[DBConnection] Connection test FAILED.");
            System.err.println("[DBConnection] Details: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------
    // Close the database connection safely
    // -------------------------------------------------------
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("[DBConnection] Connection closed.");
            } catch (SQLException e) {
                System.err.println("[DBConnection] ERROR: Could not close the connection.");
                System.err.println("[DBConnection] Details: " + e.getMessage());
            }
        }
    }
}
