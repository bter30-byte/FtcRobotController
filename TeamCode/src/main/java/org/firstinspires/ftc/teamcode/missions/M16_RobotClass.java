package org.firstinspires.ftc.teamcode.missions;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/*
 * MISSION 16 - Robot Class                         (try it after Lessons 15 and 16)
 * Goal: drive to the CORNER, turn left, and park in the box.
 *
 * The program is short because the Robot class (at the bottom) does the work: it holds the
 * motors and knows how to drive and turn. runOpMode just tells it what to do.
 *
 * This program has TWO bugs, both inside the Robot class. Read the error message for the
 * first one: which variable is empty (null)? Why, if init() set it?
 */

@Autonomous(name = "M16 Robot Class", group = "Missions")
public class M16_RobotClass extends LinearOpMode {

    @Override
    public void runOpMode() {
        Robot robot = new Robot(this);
        robot.init();

        waitForStart();

        robot.driveInches(36);
        robot.turnLeft();
        robot.driveInches(24);
    }

    /** The robot's parts and skills, all in one place (Lessons 15 and 16). */
    static class Robot {
        static final double TICKS_PER_INCH = 537.7 / (3.78 * Math.PI);
        static final double INCHES_PER_QUARTER_TURN = 11.8;  // each wheel's travel for a 90-degree turn

        private final LinearOpMode opMode;
        private DcMotor leftDrive;
        private DcMotor rightDrive;

        Robot(LinearOpMode opMode) {
            this.opMode = opMode;
        }

        /** Finds the motors and sets them up. */
        void init() {
            DcMotor leftDrive = opMode.hardwareMap.get(DcMotor.class, "left_drive");
            DcMotor rightDrive = opMode.hardwareMap.get(DcMotor.class, "right_drive");
            leftDrive.setDirection(DcMotor.Direction.REVERSE);
            leftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            rightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }

        void driveInches(double inches) {
            move(inches, inches);
        }

        /** Turns 90 degrees to the left, in place. */
        void turnLeft() {
            move(INCHES_PER_QUARTER_TURN, -INCHES_PER_QUARTER_TURN);
        }

        /** Moves each side of the robot this many inches (Lesson 12). */
        private void move(double leftInches, double rightInches) {
            leftDrive.setTargetPosition(leftDrive.getCurrentPosition() + (int) (leftInches * TICKS_PER_INCH));
            rightDrive.setTargetPosition(rightDrive.getCurrentPosition() + (int) (rightInches * TICKS_PER_INCH));
            leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            leftDrive.setPower(0.5);
            rightDrive.setPower(0.5);
            while (opMode.opModeIsActive() && (leftDrive.isBusy() || rightDrive.isBusy())) {
                opMode.idle();
            }
            leftDrive.setPower(0);
            rightDrive.setPower(0);
        }
    }
}
