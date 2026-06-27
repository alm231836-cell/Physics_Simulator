package com.physicssim.features.kinematics;

import java.util.List;

public final class KinematicsCatalog {

    private KinematicsCatalog() {
    }

    public static List<KinematicsToolItem> tools() {
        return List.of(
                new KinematicsToolItem("(1)", "Distance & Displacement", "Learn the difference between scalar and vector quantities in motion.", KinematicsToolType.DISTANCE_DISPLACEMENT),
                new KinematicsToolItem("(2)", "Speed & Velocity", "Draw a path and compare average speed (scalar) with average velocity (vector).", KinematicsToolType.SPEED_VELOCITY),
                new KinematicsToolItem("(3)", "Acceleration", "Understand how velocity changes over time.", KinematicsToolType.ACCELERATION),
                new KinematicsToolItem("(4)", "Free Fall", "Simulate vertical motion under constant gravity.", KinematicsToolType.FREE_FALL),
                new KinematicsToolItem("(5)", "Projectile Motion", "Analyze parabolic trajectories with horizontal and vertical components.", KinematicsToolType.PROJECTILE_MOTION));
    }
}
