package org.firstinspires.ftc.teamcode.missions;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

/*
 * MISSION 10 - Bumper Bot                                (try it after Lesson 10)
 * Goal: drive until the bumper (the touch sensor) touches the wall,
 *       then back up and stop in the box.
 *
 * This program has TWO bugs. Read each comment, then check: does the code really do that?
 */

@Autonomous(name = "M10 Bumper Bot", group = "Missions")
public class M10_BumperBot extends LinearOpMode {

    @Override
    public void runOpMode() {
        DcMotor leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        DcMotor rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        leftDrive.setDirection(DcMotor.Direction.REVERSE);
        leftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        TouchSensor bumper = hardwareMap.get(TouchSensor.class, "touch_sensor");

        waitForStart();
        ElapsedTime timer = new ElapsedTime();

        // Drive forward until the bumper is pressed (give up after 6 seconds, just in case)
        leftDrive.setPower(0.4);
        rightDrive.setPower(0.4);
        while (opModeIsActive() && bumper.isPressed() && timer.seconds() < 6) {
            telemetry.addData("Looking for the wall", "%.1f seconds", timer.seconds());
            telemetry.update();
        }

        // Back up for 1.5 seconds
        leftDrive.setPower(0.4);
        rightDrive.setPower(0.4);
        sleep(1500);

        // Stop
        leftDrive.setPower(0);
        rightDrive.setPower(0);
    }
}
