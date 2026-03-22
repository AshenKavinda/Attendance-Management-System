@echo off
title Attendance System
echo ================================================
echo   Starting Attendance System...
echo ================================================

REM Check bin folder exists
if not exist "bin" (
    echo.
    echo  ERROR: bin\ folder not found.
    echo  Please run compile.bat first.
    echo.
    pause
    exit /b 1
)

REM Check lib folder has a jar
if not exist "lib\*.jar" (
    echo.
    echo  ERROR: No JAR file found in lib\ folder.
    echo  Please add mysql-connector-j-x.x.x.jar to lib\
    echo.
    pause
    exit /b 1
)

REM Check .env file exists
if not exist ".env" (
    echo.
    echo  ERROR: .env file not found in project root.
    echo  Please create .env with your database credentials.
    echo.
    pause
    exit /b 1
)

echo Running...
java -cp "bin;lib\*" com.attendance.Main

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo  Application exited with an error. See output above.
    pause
)
