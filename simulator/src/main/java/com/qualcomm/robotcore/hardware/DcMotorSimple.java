package com.qualcomm.robotcore.hardware;

/** Simulator copy of the FTC SDK interface. */
public interface DcMotorSimple extends HardwareDevice {

    enum Direction {
        FORWARD, REVERSE;

        public Direction inverted() {
            return this == FORWARD ? REVERSE : FORWARD;
        }
    }

    void setDirection(Direction direction);

    Direction getDirection();

    /** Power from -1.0 to 1.0. Anything outside that range is clipped. */
    void setPower(double power);

    double getPower();
}
