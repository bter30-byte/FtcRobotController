package com.qualcomm.robotcore.hardware;

/** Simulator copy of the FTC SDK color sensor interface (raw readings). */
public interface ColorSensor extends HardwareDevice {
    int red();

    int green();

    int blue();

    /** Overall amount of light (the "clear" channel). */
    int alpha();

    int argb();

    void enableLed(boolean enable);
}
