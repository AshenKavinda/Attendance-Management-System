-- ============================================================
-- Student Attendance Management System — Database Script
-- Run this script in MySQL Workbench or MySQL CLI
-- ============================================================

-- Step 1: Create the database
CREATE DATABASE IF NOT EXISTS attendance_db;
USE attendance_db;

-- ============================================================
-- Step 2: Create tables
-- ============================================================

-- -------------------------------------------------------
-- Students Table
-- Stores all student information
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS students (
    id              INT             AUTO_INCREMENT PRIMARY KEY,
    first_name      VARCHAR(50)     NOT NULL,
    last_name       VARCHAR(50)     NOT NULL,
    date_of_birth   DATE            NULL,
    gender          VARCHAR(10)     NULL,
    email           VARCHAR(100)    NULL UNIQUE,
    phone           VARCHAR(20)     NULL,
    address         VARCHAR(255)    NULL,
    created_at      DATETIME        DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- -------------------------------------------------------
-- Classes Table
-- Stores all class information
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS classes (
    id              INT             AUTO_INCREMENT PRIMARY KEY,
    class_name      VARCHAR(100)    NOT NULL,
    class_code      VARCHAR(20)     NOT NULL UNIQUE,
    section         VARCHAR(10)     NULL,
    year            INT             NULL,
    created_at      DATETIME        DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- -------------------------------------------------------
-- Student_Class Table
-- Maps students to classes with index numbers
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS student_class (
    id                  INT             AUTO_INCREMENT PRIMARY KEY,
    student_id          INT             NOT NULL,
    class_id            INT             NOT NULL,
    index_number        INT             NOT NULL,
    enrollment_date     DATE            DEFAULT (CURRENT_DATE),
    status              VARCHAR(20)     DEFAULT 'Active',

    -- Foreign keys
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (class_id)   REFERENCES classes(id)  ON DELETE CASCADE,

    -- One student can only be assigned once to the same class
    UNIQUE KEY unique_student_class (student_id, class_id),

    -- Index numbers must be unique within a class
    UNIQUE KEY unique_index_in_class (class_id, index_number)
);

-- -------------------------------------------------------
-- Attendance Table
-- Stores daily attendance records
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS attendance (
    id                  INT             AUTO_INCREMENT PRIMARY KEY,
    student_class_id    INT             NOT NULL,
    date                DATE            NOT NULL,
    status              VARCHAR(10)     NOT NULL DEFAULT 'Absent',
    remarks             VARCHAR(255)    NULL,

    -- Foreign key
    FOREIGN KEY (student_class_id) REFERENCES student_class(id) ON DELETE CASCADE,

    -- One attendance record per student-class per date
    UNIQUE KEY unique_attendance_per_day (student_class_id, date)
);

-- ============================================================
-- Step 3: Create indexes for faster queries
-- ============================================================

CREATE INDEX idx_students_name       ON students (first_name, last_name);
CREATE INDEX idx_students_email      ON students (email);
CREATE INDEX idx_classes_code        ON classes (class_code);
CREATE INDEX idx_student_class_sid   ON student_class (student_id);
CREATE INDEX idx_student_class_cid   ON student_class (class_id);
CREATE INDEX idx_attendance_date     ON attendance (date);
CREATE INDEX idx_attendance_scid     ON attendance (student_class_id);

-- ============================================================
-- Step 4: Insert sample data (optional — for testing)
-- ============================================================

-- Sample Students
INSERT INTO students (first_name, last_name, date_of_birth, gender, email, phone, address) VALUES
('Ashen',    'Perera',     '2003-05-15', 'Male',   'ashen@email.com',    '0771234567', '123 Main St, Colombo'),
('Nimali',   'Fernando',   '2004-02-20', 'Female', 'nimali@email.com',   '0777654321', '456 Galle Rd, Kandy'),
('Kamal',    'Silva',      '2003-11-10', 'Male',   'kamal@email.com',    '0769876543', '789 Lake Rd, Galle'),
('Sithara',  'Jayawardena','2004-07-25', 'Female', 'sithara@email.com',  '0751112233', '321 Hill St, Matara'),
('Dinesh',   'Bandara',    '2003-09-03', 'Male',   'dinesh@email.com',   '0784445566', '654 Temple Rd, Kandy');

-- Sample Classes
INSERT INTO classes (class_name, class_code, section, year) VALUES
('Software Engineering',  'SE-2024',  'A', 2024),
('Database Management',   'DBM-2024', 'A', 2024),
('Web Development',       'WD-2024',  'B', 2024);

-- Sample Student-Class Assignments
INSERT INTO student_class (student_id, class_id, index_number, enrollment_date, status) VALUES
(1, 1, 1, '2024-01-15', 'Active'),
(2, 1, 2, '2024-01-15', 'Active'),
(3, 1, 3, '2024-01-15', 'Active'),
(4, 2, 1, '2024-01-20', 'Active'),
(5, 2, 2, '2024-01-20', 'Active'),
(1, 2, 3, '2024-01-20', 'Active'),
(2, 3, 1, '2024-02-01', 'Active'),
(3, 3, 2, '2024-02-01', 'Active');

-- Sample Attendance Records
INSERT INTO attendance (student_class_id, date, status, remarks) VALUES
(1, '2024-03-01', 'Present', NULL),
(2, '2024-03-01', 'Present', NULL),
(3, '2024-03-01', 'Absent',  'Sick leave'),
(4, '2024-03-01', 'Present', NULL),
(5, '2024-03-01', 'Absent',  NULL),
(6, '2024-03-01', 'Present', 'Late'),
(1, '2024-03-02', 'Present', NULL),
(2, '2024-03-02', 'Absent',  NULL),
(3, '2024-03-02', 'Present', NULL);

-- ============================================================
-- Done! Verify with:
--   SELECT * FROM students;
--   SELECT * FROM classes;
--   SELECT * FROM student_class;
--   SELECT * FROM attendance;
-- ============================================================
