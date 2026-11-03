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

    private World.Snapshot snap;
    private boolean showTrail = true;
    private double scale = 5;
    private double ox = 12, oy = 12;

    FieldPanel() {
        setPreferredSize(new Dimension(700, 700));
        setBackground(new Color(0x2B2D30));
        setFocusable(true);
    }

    void show(World.Snapshot s, boolean trail) {
        snap = s;
        showTrail = trail;
        repaint();
    }

    private double px(double x) {
        return ox + x * scale;
    }

    private double py(double y) {
        return oy + (World.FIELD - y) * scale;
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
        int size = Math.min(getWidth(), getHeight()) - 24;
        scale = size / World.FIELD;
        ox = (getWidth() - size) / 2.0;
        oy = 12;

        drawTiles(g);
        drawGoals(g);
        drawObstacles(g);
        if (showTrail) {
            drawTrail(g);
        }
        drawBlocks(g, false);
        drawRobot(g);
        drawBlocks(g, true);
        drawBanner(g);
        g.dispose();
    }

    private void drawTiles(Graphics2D g) {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                g.setColor((i + j) % 2 == 0 ? TILE_A : TILE_B);
                g.fill(new Rectangle2D.Double(px(i * 24), py((j + 1) * 24), 24 * scale, 24 * scale));
            }
        }
        g.setColor(GRID);
        g.setStroke(new BasicStroke(1f));
        for (int i = 1; i < 6; i++) {
            g.draw(new Line2D.Double(px(i * 24), py(0), px(i * 24), py(144)));
            g.draw(new Line2D.Double(px(0), py(i * 24), px(144), py(i * 24)));
        }
        g.setColor(new Color(0x1F2328));
        g.setStroke(new BasicStroke((float) Math.max(3, scale)));
        g.draw(new Rectangle2D.Double(px(0), py(144), 144 * scale, 144 * scale));
        // a light ruler: inches along the bottom and left edge, every tile
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        g.setColor(new Color(0x6E7781));
        for (int i = 1; i < 6; i++) {
            g.drawString(String.valueOf(i * 24), (float) px(i * 24) + 2, (float) py(0) - 3);
            g.drawString(String.valueOf(i * 24), (float) px(0) + 3, (float) py(i * 24) - 2);
        }
    }

    private void drawGoals(Graphics2D g) {
        Mission m = snap.mission;
        for (int i = 0; i < m.goals.size(); i++) {
            Mission.Goal goal = m.goals.get(i);
            boolean done = i < snap.goalIndex;
            boolean current = i == snap.goalIndex;
            Color c = done ? DONE : (goal instanceof Mission.ZoneGoal ? ZONE : TODO);
            if (goal instanceof Mission.ZoneGoal) {
                Mission.ZoneGoal z = (Mission.ZoneGoal) goal;
                Rectangle2D r = new Rectangle2D.Double(px(z.zone.getX()), py(z.zone.getMaxY()),
                        z.zone.getWidth() * scale, z.zone.getHeight() * scale);
                g.setColor(alpha(c, current || done ? 90 : 50));
                g.fill(r);
                g.setColor(c);
                g.setStroke(new BasicStroke(3f));
                g.draw(r);
                label(g, z.zoneName, r.getCenterX(), r.getCenterY(), c);
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

    private void drawBlocks(Graphics2D g, boolean heldOnly) {
        for (double[] b : snap.blocks) {
            boolean held = b[2] > 0;
            if (held != heldOnly) {
                continue;
            }
            double s = 6 * scale;
            Rectangle2D r = new Rectangle2D.Double(px(b[0]) - s / 2, py(b[1]) - s / 2, s, s);
            g.setColor(held && snap.armDeg > 30 ? alpha(BLOCK, 150) : BLOCK);
            g.fill(r);
            g.setColor(new Color(0x9A7B00));
            g.setStroke(new BasicStroke(1.5f));
            g.draw(r);
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

        g.setColor(ROBOT);
        g.fill(new RoundRectangle2D.Double(-9, -9, 18, 18, 3, 3));
        // wheels
        g.setColor(new Color(0x111111));
        g.fill(new RoundRectangle2D.Double(-4.5, 6.2, 9, 2.8, 1, 1));
        g.fill(new RoundRectangle2D.Double(-4.5, -9, 9, 2.8, 1, 1));
        // bumper (red when the touch sensor is pressed)
        g.setColor(snap.touch ? new Color(0xFF3B30) : new Color(0x6E7781));
        g.fill(new Rectangle2D.Double(8.2, -8, 1.6, 16));
        // arrow showing the front
        g.setColor(Color.WHITE);
        Polygon arrow = new Polygon(new int[]{6, -3, -3}, new int[]{0, 4, -4}, 3);
        g.fill(arrow);
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
        double x = px(72) - w / 2.0;
        double y = py(144) + 14;
        g.setColor(alpha(c, 235));
        g.fill(new RoundRectangle2D.Double(x, y, w, h, 16, 16));
        g.setColor(Color.WHITE);
        g.drawString(text, (float) (x + 20), (float) (y + 10 + fm.getAscent()));
    }

    private static Color alpha(Color c, int a) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), a);
    }
}
