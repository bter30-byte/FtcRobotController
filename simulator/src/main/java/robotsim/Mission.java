package robotsim;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A field layout plus a list of goals to complete in order. Field units are inches.
 * (0, 0) is the bottom-left corner; x goes right, y goes up; heading 0 points right.
 */
public final class Mission {

    /** One thing the robot has to do. Goals are checked in order. */
    public abstract static class Goal {
        public final String label;

        Goal(String label) {
            this.label = label;
        }

        abstract boolean check(World world);
    }

    /** Stop with the robot's center inside a box. */
    public static final class ZoneGoal extends Goal {
        public final Rectangle2D zone;
        public final String zoneName;

        ZoneGoal(String label, String zoneName, double x, double y, double w, double h) {
            super(label);
            this.zoneName = zoneName;
            this.zone = new Rectangle2D.Double(x, y, w, h);
        }

        @Override
        boolean check(World world) {
            return zone.contains(world.x, world.y) && world.isStopped();
        }
    }

    /** Drive the robot's center through a circle. */
    public static final class WaypointGoal extends Goal {
        public final double x, y, radius;
        public final String tag;

        WaypointGoal(String label, String tag, double x, double y, double radius) {
            super(label);
            this.tag = tag;
            this.x = x;
            this.y = y;
            this.radius = radius;
        }

        @Override
        boolean check(World world) {
            return Math.hypot(world.x - x, world.y - y) <= radius;
        }
    }

    /** Drive the robot's center across a line between two cones. */
    public static final class GateGoal extends Goal {
        public final Line2D line;
        public final String tag;

        GateGoal(String label, String tag, double x1, double y1, double x2, double y2) {
            super(label);
            this.tag = tag;
            this.line = new Line2D.Double(x1, y1, x2, y2);
        }

        @Override
        boolean check(World world) {
            return line.intersectsLine(world.prevX, world.prevY, world.x, world.y);
        }
    }

    public final int number;
    public final String title;
    public final String program;
    public final String description;
    public final double startX, startY, startHeadingDeg;
    public final List<Rectangle2D> obstacles;
    public final List<double[]> blocks;
    public final List<Goal> goals;
    public final boolean noMoveBeforeStart;
    public final boolean timed;

    private Mission(Builder b) {
        number = b.number;
        title = b.title;
        program = b.program;
        description = b.description;
        startX = b.startX;
        startY = b.startY;
        startHeadingDeg = b.startHeadingDeg;
        obstacles = Collections.unmodifiableList(b.obstacles);
        blocks = Collections.unmodifiableList(b.blocks);
        goals = Collections.unmodifiableList(b.goals);
        noMoveBeforeStart = b.noMoveBeforeStart;
        timed = b.timed;
    }

    @Override
    public String toString() {
        return number == 0 ? title : String.format("Mission %d: %s", number, title);
    }

    static Builder builder(int number, String title) {
        return new Builder(number, title);
    }

    static final class Builder {
        final int number;
        final String title;
        String program = "";
        String description = "";
        double startX = 12, startY = 12, startHeadingDeg = 0;
        final List<Rectangle2D> obstacles = new ArrayList<Rectangle2D>();
        final List<double[]> blocks = new ArrayList<double[]>();
        final List<Goal> goals = new ArrayList<Goal>();
        boolean noMoveBeforeStart = false;
        boolean timed = false;

        Builder(int number, String title) {
            this.number = number;
            this.title = title;
        }

        Builder program(String p) {
            program = p;
            return this;
        }

        Builder describe(String d) {
            description = d;
            return this;
        }

        Builder start(double x, double y, double headingDeg) {
            startX = x;
            startY = y;
            startHeadingDeg = headingDeg;
            return this;
        }

        Builder wall(double x, double y, double w, double h) {
            obstacles.add(new Rectangle2D.Double(x, y, w, h));
            return this;
        }

        /** A small cone, 3 inches across, centered at (x, y). */
        Builder cone(double x, double y) {
            return wall(x - 1.5, y - 1.5, 3, 3);
        }

        Builder block(double x, double y) {
            blocks.add(new double[]{x, y});
            return this;
        }

        Builder goal(Goal g) {
            goals.add(g);
            return this;
        }

        Builder noMoveBeforeStart() {
            noMoveBeforeStart = true;
            return this;
        }

        Builder timed() {
            timed = true;
            return this;
        }

        Mission build() {
            return new Mission(this);
        }
    }
}
