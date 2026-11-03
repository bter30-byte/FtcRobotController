package org.firstinspires.ftc.teamcode.missions;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/*
 * MISSION 3 - Square Dance
 * Goal: drive a square. Visit corner 1, then 2, then 3, then come back HOME.
 *
 * This program only drives the FIRST side and the first turn.
 * Add the other three sides. (Copy and paste is your friend!)
 *
 * Turn on "Show trail" in the simulator to see the shape you drive.
 */

@Autonomous(name = "M03 Square Dance", group = "Missions")
public class M03_SquareDance extends LinearOpMode {

    @Override
    public void runOpMode() {
        DcMotor leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        DcMotor rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        leftDrive.setDirection(DcMotor.Direction.REVERSE);

        waitForStart();

        // Side 1: drive forward
        leftDrive.setPower(0.5);
        rightDrive.setPower(0.5);
        sleep(2400);

        // Turn left
        leftDrive.setPower(-0.5);
        rightDrive.setPower(0.5);
        sleep(600);

        // Side 2: ???

        // Side 3: ???

        // Side 4: ???

        // Stop
        leftDrive.setPower(0);
        rightDrive.setPower(0);
    }
}
