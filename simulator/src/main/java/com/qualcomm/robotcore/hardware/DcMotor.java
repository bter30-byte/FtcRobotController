package com.qualcomm.robotcore.hardware;

/** Simulator copy of the FTC SDK motor interface (the parts team code uses). */
public interface DcMotor extends DcMotorSimple {

    enum ZeroPowerBehavior {
        UNKNOWN, BRAKE, FLOAT
    }

    enum RunMode {
        RUN_WITHOUT_ENCODER,
        RUN_USING_ENCODER,
        RUN_TO_POSITION,
        STOP_AND_RESET_ENCODER;

        public boolean isPIDMode() {
            return this == RUN_USING_ENCODER || this == RUN_TO_POSITION;
        }
    }

    int getPortNumber();

    void setZeroPowerBehavior(ZeroPowerBehavior zeroPowerBehavior);

    ZeroPowerBehavior getZeroPowerBehavior();

    void setTargetPosition(int position);

    int getTargetPosition();

    boolean isBusy();

    int getCurrentPosition();

    void setMode(RunMode mode);

    RunMode getMode();
}
