package org.firstinspires.ftc.teamcode.missions;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/*
 * MISSION 1 - First Moves
 * Goal: drive straight ahead and STOP inside the blue box.
 *
 *   1. PREDICT: read the code below. Where do you think the robot will stop?
 *   2. RUN it: pick "M01 First Moves" in the simulator, press INIT, then START.
 *   3. FIX it: change ONE number so the robot stops inside the box.
 *
 * Lines that start with // are comments: notes for people. The robot ignores them.
 */

@Autonomous(name = "M01 First Moves", group = "Missions")
public class M01_FirstMoves extends LinearOpMode {

    @Override
    public void runOpMode() {
        // Find the two drive motors by their names.
        DcMotor leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        DcMotor rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        // The left motor faces the other way, so we flip it.
        leftDrive.setDirection(DcMotor.Direction.REVERSE);

        // Wait here until someone presses START.
        waitForStart();

        // Drive forward at half power...
        leftDrive.setPower(0.5);
        rightDrive.setPower(0.5);
        // ...and keep going for 1000 milliseconds (1 second).
        sleep(1000);

        // Stop.
        leftDrive.setPower(0);
        rightDrive.setPower(0);
    }
}
