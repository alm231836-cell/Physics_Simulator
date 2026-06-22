package com.physicssim.model;

public class SimulationItem {

    private final String number;
    private final String title;
    private final SimulationType type;

    public SimulationItem(String number, String title, SimulationType type) {
        this.number = number;
        this.title = title;
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public String getTitle() {
        return title;
    }

    public SimulationType getType() {
        return type;
    }
}
