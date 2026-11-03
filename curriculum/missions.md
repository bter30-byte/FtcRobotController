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
## ✅ Check yourself
- [ ] I can explain what `setPower` and `sleep` do.
- [ ] I can make the robot turn left or right on purpose.
- [ ] I can read an error message and find the line it's talking about.

**Exit ticket:** In your own words, what does "the robot runs your code in order" mean? Give an
example from today where the order mattered.

---
**What's next?** [Lesson 00](lessons/00-getting-the-code.md) sets up your group's code so you
can save your work and put it on the real robot. Then [Lesson 01](lessons/01-hello-robot.md).
