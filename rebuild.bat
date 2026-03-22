@echo off
echo Cleaning bin...
if exist "bin" rmdir /s /q bin
mkdir bin

echo Collecting source files...
dir /s /b "src\*.java" > sources.txt

echo Compiling...
javac -cp "lib\*" -d bin @sources.txt
if errorlevel 1 (
    echo.
    echo  BUILD FAILED
    pause
    exit /b 1
)

echo.
echo  BUILD OK - Starting app...
java -cp "bin;lib\*" com.attendance.Main
