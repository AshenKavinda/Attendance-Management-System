package com.attendance.view;

import com.attendance.controller.ClassController;
import com.attendance.model.ClassRoom;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class ClassPanel extends JPanel {

    private final ClassController controller = new ClassController();

    // Pagination state
    private int    currentPage = 1;
    private int    totalPages  = 1;
    private String searchText  = "";
    private int    selectedId  = -1;
    private List<ClassRoom> currentList;

    // Table
    private JTable            table;
    private DefaultTableModel tableModel;

    // Pagination controls
    private JLabel  lblPageInfo;
    private JButton btnPrev;
    private JButton btnNext;

    // Form fields
    private JTextField tfClassName;
    private JTextField tfClassCode;
    private JTextField tfSection;
    private JTextField tfYear;
    private JTextField tfSearch;

    // Action buttons
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;

    public ClassPanel() {
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

        JLabel title = new JLabel("Class Management");
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

        panel.add(title, BorderLayout.WEST);
        panel.add(searchRow, BorderLayout.EAST);
        return panel;
    }

    private JPanel buildTableArea() {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);

        String[] cols = {"#", "ID", "Class Name", "Code", "Section", "Year"};
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

        // Hide ID column
        table.getColumnModel().getColumn(1).setMinWidth(0);
        table.getColumnModel().getColumn(1).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFormFromSelection();
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(0, 200));
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
        wrapper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Class Details"),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        JPanel grid = new JPanel(new GridLayout(2, 4, 12, 8));
        grid.setOpaque(false);

        tfClassName = new JTextField();
        tfClassCode = new JTextField();
        tfSection   = new JTextField();
        tfYear      = new JTextField(String.valueOf(java.time.Year.now().getValue()));

        grid.add(label("Class Name *")); grid.add(tfClassName);
        grid.add(label("Class Code *")); grid.add(tfClassCode);
        grid.add(label("Section"));      grid.add(tfSection);
        grid.add(label("Year"));         grid.add(tfYear);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);
        btnAdd    = styledButton("Add Class", new Color(26, 58, 89),    120);
        btnUpdate = styledButton("Update",    new Color(211, 84, 0),    100);
        btnDelete = styledButton("Delete",    new Color(192, 57, 43),   100);
        btnClear  = styledButton("Clear",     new Color(108, 117, 125), 100);

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
    // Data operations
    // ============================================================

    private void doSearch() {
        searchText  = tfSearch.getText().trim();
        currentPage = 1;
        loadTable();
    }

    public void loadTable() {
        try {
            int total    = controller.getTotalClassesFiltered(searchText);
            int pageSize = controller.getPageSize();
            totalPages   = (int) Math.ceil((double) total / pageSize);
            if (totalPages < 1) totalPages = 1;
            if (currentPage > totalPages) currentPage = totalPages;

            lblPageInfo.setText("Page " + currentPage + " of " + totalPages + "  (" + total + " records)");
            btnPrev.setEnabled(currentPage > 1);
            btnNext.setEnabled(currentPage < totalPages);

            currentList = controller.getClasses(currentPage, searchText);
            tableModel.setRowCount(0);
            int rowNum = (currentPage - 1) * pageSize + 1;
            for (ClassRoom c : currentList) {
                tableModel.addRow(new Object[]{
                    rowNum++,
                    c.getId(),
                    c.getClassName(),
                    c.getClassCode(),
                    c.getSection() != null ? c.getSection() : "",
                    c.getYear()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error loading classes:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fillFormFromSelection() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return;

        selectedId = (int) tableModel.getValueAt(viewRow, 1);

        ClassRoom c = null;
        if (currentList != null) {
            for (ClassRoom cr : currentList) {
                if (cr.getId() == selectedId) { c = cr; break; }
            }
        }
        if (c == null) return;

        tfClassName.setText(c.getClassName());
        tfClassCode.setText(c.getClassCode());
        tfSection  .setText(c.getSection() != null ? c.getSection() : "");
        tfYear     .setText(String.valueOf(c.getYear()));

        btnAdd   .setEnabled(false);
        btnUpdate.setEnabled(true);
        btnDelete.setEnabled(true);
    }

    private void doAdd() {
        ClassRoom c = buildFromForm();
        if (c == null) return;
        try {
            controller.addClass(c);
            JOptionPane.showMessageDialog(this, "Class added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            currentPage = 1;
            loadTable();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doUpdate() {
        ClassRoom c = buildFromForm();
        if (c == null) return;
        c.setId(selectedId);
        try {
            controller.updateClass(c);
            JOptionPane.showMessageDialog(this, "Class updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadTable();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doDelete() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Delete this class? This will also remove all student assignments and attendance records.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;
        try {
            controller.deleteClass(selectedId);
            JOptionPane.showMessageDialog(this, "Class deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadTable();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ============================================================
    // Form helpers
    // ============================================================

    private ClassRoom buildFromForm() {
        String name = tfClassName.getText().trim();
        String code = tfClassCode.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Class Name is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            tfClassName.requestFocus();
            return null;
        }
        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Class Code is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            tfClassCode.requestFocus();
            return null;
        }

        int year = 0;
        String yearStr = tfYear.getText().trim();
        if (!yearStr.isEmpty()) {
            try {
                year = Integer.parseInt(yearStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Year must be a number.", "Validation", JOptionPane.WARNING_MESSAGE);
                return null;
            }
        }

        ClassRoom c = new ClassRoom();
        c.setClassName(name);
        c.setClassCode(code);
        c.setSection  (tfSection.getText().trim());
        c.setYear     (year);
        return c;
    }

    private void clearForm() {
        selectedId = -1;
        tfClassName.setText("");
        tfClassCode.setText("");
        tfSection  .setText("");
        tfYear     .setText(String.valueOf(java.time.Year.now().getValue()));
        table.clearSelection();
        btnAdd   .setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }
}
