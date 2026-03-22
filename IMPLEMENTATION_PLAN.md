# Implementation Plan — Student Attendance Management System

> Java Swing | MySQL | MVC Architecture | Pure JDBC

---

## Phase 1: Database Setup & Verify Connection

**Goal:** Create all database tables with proper keys and verify the existing DB connection works.

| # | Task | Status |
|---|------|--------|
| 1.1 | Run `database.sql` script to create all 4 tables | Not Started |
| 1.2 | Verify DB connection through the app (F5 → check green card) | Not Started |
| 1.3 | Test with a sample INSERT/SELECT manually in MySQL Workbench | Not Started |

**Files to create/modify:**
- `database.sql` (root) — full DB creation script

---

## Phase 2: Model Classes (POJO)

**Goal:** Create plain Java classes that match each database table.

| # | Task | Status |
|---|------|--------|
| 2.1 | Create `Student.java` model — fields, constructor, getters/setters | Not Started |
| 2.2 | Create `ClassRoom.java` model — fields, constructor, getters/setters | Not Started |
| 2.3 | Create `StudentClass.java` model — fields, constructor, getters/setters | Not Started |
| 2.4 | Create `Attendance.java` model — fields, constructor, getters/setters | Not Started |

**Files to create:**
```
src/com/attendance/model/Student.java
src/com/attendance/model/ClassRoom.java
src/com/attendance/model/StudentClass.java
src/com/attendance/model/Attendance.java
```

---

## Phase 3: Student Management (Full CRUD)

**Goal:** Build the first complete feature — add, update, delete, view students with pagination.

| # | Task | Status |
|---|------|--------|
| 3.1 | Create `StudentController.java` — CRUD SQL queries (INSERT, SELECT, UPDATE, DELETE) | Not Started |
| 3.2 | Add pagination helper to `StudentController` (LIMIT/OFFSET queries) | Not Started |
| 3.3 | Create `StudentPanel.java` — Swing form (text fields, buttons, table) | Not Started |
| 3.4 | Add pagination controls (Previous / Next buttons, page label) | Not Started |
| 3.5 | Wire panel events → controller methods | Not Started |
| 3.6 | Add search/filter by name | Not Started |
| 3.7 | Add input validation (empty fields, email format, phone format) | Not Started |
| 3.8 | Test all CRUD operations end-to-end | Not Started |

**Files to create:**
```
src/com/attendance/controller/StudentController.java
src/com/attendance/view/StudentPanel.java
```

---

## Phase 4: Class Management (Full CRUD)

**Goal:** Same pattern as Phase 3 but for Classes table.

| # | Task | Status |
|---|------|--------|
| 4.1 | Create `ClassController.java` — CRUD SQL queries | Not Started |
| 4.2 | Add pagination to class queries | Not Started |
| 4.3 | Create `ClassPanel.java` — Swing form + table | Not Started |
| 4.4 | Add pagination controls | Not Started |
| 4.5 | Wire events → controller | Not Started |
| 4.6 | Add input validation | Not Started |
| 4.7 | Test all CRUD operations end-to-end | Not Started |

**Files to create:**
```
src/com/attendance/controller/ClassController.java
src/com/attendance/view/ClassPanel.java
```

---

## Phase 5: Student-Class Assignment

**Goal:** Let admin assign students to classes with index numbers.

| # | Task | Status |
|---|------|--------|
| 5.1 | Create `StudentClassController.java` — assign, remove, list queries | Not Started |
| 5.2 | Auto-generate next index number per class | Not Started |
| 5.3 | Create `StudentClassPanel.java` — class dropdown, student list, assign button | Not Started |
| 5.4 | Show assigned students in a table with pagination | Not Started |
| 5.5 | Add status change (Active / Inactive / Graduated) | Not Started |
| 5.6 | Prevent duplicate assignment (same student + same class) | Not Started |
| 5.7 | Test all assignment operations | Not Started |

**Files to create:**
```
src/com/attendance/controller/StudentClassController.java
src/com/attendance/view/StudentClassPanel.java
```

---

## Phase 6: Attendance Marking

**Goal:** Select class + date → show student list → mark Present/Absent → save.

| # | Task | Status |
|---|------|--------|
| 6.1 | Create `AttendanceController.java` — save, fetch, update attendance queries | Not Started |
| 6.2 | Create `AttendancePanel.java` — class dropdown, date picker, student table with checkboxes | Not Started |
| 6.3 | Load students for selected class, pre-fill if attendance already exists for that date | Not Started |
| 6.4 | Save/update attendance records to DB | Not Started |
| 6.5 | Add remarks column (late, excused, etc.) | Not Started |
| 6.6 | Test marking attendance and editing existing records | Not Started |

**Files to create:**
```
src/com/attendance/controller/AttendanceController.java
src/com/attendance/view/AttendancePanel.java
```

---

## Phase 7: Analytics Dashboard

**Goal:** Build a home dashboard showing key stats and quick summaries.

| # | Task | Status |
|---|------|--------|
| 7.1 | Create `DashboardController.java` — aggregate queries (counts, percentages) | Not Started |
| 7.2 | Create `DashboardPanel.java` — stat cards, summary tables | Not Started |
| 7.3 | Show: Total Students, Total Classes, Today's Attendance %, Recent Activity | Not Started |
| 7.4 | Show: Top 5 students with lowest attendance | Not Started |
| 7.5 | Show: Class-wise attendance summary | Not Started |
| 7.6 | Add a refresh button | Not Started |
| 7.7 | Test dashboard with real data | Not Started |

**Files to create:**
```
src/com/attendance/controller/DashboardController.java
src/com/attendance/view/DashboardPanel.java
```

---

## Phase 8: Reporting (Jasper Reports)

**Goal:** Generate printable PDF reports for attendance analysis.

| # | Task | Status |
|---|------|--------|
| 8.1 | Download and add JasperReports JAR files to `lib/` | Not Started |
| 8.2 | Create `ReportController.java` — data fetching for reports | Not Started |
| 8.3 | Create `ReportPanel.java` — report selection form (by student / by class / date range) | Not Started |
| 8.4 | Design report template: Student Attendance Report (.jrxml) | Not Started |
| 8.5 | Design report template: Class Attendance Report (.jrxml) | Not Started |
| 8.6 | Generate and display PDF reports | Not Started |
| 8.7 | Test report generation with sample data | Not Started |

**Files to create:**
```
src/com/attendance/controller/ReportController.java
src/com/attendance/view/ReportPanel.java
reports/student_attendance.jrxml
reports/class_attendance.jrxml
```

---

## Phase 9: Main Navigation & Final Integration

**Goal:** Connect all panels into MainFrame with sidebar/tab navigation.

| # | Task | Status |
|---|------|--------|
| 9.1 | Update `MainFrame.java` — add sidebar menu with navigation buttons | Not Started |
| 9.2 | Add CardLayout to switch between panels (Dashboard, Students, Classes, etc.) | Not Started |
| 9.3 | Integrate all panels into MainFrame | Not Started |
| 9.4 | Set Dashboard as the default landing panel | Not Started |
| 9.5 | Full end-to-end testing of all features | Not Started |

**Files to modify:**
```
src/com/attendance/view/MainFrame.java  (major update)
src/com/attendance/Main.java            (minor update)
```

---

## Phase 10: Polish & Finalization

**Goal:** Bug fixes, UI improvements, final testing.

| # | Task | Status |
|---|------|--------|
| 10.1 | Review error messages and try-catch blocks across all files | Not Started |
| 10.2 | Test with empty database (no data edge case) | Not Started |
| 10.3 | Test pagination boundaries | Not Started |
| 10.4 | UI alignment and font consistency check | Not Started |
| 10.5 | Update README.md with final instructions | Not Started |
| 10.6 | Clean up unused code and temp files | Not Started |

---

## Summary — File Map

```
attendance_system/
├── database.sql                              ← Phase 1
│
├── src/com/attendance/
│   ├── Main.java                             ← Phase 9 (update)
│   │
│   ├── model/
│   │   ├── Student.java                      ← Phase 2
│   │   ├── ClassRoom.java                    ← Phase 2
│   │   ├── StudentClass.java                 ← Phase 2
│   │   └── Attendance.java                   ← Phase 2
│   │
│   ├── controller/
│   │   ├── StudentController.java            ← Phase 3
│   │   ├── ClassController.java              ← Phase 4
│   │   ├── StudentClassController.java       ← Phase 5
│   │   ├── AttendanceController.java         ← Phase 6
│   │   ├── DashboardController.java          ← Phase 7
│   │   └── ReportController.java             ← Phase 8
│   │
│   ├── view/
│   │   ├── MainFrame.java                    ← Phase 9 (update)
│   │   ├── StudentPanel.java                 ← Phase 3
│   │   ├── ClassPanel.java                   ← Phase 4
│   │   ├── StudentClassPanel.java            ← Phase 5
│   │   ├── AttendancePanel.java              ← Phase 6
│   │   ├── DashboardPanel.java               ← Phase 7
│   │   └── ReportPanel.java                  ← Phase 8
│   │
│   └── utils/
│       ├── DBConnection.java                 ← Already done
│       └── EnvLoader.java                    ← Already done
│
├── reports/
│   ├── student_attendance.jrxml              ← Phase 8
│   └── class_attendance.jrxml                ← Phase 8
│
└── lib/
    ├── mysql-connector-j-9.6.0.jar           ← Already done
    └── jasperreports-x.x.x.jar              ← Phase 8
```

---

## Development Rules

1. **One phase at a time** — complete and test before moving on.
2. **Try-catch everywhere** — every DB call must have proper error handling with clear messages.
3. **No ORM** — use pure `PreparedStatement` SQL queries only.
4. **Pagination** — every list panel must have Previous/Next with page size of 10.
5. **MVC strict** — Views never touch the database. Controllers never build UI.
6. **Simple queries** — diploma-level SQL (SELECT, INSERT, UPDATE, DELETE, JOIN, COUNT, LIMIT).
