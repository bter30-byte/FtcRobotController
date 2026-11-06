package org.firstinspires.ftc.teamcode.missions;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/*
 * MISSION 12 - Precision Park                            (try it after Lesson 12)
 * Goal: drive EXACTLY 60 inches and park in the narrow box.
 *
 * A timer can't do this every time (try Low battery). Encoders can: they count how far the
 * wheels really turned. This program has TWO bugs. Read the error message for the first one.
 *
 * The motor: 537.7 encoder ticks per turn. The wheel: 3.78 inches across.
 * One turn of the wheel rolls the robot one circumference: pi x diameter.
 */

@Autonomous(name = "M12 Precision Park", group = "Missions")
public class M12_PrecisionPark extends LinearOpMode {

    static final double TICKS_PER_MOTOR_TURN = 537.7;
    static final double WHEEL_DIAMETER_INCHES = 3.78;
    static final double TICKS_PER_INCH = TICKS_PER_MOTOR_TURN / WHEEL_DIAMETER_INCHES;

    @Override
    public void runOpMode() {
        DcMotor leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        DcMotor rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        leftDrive.setDirection(DcMotor.Direction.REVERSE);
        leftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        waitForStart();

        int target = (int) (60 * TICKS_PER_INCH);
        telemetry.addData("Target", "%d ticks", target);
        telemetry.update();

        leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftDrive.setTargetPosition(target);
        rightDrive.setTargetPosition(target);
        leftDrive.setPower(0.5);
        rightDrive.setPower(0.5);

        while (opModeIsActive() && (leftDrive.isBusy() || rightDrive.isBusy())) {
            telemetry.addData("Left", "%d / %d", leftDrive.getCurrentPosition(), target);
            telemetry.addData("Right", "%d / %d", rightDrive.getCurrentPosition(), target);
            telemetry.update();
        }

        leftDrive.setPower(0);
        rightDrive.setPower(0);
        sleep(500);
    }
}
