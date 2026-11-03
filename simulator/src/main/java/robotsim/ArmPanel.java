package robotsim;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JPanel;

/** A side view of the arm, claw, and the touch sensor at the top of the arm's travel. */
final class ArmPanel extends JPanel {

    private World.Snapshot snap;

    ArmPanel() {
        setPreferredSize(new Dimension(150, 118));
        setMinimumSize(new Dimension(150, 118));
        setBackground(Color.WHITE);
    }

    void show(World.Snapshot s) {
        snap = s;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        if (snap == null) {
            return;
        }
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        double pivotX = 40, pivotY = 82, len = 62;

        // robot body (side view)
        g.setColor(new Color(0x3D4451));
        g.fill(new RoundRectangle2D.Double(10, 86, 70, 18, 6, 6));
        g.setColor(new Color(0x111111));
        g.fill(new Ellipse2D.Double(16, 98, 14, 14));
        g.fill(new Ellipse2D.Double(60, 98, 14, 14));

        // touch sensor at the top of the arm's travel
        double limitAngle = Math.toRadians(-30 + World.ARM_LIMIT_DEG + 6);
        double sx = pivotX + (len + 6) * Math.cos(limitAngle), sy = pivotY - (len + 6) * Math.sin(limitAngle);
        g.setColor(snap.touch ? new Color(0xFF3B30) : new Color(0xAFB8C1));
        g.fill(new Rectangle2D.Double(sx - 6, sy - 6, 12, 12));

        // the arm: 0 degrees = resting down in front, 100 = straight up and back a little
        double a = Math.toRadians(-30 + snap.armDeg);
        double ex = pivotX + len * Math.cos(a), ey = pivotY - len * Math.sin(a);
        g.setColor(new Color(0x57606A));
        g.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new Line2D.Double(pivotX, pivotY, ex, ey));
        g.setColor(new Color(0x1F2328));
        g.fill(new Ellipse2D.Double(pivotX - 4, pivotY - 4, 8, 8));

        // claw jaws at the end of the arm
        double spread = 2 + snap.clawPos * 9;
        double nx = -Math.sin(a), ny = -Math.cos(a);
        g.setColor(new Color(0xF0A500));
        g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int s = -1; s <= 1; s += 2) {
            double bx = ex + s * spread * nx, by = ey + s * spread * ny;
            g.draw(new Line2D.Double(bx, by, bx + 10 * Math.cos(a), by - 10 * Math.sin(a)));
        }

        g.setColor(new Color(0x59636E));
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        g.drawString("side view", 96, 14);
        g.drawString(String.format("arm %.0f°", snap.armDeg), 96, 28);
        g.dispose();
    }
}
