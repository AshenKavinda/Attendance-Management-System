# Attendance Management System

> A desktop application for managing student attendance — built with **Java Swing**, **MySQL**, and a clean **MVC architecture**.

![Java](https://img.shields.io/badge/Java-17%2B-orange?logo=openjdk)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql)
![Swing](https://img.shields.io/badge/UI-Java%20Swing-lightgrey)
![Architecture](https://img.shields.io/badge/Architecture-MVC-green)
![JDBC](https://img.shields.io/badge/DB%20Access-Pure%20JDBC-yellow)

---

## Overview

The **Attendance Management System** is a desktop application that allows educational institutions to manage students, classes, class assignments, and daily attendance records. It features a modern dark-themed sidebar UI, a dashboard with live stats, and a report module powered by JasperReports.

---

## Features

| Module | Description |
|---|---|
| **Dashboard** | Live stats — total students, classes, attendance rate today |
| **Students** | Add, edit, delete student records |
| **Classes** | Manage class information (name, code, section, year) |
| **Assignments** | Assign students to classes with unique index numbers |
| **Attendance** | Mark Present / Absent for any class on any date |
| **Reports** | Generate PDF reports — class attendance, student summary, sign-in sheet |

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17+ |
| UI Framework | Java Swing |
| Database | MySQL 8.0 |
| DB Access | Pure JDBC (no ORM) |
| Reports | JasperReports |
| Architecture | MVC (Model-View-Controller) |
| Config | `.env` file (built-in parser, no extra library) |

---

## Project Structure

```
attendance_system/
│
├── src/com/attendance/
│   ├── Main.java                        ← Application entry point
│   │
│   ├── model/                           ← Data / POJO classes
│   │   ├── Student.java
│   │   ├── ClassRoom.java
│   │   ├── StudentClass.java
│   │   └── Attendance.java
│   │
│   ├── controller/                      ← Business logic & DB operations
│   │   ├── DashboardController.java
│   │   ├── StudentController.java
│   │   ├── ClassController.java
│   │   ├── StudentClassController.java
│   │   ├── AttendanceController.java
│   │   └── ReportController.java
│   │
│   ├── view/                            ← Swing UI panels
│   │   ├── MainFrame.java               ← Main window with sidebar navigation
│   │   ├── DashboardPanel.java
│   │   ├── StudentPanel.java
│   │   ├── ClassPanel.java
│   │   ├── StudentClassPanel.java
│   │   ├── AttendancePanel.java
│   │   ├── ReportPanel.java
│   │   └── DatePickerField.java
│   │
│   └── utils/
│       ├── DBConnection.java            ← MySQL connection singleton (JDBC)
│       └── EnvLoader.java              ← Reads .env config file
│
├── reports/                             ← JasperReports .jrxml templates
│   ├── class_attendance.jrxml
│   ├── class_summary.jrxml
│   ├── student_attendance.jrxml
│   └── sign_in_sheet.jrxml
│
├── lib/                                 ← External JAR files (not committed)
│   ├── mysql-connector-j-x.x.x.jar
│   └── jasperreports-x.x.x.jar
│
├── bin/                                 ← Compiled .class files (auto-generated)
│
├── .env                                 ← DB credentials (NOT committed to Git)
├── .gitignore
├── database.sql                         ← Full DB schema + sample data
├── compile.bat                          ← Windows compile script
├── run.bat                              ← Windows run script
└── README.md
```

---

## Database Schema

```
students ──────────────────────────────────────────────────
  id · first_name · last_name · date_of_birth · gender
  email · phone · address · created_at · updated_at

classes ───────────────────────────────────────────────────
  id · class_name · class_code · section · year
  created_at · updated_at

student_class ─────────────────────────────────────────────
  id · student_id (FK) · class_id (FK)
  index_number · enrollment_date · status

attendance ────────────────────────────────────────────────
  id · student_class_id (FK) · date · status · remarks
```

> Relationships: `students` → `student_class` ← `classes` → `attendance`

---

## Prerequisites

| Tool | Minimum Version | Download |
|---|---|---|
| JDK | 17 | https://adoptium.net |
| MySQL | 8.0 | https://dev.mysql.com/downloads/mysql/ |
| MySQL Connector/J | 8.x | https://dev.mysql.com/downloads/connector/j/ |
| JasperReports | 6.x | https://community.jaspersoft.com/download-jasperreports-library |

Verify your environment:

```powershell
java -version       # expect 17+
javac -version      # expect 17+
mysql --version     # expect 8.x
```

---

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/AshenKavinda/Attendance-Management-System.git
cd Attendance-Management-System
```

### 2. Set up the database

Run the provided script in MySQL CLI or MySQL Workbench:

```bash
mysql -u root -p < database.sql
```

This creates the `attendance_db` database, all tables, indexes, and optional sample data.

### 3. Add library JARs

Copy these files into the `lib\` folder:

- `mysql-connector-j-x.x.x.jar`
- `jasperreports-x.x.x.jar` (and its dependencies)

### 4. Configure `.env`

Create a `.env` file in the project root (it is already in `.gitignore`):

```env
DB_HOST=localhost
DB_PORT=3306
DB_NAME=attendance_db
DB_USER=root
DB_PASSWORD=your_password_here
```

### 5. Compile

```cmd
compile.bat
```

### 6. Run

```cmd
run.bat
```

---

## Manual Compile & Run

```cmd
:: Compile all sources
javac -encoding UTF-8 -cp "lib\*" -d "bin" ^
  src\com\attendance\utils\EnvLoader.java ^
  src\com\attendance\utils\DBConnection.java ^
  src\com\attendance\model\*.java ^
  src\com\attendance\controller\*.java ^
  src\com\attendance\view\*.java ^
  src\com\attendance\Main.java

:: Run
java -cp "bin;lib\*" com.attendance.Main
```

---

## How It Works

```
App starts
  └─ EnvLoader reads .env
  └─ DBConnection tests MySQL connection (SELECT 1)
       ├─ PASS → MainFrame opens with full navigation
       └─ FAIL → Error dialog shown, app exits
```

Once running, the sidebar provides navigation between all modules. Each panel communicates with its controller, which executes JDBC queries and returns model objects back to the view.

---

## License

This project is for educational purposes. Feel free to use and adapt it.

