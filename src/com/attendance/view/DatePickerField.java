package com.attendance.view;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * A reusable Swing date-picker field.
 * Displays the selected date as a read-only YYYY-MM-DD text field alongside
 * a calendar button that opens a popup calendar dialog.
 */
public class DatePickerField extends JPanel {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final JTextField textField;
    private LocalDate        selectedDate;

    public DatePickerField() {
        this(null);
    }

    public DatePickerField(LocalDate initialDate) {
        setLayout(new BorderLayout(2, 0));
        setOpaque(false);

        textField = new JTextField(10);
        textField.setEditable(false);
        textField.setBackground(Color.WHITE);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JButton btnPick = new JButton("\uD83D\uDCC5"); // 📅
        btnPick.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        btnPick.setPreferredSize(new Dimension(30, 26));
        btnPick.setToolTipText("Open calendar");
        btnPick.setFocusPainted(false);
        btnPick.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnPick.addActionListener(e -> showDialog());

        add(textField, BorderLayout.CENTER);
        add(btnPick,   BorderLayout.EAST);

        setDate(initialDate);
    }

    /** Returns the currently selected date, or {@code null} if none is set. */
    public LocalDate getDate() {
        return selectedDate;
    }

    /** Sets the displayed date. Pass {@code null} to clear the field. */
    public void setDate(LocalDate date) {
        selectedDate = date;
        textField.setText(date != null ? date.format(FMT) : "");
    }

    private void showDialog() {
        Window owner = SwingUtilities.getWindowAncestor(this);
        CalendarDialog dlg = new CalendarDialog(owner, selectedDate);
        dlg.setVisible(true);
        if (dlg.isConfirmed()) {
            setDate(dlg.getPickedDate());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Inner calendar dialog
    // ─────────────────────────────────────────────────────────────────────────

    private static final class CalendarDialog extends JDialog {

        private static final Color HDR_BG   = new Color(26,  58,  89);
        private static final Color SEL_BG   = new Color(26,  58,  89);
        private static final Color TODAY_BG = new Color(52, 152, 219);
        private static final Color HOVER_BG = new Color(210, 225, 240);
        private static final Color DOW_FG   = new Color(80, 100, 130);

        private static final String[] MONTHS = {
            "January","February","March","April","May","June",
            "July","August","September","October","November","December"
        };

        private YearMonth        viewMonth;
        private LocalDate        pickedDate;
        private boolean          confirmed   = false;
        private boolean          updating    = false; // prevent re-entrancy

        private JComboBox<String>  cbMonth;
        private JComboBox<Integer> cbYear;
        private JPanel             dayGrid;

        CalendarDialog(Window owner, LocalDate initial) {
            super(owner, "Select Date", ModalityType.APPLICATION_MODAL);
            pickedDate = (initial != null) ? initial : LocalDate.now();
            viewMonth  = YearMonth.from(pickedDate);
            buildUI();
            pack();
            setResizable(false);
            setLocationRelativeTo(owner);
        }

        private void buildUI() {
            setLayout(new BorderLayout());

            // ── Header ──────────────────────────────────────────────────────
            JPanel header = new JPanel(new BorderLayout(4, 0));
            header.setBackground(HDR_BG);
            header.setBorder(BorderFactory.createEmptyBorder(7, 8, 7, 8));

            // Prev / Next month buttons
            JButton btnPrev = navBtn("\u2039"); // ‹
            JButton btnNext = navBtn("\u203A"); // ›
            btnPrev.addActionListener(e -> navigate(-1));
            btnNext.addActionListener(e -> navigate(+1));

            // Month combo
            cbMonth = new JComboBox<>(MONTHS);
            cbMonth.setFont(new Font("Segoe UI", Font.BOLD, 13));
            cbMonth.setBackground(new Color(40, 75, 110));
            cbMonth.setForeground(Color.WHITE);
            cbMonth.setFocusable(false);
            cbMonth.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
            cbMonth.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value,
                        int index, boolean isSelected, boolean cellHasFocus) {
                    JLabel lbl = (JLabel) super.getListCellRendererComponent(
                            list, value, index, isSelected, cellHasFocus);
                    lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    return lbl;
                }
            });
            cbMonth.addActionListener(e -> {
                if (!updating) {
                    viewMonth = YearMonth.of(viewMonth.getYear(), cbMonth.getSelectedIndex() + 1);
                    refreshGrid();
                }
            });

            // Year dropdown (1900 – 2100)
            Integer[] years = new Integer[201];
            for (int i = 0; i < years.length; i++) years[i] = 1900 + i;
            cbYear = new JComboBox<>(years);
            cbYear.setFont(new Font("Segoe UI", Font.BOLD, 13));
            cbYear.setBackground(new Color(40, 75, 110));
            cbYear.setForeground(Color.WHITE);
            cbYear.setFocusable(false);
            cbYear.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
            cbYear.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value,
                        int index, boolean isSelected, boolean cellHasFocus) {
                    JLabel lbl = (JLabel) super.getListCellRendererComponent(
                            list, value, index, isSelected, cellHasFocus);
                    lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    return lbl;
                }
            });
            cbYear.addActionListener(e -> {
                if (!updating && cbYear.getSelectedItem() != null) {
                    viewMonth = YearMonth.of((Integer) cbYear.getSelectedItem(), viewMonth.getMonthValue());
                    refreshGrid();
                }
            });

            // Center control cluster: [month combo] [year spinner]
            JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
            center.setOpaque(false);
            center.add(cbMonth);
            center.add(cbYear);

            header.add(btnPrev, BorderLayout.WEST);
            header.add(center,  BorderLayout.CENTER);
            header.add(btnNext, BorderLayout.EAST);
            add(header, BorderLayout.NORTH);

            // ── Day grid ────────────────────────────────────────────────────
            dayGrid = new JPanel();
            dayGrid.setBackground(Color.WHITE);
            dayGrid.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
            add(dayGrid, BorderLayout.CENTER);

            // ── Footer ──────────────────────────────────────────────────────
            JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 6));
            footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 215, 230)));
            footer.setBackground(new Color(248, 250, 252));

            JButton btnToday  = ftrBtn("Today");
            JButton btnClear  = ftrBtn("Clear");
            JButton btnCancel = ftrBtn("Cancel");
            JButton btnOk     = ftrBtn("  OK  ");
            btnOk.setBackground(HDR_BG);
            btnOk.setForeground(Color.WHITE);
            btnOk.setOpaque(true);

            btnToday .addActionListener(e -> {
                pickedDate = LocalDate.now();
                viewMonth  = YearMonth.from(pickedDate);
                refreshGrid();
            });
            btnClear .addActionListener(e -> { pickedDate = null; confirmed = true;  dispose(); });
            btnCancel.addActionListener(e -> {                     confirmed = false; dispose(); });
            btnOk    .addActionListener(e -> {                     confirmed = true;  dispose(); });

            footer.add(btnToday);
            footer.add(btnClear);
            footer.add(btnCancel);
            footer.add(btnOk);
            add(footer, BorderLayout.SOUTH);

            refreshGrid();
        }

        /** Moves viewMonth by {@code delta} months and refreshes. */
        private void navigate(int delta) {
            viewMonth = viewMonth.plusMonths(delta);
            refreshGrid();
        }

        private JButton navBtn(String txt) {
            JButton b = new JButton(txt);
            b.setForeground(Color.WHITE);
            b.setContentAreaFilled(false);
            b.setBorderPainted(false);
            b.setFocusPainted(false);
            b.setFont(new Font("Segoe UI", Font.BOLD, 18));
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return b;
        }

        private JButton ftrBtn(String txt) {
            JButton b = new JButton(txt);
            b.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            b.setFocusPainted(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return b;
        }

        private void refreshGrid() {
            // Sync combo + spinner without triggering their listeners
            updating = true;
            cbMonth.setSelectedIndex(viewMonth.getMonthValue() - 1);
            cbYear .setSelectedItem(viewMonth.getYear());
            updating = false;

            dayGrid.removeAll();
            dayGrid.setLayout(new GridLayout(0, 7, 4, 4));

            // Day-of-week headers
            for (String h : new String[]{"Sun","Mon","Tue","Wed","Thu","Fri","Sat"}) {
                JLabel lbl = new JLabel(h, JLabel.CENTER);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lbl.setForeground(DOW_FG);
                dayGrid.add(lbl);
            }

            LocalDate today     = LocalDate.now();
            int       startDow  = viewMonth.atDay(1).getDayOfWeek().getValue() % 7; // Sunday = 0
            int       daysCount = viewMonth.lengthOfMonth();

            for (int i = 0; i < startDow; i++) dayGrid.add(new JLabel(""));
            for (int d = 1; d <= daysCount; d++) dayGrid.add(dayBtn(viewMonth.atDay(d), today));

            int trail = (7 - (startDow + daysCount) % 7) % 7;
            for (int i = 0; i < trail; i++) dayGrid.add(new JLabel(""));

            dayGrid.revalidate();
            dayGrid.repaint();
            pack();
        }

        private JButton dayBtn(LocalDate date, LocalDate today) {
            final boolean isSel = date.equals(pickedDate);
            final boolean isTod = date.equals(today);

            JButton btn = new JButton(String.valueOf(date.getDayOfMonth()));
            btn.setFont(new Font("Segoe UI", isSel ? Font.BOLD : Font.PLAIN, 12));
            btn.setPreferredSize(new Dimension(36, 28));
            btn.setMargin(new Insets(0, 0, 0, 0));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            if (isSel) {
                btn.setBackground(SEL_BG);
                btn.setForeground(Color.WHITE);
                btn.setOpaque(true);
            } else if (isTod) {
                btn.setBackground(TODAY_BG);
                btn.setForeground(Color.WHITE);
                btn.setOpaque(true);
            } else {
                btn.setOpaque(false);
                btn.setForeground(new Color(40, 50, 60));
            }

            btn.addActionListener(e -> { pickedDate = date; refreshGrid(); });

            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                    if (!isSel && !isTod) { btn.setOpaque(true); btn.setBackground(HOVER_BG); btn.repaint(); }
                }
                @Override public void mouseExited(java.awt.event.MouseEvent e) {
                    if (!isSel && !isTod) { btn.setOpaque(false); btn.repaint(); }
                }
            });
            return btn;
        }

        boolean   isConfirmed()   { return confirmed;  }
        LocalDate getPickedDate() { return pickedDate; }
    }
}
