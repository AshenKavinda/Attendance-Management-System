package com.attendance.view;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private static final String KEY_DASHBOARD  = "DASHBOARD";
    private static final String KEY_STUDENTS   = "STUDENTS";
    private static final String KEY_CLASSES    = "CLASSES";
    private static final String KEY_ASSIGNMENT = "ASSIGNMENT";
    private static final String KEY_ATTENDANCE = "ATTENDANCE";
    private static final String KEY_REPORTS    = "REPORTS";

    private CardLayout cardLayout;
    private JPanel     contentPanel;
    private JButton[]  navButtons;

    // All panels
    private DashboardPanel    dashboardPanel;
    private StudentPanel      studentPanel;
    private ClassPanel        classPanel;
    private StudentClassPanel studentClassPanel;
    private AttendancePanel   attendancePanel;
    private ReportPanel       reportPanel;

    public MainFrame(boolean dbConnected) {
        if (!dbConnected) {
            showDbError();
        } else {
            initUI();
        }
    }

    // -------------------------------------------------------
    // Show a simple error screen if DB is not reachable
    // -------------------------------------------------------
    private void showDbError() {
        setTitle("Attendance System - Connection Error");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 190);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 15, 25));
        panel.setBackground(new Color(253, 245, 243));

        JLabel msg = new JLabel("<html><b style='color:#C0392B;font-size:14px;'>"
                + "Database Connection Failed</b><br><br>"
                + "The app could not connect to MySQL.<br>"
                + "Check the console output for details, fix your <b>.env</b> settings, "
                + "and restart the application.</html>");
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JButton btnExit = new JButton("Exit");
        btnExit.addActionListener(e -> System.exit(0));
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        btnPanel.add(btnExit);

        panel.add(msg,      BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        add(panel);
        setVisible(true);
    }

    // -------------------------------------------------------
    // Main application window
    // -------------------------------------------------------
    private void initUI() {
        setTitle("Attendance Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 720);
        setMinimumSize(new Dimension(950, 600));
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        add(buildSidebar(),      BorderLayout.WEST);
        add(buildContentPanel(), BorderLayout.CENTER);
        add(buildStatusBar(),    BorderLayout.SOUTH);

        showPanel(KEY_DASHBOARD);
    }

    // -------------------------------------------------------
    // Left sidebar navigation
    // -------------------------------------------------------
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(21, 47, 72));
        sidebar.setPreferredSize(new Dimension(205, 0));

        // Logo / title block
        JPanel logo = new JPanel(new BorderLayout());
        logo.setBackground(new Color(15, 35, 55));
        logo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));
        logo.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        JLabel logoText = new JLabel("<html><b>Attendance</b><br>"
                + "<span style='font-size:10px;color:#8AACCC;'>Management System</span></html>");
        logoText.setFont(new Font("Segoe UI", Font.BOLD, 15));
        logoText.setForeground(Color.WHITE);
        logo.add(logoText, BorderLayout.CENTER);
        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(8));

        // Nav items: [label, card key]
        String[][] items = {
            {"  Dashboard",    KEY_DASHBOARD },
            {"  Students",     KEY_STUDENTS  },
            {"  Classes",      KEY_CLASSES   },
            {"  Assignments",  KEY_ASSIGNMENT},
            {"  Attendance",   KEY_ATTENDANCE},
            {"  Reports",      KEY_REPORTS   }
        };

        navButtons = new JButton[items.length];
        for (int i = 0; i < items.length; i++) {
            final String key = items[i][1];
            JButton btn = new JButton(items[i][0]);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            btn.setForeground(new Color(170, 200, 230));
            btn.setBackground(new Color(21, 47, 72));
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setBorder(BorderFactory.createEmptyBorder(11, 22, 11, 22));
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> showPanel(key));
            navButtons[i] = btn;
            sidebar.add(btn);
        }

        sidebar.add(Box.createVerticalGlue());

        JLabel ver = new JLabel("  v1.0  |  Java Swing  |  MySQL");
        ver.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        ver.setForeground(new Color(80, 110, 140));
        ver.setBorder(BorderFactory.createEmptyBorder(8, 14, 10, 14));
        sidebar.add(ver);

        return sidebar;
    }

    // -------------------------------------------------------
    // CardLayout content area
    // -------------------------------------------------------
    private JPanel buildContentPanel() {
        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(245, 247, 251));

        dashboardPanel    = new DashboardPanel();
        studentPanel      = new StudentPanel();
        classPanel        = new ClassPanel();
        studentClassPanel = new StudentClassPanel();
        attendancePanel   = new AttendancePanel();

        contentPanel.add(dashboardPanel,    KEY_DASHBOARD);
        contentPanel.add(studentPanel,      KEY_STUDENTS);
        contentPanel.add(classPanel,        KEY_CLASSES);
        contentPanel.add(studentClassPanel, KEY_ASSIGNMENT);
        contentPanel.add(attendancePanel,   KEY_ATTENDANCE);

        reportPanel = new ReportPanel();
        contentPanel.add(reportPanel,       KEY_REPORTS);

        return contentPanel;
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 4));
        bar.setBackground(new Color(215, 225, 235));
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(180, 195, 210)));
        JLabel lbl = new JLabel("Java Swing  |  MySQL 8  |  MVC Architecture  |  Pure JDBC");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(new Color(80, 95, 115));
        bar.add(lbl);
        return bar;
    }

    // -------------------------------------------------------
    // Switch visible panel + highlight active nav button
    // -------------------------------------------------------
    public void showPanel(String key) {
        cardLayout.show(contentPanel, key);

        String[] keys = {KEY_DASHBOARD, KEY_STUDENTS, KEY_CLASSES, KEY_ASSIGNMENT, KEY_ATTENDANCE, KEY_REPORTS};
        for (int i = 0; i < keys.length; i++) {
            boolean active = keys[i].equals(key);
            navButtons[i].setBackground(active ? new Color(41, 128, 185) : new Color(21, 47, 72));
            navButtons[i].setForeground(active ? Color.WHITE : new Color(170, 200, 230));
        }

        // Refresh dashboard data every time it is shown
        if (KEY_DASHBOARD.equals(key)) {
            dashboardPanel.loadData();
        }
    }
}
