package com.attendance.controller;

import com.attendance.utils.DBConnection;
import net.sf.jasperreports.engine.*;

import java.io.File;
import java.sql.*;
import java.util.*;

/**
 * ReportController - Generates reports using JasperReports.
 *
 * Report types:
 *   1. Sign-In Sheet      - blank signing form for a class/date
 *   2. Class Attendance    - attendance records for a class on a date
 *   3. Student Attendance  - one student's attendance history in a class
 *   4. Class Summary       - overview of all classes with stats
 */
public class ReportController {

    private static final String REPORTS_DIR = "reports";

    // -------------------------------------------------------
    // 1. Sign-In Sheet
    // -------------------------------------------------------
    public JasperPrint generateSignInSheet(int classId, java.sql.Date date) throws JRException {
        String[] info = getClassInfo(classId);
        Map<String, Object> params = new HashMap<>();
        params.put("classId", classId);
        params.put("reportDate", date);
        params.put("className", info[0]);
        params.put("classCode", info[1]);
        return fillReport("sign_in_sheet.jrxml", params);
    }

    // -------------------------------------------------------
    // 2. Class Attendance Report
    // -------------------------------------------------------
    public JasperPrint generateClassAttendance(int classId, java.sql.Date date) throws JRException {
        String[] info = getClassInfo(classId);
        Map<String, Object> params = new HashMap<>();
        params.put("classId", classId);
        params.put("reportDate", date);
        params.put("className", info[0]);
        params.put("classCode", info[1]);
        return fillReport("class_attendance.jrxml", params);
    }

    // -------------------------------------------------------
    // 3. Student Attendance Report
    // -------------------------------------------------------
    public JasperPrint generateStudentAttendance(int studentId, int classId) throws JRException {
        String[] classInfo = getClassInfo(classId);
        String studentName = getStudentName(studentId);
        Map<String, Object> params = new HashMap<>();
        params.put("studentId", studentId);
        params.put("classId", classId);
        params.put("studentName", studentName);
        params.put("className", classInfo[0]);
        params.put("classCode", classInfo[1]);
        return fillReport("student_attendance.jrxml", params);
    }

    // -------------------------------------------------------
    // 4. Class Summary Report
    // -------------------------------------------------------
    public JasperPrint generateClassSummary() throws JRException {
        Map<String, Object> params = new HashMap<>();
        params.put("reportDate", new java.util.Date());
        return fillReport("class_summary.jrxml", params);
    }

    // -------------------------------------------------------
    // Export a generated report to PDF file
    // -------------------------------------------------------
    public void exportToPDF(JasperPrint print, String filePath) throws JRException {
        JasperExportManager.exportReportToPdfFile(print, filePath);
    }

    // -------------------------------------------------------
    // Get distinct attendance dates for a class
    // -------------------------------------------------------
    public List<java.sql.Date> getAvailableDates(int classId) {
        List<java.sql.Date> dates = new ArrayList<>();
        String sql = "SELECT DISTINCT a.date FROM attendance a "
                   + "JOIN student_class sc ON a.student_class_id = sc.id "
                   + "WHERE sc.class_id = ? ORDER BY a.date DESC";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, classId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                dates.add(rs.getDate("date"));
            }
        } catch (SQLException e) {
            System.err.println("[ReportController] ERROR loading dates: " + e.getMessage());
        }
        return dates;
    }

    // -------------------------------------------------------
    // Get students in a class (for report filter dropdown)
    // -------------------------------------------------------
    public List<Object[]> getStudentsInClass(int classId) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT s.id, CONCAT(s.first_name, ' ', s.last_name) AS full_name "
                   + "FROM student_class sc "
                   + "JOIN students s ON sc.student_id = s.id "
                   + "WHERE sc.class_id = ? AND sc.status = 'Active' "
                   + "ORDER BY sc.index_number";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, classId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{rs.getInt("id"), rs.getString("full_name")});
            }
        } catch (SQLException e) {
            System.err.println("[ReportController] ERROR loading students: " + e.getMessage());
        }
        return list;
    }

    // -------------------------------------------------------
    // Internal: compile JRXML and fill report with DB data
    // -------------------------------------------------------
    private JasperPrint fillReport(String jrxmlFile, Map<String, Object> params) throws JRException {
        String path = REPORTS_DIR + File.separator + jrxmlFile;
        File file = new File(path);
        if (!file.exists()) {
            throw new JRException("Report template not found: " + file.getAbsolutePath()
                + "\nEnsure the 'reports' folder exists in the project root directory.");
        }
        JasperReport report = JasperCompileManager.compileReport(path);
        Connection conn = DBConnection.getConnection();
        return JasperFillManager.fillReport(report, params, conn);
    }

    // -------------------------------------------------------
    // Helper: get class name + code
    // -------------------------------------------------------
    private String[] getClassInfo(int classId) {
        String sql = "SELECT class_name, class_code FROM classes WHERE id = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, classId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new String[]{rs.getString("class_name"), rs.getString("class_code")};
            }
        } catch (SQLException e) {
            System.err.println("[ReportController] ERROR: " + e.getMessage());
        }
        return new String[]{"Unknown", "N/A"};
    }

    // -------------------------------------------------------
    // Helper: get student full name
    // -------------------------------------------------------
    private String getStudentName(int studentId) {
        String sql = "SELECT CONCAT(first_name, ' ', last_name) AS full_name FROM students WHERE id = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getString("full_name");
        } catch (SQLException e) {
            System.err.println("[ReportController] ERROR: " + e.getMessage());
        }
        return "Unknown Student";
    }
}
