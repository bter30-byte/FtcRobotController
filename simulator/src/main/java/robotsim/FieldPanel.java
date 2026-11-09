package robotsim;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

import javax.swing.JPanel;

/** Draws the field from above: tiles, walls, goals, the trail, and the robot. */
final class FieldPanel extends JPanel {

    private static final Color TILE_A = new Color(0xD7DBE0);
    private static final Color TILE_B = new Color(0xCDD2D8);
    private static final Color GRID = new Color(0xB4BAC2);
    private static final Color WALL = new Color(0x8B5A2B);
    private static final Color ZONE = new Color(0x1F6FEB);
    private static final Color DONE = new Color(0x1A7F37);
    private static final Color TODO = new Color(0xE8590C);
    private static final Color TRAIL = new Color(0x8250DF);
    private static final Color ROBOT = new Color(0x3D4451);
    private static final Color BLOCK = new Color(0xF2C200);
    private static final Color BALL = new Color(0xE5534B);
    private static final Color WOOD = new Color(0xC9A26B);
    private static final Color PLYWOOD = new Color(0xDDBF8A);
    private static final Color HOLE = new Color(0x2A1E12);

    private World.Snapshot snap;
    private String hint;
    private boolean showTrail = true;
    private double scale = 5;
    private double ox = 12, oy = 12;

    FieldPanel() {
        setPreferredSize(new Dimension(700, 700));
        setBackground(new Color(0x2B2D30));
        setFocusable(true);
    }

    void show(World.Snapshot s, boolean trail, String keyHint) {
        snap = s;
        showTrail = trail;
        hint = keyHint;
        repaint();
    }

    private double px(double x) {
        return ox + x * scale;
    }

    private double py(double y) {
        return oy + (fieldH() - y) * scale;
    }

    private double fieldW() {
        return snap == null ? World.FIELD : snap.mission.fieldWidth;
    }

    private double fieldH() {
        return snap == null ? World.FIELD : snap.mission.fieldHeight;
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        if (snap == null) {
            return;
        }
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        scale = Math.min((getWidth() - 24) / fieldW(), (getHeight() - 24) / fieldH());
        ox = (getWidth() - fieldW() * scale) / 2.0;
        oy = Math.max(12, (getHeight() - fieldH() * scale) / 2.0);

        drawTiles(g);
        drawTape(g);
        drawPlatform(g);
        drawGoals(g);
        drawObstacles(g);
        if (showTrail) {
            drawTrail(g);
        }
        drawBlocks(g, false);
        drawBalls(g);
        drawRobot(g);
        drawBlocks(g, true);
        drawBanner(g);
        drawHint(g);
        g.dispose();
    }

    private void drawTiles(Graphics2D g) {
        double fw = fieldW(), fh = fieldH();
        // A field with a platform is a wooden table, like a classroom field; the others are foam tiles.
        boolean wooden = snap.mission.platform != null;
        int cols = (int) Math.ceil(fw / 24), rows = (int) Math.ceil(fh / 24);
        if (wooden) {
            g.setColor(WOOD);
            g.fill(new Rectangle2D.Double(px(0), py(fh), fw * scale, fh * scale));
        } else {
            for (int i = 0; i < cols; i++) {
                for (int j = 0; j < rows; j++) {
                    g.setColor((i + j) % 2 == 0 ? TILE_A : TILE_B);
                    double w = Math.min(24, fw - i * 24), h = Math.min(24, fh - j * 24);
                    g.fill(new Rectangle2D.Double(px(i * 24), py(j * 24 + h), w * scale, h * scale));
                }
            }
        }
        g.setColor(wooden ? alpha(WOOD.darker(), 70) : GRID);
        g.setStroke(new BasicStroke(1f));
        for (int i = 1; i * 24 < fw; i++) {
            g.draw(new Line2D.Double(px(i * 24), py(0), px(i * 24), py(fh)));
        }
        for (int j = 1; j * 24 < fh; j++) {
            g.draw(new Line2D.Double(px(0), py(j * 24), px(fw), py(j * 24)));
        }
        g.setColor(new Color(0x1F2328));
        g.setStroke(new BasicStroke((float) Math.max(3, scale)));
        g.draw(new Rectangle2D.Double(px(0), py(fh), fw * scale, fh * scale));
        // a light ruler: inches along the bottom and left edge, every 24 inches
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        g.setColor(wooden ? new Color(0x5C4326) : new Color(0x6E7781));
        for (int i = 1; i * 24 < fw; i++) {
            g.drawString(String.valueOf(i * 24), (float) px(i * 24) + 2, (float) py(0) - 3);
        }
        for (int j = 1; j * 24 < fh; j++) {
            g.drawString(String.valueOf(j * 24), (float) px(0) + 3, (float) py(j * 24) - 2);
        }
    }

    /** The raised platform in the middle, its ramp (arrows point uphill), and the holes in it. */
    private void drawPlatform(Graphics2D g) {
        Mission m = snap.mission;
        if (m.platform != null) {
            Rectangle2D p = m.platform;
            g.setColor(new Color(0, 0, 0, 60));
            g.fill(new Rectangle2D.Double(px(p.getX()) + 4, py(p.getMaxY()) + 4, p.getWidth() * scale,
                    p.getHeight() * scale));
            g.setColor(PLYWOOD);
            g.fill(new Rectangle2D.Double(px(p.getX()), py(p.getMaxY()), p.getWidth() * scale,
                    p.getHeight() * scale));
        }
        if (m.ramp != null) {
            Rectangle2D r = m.ramp;
            Rectangle2D s = new Rectangle2D.Double(px(r.getX()), py(r.getMaxY()), r.getWidth() * scale,
                    r.getHeight() * scale);
            g.setColor(new Color(0xB8925A));
            g.fill(s);
            g.setColor(new Color(0x7A5A2E));
            g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            double cx = r.getCenterX();
            for (double y = r.getY() + 3; y < r.getMaxY() - 1; y += 4) {
                Path2D chevron = new Path2D.Double();
                chevron.moveTo(px(cx - 5), py(y));
                chevron.lineTo(px(cx), py(y + 2.5));
                chevron.lineTo(px(cx + 5), py(y));
                g.draw(chevron);
            }
            label(g, "RAMP", px(r.getX() + 4.5), py(r.getCenterY()), new Color(0x7A5A2E));
        }
        for (Mission.Hole h : m.holes) {
            double half = h.size / 2;
            java.awt.Shape shape = h.round
                    ? new Ellipse2D.Double(px(h.x - half), py(h.y + half), h.size * scale, h.size * scale)
                    : new Rectangle2D.Double(px(h.x - half), py(h.y + half), h.size * scale, h.size * scale);
            g.setColor(HOLE);
            g.fill(shape);
            g.setColor(new Color(0x7A5A2E));
            g.setStroke(new BasicStroke(1.5f));
            g.draw(shape);
        }
    }

    private void drawTape(Graphics2D g) {
        List<Mission.Tape> ordered = new java.util.ArrayList<Mission.Tape>();
        for (Mission.Tape t : snap.tapes) {
            if (!t.dark) {
                ordered.add(t);
            }
        }
        for (Mission.Tape t : snap.tapes) {
            if (t.dark) {
                ordered.add(t);
            }
        }
        for (Mission.Tape t : ordered) {
            Path2D p = new Path2D.Double();
            p.moveTo(px(t.points[0][0]), py(t.points[0][1]));
            for (int i = 1; i < t.points.length; i++) {
                p.lineTo(px(t.points[i][0]), py(t.points[i][1]));
            }
            float w = (float) (t.width * scale);
            if (!t.dark) {
                g.setColor(new Color(0xAFB8C1));
                g.setStroke(new BasicStroke(w + 2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
                g.draw(p);
            }
            g.setColor(t.dark ? new Color(0x111111) : new Color(0xFAFAFA));
            g.setStroke(new BasicStroke(w, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
            g.draw(p);
        }
    }

    private void drawGoals(Graphics2D g) {
        Mission m = snap.mission;
        for (int i = 0; i < m.goals.size(); i++) {
            Mission.Goal goal = m.goals.get(i);
            boolean done = i < snap.goalIndex;
            boolean current = i == snap.goalIndex;
            Color c = done ? DONE : (goal instanceof Mission.ZoneGoal ? ZONE : TODO);
            if (goal instanceof Mission.BallInZoneGoal) {
                Mission.BallInZoneGoal z = (Mission.BallInZoneGoal) goal;
                Color bc = done ? DONE : BALL;
                Rectangle2D r = new Rectangle2D.Double(px(z.zone.getX()), py(z.zone.getMaxY()),
                        z.zone.getWidth() * scale, z.zone.getHeight() * scale);
                g.setColor(alpha(bc, current || done ? 70 : 40));
                g.fill(r);
                g.setColor(bc);
                g.setStroke(new BasicStroke(3f));
                g.draw(r);
                label(g, z.zoneName, r.getCenterX(), r.getCenterY(), bc);
            } else if (goal instanceof Mission.ZoneGoal) {
                Mission.ZoneGoal z = (Mission.ZoneGoal) goal;
                Rectangle2D r = new Rectangle2D.Double(px(z.zone.getX()), py(z.zone.getMaxY()),
                        z.zone.getWidth() * scale, z.zone.getHeight() * scale);
                g.setColor(alpha(c, current || done ? 90 : 50));
                g.fill(r);
                g.setColor(c);
                g.setStroke(new BasicStroke(3f));
                g.draw(r);
                label(g, z.zoneName, r.getCenterX(), r.getCenterY(), c);
            } else if (goal instanceof Mission.BlockInZoneGoal) {
                Mission.BlockInZoneGoal z = (Mission.BlockInZoneGoal) goal;
                Color zc = done ? DONE : BLOCK.darker();
                Rectangle2D r = new Rectangle2D.Double(px(z.zone.getX()), py(z.zone.getMaxY()),
                        z.zone.getWidth() * scale, z.zone.getHeight() * scale);
                g.setColor(alpha(zc, current || done ? 80 : 45));
                g.fill(r);
                g.setColor(zc);
                g.setStroke(new BasicStroke(3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f,
                        new float[]{10f, 6f}, 0f));
                g.draw(r);
                label(g, z.zoneName, r.getCenterX(), r.getY() + 14 * scale / 5, zc);
            } else if (goal instanceof Mission.FacingGoal) {
                Mission.FacingGoal f = (Mission.FacingGoal) goal;
                double rr = f.radius * scale;
                Ellipse2D e = new Ellipse2D.Double(px(f.x) - rr, py(f.y) - rr, 2 * rr, 2 * rr);
                g.setColor(alpha(c, current || done ? 90 : 50));
                g.fill(e);
                g.setColor(c);
                g.setStroke(new BasicStroke(2.5f));
                g.draw(e);
                // an arrow: which way to face
                double a = Math.toRadians(f.headingDeg);
                double tipX = f.x + Math.cos(a) * f.radius * 0.8, tipY = f.y + Math.sin(a) * f.radius * 0.8;
                g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.draw(new Line2D.Double(px(f.x - Math.cos(a) * f.radius * 0.5), py(f.y - Math.sin(a) * f.radius * 0.5),
                        px(tipX), py(tipY)));
                for (int side = -1; side <= 1; side += 2) {
                    double b = a + Math.PI + side * 0.5;
                    g.draw(new Line2D.Double(px(tipX), py(tipY), px(tipX + Math.cos(b) * 3), py(tipY + Math.sin(b) * 3)));
                }
            } else if (goal instanceof Mission.WaypointGoal) {
                Mission.WaypointGoal w = (Mission.WaypointGoal) goal;
                double rr = w.radius * scale;
                Ellipse2D e = new Ellipse2D.Double(px(w.x) - rr, py(w.y) - rr, 2 * rr, 2 * rr);
                g.setColor(alpha(c, current || done ? 110 : 60));
                g.fill(e);
                g.setColor(c);
                g.setStroke(new BasicStroke(2.5f));
                g.draw(e);
                label(g, w.tag, px(w.x), py(w.y), c);
            } else if (goal instanceof Mission.GateGoal) {
                Mission.GateGoal gate = (Mission.GateGoal) goal;
                Line2D l = gate.line;
                g.setColor(c);
                g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f,
                        new float[]{8f, 7f}, 0f));
                g.draw(new Line2D.Double(px(l.getX1()), py(l.getY1()), px(l.getX2()), py(l.getY2())));
                double mx = (l.getX1() + l.getX2()) / 2, my = (l.getY1() + l.getY2()) / 2;
                label(g, gate.tag, px(mx) + 16, py(my), c);
            }
        }
    }

    private void label(Graphics2D g, String text, double cx, double cy, Color c) {
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int) Math.max(11, scale * 2.6)));
        FontMetrics fm = g.getFontMetrics();
        int w = fm.stringWidth(text);
        double bx = cx - w / 2.0 - 5, by = cy - fm.getAscent() / 2.0 - 4;
        g.setColor(new Color(255, 255, 255, 210));
        g.fill(new RoundRectangle2D.Double(bx, by, w + 10, fm.getAscent() + 8, 8, 8));
        g.setColor(c);
        g.drawString(text, (float) (cx - w / 2.0), (float) (cy + fm.getAscent() / 2.0 - 1));
    }

    private void drawObstacles(Graphics2D g) {
        for (Rectangle2D r : snap.mission.obstacles) {
            Rectangle2D s = new Rectangle2D.Double(px(r.getX()), py(r.getMaxY()), r.getWidth() * scale,
                    r.getHeight() * scale);
            g.setColor(r.getWidth() <= 3 && r.getHeight() <= 3 ? TODO : WALL);
            if (r.getWidth() <= 3 && r.getHeight() <= 3) {
                g.fill(new Ellipse2D.Double(s.getX(), s.getY(), s.getWidth(), s.getHeight()));
            } else {
                g.fill(s);
            }
        }
    }

    private void drawTrail(Graphics2D g) {
        List<double[]> t = snap.trail;
        if (t.size() < 2) {
            return;
        }
        Path2D p = new Path2D.Double();
        p.moveTo(px(t.get(0)[0]), py(t.get(0)[1]));
        for (int i = 1; i < t.size(); i++) {
            p.lineTo(px(t.get(i)[0]), py(t.get(i)[1]));
        }
        g.setColor(alpha(TRAIL, 170));
        g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(p);
    }

    private void drawBlocks(Graphics2D g0, boolean heldOnly) {
        for (double[] b : snap.blocks) {
            boolean held = b[2] > 0 || b[5] > 0;
            if (held != heldOnly) {
                continue;
            }
            boolean inHole = b.length > 7 && b[7] > 0;
            Graphics2D g = (Graphics2D) g0.create();
            g.translate(px(b[0]), py(b[1]));
            g.rotate(-Math.toRadians(b[3]));
            double s = World.BLOCK_SIZE * scale * (inHole ? 0.85 : 1);
            Rectangle2D r = new Rectangle2D.Double(-s / 2, -s / 2, s, s);
            // See-through when it's lifted off the floor
            boolean lifted = (b[2] > 0 && snap.armDeg > 30) || b[4] > 0.5;
            g.setColor(inHole ? BLOCK.darker() : lifted ? alpha(BLOCK, 150) : BLOCK);
            g.fill(r);
            g.setColor(new Color(0x9A7B00));
            g.setStroke(new BasicStroke(1.5f));
            g.draw(r);
            if (b[6] > 0) {
                // the metal handle on top, for a hook
                g.setColor(new Color(0x57606A));
                g.fill(new Rectangle2D.Double(-s * 0.3, -s * 0.07, s * 0.6, s * 0.14));
            }
            g.dispose();
        }
    }

    private void drawBalls(Graphics2D g) {
        for (double[] b : snap.balls) {
            boolean inHole = b.length > 3 && b[3] > 0;
            double r = b[2] * scale * (inHole ? 0.85 : 1);
            Ellipse2D e = new Ellipse2D.Double(px(b[0]) - r, py(b[1]) - r, 2 * r, 2 * r);
            g.setColor(inHole ? BALL.darker() : BALL);
            g.fill(e);
            g.setColor(BALL.darker());
            g.setStroke(new BasicStroke(1.5f));
            g.draw(e);
            // a highlight, so it reads as a ball
            g.setColor(new Color(255, 255, 255, 120));
            g.fill(new Ellipse2D.Double(px(b[0]) - r * 0.45, py(b[1]) - r * 0.6, r * 0.6, r * 0.45));
        }
    }

    private void drawRobot(Graphics2D g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.translate(px(snap.x), py(snap.y));
        g.rotate(-Math.toRadians(snap.headingDeg));
        g.scale(scale, -scale); // robot coordinates: forward = +x, left = +y
        g.setStroke(new BasicStroke(0.4f));

        // claw (drawn first so the body overlaps its base)
        double open = 1.5 + snap.clawPos * 4.5;
        boolean up = snap.armDeg > 30;
        Color clawColor = alpha(new Color(0xF0A500), up ? 110 : 255);
        g.setColor(clawColor);
        g.fill(new Rectangle2D.Double(8, open - 0.6, 5.5, 1.4));
        g.fill(new Rectangle2D.Double(8, -open - 0.8, 5.5, 1.4));

        double halfL = snap.robot.length / 2, halfW = snap.robot.width / 2;
        g.setColor(ROBOT);
        g.fill(new RoundRectangle2D.Double(-halfL, -halfW, 2 * halfL, 2 * halfW, 3, 3));
        // wheels
        g.setColor(new Color(0x111111));
        g.fill(new RoundRectangle2D.Double(-4.5, halfW - 2.8, 9, 2.8, 1, 1));
        g.fill(new RoundRectangle2D.Double(-4.5, -halfW, 9, 2.8, 1, 1));
        // bumper (red when the touch sensor is pressed)
        g.setColor(snap.touch ? new Color(0xFF3B30) : new Color(0x6E7781));
        g.fill(new Rectangle2D.Double(halfL - 0.8, -halfW + 1, 1.6, 2 * halfW - 2));
        // arrow showing the front
        g.setColor(Color.WHITE);
        Polygon arrow = new Polygon(new int[]{6, -3, -3}, new int[]{0, 4, -4}, 3);
        g.fill(arrow);
        // color sensor, looking down: its dot shows the shade of the floor under it
        int shade = (int) Math.round(Math.min(1.0, snap.surface / 0.9) * 255);
        g.setColor(new Color(0x00B4D8));
        double ahead = snap.robot.colorSensorAhead;
        g.fill(new Ellipse2D.Double(ahead - 1.3, -1.3, 2.6, 2.6));
        g.setColor(new Color(shade, shade, shade));
        g.fill(new Ellipse2D.Double(ahead - 0.8, -0.8, 1.6, 1.6));
        g.dispose();

        // wheel labels, drawn without rotation so the letters stay readable
        g0.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int) Math.max(9, scale * 2)));
        g0.setColor(Color.WHITE);
        double h = Math.toRadians(snap.headingDeg);
        drawCentered(g0, "L", snap.x - 4.3 * Math.sin(h), snap.y + 4.3 * Math.cos(h));
        drawCentered(g0, "R", snap.x + 4.3 * Math.sin(h), snap.y - 4.3 * Math.cos(h));
        if (snap.blocked) {
            label(g0, "BLOCKED", px(snap.x), py(snap.y) - 14 * scale, new Color(0xCF222E));
        }
    }

    private void drawCentered(Graphics2D g, String s, double fx, double fy) {
        FontMetrics fm = g.getFontMetrics();
        g.drawString(s, (float) (px(fx) - fm.stringWidth(s) / 2.0), (float) (py(fy) + fm.getAscent() / 2.0 - 1));
    }

    private void drawBanner(Graphics2D g) {
        String text = null;
        Color c = null;
        if (snap.failure != null) {
            text = "MISSION FAILED: " + snap.failure;
            c = new Color(0xCF222E);
        } else if (snap.complete) {
            text = snap.completeSeconds >= 0
                    ? String.format("MISSION COMPLETE!   %.1f seconds", snap.completeSeconds)
                    : "MISSION COMPLETE!";
            c = DONE;
        }
        if (text == null) {
            return;
        }
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int) Math.max(15, scale * 3.4)));
        FontMetrics fm = g.getFontMetrics();
        int w = fm.stringWidth(text) + 40;
        int h = fm.getHeight() + 20;
        double x = px(fieldW() / 2) - w / 2.0;
        double y = py(fieldH()) + 14;
        g.setColor(alpha(c, 235));
        g.fill(new RoundRectangle2D.Double(x, y, w, h, 16, 16));
        g.setColor(Color.WHITE);
        g.drawString(text, (float) (x + 20), (float) (y + 10 + fm.getAscent()));
    }

    /** A note at the bottom of the field, e.g. when keys are pressed but no program reads them. */
    private void drawHint(Graphics2D g) {
        if (hint == null) {
            return;
        }
        String[] lines = hint.split("\n");
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int) Math.max(12, scale * 2.4)));
        FontMetrics fm = g.getFontMetrics();
        int w = 0;
        for (String line : lines) {
            w = Math.max(w, fm.stringWidth(line));
        }
        w += 32;
        int lh = fm.getHeight();
        int h = lh * lines.length + 18;
        double x = px(fieldW() / 2) - w / 2.0;
        double y = py(0) - h - 12;
        g.setColor(new Color(0x1F, 0x23, 0x28, 225));
        g.fill(new RoundRectangle2D.Double(x, y, w, h, 14, 14));
        for (int i = 0; i < lines.length; i++) {
            g.setColor(i == 0 ? new Color(0xFF, 0xD3, 0x3D) : Color.WHITE);
            g.drawString(lines[i], (float) (x + 16), (float) (y + 9 + fm.getAscent() + i * lh));
        }
    }

    private static Color alpha(Color c, int a) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), a);
    }
}
