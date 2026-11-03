package org.firstinspires.ftc.teamcode.missions;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/*
 * MISSION 2 - Turn and Park
 * Goal: drive forward, turn, drive forward again, and STOP inside the blue box.
 *
 * This program turns the WRONG WAY, and its times are wrong too.
 *   - To spin, one side goes forward and the other goes backward.
 *   - Which side goes backward decides which way the robot turns.
 *
 * Tip: change one thing at a time, then run it again.
 */

@Autonomous(name = "M02 Turn and Park", group = "Missions")
public class M02_TurnAndPark extends LinearOpMode {

    @Override
    public void runOpMode() {
        DcMotor leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        DcMotor rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        leftDrive.setDirection(DcMotor.Direction.REVERSE);

        waitForStart();

        // Step 1: drive forward
        leftDrive.setPower(0.5);
        rightDrive.setPower(0.5);
        sleep(1500);

        // Step 2: turn right
        leftDrive.setPower(0.5);
        rightDrive.setPower(-0.5);
        sleep(500);

        // Step 3: drive forward again
        leftDrive.setPower(0.5);
        rightDrive.setPower(0.5);
        sleep(1500);

        // Step 4: stop
        leftDrive.setPower(0);
        rightDrive.setPower(0);
    }
}
