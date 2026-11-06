# 🚀 Robot Missions: Your First Day of Programming

> **You will learn:** what a program is, how to make a robot move with Java, and how to fix
> code that doesn't work.
> **You need:** a laptop with the [Robot Simulator](simulator.md). No robot needed.
> **Time:** about 60 minutes for all six missions.

Never programmed before? Perfect, this is where you start. Each mission gives you a program that
**almost** works. Your job: figure out what it does, then fix it or finish it.

---

## How to run a mission
1. Start the simulator ([how](simulator.md#start-the-simulator)).
2. In **1. Mission**, pick the mission. Its program is picked for you.
3. Press the round **INIT** button, then **▶** (START). Watch the robot.
4. Open the program in Android Studio: **TeamCode → java → org.firstinspires.ftc.teamcode → missions**.
5. Change the code, then press **INIT** and **▶** again. The simulator loads your newest code by itself.

You've finished a mission when the field says **MISSION COMPLETE!**

## How Java reads your program
- The robot does each line **in order, top to bottom**, one at a time.
- Every instruction ends with a **semicolon** `;`
- Lines starting with `//` are **comments**: notes for people. The robot skips them.
- `sleep(1000);` means "keep doing what you're doing for 1000 milliseconds" (1 second).
- `setPower(0.5)` means half speed forward. `-0.5` is half speed backward. `0` is stop.

---

## Mission 1: First Moves
**Goal:** drive straight and stop inside the blue box.

1. **Predict:** read `M01_FirstMoves`. Where will the robot stop? Before, inside, or past the box?
2. **Run** it. Were you right?
3. **Fix:** change **one number** so the robot stops in the box.

<details><summary>💡 Hint</summary>

The robot drives while it sleeps. A longer sleep means a longer drive. The box is about three
times farther than where the robot stops now.

</details>

## Mission 2: Turn and Park
**Goal:** drive forward, turn, drive forward, and park in the box.

The program turns the **wrong way**, and its times are off.
1. **Predict:** which way does "Step 2" turn the robot? Which way does it need to turn?
2. Fix the turn first, then the two drive times. Change one thing at a time.

<details><summary>💡 Hint: turning</summary>

To spin, one side goes forward and the other goes backward. Swap which side gets the `-`
(negative) power to turn the other way.

</details>

<details><summary>💡 Hint: how far?</summary>

At power 0.5 the robot drives about 20 inches every second (1000 ms). Each floor tile is 24 inches.

</details>

## Mission 3: Square Dance
**Goal:** visit corners 1, 2, 3, then come back HOME.

The program only drives the **first side** and the first turn.
1. Turn on **Show trail** so you can see the shape you drive.
2. Add sides 2, 3 and 4. **Copy and paste** is your friend: select the lines for one side and
   one turn, press **Ctrl+C**, click where they go, press **Ctrl+V**. (On a Mac: **⌘C** and **⌘V**.)

<details><summary>💡 Hint</summary>

A square is "drive, turn" again and again. After the last side, you don't need another turn,
just stop.

</details>

## Mission 4: Scrambled
**Goal:** go through the gate, turn, and park in the box.

Every line this program needs is already there, with the right numbers. But the steps are in
the **wrong order**.
1. Read each step's comment (`// stop`, `// turn right`...) and decide the right order.
2. Move the steps. Click a line and press **Ctrl+Shift+↑** or **Ctrl+Shift+↓** to move it up or down.
   On a Mac: **⌘⇧↑** or **⌘⇧↓**.

<details><summary>💡 Hint</summary>

What should happen first, before the robot does anything at all? Look for the line that waits
for START.

</details>

## Mission 5: Bug Hunt
**Goal:** drive straight and park in the box, **without moving before START**.

This program has **three bugs**. Run it and read what the simulator tells you. Fix **one** bug,
run it again, and repeat.

<details><summary>💡 Hint for bug 1</summary>

Read the error message on the Driver Station carefully. It says which name it couldn't find. Compare it with the other
motor's name.

</details>

<details><summary>💡 Hint for bug 2</summary>

The robot starts moving as soon as you press INIT. Which line from the other missions is
missing here?

</details>

<details><summary>💡 Hint for bug 3</summary>

The robot spins instead of driving straight. To drive straight, should the two powers be the
same, or opposite?

</details>

## Mission 6: Drive It Yourself
**Goal:** drive through gate 1, then gate 2, then stop at FINISH. Beat your time!

This is a **TeleOp** program: **you** drive, with the keyboard.
- **W / S** drive the **left** wheels. **↑ / ↓** drive the **right** wheels.
- Both forward = straight. One forward and one back = spin.

Then change the code:
1. Change `maxSpeed`. What's the fastest speed you can still control?
2. Add a telemetry line that shows `maxSpeed` on the screen.
3. **Challenge:** make the robot drive with only **W A S D**.

<details><summary>💡 Hint for the challenge</summary>

`gamepad1.left_stick_x` is the A/D direction. Try
`leftPower = (-gamepad1.left_stick_y + gamepad1.left_stick_x) * maxSpeed;`
What should `rightPower` be?

</details>

---

## Done early? Challenges
- **Speed run:** in Mission 1, what happens to the time if you use power `1.0`? Can you still park?
- **Draw your initial:** pick **Free Drive**, turn on the trail, and write a program (or drive)
  that draws the first letter of your name.
- **Fastest lap:** what's your best Mission 6 time? Write it on the board.

---

# More missions (after the lessons)
These missions use what the lessons teach: sensors, loops, encoders, the claw, and arcade drive.
Each one comes with a program that **almost** works and has **two bugs**. Do each one after its
lesson. Pick the mission in the simulator, and its program is picked for you, just like on the
first day.

| Mission | Do it after | You'll practice |
|---|---|---|
| 7: Stop at the Tape | [Lesson 17](lessons/17-color-sensor.md) | Color sensor, thresholds |
| 8: Follow the Tape | [Lesson 17](lessons/17-color-sensor.md) | Following a line |
| 10: Bumper Bot | [Lesson 10](lessons/10-timer-auto.md) | Touch sensor, `while` loops |
| 11: Staircase | [Lessons 09](lessons/09-loops.md) and [11](lessons/11-methods.md) | `for` loops, `if`, methods |
| 12: Precision Park | [Lesson 12](lessons/12-encoder-drive.md) | Encoders, `RUN_TO_POSITION` |
| 13: Special Delivery | [Lesson 13](lessons/13-toggle.md) | Claw and arm |
| 14: Arcade Slalom | [Lesson 06](lessons/06-arcade-drive.md) | Arcade drive math (TeleOp) |

(Mission 9 is the [Lesson 18 Grand Challenge](lessons/18-grand-challenge.md): you write that one from scratch.)

## Mission 7: Stop at the Tape
**Goal:** drive forward and stop with the color sensor on the black tape. The tape **moves every
time you press Reset robot**, so a timer won't work.

Before you press START, read the **Brightness** on the Driver Station. Is the floor brighter or
darker than the **Threshold**? It should be brighter.

<details><summary>💡 Hint for bug 1</summary>

At gain 15 the floor reads about 0.28. What gain does this program use?

</details>

<details><summary>💡 Hint for bug 2</summary>

The comment says "keep driving while the sensor sees the **floor**". The floor is brighter than
the threshold. Does the `while` line say brighter or darker?

</details>

## Mission 8: Follow the Tape
**Goal:** follow the black tape to the END. The robot rides the tape's left edge: tape means
steer left, floor means steer right.

<details><summary>💡 Hint for bug 1</summary>

Watch the **Seeing** line on the Driver Station. Does it ever say FLOOR? The floor reads about
0.28 and the tape about 0.04. Where should the threshold be?

</details>

<details><summary>💡 Hint for bug 2</summary>

It follows the straight part, then misses the first turn. Which number decides how hard it
corrects? Try a few values. (Lesson 17 used 0.15.)

</details>

## Mission 10: Bumper Bot
**Goal:** drive until the bumper touches the wall, then back up and stop in the box.

<details><summary>💡 Hint for bug 1</summary>

The robot doesn't move at all. The loop should keep going while the bumper is **not** pressed.
In Java, "not" is `!`.

</details>

<details><summary>💡 Hint for bug 2</summary>

The comment says "back up". Which way does a power of `0.4` go?

</details>

## Mission 11: Staircase
**Goal:** climb 6 steps to the TOP: forward, turn **left**, forward, turn **right**, forward,
turn **left**... Turn on **Show trail** to see the path.

<details><summary>💡 Hint for bug 1</summary>

Count the steps the robot climbs. `for (int step = 1; step < 6; step++)` runs with step = 1,
2, 3, 4, 5. How many is that?

</details>

<details><summary>💡 Hint for bug 2</summary>

The robot goes in a circle because it always turns left. Odd steps turn left, even steps turn
right. `step % 2` is 1 for odd numbers and 0 for even numbers. Use an `if` / `else`.

</details>

## Mission 12: Precision Park
**Goal:** drive exactly 60 inches with the encoders and park in the narrow box. When it works,
tick **Low battery** and run it again. Does it still park? (A timer wouldn't.)

<details><summary>💡 Hint for bug 1</summary>

Read the error. With `RUN_TO_POSITION`, which has to come first: the target, or the mode?

</details>

<details><summary>💡 Hint for bug 2</summary>

The robot drives about 3 times too far. One turn of the wheel rolls the robot one
**circumference**, which is π × diameter. In Java, π is `Math.PI`.

</details>

## Mission 13: Special Delivery
**Goal:** grab the yellow block with the claw, carry it, and leave it in the DROP box.

<details><summary>💡 Hint for bug 1</summary>

Watch the claw when the comment says "grab". Does it close? Look at the two claw numbers at
the top.

</details>

<details><summary>💡 Hint for bug 2</summary>

The comment says "leave the block in the box", but which line makes the claw let go?

</details>

## Mission 14: Arcade Slalom
**Goal:** drive under the first wall, over the second, and stop at FINISH. **W/S** drive
forward and back, **← / →** turn. This is a TeleOp: you drive!

<details><summary>💡 Hint for bug 1</summary>

Press W. Pushing a stick **up** gives a **negative** `left_stick_y`. What has to change so up
means forward?

</details>

<details><summary>💡 Hint for bug 2</summary>

Press →. To turn right, the left wheels go faster than the right ones. Which side gets
`+ turn`?

</details>

When it works, race your best time. Then try the [Lesson 06](lessons/06-arcade-drive.md)
challenges: a slower precision mode, or making the robot easier to steer.

---
## ✅ Check yourself
- [ ] I can explain what `setPower` and `sleep` do.
- [ ] I can make the robot turn left or right on purpose.
- [ ] I can read an error message and find the line it's talking about.

**Exit ticket:** In your own words, what does "the robot runs your code in order" mean? Give an
example from today where the order mattered.

---
**What's next?** [Lesson 00](lessons/00-getting-the-code.md) sets up your group's code so you
can save your work and put it on the real robot. Then [Lesson 01](lessons/01-hello-robot.md).
