package com.physicssim.model.electricity;

/**
 * Simple model for a series circuit containing a voltage source and N resistors.
 * All physics calculations live here (total resistance, current, per-resistor drop).
 */
public class SeriesCircuitSimulation {

    private double voltage;
    private double[] resistances;
    private boolean closed = true;

    public SeriesCircuitSimulation(double voltage, double... resistances) {
        this.voltage = voltage;
        this.resistances = resistances.clone();
    }

    public SeriesCircuitSimulation() {
        this(5.0, 10.0, 10.0, 10.0);
    }

    public double getVoltage() {
        return voltage;
    }

    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }

    public int getResistorCount() {
        return resistances.length;
    }

    public double getResistance(int index) {
        return resistances[index];
    }

    public void setResistance(int index, double value) {
        if (value < 0) value = 0;
        resistances[index] = value;
    }

    public double getTotalResistance() {
        double sum = 0;
        for (double r : resistances) sum += r;
        return sum;
    }

    public double getCurrent() {
        if (!closed) return 0.0;
        double r = getTotalResistance();
        if (r <= 0) return Double.POSITIVE_INFINITY;
        return voltage / r;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public double getVoltageDrop(int index) {
        double i = getCurrent();
        if (Double.isInfinite(i)) return Double.POSITIVE_INFINITY;
        return i * resistances[index];
    }
}
