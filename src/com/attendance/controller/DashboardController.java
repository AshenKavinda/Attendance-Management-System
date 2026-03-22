package com.attendance.controller;

import com.attendance.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DashboardController {

    // -------------------------------------------------------
    // Summary counts
    // -------------------------------------------------------
    public int getTotalStudents() {
        return querySingleInt("SELECT COUNT(*) FROM students");
    }

    public int getTotalClasses() {
        return querySingleInt("SELECT COUNT(*) FROM classes");
    }

    public int getTotalActiveAssignments() {
        return querySingleInt("SELECT COUNT(*) FROM student_class WHERE status = 'Active'");
    }

    // -------------------------------------------------------
    // Today's attendance
    // -------------------------------------------------------
    public int getTodayPresentCount() {
        return querySingleInt("SELECT COUNT(*) FROM attendance WHERE date = CURDATE() AND status = 'Present'");
    }

    public int getTodayTotalMarked() {
        return querySingleInt("SELECT COUNT(*) FROM attendance WHERE date = CURDATE()");
    }

    // -------------------------------------------------------
    // Overall attendance percentage (all time)
    // -------------------------------------------------------
    public double getOverallAttendancePercentage() {
        String sql = "SELECT ROUND("
                   +   "100.0 * SUM(CASE WHEN status = 'Present' THEN 1 ELSE 0 END) / NULLIF(COUNT(*), 0)"
                   + ", 1) FROM attendance";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) return rs.getDouble(1);

        } catch (SQLException e) {
            System.err.println("[DashboardController] ERROR overall %: " + e.getMessage());
        }
        return 0.0;
    }

    // -------------------------------------------------------
    // Class-wise attendance summary
    // Returns: [className, classCode, totalStudents, attendancePct]
    // -------------------------------------------------------
    public List<String[]> getClassWiseSummary() {
        List<String[]> list = new ArrayList<>();

        String sql = "SELECT c.class_name, c.class_code, "
                   + "  COUNT(DISTINCT sc.student_id) AS total_students, "
                   + "  ROUND(100.0 * SUM(CASE WHEN a.status = 'Present' THEN 1 ELSE 0 END) "
                   + "        / NULLIF(COUNT(a.id), 0), 1) AS att_pct "
                   + "FROM classes c "
                   + "LEFT JOIN student_class sc ON sc.class_id = c.id AND sc.status = 'Active' "
                   + "LEFT JOIN attendance a ON a.student_class_id = sc.id "
                   + "GROUP BY c.id, c.class_name, c.class_code "
                   + "ORDER BY c.class_name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String pct = rs.getString("att_pct");
                list.add(new String[]{
                    rs.getString("class_name"),
                    rs.getString("class_code"),
                    String.valueOf(rs.getInt("total_students")),
                    pct != null ? pct + "%" : "No data"
                });
            }
        } catch (SQLException e) {
            System.err.println("[DashboardController] ERROR class summary: " + e.getMessage());
        }
        return list;
    }

    // -------------------------------------------------------
    // Top 5 students with lowest attendance percentage
    // Returns: [studentName, className, present/total, pct]
    // -------------------------------------------------------
    public List<String[]> getLowAttendanceStudents() {
        List<String[]> list = new ArrayList<>();

        String sql = "SELECT s.first_name, s.last_name, c.class_name, "
                   + "  COUNT(a.id) AS total, "
                   + "  SUM(CASE WHEN a.status = 'Present' THEN 1 ELSE 0 END) AS present, "
                   + "  ROUND(100.0 * SUM(CASE WHEN a.status = 'Present' THEN 1 ELSE 0 END) "
                   + "        / NULLIF(COUNT(a.id), 0), 1) AS pct "
                   + "FROM student_class sc "
                   + "JOIN students s ON sc.student_id = s.id "
                   + "JOIN classes  c ON sc.class_id   = c.id "
                   + "LEFT JOIN attendance a ON a.student_class_id = sc.id "
                   + "GROUP BY sc.id, s.first_name, s.last_name, c.class_name "
                   + "HAVING COUNT(a.id) > 0 "
                   + "ORDER BY pct ASC LIMIT 5";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("first_name") + " " + rs.getString("last_name"),
                    rs.getString("class_name"),
                    rs.getInt("present") + " / " + rs.getInt("total"),
                    rs.getString("pct") + "%"
                });
            }
        } catch (SQLException e) {
            System.err.println("[DashboardController] ERROR low attendance: " + e.getMessage());
        }
        return list;
    }

    // -------------------------------------------------------
    // Attendance count per day — last 7 days
    // Returns: date string -> present count
    // -------------------------------------------------------
    public Map<String, Integer> getLast7DaysTrend() {
        Map<String, Integer> map = new LinkedHashMap<>();

        String sql = "SELECT DATE_FORMAT(date, '%b %d') AS day, COUNT(*) AS cnt "
                   + "FROM attendance "
                   + "WHERE status = 'Present' AND date >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) "
                   + "GROUP BY date ORDER BY date";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) map.put(rs.getString("day"), rs.getInt("cnt"));

        } catch (SQLException e) {
            System.err.println("[DashboardController] ERROR 7-day trend: " + e.getMessage());
        }
        return map;
    }

    // -------------------------------------------------------
    // Helper: run a COUNT-style query and return first int
    // -------------------------------------------------------
    private int querySingleInt(String sql) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            System.err.println("[DashboardController] ERROR: " + e.getMessage());
        }
        return 0;
    }
}
