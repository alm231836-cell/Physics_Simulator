package com.physicssim.model.electricity;

/**
 * Simple model for a parallel circuit containing a voltage source and N resistors.
 */
public class ParallelCircuitSimulation {

    private double voltage;
    private double[] resistances;

    public ParallelCircuitSimulation(double voltage, double... resistances) {
        this.voltage = voltage;
        this.resistances = resistances.clone();
    }

    public ParallelCircuitSimulation() {
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

    public double getEquivalentResistance() {
        double inverseSum = 0.0;
        for (double r : resistances) {
            if (r <= 0) {
                return 0.0;
            }
            inverseSum += 1.0 / r;
        }
        return inverseSum <= 0.0 ? Double.POSITIVE_INFINITY : 1.0 / inverseSum;
    }

    public double getTotalCurrent() {
        double req = getEquivalentResistance();
        if (req <= 0.0) return Double.POSITIVE_INFINITY;
        return voltage / req;
    }

    public double getCurrentThrough(int index) {
        double r = resistances[index];
        if (r <= 0.0) return Double.POSITIVE_INFINITY;
        return voltage / r;
    }

    public double getVoltageAcrossResistor(int index) {
        return voltage;
    }
}
