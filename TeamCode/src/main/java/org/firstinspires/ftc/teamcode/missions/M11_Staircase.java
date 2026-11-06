package org.firstinspires.ftc.teamcode.missions;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/*
 * MISSION 11 - Staircase                          (try it after Lessons 09 and 11)
 * Goal: climb the stairs to the TOP. That's 6 steps:
 *   drive 1 tile, turn LEFT, drive 1 tile, turn RIGHT, drive 1 tile, turn LEFT...
 *
 * The methods at the bottom (driveTile, turnLeft, turnRight) already work.
 * The loop in runOpMode has TWO bugs. Turn on "Show trail" to see the path.
 */

@Autonomous(name = "M11 Staircase", group = "Missions")
public class M11_Staircase extends LinearOpMode {

    static final double TICKS_PER_INCH = 537.7 / (3.78 * Math.PI);
    static final double INCHES_PER_QUARTER_TURN = 11.8;  // each wheel's travel for a 90-degree turn

    private DcMotor leftDrive;
    private DcMotor rightDrive;

    @Override
    public void runOpMode() {
        leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        leftDrive.setDirection(DcMotor.Direction.REVERSE);
        leftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        waitForStart();

        // Climb all 6 steps
        for (int step = 1; step < 6; step++) {
            telemetry.addData("Step", step);
            telemetry.update();
            driveTile();
            turnLeft();
        }
    }

    /** Drives forward one tile (24 inches) using the encoders. */
    void driveTile() {
        move(24, 24);
    }

    void turnLeft() {
        move(-INCHES_PER_QUARTER_TURN, INCHES_PER_QUARTER_TURN);
    }

    void turnRight() {
        move(INCHES_PER_QUARTER_TURN, -INCHES_PER_QUARTER_TURN);
    }

    /** Moves each side of the robot this many inches (Lesson 12). */
    void move(double leftInches, double rightInches) {
        leftDrive.setTargetPosition(leftDrive.getCurrentPosition() + (int) (leftInches * TICKS_PER_INCH));
        rightDrive.setTargetPosition(rightDrive.getCurrentPosition() + (int) (rightInches * TICKS_PER_INCH));
        leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftDrive.setPower(0.5);
        rightDrive.setPower(0.5);
        while (opModeIsActive() && (leftDrive.isBusy() || rightDrive.isBusy())) {
            idle();
        }
        leftDrive.setPower(0);
        rightDrive.setPower(0);
    }
}
