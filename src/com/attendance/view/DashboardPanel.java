package com.attendance.view;

import com.attendance.controller.DashboardController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class DashboardPanel extends JPanel {

    private final DashboardController controller = new DashboardController();

    // Stat card value labels
    private JLabel lblStudents;
    private JLabel lblClasses;
    private JLabel lblTodayPct;
    private JLabel lblOverallPct;

    // Tables
    private JTable classTable;
    private JTable lowAttTable;

    // 7-day mini trend bar
    private JPanel trendPanel;

    public DashboardPanel() {
        initUI();
        loadData();
    }

    // ============================================================
    // UI Construction
    // ============================================================

    private void initUI() {
        setLayout(new BorderLayout(0, 12));
        setBackground(new Color(245, 247, 251));
        setBorder(BorderFactory.createEmptyBorder(18, 18, 12, 18));

        // Title bar
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);
        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(26, 58, 89));
        JButton btnRefresh = styledButton("Refresh", new Color(52, 152, 219), 100);
        btnRefresh.addActionListener(e -> loadData());
        titleBar.add(title,      BorderLayout.WEST);
        titleBar.add(btnRefresh, BorderLayout.EAST);
        add(titleBar, BorderLayout.NORTH);

        // Center: stat cards + tables
        JPanel center = new JPanel(new BorderLayout(0, 14));
        center.setOpaque(false);

        center.add(buildStatCards(), BorderLayout.NORTH);
        center.add(buildTablesRow(), BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
    }

    private JPanel buildStatCards() {
        JPanel row = new JPanel(new GridLayout(1, 4, 14, 0));
        row.setOpaque(false);

        lblStudents   = statValueLabel();
        lblClasses    = statValueLabel();
        lblTodayPct   = statValueLabel();
        lblOverallPct = statValueLabel();

        row.add(buildCard("Total Students",      lblStudents,   new Color(41,  128, 185)));
        row.add(buildCard("Total Classes",       lblClasses,    new Color(39,  174,  96)));
        row.add(buildCard("Today Attendance",    lblTodayPct,   new Color(230, 126,  34)));
        row.add(buildCard("Overall Attendance",  lblOverallPct, new Color(142,  68, 173)));

        return row;
    }

    private JPanel buildCard(String title, JLabel valueLabel, Color bg) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bg);
        card.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JLabel lbl = new JLabel(title, SwingConstants.LEFT);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(220, 235, 255));

        card.add(valueLabel, BorderLayout.CENTER);
        card.add(lbl,        BorderLayout.SOUTH);
        return card;
    }

    private JLabel statValueLabel() {
        JLabel l = new JLabel("—", SwingConstants.LEFT);
        l.setFont(new Font("Segoe UI", Font.BOLD, 36));
        l.setForeground(Color.WHITE);
        return l;
    }

    private JPanel buildTablesRow() {
        JPanel row = new JPanel(new GridLayout(1, 2, 14, 0));
        row.setOpaque(false);

        // Class-wise summary
        classTable = buildTable(new String[]{"Class Name", "Code", "Students", "Attendance %"});
        JScrollPane sp1 = new JScrollPane(classTable);
        sp1.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Class-Wise Attendance Summary"),
            BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));

        // Low attendance students
        lowAttTable = buildTable(new String[]{"Student", "Class", "Present / Total", "Percentage"});
        JScrollPane sp2 = new JScrollPane(lowAttTable);
        sp2.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Students with Lowest Attendance (Top 5)"),
            BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));

        row.add(sp1);
        row.add(sp2);
        return row;
    }

    private JTable buildTable(String[] cols) {
        DefaultTableModel m = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable t = new JTable(m);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setRowHeight(28);
        t.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                        tbl, value, isSelected, hasFocus, row, col);
                lbl.setBackground(new Color(26, 58, 89));
                lbl.setForeground(Color.WHITE);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
                lbl.setOpaque(true);
                lbl.setHorizontalAlignment(JLabel.CENTER);
                return lbl;
            }
        });
        t.setShowGrid(true);
        t.setGridColor(new Color(220, 225, 235));
        return t;
    }

    // ============================================================
    // Load data from controllers
    // ============================================================

    private JButton styledButton(String text, Color bg, int width) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                                    java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = isEnabled() ? bg : bg.darker();
                if (getModel().isPressed())       g2.setColor(base.darker());
                else if (getModel().isRollover()) g2.setColor(base.brighter());
                else                              g2.setColor(base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setFont(getFont());
                g2.setColor(Color.WHITE);
                java.awt.FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth()  - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(width, 34));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        return btn;
    }

    public void loadData() {
        try {
            // Stat cards
            lblStudents  .setText(String.valueOf(controller.getTotalStudents()));
            lblClasses   .setText(String.valueOf(controller.getTotalClasses()));

            int todayPresent = controller.getTodayPresentCount();
            int todayTotal   = controller.getTodayTotalMarked();
            if (todayTotal > 0) {
                double pct = 100.0 * todayPresent / todayTotal;
                lblTodayPct.setText(String.format("%.0f%%", pct));
            } else {
                lblTodayPct.setText("N/A");
            }

            double overall = controller.getOverallAttendancePercentage();
            lblOverallPct.setText(overall > 0 ? overall + "%" : "N/A");

            // Class-wise summary table
            DefaultTableModel cm = (DefaultTableModel) classTable.getModel();
            cm.setRowCount(0);
            List<String[]> classSummary = controller.getClassWiseSummary();
            if (classSummary.isEmpty()) {
                cm.addRow(new Object[]{"No data", "", "", ""});
            } else {
                for (String[] row : classSummary) cm.addRow(row);
            }

            // Low attendance students table
            DefaultTableModel lm = (DefaultTableModel) lowAttTable.getModel();
            lm.setRowCount(0);
            List<String[]> lowList = controller.getLowAttendanceStudents();
            if (lowList.isEmpty()) {
                lm.addRow(new Object[]{"No data", "", "", ""});
            } else {
                for (String[] row : lowList) {
                    lm.addRow(row);
                }
                // Highlight the lowest row (first) in orange
                lowAttTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable t, Object val,
                            boolean sel, boolean focus, int row, int col) {
                        Component c = super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                        if (!sel) {
                            c.setBackground(row == 0 ? new Color(255, 235, 220) : Color.WHITE);
                        }
                        return c;
                    }
                });
            }

        } catch (Exception ex) {
            System.err.println("[DashboardPanel] ERROR loading data: " + ex.getMessage());
            JOptionPane.showMessageDialog(this,
                "Error loading dashboard:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
