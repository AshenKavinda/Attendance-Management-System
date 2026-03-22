package com.attendance.controller;

import com.attendance.model.Attendance;
import com.attendance.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceController {

    // -------------------------------------------------------
    // Load attendance sheet for a class on a given date.
    // Returns one Attendance object per active student in the class.
    // If a record already exists for that day it is pre-filled.
    // -------------------------------------------------------
    public List<Attendance> getAttendanceSheet(int classId, Date date) {
        List<Attendance> list = new ArrayList<>();

        String sql = "SELECT sc.id AS sc_id, sc.index_number, "
                   + "       s.first_name, s.last_name, "
                   + "       a.id AS att_id, a.status, a.remarks "
                   + "FROM student_class sc "
                   + "JOIN students s ON sc.student_id = s.id "
                   + "LEFT JOIN attendance a ON a.student_class_id = sc.id AND a.date = ? "
                   + "WHERE sc.class_id = ? AND sc.status = 'Active' "
                   + "ORDER BY sc.index_number";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, date);
            stmt.setInt (2, classId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Attendance a = new Attendance();
                a.setStudentClassId  (rs.getInt   ("sc_id"));
                a.setIndexNumber     (rs.getInt   ("index_number"));
                a.setStudentFirstName(rs.getString("first_name"));
                a.setStudentLastName (rs.getString("last_name"));
                a.setDate(date);

                int attId = rs.getInt("att_id");
                if (!rs.wasNull() && attId > 0) {
                    // Existing record
                    a.setId     (attId);
                    a.setStatus (rs.getString("status"));
                    a.setRemarks(rs.getString("remarks") != null ? rs.getString("remarks") : "");
                } else {
                    // No record yet — default to Present
                    a.setId     (0);
                    a.setStatus ("Present");
                    a.setRemarks("");
                }
                list.add(a);
            }
        } catch (SQLException e) {
            System.err.println("[AttendanceController] ERROR loading sheet: " + e.getMessage());
            throw new RuntimeException("Failed to load attendance sheet: " + e.getMessage());
        }
        return list;
    }

    // -------------------------------------------------------
    // Save (insert or update) all attendance records for a class/date.
    // Uses a transaction so all records are saved atomically.
    // -------------------------------------------------------
    public boolean saveAttendance(List<Attendance> list, Date date) {
        String insertSql = "INSERT INTO attendance (student_class_id, date, status, remarks) VALUES (?, ?, ?, ?)";
        String updateSql = "UPDATE attendance SET status=?, remarks=? WHERE id=?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);  // Begin transaction

            for (Attendance a : list) {
                if (a.getId() == 0) {
                    // New record
                    try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                        stmt.setInt   (1, a.getStudentClassId());
                        stmt.setDate  (2, date);
                        stmt.setString(3, a.getStatus());
                        stmt.setString(4, a.getRemarks());
                        stmt.executeUpdate();
                    }
                } else {
                    // Update existing
                    try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                        stmt.setString(1, a.getStatus());
                        stmt.setString(2, a.getRemarks());
                        stmt.setInt   (3, a.getId());
                        stmt.executeUpdate();
                    }
                }
            }

            conn.commit();
            System.out.println("[AttendanceController] Saved " + list.size() + " attendance records.");
            return true;

        } catch (SQLException e) {
            System.err.println("[AttendanceController] ERROR saving: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) {
                    System.err.println("[AttendanceController] Rollback failed: " + ex.getMessage());
                }
            }
            throw new RuntimeException("Failed to save attendance: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    System.err.println("[AttendanceController] Cleanup error: " + ex.getMessage());
                }
            }
        }
    }
}
