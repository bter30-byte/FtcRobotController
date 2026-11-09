package robotsim;

/**
 * Everything the simulation needs to know about one robot's design: its size, drivetrain, arm,
 * claw, sensors, and which device is plugged into which port. Units are inches and degrees.
 *
 * Today there's one design, CourseBot (the course robot). A robot builder could later
 * make these from parts, so nothing else in the simulator should assume CourseBot's numbers.
 */
public final class RobotModel {

    /** What's on the other end of a port's cable. */
    enum Device { LEFT_WHEELS, RIGHT_WHEELS, ARM, CLAW, TOUCH, COLOR }

    public final String name;

    // ---- body: the robot's center is the middle of this rectangle ----
    public final double length, width;

    // ---- drivetrain ----
    /** Distance between the left and right wheels. */
    public final double trackWidth;
    public final double wheelDiameter;
    public final double ticksPerMotorRev;
    /** Wheel speed at full power, inches per second. */
    public final double maxSpeed;
    /** +1 or -1: which way each side's motor is mounted. A mirrored motor must be reversed in code. */
    public final int leftMount, rightMount;

    // ---- arm (on a motor at the front) ----
    public final double armMaxDeg, armLimitDeg, armSpeedDeg, armTicksPerDeg;
    /** Where the arm's pivot is (ahead of the center, height above the floor) and how long it is. */
    public final double armPivotAhead, armPivotHeight, armLength;
    /** A hook on the arm, this far from the pivot, that can catch a block's handle. NaN = no hook. */
    public final double hookAlongArm;

    // ---- claw and sensors ----
    /** How fast the claw servo moves, in position units per second. */
    public final double clawSpeed;
    /** The color sensor looks down at the floor this far ahead of the center. */
    public final double colorSensorAhead;

    // ---- wiring: what's plugged into each port (null = nothing) ----
    private final Device[] motorPorts, servoPorts, digitalPorts, i2cBuses;

    private RobotModel(Builder b) {
        name = b.name;
        length = b.length;
        width = b.width;
        trackWidth = b.trackWidth;
        wheelDiameter = b.wheelDiameter;
        ticksPerMotorRev = b.ticksPerMotorRev;
        maxSpeed = b.maxSpeed;
        leftMount = b.leftMount;
        rightMount = b.rightMount;
        armMaxDeg = b.armMaxDeg;
        armLimitDeg = b.armLimitDeg;
        armSpeedDeg = b.armSpeedDeg;
        armTicksPerDeg = b.armTicksPerDeg;
        armPivotAhead = b.armPivotAhead;
        armPivotHeight = b.armPivotHeight;
        armLength = b.armLength;
        hookAlongArm = b.hookAlongArm;
        clawSpeed = b.clawSpeed;
        colorSensorAhead = b.colorSensorAhead;
        motorPorts = b.motorPorts.clone();
        servoPorts = b.servoPorts.clone();
        digitalPorts = b.digitalPorts.clone();
        i2cBuses = b.i2cBuses.clone();
    }

    /** The course robot: the numbers every lesson and mission was written for. */
    static RobotModel courseBot() {
        return new Builder("CourseBot")
                .body(18, 18)
                // goBILDA-style 312 RPM motor (537.7 ticks/rev) with 96 mm wheels: same numbers as Lesson 12
                .drivetrain(15, 3.78, 537.7, 40, -1, +1)
                .arm(100, 95, 120, 5, 4, 7, 11)
                .claw(2.0)
                .colorSensor(7.0)
                .motorPort(0, Device.LEFT_WHEELS)
                .motorPort(1, Device.RIGHT_WHEELS)
                .motorPort(2, Device.ARM)
                .servoPort(0, Device.CLAW)
                .digitalPort(1, Device.TOUCH)
                .i2cBus(1, Device.COLOR)
                .build();
    }

    /**
     * The arm's angle above horizontal, in degrees, for a position on the arm's 0-100 scale:
     * 0 is resting down in front, near the floor; 100 is nearly straight up.
     */
    public double armElevationDeg(double armDeg) {
        return -35 + armDeg * 1.2;
    }

    public boolean hasHook() {
        return !Double.isNaN(hookAlongArm);
    }

    /** Encoder ticks for one inch of wheel travel. */
    public double ticksPerInch() {
        return ticksPerMotorRev / (wheelDiameter * Math.PI);
    }

    Device motorPort(int port) {
        return port >= 0 && port < motorPorts.length ? motorPorts[port] : null;
    }

    Device servoPort(int port) {
        return port >= 0 && port < servoPorts.length ? servoPorts[port] : null;
    }

    Device digitalPort(int port) {
        return port >= 0 && port < digitalPorts.length ? digitalPorts[port] : null;
    }

    Device i2cBus(int bus) {
        return bus >= 0 && bus < i2cBuses.length ? i2cBuses[bus] : null;
    }

    static final class Builder {
        private final String name;
        private double length, width, trackWidth, wheelDiameter, ticksPerMotorRev, maxSpeed;
        private int leftMount = 1, rightMount = 1;
        private double armMaxDeg, armLimitDeg, armSpeedDeg, armTicksPerDeg = 1;
        private double armPivotAhead, armPivotHeight, armLength;
        private double hookAlongArm = Double.NaN;
        private double clawSpeed, colorSensorAhead;
        private final Device[] motorPorts = new Device[4];
        private final Device[] servoPorts = new Device[6];
        private final Device[] digitalPorts = new Device[8];
        private final Device[] i2cBuses = new Device[4];

        Builder(String name) {
            this.name = name;
        }

        Builder body(double length, double width) {
            this.length = length;
            this.width = width;
            return this;
        }

        Builder drivetrain(double trackWidth, double wheelDiameter, double ticksPerMotorRev, double maxSpeed,
                           int leftMount, int rightMount) {
            this.trackWidth = trackWidth;
            this.wheelDiameter = wheelDiameter;
            this.ticksPerMotorRev = ticksPerMotorRev;
            this.maxSpeed = maxSpeed;
            this.leftMount = leftMount;
            this.rightMount = rightMount;
            return this;
        }

        Builder arm(double maxDeg, double limitDeg, double speedDeg, double ticksPerDeg,
                    double pivotAhead, double pivotHeight, double length) {
            armMaxDeg = maxDeg;
            armLimitDeg = limitDeg;
            armSpeedDeg = speedDeg;
            armTicksPerDeg = ticksPerDeg;
            armPivotAhead = pivotAhead;
            armPivotHeight = pivotHeight;
            armLength = length;
            return this;
        }

        /** Puts a hook on the arm, this far from the pivot. */
        Builder hook(double alongArm) {
            hookAlongArm = alongArm;
            return this;
        }

        Builder claw(double speed) {
            clawSpeed = speed;
            return this;
        }

        Builder colorSensor(double ahead) {
            colorSensorAhead = ahead;
            return this;
        }

        Builder motorPort(int port, Device d) {
            motorPorts[port] = d;
            return this;
        }

        Builder servoPort(int port, Device d) {
            servoPorts[port] = d;
            return this;
        }

        Builder digitalPort(int port, Device d) {
            digitalPorts[port] = d;
            return this;
        }

        Builder i2cBus(int bus, Device d) {
            i2cBuses[bus] = d;
            return this;
        }

        RobotModel build() {
            return new RobotModel(this);
        }
    }
}
