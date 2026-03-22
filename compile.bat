@echo off
title Attendance System - Compile
echo ================================================
echo   Compiling Attendance System...
echo ================================================

REM Check if lib folder has a jar
if not exist "lib\*.jar" (
    echo.
    echo  ERROR: No JAR file found in lib\ folder.
    echo  Please download mysql-connector-j-x.x.x.jar
    echo  and place it inside the lib\ folder.
    echo.
    pause
    exit /b 1
)

REM Create bin folder if missing
if not exist "bin" mkdir bin

REM Collect all .java source files
echo Collecting source files...
dir /s /b "src\*.java" > sources.txt 2>nul

if not exist sources.txt (
    echo ERROR: No .java files found in src\
    pause
    exit /b 1
)

REM Compile
echo Compiling...
javac -encoding UTF-8 -cp "lib\*" -d "bin" @sources.txt

if %ERRORLEVEL% EQU 0 (
    echo.
    echo  ✔  Compilation SUCCESSFUL
    echo  Output -> bin\
    echo.
) else (
    echo.
    echo  ✘  Compilation FAILED
    echo  Check the errors above and fix them.
    echo.
)

del sources.txt 2>nul
pause
