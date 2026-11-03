package org.firstinspires.ftc.teamcode.missions;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

/*
 * MISSION 6 - Drive It Yourself
 * Goal: drive through gate 1, then gate 2, then stop at FINISH. Beat your best time!
 *
 * This is a TeleOp program: YOU drive. In the simulator, the keyboard is the gamepad:
 *   W / S         = left stick up / down   (left wheels)
 *   Up / Down     = right stick up / down  (right wheels)
 *
 * Try these changes, one at a time:
 *   1. Change maxSpeed. What's the best speed for you?
 *   2. Add a telemetry line that shows maxSpeed on the screen.
 *   3. Challenge: make the robot drive with ONLY the W A S D keys.
 *      (Hint: use gamepad1.left_stick_x to turn.)
 */

@TeleOp(name = "M06 Drive It Yourself", group = "Missions")
public class M06_DriveItYourself extends LinearOpMode {

    @Override
    public void runOpMode() {
        DcMotor leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        DcMotor rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        leftDrive.setDirection(DcMotor.Direction.REVERSE);

        double maxSpeed = 0.5;

        waitForStart();

        // Everything inside this loop runs over and over, many times a second, until STOP.
        while (opModeIsActive()) {
            // Pushing a stick UP gives a NEGATIVE number, so we flip it with a minus sign.
            double leftPower = -gamepad1.left_stick_y * maxSpeed;
            double rightPower = -gamepad1.right_stick_y * maxSpeed;

            leftDrive.setPower(leftPower);
            rightDrive.setPower(rightPower);

            telemetry.addData("Left power", leftPower);
            telemetry.addData("Right power", rightPower);
            telemetry.update();
        }
    }
}
