package org.firstinspires.ftc.teamcode.missions;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

/*
 * MISSION 13 - Special Delivery                          (try it after Lesson 13)
 * Goal: grab the yellow block with the claw, carry it, and leave it in the DROP box.
 *
 * This program has TWO bugs. Watch the claw in the side view and on the field:
 * does it open and close when the comments say it should?
 */

@Autonomous(name = "M13 Special Delivery", group = "Missions")
public class M13_SpecialDelivery extends LinearOpMode {

    static final double CLAW_OPEN = 0.3;
    static final double CLAW_CLOSED = 0.7;

    @Override
    public void runOpMode() {
        DcMotor leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        DcMotor rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        DcMotor arm = hardwareMap.get(DcMotor.class, "arm");
        Servo claw = hardwareMap.get(Servo.class, "claw");
        leftDrive.setDirection(DcMotor.Direction.REVERSE);
        leftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        arm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        waitForStart();

        // Grab the block (it's right in front of the claw)
        claw.setPosition(CLAW_CLOSED);
        sleep(500);

        // Lift it a little, so it doesn't drag on the floor
        arm.setPower(0.4);
        sleep(300);
        arm.setPower(0);

        // Carry it to the DROP box
        leftDrive.setPower(0.5);
        rightDrive.setPower(0.5);
        sleep(2500);
        leftDrive.setPower(0);
        rightDrive.setPower(0);

        // Leave the block in the box and back away
        leftDrive.setPower(-0.3);
        rightDrive.setPower(-0.3);
        sleep(800);
        leftDrive.setPower(0);
        rightDrive.setPower(0);
    }
}
