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
                .program("L17_StopAtTape")
                .describe("Lesson 17. Drive forward and stop with the color sensor on the black "
                        + "tape. The tape moves every time you press Reset robot, so a timer won't "
                        + "work. Use the sensor!")
                .start(12, 60, 0)
                .tape(true, 84, 6, 84, 138)
                .moveTapeEachReset(-24, -12, 0, 12, 24)
                .goal(new Mission.TapeStopGoal("Stop with the sensor on the tape"))
                .build());

        list.add(Mission.builder(8, "Follow the Tape")
                .program("L17_FollowTape")
                .describe("Lesson 17. Follow the black tape all the way to the END, using only "
                        + "the color sensor.")
                .start(12, 37, 0)
                .tape(true, 6, 36, 54, 36, 78, 48, 90, 72, 90, 96, 102, 114, 132, 120)
                .goal(new Mission.WaypointGoal("Follow the tape to point 1", "1", 54, 36, 10))
                .goal(new Mission.WaypointGoal("Follow the tape to point 2", "2", 90, 72, 10))
                .goal(new Mission.WaypointGoal("Follow the tape to point 3", "3", 90, 96, 10))
                .goal(new Mission.WaypointGoal("Reach the END", "END", 128, 119, 10))
                .timed()
                .build());

        return Collections.unmodifiableList(list);
    }
}
