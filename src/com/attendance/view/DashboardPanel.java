package com.attendance.view;

import com.attendance.controller.DashboardController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class DashboardPanel extends JPanel {

    private final DashboardController controller = new DashboardController();

    private javax.swing.Timer refreshTimer;

    // Stat card value labels
    private JLabel lblStudents;
    private JLabel lblClasses;
    private JLabel lblTodayPct;
    private JLabel lblOverallPct;
    private JLabel lblAssignments;

    // Tables
    private JTable classTable;
    private JTable lowAttTable;

    // Trend chart
    private TrendChartPanel trendChart;

    public DashboardPanel() {
        initUI();
        loadData();
        startRefreshTimer();
    }

    // ============================================================
    // UI Construction
    // ============================================================

    private void initUI() {
        setLayout(new BorderLayout(0, 12));
        setBackground(new Color(245, 247, 251));
        setBorder(BorderFactory.createEmptyBorder(18, 18, 12, 18));

        add(buildTitleBar(), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 14));
        center.setOpaque(false);

        // Top section: stat cards + trend chart
        JPanel topSection = new JPanel(new BorderLayout(0, 14));
        topSection.setOpaque(false);
        topSection.add(buildStatCards(), BorderLayout.NORTH);
        topSection.add(buildTrendSection(), BorderLayout.CENTER);
        topSection.setPreferredSize(new Dimension(0, 290));

        center.add(topSection, BorderLayout.NORTH);
        center.add(buildTablesRow(), BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
    }

    private JPanel buildTitleBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftPanel.setOpaque(false);

        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(26, 58, 89));

        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));
        JLabel dateLabel = new JLabel(dateStr);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateLabel.setForeground(new Color(120, 140, 160));

        leftPanel.add(title);
        leftPanel.add(dateLabel);

        JButton btnRefresh = styledButton("Refresh", new Color(52, 152, 219), 100);
        btnRefresh.addActionListener(e -> loadData());

        bar.add(leftPanel, BorderLayout.WEST);
        bar.add(btnRefresh, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildStatCards() {
        JPanel row = new JPanel(new GridLayout(1, 5, 12, 0));
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(0, 95));

        lblStudents    = statValueLabel();
        lblClasses     = statValueLabel();
        lblTodayPct    = statValueLabel();
        lblOverallPct  = statValueLabel();
        lblAssignments = statValueLabel();

        row.add(buildCard("Total Students",      lblStudents,    new Color(41,  128, 185)));
        row.add(buildCard("Total Classes",       lblClasses,     new Color(39,  174,  96)));
        row.add(buildCard("Active Assignments",  lblAssignments, new Color(52,  73,  94)));
        row.add(buildCard("Today Attendance",    lblTodayPct,    new Color(230, 126,  34)));
        row.add(buildCard("Overall Attendance",  lblOverallPct,  new Color(142,  68, 173)));

        return row;
    }

    private JPanel buildCard(String title, JLabel valueLabel, Color bg) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Shadow
                g2.setColor(new Color(0, 0, 0, 18));
                g2.fillRoundRect(3, 3, getWidth() - 3, getHeight() - 3, 16, 16);
                // Card body
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JLabel lbl = new JLabel(title, SwingConstants.LEFT);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(new Color(220, 235, 255));

        card.add(valueLabel, BorderLayout.CENTER);
        card.add(lbl,        BorderLayout.SOUTH);
        return card;
    }

    private JLabel statValueLabel() {
        JLabel l = new JLabel("\u2014", SwingConstants.LEFT);
        l.setFont(new Font("Segoe UI", Font.BOLD, 30));
        l.setForeground(Color.WHITE);
        return l;
    }

    private JPanel buildTrendSection() {
        trendChart = new TrendChartPanel();
        trendChart.setPreferredSize(new Dimension(0, 170));
        return trendChart;
    }

    private JPanel buildTablesRow() {
        JPanel row = new JPanel(new GridLayout(1, 2, 14, 0));
        row.setOpaque(false);

        classTable = buildTable(new String[]{"Class Name", "Code", "Students", "Attendance %"});
        JScrollPane sp1 = new JScrollPane(classTable);
        sp1.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 225)),
                "  Class-Wise Attendance Summary  ",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12),
                new Color(26, 58, 89)
            ),
            BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));

        lowAttTable = buildTable(new String[]{"Student", "Class", "Present / Total", "Percentage"});
        JScrollPane sp2 = new JScrollPane(lowAttTable);
        sp2.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 225)),
                "  Low Attendance Students (Top 5)  ",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12),
                new Color(26, 58, 89)
            ),
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
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
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
    // Helpers
    // ============================================================

    private JButton styledButton(String text, Color bg, int width) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = isEnabled() ? bg : bg.darker();
                if (getModel().isPressed())       g2.setColor(base.darker());
                else if (getModel().isRollover()) g2.setColor(base.brighter());
                else                              g2.setColor(base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setFont(getFont());
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
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
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ============================================================
    // Real-time refresh (30 seconds)
    // ============================================================

    private void startRefreshTimer() {
        refreshTimer = new javax.swing.Timer(30000, e -> loadData());
        refreshTimer.start();
    }

    public void loadData() {
        try {
            // Stat cards
            lblStudents   .setText(String.valueOf(controller.getTotalStudents()));
            lblClasses    .setText(String.valueOf(controller.getTotalClasses()));
            lblAssignments.setText(String.valueOf(controller.getTotalActiveAssignments()));

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

            // 7-day trend chart
            Map<String, Integer> trend = controller.getLast7DaysTrend();
            trendChart.setData(trend);

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
                for (String[] row : lowList) lm.addRow(row);
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
        }
    }

    // ============================================================
    // Custom 7-Day Trend Bar Chart
    // ============================================================

    private class TrendChartPanel extends JPanel {
        private Map<String, Integer> data = new LinkedHashMap<>();

        TrendChartPanel() { setOpaque(false); }

        void setData(Map<String, Integer> data) {
            this.data = data != null ? data : new LinkedHashMap<>();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();

            // Background card
            g2.setColor(new Color(0, 0, 0, 10));
            g2.fillRoundRect(2, 2, w - 2, h - 2, 14, 14);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, w - 3, h - 3, 14, 14);
            g2.setColor(new Color(220, 225, 235));
            g2.drawRoundRect(0, 0, w - 3, h - 3, 14, 14);

            // Title
            g2.setColor(new Color(26, 58, 89));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2.drawString("7-Day Attendance Trend (Present Count)", 16, 24);

            if (data.isEmpty()) {
                g2.setColor(new Color(160, 170, 180));
                g2.setFont(new Font("Segoe UI", Font.ITALIC, 13));
                g2.drawString("No attendance data in the last 7 days", w / 2 - 130, h / 2 + 5);
                g2.dispose();
                return;
            }

            int padL = 20, padR = 20, padT = 40, padB = 35;
            int chartW = w - padL - padR;
            int chartH = h - padT - padB;

            // Grid lines
            g2.setColor(new Color(240, 242, 245));
            g2.setStroke(new BasicStroke(1f));
            for (int i = 0; i <= 4; i++) {
                int y = padT + chartH * i / 4;
                g2.drawLine(padL, y, w - padR, y);
            }

            int barCount = data.size();
            int maxVal = data.values().stream().mapToInt(Integer::intValue).max().orElse(1);
            int slotW = chartW / Math.max(barCount, 1);
            int barWidth = Math.min(slotW - 12, 48);

            Color barColor1 = new Color(41, 128, 185);
            Color barColor2 = new Color(52, 152, 219);

            int i = 0;
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                int barH = maxVal > 0 ? (int) ((double) entry.getValue() / maxVal * chartH) : 0;
                if (barH < 3 && entry.getValue() > 0) barH = 3;
                int x = padL + i * slotW + (slotW - barWidth) / 2;
                int y = padT + chartH - barH;

                // Gradient bar
                GradientPaint gp = new GradientPaint(x, y, barColor2, x, y + barH, barColor1);
                g2.setPaint(gp);
                g2.fillRoundRect(x, y, barWidth, barH, 5, 5);

                // Value label above bar
                g2.setColor(new Color(50, 60, 75));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                String val = String.valueOf(entry.getValue());
                int tw = g2.getFontMetrics().stringWidth(val);
                if (barH > 8) g2.drawString(val, x + (barWidth - tw) / 2, y - 4);

                // Day label below
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                String day = entry.getKey();
                tw = g2.getFontMetrics().stringWidth(day);
                g2.drawString(day, x + (barWidth - tw) / 2, padT + chartH + 16);

                i++;
            }

            g2.dispose();
        }
    }
}
