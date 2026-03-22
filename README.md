# Attendance Management System

Java Swing + MySQL desktop application — MVC architecture.

---

## Project Structure

```
attendance_system/
│
├── src/
│   └── com/
│       └── attendance/
│           ├── Main.java                  ← App entry point
│           ├── controller/               ← MVC: Controllers (business logic)
│           ├── model/                    ← MVC: Models (data / POJO classes)
│           ├── view/
│           │   └── MainFrame.java        ← MVC: Main Swing window
│           └── utils/
│               ├── DBConnection.java     ← MySQL connection (pure JDBC)
│               └── EnvLoader.java        ← Reads .env config file
│
├── lib/                                  ← Place JAR files here
│   └── mysql-connector-j-x.x.x.jar
│
├── bin/                                  ← Compiled .class files (auto-generated)
│
├── .env                                  ← DB credentials (DO NOT commit to Git)
├── .gitignore
├── compile.bat                           ← Windows compile script
├── run.bat                               ← Windows run script
└── README.md
```

---

## Computer Dependency Check Commands

Run these in Command Prompt or PowerShell to verify your environment:

```cmd
REM Check Java version (need JDK 8 or higher)
java -version

REM Check Java compiler
javac -version

REM Check MySQL server CLI
mysql --version

REM Check MySQL is running (Windows Service)
sc query MySQL

REM Alternative: check MySQL process
tasklist | findstr mysqld
```

Expected output examples:
```
java version "17.0.x" ...
javac 17.0.x
mysql  Ver 8.x.xx ...
```

---

## Setup Steps

### Step 1 — Install Dependencies

| Tool | Where to get |
|------|-------------|
| JDK 17+ | https://adoptium.net |
| MySQL 8+ | https://dev.mysql.com/downloads/mysql/ |
| MySQL Connector/J | https://dev.mysql.com/downloads/connector/j/ |

### Step 2 — Create the Database

Open MySQL CLI or MySQL Workbench and run:

```sql
CREATE DATABASE attendance_db;
```

### Step 3 — Add MySQL JDBC JAR

1. Download `mysql-connector-j-x.x.x.jar` from the link above  
2. Copy it into the `lib\` folder

### Step 4 — Configure .env

Edit the `.env` file in the project root:

```env
DB_HOST=localhost
DB_PORT=3306
DB_NAME=attendance_db
DB_USER=root
DB_PASSWORD=your_password_here
```

### Step 5 — Compile

Double-click `compile.bat`  
Or run in Command Prompt from the project root:

```cmd
compile.bat
```

### Step 6 — Run

Double-click `run.bat`  
Or run in Command Prompt:

```cmd
run.bat
```

---

## Manual Compile & Run (without .bat files)

```cmd
REM From the project root folder:

REM Compile
javac -encoding UTF-8 -cp "lib\*" -d "bin" src\com\attendance\utils\EnvLoader.java src\com\attendance\utils\DBConnection.java src\com\attendance\view\MainFrame.java src\com\attendance\Main.java

REM Run
java -cp "bin;lib\*" com.attendance.Main
```

---

## What Happens at Startup

1. App loads `.env` file  
2. App tries to connect to MySQL (`SELECT 1` test query)  
3. Console shows `PASSED` or `FAILED` with reason  
4. Swing window opens — green card = connected, red card = not connected  

---

## Technologies

- **Language**: Java 17  
- **UI**: Java Swing  
- **Database**: MySQL 8 (pure JDBC — no ORM)  
- **Architecture**: MVC  
- **Config**: `.env` file (manual parser, no extra library needed)
