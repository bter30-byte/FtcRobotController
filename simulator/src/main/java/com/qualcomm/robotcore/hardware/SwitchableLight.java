package com.qualcomm.robotcore.hardware;

/** Simulator copy of the FTC SDK interface for devices whose light can be turned on and off. */
public interface SwitchableLight extends Light {
    void enableLight(boolean enable);
}
