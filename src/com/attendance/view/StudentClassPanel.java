package com.attendance.view;

import com.attendance.controller.ClassController;
import com.attendance.controller.StudentClassController;
import com.attendance.model.ClassRoom;
import com.attendance.model.Student;
import com.attendance.model.StudentClass;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.Date;
import java.util.List;

public class StudentClassPanel extends JPanel {

    private final StudentClassController controller     = new StudentClassController();
    private final ClassController        classCtrl      = new ClassController();

    // Pagination state
    private int    currentPage = 1;
    private int    totalPages  = 1;
    private int    selectedAssignmentId = -1;
    private List<StudentClass> currentList;

    // Class selector
    private JComboBox<ClassRoom> cbClass;

    // Table
    private JTable            table;
    private DefaultTableModel tableModel;

    // Pagination
    private JLabel  lblPageInfo;
    private JButton btnPrev;
    private JButton btnNext;

    // Assignment form
    private JComboBox<Student>  cbStudent;
    private JTextField          tfIndexNumber;
    private JComboBox<String>   cbStatus;
    private JTextField          tfEnrollDate;

    // Action buttons
    private JButton btnAssign;
    private JButton btnRemove;
    private JButton btnChangeStatus;
    private JButton btnClear;

    public StudentClassPanel() {
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
        add(buildFormPanel(), BorderLayout.SOUTH);
    }

    private JPanel buildTopBar() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setOpaque(false);

        JLabel title = new JLabel("Student-Class Assignment");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(26, 58, 89));

        JPanel selRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        selRow.setOpaque(false);
        cbClass = new JComboBox<>();
        cbClass.setPreferredSize(new Dimension(300, 28));
        cbClass.addActionListener(e -> onClassSelected());
        selRow.add(new JLabel("Select Class:"));
        selRow.add(cbClass);

        panel.add(title,  BorderLayout.WEST);
        panel.add(selRow, BorderLayout.EAST);
        return panel;
    }

    private JPanel buildTableArea() {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);

        String[] cols = {"#", "SC_ID", "Index No", "Student Name", "Status", "Enrolled"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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

        // Hide SC_ID column
        table.getColumnModel().getColumn(1).setMinWidth(0);
        table.getColumnModel().getColumn(1).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onTableRowSelected();
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(0, 180));
        panel.add(scroll, BorderLayout.CENTER);

        JPanel pagBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 4));
        pagBar.setOpaque(false);
        btnPrev     = styledButton("< Previous", new Color(108, 117, 125), 110);
        btnNext     = styledButton("Next >",     new Color(108, 117, 125), 90);
        lblPageInfo = new JLabel("Page 1 of 1");
        lblPageInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnPrev.addActionListener(e -> { if (currentPage > 1)         { currentPage--; loadTable(); } });
        btnNext.addActionListener(e -> { if (currentPage < totalPages) { currentPage++; loadTable(); } });
        pagBar.add(btnPrev); pagBar.add(lblPageInfo); pagBar.add(btnNext);
        panel.add(pagBar, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildFormPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 8));
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Assign Student to Selected Class"),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        JPanel grid = new JPanel(new GridLayout(2, 4, 12, 8));
        grid.setOpaque(false);

        cbStudent     = new JComboBox<>();
        cbStudent.setPreferredSize(new Dimension(200, 26));
        tfIndexNumber = new JTextField();
        cbStatus      = new JComboBox<>(new String[]{"Active", "Inactive", "Graduated"});
        tfEnrollDate  = new JTextField(new java.text.SimpleDateFormat("yyyy-MM-dd")
                            .format(new java.util.Date()));

        grid.add(label("Student *"));          grid.add(cbStudent);
        grid.add(label("Index Number"));       grid.add(tfIndexNumber);
        grid.add(label("Enrollment Date"));    grid.add(tfEnrollDate);
        grid.add(label("Status"));             grid.add(cbStatus);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);
        btnAssign       = styledButton("Assign",        new Color(26, 58, 89),   130);
        btnRemove       = styledButton("Remove",        new Color(192, 57, 43),  110);
        btnChangeStatus = styledButton("Change Status", new Color(211, 84, 0),   145);
        btnClear        = styledButton("Clear",         new Color(108, 117, 125),100);

        btnRemove      .setEnabled(false);
        btnChangeStatus.setEnabled(false);

        btnAssign      .addActionListener(e -> doAssign());
        btnRemove      .addActionListener(e -> doRemove());
        btnChangeStatus.addActionListener(e -> doChangeStatus());
        btnClear       .addActionListener(e -> clearForm());

        btnRow.add(btnClear); btnRow.add(btnRemove); btnRow.add(btnChangeStatus); btnRow.add(btnAssign);
        wrapper.add(grid,   BorderLayout.CENTER);
        wrapper.add(btnRow, BorderLayout.SOUTH);
        return wrapper;
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return l;
    }

    /** Creates a colored JButton that renders correctly on Windows LAF (including disabled state). */
    private JButton styledButton(String text, Color bg, int width) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                                    java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                // Background
                Color base = isEnabled() ? bg : bg.darker();
                if (getModel().isPressed())       g2.setColor(base.darker());
                else if (getModel().isRollover()) g2.setColor(base.brighter());
                else                              g2.setColor(base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                // Text – always white, regardless of enabled/disabled state
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

    // ============================================================
    // Data logic
    // ============================================================

    private void loadClassDropdown() {
        try {
            List<ClassRoom> classes = classCtrl.getAllClassesForDropdown();
            cbClass.removeAllItems();
            for (ClassRoom c : classes) cbClass.addItem(c);
            if (!classes.isEmpty()) onClassSelected();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error loading classes:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onClassSelected() {
        currentPage = 1;
        clearForm();
        loadTable();
        loadUnassignedStudents();
    }

    private ClassRoom getSelectedClass() {
        return (ClassRoom) cbClass.getSelectedItem();
    }

    public void loadTable() {
        ClassRoom cr = getSelectedClass();
        if (cr == null) return;
        try {
            int total    = controller.getTotalByClass(cr.getId());
            int pageSize = controller.getPageSize();
            totalPages   = (int) Math.ceil((double) total / pageSize);
            if (totalPages < 1) totalPages = 1;
            if (currentPage > totalPages) currentPage = totalPages;

            lblPageInfo.setText("Page " + currentPage + " of " + totalPages + "  (" + total + " assigned)");
            btnPrev.setEnabled(currentPage > 1);
            btnNext.setEnabled(currentPage < totalPages);

            currentList = controller.getStudentsByClass(cr.getId(), currentPage);
            tableModel.setRowCount(0);
            int rowNum = (currentPage - 1) * pageSize + 1;
            for (StudentClass sc : currentList) {
                tableModel.addRow(new Object[]{
                    rowNum++,
                    sc.getId(),
                    sc.getIndexNumber(),
                    sc.getStudentFullName(),
                    sc.getStatus(),
                    sc.getEnrollmentDate() != null ? sc.getEnrollmentDate().toString() : ""
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error loading assignments:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadUnassignedStudents() {
        ClassRoom cr = getSelectedClass();
        if (cr == null) return;
        try {
            List<Student> students = controller.getUnassignedStudents(cr.getId());
            cbStudent.removeAllItems();
            for (Student s : students) cbStudent.addItem(s);

            // Auto-fill next index number
            int next = controller.getNextIndexNumber(cr.getId());
            tfIndexNumber.setText(String.valueOf(next));
        } catch (Exception ex) {
            System.err.println("Error loading unassigned students: " + ex.getMessage());
        }
    }

    private void onTableRowSelected() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return;

        selectedAssignmentId = (int) tableModel.getValueAt(viewRow, 1);
        String status = (String) tableModel.getValueAt(viewRow, 4);
        cbStatus.setSelectedItem(status);

        btnRemove      .setEnabled(true);
        btnChangeStatus.setEnabled(true);
        btnAssign      .setEnabled(false);
    }

    private void doAssign() {
        ClassRoom cr = getSelectedClass();
        if (cr == null) {
            JOptionPane.showMessageDialog(this, "Please select a class first.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Student s = (Student) cbStudent.getSelectedItem();
        if (s == null) {
            JOptionPane.showMessageDialog(this, "No students available to assign.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int indexNo;
        try {
            indexNo = Integer.parseInt(tfIndexNumber.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Index Number must be a valid number.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Date enrollDate;
        try {
            enrollDate = Date.valueOf(tfEnrollDate.getText().trim());
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Enrollment Date must be YYYY-MM-DD format.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StudentClass sc = new StudentClass();
        sc.setStudentId     (s.getId());
        sc.setClassId       (cr.getId());
        sc.setIndexNumber   (indexNo);
        sc.setEnrollmentDate(enrollDate);
        sc.setStatus        (cbStatus.getSelectedItem().toString());

        try {
            controller.assignStudent(sc);
            JOptionPane.showMessageDialog(this, "Student assigned successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadTable();
            loadUnassignedStudents();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doRemove() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Remove this student from the class? Attendance records will also be deleted.",
            "Confirm Remove", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;
        try {
            controller.removeAssignment(selectedAssignmentId);
            JOptionPane.showMessageDialog(this, "Assignment removed.", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadTable();
            loadUnassignedStudents();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doChangeStatus() {
        String newStatus = cbStatus.getSelectedItem().toString();
        try {
            controller.updateStatus(selectedAssignmentId, newStatus);
            JOptionPane.showMessageDialog(this, "Status updated to: " + newStatus, "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadTable();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        selectedAssignmentId = -1;
        table.clearSelection();
        cbStatus.setSelectedIndex(0);
        btnAssign      .setEnabled(true);
        btnRemove      .setEnabled(false);
        btnChangeStatus.setEnabled(false);
    }
}
