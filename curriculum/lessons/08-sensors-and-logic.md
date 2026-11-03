# Lesson 08 — Sensors and Boolean Logic

> **You will learn:** how to read a touch sensor, and how to combine true/false values with
> AND, OR, and NOT.
> **You need:** an `arm` motor and a `touch_sensor`. No arm yet? Use any spare motor and press the sensor with your finger. No robot handy? Use the [simulator](../simulator.md).
> **Time:** about 40 minutes · **Program type:** TeleOp
> **Code:** [`L08_SensorsAndLogic.java`](../../TeamCode/src/main/java/org/firstinspires/ftc/teamcode/lessons/L08_SensorsAndLogic.java)

## Before you start
1. **Pull** in GitHub Desktop.
2. **Copy** `L08_SensorsAndLogic.java` into your `students/yourname` folder.
3. In your copy, give it a unique `name` and **delete `@Disabled`**.

[Forgot how? Lesson 00, Step 7 has pictures.](00-getting-the-code.md#step-7--copy-your-first-lesson)

## 🖥️ No robot? Use the simulator
This lesson runs in the [Robot Simulator](../simulator.md), on any laptop or at home. Pick your program; the simulator switches to the **Free Drive** field. **I** is the Y button (arm up) and **J** is the A button (arm down). Watch the arm in the **side view**: the touch sensor lights up at the top. In the simulator the touch sensor is also pressed if the front bumper touches a wall, and you can press it yourself with **Space**. For the *Make* section, the Robot panel shows when the gamepad rumbles.

---

## 📖 Learn it

**A touch sensor** is a button the robot can feel. `topLimit.isPressed()` gives `true` while
it's pressed and `false` when it isn't. Mounted at the top of an arm's travel, it's called a
**limit switch**: it tells the program "the arm can't go any higher."

**Boolean logic** combines true/false values:
| Java | Name | `true` when… | Example |
|---|---|---|---|
| `a && b` | AND | **both** are true | Y pressed **and** not at the top |
| `a \|\| b` | OR | **either one** (or both) is true | bumper hit **or** time ran out |
| `!a` | NOT | `a` is false | **not** at the top |

**A truth table** lists every possible combination. With two true/false values there are only
4 combinations, so you can check them all:

| `a` | `b` | `a && b` | `a \|\| b` |
|---|---|---|---|
| false | false | false | false |
| false | true | false | true |
| true | false | false | true |
| true | true | true | true |

**Good names make logic readable.** The code stores each question in a well-named variable
first:
```java
boolean upRequested = gamepad1.y;
boolean atTop = topLimit.isPressed();
if (upRequested && !atTop) { ... }    // reads like English: "up requested and not at top"
```

**BRAKE mode.** `setZeroPowerBehavior(BRAKE)` makes a motor hold still at power 0, instead of
coasting or letting the arm fall.

---

## 1. Truth table warm-up
| upRequested | atTop | `!atTop` | `upRequested && !atTop` | `upRequested \|\| !atTop` |
|---|---|---|---|---|
| false | false | | | |
| false | true | | | |
| true | false | | | |
| true | true | | | |

## 2. Predict
1. You hold Y while the touch sensor is pressed. What does the arm do? What does telemetry say?
2. You hold Y **and** A at the same time (sensor not pressed). Up or down? Why?

## 3. Run
Test both predictions.

## 4. Investigate
1. Change `upRequested && !atTop` to `upRequested || !atTop`. Use your truth table to predict what happens when **nobody touches the gamepad**. Then test it carefully (keep hands clear!). Change it back.
2. Why does the code store `gamepad1.y` in a variable called `upRequested` instead of using `gamepad1.y` directly in the `if`?
3. Watch `Arm encoder` as the arm moves. Which direction makes the number go up?

<details><summary>💡 Hint for #1</summary>

"Nobody touches the gamepad" means `upRequested` is false. If the arm is somewhere in the
middle, `atTop` is false too. Find that row in your table, in the `||` column.

</details>

## 5. Modify
1. Add a **software lower limit**: don't let the arm go down if `arm.getCurrentPosition()` is below `0`. (You'll need `&&` again.)
2. Make the arm move slower when the left bumper is held.

<details><summary>💡 Hint for #1</summary>

Change the `else if (downRequested)` question so it's only true when down is requested **and**
the arm position is greater than 0. The encoder counts from wherever the arm was when the
robot turned on, so start with the arm all the way down.

</details>

## 6. Make
Use `gamepad1.rumble(200);` to make the gamepad buzz for 200 milliseconds when the driver tries
to go past the top limit. (Bonus: what happens if you hold Y at the top? How could you make it
buzz only once?)

---

## ✅ Check yourself
- [ ] I can read a touch sensor.
- [ ] I can fill in a truth table for `&&`, `||`, and `!`.
- [ ] I can write a safety rule as one boolean expression.

**Exit ticket:** A rule says "the arm may go up only if the driver presses Y AND the arm is NOT at the top AND the claw is closed." Write it as one Java boolean expression.

## 🆘 Stuck?
| Problem | Try this |
|---|---|
| `isPressed()` is always false | REV touch sensors use the **odd-numbered** channel of a digital port (for example port 1, not 0) in the configuration. |
| Arm drifts down at power 0 | Make sure the BRAKE line is there. A heavy arm may still need a little holding power. |
| Arm goes the wrong way | Swap the signs of the up and down powers, or reverse the motor. |

---
<!-- lesson-nav -->
[← Lesson 07: Buttons and If](07-buttons-and-if.md) · [All lessons](../START-HERE.md#lessons) · [Lesson 09: Loops →](09-loops.md)
