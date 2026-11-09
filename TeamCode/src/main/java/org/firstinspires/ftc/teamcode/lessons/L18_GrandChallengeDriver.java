package org.firstinspires.ftc.teamcode.lessons;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/*
 * LESSON 18 - The Grand Challenge, part 2: DRIVER CONTROL.
 *
 * Run this right after your autonomous (L18_GrandChallenge), without resetting the robot.
 * The driver has to:
 *   - grab the cube by its metal handle,
 *   - push the ball up the ramp into the ROUND hole,
 *   - drop the cube in the SQUARE hole.
 * The clock stops when both are in.
 *
 * It uses the SAME MyRobot class as your autonomous. For driving, your robot class needs skills
 * that DON'T wait (no sleep): set the drive power, set the arm power, open or close the claw.
 * Lesson 15 (RobotHardware.java) did exactly this.
 */
@TeleOp(name = "L18 Grand Challenge Driver", group = "Lessons")
@Disabled  // Hides this ORIGINAL from the menu. Delete this line in YOUR copy so your copy shows up.
public class L18_GrandChallengeDriver extends LinearOpMode {

    @Override
    public void runOpMode() {
        MyRobot robot = new MyRobot(this);
        robot.init();

        telemetry.addData("Status", "Ready to drive");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            // Write each part as a comment first, then turn it into code.
            // 1. Drive with the sticks (Lesson 05 or 06). A slow mode helps on the ramp.
            // 2. Raise and lower the arm with buttons (Lesson 07), never past the top (Lesson 08).
            // 3. Open and close the claw with ONE button (Lesson 13).
            // 4. Show what the driver needs to know on telemetry.

            telemetry.update();
        }
    }
}
