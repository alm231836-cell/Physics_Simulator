@echo off
:: Check if a class name is provided
if "%1"=="" (
    echo Please provide the class name to run.
    echo Usage: run ClassName
    exit /b 1
)

:: Set the JavaFX SDK path
set JAVAFX_PATH=lib/javafx/lib

:: Compile the Java file
javac --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.graphics -d bin src/practice/%1.java
if %errorlevel% neq 0 (
    echo Compilation failed.
    exit /b 1
)

:: Run the Java program
java --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.graphics -cp bin %1