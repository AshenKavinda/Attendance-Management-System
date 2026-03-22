package com.attendance.view;

import com.attendance.controller.AttendanceController;
import com.attendance.controller.ClassController;
import com.attendance.model.Attendance;
import com.attendance.model.ClassRoom;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.Date;
import java.util.List;

public class AttendancePanel extends JPanel {

    private final AttendanceController controller = new AttendanceController();
    private final ClassController      classCtrl  = new ClassController();

    // Attendance data for the currently loaded class+date
    private List<Attendance> attendanceList;

    // Top controls
    private JComboBox<ClassRoom> cbClass;
    private JTextField           tfDate;
    private JLabel               lblStatus;

    // Table
    private JTable            table;
    private DefaultTableModel tableModel;

    // Summary + save
    private JLabel  lblSummary;
    private JButton btnSave;
    private JButton btnMarkAllPresent;
    private JButton btnMarkAllAbsent;

    // Column indexes in the table
    private static final int COL_ROW     = 0;
    private static final int COL_INDEX   = 1;
    private static final int COL_NAME    = 2;
    private static final int COL_STATUS  = 3;
    private static final int COL_REMARKS = 4;

    public AttendancePanel() {
        initUI();
        loadClassDropdown();
    }

    // ============================================================
    // UI Construction
    // ============================================================

    private void initUI() {
        setLayout(new BorderLayout(0, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        add(buildTopBar(),    BorderLayout.NORTH);
        add(buildTableArea(), BorderLayout.CENTER);
        add(buildBottomBar(), BorderLayout.SOUTH);
    }

    private JPanel buildTopBar() {
        JPanel outer = new JPanel(new BorderLayout(0, 8));
        outer.setOpaque(false);

        JLabel title = new JLabel("Attendance Marking");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(26, 58, 89));

        // Selector row
        JPanel selRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        selRow.setOpaque(false);

        cbClass = new JComboBox<>();
        cbClass.setPreferredSize(new Dimension(280, 28));

        tfDate = new JTextField(new java.text.SimpleDateFormat("yyyy-MM-dd")
                     .format(new java.util.Date()), 12);

        JButton btnLoad = new JButton("Load Attendance");
        btnLoad.setBackground(new Color(52, 152, 219));
        btnLoad.setForeground(Color.WHITE);
        btnLoad.setOpaque(true);
        btnLoad.addActionListener(e -> loadAttendance());

        lblStatus = new JLabel("");
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblStatus.setForeground(new Color(100, 100, 100));

        selRow.add(new JLabel("Class:"));  selRow.add(cbClass);
        selRow.add(new JLabel("Date (YYYY-MM-DD):")); selRow.add(tfDate);
        selRow.add(btnLoad);
        selRow.add(lblStatus);

        outer.add(title,  BorderLayout.NORTH);
        outer.add(selRow, BorderLayout.CENTER);
        return outer;
    }

    private JPanel buildTableArea() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        String[] cols = {"#", "Index", "Student Name", "Status", "Remarks"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                // Only Status and Remarks columns are editable
                return (c == COL_STATUS || c == COL_REMARKS) && attendanceList != null;
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
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

        // Status column: JComboBox editor
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Present", "Absent"});
        table.getColumnModel().getColumn(COL_STATUS).setCellEditor(new DefaultCellEditor(statusCombo));

        // Column widths
        table.getColumnModel().getColumn(COL_ROW)    .setPreferredWidth(40);
        table.getColumnModel().getColumn(COL_INDEX)  .setPreferredWidth(70);
        table.getColumnModel().getColumn(COL_NAME)   .setPreferredWidth(200);
        table.getColumnModel().getColumn(COL_STATUS) .setPreferredWidth(100);
        table.getColumnModel().getColumn(COL_REMARKS).setPreferredWidth(250);

        JScrollPane scroll = new JScrollPane(table);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildBottomBar() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));

        lblSummary = new JLabel("No attendance loaded.");
        lblSummary.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSummary.setForeground(new Color(60, 80, 100));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);

        btnMarkAllPresent = new JButton("Mark All Present");
        btnMarkAllAbsent  = new JButton("Mark All Absent");
        btnSave           = new JButton("Save Attendance");

        btnMarkAllPresent.setEnabled(false);
        btnMarkAllAbsent .setEnabled(false);
        btnSave          .setEnabled(false);
        btnSave.setBackground(new Color(39, 174, 96));
        btnSave.setForeground(Color.WHITE);
        btnSave.setOpaque(true);

        btnMarkAllPresent.addActionListener(e -> markAll("Present"));
        btnMarkAllAbsent .addActionListener(e -> markAll("Absent"));
        btnSave          .addActionListener(e -> saveAttendance());

        btnRow.add(btnMarkAllPresent);
        btnRow.add(btnMarkAllAbsent);
        btnRow.add(btnSave);

        panel.add(lblSummary, BorderLayout.WEST);
        panel.add(btnRow,     BorderLayout.EAST);
        return panel;
    }

    // ============================================================
    // Data logic
    // ============================================================

    private void loadClassDropdown() {
        try {
            List<ClassRoom> classes = classCtrl.getAllClassesForDropdown();
            cbClass.removeAllItems();
            for (ClassRoom c : classes) cbClass.addItem(c);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error loading classes:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAttendance() {
        ClassRoom cr = (ClassRoom) cbClass.getSelectedItem();
        if (cr == null) {
            JOptionPane.showMessageDialog(this, "Please select a class.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Date date;
        try {
            date = Date.valueOf(tfDate.getText().trim());
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Date must be in YYYY-MM-DD format.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            attendanceList = controller.getAttendanceSheet(cr.getId(), date);
            tableModel.setRowCount(0);

            if (attendanceList.isEmpty()) {
                lblStatus .setText("No active students found in this class.");
                lblSummary.setText("No students.");
                btnSave          .setEnabled(false);
                btnMarkAllPresent.setEnabled(false);
                btnMarkAllAbsent .setEnabled(false);
                return;
            }

            int rowNum = 1;
            for (Attendance a : attendanceList) {
                tableModel.addRow(new Object[]{
                    rowNum++,
                    a.getIndexNumber(),
                    a.getStudentFullName(),
                    a.getStatus(),
                    a.getRemarks() != null ? a.getRemarks() : ""
                });
            }

            updateSummaryLabel();
            boolean isEdit = attendanceList.stream().anyMatch(a -> a.getId() > 0);
            lblStatus.setText(isEdit ? "Existing records loaded — editing mode." : "New attendance sheet.");
            btnSave          .setEnabled(true);
            btnMarkAllPresent.setEnabled(true);
            btnMarkAllAbsent .setEnabled(true);

        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void markAll(String status) {
        if (attendanceList == null) return;
        // Stop any active cell editor first
        if (table.isEditing()) table.getCellEditor().stopCellEditing();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt(status, i, COL_STATUS);
        }
        updateSummaryLabel();
    }

    private void saveAttendance() {
        if (attendanceList == null || attendanceList.isEmpty()) return;

        // Stop any active cell editor to commit current edit
        if (table.isEditing()) table.getCellEditor().stopCellEditing();

        ClassRoom cr = (ClassRoom) cbClass.getSelectedItem();
        Date date;
        try {
            date = Date.valueOf(tfDate.getText().trim());
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Sync table values back into the attendanceList
        for (int i = 0; i < attendanceList.size(); i++) {
            Attendance a = attendanceList.get(i);
            a.setStatus ((String) tableModel.getValueAt(i, COL_STATUS));
            a.setRemarks((String) tableModel.getValueAt(i, COL_REMARKS));
        }

        try {
            controller.saveAttendance(attendanceList, date);
            JOptionPane.showMessageDialog(this,
                "Attendance saved for " + cr.getClassName() + " on " + date,
                "Saved", JOptionPane.INFORMATION_MESSAGE);
            // Reload to get the new IDs assigned by DB
            loadAttendance();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSummaryLabel() {
        if (attendanceList == null) return;
        long present = 0, absent = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String s = (String) tableModel.getValueAt(i, COL_STATUS);
            if ("Present".equals(s)) present++; else absent++;
        }
        int total = (int)(present + absent);
        double pct = total > 0 ? (100.0 * present / total) : 0;
        lblSummary.setText(String.format(
            "Total: %d  |  Present: %d  |  Absent: %d  |  Attendance: %.1f%%",
            total, present, absent, pct));
    }
}
