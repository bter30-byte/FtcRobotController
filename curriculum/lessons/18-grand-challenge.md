# Lesson 18 — Capstone: Your Robot, Your Code

> **You will:** set up a robot configuration yourself, write your own robot class starting from an
> almost empty file, and combine everything from Lessons 01–17 into **one autonomous program**.
> **You need:** everything: drive motors, arm, claw, touch sensor (as the front bumper), and
> color sensor. No robot handy? The [simulator](../simulator.md) has all of it.
> **Time:** 3–4 class periods · **Program type:** Autonomous
> **Code:** [`MyRobot.java`](../../TeamCode/src/main/java/org/firstinspires/ftc/teamcode/lessons/MyRobot.java) and [`L18_GrandChallenge.java`](../../TeamCode/src/main/java/org/firstinspires/ftc/teamcode/lessons/L18_GrandChallenge.java) (both nearly empty, on purpose)

This lesson is different. There's no finished program to read and change: **you** build it. The
steps below tell you *what* to build and *in what order*. The *how* is up to you, and every
part links back to the lesson that taught it.

## Before you start
1. **Pull** in GitHub Desktop.
2. **Copy both** `MyRobot.java` and `L18_GrandChallenge.java` into your `students/yourname` folder.
3. In your copy of `L18_GrandChallenge.java`, give it a unique `name` and **delete `@Disabled`**.

[Forgot how? Lesson 00, Step 7 has pictures.](00-getting-the-code.md#step-7--copy-your-first-lesson)

## 🖥️ No robot? Use the simulator
- Picking `L18_GrandChallenge` opens the **Grand Challenge** (the last field in the mission list).
- Make your own configuration with **⋮ → Configure Robot** (Part 1).
- When you go back to other lessons, **Activate CourseBot** again. Lessons 01–17 use its names.

![The Grand Challenge field](../images/sim-grand-challenge.png)

---

## Part 1: Set up your robot (configuration)
On a real team, someone decides what every device is called, and everyone else's code depends
on it. This time, that's you.

1. Open **Configure Robot** (the Driver Station's **⋮** menu, or the simulator's **Config** tile)
   and press **New**.
2. Add every device your robot needs for the challenge: two drive motors, the arm motor, the
   claw servo, the touch sensor, and the color sensor.
3. Pick your **own** names. They don't have to match CourseBot.
4. **Save** it with your name (for example `AvaBot`) and **Activate** it.

**The standards** (a teammate or your teacher checks these before you go on):
- [ ] Every name is lowercase, with `_` between words, and no spaces.
- [ ] Every name says what the device **does** (`lift_motor`, `front_bumper`), not what it is (`motor2`, `sensor`).
- [ ] No two devices have the same name.
- [ ] Every device is on the port it's really plugged into. In the simulator, the wiring is
      fixed: see [the course robot table](../reference/robot-configuration.md#the-course-robot).
      Names are yours; ports aren't!

**Fill in your configuration sheet.** You'll need it for Part 2.

| Port | Device type | Your name |
|---|---|---|
| Motor 0 | | |
| Motor 1 | | |
| Motor 2 | | |
| Servo 0 | | |
| Digital 1 | | |
| I2C bus 1 | | |

<details><summary>💡 Hint: the touch sensor won't press</summary>

A REV Touch Sensor in the port marked 0-1 is configured as **digital port 1**, not 0.

</details>

---

## Part 2: Build your robot class (`MyRobot.java`)
Open your copy of `MyRobot.java`. It has three empty sections. Fill them in, testing as you go.

### 1. Devices
One `private` field for each device in your configuration.
[Lesson 15](15-robot-class-teleop.md) did this in `RobotHardware.java`.

### 2. `init()`
- [ ] One `opMode.hardwareMap.get(...)` per device, using **your configuration's names**.
- [ ] Reverse the motor that needs it, so positive power drives both wheels forward ([Lesson 02](02-drive-sequence.md)).
- [ ] Set the drive motors to `BRAKE` and reset their encoders ([Lesson 12](12-encoder-drive.md)).
- [ ] Set up the color sensor: gain and light ([Lesson 17](17-color-sensor.md)).
- [ ] Start with the claw **open**.

### 3. Skills
One method per skill. You need **at least** these. Name them however you like:

| Skill | Hint | Learned in |
|---|---|---|
| Drive an exact distance, forward or back | Encoders, `RUN_TO_POSITION` | [12](12-encoder-drive.md), [16](16-robot-class-auto.md) |
| Turn about 90° left or right | Encoders, wheels in opposite directions | [16](16-robot-class-auto.md) |
| Open and close the claw | Servo positions, then wait | [13](13-toggle.md) |
| Raise the arm partway | Power for a short time, then `BRAKE` | [08](08-sensors-and-logic.md) |
| Drive until the color sensor sees black tape | A `while` loop with a sensor condition | [17](17-color-sensor.md) |
| Drive until the bumper touches something | A `while` loop with a touch sensor, **plus a time limit** | [10](10-timer-auto.md) |

<details><summary>💡 Hint: what does a skill method look like?</summary>

The same shape as the methods in Lessons 11 and 16:
```java
public void closeClaw() {
    claw.setPosition(0.3);
    opMode.sleep(500);     // give the servo time to move
}
```
Inside `MyRobot`, use `opMode.sleep(...)`, `opMode.opModeIsActive()`, and `opMode.telemetry`.

</details>

<details><summary>💡 Hint: every "until" loop needs a way out</summary>

What if the sensor never sees the tape? Your robot drives forever. Give every `while` loop a
timer too, like Lesson 10:
`while (opMode.opModeIsActive() && !touched && timer.seconds() < 5)`

</details>

---

## Part 3: Test one skill at a time
Don't write the whole challenge and hope! Test each skill **by itself** first.

1. In `L18_GrandChallenge`, after `waitForStart()`, call **one** skill. Run it.
2. Does it do exactly what you expect? Use telemetry to show numbers (ticks, brightness).
3. When it works, test the next skill. **Commit** after each one that works.

In the simulator, pick **Free Drive** to test without the challenge's goals. The Robot panel
shows every device's port and your configuration's names.

---

## Part 4: The Grand Challenge
One autonomous program, no gamepad, no help. Starting from the bottom-left corner, facing the block:

1. **Grab** the yellow block.
2. **Lift** it: raise the arm partway (past 45°).
3. **Find the black tape** and stop on it. The tape **moves every time you reset**, so only the
   color sensor can find it.
4. **Drive to the wall** until the bumper (touch sensor) presses.
5. **Back up and drop** the block in the **GOAL**.
6. **PARK** in the box.

The Mission panel checks off each step. Your time shows when you finish.

### Plan first
Before you write any code, write your plan as comments in `L18_GrandChallenge`: one line per
step, in plain English. Then turn each comment into a call to one of your skills. A good plan
reads almost like the list above.

<details><summary>💡 Hint: the robot stops right away when it should drive to the wall</summary>

Look at Lesson 08. What else presses the touch sensor besides the bumper? How high did you raise
the arm?

</details>

<details><summary>💡 Hint: the block lands in the wrong place</summary>

The block sits in front of the claw. If the bumper is touching the wall, where is the block?

</details>

<details><summary>💡 Hint: it works sometimes, but not every time</summary>

Does any step use a time or distance where it should use a sensor? Run it several times and
press **Reset robot** in between, so the tape moves.

</details>

### Levels
| Level | You did it when... |
|---|---|
| 🥉 Bronze | Steps 1–3 work. |
| 🥈 Silver | All 6 steps work. |
| 🥇 Gold | All 6 steps, under **30 seconds** (a real FTC autonomous is 30 seconds long). |
| 🏆 Platinum | Gold, **5 resets in a row** without changing the code. Then try it on the real robot! |

### On the real robot
Set up the same course on the floor: a block in front of the robot, a strip of black tape
across its path about 3 feet away, and a wall (or a box) to drive into. Expect to tune turn
distances, the tape threshold, and powers. That's normal ([Lesson 17](17-color-sensor.md) explains why).

---

## ✅ Check yourself
- [ ] I made a robot configuration with my own names, and my code uses them.
- [ ] I wrote a robot class from an empty file: fields, `init()`, and skill methods.
- [ ] I tested each skill by itself before combining them.
- [ ] My autonomous uses encoders, the color sensor, and the touch sensor, each where it fits best.

**Reflection:** Which step was hardest, and why? Which lesson helped the most? If you had one
more week, what would you add to your robot class?

## 🆘 Stuck?
| Problem | Try this |
|---|---|
| *Unable to find a hardware device* | The name in quotes doesn't match **your active** configuration. Check your configuration sheet, and check which configuration is active. |
| Other lessons stopped working | They use CourseBot's names. **Activate CourseBot** when you go back to them. |
| The robot drives backwards or spins | Is the right motor reversed? Are left and right swapped in your configuration? |
| The robot never stops at the tape | Show the brightness on telemetry. Is your threshold between the floor and tape readings? Did you set the gain? |
| The robot drives into the wall forever | The touch sensor is on the wrong port in your configuration, or your loop never checks it. Add a time limit too. |
| The program ends instantly | `L18_GrandChallenge` is empty after `waitForStart()`, or a `while` loop's condition is false from the start. |

---
<!-- lesson-nav -->
[← Lesson 17: Color Sensor](17-color-sensor.md) · [All lessons](../START-HERE.md#lessons) · [Back to Start Here →](../START-HERE.md)
