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

    /** Tape on the floor: a path of points, 2 inches wide. */
    public static final class Tape {
        public final double[][] points;
        public final double width;
        /** How much red, green, and blue light the tape reflects (0 to 1). */
        public final float[] reflect;
        public final boolean dark;

        Tape(double[][] points, double width, float[] reflect, boolean dark) {
            this.points = points;
            this.width = width;
            this.reflect = reflect;
            this.dark = dark;
        }

        Tape shifted(double dx, double dy) {
            double[][] p = new double[points.length][];
            for (int i = 0; i < points.length; i++) {
                p[i] = new double[]{points[i][0] + dx, points[i][1] + dy};
            }
            return new Tape(p, width, reflect, dark);
        }

        /** Distance from a point to the middle of the tape. */
        double distanceTo(double x, double y) {
            double best = Double.MAX_VALUE;
            for (int i = 0; i + 1 < points.length; i++) {
                best = Math.min(best, Line2D.ptSegDist(points[i][0], points[i][1],
                        points[i + 1][0], points[i + 1][1], x, y));
            }
            return best;
        }
    }

    static final float[] BLACK = {0.04f, 0.04f, 0.05f};
    static final float[] WHITE = {0.85f, 0.86f, 0.88f};

    /**
     * Stop driving forward with the color sensor on the tape. Turning in place right away
     * counts too, but driving straight across the tape doesn't.
     */
    public static final class TapeStopGoal extends Goal {
        TapeStopGoal(String label) {
            super(label);
        }

        @Override
        boolean check(World world) {
            double[] s = world.sensorPoint();
            for (Tape t : world.tapes) {
                if (t.dark && t.distanceTo(s[0], s[1]) <= t.width / 2 + 1.5 && world.forwardSpeed() < 1) {
                    return true;
                }
            }
            return false;
        }
    }

    /** Close the claw on a block. */
    public static final class BlockHeldGoal extends Goal {
        BlockHeldGoal(String label) {
            super(label);
        }

        @Override
        boolean check(World world) {
            for (World.Block b : world.blocks) {
                if (b.held) {
                    return true;
                }
            }
            return false;
        }
    }

    /** Raise the arm to at least some angle while holding a block. */
    public static final class ArmUpGoal extends Goal {
        final double degrees;

        ArmUpGoal(String label, double degrees) {
            super(label);
            this.degrees = degrees;
        }

        @Override
        boolean check(World world) {
            if (world.armDeg < degrees) {
                return false;
            }
            for (World.Block b : world.blocks) {
                if (b.held) {
                    return true;
                }
            }
            return false;
        }
    }

    /** Drive until the front bumper (the touch sensor) touches a wall. */
    public static final class BumperGoal extends Goal {
        BumperGoal(String label) {
            super(label);
        }

        @Override
        boolean check(World world) {
            return world.bumperContact;
        }
    }

    /** Let go of a block (claw open) so it lands inside a box. */
    public static final class BlockInZoneGoal extends Goal {
        public final Rectangle2D zone;
        public final String zoneName;

        BlockInZoneGoal(String label, String zoneName, double x, double y, double w, double h) {
            super(label);
            this.zoneName = zoneName;
            this.zone = new Rectangle2D.Double(x, y, w, h);
        }

        @Override
        boolean check(World world) {
            for (World.Block b : world.blocks) {
                if (!b.held && zone.contains(b.x, b.y)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * The Grand Challenge's number. It has no number on screen and always comes last in the list,
     * however many missions there are.
     */
    public static final int FINAL = 99;

    public final int number;
    public final String title;
    public final String program;
    /** Other programs that open this mission when they're picked (like a lesson's own program). */
    public final List<String> otherPrograms;
    public final String description;
    public final double startX, startY, startHeadingDeg;
    public final List<Rectangle2D> obstacles;
    public final List<double[]> blocks;
    public final List<Goal> goals;
    public final boolean noMoveBeforeStart;
    public final boolean timed;
    public final List<Tape> tapes;
    /** If not empty, the tape moves by one of these amounts (inches) on every reset. */
    public final double[] tapeShiftsX, tapeShiftsY;

    private Mission(Builder b) {
        tapes = Collections.unmodifiableList(b.tapes);
        tapeShiftsX = b.tapeShiftsX;
        tapeShiftsY = b.tapeShiftsY;
        number = b.number;
        title = b.title;
        program = b.program;
        otherPrograms = Collections.unmodifiableList(b.otherPrograms);
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
        if (number == 0) {
            return title;
        }
        return number == FINAL ? title + " (Lesson 18)" : String.format("Mission %d: %s", number, title);
    }

    static Builder builder(int number, String title) {
        return new Builder(number, title);
    }

    static final class Builder {
        final int number;
        final String title;
        String program = "";
        final List<String> otherPrograms = new ArrayList<String>();
        String description = "";
        double startX = 12, startY = 12, startHeadingDeg = 0;
        final List<Rectangle2D> obstacles = new ArrayList<Rectangle2D>();
        final List<double[]> blocks = new ArrayList<double[]>();
        final List<Goal> goals = new ArrayList<Goal>();
        boolean noMoveBeforeStart = false;
        boolean timed = false;
        final List<Tape> tapes = new ArrayList<Tape>();
        double[] tapeShiftsX = new double[0];
        double[] tapeShiftsY = new double[0];

        Builder(int number, String title) {
            this.number = number;
            this.title = title;
        }

        Builder program(String p) {
            program = p;
            return this;
        }

        /** Picking one of these programs opens this mission too. */
        Builder alsoFor(String... programs) {
            for (String p : programs) {
                otherPrograms.add(p);
            }
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

        /** Tape through the given points: x1, y1, x2, y2, ... */
        Builder tape(boolean dark, double... xy) {
            double[][] p = new double[xy.length / 2][];
            for (int i = 0; i < p.length; i++) {
                p[i] = new double[]{xy[2 * i], xy[2 * i + 1]};
            }
            tapes.add(new Tape(p, 2.0, dark ? BLACK : WHITE, dark));
            return this;
        }

        Builder moveTapeEachReset(double... shiftsX) {
            tapeShiftsX = shiftsX;
            return this;
        }

        Builder moveTapeUpDownEachReset(double... shiftsY) {
            tapeShiftsY = shiftsY;
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
