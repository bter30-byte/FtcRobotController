"""Concept diagrams for the lesson handouts."""
from lib import *

KW = "#cf8e6d"
STR = "#6aab73"
ANN = "#b3ae60"
CMT = "#7a7e85"
TXT = "#bcbec4"
NUM = "#2aacb8"


def card(s, x, y, w, h, fill=SUBTLE, stroke=LINE, r=10):
    s.rect(x, y, w, h, fill=fill, stroke=stroke, r=r)


def box_text(s, x, y, w, h, lines, size=14, fill="#ffffff", stroke=LINE, color=INK, bold_first=True):
    s.rect(x, y, w, h, fill=fill, stroke=stroke, r=8, sw=1.5)
    total = len(lines) * size * 1.35
    yy = y + h / 2 - total / 2 + size
    for i, t in enumerate(lines):
        s.text(x + w / 2, yy, t, size=size, fill=color, anchor="middle",
               weight="bold" if (i == 0 and bold_first) else "normal")
        yy += size * 1.35


def diamond(s, cx, cy, w, h, lines, fill="#fff8c5", stroke=YELLOW, size=13):
    s.path(f"M{cx},{cy - h / 2} L{cx + w / 2},{cy} L{cx},{cy + h / 2} L{cx - w / 2},{cy} Z",
           stroke=stroke, fill=fill, sw=1.5)
    total = len(lines) * size * 1.3
    yy = cy - total / 2 + size
    for t in lines:
        s.text(cx, yy, t, size=size, fill=INK, anchor="middle", weight="600")
        yy += size * 1.3


# ---------------------------------------------------------------- L01
def opmode_anatomy():
    s = Svg(980, 590, "Anatomy of an OpMode", ui=False)
    s.text(20, 34, "The parts of every OpMode", size=20, weight="bold")
    code = [
        ([("package ", KW, "normal"), ("org.firstinspires.ftc.teamcode.students.ava;", TXT, "normal")], "folder"),
        ([("", TXT, "normal")], None),
        ([("import ", KW, "normal"), ("com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;", TXT, "normal")], "import"),
        ([("import ", KW, "normal"), ("com.qualcomm.robotcore.eventloop.opmode.TeleOp;", TXT, "normal")], "import"),
        ([("", TXT, "normal")], None),
        ([("@TeleOp", ANN, "normal"), ("(name = ", TXT, "normal"), ('"L01 Hello Robot - Ava"', STR, "normal"),
          (", group = ", TXT, "normal"), ('"Ava"', STR, "normal"), (")", TXT, "normal")], "menu"),
        ([("public class ", KW, "normal"), ("L01_HelloRobot ", TXT, "normal"), ("extends ", KW, "normal"),
          ("LinearOpMode {", TXT, "normal")], "class"),
        ([("    @Override", ANN, "normal")], None),
        ([("    public void ", KW, "normal"), ("runOpMode() {", TXT, "normal")], "run"),
        ([("        telemetry.addData(", TXT, "normal"), ('"Status"', STR, "normal"), (", ", TXT, "normal"),
          ('"Ready"', STR, "normal"), (");", TXT, "normal")], "init"),
        ([("        telemetry.update();", TXT, "normal")], "init"),
        ([("        waitForStart();", TXT, "bold")], "wait"),
        ([("        while ", KW, "normal"), ("(opModeIsActive()) {", TXT, "normal")], "loop"),
        ([("            ", TXT, "normal"), ("// runs over and over until STOP", CMT, "normal")], "loop"),
        ([("        }", TXT, "normal")], "loop"),
        ([("    }", TXT, "normal")], None),
        ([("}", TXT, "normal")], None),
    ]
    x0, y0, lh = 20, 52, 22
    s.rect(x0, y0, 560, len(code) * lh + 18, fill="#1e1f22", r=8)
    ys = {}
    for i, (segs, key) in enumerate(code):
        y = y0 + 24 + i * lh
        s.rich(x0 + 14, y, segs, size=12.5, mono=True)
        if key:
            ys.setdefault(key, []).append(y)
    labels = [
        ("folder", BLUE, "Which folder (package) this file lives in"),
        ("import", PURPLE, "Tools borrowed from the FTC SDK"),
        ("menu", GREEN, "Puts the program on the Driver Station menu"),
        ("class", BLUE, "The program’s name. Must match the file name."),
        ("run", PURPLE, "Your instructions go inside runOpMode()"),
        ("init", YELLOW, "INIT part: runs when you press INIT"),
        ("wait", RED, "Waits here until you press START"),
        ("loop", GREEN, "START part: repeats until you press STOP"),
    ]
    for key, color, txt in labels:
        top, bot = ys[key][0] - 15, ys[key][-1] + 5
        s.rect(586, top, 5, bot - top, fill=color, r=2)
        s.text(600, (top + bot) / 2 + 5, txt, size=14, fill=INK)
    # timeline
    ty = 510
    s.text(20, ty - 22, "What happens when you use the Driver Station:", size=15, weight="bold", fill=INK)
    steps = [("Press INIT", YELLOW, "code above waitForStart() runs"),
             ("Press START", GREEN, "code below it runs; the loop repeats"),
             ("Press STOP", RED, "opModeIsActive() turns false; it ends")]
    x = 20
    for i, (a, c, b) in enumerate(steps):
        s.rect(x, ty, 300, 56, fill="#ffffff", stroke=c, sw=2, r=8)
        s.text(x + 14, ty + 23, a, size=15, fill=c, weight="bold")
        s.text(x + 14, ty + 44, b, size=13, fill=INK)
        if i < 2:
            s.line(x + 304, ty + 28, x + 326, ty + 28, arrow=True)
        x += 330
    return s.save("concept-opmode-anatomy.svg")


# ---------------------------------------------------------------- L02
def robot_top(s, cx, cy, left, right, label, motion):
    """left/right: wheel power (-1..1). motion: 'up', 'spin', 'none'."""
    s.rect(cx - 40, cy - 50, 80, 100, fill="#dfe6ee", stroke=MUTED, sw=1.5, r=8)
    s.text(cx, cy + 4, "robot", size=12, fill=MUTED, anchor="middle")
    for side, p, wx in (("L", left, cx - 54), ("R", right, cx + 46)):
        s.rect(wx, cy - 30, 8, 60, fill="#57606a", r=3)
        if p:
            L = 32 * abs(p) / 0.5 if abs(p) <= 0.5 else 46
            y1, y2 = (cy + L / 2, cy - L / 2) if p > 0 else (cy - L / 2, cy + L / 2)
            ax = wx - 14 if side == "L" else wx + 22
            s.line(ax, y1, ax, y2, stroke=ACC, sw=3, arrow=True)
            s.text(ax, cy + 52 if p > 0 else cy - 40, f"{p:+.1f}", size=12, fill=ACC, anchor="middle",
                   weight="bold")
    s.text(cx, cy + 82, label, size=14, fill=INK, anchor="middle", weight="bold")
    if motion == "up":
        s.line(cx, cy - 58, cx, cy - 92, stroke=GREEN, sw=4, arrow=True)
    elif motion == "spin":
        s.path(f"M{cx - 28},{cy - 66} Q{cx},{cy - 92} {cx + 28},{cy - 66}", stroke=GREEN, sw=4, arrow=True)


def motor_power():
    s = Svg(980, 430, "Motor power from -1.0 to 1.0", ui=False)
    s.text(20, 34, "setPower(…) takes a number from −1.0 to 1.0", size=20, weight="bold")
    x0, x1, y = 80, 900, 90
    s.line(x0, y, x1, y, stroke=INK, sw=3)
    marks = [(-1.0, "full speed", "backward"), (-0.5, "half speed", "backward"), (0, "stopped", ""),
             (0.5, "half speed", "forward"), (1.0, "full speed", "forward")]
    for v, a, b in marks:
        x = x0 + (v + 1) / 2 * (x1 - x0)
        s.line(x, y - 10, x, y + 10, stroke=INK, sw=3)
        s.text(x, y - 18, f"{v:+.1f}" if v else "0", size=15, weight="bold", anchor="middle",
               fill=RED if v < 0 else (GREEN if v > 0 else INK))
        s.text(x, y + 32, a, size=13, anchor="middle", fill=INK)
        s.text(x, y + 50, b, size=13, anchor="middle", fill=MUTED)
    s.text(20, 196, "Each side of the robot has its own motor:", size=15, weight="bold")
    robot_top(s, 180, 316, 0.5, 0.5, "both +0.5", "up")
    s.text(180, 418, "drives straight", size=13, anchor="middle", fill=MUTED)
    robot_top(s, 490, 316, 0.5, -0.5, "left +0.5, right −0.5", "spin")
    s.text(490, 418, "spins in place", size=13, anchor="middle", fill=MUTED)
    robot_top(s, 800, 316, 0, 0, "both 0", "none")
    s.text(800, 418, "stops (until you change it)", size=13, anchor="middle", fill=MUTED)
    return s.save("concept-motor-power.svg")


# ---------------------------------------------------------------- L03
def variables():
    s = Svg(980, 400, "Variables are named boxes with a type", ui=False)
    s.text(20, 34, "A variable is a named box. Its type says what can go inside.", size=20, weight="bold")
    parts = [("double", BLUE, "type", "what kind of value"), ("wheelDiameter", PURPLE, "name", "what you call it"),
             ("=", MUTED, "store", "put a value in"), ("3.78", GREEN, "value", "what’s inside"),
             (";", MUTED, "end", "end of the line")]
    x = 60
    for t, c, a, b in parts:
        w = tw(t, 26, mono=True) + 20
        s.rect(x, 62, w, 46, fill="#ffffff", stroke=c, sw=2, r=6)
        s.text(x + w / 2, 94, t, size=26, mono=True, fill=c, anchor="middle", weight="bold")
        s.text(x + w / 2, 132, a, size=14, fill=c, anchor="middle", weight="bold")
        s.text(x + w / 2, 150, b, size=12, fill=MUTED, anchor="middle")
        x += w + 18
    boxes = [("int", "whole numbers", "teamNumber", "12345", BLUE),
             ("double", "decimal numbers", "wheelDiameter", "3.78", PURPLE),
             ("boolean", "true or false", "hasClaw", "true", GREEN),
             ("String", "text, in quotes", "robotName", "\"Sparky\"", YELLOW)]
    x = 40
    for typ, desc, name, val, c in boxes:
        s.rect(x, 196, 200, 26, fill=c, r=6)
        s.text(x + 100, 214, f"{typ}  —  {desc}", size=13, fill="#ffffff", anchor="middle", weight="bold")
        s.rect(x, 222, 200, 80, fill="#ffffff", stroke=c, sw=2, r=6)
        s.text(x + 100, 272, val, size=24, mono=True, fill=INK, anchor="middle", weight="bold")
        s.text(x + 100, 326, name, size=15, mono=True, fill=c, anchor="middle", weight="bold")
        x += 230
    s.rect(40, 350, 900, 38, fill="#ffebe9", stroke=RED, r=8)
    s.rich(56, 375, [("int cookies = 7.5;", RED, "bold"),
                     ("    ✖  An int box can’t hold a decimal. Java stops you before the robot runs.",
                      INK, "normal")], size=14)
    return s.save("concept-variables.svg")


# ---------------------------------------------------------------- L04
def gamepad():
    s = Svg(980, 560, "Gamepad map with Java names", ui=False)
    s.text(20, 34, "The gamepad in Java", size=20, weight="bold")
    s.text(20, 58, "Every name starts with gamepad1. (or gamepad2.)   Example: gamepad1.left_stick_y",
           size=14, fill=MUTED)
    cx, cy = 490, 320
    # triggers + bumpers
    s.rect(cx - 190, cy - 152, 90, 34, fill="#c9d1d9", stroke=MUTED, r=10)
    s.rect(cx + 100, cy - 152, 90, 34, fill="#c9d1d9", stroke=MUTED, r=10)
    s.rect(cx - 196, cy - 114, 102, 20, fill="#d0d7de", stroke=MUTED, r=8)
    s.rect(cx + 94, cy - 114, 102, 20, fill="#d0d7de", stroke=MUTED, r=8)
    # body
    s.path(f"M{cx - 200},{cy - 90} Q{cx},{cy - 120} {cx + 200},{cy - 90} "
           f"Q{cx + 260},{cy - 60} {cx + 250},{cy + 60} Q{cx + 240},{cy + 130} {cx + 180},{cy + 120} "
           f"Q{cx + 130},{cy + 110} {cx + 90},{cy + 40} L{cx - 90},{cy + 40} "
           f"Q{cx - 130},{cy + 110} {cx - 180},{cy + 120} Q{cx - 240},{cy + 130} {cx - 250},{cy + 60} "
           f"Q{cx - 260},{cy - 60} {cx - 200},{cy - 90} Z", stroke=MUTED, fill="#eaeef2", sw=2)
    # dpad
    dx, dy = cx - 160, cy - 30
    s.rect(dx - 12, dy - 34, 24, 68, fill="#57606a", r=3)
    s.rect(dx - 34, dy - 12, 68, 24, fill="#57606a", r=3)
    # buttons
    bx, by = cx + 160, cy - 30
    for lab, ox, oy, col in (("y", 0, -28, "#d4a72c"), ("x", -28, 0, "#0969da"), ("b", 28, 0, "#cf222e"),
                             ("a", 0, 28, "#1a7f37")):
        s.circle(bx + ox, by + oy, 13, fill=col)
        s.text(bx + ox, by + oy + 5, lab.upper(), size=13, fill="#ffffff", anchor="middle", weight="bold")
    # sticks
    for sx in (cx - 80, cx + 80):
        s.circle(sx, cy + 30, 30, fill="#57606a")
        s.circle(sx, cy + 30, 18, fill="#8c959f")

    def lab(x, y, lines, anchor="start", color=INK):
        for i, t in enumerate(lines):
            s.text(x, y + i * 18, t, size=13.5, fill=color if i == 0 else MUTED,
                   weight="bold" if i == 0 else "normal", anchor=anchor, mono=(i == 0))

    # left side labels
    s.line(cx - 145, cy - 135, 250, 112, stroke=MUTED, sw=1)
    lab(40, 92, ["left_trigger", "0.0 (up) → 1.0 (pressed)"])
    s.line(cx - 160, cy - 104, 250, 172, stroke=MUTED, sw=1)
    lab(40, 162, ["left_bumper", "true / false"])
    s.line(dx - 36, dy, 250, 258, stroke=MUTED, sw=1)
    lab(40, 236, ["dpad_up / dpad_down", "dpad_left / dpad_right", "true / false"])
    s.line(cx - 110, cy + 40, 250, 424, stroke=MUTED, sw=1)
    lab(40, 388, ["left_stick_x", "left −1 … right +1"])
    lab(40, 444, ["left_stick_y", "UP = −1 (!)  …  down = +1"], color=RED)
    # right side labels
    s.line(cx + 145, cy - 135, 730, 112, stroke=MUTED, sw=1)
    lab(740, 92, ["right_trigger", "0.0 (up) → 1.0 (pressed)"])
    s.line(cx + 160, cy - 104, 730, 172, stroke=MUTED, sw=1)
    lab(740, 162, ["right_bumper", "true / false"])
    s.line(bx + 44, by, 730, 258, stroke=MUTED, sw=1)
    lab(740, 236, ["a  b  x  y", "true / false", "PlayStation pads: a = cross,",
                   "b = circle, x = square, y = triangle"])
    s.line(cx + 110, cy + 40, 730, 424, stroke=MUTED, sw=1)
    lab(740, 388, ["right_stick_x", "left −1 … right +1"])
    lab(740, 444, ["right_stick_y", "UP = −1 (!)  …  down = +1"], color=RED)
    s.rect(150, 500, 680, 44, fill="#ffebe9", stroke=RED, r=8)
    s.text(490, 528, "Pushing a stick UP gives a NEGATIVE number. That’s why we write -gamepad1.left_stick_y.",
           size=13, fill=INK, anchor="middle")
    return s.save("concept-gamepad.svg")


def game_loop():
    s = Svg(820, 520, "The game loop", ui=False)
    s.text(20, 34, "The game loop: every TeleOp works like this", size=20, weight="bold")
    cx, cy = 410, 280
    box_text(s, cx - 70, 54, 140, 36, ["START pressed"], fill="#dafbe1", stroke=GREEN)
    s.line(cx, 92, cx, 116, arrow=True)
    pos = {
        "READ": (cx - 110, 120, ["1  READ", "gamepad sticks,", "buttons, sensors"], "#ddf4ff", BLUE),
        "DECIDE": (cx + 150, cy - 50, ["2  DECIDE", "math, if / else"], "#fbefff", PURPLE),
        "ACT": (cx - 110, cy + 90, ["3  ACT", "setPower(),", "setPosition()"], "#fff1e5", ACC),
        "SHOW": (cx - 370, cy - 50, ["4  SHOW", "telemetry.update()"], "#fff8c5", YELLOW),
    }
    for k, (x, y, lines, fill, st) in pos.items():
        box_text(s, x, y, 220, 84, lines, fill=fill, stroke=st)
    s.path(f"M{cx + 112},{162} Q{cx + 250},{170} {cx + 258},{cy - 54}", arrow=True)
    s.path(f"M{cx + 258},{cy + 38} Q{cx + 250},{cy + 120} {cx + 114},{cy + 128}", arrow=True)
    s.path(f"M{cx - 112},{cy + 128} Q{cx - 250},{cy + 120} {cx - 258},{cy + 38}", arrow=True)
    s.path(f"M{cx - 258},{cy - 54} Q{cx - 250},{170} {cx - 114},{162}", arrow=True)
    s.text(cx, cy - 2, "while (opModeIsActive())", size=15, mono=True, anchor="middle", weight="bold")
    s.text(cx, cy + 22, "repeats about 50+ times every second", size=13, anchor="middle", fill=MUTED)
    s.rect(40, 470, 740, 36, fill="#ffebe9", stroke=RED, r=8)
    s.text(410, 494, "STOP pressed → opModeIsActive() becomes false → the loop ends → the program ends",
           size=14, anchor="middle")
    return s.save("concept-game-loop.svg")


# ---------------------------------------------------------------- L06
def arcade():
    s = Svg(980, 420, "Arcade drive: mixing drive and turn", ui=False)
    s.text(20, 34, "Arcade drive mixes two numbers into two motor powers", size=20, weight="bold")
    s.rich(20, 64, [("leftPower = drive + turn", BLUE, "bold"), ("      ", INK, "normal"),
                    ("rightPower = drive − turn", PURPLE, "bold")], size=17, mono=True)
    cards = [(0.5, 0.0, "up"), (0.0, 0.5, "spin"), (0.6, 0.2, "curve")]
    x = 30
    for d, t, mot in cards:
        L, R = d + t, d - t
        card(s, x, 90, 290, 310)
        s.text(x + 145, 118, f"drive = {d:.1f}    turn = {t:.1f}", size=14, mono=True, anchor="middle",
               weight="bold")
        s.text(x + 145, 142, f"left = {L:.1f}    right = {R:.1f}", size=14, mono=True, anchor="middle",
               fill=ACC, weight="bold")
        cx, cy = x + 145, 262
        robot_top(s, cx, cy, round(L, 2), round(R, 2), "", "none")
        if mot == "up":
            s.line(cx, cy - 58, cx, cy - 98, stroke=GREEN, sw=4, arrow=True)
            res = "drives straight"
        elif mot == "spin":
            s.path(f"M{cx - 28},{cy - 66} Q{cx},{cy - 92} {cx + 28},{cy - 66}", stroke=GREEN, sw=4, arrow=True)
            res = "spins in place"
        else:
            s.path(f"M{cx - 10},{cy - 58} Q{cx},{cy - 98} {cx + 40},{cy - 100}", stroke=GREEN, sw=4, arrow=True)
            res = "curves to the right"
        s.text(cx, 388, res, size=15, anchor="middle", weight="bold", fill=GREEN)
        x += 320
    return s.save("concept-arcade.svg")


# ---------------------------------------------------------------- L07
def if_else():
    s = Svg(820, 640, "if / else if / else flowchart", ui=False)
    s.text(20, 34, "if / else if / else: only ONE branch runs", size=20, weight="bold")
    cx = 230
    qs = [("dpad_up", "gear = FAST"), ("dpad_right", "gear = MEDIUM"), ("dpad_down", "gear = SLOW")]
    y = 110
    for i, (q, act) in enumerate(qs):
        diamond(s, cx, y, 220, 84, [f"is {q}", "pressed?"])
        s.line(cx + 110, y, 470, y, stroke=GREEN, sw=2, arrow=True)
        s.text(380, y - 8, "true", size=13, fill=GREEN, weight="bold", anchor="middle")
        box_text(s, 472, y - 24, 180, 48, [act], fill="#dafbe1", stroke=GREEN)
        s.line(652, y, 720, y, stroke=MUTED, sw=2)
        s.line(cx, y + 42, cx, y + 78, stroke=RED, sw=2, arrow=True)
        s.text(cx + 12, y + 66, "false", size=13, fill=RED, weight="bold")
        y += 120
    box_text(s, cx - 110, y - 40, 220, 48, ["no change:", "keep the old gear"], fill="#f6f8fa", bold_first=False)
    s.line(720, 110, 720, y + 70, stroke=MUTED, sw=2)
    s.line(cx, y + 8, cx, y + 70, stroke=MUTED, sw=2)
    s.line(cx, y + 70, 720, y + 70, stroke=MUTED, sw=2)
    s.line(475, y + 70, 475, y + 98, stroke=MUTED, sw=2, arrow=True)
    box_text(s, 365, y + 100, 220, 40, ["drive using the gear"], fill="#ddf4ff", stroke=BLUE, bold_first=False)
    s.text(560, 180, "As soon as one answer is", size=13, fill=MUTED)
    s.text(560, 198, "true, the rest are skipped.", size=13, fill=MUTED)
    return s.save("concept-if-else.svg")


# ---------------------------------------------------------------- L09
def for_loop():
    s = Svg(980, 430, "Anatomy of a for loop", ui=False)
    s.text(20, 34, "Anatomy of a for loop", size=20, weight="bold")
    segs = [("for (", INK, "1"), ("int i = 0", BLUE, "1"), ("; ", INK, None), ("i < 3", GREEN, "2"),
            ("; ", INK, None), ("i++", PURPLE, "4"), (") {", INK, None)]
    x = 60
    centers = {}
    for t, c, n in segs:
        w = tw(t, 24, mono=True)
        s.text(x, 90, t, size=24, mono=True, fill=c, weight="bold")
        if n and t not in ("for (",):
            centers[n] = x + w / 2
        x += w
    s.text(110, 130, "    telemetry.addData(\"i\", i);   ", size=20, mono=True, fill=ACC, weight="bold")
    s.text(60, 166, "}", size=24, mono=True, weight="bold")
    s.callout(3, 92, 124)
    notes = {"1": ("START: runs once", BLUE), "2": ("CHECK: keep going?", GREEN),
             "4": ("AFTER each lap: add 1", PURPLE)}
    for n, (t, c) in notes.items():
        s.callout(n, centers[n], 54)
        s.text(centers[n], 30 + 4, "", size=1)
    s.text(540, 70, "1  START: runs once, before anything else", size=14, fill=BLUE, weight="bold")
    s.text(540, 94, "2  CHECK: if true, do the body; if false, leave", size=14, fill=GREEN, weight="bold")
    s.text(540, 118, "3  BODY: the code inside { }", size=14, fill=ACC, weight="bold")
    s.text(540, 142, "4  AFTER each lap: i++ adds 1, then CHECK again", size=14, fill=PURPLE, weight="bold")
    # trace
    s.text(60, 214, "Trace it:", size=16, weight="bold")
    rows = [("i = 0", "0 < 3 ?  true", "body runs (shows 0)"), ("i = 1", "1 < 3 ?  true", "body runs (shows 1)"),
            ("i = 2", "2 < 3 ?  true", "body runs (shows 2)"), ("i = 3", "3 < 3 ?  false", "loop ends")]
    y = 236
    for i, (a, b, c) in enumerate(rows):
        fill = "#ffebe9" if i == 3 else "#ffffff"
        s.rect(60, y, 600, 36, fill=fill, stroke=LINE, r=4)
        s.text(76, y + 24, a, size=15, mono=True, fill=BLUE, weight="bold")
        s.text(196, y + 24, b, size=15, mono=True, fill=GREEN if i < 3 else RED, weight="bold")
        s.text(400, y + 24, c, size=15, fill=INK)
        y += 40
    s.text(690, 260, "Runs 3 times: i is 0, 1, 2.", size=15, fill=INK, weight="bold")
    s.text(690, 284, "Counting starts at 0, and", size=14, fill=MUTED)
    s.text(690, 304, "“< 3” stops BEFORE 3.", size=14, fill=MUTED)
    s.text(690, 340, "Blocks version:", size=14, fill=INK, weight="bold")
    s.text(690, 362, "“repeat 3 times”", size=14, fill=MUTED)
    return s.save("concept-for-loop.svg")


# ---------------------------------------------------------------- L10
def timer_loop():
    s = Svg(980, 470, "A loop that checks time and a sensor", ui=False)
    s.text(20, 34, "Don’t sleep. Check every pass of the loop.", size=20, weight="bold")
    box_text(s, 40, 70, 260, 56, ["timer.reset()", "motors ON"], fill="#dafbe1", stroke=GREEN)
    s.line(300, 98, 360, 98, arrow=True)
    diamond(s, 560, 98, 400, 150, ["opModeIsActive()", "AND  timer.seconds() < 3", "AND  NOT bumper.isPressed()"],
            size=14)
    s.line(760, 98, 820, 98, stroke=GREEN, sw=2)
    s.text(790, 88, "all true", size=13, fill=GREEN, weight="bold", anchor="middle")
    box_text(s, 820, 70, 140, 56, ["update", "telemetry"], fill="#ddf4ff", stroke=BLUE, bold_first=False)
    s.path("M890,126 L890,226 L560,226 L560,176", stroke=MUTED, sw=2, arrow=True)
    s.text(725, 244, "check again (many times per second)", size=13, fill=MUTED, anchor="middle")
    s.line(360, 98, 360, 98)
    s.path("M560,176 L560,176")
    s.line(458, 137, 300, 300, stroke=RED, sw=2, arrow=True)
    s.text(392, 230, "any one false", size=13, fill=RED, weight="bold")
    box_text(s, 160, 302, 220, 50, ["motors OFF"], fill="#ffebe9", stroke=RED)
    s.line(270, 352, 270, 380, arrow=True)
    box_text(s, 60, 382, 860, 70, ["Why did it stop? Check each reason with if / else if / else:",
                                   "bumper pressed?  →  “Bumper hit something”     time up?  →  "
                                   "“Time ran out”     otherwise  →  “STOP pressed”"],
             size=14, fill="#ffffff")
    s.text(560, 300, "With sleep(3000) the robot is BLIND for 3 seconds.", size=14, fill=INK, weight="bold")
    s.text(560, 322, "With this loop it checks the time AND the sensor", size=14, fill=MUTED)
    s.text(560, 342, "dozens of times every second.", size=14, fill=MUTED)
    return s.save("concept-timer-loop.svg")


# ---------------------------------------------------------------- L11 / L12
def method_anatomy():
    s = Svg(980, 520, "Anatomy of a method", ui=False)
    s.text(20, 34, "A method is a named chunk of code you can reuse", size=20, weight="bold")
    segs = [("private ", BLUE, "who can use it", "only this file", 0),
            ("void ", PURPLE, "gives back", "void = nothing", 1),
            ("driveForward", GREEN, "name", "a verb: what it does", 0),
            ("(double power, long timeMs)", ACC, "parameters", "inputs, each with a type", 1),
            (" {", INK, None, None, 0)]
    x = 40
    for t, c, a, b, row in segs:
        w = tw(t, 21, mono=True)
        s.text(x, 104, t, size=21, mono=True, fill=c, weight="bold")
        if a:
            ly = 140 + row * 42
            s.line(x + 4, 114, x + w - 8, 114, stroke=c, sw=3)
            if row:
                s.line(x + w / 2, 116, x + w / 2, ly - 16, stroke=c, sw=1.5)
            s.text(x + w / 2, ly, a, size=14, fill=c, anchor="middle", weight="bold")
            s.text(x + w / 2, ly + 18, b, size=12, fill=MUTED, anchor="middle")
        x += w
    s.text(40, 236, "    setDrivePower(power, power);", size=17, mono=True, fill=INK)
    s.text(40, 260, "    sleep(timeMs);", size=17, mono=True, fill=INK)
    s.text(40, 284, "}", size=17, mono=True, fill=INK)
    # call
    s.text(40, 336, "Calling it:", size=16, weight="bold")
    s.rich(40, 372, [("driveForward(", GREEN, "bold"), ("0.5", ACC, "bold"), (", ", INK, "bold"),
                     ("1000", ACC, "bold"), (");", GREEN, "bold")], size=21, mono=True)
    s.path("M228,352 C250,260 440,240 470,124", stroke=ACC, sw=2, arrow=True, dash="5 4")
    s.path("M296,352 C330,280 600,250 628,124", stroke=ACC, sw=2, arrow=True, dash="5 4")
    s.text(340, 372, "← the values you pass in are called ARGUMENTS", size=14, fill=MUTED)
    # return example
    card(s, 40, 410, 900, 86)
    s.rich(60, 442, [("int ", PURPLE, "bold"), ("inchesToTicks", GREEN, "bold"), ("(double inches) {  ", ACC, "bold"),
                     ("return ", PURPLE, "bold"), ("(int) (inches * TICKS_PER_INCH);  }", INK, "normal")],
           size=16, mono=True)
    s.text(60, 476, "Not void? Then the method RETURNS an answer of that type, here an int.", size=14, fill=MUTED)
    return s.save("concept-method.svg")


def encoder():
    s = Svg(980, 420, "Encoders: from ticks to inches", ui=False)
    s.text(20, 34, "Encoders count how far the wheel turns", size=20, weight="bold")
    # motor + encoder
    card(s, 30, 60, 280, 300)
    s.rect(70, 140, 120, 70, fill="#57606a", r=8)
    s.text(130, 182, "motor", size=14, fill="#ffffff", anchor="middle", weight="bold")
    s.circle(220, 175, 30, fill="#ffffff", stroke=ACC, sw=3)
    for i in range(12):
        import math
        a = i * math.pi / 6
        s.line(220 + 20 * math.cos(a), 175 + 20 * math.sin(a), 220 + 28 * math.cos(a), 175 + 28 * math.sin(a),
               stroke=ACC, sw=2)
    s.text(170, 250, "The encoder counts “ticks”", size=14, anchor="middle", weight="bold")
    s.text(170, 272, "as the shaft spins.", size=14, anchor="middle")
    s.text(170, 306, "e.g. 537.7 ticks = 1 rotation", size=13, anchor="middle", fill=MUTED)
    s.text(170, 326, "(look up YOUR motor)", size=13, anchor="middle", fill=MUTED)
    # wheel
    card(s, 340, 60, 280, 300)
    s.circle(480, 170, 70, fill="#ffffff", stroke=INK, sw=6)
    s.circle(480, 170, 8, fill=INK)
    s.line(410, 170, 550, 170, stroke=BLUE, sw=2)
    s.text(480, 162, "diameter", size=12, fill=BLUE, anchor="middle")
    s.path("M420,120 A78,78 0 0 1 540,120", stroke=GREEN, sw=3, arrow=True)
    s.text(480, 278, "One rotation rolls the robot", size=14, anchor="middle", weight="bold")
    s.text(480, 300, "π × diameter inches", size=14, anchor="middle")
    s.text(480, 326, "(the wheel’s circumference)", size=13, anchor="middle", fill=MUTED)
    # formula
    card(s, 650, 60, 300, 300)
    s.text(800, 100, "Put them together", size=15, anchor="middle", weight="bold")
    s.text(800, 140, "ticks per inch =", size=15, anchor="middle", mono=True)
    s.text(800, 168, "ticks per rotation", size=15, anchor="middle", mono=True, fill=ACC)
    s.line(700, 178, 900, 178, stroke=INK, sw=2)
    s.text(800, 200, "π × diameter", size=15, anchor="middle", mono=True, fill=GREEN)
    s.text(800, 250, "To drive 24 inches:", size=14, anchor="middle", weight="bold")
    s.text(800, 274, "target = 24 × ticks per inch", size=13, anchor="middle", mono=True)
    s.text(800, 310, "Battery low? Still 24 inches.", size=13, anchor="middle", fill=MUTED)
    s.text(800, 330, "(Time-based driving can’t say that.)", size=13, anchor="middle", fill=MUTED)
    s.text(490, 395, "Gears between the motor and wheel? Multiply ticks per rotation by the gear ratio.",
           size=14, anchor="middle", fill=MUTED)
    return s.save("concept-encoder.svg")


# ---------------------------------------------------------------- L13
def edge_detection():
    s = Svg(980, 420, "Edge detection timing diagram", ui=False)
    s.text(20, 34, "Why a toggle needs edge detection", size=20, weight="bold")
    s.text(20, 58, "The loop runs many times while your finger is on the button. Each column is one pass of the loop.",
           size=14, fill=MUTED)
    held = [0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0]
    x0, cw = 340, 50
    for i in range(len(held)):
        s.text(x0 + i * cw + cw / 2, 92, str(i + 1), size=12, fill=MUTED, anchor="middle")
    s.text(x0 - 12, 92, "loop #", size=12, fill=MUTED, anchor="end")
    rows = [("A button held?", None), ("naive:  if (a) flip", "naive"), ("edge:  if (a && !aWasDown) flip", "edge")]
    y = 110
    for label, kind in rows:
        s.text(x0 - 12, y + 30, label, size=14, anchor="end", weight="bold", mono=(kind is not None))
        state = False
        prev = 0
        for i, h in enumerate(held):
            x = x0 + i * cw
            if kind is None:
                s.rect(x + 2, y + 6, cw - 4, 40, fill=GREEN if h else "#eaeef2", r=4)
                s.text(x + cw / 2, y + 32, "down" if h else "up", size=12, anchor="middle",
                       fill="#ffffff" if h else MUTED, weight="bold")
            else:
                flip = (h and kind == "naive") or (h and not prev and kind == "edge")
                if flip:
                    state = not state
                s.rect(x + 2, y + 6, cw - 4, 40, fill="#fff1e5" if state else "#ffffff",
                       stroke=ACC if flip else LINE, sw=3 if flip else 1, r=4)
                s.text(x + cw / 2, y + 32, "OPEN" if state else "shut", size=11, anchor="middle",
                       fill=ACC if state else MUTED, weight="bold")
            prev = h
        y += 70
    s.text(x0, y + 26, "Orange outline = the claw flipped on that pass.", size=14, fill=INK)
    s.text(x0, y + 50, "Naive: flips on EVERY pass while held (5 flips!), so it lands randomly.", size=14, fill=RED)
    s.text(x0, y + 74, "Edge: flips ONCE, at the pass where the button goes from up to down.", size=14, fill=GREEN)
    s.line(x0 + 2 * cw + cw / 2, 104, x0 + 2 * cw + cw / 2, 116 + 140 + 40, stroke=ACC, sw=2, dash="4 4")
    s.text(x0 + 2 * cw + cw / 2 + 6, 104, "rising edge", size=12, fill=ACC, weight="bold")
    return s.save("concept-edge-detection.svg")


# ---------------------------------------------------------------- L14
def state_machine():
    s = Svg(980, 380, "State machine example: a traffic light", ui=False)
    s.text(20, 34, "A state machine: named states + rules for moving between them", size=20, weight="bold")
    s.text(20, 58, "Example: a traffic light. It is always in exactly ONE state.", size=14, fill=MUTED)
    states = [("GREEN", "#1a7f37", 180), ("YELLOW", "#bf8700", 490), ("RED", "#cf222e", 800)]
    for name, c, x in states:
        s.circle(x, 170, 62, fill="#ffffff", stroke=c, sw=4)
        s.text(x, 176, name, size=17, fill=c, anchor="middle", weight="bold", mono=True)
    s.line(246, 170, 422, 170, sw=2.5, arrow=True)
    s.text(334, 158, "after 10 s", size=14, anchor="middle", fill=INK, weight="bold")
    s.line(556, 170, 732, 170, sw=2.5, arrow=True)
    s.text(644, 158, "after 3 s", size=14, anchor="middle", fill=INK, weight="bold")
    s.path("M800,234 C780,320 200,320 180,236", sw=2.5, arrow=True)
    s.text(490, 322, "after 10 s", size=14, anchor="middle", fill=INK, weight="bold")
    s.line(60, 170, 114, 170, sw=2.5, arrow=True)
    s.text(40, 165, "start", size=13, fill=MUTED)
    s.text(20, 362, "In code: an enum lists the states, a switch runs the current state, and a timer decides "
                    "when to move on.", size=14, fill=INK)
    return s.save("concept-state-machine.svg")


# ---------------------------------------------------------------- L15 / L16
def class_object():
    s = Svg(980, 470, "A class is a blueprint; objects are built from it", ui=False)
    s.text(20, 34, "A class is a blueprint. An object is built from it.", size=20, weight="bold")
    # blueprint
    s.rect(30, 60, 380, 380, fill="#ddf4ff", stroke=BLUE, sw=2, r=10)
    s.text(50, 92, "class RobotHardware", size=18, mono=True, fill=BLUE, weight="bold")
    s.text(50, 112, "(the blueprint: RobotHardware.java)", size=13, fill=MUTED)
    s.rect(46, 128, 348, 130, fill="#ffffff", stroke=LINE, r=8)
    s.text(60, 152, "private  (hidden inside)", size=14, fill=RED, weight="bold")
    s.lines(76, 178, ["leftDrive, rightDrive", "arm, claw", "armTopLimit"], size=14, mono=True, fill=INK, lh=22)
    s.rect(46, 272, 348, 150, fill="#ffffff", stroke=LINE, r=8)
    s.text(60, 296, "public  (anyone can use)", size=14, fill=GREEN, weight="bold")
    s.lines(76, 322, ["init()", "arcadeDrive(drive, turn, speed)", "setArmPower(power)", "setClawOpen(open)",
                      "encoderDrive(…)"], size=14, mono=True, fill=INK, lh=20)
    # arrows to objects
    for i, (title, use) in enumerate((("in your TeleOp", "robot.arcadeDrive(…)"),
                                      ("in your Autonomous", "robot.encoderDrive(…)"))):
        y = 80 + i * 200
        s.path(f"M412,{200 + i * 60} C480,{200 + i * 60} 500,{y + 60} 560,{y + 60}", stroke=BLUE, sw=2, arrow=True)
        ly = y + 30 if i == 0 else y + 120
        s.text(486, ly, "new", size=12, mono=True, fill=BLUE, anchor="middle")
        s.text(486, ly + 15, "RobotHardware(this)", size=12, mono=True, fill=BLUE, anchor="middle")
        s.rect(562, y, 390, 140, fill="#dafbe1", stroke=GREEN, sw=2, r=10)
        s.text(582, y + 30, "robot", size=18, mono=True, fill=GREEN, weight="bold")
        s.text(646, y + 30, f"(an object {title})", size=13, fill=MUTED)
        s.text(582, y + 66, use, size=15, mono=True, fill=INK)
        s.text(582, y + 92, "✔ uses the public methods", size=13, fill=GREEN)
        if i == 0:
            s.text(582, y + 118, "robot.leftDrive.setPower(1);   ✖ private!", size=13, mono=True, fill=RED)
    return s.save("concept-class-object.svg")



# ---------------------------------------------------------------- configuration
def config_names():
    s = Svg(980, 400, "Where device names come from", ui=False)
    s.text(20, 34, "One motor, four names: where \u201cleft_drive\u201d comes from", size=20, weight="bold")
    boxes = [
        ("1  PORT", "Motor 0", "Where the cable plugs in", "on the Control Hub", "#6e7781"),
        ("2  CONFIGURATION NAME", '"left_drive"', "Typed on the Driver Station", "(Configure Robot)", BLUE),
        ("3  NAME IN CODE", 'hardwareMap.get(DcMotor.class,', '"left_drive")', "", PURPLE),
        ("4  JAVA VARIABLE", "leftDrive", "Any name you like,", "used in the rest of the program", GREEN),
    ]
    x = 20
    for i, (title, big, l1, l2, c) in enumerate(boxes):
        s.rect(x, 64, 214, 170, fill="#ffffff", stroke=c, sw=2.5, r=10)
        s.rect(x, 64, 214, 32, fill=c, r=10)
        s.rect(x, 84, 214, 12, fill=c)
        s.text(x + 107, 86, title, size=13, fill="#ffffff", weight="bold", anchor="middle")
        if i == 2:
            s.text(x + 107, 134, big, size=12, fill=INK, anchor="middle", mono=True)
            s.text(x + 107, 154, l1, size=12, fill=PURPLE, anchor="middle", mono=True, weight="bold")
            s.text(x + 107, 200, "The string in quotes", size=13, fill=MUTED, anchor="middle")
            s.text(x + 107, 218, "finds the device", size=13, fill=MUTED, anchor="middle")
        else:
            s.text(x + 107, 146, big, size=21, fill=c, anchor="middle", mono=True, weight="bold")
            s.text(x + 107, 200, l1, size=13, fill=MUTED, anchor="middle")
            s.text(x + 107, 218, l2, size=13, fill=MUTED, anchor="middle")
        if i < 3:
            s.line(x + 217, 150, x + 237, 150, sw=2.5, arrow=True)
        x += 240
    # must match bracket between 2 and 3
    s.path("M367,248 L367,262 L607,262 L607,248", stroke=RED, sw=2.5)
    s.text(487, 284, "2 and 3 must match EXACTLY (capitals and _ count)", size=15, fill=RED, weight="bold",
           anchor="middle")
    s.path("M800,248 L800,262 L894,262 L894,248", stroke=GREEN, sw=2.5)
    s.text(847, 284, "your choice", size=15, fill=GREEN, weight="bold", anchor="middle")
    s.rich(20, 330, [("In code:   ", INK, "bold"), ("DcMotor ", KW_LIGHT, "bold"), ("leftDrive", GREEN, "bold"),
                     (" = hardwareMap.get(DcMotor.class, ", INK, "normal"), ('"left_drive"', PURPLE, "bold"),
                     (");", INK, "normal")], size=16, mono=True)
    s.text(20, 368, "If 2 and 3 don't match, the program stops at INIT with: Unable to find a hardware device "
                    "with name \"...\"", size=14, fill=MUTED)
    return s.save("concept-config-names.svg")


KW_LIGHT = "#9a6700"


def control_hub():
    s = Svg(980, 560, "Control Hub ports and what plugs in where", ui=False)
    s.text(20, 34, "The course robot: what plugs in where", size=20, weight="bold")
    s.text(20, 56, "Simplified drawing of a REV Control Hub. Port numbers are printed on the hub next to each port.",
           size=13, fill=MUTED, italic=True)
    hx, hy, hw, hh = 340, 110, 300, 380
    s.rect(hx, hy, hw, hh, fill="#2b2d30", stroke="#111111", sw=2, r=14)
    s.text(hx + hw / 2, hy + 30, "REV Control Hub", size=17, fill="#ffffff", weight="bold", anchor="middle")
    s.text(hx + hw / 2, hy + 50, "(built-in IMU on I2C bus 0)", size=12, fill="#afb8c1", anchor="middle")

    def port(px, py, label, used):
        s.rect(px, py, 86, 26, fill="#57606a" if not used else "#ffffff", stroke="#111111", r=4)
        s.text(px + 43, py + 18, label, size=12, fill="#ffffff" if not used else INK, anchor="middle",
               weight="bold" if used else "normal")

    # left side: motor ports
    s.text(hx + 14, hy + 84, "MOTOR", size=12, fill="#afb8c1", weight="bold")
    for i in range(4):
        port(hx + 14, hy + 94 + i * 34, "Motor %d" % i, i < 3)
    s.text(hx + 14, hy + 250, "SERVO", size=12, fill="#afb8c1", weight="bold")
    for i in range(3):
        port(hx + 14, hy + 260 + i * 34, "Servo %d" % i, i == 0)
    s.text(hx + 14, hy + 368, "(Servo 3-5 too)", size=11, fill="#afb8c1")
    # right side: digital + I2C
    s.text(hx + 200, hy + 84, "DIGITAL", size=12, fill="#afb8c1", weight="bold")
    for i in range(3):
        port(hx + 200, hy + 94 + i * 34, "Digital %d-%d" % (2 * i, 2 * i + 1), i == 0)
    s.text(hx + 200, hy + 250, "I2C", size=12, fill="#afb8c1", weight="bold")
    for i in range(3):
        port(hx + 200, hy + 260 + i * 34, "I2C %d" % i, i == 1)
    s.text(hx + 200, hy + 368, "(I2C 3 too)", size=11, fill="#afb8c1")

    def device(dx, dy, name, kind, note, color):
        s.rect(dx, dy, 250, 54, fill="#ffffff", stroke=color, sw=2, r=8)
        s.text(dx + 12, dy + 22, name, size=16, fill=color, weight="bold", mono=True)
        s.text(dx + 12, dy + 42, kind, size=12, fill=MUTED)
        if note:
            s.text(dx + 12, dy + 70, note, size=11.5, fill=MUTED, italic=True)

    # left devices
    device(30, 120, "left_drive", "drive motor + encoder cable", "", BLUE)
    s.line(280, 147, hx + 14, hy + 107, stroke=BLUE, sw=2.5)
    device(30, 196, "right_drive", "drive motor + encoder cable", "", BLUE)
    s.line(280, 223, hx + 14, hy + 141, stroke=BLUE, sw=2.5)
    device(30, 272, "arm", "arm motor + encoder cable", "", BLUE)
    s.line(280, 299, hx + 14, hy + 175, stroke=BLUE, sw=2.5)
    device(30, 380, "claw", "servo", "black/brown wire toward the - (ground) mark", PURPLE)
    s.line(280, 407, hx + 14, hy + 273, stroke=PURPLE, sw=2.5)
    # right devices
    device(700, 120, "touch_sensor", "REV Touch Sensor, Digital port 0-1", "configure it as channel 1 (the odd one)",
           YELLOW)
    s.line(700, 147, hx + 286, hy + 107, stroke=YELLOW, sw=2.5)
    device(700, 300, "sensor_color", "REV Color Sensor V3, I2C bus 1", "about 1 cm above the floor, looking down",
           GREEN)
    s.line(700, 327, hx + 286, hy + 307, stroke=GREEN, sw=2.5)
    s.text(490, 528, "White ports = used by the course robot.  Each motor's encoder cable plugs in next to its "
                     "motor port, with the same number.", size=13, fill=INK, anchor="middle")
    return s.save("concept-control-hub.svg")


def color_sensor():
    s = Svg(980, 470, "How a color sensor sees black tape", ui=False)
    s.text(20, 34, "The color sensor measures how much light bounces back", size=20, weight="bold")
    # side views
    for i, (label, floor, val, refl) in enumerate((("Over the gray floor", "#c9ced4", "0.28", 4),
                                                  ("Over black tape", "#1b1b1b", "0.04", 1))):
        x0 = 40 + i * 330
        s.rect(x0, 210, 280, 26, fill=floor, stroke=MUTED, r=3)
        s.rect(x0 + 100, 90, 80, 40, fill="#2b2d30", r=6)
        s.text(x0 + 140, 116, "sensor", size=12, fill="#ffffff", anchor="middle")
        s.line(x0 + 128, 132, x0 + 128, 204, stroke="#f2c200", sw=3, arrow=True)
        for k in range(refl):
            s.line(x0 + 150 + k * 6, 204, x0 + 150 + k * 6, 136, stroke="#f2c200", sw=2, arrow=True,
                   dash="4 3")
        s.text(x0 + 140, 72, label, size=15, fill=INK, weight="bold", anchor="middle")
        s.text(x0 + 140, 270, "brightness (alpha)", size=13, fill=MUTED, anchor="middle")
        s.text(x0 + 140, 296, "about " + val, size=20, fill=INK, weight="bold", anchor="middle", mono=True)
    # threshold number line
    x0, x1, y = 40, 600, 380
    s.line(x0, y, x1, y, stroke=INK, sw=3)
    for v, lab, c in ((0.0, "0.0 black", INK), (0.04, "", INK), (0.15, "", RED), (0.28, "", INK), (1.0, "1.0 white", INK)):
        px = x0 + (x1 - x0) * (v / 0.5 if v <= 0.5 else 1)
        if v == 1.0:
            px = x1
        s.line(px, y - 8, px, y + 8, stroke=c, sw=3)
        if lab:
            s.text(px, y + 26, lab, size=12, fill=MUTED, anchor="middle")
    def tick(v, text, color, up):
        px = x0 + (x1 - x0) * (v / 0.5)
        s.text(px, y - 16 if up else y + 44, text, size=13, fill=color, weight="bold", anchor="middle")
    tick(0.04, "tape 0.04", INK, True)
    tick(0.28, "floor 0.28", INK, True)
    tick(0.15, "THRESHOLD 0.15", RED, False)
    s.text(x1 + 6, y + 5, "(scale stretched)", size=11, fill=MUTED)
    # edge following
    ex = 720
    s.text(ex + 110, 72, "Following the LEFT edge", size=15, fill=INK, weight="bold", anchor="middle")
    s.rect(ex + 120, 90, 26, 330, fill="#1b1b1b")
    for k, (cx, txt, arrow) in enumerate(((ex + 133, "on tape: steer LEFT", -1), (ex + 106, "on floor: steer RIGHT", 1))):
        cy = 185 + k * 140
        s.rect(cx - 30, cy - 30, 60, 60, fill="#dfe6ee", stroke=MUTED, r=8)
        s.circle(cx, cy - 30, 5, fill="#00b4d8")
        s.path("M%d,%d Q%d,%d %d,%d" % (cx, cy - 40, cx, cy - 75, cx + arrow * 40, cy - 80), stroke=GREEN, sw=3,
               arrow=True)
        s.rect(ex + 30, cy + 37, 170, 22, fill="#ffffff", stroke=GREEN, r=6)
        s.text(ex + 115, cy + 53, txt, size=13, fill=GREEN, weight="bold", anchor="middle")
    return s.save("concept-color-sensor.svg")


if __name__ == "__main__":
    for f in (opmode_anatomy, motor_power, variables, gamepad, game_loop, arcade, if_else, for_loop,
              timer_loop, method_anatomy, encoder, edge_detection, state_machine, class_object,
              config_names, control_hub, color_sensor):
        print(f())
