package org.firstinspires.ftc.teamcode.lessons;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/*
 * LESSON 18 - The Grand Challenge.
 *
 * One autonomous program that uses everything you've learned:
 *   grab the block, lift it, find the black tape, drive to the wall,
 *   back up and drop the block in the GOAL, then PARK.
 *
 * The robot's devices and skills live in YOUR MyRobot class. This program is the plan:
 * it tells your robot what to do, in order.
 *
 * In the simulator, picking this program opens Mission 9: Grand Challenge.
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
