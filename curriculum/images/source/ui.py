"""Screen-style illustrations for Lesson 00 and the deploy guide."""
from lib import *


def gh_header(s, x, y, w, repo):
    s.rect(x, y, w, 44, fill=SUBTLE)
    s.line(x, y + 44, x + w, y + 44, stroke=LINE, sw=1)
    s.circle(x + 24, y + 22, 11, fill=INK)
    s.text(x + 44, y + 27, repo, size=15, fill=INK, weight="600")


def fork():
    s = Svg(980, 400, "GitHub: fork the teacher's repository")
    # Panel A: repo page
    s.panel_label(20, 30, "A  The teacher's repo page")
    y0 = s.window(20, 42, 460, 320, "github.com/drichardi/FtcRobotController")
    gh_header(s, 21, y0, 458, "drichardi / FtcRobotController")
    s.rect(40, y0 + 62, 50, 22, fill="#ffffff", stroke=LINE, r=11)
    s.text(65, y0 + 78, "Public", size=12, fill=MUTED, anchor="middle")
    bx = 160
    for label, w in (("Watch  1", 92), ("Fork  0", 86), ("Star  0", 80)):
        s.button(bx, y0 + 98, w, 30, label, "gray", size=13)
        if label.startswith("Fork"):
            s.hl(bx, y0 + 98, w, 30)
            s.callout(1, bx + w / 2, y0 + 150)
        bx += w + 10
    s.line(40, y0 + 176, 460, y0 + 176, stroke=LINE, sw=1)
    for t, tx in (("Code", 45), ("Issues", 115), ("Pull requests", 190), ("Actions", 300)):
        s.text(tx, y0 + 166, t, size=13, fill=MUTED)
    for i in range(4):
        s.rect(45, y0 + 192 + i * 24, 120 + (i % 2) * 60, 10, fill="#eaeef2", r=5)
        s.rect(260, y0 + 192 + i * 24, 180, 10, fill="#eaeef2", r=5)

    # Panel B: create fork form
    s.panel_label(500, 30, "B  The \u201cCreate a new fork\u201d page")
    y1 = s.window(500, 42, 460, 320, "github.com/drichardi/FtcRobotController/fork")
    s.text(520, y1 + 34, "Create a new fork", size=20, fill=INK, weight="600")
    s.text(520, y1 + 58, "A fork is a copy of a repository.", size=13, fill=MUTED)
    s.text(520, y1 + 92, "Owner *", size=13, fill=INK, weight="600")
    s.text(660, y1 + 92, "Repository name *", size=13, fill=INK, weight="600")
    s.field(520, y1 + 102, 120, 32, "you  \u25be", size=13)
    s.text(646, y1 + 124, "/", size=18, fill=MUTED)
    s.field(660, y1 + 102, 270, 32, "robotics-group-3", size=13)
    s.hl(520, y1 + 102, 120, 32)
    s.hl(660, y1 + 102, 270, 32)
    s.callout(2, 580, y1 + 156)
    s.callout(3, 795, y1 + 156)
    s.rect(520, y1 + 184, 16, 16, fill=BLUE, r=3)
    s.text(528, y1 + 197, "\u2713", size=12, fill="#ffffff", anchor="middle", weight="bold")
    s.text(544, y1 + 197, "Copy the master branch only", size=13, fill=INK)
    s.button(800, y1 + 232, 130, 34, "Create fork", "green")
    s.hl(800, y1 + 232, 130, 34)
    s.callout(4, 778, y1 + 249)
    return s.save("00-fork.svg")


def collaborators():
    s = Svg(980, 400, "GitHub: invite teammates as collaborators")
    y0 = s.window(20, 20, 940, 360, "github.com/captain-username/robotics-group-3/settings/access")
    gh_header(s, 21, y0, 938, "captain-username / robotics-group-3")
    tabs = (("Code", 40), ("Issues", 110), ("Pull requests", 180), ("Actions", 300), ("Settings", 380))
    for t, x in tabs:
        s.text(x, y0 + 72, t, size=14, fill=INK if t == "Settings" else MUTED,
               weight="600" if t == "Settings" else "normal")
    s.line(378, y0 + 82, 446, y0 + 82, stroke=ACC, sw=3)
    s.hl(376, y0 + 56, 72, 24)
    s.callout(1, 465, y0 + 66)
    s.line(21, y0 + 86, 959, y0 + 86, stroke=LINE, sw=1)
    # sidebar
    items = [("General", False, False), ("Access", True, False), ("Collaborators", False, True),
             ("Moderation options", False, False), ("Code and automation", True, False),
             ("Branches", False, False), ("Actions", False, False)]
    yy = y0 + 118
    for t, head, sel in items:
        if head:
            s.text(40, yy, t, size=12, fill=MUTED, weight="600")
        else:
            if sel:
                s.rect(34, yy - 17, 200, 26, fill="#eaeef2", r=6)
                s.rect(34, yy - 17, 4, 26, fill=BLUE, r=2)
            s.text(48, yy, t, size=14, fill=INK, weight="600" if sel else "normal")
        if sel:
            s.hl(34, yy - 17, 200, 26)
            s.callout(2, 252, yy - 4)
        yy += 30
    # main
    s.text(290, y0 + 120, "Manage access", size=18, fill=INK, weight="600")
    s.rect(290, y0 + 136, 640, 150, fill="#ffffff", stroke=LINE, r=6)
    s.text(610, y0 + 196, "You haven\u2019t invited any collaborators yet", size=15,
           fill=INK, weight="600", anchor="middle")
    s.button(555, y0 + 220, 110, 32, "Add people", "green")
    s.hl(555, y0 + 220, 110, 32)
    s.callout(3, 690, y0 + 236)
    s.note(298, y0 + 314, "Then type each teammate\u2019s GitHub username and add them.", color=MUTED,
           weight="normal", size=14)
    s.callout(4, 276, y0 + 309)
    return s.save("00-collaborators.svg")


def desktop_account():
    s = Svg(980, 470, "GitHub Desktop: sign in and set your name")
    # menu
    s.panel_label(20, 30, "A  File menu")
    y0 = s.window(20, 42, 270, 250, "GitHub Desktop")
    s.text(32, y0 + 22, "File", size=13, fill=INK, weight="600")
    s.text(76, y0 + 22, "Edit    View    Repository", size=13, fill=MUTED)
    s.rect(28, y0 + 32, 230, 170, fill="#ffffff", stroke="#b8b8b8", r=4)
    menu = ["New repository\u2026", "Add local repository\u2026", "Clone repository\u2026", "-",
            "Options\u2026", "-", "Exit"]
    yy = y0 + 54
    for m in menu:
        if m == "-":
            s.line(36, yy - 8, 250, yy - 8, stroke=LINE, sw=1)
            yy += 8
            continue
        if m.startswith("Options"):
            s.rect(32, yy - 17, 222, 24, fill="#dbeafe", r=3)
            s.hl(32, yy - 17, 222, 24)
            s.callout(1, 274, yy - 5)
        s.text(44, yy, m, size=13, fill=INK)
        yy += 26
    # Options: accounts
    s.panel_label(320, 30, "B  Options \u2192 Accounts")
    y1 = s.window(320, 42, 640, 180, "Options")
    tabs = ["Accounts", "Integrations", "Git", "Appearance"]
    for i, t in enumerate(tabs):
        if t == "Accounts":
            s.rect(330, y1 + 12 + i * 30, 150, 26, fill="#dbeafe", r=4)
        s.text(342, y1 + 30 + i * 30, t, size=13, fill=INK, weight="600" if t == "Accounts" else "normal")
    s.text(510, y1 + 34, "GitHub.com", size=16, fill=INK, weight="600")
    s.text(510, y1 + 58, "Sign in to your GitHub.com account to access your", size=13, fill=MUTED)
    s.text(510, y1 + 76, "repositories.", size=13, fill=MUTED)
    s.button(510, y1 + 92, 90, 30, "Sign in", "blue", size=13)
    s.hl(510, y1 + 92, 90, 30)
    s.callout(2, 622, y1 + 107)
    # Options: git
    s.panel_label(320, 254, "C  Options \u2192 Git")
    y2 = s.window(320, 266, 640, 180, "Options")
    for i, t in enumerate(tabs):
        if t == "Git":
            s.rect(330, y2 + 12 + i * 30, 150, 26, fill="#dbeafe", r=4)
            s.hl(330, y2 + 12 + i * 30, 150, 26)
            s.callout(3, 496, y2 + 25 + i * 30)
        s.text(342, y2 + 30 + i * 30, t, size=13, fill=INK, weight="600" if t == "Git" else "normal")
    s.text(530, y2 + 30, "Name", size=13, fill=INK, weight="600")
    s.field(530, y2 + 38, 300, 30, "Ava Smith", size=13)
    s.hl(530, y2 + 38, 300, 30)
    s.callout(4, 852, y2 + 53)
    s.text(530, y2 + 92, "Email", size=13, fill=INK, weight="600")
    s.field(530, y2 + 100, 300, 30, "the email on your GitHub account", size=13, placeholder=True)
    s.note(20, 330, "Use your real first and", color=MUTED, weight="normal", size=14)
    s.note(20, 350, "last name. It goes on every", color=MUTED, weight="normal", size=14)
    s.note(20, 370, "commit you make.", color=MUTED, weight="normal", size=14)
    return s.save("00-desktop-account.svg")


def desktop_clone():
    s = Svg(760, 470, "GitHub Desktop: clone the group repository")
    y0 = s.window(30, 20, 700, 430, "Clone a repository")
    for i, t in enumerate(("GitHub.com", "GitHub Enterprise", "URL")):
        x = 54 + i * 150
        s.text(x, y0 + 32, t, size=14, fill=INK if i == 0 else MUTED, weight="600" if i == 0 else "normal")
    s.line(52, y0 + 42, 140, y0 + 42, stroke="#0366d6", sw=3)
    s.hl(48, y0 + 14, 96, 30)
    s.callout(1, 30, y0 + 30)
    s.line(31, y0 + 46, 729, y0 + 46, stroke=LINE, sw=1)
    s.field(54, y0 + 62, 652, 30, "Filter your repositories", size=13, placeholder=True)
    s.rect(54, y0 + 104, 652, 160, fill="#ffffff", stroke=LINE, r=4)
    s.text(66, y0 + 126, "captain-username", size=12, fill=MUTED, weight="600")
    s.rect(56, y0 + 134, 648, 28, fill="#0366d6")
    s.text(76, y0 + 153, "robotics-group-3", size=14, fill="#ffffff")
    s.hl(56, y0 + 134, 648, 28, r=2)
    s.callout(2, 724, y0 + 148)
    s.text(66, y0 + 186, "your-username", size=12, fill=MUTED, weight="600")
    s.text(76, y0 + 212, "some-other-repo", size=14, fill=INK)
    s.text(54, y0 + 292, "Local path", size=13, fill=INK, weight="600")
    s.field(54, y0 + 300, 540, 30, "C:\\Robotics\\robotics-group-3", size=13)
    s.button(604, y0 + 300, 102, 30, "Choose\u2026", "white", size=13)
    s.hl(54, y0 + 300, 540, 30)
    s.callout(3, 30, y0 + 315)
    s.note(54, y0 + 350, "Not OneDrive, Desktop, or Documents if they sync to the cloud.", size=13)
    s.button(512, y0 + 368, 90, 32, "Clone", "blue")
    s.button(612, y0 + 368, 94, 32, "Cancel", "white")
    s.hl(512, y0 + 368, 90, 32)
    s.callout(4, 490, y0 + 384)
    return s.save("00-desktop-clone.svg")


def studio_open():
    s = Svg(980, 515, "Android Studio: open the project and wait for Gradle sync")
    # A welcome
    s.panel_label(20, 30, "A  Welcome screen")
    y0 = s.window(20, 42, 460, 210, "Welcome to Android Studio", dark=True)
    s.rect(21, y0, 130, 179, fill="#26282b")
    s.rect(28, y0 + 14, 116, 26, fill=AS_SEL, r=4)
    s.text(40, y0 + 32, "Projects", size=13, fill=AS_TXT)
    s.text(40, y0 + 62, "Customize", size=13, fill=AS_MUTED)
    s.text(40, y0 + 88, "Plugins", size=13, fill=AS_MUTED)
    bx = 170
    for t in ("New Project", "Open", "Get from VCS"):
        w = tw(t, 13) + 26
        s.button(bx, y0 + 18, w, 30, t, "dark", size=13)
        if t == "Open":
            s.hl(bx, y0 + 18, w, 30)
            s.callout(1, bx + w / 2, y0 + 70)
        bx += w + 10
    s.text(170, y0 + 120, "Or: File \u2192 Open\u2026 from any project window", size=13, fill=AS_MUTED)
    # B open dialog
    s.panel_label(500, 30, "B  Pick the cloned folder")
    y1 = s.window(500, 42, 460, 210, "Open File or Project", dark=True)
    s.field(512, y1 + 10, 436, 26, "C:\\Robotics\\robotics-group-3", dark=True, size=12)
    rows = [(0, "Robotics", False, True), (1, "robotics-group-3", True, True),
            (2, "FtcRobotController", False, True), (2, "TeamCode", False, True),
            (2, "settings.gradle", False, False)]
    yy = y1 + 58
    for ind, t, sel, isdir in rows:
        x = 520 + ind * 20
        if sel:
            s.rect(512, yy - 15, 436, 22, fill=AS_SEL)
            s.hl(512, yy - 15, 436, 22, r=2)
            s.callout(2, 930, yy - 4)
        if isdir:
            s.folder(x, yy - 12)
        s.text(x + 20, yy, t, size=13, fill=AS_TXT, weight="600" if sel else "normal")
        yy += 24
    s.text(540, y1 + 176, "\u2191 pick the folder that holds settings.gradle", size=12, fill=ACC, weight="bold")
    s.button(870, y1 + 146, 74, 26, "OK", "darkblue", size=13)
    # C trust
    s.panel_label(20, 286, "C  Trust the project")
    y2 = s.window(20, 298, 460, 200, "Trust and Open Project?", dark=True)
    s.lines(36, y2 + 30, ["Trust and Open Project \u2018robotics-group-3\u2019?",
                          "Opening a project can run code from it."], size=13, fill=AS_TXT)
    s.button(36, y2 + 110, 120, 30, "Trust Project", "darkblue", size=13)
    s.button(166, y2 + 110, 160, 30, "Preview in Safe Mode", "dark", size=13)
    s.button(336, y2 + 110, 80, 30, "Cancel", "dark", size=13)
    s.hl(36, y2 + 110, 120, 30)
    s.callout(3, 96, y2 + 160)
    # D sync status
    s.panel_label(500, 286, "D  Wait for Gradle sync")
    y3 = s.window(500, 298, 460, 200, "robotics-group-3 \u2013 Android Studio", dark=True)
    s.rect(501, y3, 458, 120, fill=AS_BG)
    s.text(730, y3 + 66, "(project opens here)", size=13, fill=AS_MUTED, anchor="middle")
    s.rect(501, y3 + 120, 458, 49, fill=AS_BAR)
    s.text(514, y3 + 149, "Gradle sync running\u2026", size=13, fill=AS_TXT)
    s.rect(800, y3 + 138, 140, 10, fill="#43454a", r=5)
    s.rect(800, y3 + 138, 70, 10, fill="#3574f0", r=5)
    s.hl(508, y3 + 128, 440, 32)
    s.callout(4, 520, y3 + 106)
    s.text(540, y3 + 110, "First time: several minutes. Wait until it finishes.", size=12, fill=ACC, weight="bold")
    return s.save("00-studio-open.svg")


def tree_rows(s, x, y, rows, w, lh=24, size=13):
    """rows: (indent, text, kind, selected) kind in dir/pkg/java/gradle/none."""
    pos = {}
    for i, (ind, t, kind, sel) in enumerate(rows):
        yy = y + i * lh
        if sel:
            s.rect(x, yy - 16, w, lh - 2, fill=AS_SEL)
        xx = x + 10 + ind * 18
        if kind in ("dir", "pkg"):
            s.tri(xx + 4, yy - 5, down=True, color=AS_MUTED, s=4)
            if kind == "dir":
                s.folder(xx + 14, yy - 12)
            else:
                s.folder(xx + 14, yy - 12, color="#8c8c8c")
            s.text(xx + 34, yy, t, size=size, fill=AS_TXT)
        elif kind == "java":
            s.jfile(xx + 14, yy - 12)
            s.text(xx + 34, yy, t, size=size, fill=AS_TXT)
        elif kind == "gradle":
            s.circle(xx + 21, yy - 5, 6, fill="#5fb865")
            s.text(xx + 34, yy, t, size=size, fill=AS_TXT)
        else:
            s.text(xx + 34, yy, t, size=size, fill=AS_MUTED)
        pos[t] = yy
    return pos


def project_map():
    s = Svg(940, 600, "Android Studio project panel: which folders are yours")
    y0 = s.window(20, 20, 470, 560, "Project", dark=True)
    s.text(36, y0 + 24, "Android", size=13, fill=AS_TXT, weight="600")
    s.tri(100, y0 + 19, color=AS_TXT)
    s.hl(30, y0 + 8, 84, 24)
    s.callout(1, 134, y0 + 20)
    s.line(21, y0 + 36, 489, y0 + 36, stroke=AS_BORDER, sw=1)
    rows = [
        (0, "FtcRobotController", "dir", False),
        (1, "manifests", "dir", False),
        (1, "java", "dir", False),
        (2, "org.firstinspires.ftc.robotcontroller", "pkg", False),
        (3, "external.samples", "pkg", False),
        (0, "TeamCode", "dir", False),
        (1, "java", "dir", False),
        (2, "org.firstinspires.ftc.teamcode", "pkg", False),
        (3, "lessons", "pkg", False),
        (4, "L01_HelloRobot", "java", False),
        (4, "L02_DriveSequence", "java", False),
        (4, "\u22ee", "none", False),
        (4, "RobotHardware", "java", False),
        (3, "students", "pkg", False),
        (4, "ava", "pkg", True),
        (5, "L01_HelloRobot", "java", False),
        (0, "Gradle Scripts", "dir", False),
        (1, "build.gradle (Project: robotics-group-3)", "gradle", False),
        (1, "build.dependencies.gradle", "gradle", False),
    ]
    pos = tree_rows(s, 21, y0 + 62, rows, 468, lh=25)
    # tags
    tx = 520

    def tagline(y, label, color, desc):
        s.line(492, y - 5, tx - 6, y - 5, stroke=color, sw=2, dash="4 3")
        w = s.tag(tx, y, label, color)
        s.text(tx + w + 10, y, desc, size=13, fill=INK)

    tagline(pos["FtcRobotController"], "DON\u2019T EDIT", RED, "The Robot Controller app itself")
    tagline(pos["external.samples"], "COPY FROM", YELLOW, "FIRST\u2019s example programs")
    tagline(pos["lessons"], "COPY FROM", YELLOW, "Teacher\u2019s lessons (don\u2019t edit)")
    tagline(pos["ava"], "YOURS", GREEN, "Your folder: edit anything here")
    tagline(pos["Gradle Scripts"], "DON\u2019T EDIT", RED, "Build settings for everyone")
    s.rect(510, 342, 410, 92, fill=SUBTLE, stroke=LINE, r=8)
    s.text(526, 366, "Work only in the green folder.", size=14, fill=INK, weight="bold")
    s.text(526, 388, "Copy from the yellow ones. Leave the red ones alone.", size=14, fill=INK)
    s.text(526, 416, "1 = make sure the dropdown says \u201cAndroid\u201d.", size=14, fill=MUTED)
    return s.save("00-project-map.svg")


def context_menu(s, x, y, items, hi=None, w=200):
    h = len(items) * 26 + 10
    s.rect(x, y, w, h, fill="#2b2d30", stroke=AS_BORDER, r=6)
    yy = y + 24
    for it in items:
        label, short = it if isinstance(it, tuple) else (it, "")
        if label == hi:
            s.rect(x + 4, yy - 17, w - 8, 24, fill=AS_SEL, r=4)
        s.text(x + 14, yy, label, size=13, fill=AS_TXT)
        if short:
            s.text(x + w - 12, yy, short, size=12, fill=AS_MUTED, anchor="end")
        yy += 26
    return y + 24


def new_package():
    s = Svg(980, 420, "Android Studio: make your own package (folder)")
    s.panel_label(20, 30, "A  Right-click the teamcode package \u2192 New \u2192 Package")
    s.rect(20, 42, 560, 340, fill=AS_BAR, stroke=AS_BORDER, r=6)
    rows = [(0, "TeamCode", "dir", False), (1, "java", "dir", False),
            (2, "org.firstinspires.ftc.teamcode", "pkg", True), (3, "lessons", "pkg", False)]
    pos = tree_rows(s, 21, 72, rows, 558)
    s.hl(24, pos["org.firstinspires.ftc.teamcode"] - 17, 290, 24)
    s.callout(1, 334, pos["org.firstinspires.ftc.teamcode"] - 5)
    top = context_menu(s, 150, 140, [("New", "\u25b8"), ("Cut", "Ctrl+X"), ("Copy", "Ctrl+C"),
                                    ("Paste", "Ctrl+V"), ("Delete\u2026", "Delete")], hi="New", w=190)
    s.hl(154, top - 17, 182, 24)
    s.callout(2, 150, top - 5)
    top2 = context_menu(s, 350, 140, ["Java Class", "Kotlin Class/File", "File", "Package"],
                        hi="Package", w=200)
    s.hl(354, top2 + 3 * 26 - 17, 192, 24)
    s.callout(3, 568, top2 + 3 * 26 - 5)
    # dialog
    s.panel_label(600, 30, "B  Type your package name")
    y0 = s.window(600, 42, 360, 160, "New Package", dark=True)
    s.rect(616, y0 + 20, 328, 32, fill=AS_BG, stroke="#3574f0", sw=2, r=5)
    s.rich(626, y0 + 41, [("org.firstinspires.ftc.teamcode.", AS_MUTED, "normal"),
                          ("students.ava", "#ffb86b", "bold")], size=12, mono=True)
    s.callout(4, 944, y0 + 90)
    s.lines(616, y0 + 82, ["Type the orange part: students.yourname", "all lowercase, no spaces.",
                           "Press Enter."], size=13, fill=AS_TXT)
    s.text(600, 250, "Your new folder appears here:", size=14, fill=INK, weight="600")
    s.rect(600, 262, 360, 120, fill=AS_BAR, stroke=AS_BORDER, r=6)
    rows = [(0, "org.firstinspires.ftc.teamcode", "pkg", False), (1, "lessons", "pkg", False),
            (1, "students", "pkg", False), (2, "ava", "pkg", True)]
    tree_rows(s, 601, 290, rows, 358)
    return s.save("00-new-package.svg")


def copy_class():
    s = Svg(980, 430, "Android Studio: copy a lesson into your folder")
    s.panel_label(20, 30, "A  Right-click the lesson \u2192 Copy")
    s.rect(20, 42, 300, 230, fill=AS_BAR, stroke=AS_BORDER, r=6)
    rows = [(0, "lessons", "pkg", False), (1, "L01_HelloRobot", "java", True),
            (1, "L02_DriveSequence", "java", False)]
    pos = tree_rows(s, 21, 70, rows, 298)
    top = context_menu(s, 80, 126, [("Cut", "Ctrl+X"), ("Copy", "Ctrl+C"), ("Paste", "Ctrl+V")],
                       hi="Copy", w=200)
    s.hl(84, top + 26 - 17, 192, 24)
    s.callout(1, 300, top + 26 - 5)

    s.panel_label(340, 30, "B  Right-click YOUR folder \u2192 Paste")
    s.rect(340, 42, 300, 230, fill=AS_BAR, stroke=AS_BORDER, r=6)
    rows = [(0, "students", "pkg", False), (1, "ava", "pkg", True)]
    tree_rows(s, 341, 70, rows, 298)
    top = context_menu(s, 400, 104, [("Cut", "Ctrl+X"), ("Copy", "Ctrl+C"), ("Paste", "Ctrl+V")],
                       hi="Paste", w=200)
    s.hl(404, top + 52 - 17, 192, 24)
    s.callout(2, 620, top + 52 - 5)

    s.panel_label(660, 30, "C  Keep the name \u2192 OK")
    y0 = s.window(660, 42, 300, 230, "Copy Class", dark=True)
    s.text(674, y0 + 26, "Copy class L01_HelloRobot", size=12, fill=AS_TXT)
    s.text(674, y0 + 54, "New name:", size=12, fill=AS_TXT)
    s.field(674, y0 + 62, 272, 26, "L01_HelloRobot", dark=True, size=12, mono=True)
    s.text(674, y0 + 108, "Destination package:", size=12, fill=AS_TXT)
    s.field(674, y0 + 116, 272, 26, "\u2026teamcode.students.ava", dark=True, size=12, mono=True)
    s.hl(674, y0 + 116, 272, 26)
    s.button(866, y0 + 158, 80, 28, "OK", "darkblue", size=13)
    s.hl(866, y0 + 158, 80, 28)
    s.callout(3, 846, y0 + 172)
    s.lines(20, 310, ["Check: the destination package ends in .students.yourname.",
                      "Shortcut: click the lesson and press Ctrl+C, then click your folder and press Ctrl+V."],
            size=14, fill=INK, lh=24)
    s.text(20, 370, "Copying RobotHardware + L15 together? Select both files (hold Ctrl) before copying.",
           size=14, fill=MUTED)
    return s.save("00-copy-class.svg")


def code_panel(s, x, y, w, lines, title):
    s.text(x, y - 10, title, size=15, fill=MUTED, weight="bold")
    h = len(lines) * 24 + 20
    s.rect(x, y, w, h, fill=AS_BG, stroke=AS_BORDER, r=6)
    s.rect(x, y, 34, h, fill="#26282b", r=6)
    yy = y + 28
    pos = []
    for segs in lines:
        s.rich(x + 46, yy, segs, size=13, mono=True)
        pos.append(yy)
        yy += 24
    return pos


KW = "#cf8e6d"
STR = "#6aab73"
ANN = "#b3ae60"
CMT = "#7a7e85"
TXT = "#bcbec4"


def edit_annotation():
    s = Svg(980, 390, "Edit your copy: unique name and delete @Disabled")
    before = [
        [("package ", KW, "normal"), ("org.firstinspires.ftc.teamcode.students.ava;", TXT, "normal")],
        [("\u22ee", CMT, "normal")],
        [("@TeleOp", ANN, "normal"), ("(name = ", TXT, "normal"), ('"L01 Hello Robot"', STR, "normal"),
         (", group = ", TXT, "normal"), ('"Lessons"', STR, "normal"), (")", TXT, "normal")],
        [("@Disabled", ANN, "normal"), ("  // Hides this ORIGINAL\u2026", CMT, "normal")],
        [("public class ", KW, "normal"), ("L01_HelloRobot ", TXT, "normal"), ("extends ", KW, "normal"),
         ("LinearOpMode {", TXT, "normal")],
    ]
    p = code_panel(s, 20, 42, 600, before, "BEFORE  (your copy, right after pasting)")
    s.note(640, p[0] + 4, "\u2713 Android Studio changed this for you", color=GREEN)
    s.hl(56, p[2] - 17, 540, 24, r=3)
    s.callout(1, 610, p[2] - 5)
    s.note(640, p[2] + 4, "Change the name and group")
    s.rect(56, p[3] - 17, 540, 24, fill=RED, opacity=0.25, r=3)
    s.line(56, p[3] - 5, 300, p[3] - 5, stroke="#ff6b6b", sw=2)
    s.callout(2, 610, p[3] - 5)
    s.note(640, p[3] + 4, "Delete this whole line", color=RED)
    after = [
        [("package ", KW, "normal"), ("org.firstinspires.ftc.teamcode.students.ava;", TXT, "normal")],
        [("\u22ee", CMT, "normal")],
        [("@TeleOp", ANN, "normal"), ("(name = ", TXT, "normal"), ('"L01 Hello Robot - Ava"', STR, "normal"),
         (", group = ", TXT, "normal"), ('"Ava"', STR, "normal"), (")", TXT, "normal")],
        [("public class ", KW, "normal"), ("L01_HelloRobot ", TXT, "normal"), ("extends ", KW, "normal"),
         ("LinearOpMode {", TXT, "normal")],
    ]
    p2 = code_panel(s, 20, 250, 600, after, "AFTER")
    s.note(640, p2[2] + 4, "Unique name + your own group", color=GREEN)
    s.lines(640, 350, ["The import line for Disabled can stay.", "Android Studio just grays it out."],
            size=13, fill=MUTED)
    return s.save("00-edit-annotation.svg")


def hammer(s, x, y, color=AS_TXT):
    s.rect(x, y + 2, 12, 6, fill=color, r=1)
    s.rect(x + 4, y + 8, 4, 10, fill=color, r=1)


def studio_run():
    s = Svg(980, 360, "Android Studio: deploy to the Control Hub")
    y0 = s.window(20, 20, 940, 320, "robotics-group-3 \u2013 Android Studio", dark=True)
    s.rect(21, y0, 938, 46, fill=AS_BAR)
    s.rect(21, y0 + 46, 938, 120, fill=AS_BG)
    s.text(490, y0 + 140, "(your code)", size=13, fill=AS_MUTED, anchor="middle")
    s.text(36, y0 + 29, "robotics-group-3  \u25be", size=13, fill=AS_TXT, weight="600")
    s.text(210, y0 + 29, "master  \u25be", size=13, fill=AS_TXT)
    hammer(s, 470, y0 + 12)
    s.text(476, y0 + 62, "Build", size=11, fill=AS_MUTED, anchor="middle")
    s.button(510, y0 + 9, 120, 28, "TeamCode  \u25be", "dark", size=13)
    s.hl(510, y0 + 9, 120, 28)
    s.callout(1, 570, y0 + 66)
    s.button(640, y0 + 9, 220, 28, "REV Robotics Control Hub \u25be", "dark", size=13)
    s.hl(640, y0 + 9, 220, 28)
    s.callout(2, 750, y0 + 66)
    s.rect(872, y0 + 9, 32, 28, fill="#3c3f41", r=5)
    s.path(f"M881,{y0 + 15} L897,{y0 + 23} L881,{y0 + 31} Z", stroke="#5fb865", fill="#5fb865", sw=1)
    s.hl(872, y0 + 9, 32, 28)
    s.callout(3, 888, y0 + 66)
    s.rect(912, y0 + 9, 32, 28, fill="#3c3f41", r=5)
    s.circle(928, y0 + 23, 6, fill="#e5a050")
    s.lines(560, y0 + 104, ["1 = which program to build (TeamCode)", "2 = where to send it",
                            "3 = Run: build + install on the robot"], size=13, fill=AS_TXT, lh=19)
    s.rect(21, y0 + 166, 938, 123, fill=AS_BAR)
    s.text(36, y0 + 188, "Run:  TeamCode", size=13, fill=AS_TXT, weight="600")
    s.text(36, y0 + 214, "> Task :TeamCode:assembleDebug", size=12, fill=AS_MUTED, mono=True)
    s.text(36, y0 + 236, "Installing and launching on REV Robotics Control Hub\u2026", size=12, fill=AS_MUTED, mono=True)
    s.text(36, y0 + 262, "Launch succeeded", size=13, fill="#5fb865", mono=True, weight="bold")
    s.hl(32, y0 + 247, 150, 22)
    s.callout(4, 204, y0 + 258)
    s.note(230, y0 + 263, "Wait for this, then watch the Driver Station reconnect.", size=13)
    return s.save("00-studio-run.svg")


def ds_screen(s, x, y, w, h, title):
    s.rect(x, y, w, h, fill="#212121", stroke="#000000", sw=2, r=14)
    s.rect(x + 10, y + 10, w - 20, h - 20, fill="#424242", r=6)
    s.rect(x + 10, y + 10, w - 20, 36, fill="#1565c0", r=6)
    s.text(x + 22, y + 34, title, size=14, fill="#ffffff", weight="600")
    s.text(x + w - 26, y + 34, "\u22ee", size=18, fill="#ffffff", anchor="middle")
    return y + 46


def driver_station():
    s = Svg(980, 470, "Driver Station: find and run your program")
    s.panel_label(20, 30, "A  Open the TeleOp list and find your name")
    y0 = ds_screen(s, 20, 42, 460, 400, "1234-RC")
    s.button(36, y0 + 14, 130, 32, "Autonomous \u25be", "gray", size=13)
    s.button(334, y0 + 14, 130, 32, "TeleOp \u25be", "gray", size=13)
    s.hl(334, y0 + 14, 130, 32)
    s.callout(1, 312, y0 + 30)
    s.rect(130, y0 + 58, 300, 270, fill="#fafafa", stroke="#9e9e9e", r=4)
    s.text(144, y0 + 82, "Select TeleOp", size=14, fill=INK, weight="600")
    items = [("Ava", True), ("L01 Hello Robot - Ava", False), ("Ben", True), ("L01 Hello Robot - Ben", False),
             ("Cam", True), ("L01 Hello Robot - Cam", False)]
    yy = y0 + 112
    for t, head in items:
        if head:
            s.text(144, yy, t, size=12, fill="#1565c0", weight="bold")
        else:
            if t.endswith("Ava"):
                s.rect(136, yy - 18, 288, 26, fill="#e3f2fd", r=3)
                s.hl(136, yy - 18, 288, 26)
                s.callout(2, 412, yy - 5)
            s.text(160, yy, t, size=14, fill=INK)
        yy += 30 if not head else 26
    s.text(144, y0 + 318, "(grouped by the group = \"\u2026\" in your code)", size=11, fill=MUTED)

    s.panel_label(500, 30, "B  INIT, then START")
    y1 = ds_screen(s, 500, 42, 460, 400, "1234-RC")
    s.text(730, y1 + 32, "L01 Hello Robot - Ava", size=15, fill="#ffffff", weight="600", anchor="middle")
    s.rect(650, y1 + 50, 160, 54, fill="#757575", r=8)
    s.text(730, y1 + 85, "INIT", size=20, fill="#ffffff", weight="bold", anchor="middle")
    s.hl(650, y1 + 50, 160, 54)
    s.callout(3, 628, y1 + 77)
    s.circle(730, y1 + 140, 26, fill="#43a047")
    s.path(f"M722,{y1 + 126} L742,{y1 + 140} L722,{y1 + 154} Z", stroke="#ffffff", fill="#ffffff", sw=1)
    s.hl(702, y1 + 112, 56, 56, r=28)
    s.callout(4, 784, y1 + 140)
    s.text(820, y1 + 145, "appears after INIT", size=12, fill="#ffffff")
    s.rect(520, y1 + 186, 420, 140, fill="#303030", r=4)
    s.lines(534, y1 + 212, ["Status : Running!", "Programmer : Ava and Ben", "Seconds since INIT : 12.3"],
            size=14, fill="#e0e0e0", mono=True, lh=26)
    s.text(534, y1 + 310, "\u2190 telemetry shows up here", size=12, fill="#ffcc80")
    return s.save("00-driver-station.svg")


def gd_topbar(s, x, y, w, third_label, third_sub):
    s.rect(x, y, w, 56, fill="#24292e")
    cw = w / 3
    for i, (a, b) in enumerate([("Current repository", "robotics-group-3"), ("Current branch", "master")]):
        cx = x + i * cw
        if i:
            s.line(cx, y, cx, y + 56, stroke="#444d56", sw=1)
        s.text(cx + 16, y + 22, a, size=11, fill="#959da5")
        s.text(cx + 16, y + 42, b, size=14, fill="#ffffff", weight="600")
    cx = x + 2 * cw
    s.line(cx, y, cx, y + 56, stroke="#444d56", sw=1)
    s.text(cx + 16, y + 25, third_label, size=14, fill="#ffffff", weight="600")
    s.text(cx + 16, y + 44, third_sub, size=11, fill="#959da5")
    return cx, cw


def desktop_commit():
    s = Svg(980, 520, "GitHub Desktop: commit and push")
    y0 = s.window(20, 20, 940, 480, "GitHub Desktop")
    tx, cw = gd_topbar(s, 21, y0, 938, "Push origin  \u21911", "Last fetched just now")
    s.hl(tx + 4, y0 + 4, cw - 10, 48)
    s.callout(4, tx + cw - 30, y0 + 28)
    s.note(tx + cw - 12, y0 + 82, "Push appears here after you commit", size=12, anchor="end")
    # sidebar
    sx, sy, sw_ = 21, y0 + 56, 340
    s.rect(sx, sy, sw_, 392, fill="#ffffff")
    s.line(sx + sw_, sy, sx + sw_, sy + 392, stroke=LINE, sw=1)
    s.text(sx + 70, sy + 28, "Changes  1", size=14, fill=INK, weight="600", anchor="middle")
    s.text(sx + 240, sy + 28, "History", size=14, fill=MUTED, anchor="middle")
    s.line(sx + 10, sy + 40, sx + 160, sy + 40, stroke="#f9826c", sw=3)
    s.line(sx, sy + 42, sx + sw_, sy + 42, stroke=LINE, sw=1)
    s.text(sx + 14, sy + 66, "1 changed file", size=12, fill=MUTED)
    s.rect(sx + 12, sy + 80, 14, 14, fill="#0366d6", r=3)
    s.text(sx + 19, sy + 92, "\u2713", size=10, fill="#ffffff", anchor="middle", weight="bold")
    s.text(sx + 34, sy + 92, "\u2026\\students\\ava\\L01_HelloRobot.java", size=12, fill=INK)
    s.rect(sx + 312, sy + 80, 14, 14, fill="#ffffff", stroke="#28a745", r=3)
    s.text(sx + 319, sy + 92, "+", size=12, fill="#28a745", anchor="middle", weight="bold")
    s.hl(sx + 8, sy + 76, sw_ - 16, 22)
    s.callout(1, sx + sw_ + 16, sy + 87)
    # commit box
    s.line(sx, sy + 250, sx + sw_, sy + 250, stroke=LINE, sw=1)
    s.field(sx + 12, sy + 262, sw_ - 24, 30, "Add Ava's copy of L01 Hello Robot", size=12)
    s.hl(sx + 12, sy + 262, sw_ - 24, 30)
    s.callout(2, sx + sw_ + 16, sy + 277)
    s.field(sx + 12, sy + 300, sw_ - 24, 40, "Description (optional)", size=12, placeholder=True)
    s.button(sx + 12, sy + 350, sw_ - 24, 32, "Commit to master", "blue", size=14)
    s.hl(sx + 12, sy + 350, sw_ - 24, 32)
    s.callout(3, sx + sw_ + 16, sy + 366)
    # diff area
    dx = sx + sw_ + 40
    s.text(dx, sy + 30, "L01_HelloRobot.java", size=12, fill=MUTED)
    for i in range(10):
        s.rect(dx, sy + 48 + i * 22, 540, 20, fill="#e6ffed")
        s.text(dx + 8, sy + 63 + i * 22, "+", size=12, fill="#28a745", mono=True)
        s.rect(dx + 26, sy + 55 + i * 22, 120 + (i * 53) % 300, 6, fill="#acf2bd", r=3)
    s.lines(dx, sy + 300, ["1  Only files in YOUR folder should be listed.",
                           "2  Write what you did in Summary.",
                           "3  Commit (saves on your laptop)",
                           "4  Push origin (uploads to GitHub)"], size=14, fill=INK, lh=22)
    return s.save("00-desktop-commit.svg")


def desktop_pull():
    s = Svg(980, 250, "GitHub Desktop: fetch, pull, and push buttons")
    states = [
        ("Fetch origin", "Last fetched 2 minutes ago", "Checks GitHub for", "teammates\u2019 changes."),
        ("Pull origin  \u21932", "2 commits to download", "Downloads teammates\u2019", "changes. Do it first!"),
        ("Push origin  \u21911", "1 commit to upload", "Uploads your commits", "to GitHub."),
    ]
    x = 20
    for i, (a, b, c, d) in enumerate(states):
        s.rect(x, 30, 290, 56, fill="#24292e", r=6)
        s.text(x + 16, 52, a, size=15, fill="#ffffff", weight="600")
        s.text(x + 16, 74, b, size=12, fill="#959da5")
        s.text(x + 145, 118, c, size=14, fill=INK, anchor="middle")
        s.text(x + 145, 138, d, size=14, fill=INK, anchor="middle")
        s.callout(i + 1, x + 145, 170)
        if i < 2:
            s.line(x + 296, 58, x + 324, 58, stroke=MUTED, sw=2, arrow=True)
        x += 330
    s.text(490, 210, "It\u2019s the same button in the top bar. Its label changes to tell you what to do next.",
           size=14, fill=MUTED, anchor="middle")
    return s.save("00-desktop-pull.svg")


def studio_sim_run():
    s = Svg(980, 330, "Android Studio: start the Robot Simulator")
    y0 = s.window(20, 20, 940, 290, "robotics-group-3 – Android Studio", dark=True)
    s.rect(21, y0, 938, 46, fill=AS_BAR)
    s.rect(21, y0 + 46, 938, 213, fill=AS_BG)
    s.text(36, y0 + 29, "robotics-group-3  ▾", size=13, fill=AS_TXT, weight="600")
    s.text(210, y0 + 29, "master  ▾", size=13, fill=AS_TXT)
    s.button(560, y0 + 9, 170, 28, "Robot Simulator  ▾", "dark", size=13)
    s.hl(560, y0 + 9, 170, 28)
    s.callout(1, 538, y0 + 23)
    s.text(745, y0 + 29, "(no robot needed)", size=12, fill=AS_MUTED)
    s.rect(872, y0 + 9, 32, 28, fill="#3c3f41", r=5)
    s.path(f"M881,{y0 + 15} L897,{y0 + 23} L881,{y0 + 31} Z", stroke="#5fb865", fill="#5fb865", sw=1)
    s.hl(872, y0 + 9, 32, 28)
    s.callout(3, 888, y0 + 66)
    top = context_menu(s, 560, y0 + 42, ["TeamCode", "Robot Simulator", "Edit Configurations…"],
                       hi="Robot Simulator", w=230)
    s.hl(564, top + 26 - 17, 222, 24)
    s.callout(2, 808, top + 26 - 5)
    s.lines(60, y0 + 90, ["1  Open the run list at the top of Android Studio.",
                          "2  Pick Robot Simulator.",
                          "3  Click the green ► Run button. The simulator window opens.",
                          "",
                          "To put code on the REAL robot later, switch back to TeamCode."],
            size=14, fill=AS_TXT, lh=24)
    return s.save("sim-run-config.svg")


if __name__ == "__main__":
    for f in (fork, collaborators, desktop_account, desktop_clone, studio_open, project_map,
              new_package, copy_class, edit_annotation, studio_run, driver_station,
              desktop_commit, desktop_pull, studio_sim_run):
        print(f())
