package robotsim;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

/**
 * The simulated state of one motor: what the program asked for (power, direction, mode,
 * target) and what the motor is really doing (speed, encoder count).
 * All access happens while holding the World lock.
 */
final class MotorState {

    final String name;
    /** +1 or -1: which way the motor is mounted. The left drive motor is mounted mirrored. */
    final int mount;
    /** Encoder ticks per inch (drive) or per degree (arm). */
    final double ticksPerUnit;
    /** RUN_TO_POSITION: power per tick of error, and how close counts as "there". */
    final double kp;
    final int tolerance;

    double power = 0;
    DcMotorSimple.Direction direction = DcMotorSimple.Direction.FORWARD;
    DcMotor.RunMode mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER;
    DcMotor.ZeroPowerBehavior zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT;
    int target = 0;
    boolean targetSet = false;

    /** Raw encoder count, positive when the motor gets positive voltage. */
    double rawTicks = 0;
    /** Actual speed: inches/second for a wheel (forward is +), degrees/second for the arm. */
    double speed = 0;

    MotorState(String name, int mount, double ticksPerUnit, double kp, int tolerance) {
        this.name = name;
        this.mount = mount;
        this.ticksPerUnit = ticksPerUnit;
        this.kp = kp;
        this.tolerance = tolerance;
    }

    double directionSign() {
        return direction == DcMotorSimple.Direction.REVERSE ? -1 : 1;
    }

    /** Encoder count the program sees. Reversing a motor also reverses its encoder, like the real SDK. */
    int reportedPosition() {
        return (int) Math.round(rawTicks * directionSign());
    }

    /** The voltage actually sent to the motor, from -1 to 1. */
    double voltage() {
        switch (mode) {
            case STOP_AND_RESET_ENCODER:
                return 0;
            case RUN_TO_POSITION: {
                double limit = Math.min(1.0, Math.abs(power));
                double error = target - reportedPosition();
                double u = clip(error * kp, -limit, limit);
                return u * directionSign();
            }
            default:
                return clip(power, -1, 1) * directionSign();
        }
    }

    boolean isBusy() {
        return mode == DcMotor.RunMode.RUN_TO_POSITION
                && Math.abs(power) > 0.001
                && Math.abs(target - reportedPosition()) > tolerance;
    }

    void setMode(DcMotor.RunMode newMode) {
        if (newMode == DcMotor.RunMode.STOP_AND_RESET_ENCODER) {
            rawTicks = 0;
            target = 0;
            targetSet = false;
        } else if (newMode == DcMotor.RunMode.RUN_TO_POSITION && !targetSet) {
            throw new IllegalStateException("Failed to enable motor \"" + name + "\". You must set a "
                    + "target position before switching to RUN_TO_POSITION mode");
        }
        mode = newMode;
    }

    void resetAll() {
        power = 0;
        direction = DcMotorSimple.Direction.FORWARD;
        mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER;
        zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT;
        target = 0;
        targetSet = false;
        rawTicks = 0;
        speed = 0;
    }

    static double clip(double v, double lo, double hi) {
        return v < lo ? lo : (v > hi ? hi : v);
    }
}
