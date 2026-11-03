# Lesson 16 — Reuse: Autonomous With the Same Robot Class

> **You will learn:** how one well-built class powers both driver control and autonomous, and
> how to pass objects to methods.
> **You need:** everything in `RobotHardware.init()`, and a block for the claw to carry. No robot handy? Use the [simulator](../simulator.md).
> **Time:** about 40 minutes · **Program type:** Autonomous
> **Code:** [`L16_AutoWithClass.java`](../../TeamCode/src/main/java/org/firstinspires/ftc/teamcode/lessons/L16_AutoWithClass.java) (uses `RobotHardware.java`)

## Before you start
1. **Pull** in GitHub Desktop.
2. **Copy** `L16_AutoWithClass.java` into the **same** folder as your `RobotHardware.java` from Lesson 15.
3. In your copy, give it a unique `name` and **delete `@Disabled`**.
4. Delete any `import org.firstinspires.ftc.teamcode.lessons.RobotHardware;` line, so it uses **your** robot class.

[Forgot how to copy? Lesson 00, Step 7 has pictures.](00-getting-the-code.md#step-7--copy-your-first-lesson)

## 🖥️ No robot? Use the simulator
This lesson runs in the [Robot Simulator](../simulator.md), on any laptop or at home. Pick your program; the simulator switches to the **Free Drive** field. The yellow block starts in front of the claw, so the robot picks it up, carries it, and drops it. Tune `INCHES_PER_90_DEGREE_TURN` until the turn looks like a clean 90 degrees.

---

## 📖 Learn it

In Lesson 15 a TeleOp used your `RobotHardware` class. This autonomous uses **the exact same
class**. That's **reuse**: write it once, use it everywhere.

![The same class used by TeleOp and Autonomous](../images/concept-class-object.svg)

Why it matters:
- **Fix a bug once.** If `encoderDrive` has a problem, you fix one file and every program gets better.
- **Tune once.** Change `CLAW_OPEN_POSITION` in `RobotHardware`, and TeleOp and Autonomous both use the new value.
- **Shorter programs.** The autonomous only describes *what* to do: drive, turn, open the claw.
  *How* the motors do it is the class's job.

**Methods can return a `boolean`.** `encoderDrive(...)` returns `true` if it reached its
target, or `false` if it gave up (timed out). The program can react:
```java
boolean made = robot.encoderDrive(-6, -6, 0.4, 2.0);
if (!made) {
    telemetry.addLine("Something blocked the robot!");
}
```

**Objects can be passed to methods**, just like numbers:
```java
private void turnRight90(RobotHardware robot) { ... }
```
The method needs the object to call its methods, so you hand it in as a parameter.

---

## 1. Predict
Read the code. Draw the path on graph paper. Mark where the claw opens.

## 2. Run
Run it. Then tune `INCHES_PER_90_DEGREE_TURN` until the turn is a clean 90°. Record your value.

## 3. Investigate
1. Change `CLAW_OPEN_POSITION` in your `RobotHardware.java`. Run **both** L15 and L16. What changed in each? Why is this powerful?
2. `encoderDrive` returns a `boolean`. What does `true` mean? What does `false` mean? Where does L16 use it?
3. `turnRight90(RobotHardware robot)` takes an **object** as a parameter. Why does it need one?

## 4. Modify
1. Add `turnLeft90(robot)`.
2. If the first `encoderDrive` times out, skip the rest of the routine and show a warning.

<details><summary>💡 Hint for #2</summary>

Put the first drive in an `if (!robot.encoderDrive(...)) { ... }`. Inside, show telemetry,
wait a moment, then use `return;`. `return` in a `void` method means "stop this method right now."

</details>

## 5. Make
Move turning **into** `RobotHardware` as `turnDegrees(double degrees, double power)`. Calculate
the inches from the degrees using your tuned value (90° → `INCHES_PER_90_DEGREE_TURN`). Now any
program can turn any angle.

<details><summary>💡 Hint</summary>

Move the constant into `RobotHardware` too. If 90° needs `INCHES_PER_90_DEGREE_TURN` inches,
then `degrees` needs `degrees / 90.0 × INCHES_PER_90_DEGREE_TURN`. Turning right means the
left side goes forward and the right side goes backward.

</details>

---

## ✅ Check yourself
- [ ] I can use the same class in a TeleOp and an Autonomous.
- [ ] I can use a method's `boolean` answer in an `if`.
- [ ] I can move a useful method into the robot class.

**Exit ticket:** You find a bug in `encoderDrive`. How many files do you need to fix? Before Lesson 15, how many would it have been?

## 🆘 Stuck?
| Problem | Try this |
|---|---|
| L16 doesn't see my changes to `RobotHardware` | It's using the teacher's copy. Delete the `import …lessons.RobotHardware;` line, and make sure L16 is in the same folder as your `RobotHardware`. |
| Turns are too big or too small | Tune `INCHES_PER_90_DEGREE_TURN`. |
| A drive "times out" every time | The timeout is too short for the distance, or a wheel is blocked. |

**What's next?** You've finished the lessons! Ask your teacher about extension challenges, like
turning with the IMU or driving a mecanum robot, or start using your `RobotHardware` class on
your own project.

---
<!-- lesson-nav -->
[← Lesson 15: Robot Class: TeleOp](15-robot-class-teleop.md) · [All lessons](../START-HERE.md#lessons) · [Lesson 17: Color Sensor →](17-color-sensor.md)
