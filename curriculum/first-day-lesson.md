# First Day: Learn to Program with the Robot Simulator (90 minutes)

A highly interactive first lesson for students who have **never programmed**. Every student has
their own simulated robot, so nobody waits for hardware, and everything they write is real FTC
Java that runs on the real robot.

**Students use:** [Robot Missions](missions.md) (their handout) and the [simulator](simulator.md).
**Answers:** in the teacher answer key (outside this repo).

## Learning goals
By the end, students can:
- explain that a program is a list of exact instructions that runs **in order**,
- make the robot drive and turn with `setPower` and `sleep`,
- read an error message and use it to fix a bug,
- drive with a TeleOp program and change it.

## Why it's built this way
- **Read working code first, then change it.** Every mission starts from a program that almost
  works (the same Predict → Run → Investigate → Modify → Make method the handouts use).
- **Instant, visible feedback.** The trail, the goal checklist, and the **MISSION COMPLETE**
  banner tell students right away whether they got it.
- **Mission 4 is an unscramble puzzle.** Putting existing lines in the right order teaches the
  same ideas as writing code from scratch, in less time and with less frustration.
- **Errors are part of the game.** Mission 5 makes reading error messages a skill, not a failure.

---

## Before class (allow 30 minutes the day before)
- [ ] **Android Studio** installed on every student laptop.
- [ ] **Get the code onto each laptop.** Students don't need GitHub accounts yet. On
      github.com/drichardi/FtcRobotController click **Code → Download ZIP**, unzip it to
      `C:\Robotics\day1`, and open that folder in Android Studio.
      (Students clone their group's own copy later, in Lesson 00.)
- [ ] **Start the simulator once on each laptop** (run list → **Robot Simulator** → ▶). The first
      Gradle sync and launch are slow; get them out of the way before class.
- [ ] **Print** the Robot Commander cards (below), and optionally the [Robot Missions](missions.md) handout.
- [ ] *Optional, for the finale:* a bench board or robot set up with the course configuration.

---

## Timeline

| Time | Activity | What students do | Teacher moves |
|---|---|---|---|
| 0–10 | **Robot Commander** (no computers) | In pairs: one is the "robot", one gives instructions from cards to reach a target in the room. | Afterward ask: *"What went wrong?"* Steer toward: instructions must be **exact**, **in order**, with **how far / how long**. That's programming. |
| 10–20 | **First look** | Watch you run Mission 1 on the projector. Then predict, on their own, where the robot stops. | Show the 3 parts of the code: find the motors, `waitForStart()`, and the steps. Read `setPower` and `sleep` aloud in plain English. |
| 20–35 | **Missions 1–2** | Fix the sleep time; fix the turn direction and times. | Circulate. Ask *"What did you predict? What happened?"* rather than giving answers. |
| 35–45 | **Missions 3–4** | Copy and paste the sides of the square; unscramble the steps. | Show **Ctrl+Shift+↑/↓** once on the projector. |
| 45–55 | **Mission 5: Bug Hunt** | Read each error, fix one bug at a time. | Model reading an error aloud: *which file, which line, what it says*. |
| 55–75 | **Mission 6: Drive It Yourself** | Drive the course, then change `maxSpeed`, add telemetry, try the W A S D challenge. | Start a "best time" board. Point out the `while (opModeIsActive())` loop: *"this runs many times a second."* |
| 75–85 | **Challenges** | Speed run, draw your initial, fastest lap. | Fast finishers help classmates (explain, don't type for them). |
| 85–90 | **The real robot + exit ticket** | Watch one student's mission run on the real robot, unchanged. Answer the exit ticket. | Deploy with the **TeamCode** run configuration; the missions are in the Driver Station's **Missions** group. |

**Pacing:** it's fine if some students only finish Missions 1–3. The core idea is
**sequence**: steps happen in order, each with an amount. Missions 5–6 can carry into the next class.

---

## Robot Commander cards
Print one set per pair. The "robot" follows **only** what the cards say, exactly.

| Card | Card | Card | Card |
|---|---|---|---|
| **FORWARD __ steps** | **BACKWARD __ steps** | **TURN LEFT** (a quarter turn) | **TURN RIGHT** (a quarter turn) |
| **WAIT __ seconds** | **PICK UP** | **PUT DOWN** | **START** |

1. Put a target (a book, a cone) somewhere in the room.
2. The commander lays out cards in a row and fills in the numbers, **without** walking the path first.
3. The robot does exactly what the cards say, in order. No guessing what the commander meant!
4. Fix the cards and try again.

**Debrief questions** (2 minutes):
- What happened when the cards were in the wrong order?
- What happened when a number was wrong?
- Why do you think the START card exists? *(Teaser for `waitForStart()` and Mission 5.)*

---

## Common snags
| Student says | Usually means |
|---|---|
| "Nothing happens when I press START." | They didn't press INIT first, or the program crashed. Read the message on the right side of the Driver Station panel. |
| "My change didn't do anything." | The file wasn't saved, or they edited a different mission. INIT reloads saved code. |
| "The keys don't work." | Click the field once. The program must be running. |
| "It says BLOCKED." | The robot hit a wall or is trying to spin right next to one. |
| "Red error about `;` expected." | A missing semicolon. The line given is often the line *after* the real mistake. |

---

## After today
- Students move on to [Lesson 00](lessons/00-getting-the-code.md) (their group's own copy of the
  code), then the lessons. Every lesson can be tested in the simulator, in class or at home.
- The missions folder stays in the project. In their group repo, students who want to keep
  playing with missions should copy them into their own `students/yourname` folder, just like lessons.
