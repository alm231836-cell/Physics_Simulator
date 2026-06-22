@echo off
setlocal

:: Set the JavaFX SDK path
set JAVAFX_PATH=lib/javafx/lib

if not exist bin mkdir bin

:: Compile the application source tree
javac --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.graphics -d bin 
src/com/physicssim/app/*.java src/com/physicssim/components/*.java src/com/physicssim/model/*.java src/com/physicssim/theme/*.java src/com/physicssim/views/*.java src/com/physicssim/navigation/*.java src/com/physicssim/features/pendulum/*.java src/com/physicssim/features/simulations/*.java
if %errorlevel% neq 0 (
    echo Compilation failed.
    exit /b 1
)

:: Run the Java program
java --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.graphics -cp bin com.physicssim.app.PhysicsSimulatorApp
