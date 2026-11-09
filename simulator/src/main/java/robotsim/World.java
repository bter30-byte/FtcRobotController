package robotsim;

import com.qualcomm.robotcore.hardware.DcMotor;

import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The simulated field and robot. Units are inches, seconds, and degrees.
 *
 * The robot's size, drivetrain, arm, claw, and sensors come from a RobotModel (CourseBot, the
 * course robot, unless another design is given). CourseBot has two drive motors (the left one is
 * mounted mirrored, so programs must reverse it), an arm motor with a touch sensor at the top,
 * and a claw servo. The touch sensor is ALSO pressed when the front bumper touches a wall,
 * so it works for both the arm lessons and the bumper lessons. Space presses it by hand.
 *
 * Everything here is guarded by synchronizing on this World object.
 */
public final class World {

    public static final double FIELD = 144;
    /** Blocks are 6-inch cubes. A hook block has a metal handle this tall on top. */
    public static final double BLOCK_SIZE = 6, HANDLE_HEIGHT = 1.5;
    /** How close (sideways) a hook must be to a handle to catch it, in inches. */
    static final double HOOK_CATCH = 1.5;

    /** The robot's design. */
    final RobotModel robot;
    final MotorState left, right, arm;

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
    final List<Ball> balls = new ArrayList<Ball>();
    /** Physics for blocks and balls, in missions where they're real objects (else null). */
    private ObjectPhysics physics;
    final List<double[]> trail = new ArrayList<double[]>();
    /** Tape on the floor for this run (a mission's tape may be shifted on each reset). */
    final List<Mission.Tape> tapes = new ArrayList<Mission.Tape>();
    /** Tape positions and sensor noise. Tests can fix the seed with -Drobotsim.seed=... */
    private final Random random = new Random(Long.getLong("robotsim.seed", System.nanoTime()));

    // ---- color sensor (REV Color Sensor V3, at the front of the robot, looking down) ----
    /** The gray foam tiles reflect this much red, green, and blue light. */
    static final float[] FLOOR = {0.30f, 0.31f, 0.33f};
    float colorGain = 1.0f;
    boolean colorLight = true;

    // ---- mission progress ----
    Mission mission;
    int goalIndex = 0;
    long startNanos = -1;
    long completeNanos = -1;
    String failure = null;
    /** The last moment a program was running, and whether this TeleOp continues the autonomous's match. */
    long lastRunNanos = -1;
    boolean continuingMatch;
    double initX, initY, initHeading;

    /** A yellow block the claw can pick up (and a hook can lift, if it has a handle). */
    static final class Block {
        double x, y, angleDeg;
        /** Held in the claw, or hanging from the hook. */
        boolean held, hooked;
        /** Dropped into a square hole: it stays there. */
        boolean inHole;
        /** How high the block's bottom is above the floor (only while hooked). */
        double lift;
        final boolean hasHandle;

        Block(double x, double y, boolean hasHandle) {
            this.x = x;
            this.y = y;
            this.hasHandle = hasHandle;
        }
    }

    /** A ball the robot can push. */
    static final class Ball {
        double x, y;
        final double radius;
        /** Rolled into a round hole: it stays there. */
        boolean inHole;

        Ball(double x, double y, double radius) {
            this.x = x;
            this.y = y;
            this.radius = radius;
        }
    }

    public World(Mission mission) {
        this(mission, RobotModel.courseBot());
    }

    World(Mission mission, RobotModel robot) {
        this.robot = robot;
        double ticksPerInch = robot.ticksPerInch();
        left = new MotorState("left wheels", robot.leftMount, ticksPerInch, 1.0 / (2 * ticksPerInch), 10);
        right = new MotorState("right wheels", robot.rightMount, ticksPerInch, 1.0 / (2 * ticksPerInch), 10);
        arm = new MotorState("arm", +1, robot.armTicksPerDeg, 1.0 / (10 * robot.armTicksPerDeg), 5);
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
            blocks.add(new Block(b[0], b[1], b.length > 2 && b[2] > 0));
        }
        balls.clear();
        for (double[] b : m.balls) {
            balls.add(new Ball(b[0], b[1], b[2] / 2));
        }
        physics = null;
        if (m.physicalObjects) {
            physics = new ObjectPhysics(m, robot);
            for (Block b : blocks) {
                physics.addBlock(b);
            }
            for (Ball b : balls) {
                physics.addBall(b);
            }
        }
        tapes.clear();
        double shiftX = m.tapeShiftsX.length == 0 ? 0 : m.tapeShiftsX[random.nextInt(m.tapeShiftsX.length)];
        double shiftY = m.tapeShiftsY.length == 0 ? 0 : m.tapeShiftsY[random.nextInt(m.tapeShiftsY.length)];
        for (Mission.Tape t : m.tapes) {
            tapes.add(t.shifted(shiftX, shiftY));
        }
        colorGain = 1.0f;
        colorLight = true;
        trail.clear();
        trail.add(new double[]{x, y});
        resetProgress();
    }

    synchronized void resetProgress() {
        lastRunNanos = -1;
        continuingMatch = false;
        goalIndex = 0;
        startNanos = -1;
        completeNanos = -1;
        failure = null;
    }

    // ---- program lifecycle hooks ----

    synchronized void onInit(boolean teleOp) {
        // In an autonomous-then-driver match, a TeleOp carries on where the autonomous left off:
        // same goals, same clock. Running an autonomous always starts a new match.
        boolean carryOn = mission.autoThenDriver && teleOp && startNanos >= 0 && completeNanos < 0
                && failure == null;
        if (!carryOn) {
            resetProgress();
        }
        continuingMatch = carryOn;
        initX = x;
        initY = y;
        initHeading = headingRad;
    }

    synchronized void onStart(long nowNanos) {
        if (continuingMatch && lastRunNanos >= 0) {
            // The time between the two programs doesn't count, like the break in a real match.
            startNanos += nowNanos - lastRunNanos;
        } else {
            startNanos = nowNanos;
        }
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
        stepHook();
        stepRampAndHoles();
        bumperContact = intersectsAnything(frontStrip()) || touchesObject(frontStrip(), 0);
        recordTrail();
        if (runPhase) {
            lastRunNanos = nowNanos;
        }
        checkMission(initPhase, runPhase, nowNanos);
    }

    private void stepDrive(double dt) {
        for (MotorState m : new MotorState[]{left, right}) {
            double v = m.voltage();
            double target = v * m.mount * robot.maxSpeed * battery;
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
        double omega = (vR - vL) / robot.trackWidth;
        double newHeading = headingRad + omega * dt;
        double midHeading = headingRad + omega * dt / 2.0;
        double nx = x + v * Math.cos(midHeading) * dt;
        double ny = y + v * Math.sin(midHeading) * dt;

        prevX = x;
        prevY = y;
        boolean stuck = intersectsAnything(robotShape(nx, ny, newHeading));
        if (physics != null) {
            // Push blocks and balls. One that can't get out of the way stops the robot like a wall.
            if (stuck) {
                physics.step(dt, x, y, headingRad, x, y, headingRad);
            } else {
                physics.step(dt, x, y, headingRad, nx, ny, newHeading);
            }
            for (Block b : blocks) {
                physics.readBack(b);
            }
            for (Ball b : balls) {
                physics.readBack(b);
            }
            if (!stuck && touchesObject(robotShape(nx, ny, newHeading), 0.15)) {
                stuck = true;
            }
        }
        if (stuck) {
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
        left.rawTicks += vL * left.mount * left.ticksPerUnit * dt;
        right.rawTicks += vR * right.mount * right.ticksPerUnit * dt;
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
            rate = v * robot.armSpeedDeg * battery;
        } else {
            // With FLOAT, gravity slowly pulls the arm down. BRAKE holds it.
            rate = arm.zeroPowerBehavior == DcMotor.ZeroPowerBehavior.BRAKE ? 0 : -40;
        }
        armDeg = MotorState.clip(armDeg + rate * dt, 0, robot.armMaxDeg);
        arm.speed = rate;
        arm.rawTicks = armDeg * arm.ticksPerUnit;
    }

    private void stepClaw(double dt) {
        boolean wasOpen = clawPos >= 0.5;
        double step = robot.clawSpeed * dt;
        if (Math.abs(clawTarget - clawPos) <= step) {
            clawPos = clawTarget;
        } else {
            clawPos += Math.signum(clawTarget - clawPos) * step;
        }
        boolean open = clawPos >= 0.5;
        if (wasOpen && !open && armDeg < 30) {
            // Closing: grab a block that is right in front of the robot.
            for (Block b : blocks) {
                if (!b.held && !b.hooked && grabZone().contains(b.x, b.y)) {
                    b.held = true;
                    if (physics != null) {
                        physics.remove(b);
                    }
                    break;
                }
            }
        } else if (!wasOpen && open) {
            for (Block b : blocks) {
                if (b.held) {
                    b.held = false;
                    b.angleDeg = Math.toDegrees(headingRad);
                    if (physics != null) {
                        physics.addBlock(b);
                    }
                }
            }
        }
    }

    /**
     * A hook on the arm catches a block's handle when the arm lifts with the hook right under the
     * handle. The block hangs from the hook until it's set back down on the floor.
     */
    private void stepHook() {
        if (!robot.hasHook()) {
            return;
        }
        double[] tip = hookTip();
        for (Block b : blocks) {
            if (b.hooked) {
                b.x = tip[0];
                b.y = tip[1];
                b.angleDeg = Math.toDegrees(headingRad);
                b.lift = Math.max(0, tip[2] - HANDLE_HEIGHT - BLOCK_SIZE);
                if (b.lift <= 0 && arm.speed < 0) {
                    // Set down on the floor: the hook lets go.
                    b.hooked = false;
                    if (physics != null) {
                        physics.addBlock(b);
                    }
                }
                return;
            }
        }
        if (arm.speed <= 0) {
            return;
        }
        for (Block b : blocks) {
            if (b.hasHandle && !b.held && Math.hypot(tip[0] - b.x, tip[1] - b.y) <= HOOK_CATCH
                    && tip[2] >= BLOCK_SIZE && tip[2] <= BLOCK_SIZE + HANDLE_HEIGHT + 0.5) {
                b.hooked = true;
                b.lift = 0;
                if (physics != null) {
                    physics.remove(b);
                }
                return;
            }
        }
    }

    /** How hard a ramp pulls things back down, in m/s² (about a 12° slope). */
    static final double RAMP_PULL = 2.0;

    /**
     * Balls and loose blocks on the ramp get pulled back down it. A ball over a round hole, or a
     * loose block over a square hole, drops in and stays there.
     */
    private void stepRampAndHoles() {
        if (physics == null) {
            return;
        }
        Rectangle2D ramp = mission.ramp;
        for (Ball b : balls) {
            if (b.inHole) {
                continue;
            }
            for (Mission.Hole h : mission.holes) {
                if (h.round && Math.hypot(b.x - h.x, b.y - h.y) < h.size / 2 - b.radius * 0.4) {
                    b.inHole = true;
                    b.x = h.x;
                    b.y = h.y;
                    physics.remove(b);
                    break;
                }
            }
            if (!b.inHole && ramp != null && ramp.contains(b.x, b.y)) {
                physics.pull(b, 0, -RAMP_PULL);
            }
        }
        for (Block b : blocks) {
            if (b.inHole || b.held || b.hooked) {
                continue;
            }
            for (Mission.Hole h : mission.holes) {
                if (!h.round && Math.abs(b.x - h.x) < h.size / 2 - 1 && Math.abs(b.y - h.y) < h.size / 2 - 1) {
                    b.inHole = true;
                    b.x = h.x;
                    b.y = h.y;
                    physics.remove(b);
                    break;
                }
            }
            if (!b.inHole && ramp != null && ramp.contains(b.x, b.y)) {
                physics.pull(b, 0, -RAMP_PULL);
            }
        }
    }

    /** Where the hook's tip is: field x, field y, and height above the floor. */
    double[] hookTip() {
        double e = Math.toRadians(robot.armElevationDeg(armDeg));
        double ahead = robot.armPivotAhead + robot.hookAlongArm * Math.cos(e);
        double height = robot.armPivotHeight + robot.hookAlongArm * Math.sin(e);
        return new double[]{x + ahead * Math.cos(headingRad), y + ahead * Math.sin(headingRad), height};
    }

    /** True if a shape overlaps a block or ball on the floor by more than the given depth (inches). */
    private boolean touchesObject(Area shape, double depth) {
        if (physics == null) {
            return false;
        }
        // Measure the overlap along the robot's own sides, not the field's: when the robot is at an
        // angle, a field-aligned box around a thin sliver of overlap looks much deeper than it is.
        java.awt.geom.AffineTransform toRobot = java.awt.geom.AffineTransform.getRotateInstance(-headingRad);
        for (Block b : blocks) {
            if (!b.held && !b.hooked && !b.inHole && overlaps(shape, blockShape(b), depth, toRobot)) {
                return true;
            }
        }
        for (Ball b : balls) {
            if (!b.inHole && overlaps(shape, new Area(new java.awt.geom.Ellipse2D.Double(b.x - b.radius,
                    b.y - b.radius, 2 * b.radius, 2 * b.radius)), depth, toRobot)) {
                return true;
            }
        }
        return false;
    }

    private static boolean overlaps(Area a, Area b, double depth, java.awt.geom.AffineTransform toRobot) {
        Area both = new Area(a);
        both.intersect(b);
        if (both.isEmpty()) {
            return false;
        }
        both.transform(toRobot);
        Rectangle2D r = both.getBounds2D();
        return r.getWidth() > depth && r.getHeight() > depth;
    }

    private static Area blockShape(Block b) {
        return shape(b.x, b.y, Math.toRadians(b.angleDeg), -BLOCK_SIZE / 2, -BLOCK_SIZE / 2, BLOCK_SIZE, BLOCK_SIZE);
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

    double[] sensorPoint() {
        double ahead = robot.colorSensorAhead;
        return new double[]{x + ahead * Math.cos(headingRad), y + ahead * Math.sin(headingRad)};
    }

    /**
     * How much red, green, and blue light the floor under the sensor reflects, 0 to 1.
     * The sensor sees a small spot, so it reads in-between values on the edge of the tape.
     */
    float[] surfaceUnderSensor() {
        double[] c = sensorPoint();
        double[][] spot = {{0, 0}, {0.45, 0}, {-0.45, 0}, {0, 0.45}, {0, -0.45}};
        float[] sum = new float[3];
        for (double[] o : spot) {
            float[] r = surfaceAt(c[0] + o[0], c[1] + o[1]);
            for (int i = 0; i < 3; i++) {
                sum[i] += r[i] / spot.length;
            }
        }
        return sum;
    }

    private float[] surfaceAt(double px, double py) {
        for (Mission.Tape t : tapes) {
            if (t.distanceTo(px, py) <= t.width / 2) {
                return t.reflect;
            }
        }
        return FLOOR;
    }

    /** One reading, like the real sensor: a little noise every time, darker without the LED. */
    float[] colorReading() {
        float[] s = surfaceUnderSensor();
        float light = colorLight ? 1.0f : 0.25f;
        float[] out = new float[3];
        for (int i = 0; i < 3; i++) {
            float noise = 1.0f + (float) (random.nextGaussian() * 0.02);
            out[i] = s[i] * light * noise;
        }
        return out;
    }

    boolean touchPressed() {
        return bumperContact || armDeg >= robot.armLimitDeg || manualTouch;
    }

    /** How fast the robot is moving forward or back (a spin in place is 0). */
    double forwardSpeed() {
        return Math.abs(left.speed + right.speed) / 2;
    }

    boolean isStopped() {
        return Math.abs(left.speed) < 1 && Math.abs(right.speed) < 1;
    }

    // ---- geometry ----

    private Area robotShape(double cx, double cy, double heading) {
        return shape(cx, cy, heading, -robot.length / 2, -robot.width / 2, robot.length, robot.width);
    }

    /** A thin strip along the front of the robot: the bumper. */
    private Area frontStrip() {
        return shape(x, y, headingRad, robot.length / 2, -robot.width / 2 + 1, 1.5, robot.width - 2);
    }

    private Area grabZone() {
        return shape(x, y, headingRad, robot.length / 2 - 2, -5, 9, 10);
    }

    /** Where a held block sits: just in front of the claw. */
    private double[] clawPoint() {
        double d = robot.length / 2 + 3;
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
        Rectangle2D field = new Rectangle2D.Double(0, 0, mission.fieldWidth, mission.fieldHeight);
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
            s.blocks.add(new double[]{b.x, b.y, b.held ? 1 : 0, b.angleDeg, b.lift, b.hooked ? 1 : 0,
                    b.hasHandle ? 1 : 0, b.inHole ? 1 : 0});
        }
        s.balls = new ArrayList<double[]>();
        for (Ball b : balls) {
            s.balls.add(new double[]{b.x, b.y, b.radius, b.inHole ? 1 : 0});
        }
        s.trail = new ArrayList<double[]>(trail);
        s.goalIndex = goalIndex;
        s.complete = completeNanos >= 0;
        s.completeSeconds = (completeNanos >= 0 && startNanos >= 0) ? (completeNanos - startNanos) / 1e9 : -1;
        s.failure = failure;
        s.mission = mission;
        s.robot = robot;
        s.tapes = new ArrayList<Mission.Tape>(tapes);
        double[] sp = sensorPoint();
        s.sensorX = sp[0];
        s.sensorY = sp[1];
        float[] surface = surfaceUnderSensor();
        s.brightness = Math.min(1.0, (surface[0] + surface[1] + surface[2]) / 3 * (colorLight ? 1 : 0.25)
                * colorGain * SimDevices.NORMALIZE);
        s.colorGain = colorGain;
        s.surface = (surface[0] + surface[1] + surface[2]) / 3;
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
        /** Blocks: x, y, held in claw (1/0), angle, lift height, hooked (1/0), has handle (1/0), in a hole (1/0). */
        public List<double[]> blocks;
        /** Balls: x, y, radius, in a hole (1/0). */
        public List<double[]> balls;
        public List<double[]> trail;
        public Mission mission;
        public RobotModel robot;
        public List<Mission.Tape> tapes;
        public double sensorX, sensorY, brightness, surface;
        public float colorGain;
    }
}
