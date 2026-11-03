package robotsim;

import com.qualcomm.robotcore.hardware.Gamepad;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Scrollable;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

/** The simulator window: the field on the left, a Driver Station on the right. */
final class SimFrame extends JFrame {

    private static final Pattern MISSION_CLASS = Pattern.compile("^M(\\d\\d)_");
    private static final Pattern LESSON_CLASS = Pattern.compile("^L\\d\\d_");
    private static final boolean[] NO_BUTTONS = new boolean[Gamepad.BUTTON_COUNT];
    private static final double[] SPEEDS = {0.25, 0.5, 1.0, 2.0};
    private static final String[] SPEED_LABELS = {"1/4 speed", "1/2 speed", "Normal", "2x"};

    private final SimClock clock = new SimClock();
    private final List<Mission> missions = Missions.all();
    private final World world = new World(missions.get(1));
    private final OpModeRunner runner = new OpModeRunner(world, clock);
    private final KeyboardGamepad keyboard = new KeyboardGamepad();
    private final CodeLoader loader;

    private final FieldPanel field = new FieldPanel();
    private final ArmPanel armView = new ArmPanel();
    private final JComboBox<Mission> missionBox = new JComboBox<Mission>();
    private final JTextArea missionText = textArea(3, false);
    private final JLabel goalsLabel = new JLabel();
    private final JComboBox<CodeLoader.Entry> programBox = new JComboBox<CodeLoader.Entry>();
    private final JLabel codeStatus = new JLabel(" ");
    private final DriverStationPanel ds = new DriverStationPanel(new DsHost());
    private final JButton resetButton = new JButton("Reset robot");
    private final JButton reloadButton = new JButton("Reload code");
    private final JLabel robotLabel = new JLabel();
    private final JCheckBox trailBox = new JCheckBox("Show trail", true);
    private final JCheckBox batteryBox = new JCheckBox("Low battery");
    private final JCheckBox gamepad2Box = new JCheckBox("Keys drive gamepad2");
    private double speed = 1.0;

    private boolean updatingCombos = false;
    private long lastFrameNanos = System.nanoTime();
    private long runEndNanos = -1;

    SimFrame(CodeLoader loader) {
        super("FTC Robot Simulator");
        this.loader = loader;
        SimClock.setActive(clock);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        buildLayout();
        wireEvents();
        for (Mission m : missions) {
            missionBox.addItem(m);
        }
        reloadCode(null);
        selectMission(missions.get(1), true);

        Timer timer = new Timer(15, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tick();
            }
        });
        timer.start();
    }

    // ------------------------------------------------------------------ layout

    /** A panel that always fits the scroll pane's width, so nothing gets cut off on the right. */
    private static final class FitWidthPanel extends JPanel implements Scrollable {
        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle r, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle r, int orientation, int direction) {
            return 120;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    private void buildLayout() {
        JPanel side = new FitWidthPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 8));

        JPanel missionPanel = section("1. Mission");
        missionPanel.add(full(missionBox));
        missionPanel.add(grows(missionText));
        goalsLabel.setFont(goalsLabel.getFont().deriveFont(12f));
        missionPanel.add(grows(goalsLabel));
        side.add(missionPanel);

        // The program list, INIT/START/STOP and telemetry, laid out like the real Driver Station app.
        JPanel dsPanel = section("2. Driver Station  (your code in TeamCode)");
        ds.setAlignmentX(Component.LEFT_ALIGNMENT);
        ds.setMaximumSize(new Dimension(Integer.MAX_VALUE, ds.getPreferredSize().height));
        dsPanel.add(ds);
        dsPanel.add(Box.createVerticalStrut(4));
        JPanel codeRow = new JPanel(new BorderLayout(6, 0));
        codeRow.add(codeStatus, BorderLayout.CENTER);
        JPanel codeButtons = new JPanel(new GridLayout(1, 2, 6, 0));
        reloadButton.setFocusable(false);
        resetButton.setFocusable(false);
        codeButtons.add(reloadButton);
        codeButtons.add(resetButton);
        codeRow.add(codeButtons, BorderLayout.EAST);
        dsPanel.add(full(codeRow));
        side.add(dsPanel);

        JPanel robotPanel = section("Robot");
        JPanel robotRow = new JPanel(new BorderLayout(8, 0));
        robotLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        robotRow.add(robotLabel, BorderLayout.CENTER);
        robotRow.add(armView, BorderLayout.EAST);
        robotPanel.add(grows(robotRow));
        side.add(robotPanel);

        JPanel viewPanel = section("View and keyboard gamepad");
        JPanel viewRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < SPEEDS.length; i++) {
            final double s = SPEEDS[i];
            JRadioButton b = new JRadioButton(SPEED_LABELS[i], s == 1.0);
            b.setFocusable(false);
            b.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    speed = s;
                }
            });
            group.add(b);
            viewRow.add(b);
        }
        trailBox.setFocusable(false);
        viewRow.add(trailBox);
        JButton clearTrail = new JButton("Clear trail");
        clearTrail.setFocusable(false);
        clearTrail.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                world.clearTrail();
            }
        });
        viewRow.add(clearTrail);
        viewPanel.add(full(viewRow));
        JPanel optionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        batteryBox.setFocusable(false);
        batteryBox.setToolTipText("Motors get about 25% weaker, like a robot with a low battery.");
        batteryBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                world.battery = batteryBox.isSelected() ? 0.75 : 1.0;
            }
        });
        gamepad2Box.setFocusable(false);
        gamepad2Box.setToolTipText("The keyboard controls gamepad2 instead of gamepad1 (for a second driver).");
        optionRow.add(batteryBox);
        optionRow.add(gamepad2Box);
        viewPanel.add(full(optionRow));
        JLabel legend = new JLabel(KeyboardGamepad.LEGEND);
        legend.setFont(legend.getFont().deriveFont(11f));
        viewPanel.add(full(legend));
        side.add(viewPanel);
        side.add(Box.createVerticalGlue());

        JScrollPane sideScroll = new JScrollPane(side, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sideScroll.setBorder(null);
        sideScroll.setPreferredSize(new Dimension(520, 700));
        sideScroll.getVerticalScrollBar().setUnitIncrement(16);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(field, BorderLayout.CENTER);
        getContentPane().add(sideScroll, BorderLayout.EAST);

        Rectangle screen = GraphicsEnvironment.isHeadless() ? new Rectangle(0, 0, 1280, 820)
                : GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        setSize(Math.min(1300, screen.width), Math.min(880, screen.height));
        setLocationRelativeTo(null);
    }

    private static JPanel section(String title) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        TitledBorder border = BorderFactory.createTitledBorder(title);
        border.setTitleFont(border.getTitleFont() == null ? new Font(Font.SANS_SERIF, Font.BOLD, 13)
                : border.getTitleFont().deriveFont(Font.BOLD, 13f));
        p.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(2, 4, 4, 4)));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        return p;
    }

    private static <T extends JComponent> T full(T c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
        return c;
    }

    /** For components whose height changes with their text (wrapped descriptions, goal lists). */
    private static <T extends JComponent> T grows(T c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        return c;
    }

    private static JTextArea textArea(int rows, boolean mono) {
        JTextArea t = new JTextArea(rows, 30);
        t.setEditable(false);
        t.setLineWrap(true);
        t.setWrapStyleWord(true);
        t.setFocusable(false);
        if (!mono) {
            t.setOpaque(false);
            t.setFont(new JLabel().getFont().deriveFont(12.5f));
        }
        return t;
    }

    // ------------------------------------------------------------------ events

    private void wireEvents() {
        missionBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!updatingCombos && missionBox.getSelectedItem() != null) {
                    selectMission((Mission) missionBox.getSelectedItem(), true);
                }
            }
        });
        programBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!updatingCombos) {
                    onProgramChosen();
                }
            }
        });
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetRobot();
            }
        });
        reloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reloadCode(selectedClassName());
            }
        });
        field.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                field.requestFocusInWindow();
            }
        });
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (!keyboard.handles(e.getKeyCode()) || e.getID() == KeyEvent.KEY_TYPED) {
                    return false;
                }
                if (e.getID() == KeyEvent.KEY_PRESSED || e.getID() == KeyEvent.KEY_RELEASED) {
                    keyboard.keyChanged(e.getKeyCode(), e.getID() == KeyEvent.KEY_PRESSED, e.isShiftDown());
                }
                // While a program runs, the keys belong to the gamepad (not the buttons and lists).
                OpModeRunner.Phase p = runner.phase();
                return p == OpModeRunner.Phase.INIT || p == OpModeRunner.Phase.RUNNING;
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowDeactivated(WindowEvent e) {
                keyboard.releaseAll();
            }
        });
    }

    private void resetRobot() {
        runner.stop();
        world.reset((Mission) missionBox.getSelectedItem());
    }

    /** Connects the Driver Station panel's buttons and lists to the simulator. */
    private final class DsHost implements DriverStationPanel.Host {
        @Override
        public List<CodeLoader.Entry> programs() {
            List<CodeLoader.Entry> list = new ArrayList<CodeLoader.Entry>();
            for (int i = 0; i < programBox.getItemCount(); i++) {
                list.add(programBox.getItemAt(i));
            }
            return list;
        }

        @Override
        public CodeLoader.Entry selectedProgram() {
            return (CodeLoader.Entry) programBox.getSelectedItem();
        }

        @Override
        public void chooseProgram(CodeLoader.Entry entry) {
            // Choosing a different program stops the current one, like the real Driver Station.
            if (entry != programBox.getSelectedItem()) {
                runner.stop();
            }
            programBox.setSelectedItem(entry);
        }

        @Override
        public void pressInit() {
            onInit();
        }

        @Override
        public void pressStart() {
            runner.start();
            field.requestFocusInWindow();
        }

        @Override
        public void pressStop() {
            runner.stop();
        }

        @Override
        public void reloadCode() {
            SimFrame.this.reloadCode(selectedClassName());
        }

        @Override
        public void resetRobot() {
            SimFrame.this.resetRobot();
        }
    }

    private String selectedClassName() {
        CodeLoader.Entry e = (CodeLoader.Entry) programBox.getSelectedItem();
        return e == null ? null : e.type.getName();
    }

    private void reloadCode(String keepClassName) {
        CodeLoader.Result result = loader.load();
        updatingCombos = true;
        DefaultComboBoxModel<CodeLoader.Entry> model = new DefaultComboBoxModel<CodeLoader.Entry>();
        CodeLoader.Entry keep = null;
        for (CodeLoader.Entry e : result.entries) {
            model.addElement(e);
            if (e.type.getName().equals(keepClassName)) {
                keep = e;
            }
        }
        programBox.setModel(model);
        programBox.setSelectedIndex(-1);
        if (keep != null) {
            programBox.setSelectedItem(keep);
        } else {
            CodeLoader.Entry missionProgram = findProgram(missionProgramName());
            if (missionProgram != null) {
                programBox.setSelectedItem(missionProgram);
            }
        }
        updatingCombos = false;

        if (result.fatal != null) {
            codeStatus.setText("Couldn't load code");
            codeStatus.setForeground(new Color(0xCF222E));
            runner.setMessage(result.fatal, true);
        } else if (!result.problems.isEmpty()) {
            codeStatus.setText(String.format("%d programs loaded.  %d error(s) below.",
                    result.entries.size(), result.problems.size()));
            codeStatus.setForeground(new Color(0xCF222E));
            StringBuilder sb = new StringBuilder("Your code has errors, so some programs couldn't load:\n\n");
            for (CodeLoader.Problem p : result.problems) {
                sb.append(p).append("\n\n");
            }
            sb.append("Fix the errors in Android Studio, save, then press INIT again.");
            runner.setMessage(sb.toString(), true);
        } else {
            codeStatus.setText(String.format("%d programs loaded from TeamCode.", result.entries.size()));
            codeStatus.setForeground(new Color(0x1A7F37));
            runner.setMessage(null, false);
        }
    }

    /** Picking a mission resets the robot and picks the matching program, if there is one. */
    private void selectMission(Mission m, boolean pickProgram) {
        runner.stop();
        updatingCombos = true;
        missionBox.setSelectedItem(m);
        updatingCombos = false;
        world.reset(m);
        missionText.setText(m.description);
        if (pickProgram && !m.program.isEmpty()) {
            int found = -1;
            for (int i = 0; i < programBox.getItemCount(); i++) {
                if (programBox.getItemAt(i).type.getSimpleName().equals(m.program)) {
                    found = i;
                    break;
                }
            }
            updatingCombos = true;
            // If the mission's program didn't load (usually a typo), pick nothing rather than
            // a different program, so INIT can't run the wrong code on this mission.
            programBox.setSelectedIndex(found);
            updatingCombos = false;
            if (found < 0) {
                String errors = runner.message();
                int at = errors == null ? -1 : errors.indexOf("Your code has errors");
                runner.setMessage("\"" + m.program + "\" didn't load, so it can't run yet. Fix the error, save, "
                        + "and press INIT again." + (at >= 0 ? "\n\n" + errors.substring(at)
                        : "\n\nCheck that the file is in TeamCode/.../missions."), true);
            }
        }
    }

    /** Picking a mission program (M01_..., M02_...) also picks its mission. */
    private void onProgramChosen() {
        CodeLoader.Entry e = (CodeLoader.Entry) programBox.getSelectedItem();
        if (e == null) {
            return;
        }
        // A mission that names this program (for example L17_StopAtTape) gets picked first.
        for (Mission m : missions) {
            if (m.program.equals(e.type.getSimpleName())) {
                if (m != missionBox.getSelectedItem()) {
                    selectMission(m, false);
                }
                return;
            }
        }
        if (LESSON_CLASS.matcher(e.type.getSimpleName()).find() && missionBox.getSelectedItem() != missions.get(0)) {
            // Other lessons (and students' copies of them) run on the Free Drive field.
            selectMission(missions.get(0), false);
            return;
        }
        Matcher matcher = MISSION_CLASS.matcher(e.type.getSimpleName());
        if (matcher.find()) {
            int n = Integer.parseInt(matcher.group(1));
            for (Mission m : missions) {
                if (m.number == n && m != missionBox.getSelectedItem()) {
                    selectMission(m, false);
                }
            }
        }
    }

    private String missionProgramName() {
        Mission m = (Mission) missionBox.getSelectedItem();
        return m == null ? null : m.program;
    }

    private CodeLoader.Entry findProgram(String simpleName) {
        if (simpleName == null || simpleName.isEmpty()) {
            return null;
        }
        for (int i = 0; i < programBox.getItemCount(); i++) {
            if (programBox.getItemAt(i).type.getSimpleName().equals(simpleName)) {
                return programBox.getItemAt(i);
            }
        }
        return null;
    }

    private void onInit() {
        String keep = selectedClassName();
        if (loader.changedSinceLastLoad()) {
            reloadCode(keep);
        }
        CodeLoader.Entry e = (CodeLoader.Entry) programBox.getSelectedItem();
        if (e == null || (keep != null && !e.type.getName().equals(keep))) {
            String wanted = keep != null ? keep.substring(keep.lastIndexOf('.') + 1) : missionProgramName();
            if (wanted == null || wanted.isEmpty()) {
                runner.setMessage("Pick a program from the list first.", true);
                return;
            }
            String errors = runner.message();
            int at = errors == null ? -1 : errors.indexOf("Your code has errors");
            runner.setMessage("\"" + wanted + "\" didn't load, so it can't run yet. Fix the error, save, "
                    + "and press INIT again." + (at >= 0 ? "\n\n" + errors.substring(at) : ""), true);
            return;
        }
        String reloadNote = runner.message();
        runner.init(e);
        if (runner.message() == null && reloadNote != null && reloadNote.startsWith("Your code has errors")) {
            runner.setMessage(reloadNote, true);
        }
        field.requestFocusInWindow();
    }

    // ------------------------------------------------------------------ frame loop

    private void tick() {
        long now = System.nanoTime();
        double dtWall = Math.min(0.05, (now - lastFrameNanos) / 1e9);
        lastFrameNanos = now;
        double simDt = dtWall * speed;

        if (gamepad2Box.isSelected()) {
            keyboard.applyTo(runner.gamepad2);
            runner.gamepad1.simUpdate(0, 0, 0, 0, 0, 0, NO_BUTTONS);
        } else {
            keyboard.applyTo(runner.gamepad1);
            runner.gamepad2.simUpdate(0, 0, 0, 0, 0, 0, NO_BUTTONS);
        }
        world.manualTouch = keyboard.touchHeld();
        runner.poll();
        OpModeRunner.Phase phase = runner.phase();

        int steps = Math.max(1, (int) Math.ceil(simDt / 0.004));
        double sub = simDt / steps;
        for (int i = 0; i < steps; i++) {
            clock.advance((long) (sub * 1e9));
            world.step(sub, phase == OpModeRunner.Phase.INIT, phase == OpModeRunner.Phase.RUNNING, clock.now());
        }

        World.Snapshot s = world.snapshot();
        field.show(s, trailBox.isSelected());
        armView.show(s);
        updateLabels(s, phase);
    }

    private void updateLabels(World.Snapshot s, OpModeRunner.Phase phase) {
        Mission m = s.mission;
        StringBuilder goals = new StringBuilder("<html>");
        for (int i = 0; i < m.goals.size(); i++) {
            String label = m.goals.get(i).label;
            if (i < s.goalIndex) {
                goals.append("<font color='#1a7f37'>● ").append(label).append(" - done</font><br>");
            } else if (i == s.goalIndex && s.failure == null) {
                goals.append("<b>○ ").append(label).append("</b>  &larr; next<br>");
            } else {
                goals.append("<font color='#8c959f'>○ ").append(label).append("</font><br>");
            }
        }
        if (m.goals.isEmpty()) {
            goals.append("<font color='#59636e'>No goals here. Try any lesson program.</font>");
        }
        goalsLabel.setText(goals.append("</html>").toString());

        DriverStationPanel.State st = new DriverStationPanel.State();
        st.phase = phase;
        CodeLoader.Entry entry = phase == OpModeRunner.Phase.IDLE ? null : runner.entry();
        if (entry == null) {
            entry = (CodeLoader.Entry) programBox.getSelectedItem();
        }
        if (entry != null) {
            st.programName = entry.name;
            st.programIsTeleOp = entry.teleOp;
        }
        // Run Time counts from START and freezes when the program ends.
        if (phase == OpModeRunner.Phase.RUNNING) {
            runEndNanos = -1;
        } else if (runEndNanos < 0 && world.startNanos >= 0) {
            runEndNanos = clock.now();
        }
        if (world.startNanos >= 0) {
            long end = phase == OpModeRunner.Phase.RUNNING || runEndNanos < 0 ? clock.now() : runEndNanos;
            st.runSeconds = (end - world.startNanos) / 1e9;
        }
        RunContext ctx = runner.context();
        st.telemetry = ctx == null ? null : ctx.telemetry();
        st.message = runner.message();
        st.messageIsError = runner.messageIsError();
        st.batteryVolts = batteryBox.isSelected() ? 11.6 : 12.8;
        st.user2Mode = gamepad2Box.isSelected();
        boolean keys = keyboard.anyDown() && (phase == OpModeRunner.Phase.INIT || phase == OpModeRunner.Phase.RUNNING);
        st.user1Active = keys && !st.user2Mode;
        st.user2Active = keys && st.user2Mode;
        ds.show(st);

        robotLabel.setText(String.format("<html><pre style='font-size:9px'>"
                        + "x %5.1f in   y %5.1f in   heading %4.0f°%n"
                        + "PORT       NAME          POWER   TICKS%n"
                        + "Motor 0    left_drive    %5.2f  %6d%n"
                        + "Motor 1    right_drive   %5.2f  %6d%n"
                        + "Motor 2    arm           %5.2f  %6d%n"
                        + "Servo 0    claw          position %.2f %s%n"
                        + "Digital 1  touch_sensor  %s%n"
                        + "I2C bus 1  sensor_color  brightness %.2f%s</pre></html>",
                s.x, s.y, normalize(s.headingDeg), s.leftPower, s.leftTicks, s.rightPower, s.rightTicks,
                s.armPower, s.armTicks, s.clawPos, s.clawPos >= 0.5 ? "open" : "closed",
                s.touch ? "PRESSED" : "not pressed", s.brightness,
                runner.gamepad1.isRumbling() ? "\n           gamepad RUMBLE!" : ""));
    }

    private static double normalize(double deg) {
        double d = deg % 360;
        if (d > 180) {
            d -= 360;
        } else if (d <= -180) {
            d += 360;
        }
        return d;
    }

    // ------------------------------------------------------------------ for screenshots and tests

    World world() {
        return world;
    }

    OpModeRunner runner() {
        return runner;
    }

    JComboBox<CodeLoader.Entry> programs() {
        return programBox;
    }

    void chooseMission(int number) {
        for (Mission m : missions) {
            if (m.number == number) {
                selectMission(m, true);
            }
        }
    }

    void pressInit() {
        onInit();
    }

    void advanceFrames(int frames, double secondsPerFrame) {
        for (int i = 0; i < frames; i++) {
            lastFrameNanos = System.nanoTime() - (long) (secondsPerFrame * 1e9);
            tick();
        }
    }
}
