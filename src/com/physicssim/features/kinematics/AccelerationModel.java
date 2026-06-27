package com.physicssim.features.kinematics;

/**
 * Headless kinematics for constant-acceleration motion along a line.
 * Uses v = u + at and s = ut + ½at².
 */
public final class AccelerationModel {

    public static final double MIN_VELOCITY = 0.0;

    private AccelerationModel() {
    }

    public static double velocityAtTime(double initialSpeed, double acceleration, double time) {
        return initialSpeed + acceleration * time;
    }

    public static double displacementAtTime(double initialSpeed, double acceleration, double time) {
        return initialSpeed * time + 0.5 * acceleration * time * time;
    }

    /**
     * Time when velocity first reaches zero for motion with u &gt; 0 and a &lt; 0.
     * Returns {@link Double#POSITIVE_INFINITY} if velocity never reaches zero.
     */
    public static double timeWhenVelocityReachesZero(double initialSpeed, double acceleration) {
        if (acceleration >= 0 || initialSpeed <= 0) {
            return Double.POSITIVE_INFINITY;
        }
        return -initialSpeed / acceleration;
    }

    public static SimulationStep advance(
            double initialSpeed,
            double acceleration,
            double elapsedTime,
            double positionMeters,
            boolean stopped,
            double deltaTime,
            double maxDistanceMeters) {

        if (stopped || deltaTime <= 0) {
            return new SimulationStep(
                    elapsedTime,
                    positionMeters,
                    Math.max(MIN_VELOCITY, velocityAtTime(initialSpeed, acceleration, elapsedTime)),
                    true);
        }

        double nextTime = elapsedTime + deltaTime;
        double nextVelocity = velocityAtTime(initialSpeed, acceleration, nextTime);

        if (nextVelocity <= MIN_VELOCITY) {
            double stopTime = timeWhenVelocityReachesZero(initialSpeed, acceleration);
            if (Double.isFinite(stopTime) && stopTime >= elapsedTime) {
                return new SimulationStep(
                        stopTime,
                        clampDistance(displacementAtTime(initialSpeed, acceleration, stopTime), maxDistanceMeters),
                        MIN_VELOCITY,
                        true);
            }

            if (initialSpeed <= MIN_VELOCITY && acceleration <= 0) {
                return new SimulationStep(elapsedTime, positionMeters, MIN_VELOCITY, true);
            }
        }

        double nextPosition = displacementAtTime(initialSpeed, acceleration, nextTime);
        if (nextPosition >= maxDistanceMeters) {
            return new SimulationStep(
                    nextTime,
                    maxDistanceMeters,
                    Math.max(MIN_VELOCITY, nextVelocity),
                    true);
        }

        if (nextPosition < MIN_VELOCITY) {
            return new SimulationStep(elapsedTime, MIN_VELOCITY, MIN_VELOCITY, true);
        }

        return new SimulationStep(nextTime, nextPosition, nextVelocity, false);
    }

    private static double clampDistance(double distance, double maxDistanceMeters) {
        return Math.max(MIN_VELOCITY, Math.min(distance, maxDistanceMeters));
    }

    public record SimulationStep(
            double elapsedTime,
            double positionMeters,
            double velocity,
            boolean stopped) {
    }
}
