package org.firstinspires.ftc.teamcode.missions;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

/*
 * MISSION 14 - Arcade Slalom                             (try it after Lesson 06)
 * Goal: drive under the first wall, over the second, and stop at FINISH. Beat your time!
 *
 * Arcade drive: one stick goes forward and back, the other turns.
 *   W / S        = left stick up / down   (forward and back)
 *   Left / Right = right stick left / right  (turn)
 *
 * The driving math has TWO bugs. Press W: which way does the robot go? Press Right: which way
 * does it turn?
 */

@TeleOp(name = "M14 Arcade Slalom", group = "Missions")
public class M14_ArcadeSlalom extends LinearOpMode {

    @Override
    public void runOpMode() {
        DcMotor leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        DcMotor rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        leftDrive.setDirection(DcMotor.Direction.REVERSE);

        waitForStart();

        while (opModeIsActive()) {
            double drive = gamepad1.left_stick_y;     // forward = pushing the stick up
            double turn = gamepad1.right_stick_x;     // right = pushing the stick right

            double leftPower = drive - turn;
            double rightPower = drive + turn;

            leftDrive.setPower(Range.clip(leftPower, -1, 1) * 0.6);
            rightDrive.setPower(Range.clip(rightPower, -1, 1) * 0.6);

            telemetry.addData("Drive", "%.2f", drive);
            telemetry.addData("Turn", "%.2f", turn);
            telemetry.update();
        }
    }
}
