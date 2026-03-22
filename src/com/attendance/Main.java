package com.attendance;

import com.attendance.utils.DBConnection;
import com.attendance.utils.EnvLoader;
import com.attendance.view.MainFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main - Application entry point.
 *
 * Startup sequence:
 *   1. Load .env configuration
 *   2. Test database connection
 *   3. Launch the Swing GUI
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("================================================");
        System.out.println("   Attendance Management System - Starting...   ");
        System.out.println("================================================");

        // Step 1: Load environment variables from .env
        EnvLoader.load(".env");

        System.out.println("------------------------------------------------");

        // Step 2: Test database connection at startup
        System.out.println("[Main] Testing database connection...");
        boolean isConnected = DBConnection.testConnection();

        System.out.println("------------------------------------------------");

        if (isConnected) {
            System.out.println("[Main] Startup check PASSED  - Database is reachable.");
        } else {
            System.out.println("[Main] Startup check FAILED  - Database is NOT reachable.");
            System.out.println("[Main] The application will still open.");
            System.out.println("[Main] Fix the database issue and restart.");
        }

        System.out.println("================================================");

        // Step 3: Launch GUI on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {

            // Use the OS native look and feel (Windows/Mac/Linux)
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("[Main] WARNING: Could not apply system look and feel.");
                System.err.println("[Main] Details: " + e.getMessage());
            }

            MainFrame frame = new MainFrame(isConnected);
            frame.setVisible(true);

            System.out.println("[Main] Application window opened.");
        });

        // Shutdown hook - close DB connection when app exits
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("[Main] Application shutting down...");
            DBConnection.closeConnection();
            System.out.println("[Main] Goodbye!");
        }));
    }
}
