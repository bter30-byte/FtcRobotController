package org.firstinspires.ftc.teamcode.missions;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

/*
 * MISSION 9 - Pick and Place                      (try it after Lessons 07, 08, and 13)
 * Goal: drive to the yellow block, grab it, lift it, and leave it in the DROP box.
 *
 * Controls:
 *   W / S and Left / Right   drive and turn (arcade)
 *   Hold Q (left bumper)     slow, careful driving
 *   Hold I (Y) / J (A)       arm up / down. The touch sensor at the top stops the arm.
 *   Tap K (B)                open or close the claw
 *
 * This program has TWO bugs. One stops the arm from ever going up. The other makes the claw
 * act strangely: watch it while you hold K down.
 */

@TeleOp(name = "M09 Pick and Place", group = "Missions")
public class M09_PickAndPlace extends LinearOpMode {

    static final double CLAW_OPEN = 0.7;
    static final double CLAW_CLOSED = 0.3;

    @Override
    public void runOpMode() {
        DcMotor leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        DcMotor rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        DcMotor arm = hardwareMap.get(DcMotor.class, "arm");
        Servo claw = hardwareMap.get(Servo.class, "claw");
        TouchSensor armTop = hardwareMap.get(TouchSensor.class, "touch_sensor");
        leftDrive.setDirection(DcMotor.Direction.REVERSE);
        arm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        boolean clawOpen = true;
        boolean bWasPressed = false;
        claw.setPosition(CLAW_OPEN);

        waitForStart();

        while (opModeIsActive()) {
            // ---- driving (arcade), with a slow mode while the left bumper is held ----
            double drive = -gamepad1.left_stick_y;
            double turn = gamepad1.right_stick_x;
            double speed;
            if (gamepad1.left_bumper) {
                speed = 0.3;
            } else {
                speed = 0.7;
            }
            leftDrive.setPower((drive + turn) * speed);
            rightDrive.setPower((drive - turn) * speed);

            // ---- arm: Y raises it, but never past the touch sensor at the top ----
            if (gamepad1.y && armTop.isPressed()) {
                arm.setPower(0.5);
            } else if (gamepad1.a) {
                arm.setPower(-0.4);
            } else {
                arm.setPower(0);
            }

            // ---- claw: each TAP of B switches between open and closed ----
            if (gamepad1.b) {
                clawOpen = !clawOpen;
            }
            claw.setPosition(clawOpen ? CLAW_OPEN : CLAW_CLOSED);

            telemetry.addData("Claw", clawOpen ? "open" : "closed");
            telemetry.addData("Arm at the top", armTop.isPressed());
            telemetry.update();
        }
    }
}
