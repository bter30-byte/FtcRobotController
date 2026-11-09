package org.firstinspires.ftc.teamcode.lessons;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/*
 * LESSON 18 - The Grand Challenge.
 *
 * A whole match, in two programs:
 *   1. AUTONOMOUS (this program): drive around the outer lane, using the black and white tape.
 *      Forward, turn left, forward, turn left, forward, turn left. Don't cross the low wall!
 *      Stop in the corner, facing the ramp lane.
 *   2. DRIVER CONTROL (L18_GrandChallengeDriver): grab the cube, push the ball up the ramp into
 *      the round hole, and drop the cube in the square hole.
 *
 * The robot's devices and skills live in YOUR MyRobot class. This program is the plan:
 * it tells your robot what to do, in order.
 *
 * In the simulator, picking this program opens the Grand Challenge (last in the mission list).
 */
@Autonomous(name = "L18 Grand Challenge", group = "Lessons")
@Disabled  // Hides this ORIGINAL from the menu. Delete this line in YOUR copy so your copy shows up.
public class L18_GrandChallenge extends LinearOpMode {

    @Override
    public void runOpMode() {
        MyRobot robot = new MyRobot(this);
        robot.init();

        telemetry.addData("Status", "Ready for the Grand Challenge");
        telemetry.update();
        waitForStart();

        // Write your plan here FIRST, as comments: one line per step, in plain English.
        // Then turn each comment into a call to one of your robot's skills.
        // 1.
        // 2.
        // 3.

    }
}
