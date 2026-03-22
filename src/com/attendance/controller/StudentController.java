package com.attendance.controller;

import com.attendance.model.Student;
import com.attendance.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentController {

    private static final int PAGE_SIZE = 10;

    // -------------------------------------------------------
    // Count students matching search (for pagination)
    // -------------------------------------------------------
    public int getTotalStudentsFiltered(String search) {
        String sql = "SELECT COUNT(*) FROM students "
                   + "WHERE first_name LIKE ? OR last_name LIKE ? OR email LIKE ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            String like = "%" + search + "%";
            stmt.setString(1, like);
            stmt.setString(2, like);
            stmt.setString(3, like);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            System.err.println("[StudentController] ERROR counting: " + e.getMessage());
        }
        return 0;
    }

    // -------------------------------------------------------
    // Get one page of students
    // -------------------------------------------------------
    public List<Student> getStudents(int page, String search) {
        List<Student> list = new ArrayList<>();
        int offset = (page - 1) * PAGE_SIZE;

        String sql = "SELECT * FROM students "
                   + "WHERE first_name LIKE ? OR last_name LIKE ? OR email LIKE ? "
                   + "ORDER BY id DESC LIMIT ? OFFSET ?";

        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            String like = "%" + search + "%";
            stmt.setString(1, like);
            stmt.setString(2, like);
            stmt.setString(3, like);
            stmt.setInt(4, PAGE_SIZE);
            stmt.setInt(5, offset);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            System.err.println("[StudentController] ERROR fetching students: " + e.getMessage());
        }
        return list;
    }

    // -------------------------------------------------------
    // Get single student by ID (used to fill form)
    // -------------------------------------------------------
    public Student getStudentById(int id) {
        String sql = "SELECT * FROM students WHERE id = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);

        } catch (SQLException e) {
            System.err.println("[StudentController] ERROR fetching student: " + e.getMessage());
        }
        return null;
    }

    // -------------------------------------------------------
    // Add new student
    // -------------------------------------------------------
    public boolean addStudent(Student s) {
        String sql = "INSERT INTO students (first_name, last_name, date_of_birth, gender, email, phone, address) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, s.getFirstName());
            stmt.setString(2, s.getLastName());
            stmt.setDate  (3, s.getDateOfBirth());
            stmt.setString(4, s.getGender());
            stmt.setString(5, s.getEmail().isEmpty() ? null : s.getEmail());
            stmt.setString(6, s.getPhone());
            stmt.setString(7, s.getAddress());
            return stmt.executeUpdate() > 0;

        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RuntimeException("A student with this email already exists.");
        } catch (SQLException e) {
            System.err.println("[StudentController] ERROR adding student: " + e.getMessage());
            throw new RuntimeException("Failed to add student: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // Update existing student
    // -------------------------------------------------------
    public boolean updateStudent(Student s) {
        String sql = "UPDATE students SET first_name=?, last_name=?, date_of_birth=?, "
                   + "gender=?, email=?, phone=?, address=? WHERE id=?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, s.getFirstName());
            stmt.setString(2, s.getLastName());
            stmt.setDate  (3, s.getDateOfBirth());
            stmt.setString(4, s.getGender());
            stmt.setString(5, s.getEmail().isEmpty() ? null : s.getEmail());
            stmt.setString(6, s.getPhone());
            stmt.setString(7, s.getAddress());
            stmt.setInt   (8, s.getId());
            return stmt.executeUpdate() > 0;

        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RuntimeException("A student with this email already exists.");
        } catch (SQLException e) {
            System.err.println("[StudentController] ERROR updating student: " + e.getMessage());
            throw new RuntimeException("Failed to update student: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // Delete student
    // -------------------------------------------------------
    public boolean deleteStudent(int id) {
        String sql = "DELETE FROM students WHERE id=?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[StudentController] ERROR deleting student: " + e.getMessage());
            throw new RuntimeException("Failed to delete student: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // Get all students for dropdown (no pagination)
    // -------------------------------------------------------
    public List<Student> getAllStudentsForDropdown() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT id, first_name, last_name FROM students ORDER BY first_name, last_name";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Student s = new Student();
                s.setId(rs.getInt("id"));
                s.setFirstName(rs.getString("first_name"));
                s.setLastName (rs.getString("last_name"));
                list.add(s);
            }
        } catch (SQLException e) {
            System.err.println("[StudentController] ERROR fetching dropdown: " + e.getMessage());
        }
        return list;
    }

    // -------------------------------------------------------
    // Map a ResultSet row to a Student object
    // -------------------------------------------------------
    private Student mapRow(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId          (rs.getInt   ("id"));
        s.setFirstName   (rs.getString("first_name"));
        s.setLastName    (rs.getString("last_name"));
        s.setDateOfBirth (rs.getDate  ("date_of_birth"));
        s.setGender      (rs.getString("gender"));
        s.setEmail       (rs.getString("email"));
        s.setPhone       (rs.getString("phone"));
        s.setAddress     (rs.getString("address"));
        return s;
    }

    public int getPageSize() { return PAGE_SIZE; }
}
