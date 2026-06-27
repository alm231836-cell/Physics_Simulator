package com.physicssim.features.kinematics;

import java.util.List;

/**
 * Headless physics model for distance and displacement along a 2D path.
 * 40 canvas pixels = 10 meters.
 */
public final class DistanceDisplacementModel {

    public static final double PIXELS_PER_METER = 4.0;

    private DistanceDisplacementModel() {
    }

    public static double pixelsToMeters(double pixels) {
        return pixels / PIXELS_PER_METER;
    }

    public static double segmentLengthPixels(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Total path length in meters. When {@code includeLiveEnd} is true, adds the segment
     * from the last recorded path point to the live end cursor (used while drawing).
     */
    public static double calculateDistanceMeters(
            List<Double> pathX,
            List<Double> pathY,
            double endX,
            double endY,
            boolean includeLiveEnd) {

        if (pathX.isEmpty()) {
            return 0;
        }

        double totalPixels = 0;
        for (int i = 1; i < pathX.size(); i++) {
            totalPixels += segmentLengthPixels(
                    pathX.get(i - 1), pathY.get(i - 1),
                    pathX.get(i), pathY.get(i));
        }

        if (includeLiveEnd && pathX.size() >= 1) {
            double lastX = pathX.get(pathX.size() - 1);
            double lastY = pathY.get(pathY.size() - 1);
            totalPixels += segmentLengthPixels(lastX, lastY, endX, endY);
        }

        return pixelsToMeters(totalPixels);
    }

    /**
     * Straight-line displacement magnitude from start to end, in meters.
     */
    public static double calculateDisplacementMeters(
            double startX, double startY, double endX, double endY) {

        if (startX < 0 || endX < 0) {
            return 0;
        }

        return pixelsToMeters(segmentLengthPixels(startX, startY, endX, endY));
    }
}
