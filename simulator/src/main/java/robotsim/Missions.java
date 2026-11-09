package robotsim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The missions for the first-day lesson, plus a free-drive field for every other lesson.
 * Field tiles are 24 inches. Tile centers are at 12, 36, 60, 84, 108, 132.
 */
public final class Missions {

    private Missions() {
    }

    public static List<Mission> all() {
        List<Mission> list = new ArrayList<Mission>();

        list.add(Mission.builder(0, "Free Drive")
                .describe("An open field for trying any lesson. There's a wooden wall ahead for "
                        + "bumper (touch sensor) tests, a yellow block right in front of the claw, "
                        + "and another block to drive to.")
                .start(60, 72, 0)
                .wall(108, 48, 6, 48)
                .tape(false, 92, 8, 92, 136)
                .block(73, 72)
                .block(84, 24)
                .build());

        list.add(Mission.builder(1, "First Moves")
                .program("M01_FirstMoves")
                .describe("Drive straight ahead and STOP inside the blue box. "
                        + "Change one number in the program until you park in the box.")
                .start(12, 60, 0)
                .goal(new Mission.ZoneGoal("Stop inside the blue box", "PARK", 72, 48, 24, 24))
                .build());

        list.add(Mission.builder(2, "Turn and Park")
                .program("M02_TurnAndPark")
                .describe("Drive forward, turn, and park in the box. The program turns the wrong "
                        + "way and its times are off. Fix it!")
                .start(12, 36, 0)
                .goal(new Mission.ZoneGoal("Stop inside the blue box", "PARK", 48, 96, 24, 24))
                .build());

        list.add(Mission.builder(3, "Square Dance")
                .program("M03_SquareDance")
                .describe("Visit corners 1, 2, 3 in order, then come back HOME. The program only "
                        + "drives the first side. Add the rest. Turn on the trail to see your square.")
                .start(36, 36, 0)
                .goal(new Mission.WaypointGoal("Reach corner 1", "1", 84, 36, 10))
                .goal(new Mission.WaypointGoal("Reach corner 2", "2", 84, 84, 10))
                .goal(new Mission.WaypointGoal("Reach corner 3", "3", 36, 84, 10))
                .goal(new Mission.WaypointGoal("Come back HOME", "HOME", 36, 36, 10))
                .build());

        list.add(Mission.builder(4, "Scrambled")
                .program("M04_Scrambled")
                .describe("All the right steps are in the program, but in the WRONG ORDER. "
                        + "Put them in order to drive through the gate and park in the box.")
                .start(12, 108, 0)
                .cone(60, 95).cone(60, 121)
                .goal(new Mission.GateGoal("Drive through the gate", "GATE", 60, 96.5, 60, 119.5))
                .goal(new Mission.ZoneGoal("Stop inside the blue box", "PARK", 84, 48, 24, 24))
                .build());

        list.add(Mission.builder(5, "Bug Hunt")
                .program("M05_BugHunt")
                .describe("This program has 3 bugs. Find and fix them all, then park in the box. "
                        + "Rule: the robot must NOT move before START is pressed.")
                .start(12, 60, 0)
                .goal(new Mission.ZoneGoal("Stop inside the blue box", "PARK", 72, 48, 24, 24))
                .noMoveBeforeStart()
                .build());

        list.add(Mission.builder(6, "Drive It Yourself")
                .program("M06_DriveItYourself")
                .describe("Drive with the keyboard: W/S moves the left wheels, Up/Down arrows move "
                        + "the right wheels. Go through gate 1, then gate 2, then park at FINISH. "
                        + "How fast can you do it?")
                .start(18, 18, 0)
                .wall(47, 36, 2, 108)
                .wall(95, 0, 2, 48)
                .wall(95, 96, 2, 48)
                .goal(new Mission.GateGoal("Drive through gate 1", "1", 48, 0, 48, 36))
                .goal(new Mission.GateGoal("Drive through gate 2", "2", 96, 48, 96, 96))
                .goal(new Mission.ZoneGoal("Stop at FINISH", "FINISH", 120, 120, 24, 24))
                .timed()
                .build());

        list.add(Mission.builder(7, "Stop at the Tape")
                .program("M07_StopAtTape")
                .alsoFor("L17_StopAtTape")
                .describe("Drive forward and stop with the color sensor on the black tape. The tape "
                        + "moves every time you press Reset robot, so a timer won't work. This program "
                        + "has 2 bugs. (Lesson 17's program runs here too.)")
                .start(12, 60, 0)
                .tape(true, 84, 6, 84, 138)
                .moveTapeEachReset(-24, -12, 0, 12, 24)
                .goal(new Mission.TapeStopGoal("Stop with the sensor on the tape"))
                .build());

        list.add(Mission.builder(8, "Follow the Tape")
                .program("M08_FollowTape")
                .alsoFor("L17_FollowTape")
                .describe("Follow the black tape all the way to the END, using only the color sensor. "
                        + "This program has 2 bugs: it thinks it's always on the tape, and it can't make "
                        + "the turns. (Lesson 17's program runs here too.)")
                .start(12, 37, 0)
                .tape(true, 6, 36, 54, 36, 78, 48, 90, 72, 90, 96, 102, 114, 132, 120)
                .goal(new Mission.WaypointGoal("Follow the tape to point 1", "1", 54, 36, 10))
                .goal(new Mission.WaypointGoal("Follow the tape to point 2", "2", 90, 72, 10))
                .goal(new Mission.WaypointGoal("Follow the tape to point 3", "3", 90, 96, 10))
                .goal(new Mission.WaypointGoal("Reach the END", "END", 128, 119, 10))
                .timed()
                .build());

        list.add(Mission.builder(9, "Pick and Place")
                .program("M09_PickAndPlace")
                .describe("After Lessons 07, 08, and 13. You drive: grab the yellow block with the claw, "
                        + "lift it with the arm, and leave it in the DROP box. Keys: W/S and Left/Right "
                        + "drive, I/J arm up/down, K opens and closes the claw. This program has 2 bugs.")
                .start(18, 72, 0)
                .block(54, 72)
                .goal(new Mission.BlockHeldGoal("Grab the block"))
                .goal(new Mission.ArmUpGoal("Lift it: arm up past 45\u00b0", 45))
                .goal(new Mission.BlockInZoneGoal("Leave it in the DROP box", "DROP", 100, 96, 24, 24))
                .timed()
                .build());

        list.add(Mission.builder(10, "Bumper Bot")
                .program("M10_BumperBot")
                .describe("After Lesson 10. Drive until the bumper (touch sensor) touches the wall, then "
                        + "back up and stop in the box. This program has 2 bugs.")
                .start(24, 72, 0)
                .wall(96, 36, 6, 72)
                .goal(new Mission.BumperGoal("Drive until the bumper touches the wall"))
                .goal(new Mission.ZoneGoal("Back up and stop in the box", "PARK", 48, 60, 24, 24))
                .build());

        list.add(Mission.builder(11, "Staircase")
                .program("M11_Staircase")
                .describe("After Lessons 09 and 11. Climb the stairs: one tile forward, turn, one tile "
                        + "forward, turn... all the way to the TOP. A loop does the climbing, but it "
                        + "has 2 bugs.")
                .start(12, 24, 0)
                .goal(new Mission.WaypointGoal("Step 1", "1", 36, 24, 10))
                .goal(new Mission.WaypointGoal("Step 2", "2", 36, 48, 10))
                .goal(new Mission.WaypointGoal("Step 3", "3", 60, 48, 10))
                .goal(new Mission.WaypointGoal("Step 4", "4", 60, 72, 10))
                .goal(new Mission.WaypointGoal("Step 5", "5", 84, 72, 10))
                .goal(new Mission.WaypointGoal("Reach the TOP", "TOP", 84, 96, 10))
                .timed()
                .build());

        list.add(Mission.builder(12, "Precision Park")
                .program("M12_PrecisionPark")
                .describe("After Lesson 12. Drive exactly 60 inches with the encoders and park in the "
                        + "narrow box. This program has 2 bugs. When it works, turn on Low battery: "
                        + "does it still park?")
                .start(12, 72, 0)
                .goal(new Mission.ZoneGoal("Park in the narrow box", "PARK", 66, 60, 12, 24))
                .build());

        list.add(Mission.builder(13, "Special Delivery")
                .program("M13_SpecialDelivery")
                .describe("After Lesson 13. Grab the yellow block with the claw, carry it, and leave it "
                        + "in the DROP box. This program has 2 bugs.")
                .start(18, 72, 0)
                .block(30, 72)
                .goal(new Mission.BlockHeldGoal("Grab the block"))
                .goal(new Mission.BlockInZoneGoal("Leave it in the DROP box", "DROP", 72, 60, 24, 24))
                .build());

        list.add(Mission.builder(14, "Arcade Slalom")
                .program("M14_ArcadeSlalom")
                .describe("After Lesson 06. Drive with arcade controls: W/S forward and back, Left/Right "
                        + "arrows to turn. Go under the first wall, over the second, and park at FINISH. "
                        + "The controls have 2 bugs. Fix them, then race your best time!")
                .start(18, 18, 0)
                .wall(46, 40, 2, 104)
                .wall(94, 0, 2, 104)
                .goal(new Mission.GateGoal("Under the first wall", "1", 48, 0, 48, 40))
                .goal(new Mission.GateGoal("Over the second wall", "2", 96, 104, 96, 144))
                .goal(new Mission.ZoneGoal("Stop at FINISH", "FINISH", 120, 0, 24, 24))
                .timed()
                .build());

        list.add(Mission.builder(15, "State Machine")
                .program("M15_StateMachine")
                .describe("After Lessons 10 and 14. Drive to the wall, back up, turn right, and park in "
                        + "the box, one state at a time. This program has 2 bugs.")
                .start(24, 72, 0)
                .wall(96, 36, 6, 72)
                .goal(new Mission.BumperGoal("Drive until the bumper touches the wall"))
                .goal(new Mission.ZoneGoal("Back up, turn right, and park in the box", "PARK", 68, 36, 24, 24))
                .build());

        list.add(Mission.builder(16, "Robot Class")
                .program("M16_RobotClass")
                .describe("After Lessons 15 and 16. The Robot class drives to the CORNER, turns left, "
                        + "and parks in the box. This program has 2 bugs, both inside the Robot class.")
                .start(24, 36, 0)
                .goal(new Mission.WaypointGoal("Drive to the CORNER", "CORNER", 60, 36, 8))
                .goal(new Mission.ZoneGoal("Turn left and park in the box", "PARK", 48, 48, 24, 24))
                .build());

        // The Grand Challenge always comes last.
        list.add(Mission.builder(Mission.FINAL, "Grand Challenge")
                .program("L18_GrandChallenge")
                .describe("Lesson 18. One autonomous program, everything you've learned: grab the "
                        + "block, lift it, find the black tape (it moves every reset!), drive to the "
                        + "wall, back up and drop the block in the GOAL, then PARK. Can you do it in "
                        + "30 seconds, like a real FTC autonomous?")
                .start(18, 18, 0)
                .block(52, 18)
                .tape(true, 20, 84, 66, 84)
                .moveTapeUpDownEachReset(-12, -6, 0, 6, 12)
                .wall(120, 40, 6, 80)
                .goal(new Mission.BlockHeldGoal("Grab the yellow block"))
                .goal(new Mission.ArmUpGoal("Lift it: arm up past 45\u00b0", 45))
                .goal(new Mission.TapeStopGoal("Stop at the black tape"))
                .goal(new Mission.BumperGoal("Drive until the bumper touches the wall"))
                .goal(new Mission.BlockInZoneGoal("Back up and drop the block in the GOAL", "GOAL",
                        92, 44, 24, 72))
                .goal(new Mission.ZoneGoal("PARK in the box", "PARK", 84, 0, 36, 36))
                .timed()
                .build());

        return Collections.unmodifiableList(list);
    }
}
