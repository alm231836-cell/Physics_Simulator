package com.physicssim.features.pendulum;

public class PendulumModel {

    private static final double PIXELS_PER_METER = 110.0;
    private static final double MAX_STABLE_STEP_SECONDS = 1.0 / 240.0;

    private double lengthMeters;
    private double gravity;
    private double bobMass;
    private double angle;
    private double angularVelocity;

    public PendulumModel(double lengthMeters, double gravity, double bobMass, double initialAngleRadians) {
        this.lengthMeters = lengthMeters;
        this.gravity = gravity;
        this.bobMass = bobMass;
        this.angle = initialAngleRadians;
    }

    public void update(double deltaSeconds) {
        if (deltaSeconds <= 0 || lengthMeters <= 0 || gravity <= 0) {
            return;
        }

        int steps = Math.max(1, (int) Math.ceil(deltaSeconds / MAX_STABLE_STEP_SECONDS));
        double stepSeconds = deltaSeconds / steps;

        for (int i = 0; i < steps; i++) {
            step(stepSeconds);
        }
    }

    private void step(double deltaSeconds) {
        double angularAcceleration = -(gravity / lengthMeters) * Math.sin(angle);

        // Semi-implicit Euler is much more stable for oscillators than
        // updating position from the previous-frame velocity.
        angularVelocity += angularAcceleration * deltaSeconds;
        angle += angularVelocity * deltaSeconds;
    }

    public void reset(double angleRadians) {
        angle = angleRadians;
        angularVelocity = 0;
    }

    public double getLengthMeters() {
        return lengthMeters;
    }

    public void setLengthMeters(double lengthMeters) {
        this.lengthMeters = lengthMeters;
    }

    public double getDisplayLength() {
        return lengthMeters * PIXELS_PER_METER;
    }

    public double getBobMass() {
        return bobMass;
    }

    public void setBobMass(double bobMass) {
        this.bobMass = bobMass;
    }

    public double getBobRadius() {
        return 14 + bobMass * 4.5;
    }

    public double getAngle() {
        return angle;
    }

    public double getAngularVelocity() {
        return angularVelocity;
    }

    public double getGravity() {
        return gravity;
    }

    public void setGravity(double gravity) {
        this.gravity = gravity;
    }

    public double getPeriod() {
        return 2 * Math.PI * Math.sqrt(lengthMeters / gravity);
    }

    public double getHorizontalPosition() {
        return lengthMeters * Math.sin(angle);
    }

    public double getVerticalPosition() {
        return lengthMeters * Math.cos(angle);
    }

    public double getLinearSpeed() {
        return Math.abs(angularVelocity * lengthMeters);
    }
}
