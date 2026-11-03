package org.firstinspires.ftc.teamcode.missions;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/*
 * MISSION 4 - Scrambled
 * Goal: drive through the gate, then turn and STOP inside the blue box.
 *
 * Every step this program needs is already here, and every number is right.
 * But the steps are in the WRONG ORDER! Put them in the right order.
 *
 * Shortcut: click a line, then press Ctrl+Shift+Up or Ctrl+Shift+Down to move it.
 * (Or cut with Ctrl+X and paste with Ctrl+V.)
 */

@Autonomous(name = "M04 Scrambled", group = "Missions")
public class M04_Scrambled extends LinearOpMode {

    @Override
    public void runOpMode() {
        DcMotor leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        DcMotor rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        leftDrive.setDirection(DcMotor.Direction.REVERSE);

        // stop
        leftDrive.setPower(0);
        rightDrive.setPower(0);

        // drive to the box
        leftDrive.setPower(0.5);
        rightDrive.setPower(0.5);
        sleep(2350);

        waitForStart();

        // turn right
        leftDrive.setPower(0.5);
        rightDrive.setPower(-0.5);
        sleep(600);

        // drive through the gate
        leftDrive.setPower(0.5);
        rightDrive.setPower(0.5);
        sleep(4200);
    }
}
