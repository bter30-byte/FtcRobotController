# Blocks to Java: A Robotics Programming Resource

A self-guided set of lessons, illustrated handouts, and working robot code that helps students
move from FTC Blocks to confident Java. It's built for open-ended, project-based classes:
students explore one lesson at a time on their own, and the teacher steps in with a short
explanation when it helps.

**Students start at [START-HERE.md](START-HERE.md).** This page is the teacher guide.

**🖥️ Every lesson runs in the [Robot Simulator](simulator.md),** a robot on the screen that runs
the same Java code as the real robot. Every student can test at the same time on their own
laptop, and they can keep practicing at home. Brand-new programmers start with a 90-minute
[first-day lesson](first-day-lesson.md) of simulator missions.

---

## How it's designed
- **Self-guided.** Every handout explains its new idea in a **📖 Learn it** section with a
  diagram, then walks students through it. If you give a short explanation, that section and its
  diagram work as a ready-made 5-minute mini-lesson.
- **Visual.** Lesson 00 has a picture for every step (GitHub, GitHub Desktop, Android Studio,
  Driver Station), with numbered callouts that match the numbered instructions. Each lesson has
  a concept diagram. All of them are in [`images/`](images/).
- **The same five steps every time (PRIMM):** **P**redict what the code does → **R**un it →
  **I**nvestigate with guided questions → **M**odify it → **M**ake something new. Students
  always start from code that works, which is the gentlest move up from Blocks.
- **Hints, not answers.** Harder questions have a collapsible 💡 hint, so students can get
  unstuck without being handed the answer.
- **Each lesson ends with a checklist and a "Stuck?" table**, so students can tell when
  they're ready to move on and fix the most common problems on their own.
- **One idea at a time.** Each lesson teaches one computer science idea through one robot
  skill. Most lessons are either driver control (TeleOp) or autonomous, not both.
- **Go at any pace.** Lessons take 30–45 minutes of focused work. Students can spread them
  across build days however your class runs. Not everyone has to finish all 16.

### Habits that make it work
- **Every student codes.** Every student has their own laptop and their own folder in the
  group repo, so every line they commit is theirs.
- **Programming doesn't wait for the robot.** Students test in the [simulator](simulator.md)
  first, all at once, then move working code to the robot. A [bench board](#the-bench-board)
  per group helps with the real-hardware step while the robot is being built.
- **Talk about code.** Encourage students to explain their code to a classmate before asking you.
  Explaining it out loud finds a lot of bugs.

---

## Lessons

| # | Lesson | CS idea | Robot skill | Type | Hardware |
|---|---|---|---|---|---|
| 🚀 | [Robot Missions](missions.md) (first day, [teacher plan](first-day-lesson.md)) | Sequence, debugging | Driving and turning, TeleOp | Both | Simulator only |
| 00 | [Getting the Code](lessons/00-getting-the-code.md) | Version control, packages | GitHub Desktop, Android Studio, deploying | — | Control Hub |
| 01 | [Hello Robot](lessons/01-hello-robot.md) | Syntax, comments, errors | Telemetry | TeleOp | Hub only |
| 02 | [Drive Sequence](lessons/02-drive-sequence.md) | Sequence | Motors, `sleep` | Auto | Drive motors |
| 03 | [Variables](lessons/03-variables.md) | Variables, types, operators | — | TeleOp | Hub only |
| 04 | [Gamepad Input](lessons/04-gamepad-input.md) | Input, the game loop | Gamepad | TeleOp | Hub + gamepad |
| 05 | [Tank Drive](lessons/05-tank-drive.md) | Input → process → output | Driving by hand | TeleOp | Drive motors |
| 06 | [Arcade Drive](lessons/06-arcade-drive.md) | Expressions | Arcade drive | TeleOp | Drive motors |
| 07 | [Buttons and If](lessons/07-buttons-and-if.md) | `if`/`else`, scope | Speed modes | TeleOp | Drive motors |
| 08 | [Sensors and Logic](lessons/08-sensors-and-logic.md) | Boolean logic | Touch sensor, arm limit | TeleOp | Arm + touch sensor |
| 09 | [Loops](lessons/09-loops.md) | `for`/`while`, off-by-one | Repeated moves | Auto | Drive motors |
| 10 | [Timer Auto](lessons/10-timer-auto.md) | Loop conditions | Timers, stopping on a sensor | Auto | Drive + touch sensor |
| 11 | [Methods](lessons/11-methods.md) | Methods, parameters | Reusable moves | Auto | Drive motors |
| 12 | [Encoder Drive](lessons/12-encoder-drive.md) | Return values, constants | Driving by inches | Auto | Drive motors with encoders |
| 13 | [Toggle](lessons/13-toggle.md) | State, edge detection | Claw toggle | TeleOp | Claw servo |
| 14 | [State Machine](lessons/14-state-machine-auto.md) | Enums, `switch` | Multi-step routines | Auto | Drive + touch sensor |
| 15 | [Robot Class: TeleOp](lessons/15-robot-class-teleop.md) | Classes, objects | One hardware class for all programs | TeleOp | Everything |
| 16 | [Robot Class: Auto](lessons/16-robot-class-auto.md) | Reuse | Shared code across programs | Auto | Everything |
| 17 | [Color Sensor](lessons/17-color-sensor.md) | Thresholds, feedback loops | Stop at and follow black tape | Auto | Drive + color sensor |

**A natural stopping point** for students who need more time is after **Lesson 10**. By then
they can drive by hand, use sensors, and write a simple autonomous. Lessons 11–16 make the code
more precise and better organized, and Lesson 17 adds a color sensor.

**Optional warm-up before Lesson 01:** [FTCSIM](#ftcsim). Students solve a few puzzles in Blocks,
then switch to the Java view to see the same program as text.

---

## What's in this repo

```
curriculum/
├── START-HERE.md                 ← students begin here
├── README.md                     ← this teacher guide
├── first-day-lesson.md           ← 90-minute first lesson plan (teacher)
├── missions.md                   ← Robot Missions handout (students, first day)
├── simulator.md                  ← how to use the simulator, in class and at home
├── lessons/00 … 16-*.md          ← student handouts
├── images/                       ← drawings, diagrams, and simulator screenshots (sim-*.png)
└── reference/
    ├── blocks-to-java.md         ← cheat sheet (print it!)
    ├── deploying-and-managing.md ← deploying (USB/Wi-Fi), cleaning up the menu, deploy troubleshooting
    └── robot-configuration.md    ← ports, wiring, Driver Station setup, where names in code come from

TeamCode/src/main/java/org/firstinspires/ftc/teamcode/
├── lessons/L01_HelloRobot.java … L17_FollowTape.java      ← working OpModes for every lesson
├── lessons/RobotHardware.java                              ← the robot class (Lessons 15–16)
└── missions/M01_FirstMoves.java … M06_DriveItYourself.java ← first-day missions

simulator/                        ← the Robot Simulator (desktop app, never goes on the robot)
.run/Robot Simulator.run.xml      ← adds "Robot Simulator" to Android Studio's run list
```

All lesson and mission files compile against FTC SDK 12.0 (this fork's version) **and** run
unchanged in the simulator, and each one is heavily commented for students. The six missions are
not `@Disabled`; on the real robot they appear in the Driver Station's **Missions** group. The original lesson OpModes are marked **`@Disabled`** so they don't
fill up the Driver Station menu. Students delete that line in their own copies (Lesson 00,
Step 7). To demo an original yourself, remove `@Disabled` locally, deploy, and don't commit that
change.

**About the pictures:** the simulator images (`sim-*.png`) are real screenshots. The other
screen images are simplified drawings, not real screenshots, and each one says so. They show the layout and where to click. If your versions of GitHub Desktop or
Android Studio look different, the steps are the same. The images are SVG files, so you can open
and edit their text in any text editor.

The answer key and optional skill checks are kept **outside** this repository so students can't
see them.

---

## Setup

### How students get and share code
Students use **Android Studio** and **GitHub Desktop**. [Lesson 00](lessons/00-getting-the-code.md)
walks groups through it step by step. Here's how it fits together:

```
your fork (drichardi/FtcRobotController)      ← you add and update lessons here
   └── fork → captain/robotics-group-#        ← one per group of 3–4
                 └── clone → each student's laptop
                               └── TeamCode/.../teamcode/students/<name>/   ← each student's own folder
```

- **Each student copies lessons into their own folder** (`students/<name>`), gives each copy a
  unique name, and deletes `@Disabled`. Nobody ever edits the same file as someone else, so
  beginners never hit merge conflicts.
- **Pushing new or fixed lessons to groups:** commit to your fork. Each group's Captain opens
  their repo on GitHub.com and clicks **Sync fork → Update branch**, and then everyone pulls.
  Because students never edit `lessons/`, this always goes through cleanly.
- **Checking participation:** each group repo's **Insights → Contributors** page and commit
  history show who committed what.
- **Later, for shared group projects:** when several students need to work in one shared
  folder, have them agree on an **owner for each file**, and pull and push often.

### Before Lesson 00 (allow a week)
- [ ] **GitHub accounts** for every student. GitHub requires users to be **13 or older**.
  Have students sign up ahead of time, because verification emails can get stuck in school
  filters. Collect usernames so Captains can invite teammates quickly.
- [ ] **Install Android Studio** (the fork's README requires *Narwhal 3 Feature Drop* or later)
  and **GitHub Desktop** on every student laptop.
- [ ] **Pre-warm Gradle.** The first time each laptop opens the project, Android Studio
  downloads a large amount of tools and libraries. If every laptop does this at once on school
  Wi-Fi, it can take a whole period. Open the project once on each machine beforehand. If
  students log in with their own profiles, do it once per profile or plan extra time.
- [ ] **Network:** ask IT to allow `github.com`, `dl.google.com`, `maven.google.com`,
  `repo.maven.apache.org`, `services.gradle.org`, and `plugins.gradle.org`.
- [ ] **Clone folder:** pick a short local path that isn't synced to the cloud (for example
  `C:\Robotics`). OneDrive-synced folders cause random build failures.
- [ ] **USB-C data cables** for deploying to each Control Hub. Some cables only charge.
- [ ] **Clear old Blocks and OnBot Java programs** off each Control Hub. An Android Studio deploy
  doesn't remove them, so they'd stay on the menu. Download any worth keeping first. Steps are in
  [deploying-and-managing.md](reference/deploying-and-managing.md#leftover-blocks-and-onbot-java-programs).

*Alternative:* GitHub Classroom group assignments can create group repos for you and skip
Steps 1–2 of Lesson 00. However, a Classroom repo isn't a fork, so the one-click **Sync fork**
for lesson updates won't be there.

### Robot configuration
Every lesson uses these exact names. Create **one** configuration on each Control Hub. The
[robot configuration guide](reference/robot-configuration.md) has a wiring diagram,
Driver Station steps with pictures, and an explanation students can read of how a port becomes
a name in code.


| Name | Device | Port | Used in |
|---|---|---|---|
| `left_drive` | DC motor (with encoder cable) | Motor 0 | L02, L05–L07, L09–L12, L14–L16 |
| `right_drive` | DC motor (with encoder cable) | Motor 1 | same |
| `arm` | DC motor | Motor 2 | L08, L15–L16 |
| `claw` | Servo | Servo 0 | L13, L15–L16 |
| `touch_sensor` | REV Touch Sensor | Digital 1 (use the odd channel of the pair on REV hubs) | L08, L10, L14–L16 |
| `sensor_color` | REV Color Sensor V3, at the front looking down, ~1 cm above the floor | I2C bus 1 | L17 |

If a device is missing from the configuration, the OpMode stops with an error when you press
INIT. That makes a good teaching moment for students to read. Lessons **01, 03 and 04 need no
devices at all.**

### The bench board
Programming usually gets left behind because the robot isn't done yet. To fix that, give each
group a **bench board**: a piece of plywood or a Tetrix/goBILDA plate with a Control Hub,
battery, switch, 2 motors with wheels or flags taped on, 1 servo, and 1 touch sensor, all wired
to the configuration above. Students can do every lesson on it while the real robot is being
built. Programmers learn wiring along the way.

### FTCSIM
[FTCSIM](https://ftcsim.org) (from FIRST Robotics Canada) runs in a browser and supports Blocks
and OnBot Java. It has Intro, Movement, Sensors, and Puzzle tracks. Create a teacher account so
students can save work. It's useful as a warm-up before Lesson 01, and as extra practice for
anyone waiting on hardware. The simulated robots use their own hardware names, so check them in
each puzzle's Java view before pasting lesson code in.

---

## Checking understanding
- **Checklists and exit tickets:** every handout ends with an "I can…" checklist and one exit
  ticket question. A quick look at exit tickets shows who's ready to move on.
- **Commit history:** each student's commits show steady progress (or a stall worth checking on).
- **Optional skill checks** (in the teacher answer key): two short individual checks, each with
  a hands-on task and a code-reading quiz.
  - **Ready to drive** (after Lesson 08): write arcade drive with a slow mode from a blank OpMode.
  - **Ready for autonomous** (after Lesson 13): drive a square using methods and a timer loop.

---

## Supporting different learners

**For students who are flying:**
- Every *Make* section is open-ended. Push for clean code, not just working code.
- Extensions: IMU-based turning (`RobotAutoDriveByGyro_Linear` sample), mecanum drive
  (`BasicOmniOpMode_Linear` sample), AprilTags (`ConceptAprilTagEasy` sample). All of them are
  in this fork under `FtcRobotController/.../external/samples`.
- Split `RobotHardware` into `Drivetrain` and `Arm` classes (FRC subsystem style).

**For students who are struggling:**
- Let them build the idea in Blocks first, then translate it with the cheat sheet.
- Use **trace tables**: write each variable's value after every line on paper.
- Pair them with a patient classmate and have them explain each line back.
- Lower the bar to Learn it + Predict/Run/Investigate + Modify #1. Skip *Make*.

---

## Common problems and fixes

| Symptom | Likely cause |
|---|---|
| OpMode isn't on the Driver Station menu | Student didn't delete `@Disabled` in their copy; build didn't finish or failed; looking under TeleOp for an `@Autonomous` |
| Teammate's program vanished, or a deleted one came back | Someone deployed without pulling first. See [deploying-and-managing.md](reference/deploying-and-managing.md) |
| Nobody in a group can deploy | Someone pushed code that doesn't build. The **Build** tab shows whose file. |
| Error at INIT: "unable to find a hardware device" | Name in code doesn't match the configuration (check capitals and `_`) |
| Robot spins when it should drive straight | Missing or wrong `setDirection(REVERSE)` |
| Pushing stick up drives backward | Forgot the `-` on `left_stick_y` |
| Encoder drive never finishes | Encoder cable unplugged, or one motor's direction is wrong so it drives away from its target |
| Toggle flickers | No edge detection (see Lesson 13) |
| Robot doesn't stop when STOP is pressed | A loop is missing `opModeIsActive()` |
| Timed autonomous worked yesterday, overshoots today | Battery charge changed. That's the reason for Lesson 12 (encoders). |

---

## Going further
- When students start on a real FTC robot, they can start a fresh package for that robot and
  copy in their `RobotHardware` class as a starting point.
