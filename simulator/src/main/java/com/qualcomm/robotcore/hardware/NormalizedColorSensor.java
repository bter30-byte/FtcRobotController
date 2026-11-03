package com.qualcomm.robotcore.hardware;

/** Simulator copy of the FTC SDK interface: color readings scaled to 0.0 - 1.0. */
public interface NormalizedColorSensor extends HardwareDevice {
    NormalizedRGBA getNormalizedColors();

    float getGain();

    /** Bigger gain = brighter readings. FIRST's line-following sample uses 15. */
    void setGain(float newGain);
}
