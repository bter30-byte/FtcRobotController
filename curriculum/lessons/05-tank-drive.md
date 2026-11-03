# Lesson 05 — Tank Drive

> **You will learn:** how to connect the gamepad to the motors, so you can drive the robot.
> **You need:** the two drive motors (`left_drive`, `right_drive`) and a gamepad. No robot handy? Use the [simulator](../simulator.md).
> **Time:** about 30 minutes · **Program type:** TeleOp
> **Code:** [`L05_TankDrive.java`](../../TeamCode/src/main/java/org/firstinspires/ftc/teamcode/lessons/L05_TankDrive.java)

## Before you start
1. **Pull** in GitHub Desktop.
2. **Copy** `L05_TankDrive.java` into your `students/yourname` folder.
3. In your copy, give it a unique `name` and **delete `@Disabled`**.

[Forgot how? Lesson 00, Step 7 has pictures.](00-getting-the-code.md#step-7--copy-your-first-lesson)

## 🖥️ No robot? Use the simulator
This lesson runs in the [Robot Simulator](../simulator.md), on any laptop or at home. Pick your program; the simulator switches to the **Free Drive** field. **W/S** drive the left wheels and **↑/↓** drive the right wheels.

---

## 📖 Learn it

This lesson puts Lessons 02 and 04 together. Almost every robot program follows the same
three-part pattern, every pass of the game loop:

| Part | What it means | In this lesson |
|---|---|---|
| **Input** | Read something from the world | Read the left and right sticks |
| **Process** | Do math or make a decision | Multiply by a speed limit |
| **Output** | Make something happen | Set the motor powers |

**Tank drive** works like a tank or a bulldozer: the **left stick** controls the **left
wheels**, and the **right stick** controls the **right wheels**. Both sticks up = forward. One
up and one down = spin.

**A tuning variable.** The code has `double maxSpeed = 0.5;`. Every stick value gets multiplied
by it, so full stick = half power. Changing **one** number changes how fast the whole robot can
go. That's the power of a variable.

```java
double leftPower = leftStick * maxSpeed;   // stick 1.0 × 0.5 = power 0.5
```

---

## 1. Predict
1. You push both sticks all the way up. What power goes to each motor?
2. You push the left stick up and the right stick down. What does the robot do?

## 2. Run
Drive the robot around. Find the **Input**, **Process**, and **Output** sections in the code and
add a comment above each one: `// INPUT`, `// PROCESS`, `// OUTPUT`.

## 3. Investigate
1. Remove the `-` from `-gamepad1.left_stick_y`. What happens when you push the left stick up?
2. Set `maxSpeed = 2.0`. Is the robot faster? Is it easier or harder to drive slowly? Why?
3. Why is `maxSpeed` a variable instead of typing `0.5` in two places?

<details><summary>💡 Hint for #2</summary>

Motors can't go above 1.0 power. What is half-stick (0.5) × 2.0? What about three-quarter stick?

</details>

## 4. Modify
1. Find the `maxSpeed` that you can drive with best.
2. Make the robot drive **backward** when the sticks are pushed up (a "reverse mode" for driving back to where you started).

<details><summary>💡 Hint for #2</summary>

There are two places you could flip the sign: when you read the sticks, or when you set the power.
Either one works.

</details>

## 5. Make
Add telemetry that shows the robot's *average* power: `(leftPower + rightPower) / 2`. When is it 0
even though the wheels are moving?

---

## ✅ Check yourself
- [ ] I can point to the input, process, and output parts of my code.
- [ ] I can change how fast the robot drives by changing one variable.
- [ ] I can drive the robot forward, backward, and turn with tank drive.

**Exit ticket:** Write the Input, Process, and Output for something that isn't a robot (a microwave, a video game, a thermostat…).

## 🆘 Stuck?
| Problem | Try this |
|---|---|
| Pushing up drives backward | Check the minus signs on both stick lines. |
| Robot spins when both sticks are up | One motor needs `setDirection(REVERSE)`. Try the other side. |
| Robot is too fast to control | Lower `maxSpeed`. |
| Nothing moves | Is the robot plugged in and on? Does the telemetry show power values changing? |

---
<!-- lesson-nav -->
[← Lesson 04: Reading the Gamepad](04-gamepad-input.md) · [All lessons](../START-HERE.md#lessons) · [Lesson 06: Arcade Drive →](06-arcade-drive.md)
