package com.attendance.view;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GridLayout;

/**
 * MainFrame - The main application window.
 * Shows the database connection status on startup.
 * Business logic panels will be added here later.
 */
public class MainFrame extends JFrame {

    // -------------------------------------------------------
    // Constructor
    // -------------------------------------------------------
    public MainFrame(boolean dbConnected) {
        initComponents(dbConnected);
    }

    // -------------------------------------------------------
    // Build UI components
    // -------------------------------------------------------
    private void initComponents(boolean dbConnected) {

        // --- Frame settings ---
        setTitle("Attendance Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 420);
        setLocationRelativeTo(null);   // Center on screen
        setResizable(false);

        // --- Root panel ---
        JPanel rootPanel = new JPanel(new BorderLayout(0, 0));
        rootPanel.setBackground(new Color(245, 247, 251));

        // --- Top header bar ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(33, 97, 140));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(18, 25, 18, 25));

        JLabel titleLabel = new JLabel("Attendance Management System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        JLabel subTitleLabel = new JLabel("Campus Attendance Tracking");
        subTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subTitleLabel.setForeground(new Color(200, 220, 240));

        JPanel headerText = new JPanel(new GridLayout(2, 1, 0, 4));
        headerText.setOpaque(false);
        headerText.add(titleLabel);
        headerText.add(subTitleLabel);
        headerPanel.add(headerText, BorderLayout.WEST);

        // --- Center content panel ---
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(new Color(245, 247, 251));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));

        // DB Status card
        JPanel statusCard = buildStatusCard(dbConnected);
        centerPanel.add(statusCard, BorderLayout.NORTH);

        // Placeholder info label
        JLabel infoLabel = new JLabel(
            "<html><center>Project structure created.<br>"
            + "Business logic modules will appear here.</center></html>",
            SwingConstants.CENTER
        );
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        infoLabel.setForeground(new Color(120, 130, 145));
        centerPanel.add(infoLabel, BorderLayout.CENTER);

        // --- Bottom status bar ---
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        statusBar.setBackground(new Color(210, 220, 230));
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(180, 190, 205)));

        JLabel footerLabel = new JLabel("Java Swing  |  MySQL  |  MVC Architecture");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerLabel.setForeground(new Color(80, 90, 110));
        statusBar.add(footerLabel);

        // --- Assemble ---
        rootPanel.add(headerPanel, BorderLayout.NORTH);
        rootPanel.add(centerPanel, BorderLayout.CENTER);
        rootPanel.add(statusBar,   BorderLayout.SOUTH);

        add(rootPanel);
    }

    // -------------------------------------------------------
    // Build the DB status indicator card
    // -------------------------------------------------------
    private JPanel buildStatusCard(boolean connected) {

        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(connected ? new Color(39, 174, 96) : new Color(192, 57, 43), 2),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        card.setBackground(connected ? new Color(232, 248, 240) : new Color(252, 235, 230));

        // Left icon text
        JLabel iconLabel = new JLabel(connected ? "✔" : "✘");
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        iconLabel.setForeground(connected ? new Color(39, 174, 96) : new Color(192, 57, 43));

        // Right text block
        JPanel textBlock = new JPanel(new GridLayout(2, 1, 0, 3));
        textBlock.setOpaque(false);

        JLabel statusTitle = new JLabel("Database Connection");
        statusTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusTitle.setForeground(new Color(40, 50, 60));

        JLabel statusDetail = new JLabel(
            connected
                ? "Connected to MySQL successfully. App is ready."
                : "Could not connect to MySQL. Check console output for details."
        );
        statusDetail.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusDetail.setForeground(connected ? new Color(30, 120, 60) : new Color(150, 40, 30));

        textBlock.add(statusTitle);
        textBlock.add(statusDetail);

        card.add(iconLabel, BorderLayout.WEST);
        card.add(textBlock, BorderLayout.CENTER);

        return card;
    }
}
