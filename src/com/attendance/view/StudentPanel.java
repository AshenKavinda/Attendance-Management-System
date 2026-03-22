package com.attendance.view;

import com.attendance.controller.StudentController;
import com.attendance.model.Student;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.Date;
import java.util.List;

public class StudentPanel extends JPanel {

    private final StudentController controller = new StudentController();

    // Pagination state
    private int    currentPage  = 1;
    private int    totalPages   = 1;
    private String searchText   = "";
    private int    selectedId   = -1;   // ID of the student currently selected in the table
    private List<Student> currentList; // in-memory copy of this page

    // Table
    private JTable             table;
    private DefaultTableModel  tableModel;

    // Pagination controls
    private JLabel  lblPageInfo;
    private JButton btnPrev;
    private JButton btnNext;

    // Form fields
    private JTextField        tfFirstName;
    private JTextField        tfLastName;
    private JTextField        tfDob;
    private JComboBox<String> cbGender;
    private JTextField        tfEmail;
    private JTextField        tfPhone;
    private JTextField        tfAddress;
    private JTextField        tfSearch;

    // Action buttons
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;

    public StudentPanel() {
        initUI();
        loadTable();
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

        JLabel title = new JLabel("Student Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(26, 58, 89));

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchRow.setOpaque(false);
        tfSearch = new JTextField(20);
        JButton btnSearch = styledButton("Search", new Color(52, 152, 219), 100);
        btnSearch.addActionListener(e -> doSearch());
        tfSearch.addActionListener(e -> doSearch());
        searchRow.add(new JLabel("Search:"));
        searchRow.add(tfSearch);
        searchRow.add(btnSearch);

        panel.add(title,     BorderLayout.WEST);
        panel.add(searchRow, BorderLayout.EAST);
        return panel;
    }

    private JPanel buildTableArea() {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);

        // Table
        String[] cols = {"#", "ID", "First Name", "Last Name", "Gender", "Email", "Phone"};
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

        // Hide ID column (col 1)
        table.getColumnModel().getColumn(1).setMinWidth(0);
        table.getColumnModel().getColumn(1).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFormFromSelection();
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(0, 180));
        panel.add(scroll, BorderLayout.CENTER);

        // Pagination bar
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
        wrapper.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 210, 220)),
            "Student Details"
        ));
        wrapper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Student Details"),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        // 4-column grid: label | field | label | field
        JPanel grid = new JPanel(new GridLayout(4, 4, 12, 8));
        grid.setOpaque(false);

        tfFirstName = new JTextField();
        tfLastName  = new JTextField();
        tfDob       = new JTextField("YYYY-MM-DD");
        cbGender    = new JComboBox<>(new String[]{"", "Male", "Female", "Other"});
        tfEmail     = new JTextField();
        tfPhone     = new JTextField();
        tfAddress   = new JTextField();

        grid.add(label("First Name *")); grid.add(tfFirstName);
        grid.add(label("Last Name *"));  grid.add(tfLastName);
        grid.add(label("Date of Birth")); grid.add(tfDob);
        grid.add(label("Gender"));        grid.add(cbGender);
        grid.add(label("Email"));         grid.add(tfEmail);
        grid.add(label("Phone"));         grid.add(tfPhone);
        grid.add(label("Address"));       grid.add(tfAddress);
        grid.add(new JLabel());           grid.add(new JLabel());

        // Buttons row
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);
        btnAdd    = styledButton("Add Student", new Color(26, 58, 89),    120);
        btnUpdate = styledButton("Update",      new Color(211, 84, 0),    100);
        btnDelete = styledButton("Delete",      new Color(192, 57, 43),   100);
        btnClear  = styledButton("Clear",       new Color(108, 117, 125), 100);

        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);

        btnAdd   .addActionListener(e -> doAdd());
        btnUpdate.addActionListener(e -> doUpdate());
        btnDelete.addActionListener(e -> doDelete());
        btnClear .addActionListener(e -> clearForm());

        btnRow.add(btnClear); btnRow.add(btnDelete); btnRow.add(btnUpdate); btnRow.add(btnAdd);

        wrapper.add(grid,   BorderLayout.CENTER);
        wrapper.add(btnRow, BorderLayout.SOUTH);
        return wrapper;
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return l;
    }

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

    // ============================================================
    // Data Operations
    // ============================================================

    private void doSearch() {
        searchText  = tfSearch.getText().trim();
        currentPage = 1;
        loadTable();
    }

    public void loadTable() {
        try {
            int total    = controller.getTotalStudentsFiltered(searchText);
            int pageSize = controller.getPageSize();
            totalPages   = (int) Math.ceil((double) total / pageSize);
            if (totalPages < 1) totalPages = 1;
            if (currentPage > totalPages) currentPage = totalPages;

            lblPageInfo.setText("Page " + currentPage + " of " + totalPages + "  (" + total + " records)");
            btnPrev.setEnabled(currentPage > 1);
            btnNext.setEnabled(currentPage < totalPages);

            currentList = controller.getStudents(currentPage, searchText);
            tableModel.setRowCount(0);
            int rowNum = (currentPage - 1) * pageSize + 1;
            for (Student s : currentList) {
                tableModel.addRow(new Object[]{
                    rowNum++,
                    s.getId(),
                    s.getFirstName(),
                    s.getLastName(),
                    s.getGender() != null ? s.getGender() : "",
                    s.getEmail()  != null ? s.getEmail()  : "",
                    s.getPhone()  != null ? s.getPhone()  : ""
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error loading students:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fillFormFromSelection() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return;

        selectedId = (int) tableModel.getValueAt(viewRow, 1);

        // Look up full data from in-memory list
        Student s = null;
        if (currentList != null) {
            for (Student st : currentList) {
                if (st.getId() == selectedId) { s = st; break; }
            }
        }
        if (s == null) return;

        tfFirstName.setText(s.getFirstName());
        tfLastName .setText(s.getLastName());
        tfDob      .setText(s.getDateOfBirth() != null ? s.getDateOfBirth().toString() : "");
        cbGender   .setSelectedItem(s.getGender() != null ? s.getGender() : "");
        tfEmail    .setText(s.getEmail()   != null ? s.getEmail()   : "");
        tfPhone    .setText(s.getPhone()   != null ? s.getPhone()   : "");
        tfAddress  .setText(s.getAddress() != null ? s.getAddress() : "");

        btnAdd   .setEnabled(false);
        btnUpdate.setEnabled(true);
        btnDelete.setEnabled(true);
    }

    private void doAdd() {
        Student s = buildFromForm();
        if (s == null) return;
        try {
            controller.addStudent(s);
            JOptionPane.showMessageDialog(this, "Student added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            currentPage = 1;
            loadTable();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doUpdate() {
        Student s = buildFromForm();
        if (s == null) return;
        s.setId(selectedId);
        try {
            controller.updateStudent(s);
            JOptionPane.showMessageDialog(this, "Student updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadTable();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doDelete() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Delete this student? This will also remove all attendance records.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;
        try {
            controller.deleteStudent(selectedId);
            JOptionPane.showMessageDialog(this, "Student deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadTable();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ============================================================
    // Form helpers
    // ============================================================

    private Student buildFromForm() {
        String firstName = tfFirstName.getText().trim();
        String lastName  = tfLastName .getText().trim();

        if (firstName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "First Name is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            tfFirstName.requestFocus();
            return null;
        }
        if (lastName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Last Name is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            tfLastName.requestFocus();
            return null;
        }

        Date dob = null;
        String dobStr = tfDob.getText().trim();
        if (!dobStr.isEmpty() && !dobStr.equals("YYYY-MM-DD")) {
            try {
                dob = Date.valueOf(dobStr);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Date of Birth must be YYYY-MM-DD format.", "Validation", JOptionPane.WARNING_MESSAGE);
                return null;
            }
        }

        Student s = new Student();
        s.setFirstName (firstName);
        s.setLastName  (lastName);
        s.setDateOfBirth(dob);
        s.setGender    (cbGender.getSelectedItem().toString());
        s.setEmail     (tfEmail  .getText().trim());
        s.setPhone     (tfPhone  .getText().trim());
        s.setAddress   (tfAddress.getText().trim());
        return s;
    }

    private void clearForm() {
        selectedId = -1;
        tfFirstName.setText("");
        tfLastName .setText("");
        tfDob      .setText("YYYY-MM-DD");
        cbGender   .setSelectedIndex(0);
        tfEmail    .setText("");
        tfPhone    .setText("");
        tfAddress  .setText("");
        table.clearSelection();
        btnAdd   .setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }
}
