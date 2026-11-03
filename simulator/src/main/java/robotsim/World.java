package robotsim;

import com.qualcomm.robotcore.hardware.DcMotor;

import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * The simulated field and robot. Units are inches, seconds, and degrees.
 *
 * The robot matches the course's robot configuration: two drive motors (the left one is
 * mounted mirrored, so programs must reverse it), an arm motor with a touch sensor at the top,
 * and a claw servo. The touch sensor is ALSO pressed when the front bumper touches a wall,
 * so it works for both the arm lessons and the bumper lessons. Space presses it by hand.
 *
 * Everything here is guarded by synchronizing on this World object.
 */
public final class World {

    // ---- robot geometry and physics ----
    public static final double FIELD = 144;
    public static final double ROBOT = 18;
    public static final double TRACK_WIDTH = 15;
    /** Wheel speed at full power, inches per second. */
    public static final double MAX_SPEED = 40;
    /** goBILDA 312 RPM motor (537.7 ticks/rev) with 96 mm wheels: same numbers as Lesson 12. */
    public static final double TICKS_PER_INCH = 537.7 / (3.78 * Math.PI);
    public static final double ARM_MAX_DEG = 100;
    public static final double ARM_LIMIT_DEG = 95;
    public static final double ARM_SPEED_DEG = 120;
    public static final double ARM_TICKS_PER_DEG = 5;
    public static final double CLAW_SPEED = 2.0;

    final MotorState left = new MotorState("left_drive", -1, TICKS_PER_INCH, 1.0 / (2 * TICKS_PER_INCH), 10);
    final MotorState right = new MotorState("right_drive", +1, TICKS_PER_INCH, 1.0 / (2 * TICKS_PER_INCH), 10);
    final MotorState arm = new MotorState("arm", +1, ARM_TICKS_PER_DEG, 1.0 / (10 * ARM_TICKS_PER_DEG), 5);

    // pose of the robot's center
    double x, y, headingRad;
    double prevX, prevY;
    double armDeg = 0;
    double clawTarget = 0.5;
    double clawPos = 0.5;
    boolean clawReversed = false;
    boolean blocked = false;
    boolean bumperContact = false;
    volatile boolean manualTouch = false;
    /** 1.0 = full battery. A low battery makes every motor weaker, like on a real robot. */
    volatile double battery = 1.0;
    final List<Block> blocks = new ArrayList<Block>();
    final List<double[]> trail = new ArrayList<double[]>();

    // ---- mission progress ----
    Mission mission;
    int goalIndex = 0;
    long startNanos = -1;
    long completeNanos = -1;
    String failure = null;
    double initX, initY, initHeading;

    /** A yellow block the claw can pick up. */
    static final class Block {
        double x, y;
        boolean held;

        Block(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    public World(Mission mission) {
        reset(mission);
    }

    /** Puts the robot back at the mission's start, like turning the robot off and on. */
    public synchronized void reset(Mission m) {
        mission = m;
        x = prevX = initX = m.startX;
        y = prevY = initY = m.startY;
        headingRad = Math.toRadians(m.startHeadingDeg);
        left.resetAll();
        right.resetAll();
        arm.resetAll();
        armDeg = 0;
        clawTarget = clawPos = 0.5;
        clawReversed = false;
        blocked = false;
        bumperContact = false;
        blocks.clear();
        for (double[] b : m.blocks) {
            blocks.add(new Block(b[0], b[1]));
        }
        trail.clear();
        trail.add(new double[]{x, y});
        resetProgress();
    }

    synchronized void resetProgress() {
        goalIndex = 0;
        startNanos = -1;
        completeNanos = -1;
        failure = null;
    }

    // ---- program lifecycle hooks ----

    synchronized void onInit() {
        resetProgress();
        initX = x;
        initY = y;
        initHeading = headingRad;
    }

    synchronized void onStart(long nowNanos) {
        startNanos = nowNanos;
    }

    /** When a program ends, the SDK cuts power to every motor. */
    synchronized void stopAllMotors() {
        left.power = 0;
        right.power = 0;
        arm.power = 0;
    }

    // ---- physics ----

    /** Moves the simulation forward by dt seconds. */
    synchronized void step(double dt, boolean initPhase, boolean runPhase, long nowNanos) {
        stepDrive(dt);
        stepArm(dt);
        stepClaw(dt);
        bumperContact = intersectsAnything(frontStrip());
        recordTrail();
        checkMission(initPhase, runPhase, nowNanos);
    }

    private void stepDrive(double dt) {
        for (MotorState m : new MotorState[]{left, right}) {
            double v = m.voltage();
            double target = v * m.mount * MAX_SPEED * battery;
            double tau;
            if (Math.abs(v) > 0.001) {
                tau = 0.1;
            } else {
                // BRAKE stops almost at once; FLOAT coasts a little.
                tau = m.zeroPowerBehavior == DcMotor.ZeroPowerBehavior.BRAKE ? 0.04 : 0.15;
            }
            m.speed += (target - m.speed) * (1 - Math.exp(-dt / tau));
        }
        double vL = left.speed, vR = right.speed;
        double v = (vL + vR) / 2.0;
        double omega = (vR - vL) / TRACK_WIDTH;
        double newHeading = headingRad + omega * dt;
        double midHeading = headingRad + omega * dt / 2.0;
        double nx = x + v * Math.cos(midHeading) * dt;
        double ny = y + v * Math.sin(midHeading) * dt;

        prevX = x;
        prevY = y;
        if (intersectsAnything(robotShape(nx, ny, newHeading))) {
            // Stuck against something: wheels stall, so encoders stop counting too.
            blocked = true;
            left.speed = 0;
            right.speed = 0;
            return;
        }
        blocked = false;
        x = nx;
        y = ny;
        headingRad = newHeading;
        left.rawTicks += vL * left.mount * TICKS_PER_INCH * dt;
        right.rawTicks += vR * right.mount * TICKS_PER_INCH * dt;
        for (Block b : blocks) {
            if (b.held) {
                double[] p = clawPoint();
                b.x = p[0];
                b.y = p[1];
            }
        }
    }

    private void stepArm(double dt) {
        double v = arm.voltage();
        double rate;
        if (Math.abs(v) > 0.001) {
            rate = v * ARM_SPEED_DEG * battery;
        } else {
            // With FLOAT, gravity slowly pulls the arm down. BRAKE holds it.
            rate = arm.zeroPowerBehavior == DcMotor.ZeroPowerBehavior.BRAKE ? 0 : -40;
        }
        armDeg = MotorState.clip(armDeg + rate * dt, 0, ARM_MAX_DEG);
        arm.speed = rate;
        arm.rawTicks = armDeg * ARM_TICKS_PER_DEG;
    }

    private void stepClaw(double dt) {
        boolean wasOpen = clawPos >= 0.5;
        double step = CLAW_SPEED * dt;
        if (Math.abs(clawTarget - clawPos) <= step) {
            clawPos = clawTarget;
        } else {
            clawPos += Math.signum(clawTarget - clawPos) * step;
        }
        boolean open = clawPos >= 0.5;
        if (wasOpen && !open && armDeg < 30) {
            // Closing: grab a block that is right in front of the robot.
            for (Block b : blocks) {
                if (!b.held && grabZone().contains(b.x, b.y)) {
                    b.held = true;
                    break;
                }
            }
        } else if (!wasOpen && open) {
            for (Block b : blocks) {
                b.held = false;
            }
        }
    }

    private void recordTrail() {
        double[] last = trail.get(trail.size() - 1);
        if (Math.hypot(x - last[0], y - last[1]) > 0.5 && trail.size() < 50000) {
            trail.add(new double[]{x, y});
        }
    }

    private void checkMission(boolean initPhase, boolean runPhase, long nowNanos) {
        if (mission.goals.isEmpty() || completeNanos >= 0 || failure != null) {
            return;
        }
        if (mission.noMoveBeforeStart && initPhase
                && (Math.hypot(x - initX, y - initY) > 1.0 || Math.abs(headingRad - initHeading) > Math.toRadians(3))) {
            failure = "The robot moved before START was pressed!";
            return;
        }
        // Keep checking after the program ends: the robot may still be coasting to a stop.
        if (!runPhase && !initPhase && startNanos < 0) {
            return;
        }
        while (goalIndex < mission.goals.size() && mission.goals.get(goalIndex).check(this)) {
            goalIndex++;
        }
        if (goalIndex >= mission.goals.size()) {
            completeNanos = nowNanos;
        }
    }

    // ---- sensors ----

    boolean touchPressed() {
        return bumperContact || armDeg >= ARM_LIMIT_DEG || manualTouch;
    }

    boolean isStopped() {
        return Math.abs(left.speed) < 1 && Math.abs(right.speed) < 1;
    }

    // ---- geometry ----

    private Area robotShape(double cx, double cy, double heading) {
        return shape(cx, cy, heading, -ROBOT / 2, -ROBOT / 2, ROBOT, ROBOT);
    }

    /** A thin strip along the front of the robot: the bumper. */
    private Area frontStrip() {
        return shape(x, y, headingRad, ROBOT / 2, -ROBOT / 2 + 1, 1.5, ROBOT - 2);
    }

    private Area grabZone() {
        return shape(x, y, headingRad, ROBOT / 2 - 2, -5, 9, 10);
    }

    /** Where a held block sits: just in front of the claw. */
    private double[] clawPoint() {
        double d = ROBOT / 2 + 3;
        return new double[]{x + d * Math.cos(headingRad), y + d * Math.sin(headingRad)};
    }

    /** A rectangle in robot coordinates (forward = +x, left = +y), placed on the field. */
    private static Area shape(double cx, double cy, double heading, double rx, double ry, double w, double h) {
        AffineTransform t = new AffineTransform();
        t.translate(cx, cy);
        t.rotate(heading);
        return new Area(t.createTransformedShape(new Rectangle2D.Double(rx, ry, w, h)));
    }

    private boolean intersectsAnything(Area shape) {
        Rectangle2D field = new Rectangle2D.Double(0, 0, FIELD, FIELD);
        if (!field.contains(shape.getBounds2D())) {
            return true;
        }
        for (Rectangle2D r : mission.obstacles) {
            if (shape.intersects(r)) {
                Area a = new Area(shape);
                a.intersect(new Area(r));
                if (!a.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    // ---- read-only snapshot for the screen ----

    public synchronized Snapshot snapshot() {
        Snapshot s = new Snapshot();
        s.x = x;
        s.y = y;
        s.headingDeg = Math.toDegrees(headingRad);
        s.leftPower = left.power;
        s.rightPower = right.power;
        s.armPower = arm.power;
        s.leftTicks = left.reportedPosition();
        s.rightTicks = right.reportedPosition();
        s.armTicks = arm.reportedPosition();
        s.armDeg = armDeg;
        s.clawPos = clawPos;
        s.touch = touchPressed();
        s.blocked = blocked;
        s.blocks = new ArrayList<double[]>();
        for (Block b : blocks) {
            s.blocks.add(new double[]{b.x, b.y, b.held ? 1 : 0});
        }
        s.trail = new ArrayList<double[]>(trail);
        s.goalIndex = goalIndex;
        s.complete = completeNanos >= 0;
        s.completeSeconds = (completeNanos >= 0 && startNanos >= 0) ? (completeNanos - startNanos) / 1e9 : -1;
        s.failure = failure;
        s.mission = mission;
        return s;
    }

    public synchronized void clearTrail() {
        trail.clear();
        trail.add(new double[]{x, y});
    }

    /** Everything the screen needs to draw one frame. */
    public static final class Snapshot {
        public double x, y, headingDeg, leftPower, rightPower, armPower, armDeg, clawPos, completeSeconds;
        public int leftTicks, rightTicks, armTicks, goalIndex;
        public boolean touch, blocked, complete;
        public String failure;
        public List<double[]> blocks, trail;
        public Mission mission;
    }
}
