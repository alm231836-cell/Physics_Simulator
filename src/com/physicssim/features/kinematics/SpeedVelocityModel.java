package com.physicssim.features.kinematics;

import java.util.List;

public final class SpeedVelocityModel {

    private SpeedVelocityModel() {
    }

    /**
     * Average speed = total path length / time.
     */
    public static double calculateAverageSpeedMetersPerSecond(
            List<Double> pathX,
            List<Double> pathY,
            double endX,
            double endY,
            boolean includeLiveEnd,
            double timeSeconds) {

        if (timeSeconds <= 0) {
            return 0;
        }

        double distanceMeters = DistanceDisplacementModel.calculateDistanceMeters(
                pathX, pathY, endX, endY, includeLiveEnd);
        return distanceMeters / timeSeconds;
    }

    /**
     * Average velocity magnitude = displacement / time.
     */
    public static double calculateAverageVelocityMetersPerSecond(
            double startX,
            double startY,
            double endX,
            double endY,
            double timeSeconds) {

        if (timeSeconds <= 0 || startX < 0 || endX < 0) {
            return 0;
        }

        double displacementMeters = DistanceDisplacementModel.calculateDisplacementMeters(
                startX, startY, endX, endY);
        return displacementMeters / timeSeconds;
    }

    public static double velocityDirectionRadians(
            double startX, double startY, double endX, double endY) {
        return Math.atan2(endY - startY, endX - startX);
    }
}
