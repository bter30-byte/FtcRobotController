package robotsim;

import java.awt.geom.Rectangle2D;
import java.util.IdentityHashMap;
import java.util.Map;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.PhysicsWorld;

/**
 * Blocks and balls as real objects on the floor, seen from above: the robot pushes them, and they
 * bump into each other and the walls. Uses the dyn4j 2D physics library.
 *
 * The robot itself still moves the way World says (its wheels decide where it goes). Here it's a
 * body that can't be pushed back, so it shoves objects out of its way. If an object can't get out
 * of the way (it's pinned against a wall), World stops the robot, just like hitting the wall.
 *
 * dyn4j works in meters and kilograms, so inches are converted on the way in and out.
 */
final class ObjectPhysics {

    private static final double M = 0.0254;  // meters per inch

    private final org.dyn4j.world.World<Body> world = new org.dyn4j.world.World<Body>();
    private final Body robotBody = new Body();
    private final Map<Object, Body> bodies = new IdentityHashMap<Object, Body>();

    ObjectPhysics(Mission mission, RobotModel robot) {
        world.setGravity(PhysicsWorld.ZERO_GRAVITY);
        // Objects may only overlap a hair (1 mm), so nothing visibly sinks into a wall.
        world.getSettings().setLinearTolerance(0.001);
        world.getSettings().setPositionConstraintSolverIterations(20);
        // Field edges and the mission's walls never move.
        double fw = mission.fieldWidth, fh = mission.fieldHeight, t = 12;
        addWall(new Rectangle2D.Double(-t, -t, fw + 2 * t, t));
        addWall(new Rectangle2D.Double(-t, fh, fw + 2 * t, t));
        addWall(new Rectangle2D.Double(-t, 0, t, fh));
        addWall(new Rectangle2D.Double(fw, 0, t, fh));
        for (Rectangle2D r : mission.obstacles) {
            addWall(r);
        }
        BodyFixture chassis = robotBody.addFixture(Geometry.createRectangle(robot.length * M, robot.width * M));
        chassis.setFriction(0.5);
        robotBody.setMass(MassType.INFINITE);
        robotBody.setAtRestDetectionEnabled(false);
        world.addBody(robotBody);
    }

    private void addWall(Rectangle2D r) {
        Body wall = new Body();
        BodyFixture fixture = wall.addFixture(Geometry.createRectangle(r.getWidth() * M, r.getHeight() * M));
        fixture.setRestitution(0.3);
        wall.translate(r.getCenterX() * M, r.getCenterY() * M);
        wall.setMass(MassType.INFINITE);
        world.addBody(wall);
    }

    /** A ball rolls a long way and bounces a little. */
    void addBall(World.Ball b) {
        Body body = new Body();
        BodyFixture fixture = body.addFixture(Geometry.createCircle(b.radius * M));
        fixture.setRestitution(0.45);
        // Low friction: a ball pushed straight ahead stays in front of the robot instead of
        // rolling off to the side the moment the robot turns a little.
        fixture.setFriction(0.05);
        setMass(body, fixture, b.radius * 2 * 0.03);  // about 150 g for a 5-inch ball
        body.setLinearDamping(0.6);
        body.setAngularDamping(0.6);
        body.translate(b.x * M, b.y * M);
        add(b, body);
    }

    /** A block slides a short way when pushed and turns if it's pushed off-center. */
    void addBlock(World.Block b) {
        Body body = new Body();
        BodyFixture fixture = body.addFixture(Geometry.createSquare(World.BLOCK_SIZE * M));
        fixture.setRestitution(0.05);
        fixture.setFriction(0.6);
        setMass(body, fixture, 0.4);
        body.setLinearDamping(6);
        body.setAngularDamping(6);
        body.translate(b.x * M, b.y * M);
        body.rotate(Math.toRadians(b.angleDeg), b.x * M, b.y * M);
        add(b, body);
    }

    private static void setMass(Body body, BodyFixture fixture, double kg) {
        double area = fixture.getShape().createMass(1).getMass();  // mass at density 1 = area
        fixture.setDensity(kg / area);
        body.setMass(MassType.NORMAL);
    }

    private void add(Object key, Body body) {
        body.setAtRestDetectionEnabled(false);
        bodies.put(key, body);
        world.addBody(body);
    }

    /** Takes an object out of the physics while the robot holds it (claw or hook). */
    void remove(Object key) {
        Body body = bodies.remove(key);
        if (body != null) {
            world.removeBody(body);
        }
    }

    /** Pulls an object with an acceleration in m/s² for the next step, like a slope does. */
    void pull(Object key, double ax, double ay) {
        Body body = bodies.get(key);
        if (body != null) {
            double m = body.getMass().getMass();
            body.applyForce(new Vector2(ax * m, ay * m));
        }
    }

    boolean contains(Object key) {
        return bodies.containsKey(key);
    }

    /**
     * Moves the robot from one pose to another over dt seconds, pushing objects on the way, and
     * lets every object move. Poses are inches and radians.
     */
    void step(double dt, double x0, double y0, double heading0, double x1, double y1, double heading1) {
        robotBody.getTransform().setRotation(heading0);
        robotBody.getTransform().setTranslation(x0 * M, y0 * M);
        robotBody.setLinearVelocity(new Vector2((x1 - x0) * M / dt, (y1 - y0) * M / dt));
        robotBody.setAngularVelocity((heading1 - heading0) / dt);
        world.step(1, dt);
        robotBody.setLinearVelocity(0, 0);
        robotBody.setAngularVelocity(0);
        robotBody.getTransform().setRotation(heading1);
        robotBody.getTransform().setTranslation(x1 * M, y1 * M);
    }

    /** Copies an object's new position (inches) and angle (degrees) out of the physics. */
    void readBack(World.Ball b) {
        Body body = bodies.get(b);
        if (body != null) {
            b.x = body.getTransform().getTranslationX() / M;
            b.y = body.getTransform().getTranslationY() / M;
        }
    }

    void readBack(World.Block b) {
        Body body = bodies.get(b);
        if (body != null) {
            b.x = body.getTransform().getTranslationX() / M;
            b.y = body.getTransform().getTranslationY() / M;
            b.angleDeg = Math.toDegrees(body.getTransform().getRotationAngle());
        }
    }
}
