package com.attendance.controller;

import com.attendance.model.Student;
import com.attendance.model.StudentClass;
import com.attendance.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentClassController {

    private static final int PAGE_SIZE = 10;

    // -------------------------------------------------------
    // Count assigned students in a class
    // -------------------------------------------------------
    public int getTotalByClass(int classId) {
        String sql = "SELECT COUNT(*) FROM student_class WHERE class_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, classId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            System.err.println("[StudentClassController] ERROR counting: " + e.getMessage());
        }
        return 0;
    }

    // -------------------------------------------------------
    // Get one page of students assigned to a class (with JOIN)
    // -------------------------------------------------------
    public List<StudentClass> getStudentsByClass(int classId, int page) {
        List<StudentClass> list = new ArrayList<>();
        int offset = (page - 1) * PAGE_SIZE;

        String sql = "SELECT sc.id, sc.student_id, sc.class_id, sc.index_number, "
                   + "       sc.enrollment_date, sc.status, "
                   + "       s.first_name, s.last_name, "
                   + "       c.class_name, c.class_code "
                   + "FROM student_class sc "
                   + "JOIN students s ON sc.student_id = s.id "
                   + "JOIN classes  c ON sc.class_id   = c.id "
                   + "WHERE sc.class_id = ? "
                   + "ORDER BY sc.index_number LIMIT ? OFFSET ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, classId);
            stmt.setInt(2, PAGE_SIZE);
            stmt.setInt(3, offset);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                StudentClass sc = new StudentClass();
                sc.setId             (rs.getInt   ("id"));
                sc.setStudentId      (rs.getInt   ("student_id"));
                sc.setClassId        (rs.getInt   ("class_id"));
                sc.setIndexNumber    (rs.getInt   ("index_number"));
                sc.setEnrollmentDate (rs.getDate  ("enrollment_date"));
                sc.setStatus         (rs.getString("status"));
                sc.setStudentFirstName(rs.getString("first_name"));
                sc.setStudentLastName (rs.getString("last_name"));
                sc.setClassName      (rs.getString("class_name"));
                sc.setClassCode      (rs.getString("class_code"));
                list.add(sc);
            }
        } catch (SQLException e) {
            System.err.println("[StudentClassController] ERROR fetching: " + e.getMessage());
        }
        return list;
    }

    // -------------------------------------------------------
    // Get students NOT yet assigned to this class
    // -------------------------------------------------------
    public List<Student> getUnassignedStudents(int classId) {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT id, first_name, last_name FROM students "
                   + "WHERE id NOT IN (SELECT student_id FROM student_class WHERE class_id = ?) "
                   + "ORDER BY first_name, last_name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, classId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Student s = new Student();
                s.setId        (rs.getInt   ("id"));
                s.setFirstName (rs.getString("first_name"));
                s.setLastName  (rs.getString("last_name"));
                list.add(s);
            }
        } catch (SQLException e) {
            System.err.println("[StudentClassController] ERROR unassigned: " + e.getMessage());
        }
        return list;
    }

    // -------------------------------------------------------
    // Get next available index number for the class
    // -------------------------------------------------------
    public int getNextIndexNumber(int classId) {
        String sql = "SELECT COALESCE(MAX(index_number), 0) + 1 FROM student_class WHERE class_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, classId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            System.err.println("[StudentClassController] ERROR next index: " + e.getMessage());
        }
        return 1;
    }

    // -------------------------------------------------------
    // Assign student to class
    // -------------------------------------------------------
    public boolean assignStudent(StudentClass sc) {
        String sql = "INSERT INTO student_class (student_id, class_id, index_number, enrollment_date, status) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt   (1, sc.getStudentId());
            stmt.setInt   (2, sc.getClassId());
            stmt.setInt   (3, sc.getIndexNumber());
            stmt.setDate  (4, sc.getEnrollmentDate());
            stmt.setString(5, sc.getStatus());
            return stmt.executeUpdate() > 0;

        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RuntimeException("This student is already assigned to this class.");
        } catch (SQLException e) {
            System.err.println("[StudentClassController] ERROR assigning: " + e.getMessage());
            throw new RuntimeException("Failed to assign student: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // Update status of an assignment (Active/Inactive/Graduated)
    // -------------------------------------------------------
    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE student_class SET status=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt   (2, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[StudentClassController] ERROR updating status: " + e.getMessage());
            throw new RuntimeException("Failed to update status: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // Remove a student-class assignment
    // -------------------------------------------------------
    public boolean removeAssignment(int id) {
        String sql = "DELETE FROM student_class WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[StudentClassController] ERROR removing: " + e.getMessage());
            throw new RuntimeException("Failed to remove assignment: " + e.getMessage());
        }
    }

    public int getPageSize() { return PAGE_SIZE; }
}
