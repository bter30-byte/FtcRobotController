package com.qualcomm.robotcore.hardware;

/** Simulator copy of the FTC SDK touch sensor interface. */
public interface TouchSensor extends HardwareDevice {
    /** 1.0 when pressed, 0.0 when not. */
    double getValue();

    boolean isPressed();
}
