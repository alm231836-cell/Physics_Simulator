#!/bin/bash
set -e

# JavaFX path
JAVAFX_PATH="lib/javafx_mac_binary/javafx-sdk-21.0.11/lib"

mkdir -p bin

# Compile
javac \
    --module-path "$JAVAFX_PATH" \
    --add-modules javafx.controls,javafx.graphics \
    -d bin \
    src/com/physicssim/app/*.java \
    src/com/physicssim/components/*.java \
    src/com/physicssim/model/*.java \
    src/com/physicssim/theme/*.java \
    src/com/physicssim/views/*.java \
    src/com/physicssim/navigation/*.java \
    src/com/physicssim/features/pendulum/*.java \
    src/com/physicssim/features/simulations/*.java

# Run
java \
    --module-path "$JAVAFX_PATH" \
    --add-modules javafx.controls,javafx.graphics \
    -cp bin \
    com.physicssim.app.PhysicsSimulatorApp