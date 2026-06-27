package com.physicssim.model.electricity;

/**
 * Simple Ohm's law simulation model. Holds voltage and resistance and computes current.
 */
public class OhmsLawSimulation {

    private double voltage; // volts
    private double resistance; // ohms
    private boolean closed = true; // circuit switch state

    public OhmsLawSimulation(double voltage, double resistance) {
        this.voltage = voltage;
        this.resistance = resistance;
    }

    public OhmsLawSimulation() {
        this(5.0, 10.0);
    }

    public double getVoltage() {
        return voltage;
    }

    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }

    public double getResistance() {
        return resistance;
    }

    public void setResistance(double resistance) {
        this.resistance = resistance;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    /**
     * Computes current using I = V / R. Returns Double.POSITIVE_INFINITY for zero resistance.
     */
    public double getCurrent() {
        if (!closed) {
            return 0.0;
        }

        if (resistance == 0) {
            return Double.POSITIVE_INFINITY;
        }

        return voltage / resistance;
    }
}
