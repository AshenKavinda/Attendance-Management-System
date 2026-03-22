package com.attendance.controller;

import com.attendance.model.ClassRoom;
import com.attendance.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClassController {

    private static final int PAGE_SIZE = 10;

    // -------------------------------------------------------
    // Count classes matching search
    // -------------------------------------------------------
    public int getTotalClassesFiltered(String search) {
        String sql = "SELECT COUNT(*) FROM classes "
                   + "WHERE class_name LIKE ? OR class_code LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String like = "%" + search + "%";
            stmt.setString(1, like);
            stmt.setString(2, like);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            System.err.println("[ClassController] ERROR counting: " + e.getMessage());
        }
        return 0;
    }

    // -------------------------------------------------------
    // Get one page of classes
    // -------------------------------------------------------
    public List<ClassRoom> getClasses(int page, String search) {
        List<ClassRoom> list = new ArrayList<>();
        int offset = (page - 1) * PAGE_SIZE;

        String sql = "SELECT * FROM classes "
                   + "WHERE class_name LIKE ? OR class_code LIKE ? "
                   + "ORDER BY id DESC LIMIT ? OFFSET ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String like = "%" + search + "%";
            stmt.setString(1, like);
            stmt.setString(2, like);
            stmt.setInt   (3, PAGE_SIZE);
            stmt.setInt   (4, offset);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            System.err.println("[ClassController] ERROR fetching classes: " + e.getMessage());
        }
        return list;
    }

    // -------------------------------------------------------
    // Add new class
    // -------------------------------------------------------
    public boolean addClass(ClassRoom c) {
        String sql = "INSERT INTO classes (class_name, class_code, section, year) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, c.getClassName());
            stmt.setString(2, c.getClassCode());
            stmt.setString(3, c.getSection());
            stmt.setInt   (4, c.getYear());
            return stmt.executeUpdate() > 0;

        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RuntimeException("Class code '" + c.getClassCode() + "' already exists. Use a unique code.");
        } catch (SQLException e) {
            System.err.println("[ClassController] ERROR adding class: " + e.getMessage());
            throw new RuntimeException("Failed to add class: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // Update existing class
    // -------------------------------------------------------
    public boolean updateClass(ClassRoom c) {
        String sql = "UPDATE classes SET class_name=?, class_code=?, section=?, year=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, c.getClassName());
            stmt.setString(2, c.getClassCode());
            stmt.setString(3, c.getSection());
            stmt.setInt   (4, c.getYear());
            stmt.setInt   (5, c.getId());
            return stmt.executeUpdate() > 0;

        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RuntimeException("Class code '" + c.getClassCode() + "' already exists. Use a unique code.");
        } catch (SQLException e) {
            System.err.println("[ClassController] ERROR updating class: " + e.getMessage());
            throw new RuntimeException("Failed to update class: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // Delete class
    // -------------------------------------------------------
    public boolean deleteClass(int id) {
        String sql = "DELETE FROM classes WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[ClassController] ERROR deleting class: " + e.getMessage());
            throw new RuntimeException("Failed to delete class: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // Get all classes for dropdowns (no pagination)
    // -------------------------------------------------------
    public List<ClassRoom> getAllClassesForDropdown() {
        List<ClassRoom> list = new ArrayList<>();
        String sql = "SELECT * FROM classes ORDER BY class_name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            System.err.println("[ClassController] ERROR fetching dropdown: " + e.getMessage());
        }
        return list;
    }

    // -------------------------------------------------------
    // Map a ResultSet row to a ClassRoom object
    // -------------------------------------------------------
    private ClassRoom mapRow(ResultSet rs) throws SQLException {
        ClassRoom c = new ClassRoom();
        c.setId        (rs.getInt   ("id"));
        c.setClassName (rs.getString("class_name"));
        c.setClassCode (rs.getString("class_code"));
        c.setSection   (rs.getString("section"));
        c.setYear      (rs.getInt   ("year"));
        return c;
    }

    public int getPageSize() { return PAGE_SIZE; }
}
