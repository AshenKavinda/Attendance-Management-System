package com.attendance.view;

import com.attendance.controller.ClassController;
import com.attendance.controller.ReportController;
import com.attendance.model.ClassRoom;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.swing.JRViewer;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * ReportPanel - UI for generating, previewing, and exporting reports.
 *
 * Report types:
 *   1. Sign-In Sheet
 *   2. Class Attendance Report
 *   3. Student Attendance Report
 *   4. Class Summary Report
 */
public class ReportPanel extends JPanel {

    private final ReportController reportCtrl = new ReportController();
    private final ClassController  classCtrl  = new ClassController();

    private static final String[] REPORT_TYPES = {
        "Sign-In Sheet",
        "Class Attendance Report",
        "Student Attendance Report",
        "Class Summary Report"
    };

    // Filter controls
    private JComboBox<String>      cbReportType;
    private JComboBox<ClassRoom>   cbClass;
    private JComboBox<StudentItem> cbStudent;
    private JComboBox<DateItem>    cbDate;   // available dates dropdown (Class Attendance)
    private DatePickerField        dpDate;   // free date picker (Sign-In Sheet)

    // Filter wrapper panels (for show/hide)
    private JPanel pnlClass;
    private JPanel pnlDate;
    private JPanel pnlDateCombo;
    private JPanel pnlStudent;

    // Report viewer area
    private JPanel     viewerContainer;
    private JasperPrint currentPrint;

    public ReportPanel() {
        initUI();
        loadClassDropdown();
    }

    // ============================================================
    // UI Construction
    // ============================================================

    private void initUI() {
        setLayout(new BorderLayout(0, 10));
        setBackground(new Color(245, 247, 251));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        add(buildToolbar(), BorderLayout.NORTH);

        viewerContainer = new JPanel(new BorderLayout());
        viewerContainer.setBackground(Color.WHITE);
        viewerContainer.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 235)));
        showPlaceholder();
        add(viewerContainer, BorderLayout.CENTER);
    }

    private JPanel buildToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout(0, 8));
        toolbar.setOpaque(false);

        // Row 1: Title + action buttons
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);

        JLabel title = new JLabel("Reports");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(26, 58, 89));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);
        JButton btnPreview = styledButton("Preview Report", new Color(52, 152, 219), 150);
        JButton btnExport  = styledButton("Export PDF",     new Color(39, 174,  96), 120);
        btnPreview.addActionListener(e -> previewReport());
        btnExport .addActionListener(e -> exportPDF());
        btnRow.add(btnPreview);
        btnRow.add(btnExport);

        titleRow.add(title,  BorderLayout.WEST);
        titleRow.add(btnRow, BorderLayout.EAST);

        // Row 2: Filter controls
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        filterRow.setOpaque(false);

        cbReportType = new JComboBox<>(REPORT_TYPES);
        cbReportType.setPreferredSize(new Dimension(220, 30));
        cbReportType.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbReportType.addActionListener(e -> onReportTypeChanged());

        cbClass = new JComboBox<>();
        cbClass.setPreferredSize(new Dimension(220, 30));
        cbClass.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbClass.addActionListener(e -> onClassChanged());

        dpDate = new DatePickerField(java.time.LocalDate.now());
        dpDate.setPreferredSize(new Dimension(150, 30));

        cbDate = new JComboBox<>();
        cbDate.setPreferredSize(new Dimension(170, 30));
        cbDate.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        cbStudent = new JComboBox<>();
        cbStudent.setPreferredSize(new Dimension(220, 30));
        cbStudent.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Wrap each filter in a labeled panel
        JPanel pnlType = wrapFilter("Report Type:", cbReportType);
        pnlClass     = wrapFilter("Class:",         cbClass);
        pnlDate      = wrapFilter("Date:",           dpDate);
        pnlDateCombo = wrapFilter("Date:",           cbDate);
        pnlStudent   = wrapFilter("Student:",        cbStudent);

        filterRow.add(pnlType);
        filterRow.add(pnlClass);
        filterRow.add(pnlDate);
        filterRow.add(pnlDateCombo);
        filterRow.add(pnlStudent);

        toolbar.add(titleRow,  BorderLayout.NORTH);
        toolbar.add(filterRow, BorderLayout.CENTER);

        // Set initial filter visibility
        onReportTypeChanged();

        return toolbar;
    }

    private JPanel wrapFilter(String label, JComponent comp) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(new Color(60, 80, 100));
        p.add(lbl);
        p.add(comp);
        return p;
    }

    // ============================================================
    // Filter logic
    // ============================================================

    private void onReportTypeChanged() {
        int idx = cbReportType.getSelectedIndex();
        // 0 Sign-In Sheet:         class + date picker (any date)
        // 1 Class Attendance:      class + date combo (existing dates)
        // 2 Student Attendance:    class + student
        // 3 Class Summary:         (none)
        pnlClass    .setVisible(idx <= 2);
        pnlDate     .setVisible(idx == 0);
        pnlDateCombo.setVisible(idx == 1);
        pnlStudent  .setVisible(idx == 2);
        revalidate();

        if (idx == 1) loadDatesForClass();
        if (idx == 2) onClassChanged();
    }

    private void onClassChanged() {
        int reportIdx = cbReportType.getSelectedIndex();
        ClassRoom cr = (ClassRoom) cbClass.getSelectedItem();

        if (reportIdx == 1) {
            loadDatesForClass();
        } else if (reportIdx == 2) {
            cbStudent.removeAllItems();
            if (cr == null) return;
            List<Object[]> students = reportCtrl.getStudentsInClass(cr.getId());
            for (Object[] row : students) {
                cbStudent.addItem(new StudentItem((int) row[0], (String) row[1]));
            }
        }
    }

    private void loadDatesForClass() {
        cbDate.removeAllItems();
        ClassRoom cr = (ClassRoom) cbClass.getSelectedItem();
        if (cr == null) return;
        List<java.sql.Date> dates = reportCtrl.getAvailableDates(cr.getId());
        if (dates.isEmpty()) {
            cbDate.addItem(new DateItem(null, "No attendance dates found"));
        } else {
            for (java.sql.Date d : dates) {
                cbDate.addItem(new DateItem(d,
                    new java.text.SimpleDateFormat("EEE, MMM dd yyyy").format(d)));
            }
        }
    }

    private void loadClassDropdown() {
        try {
            List<ClassRoom> classes = classCtrl.getAllClassesForDropdown();
            cbClass.removeAllItems();
            for (ClassRoom c : classes) cbClass.addItem(c);
        } catch (Exception ex) {
            System.err.println("[ReportPanel] Error loading classes: " + ex.getMessage());
        }
    }

    // ============================================================
    // Report generation
    // ============================================================

    private void previewReport() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            currentPrint = generateCurrentReport();
            if (currentPrint != null) {
                viewerContainer.removeAll();
                JRViewer viewer = new JRViewer(currentPrint);
                viewerContainer.add(viewer, BorderLayout.CENTER);
                viewerContainer.revalidate();
                viewerContainer.repaint();
            }
        } catch (Exception ex) {
            showError("Error generating report:\n" + ex.getMessage());
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void exportPDF() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            JasperPrint print = currentPrint != null ? currentPrint : generateCurrentReport();
            if (print == null) return;

            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Save PDF Report");
            fc.setSelectedFile(new File(print.getName() + ".pdf"));
            fc.setFileFilter(new FileNameExtensionFilter("PDF Files (*.pdf)", "pdf"));

            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String path = fc.getSelectedFile().getAbsolutePath();
                if (!path.toLowerCase().endsWith(".pdf")) path += ".pdf";
                reportCtrl.exportToPDF(print, path);
                JOptionPane.showMessageDialog(this,
                    "PDF saved successfully:\n" + path,
                    "Export Complete", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            showError("Error exporting PDF:\n" + ex.getMessage());
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private JasperPrint generateCurrentReport() throws Exception {
        int idx = cbReportType.getSelectedIndex();
        ClassRoom cr = (ClassRoom) cbClass.getSelectedItem();

        switch (idx) {
            case 0: // Sign-In Sheet
                validateClass(cr);
                return reportCtrl.generateSignInSheet(cr.getId(), getSelectedDate());

            case 1: // Class Attendance
                validateClass(cr);
                return reportCtrl.generateClassAttendance(cr.getId(), getSelectedDate());

            case 2: // Student Attendance
                validateClass(cr);
                StudentItem si = (StudentItem) cbStudent.getSelectedItem();
                if (si == null) throw new IllegalArgumentException("Please select a student.");
                return reportCtrl.generateStudentAttendance(si.id, cr.getId());

            case 3: // Class Summary
                return reportCtrl.generateClassSummary();

            default:
                throw new IllegalArgumentException("Unknown report type.");
        }
    }

    private void validateClass(ClassRoom cr) {
        if (cr == null) throw new IllegalArgumentException("Please select a class.");
    }

    private java.sql.Date getSelectedDate() {
        int idx = cbReportType.getSelectedIndex();
        if (idx == 1) {
            // Class Attendance - use date combo
            DateItem di = (DateItem) cbDate.getSelectedItem();
            if (di == null || di.date == null)
                throw new IllegalArgumentException("No attendance dates available for this class.");
            return di.date;
        }
        // Sign-In Sheet - use date picker
        java.time.LocalDate ld = dpDate.getDate();
        if (ld == null) throw new IllegalArgumentException("Please select a date.");
        return java.sql.Date.valueOf(ld);
    }

    // ============================================================
    // Placeholder & helpers
    // ============================================================

    private void showPlaceholder() {
        viewerContainer.removeAll();
        JPanel ph = new JPanel(new GridBagLayout());
        ph.setBackground(Color.WHITE);

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setOpaque(false);

        JLabel icon = new JLabel("\uD83D\uDCC4", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel msg1 = new JLabel("No Report Generated");
        msg1.setFont(new Font("Segoe UI", Font.BOLD, 16));
        msg1.setForeground(new Color(100, 110, 130));
        msg1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel msg2 = new JLabel("Select a report type, set filters, and click 'Preview Report'");
        msg2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        msg2.setForeground(new Color(150, 160, 175));
        msg2.setAlignmentX(Component.CENTER_ALIGNMENT);

        inner.add(icon);
        inner.add(Box.createVerticalStrut(12));
        inner.add(msg1);
        inner.add(Box.createVerticalStrut(6));
        inner.add(msg2);

        ph.add(inner);
        viewerContainer.add(ph, BorderLayout.CENTER);
        viewerContainer.revalidate();
        viewerContainer.repaint();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Report Error", JOptionPane.ERROR_MESSAGE);
    }

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
    // Date dropdown item wrapper
    // ============================================================

    private static class DateItem {
        final java.sql.Date date;
        final String        label;

        DateItem(java.sql.Date date, String label) {
            this.date  = date;
            this.label = label;
        }

        @Override
        public String toString() { return label; }
    }

    // ============================================================
    // Student dropdown item wrapper
    // ============================================================

    private static class StudentItem {
        final int id;
        final String name;

        StudentItem(int id, String name) {
            this.id   = id;
            this.name = name;
        }

        @Override
        public String toString() { return name; }
    }
}
