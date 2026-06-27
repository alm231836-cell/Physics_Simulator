package com.physicssim.features.kinematics;

public record ProjectileModel(
        double time,
        double x,
        double y,
        double vx,
        double vy,
        double speed,
        double maxHeight,
        double totalRange,
        boolean stopped) {

    public static ProjectileModel advance(
            double initialVelocity,
            double launchAngleDegrees,
            double initialHeight,
            double gravity,
            double elapsedTime,
            double maxHorizontalRange,
            boolean stopped,
            double deltaTime) {

        if (stopped) {
            return new ProjectileModel(elapsedTime, 0, 0, 0, 0, 0, 0, 0, true);
        }

        double angleRad = Math.toRadians(launchAngleDegrees);
        double vx = initialVelocity * Math.cos(angleRad);
        double vy0 = initialVelocity * Math.sin(angleRad);

        double newTime = elapsedTime + deltaTime;
        double x = vx * newTime;
        double y = initialHeight + vy0 * newTime - 0.5 * gravity * newTime * newTime;

        // Calculate current vertical velocity
        double vy = vy0 - gravity * newTime;

        // Calculate speed
        double speed = Math.sqrt(vx * vx + vy * vy);

        // Calculate maximum height reached
        double timeToPeak = vy0 / gravity;
        double maxHeight = initialHeight + vy0 * timeToPeak - 0.5 * gravity * timeToPeak * timeToPeak;
        if (maxHeight < initialHeight) {
            maxHeight = initialHeight;
        }

        // Calculate total range (when projectile hits ground)
        // Solve: 0 = initialHeight + vy0*t - 0.5*g*t^2
        // Using quadratic formula: t = (-b ± sqrt(b^2 - 4ac)) / 2a
        double discriminant = vy0 * vy0 + 2 * gravity * initialHeight;
        double totalRange = 0;
        if (discriminant >= 0) {
            double tLanding = (vy0 + Math.sqrt(discriminant)) / gravity;
            totalRange = vx * tLanding;
        }

        // Check if projectile has landed or gone out of bounds
        boolean isStopped = y <= 0 || x > maxHorizontalRange;

        if (isStopped) {
            // Snap to ground if landed
            if (y <= 0) {
                y = 0;
                // Recalculate exact landing time
                double tLanding = (vy0 + Math.sqrt(discriminant)) / gravity;
                x = vx * tLanding;
                newTime = tLanding;
                vy = 0;
                speed = Math.abs(vx);
            }
        }

        return new ProjectileModel(newTime, x, y, vx, vy, speed, maxHeight, totalRange, isStopped);
    }
}