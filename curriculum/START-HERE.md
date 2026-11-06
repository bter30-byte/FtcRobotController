# 👋 Start Here: Learning Java for Robots

Welcome! If you've programmed in **Blocks**, you already know more than you think. These lessons
teach the same ideas in **Java**, the language real FTC and FRC teams use, one small step at a time.

You'll work through the lessons **mostly on your own**, at your own pace. Everything you need is
written down, with pictures. Your teacher is there when you're really stuck.

---

## How to get started
0. **Never programmed before?** Start with the [**Robot Missions**](missions.md) in the
   [simulator](simulator.md). No robot needed.
1. **Do [Lesson 00](lessons/00-getting-the-code.md) first.** It sets up your laptop, your
   group's copy of the code, and your own folder. Every step has a picture.
2. **Then go in order: 01, 02, 03…** Each lesson builds on the one before.
3. **Every class:** pull when you sit down, push before you leave (see the
   [daily routine](lessons/00-getting-the-code.md#daily-routine)).

## 🍎 On a Mac?
Everything works on a Mac too: Android Studio, GitHub Desktop, and the simulator. The lessons
show Windows keys, so swap them like this:

| Windows | Mac |
|---|---|
| **Ctrl** (Ctrl+C, Ctrl+V, Ctrl+Z, Ctrl+S) | **⌘ Command** (⌘C, ⌘V, ⌘Z, ⌘S) |
| **Ctrl+Shift+↑ / ↓** (move a line up or down) | **⌘⇧↑ / ↓** |
| **Alt+Enter** (fix it / add an import) | **⌥ Option + Return** |
| **Right-click** | Click with **two fingers** on the trackpad (or Control-click) |
| **Backspace** | **delete** |
| **Enter** | **return** |
| GitHub Desktop: **File → Options** | GitHub Desktop: **GitHub Desktop → Settings** |

When you start the simulator, its window may open **behind** Android Studio. If you don't see
it, click its icon in the Dock.

## How each lesson works
Every lesson has the same parts:

| Section | What you do |
|---|---|
| **Before you start** | Pull, then copy the lesson's program into your own folder. |
| **📖 Learn it** | Read the explanation and look at the diagram. This is the new idea. |
| **1. Predict** | Read the code and **guess** what it will do. Write it down! Guessing wrong is fine. That's how you learn. |
| **2. Run** | Put it on the robot and see if you were right. |
| **3. Investigate** | Answer questions and try small experiments to see *why* it works. |
| **4. Modify** | Make small changes to the working code. |
| **5. Make** | Build something new with the idea. |
| **✅ Check yourself** | A checklist. If you can do all of it, move on. |
| **🆘 Stuck?** | Fixes for the most common problems in that lesson. |

### Hints are hidden on purpose
Some questions have a hint you can open:

<details><summary>💡 Hint (click me)</summary>

Like this! Try the problem on your own for a few minutes **before** you open a hint. The
struggle is where the learning happens.

</details>

## When you're stuck, in this order
1. **Read the error message.** Click it in Android Studio's **Build** panel to jump to the line.
2. Check the lesson's **🆘 Stuck?** table.
3. Check the [**Blocks → Java cheat sheet**](reference/blocks-to-java.md).
4. **Ask a classmate** to look with you. Explaining your code out loud often shows you the bug.
5. **Ask your teacher.**

## Lessons
| # | Lesson | You'll learn |
|---|---|---|
| 🚀 | [Robot Missions](missions.md) | Your first programs: drive, turn, fix bugs, drive it yourself. [More missions](missions.md#more-missions-after-the-lessons) for after the lessons. |
| 00 | [Getting the Code](lessons/00-getting-the-code.md) | GitHub Desktop, Android Studio, putting code on the robot |
| 01 | [Hello, Robot!](lessons/01-hello-robot.md) | The parts of a Java program, telemetry, error messages |
| 02 | [Drive in Sequence](lessons/02-drive-sequence.md) | Making motors move, one step at a time |
| 03 | [Variables and Types](lessons/03-variables.md) | Storing values, types, math |
| 04 | [Reading the Gamepad](lessons/04-gamepad-input.md) | Gamepad input, the game loop |
| 05 | [Tank Drive](lessons/05-tank-drive.md) | Driving with two sticks |
| 06 | [Arcade Drive](lessons/06-arcade-drive.md) | Mixing numbers with math |
| 07 | [Buttons and If](lessons/07-buttons-and-if.md) | Making decisions with `if` / `else` |
| 08 | [Sensors and Logic](lessons/08-sensors-and-logic.md) | Touch sensors, AND / OR / NOT |
| 09 | [Loops](lessons/09-loops.md) | Repeating with `for` and `while` |
| 10 | [Timers](lessons/10-timer-auto.md) | Checking time and sensors while moving |
| 11 | [Methods](lessons/11-methods.md) | Writing your own reusable commands |
| 12 | [Encoders](lessons/12-encoder-drive.md) | Driving exact distances, methods that return answers |
| 13 | [Toggles](lessons/13-toggle.md) | One-tap on/off buttons, remembering the past |
| 14 | [State Machines](lessons/14-state-machine-auto.md) | Multi-step autonomous with `enum` and `switch` |
| 15 | [Robot Class: TeleOp](lessons/15-robot-class-teleop.md) | Classes and objects |
| 16 | [Robot Class: Autonomous](lessons/16-robot-class-auto.md) | Reusing one class everywhere |
| 17 | [Color Sensor](lessons/17-color-sensor.md) | Stopping at and following black tape |
| 18 | [Capstone: Your Robot, Your Code](lessons/18-grand-challenge.md) | Your own configuration and robot class, then one autonomous that uses everything |

## Reference pages
- [🖥️ Robot Simulator](simulator.md): test any lesson on your laptop, in class or at home
- [Blocks → Java cheat sheet](reference/blocks-to-java.md): every Blocks block's Java twin, plus common errors
- [Robot Configuration](reference/robot-configuration.md): what plugs in where, and where names like `"left_drive"` come from
- [Deploying and Managing Programs](reference/deploying-and-managing.md): putting code on the robot, cleaning up the menu

## Two program types you'll see
| | **TeleOp** (driver control) | **Autonomous** |
|---|---|---|
| Who's in charge | A driver with a gamepad | The program, all by itself |
| Driver Station list | The **TeleOp** list (right) | The **Autonomous** list (left) |
| Code line | `@TeleOp(...)` | `@Autonomous(...)` |
| Lessons | 01, 04–08, 13, 15 | 02, 09–12, 14, 16–18 |
