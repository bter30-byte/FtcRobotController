package robotsim;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Looks and works like the FTC Driver Station app on a REV Driver Hub: a status bar across the
 * top, the Autonomous (left) and TeleOp (right) program lists, one big round button that goes
 * INIT, then START, then STOP, and telemetry on the right.
 */
final class DriverStationPanel extends JPanel {

    /** What the panel needs from the simulator window. */
    interface Host {
        List<CodeLoader.Entry> programs();

        CodeLoader.Entry selectedProgram();

        void chooseProgram(CodeLoader.Entry entry);

        void pressInit();

        void pressStart();

        void pressStop();

        void reloadCode();

        void resetRobot();
    }

    /** Everything that changes from frame to frame. */
    static final class State {
        OpModeRunner.Phase phase = OpModeRunner.Phase.IDLE;
        String programName;
        boolean programIsTeleOp;
        double runSeconds = -1;
        List<String> telemetry;
        String message;
        boolean messageIsError;
        double batteryVolts = 12.8;
        boolean user1Active, user2Active, user2Mode;
    }

    // Design size (the real Driver Hub screen is 800 x 480); everything scales from this.
    private static final double W = 480, H = 330;

    private static final Color BG = new Color(0x24, 0x0c, 0x4f);
    private static final Color TILE = new Color(0x34, 0x16, 0x72);
    private static final Color BAR = new Color(0x6d, 0x28, 0xd9);
    private static final Color TOP = new Color(0x19, 0x08, 0x38);
    private static final Color GREEN = new Color(0x1f, 0xb5, 0x4d);
    private static final Color BATTERY = new Color(0x43, 0xa0, 0x47);
    private static final Color LOW_BATTERY = new Color(0xe0, 0x9b, 0x1a);
    private static final Color TEXT = new Color(0xf2, 0xee, 0xfa);
    private static final Color DIM = new Color(0xb9, 0xa8, 0xe0);
    private static final Color PURPLE_TEXT = new Color(0x4c, 0x1d, 0x95);

    // Clickable areas, in design coordinates
    private static final Rectangle AUTO_ARROW = new Rectangle(0, 108, 46, 34);
    private static final Rectangle TELEOP_ARROW = new Rectangle(194, 108, 46, 34);
    private static final Rectangle NAME_BAR = new Rectangle(46, 108, 148, 34);
    private static final Rectangle MENU = new Rectangle(456, 0, 24, 30);
    private static final double BIG_X = 104, BIG_Y = 232, BIG_R = 54;
    private static final double SMALL_X = 186, SMALL_Y = 214, SMALL_R = 15;
    private static final Rectangle TELEMETRY = new Rectangle(244, 70, 232, 256);

    private final Host host;
    private final JTextArea telemetryText = new JTextArea();
    private final JScrollPane telemetryScroll = new JScrollPane(telemetryText);
    private State state = new State();
    private String lastText = "";
    private boolean lastError = false;

    DriverStationPanel(Host host) {
        this.host = host;
        setLayout(null);
        setBackground(BG);
        setPreferredSize(new Dimension((int) W, (int) H));
        setMinimumSize(new Dimension(360, 248));

        telemetryText.setEditable(false);
        telemetryText.setLineWrap(true);
        telemetryText.setWrapStyleWord(true);
        telemetryText.setBackground(BG);
        telemetryText.setForeground(TEXT);
        telemetryText.setFocusable(false);
        telemetryText.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 4));
        telemetryScroll.setBorder(BorderFactory.createEmptyBorder());
        telemetryScroll.getViewport().setBackground(BG);
        telemetryScroll.setBackground(BG);
        add(telemetryScroll);

        MouseAdapter mouse = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onClick(toDesign(e.getX(), e.getY()), e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                Point2D p = toDesign(e.getX(), e.getY());
                setCursor(Cursor.getPredefinedCursor(isClickable(p) ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
            }
        };
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
        setToolTipText("");
    }

    void show(State s) {
        state = s;
        String text;
        boolean error = false;
        if (s.message != null) {
            text = s.message;
            error = s.messageIsError;
        } else if (s.telemetry != null && !s.telemetry.isEmpty()) {
            text = String.join("\n", s.telemetry);
        } else if (s.phase == OpModeRunner.Phase.IDLE || s.phase == OpModeRunner.Phase.FINISHED
                || s.phase == OpModeRunner.Phase.STOPPED) {
            text = "Status : Robot is stopped";
        } else {
            text = "";
        }
        if (!text.equals(lastText) || error != lastError) {
            telemetryText.setText(text);
            telemetryText.setForeground(error ? new Color(0xff, 0xb4, 0xa8) : TEXT);
            telemetryText.setCaretPosition(0);
            lastText = text;
            lastError = error;
        }
        repaint();
    }

    // ---------------------------------------------------------------- geometry

    private double scale() {
        return Math.min(getWidth() / W, getHeight() / H);
    }

    private double offsetX() {
        return (getWidth() - W * scale()) / 2;
    }

    private Point2D toDesign(int x, int y) {
        double s = scale();
        return new Point2D.Double((x - offsetX()) / s, y / s);
    }

    @Override
    public void doLayout() {
        double s = scale();
        telemetryScroll.setBounds((int) (offsetX() + TELEMETRY.x * s), (int) (TELEMETRY.y * s),
                (int) (TELEMETRY.width * s), (int) (TELEMETRY.height * s));
        telemetryText.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, (int) Math.max(11, 12.5 * s)));
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        doLayout();
    }

    private boolean isClickable(Point2D p) {
        return AUTO_ARROW.contains(p) || TELEOP_ARROW.contains(p) || NAME_BAR.contains(p) || MENU.contains(p)
                || p.distance(BIG_X, BIG_Y) <= BIG_R
                || (state.phase == OpModeRunner.Phase.INIT && p.distance(SMALL_X, SMALL_Y) <= SMALL_R);
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        Point2D p = toDesign(e.getX(), e.getY());
        if (AUTO_ARROW.contains(p)) {
            return "Choose an Autonomous program";
        } else if (TELEOP_ARROW.contains(p)) {
            return "Choose a TeleOp program";
        } else if (MENU.contains(p)) {
            return "Menu: reload code, reset robot";
        } else if (state.phase == OpModeRunner.Phase.INIT && p.distance(SMALL_X, SMALL_Y) <= SMALL_R) {
            return "Stop without starting";
        }
        return null;
    }

    private void onClick(Point2D p, MouseEvent e) {
        if (AUTO_ARROW.contains(p)) {
            OpModeChooser.show(this, host, false);
        } else if (TELEOP_ARROW.contains(p)) {
            OpModeChooser.show(this, host, true);
        } else if (NAME_BAR.contains(p)) {
            OpModeChooser.show(this, host, state.programName == null || state.programIsTeleOp);
        } else if (MENU.contains(p)) {
            showMenu(e);
        } else if (p.distance(BIG_X, BIG_Y) <= BIG_R) {
            switch (state.phase) {
                case INIT:
                    host.pressStart();
                    break;
                case RUNNING:
                    host.pressStop();
                    break;
                default:
                    host.pressInit();
                    break;
            }
        } else if (state.phase == OpModeRunner.Phase.INIT && p.distance(SMALL_X, SMALL_Y) <= SMALL_R) {
            host.pressStop();
        }
    }

    private void showMenu(MouseEvent e) {
        javax.swing.JPopupMenu menu = new javax.swing.JPopupMenu();
        javax.swing.JMenuItem reload = new javax.swing.JMenuItem("Reload code");
        reload.addActionListener(ev -> host.reloadCode());
        javax.swing.JMenuItem reset = new javax.swing.JMenuItem("Reset robot");
        reset.addActionListener(ev -> host.resetRobot());
        menu.add(reload);
        menu.add(reset);
        menu.show(this, e.getX() - 120, e.getY() + 10);
    }

    // ---------------------------------------------------------------- painting

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        AffineTransform t = new AffineTransform();
        t.translate(offsetX(), 0);
        t.scale(scale(), scale());
        g.transform(t);

        g.setColor(BG);
        g.fillRect(0, 0, (int) W, (int) H);
        drawTopBar(g);
        drawLeftTiles(g);
        drawOpModeBar(g);
        drawBigButton(g);
        drawRightTiles(g);
        g.setColor(new Color(0x4a, 0x24, 0x8f));
        g.setStroke(new BasicStroke(1.5f));
        g.drawLine(240, 32, 240, (int) H);
        g.dispose();
    }

    private void drawTopBar(Graphics2D g) {
        g.setColor(TOP);
        g.fillRect(0, 0, (int) W, 30);
        // little logo square
        g.setColor(new Color(0xd3, 0x2f, 0x2f));
        g.fill(new RoundRectangle2D.Double(6, 7, 10, 16, 3, 3));
        g.setColor(new Color(0x19, 0x76, 0xd2));
        g.fill(new RoundRectangle2D.Double(17, 7, 10, 16, 3, 3));
        // green "connected" banner
        Polygon banner = new Polygon(new int[]{32, 196, 186, 32}, new int[]{4, 4, 26, 26}, 4);
        g.setColor(GREEN);
        g.fill(banner);
        text(g, "Robot Connected (simulator)", 40, 20, 11.5f, Font.BOLD, Color.WHITE);
        text(g, "Network: Simulator", 220, 13, 9f, Font.PLAIN, TEXT);
        text(g, "Ping: 0 ms", 220, 25, 9f, Font.PLAIN, TEXT);
        // gamepad users
        drawUser(g, 360, "User 1", state.user1Active, !state.user2Mode);
        drawUser(g, 404, "User 2", state.user2Active, state.user2Mode);
        // menu dots
        g.setColor(TEXT);
        for (int i = 0; i < 3; i++) {
            g.fill(new Ellipse2D.Double(466, 8 + i * 6, 3.4, 3.4));
        }
    }

    private void drawUser(Graphics2D g, int x, String label, boolean active, boolean assigned) {
        Color c = active ? new Color(0x7c, 0xff, 0x9e) : (assigned ? TEXT : new Color(0x7a, 0x6a, 0x9e));
        g.setColor(c);
        g.fill(new RoundRectangle2D.Double(x + 6, 5, 18, 9, 6, 6));
        text(g, label, x, 25, 9f, Font.PLAIN, c);
    }

    private void drawLeftTiles(Graphics2D g) {
        tile(g, 4, 34, 114, 70);
        text(g, "Run Time", 30, 52, 9.5f, Font.PLAIN, TEXT);
        String time = "0:00";
        if (state.runSeconds >= 0) {
            int sec = (int) state.runSeconds;
            time = String.format("%d:%02d", sec / 60, sec % 60);
        }
        text(g, time, 34, 84, 19f, Font.BOLD, TEXT);
        tile(g, 122, 34, 114, 70);
        // a little phone icon
        g.setColor(TEXT);
        g.setStroke(new BasicStroke(1.6f));
        g.draw(new RoundRectangle2D.Double(134, 50, 14, 24, 4, 4));
        text(g, "Config:", 156, 58, 9f, Font.PLAIN, DIM);
        text(g, "CourseBot", 156, 72, 11f, Font.BOLD, TEXT);
    }

    private void drawOpModeBar(Graphics2D g) {
        g.setColor(BAR);
        g.fillRect(0, 108, 240, 34);
        g.setColor(Color.WHITE);
        g.fill(new Polygon(new int[]{10, 36, 23}, new int[]{116, 116, 134}, 3));
        g.fill(new Polygon(new int[]{204, 230, 217}, new int[]{116, 116, 134}, 3));
        if (state.programName != null) {
            centered(g, state.programName, 120, 130, 11f, Font.BOLD, Color.WHITE, 140);
        } else {
            centered(g, "← Autonomous    TeleOp →", 120, 129, 9.5f, Font.PLAIN, TEXT, 140);
        }
    }

    private void drawBigButton(Graphics2D g) {
        // the soft purple disc behind the button, like the real app
        g.setColor(new Color(0x2f, 0x11, 0x66));
        g.fill(new Ellipse2D.Double(BIG_X - 96, BIG_Y - 82, 192, 192));
        boolean enabled = state.programName != null;
        g.setColor(enabled ? Color.WHITE : new Color(0x8e, 0x80, 0xb0));
        g.fill(new Ellipse2D.Double(BIG_X - BIG_R, BIG_Y - BIG_R, 2 * BIG_R, 2 * BIG_R));
        g.setColor(PURPLE_TEXT);
        switch (state.phase) {
            case INIT:
                g.fill(new Polygon(new int[]{(int) BIG_X - 14, (int) BIG_X + 22, (int) BIG_X - 14},
                        new int[]{(int) BIG_Y - 22, (int) BIG_Y, (int) BIG_Y + 22}, 3));
                // small stop button next to it, to stop without starting
                g.setColor(new Color(0x55, 0x33, 0x99));
                g.fill(new Ellipse2D.Double(SMALL_X - SMALL_R, SMALL_Y - SMALL_R, 2 * SMALL_R, 2 * SMALL_R));
                g.setColor(Color.WHITE);
                g.fillRect((int) SMALL_X - 5, (int) SMALL_Y - 5, 10, 10);
                break;
            case RUNNING:
                g.fillRect((int) BIG_X - 18, (int) BIG_Y - 18, 36, 36);
                break;
            default:
                centered(g, "INIT", BIG_X, BIG_Y + 8, 22f, Font.BOLD, PURPLE_TEXT, 100);
                break;
        }
        String hint;
        switch (state.phase) {
            case INIT:
                hint = "Press ▶ to START";
                break;
            case RUNNING:
                hint = "Running.  Press ■ to STOP";
                break;
            case CRASHED:
                hint = "Program crashed: read the message";
                break;
            case FINISHED:
                hint = "Program finished";
                break;
            default:
                hint = state.programName == null ? "Choose a program with ▼" : "Press INIT";
                break;
        }
        centered(g, hint, 120, 322, 9.5f, Font.PLAIN, state.phase == OpModeRunner.Phase.CRASHED
                ? new Color(0xff, 0xb4, 0xa8) : DIM, 230);
    }

    private void drawRightTiles(Graphics2D g) {
        tile(g, 244, 34, 58, 32);
        text(g, "DS: 100%", 250, 54, 9f, Font.PLAIN, TEXT);
        boolean low = state.batteryVolts < 12;
        g.setColor(low ? LOW_BATTERY : BATTERY);
        g.fill(new RoundRectangle2D.Double(306, 34, 170, 32, 6, 6));
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(1.6f));
        g.draw(new RoundRectangle2D.Double(318, 42, 30, 16, 3, 3));
        g.fillRect(348, 47, 3, 6);
        text(g, String.format("%.2f V", state.batteryVolts), 364, 56, 13f, Font.BOLD, Color.WHITE);
        text(g, low ? "low" : "", 436, 56, 10f, Font.BOLD, Color.WHITE);
    }

    // ---------------------------------------------------------------- helpers

    private static void tile(Graphics2D g, double x, double y, double w, double h) {
        g.setColor(TILE);
        g.fill(new RoundRectangle2D.Double(x, y, w, h, 6, 6));
    }

    private static void text(Graphics2D g, String s, double x, double y, float size, int style, Color c) {
        g.setFont(new Font(Font.SANS_SERIF, style, 1).deriveFont(size));
        g.setColor(c);
        g.drawString(s, (float) x, (float) y);
    }

    /** Draws text centered on cx, shrinking it to fit maxWidth. */
    private static void centered(Graphics2D g, String s, double cx, double y, float size, int style, Color c,
                                 double maxWidth) {
        Font f = new Font(Font.SANS_SERIF, style, 1).deriveFont(size);
        g.setFont(f);
        FontMetrics fm = g.getFontMetrics();
        while (fm.stringWidth(s) > maxWidth && f.getSize2D() > 7) {
            f = f.deriveFont(f.getSize2D() - 0.5f);
            g.setFont(f);
            fm = g.getFontMetrics();
        }
        g.setColor(c);
        g.drawString(s, (float) (cx - fm.stringWidth(s) / 2.0), (float) y);
    }
}
