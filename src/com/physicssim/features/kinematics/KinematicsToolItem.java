package com.physicssim.features.kinematics;

public class KinematicsToolItem {

    private final String number;
    private final String title;
    private final String description;
    private final KinematicsToolType type;

    public KinematicsToolItem(String number, String title, String description, KinematicsToolType type) {
        this.number = number;
        this.title = title;
        this.description = description;
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public KinematicsToolType getType() {
        return type;
    }
}
