package com.qualcomm.robotcore.hardware;

/** Simulator copy of the FTC SDK servo interface. */
public interface Servo extends HardwareDevice {

    double MIN_POSITION = 0.0;
    double MAX_POSITION = 1.0;

    enum Direction {
        FORWARD, REVERSE
    }

    int getPortNumber();

    void setDirection(Direction direction);

    Direction getDirection();

    /** Position from 0.0 to 1.0. */
    void setPosition(double position);

    double getPosition();

    void scaleRange(double min, double max);
}
