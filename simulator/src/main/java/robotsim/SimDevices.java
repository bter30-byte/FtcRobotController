package robotsim;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.SwitchableLight;
import com.qualcomm.robotcore.hardware.TouchSensor;

/**
 * The device objects a program gets from hardwareMap.get(...). Each run gets fresh objects,
 * so a program that is still running after STOP can't move the robot.
 */
final class SimDevices {

    private SimDevices() {
    }

    /**
     * Builds the hardware map from a robot configuration. What's really plugged into each port
     * comes from the robot's design (CourseBot: left wheels on motor 0, right wheels on motor 1,
     * arm on motor 2, claw on servo 0, touch sensor on digital 1, color sensor on I2C bus 1).
     * A device configured on a port with nothing plugged in still exists, but does nothing, just
     * like on a real robot.
     */
    static HardwareMap createHardwareMap(World world, RunContext context, RobotConfig config) {
        HardwareMap map = new HardwareMap();
        for (RobotConfig.Device d : config.devices()) {
            switch (d.section) {
                case MOTORS:
                    RobotModel.Device plugged = world.robot.motorPort(d.port);
                    MotorState state = plugged == RobotModel.Device.LEFT_WHEELS ? world.left
                            : plugged == RobotModel.Device.RIGHT_WHEELS ? world.right
                            : plugged == RobotModel.Device.ARM ? world.arm : new MotorState(d.name, 1, 1, 0.01, 10);
                    map.put(d.name, new Motor(world, state, context, d.name, d.port));
                    break;
                case SERVOS:
                    map.put(d.name, new Claw(world, context, d.name, d.port));
                    break;
                case DIGITAL:
                    if (RobotConfig.TOUCH.equals(d.type)) {
                        map.put(d.name, new Touch(world, d.name,
                                world.robot.digitalPort(d.port) == RobotModel.Device.TOUCH));
                    }
                    break;
                default:
                    if (RobotConfig.COLOR.equals(d.type)) {
                        map.put(d.name, new Color(world, context, d.name,
                                world.robot.i2cBus(d.section.bus()) == RobotModel.Device.COLOR));
                    }
                    // The IMU isn't simulated, so it isn't in the map.
                    break;
            }
        }
        return map;
    }

    /**
     * Turns reflected light into normalized readings: at gain 15, gray tile reads about 0.28,
     * black tape about 0.04, and white tape about 0.80 (close to a REV Color Sensor V3).
     */
    static final double NORMALIZE = 0.062;
    /** Raw counts per unit of reflected light, for the ColorSensor red()/green()/blue() methods. */
    static final double RAW = 4000;

    private abstract static class Device {
        final World world;
        final String name;

        Device(World world, String name) {
            this.world = world;
            this.name = name;
        }

        public String getDeviceName() {
            return "Simulated " + name;
        }

        public String getConnectionInfo() {
            return "simulator";
        }

        public int getVersion() {
            return 1;
        }

        public void resetDeviceConfigurationForOpMode() {
        }

        public void close() {
        }
    }

    static final class Motor extends Device implements DcMotor {
        private final MotorState state;
        private final RunContext context;
        private final int port;

        Motor(World world, MotorState state, RunContext context, String name, int port) {
            super(world, name);
            this.state = state;
            this.context = context;
            this.port = port;
        }

        @Override
        public int getPortNumber() {
            return port;
        }

        @Override
        public void setDirection(DcMotorSimple.Direction direction) {
            synchronized (world) {
                if (context.acceptsCommands()) {
                    state.direction = direction;
                }
            }
        }

        @Override
        public DcMotorSimple.Direction getDirection() {
            synchronized (world) {
                return state.direction;
            }
        }

        @Override
        public void setPower(double power) {
            synchronized (world) {
                if (context.acceptsCommands()) {
                    state.power = MotorState.clip(power, -1, 1);
                }
            }
        }

        @Override
        public double getPower() {
            synchronized (world) {
                return state.power;
            }
        }

        @Override
        public void setZeroPowerBehavior(ZeroPowerBehavior behavior) {
            synchronized (world) {
                if (context.acceptsCommands() && behavior != ZeroPowerBehavior.UNKNOWN) {
                    state.zeroPowerBehavior = behavior;
                }
            }
        }

        @Override
        public ZeroPowerBehavior getZeroPowerBehavior() {
            synchronized (world) {
                return state.zeroPowerBehavior;
            }
        }

        @Override
        public void setTargetPosition(int position) {
            synchronized (world) {
                if (context.acceptsCommands()) {
                    state.target = position;
                    state.targetSet = true;
                }
            }
        }

        @Override
        public int getTargetPosition() {
            synchronized (world) {
                return state.target;
            }
        }

        @Override
        public boolean isBusy() {
            synchronized (world) {
                return state.isBusy();
            }
        }

        @Override
        public int getCurrentPosition() {
            synchronized (world) {
                return state.reportedPosition();
            }
        }

        @Override
        public void setMode(RunMode mode) {
            synchronized (world) {
                if (context.acceptsCommands()) {
                    state.setMode(mode);
                }
            }
        }

        @Override
        public RunMode getMode() {
            synchronized (world) {
                return state.mode;
            }
        }
    }

    /** A servo. It moves the claw if the claw is plugged into its port. */
    static final class Claw extends Device implements Servo {
        private final RunContext context;
        private final int port;
        // A servo port with nothing plugged in just remembers what it was told.
        private boolean unpluggedReversed = false;
        private double unpluggedPosition = 0.5;

        Claw(World world, RunContext context, String name, int port) {
            super(world, name);
            this.context = context;
            this.port = port;
        }

        private boolean pluggedIn() {
            return world.robot.servoPort(port) == RobotModel.Device.CLAW;
        }

        @Override
        public int getPortNumber() {
            return port;
        }

        @Override
        public void setDirection(Direction direction) {
            synchronized (world) {
                if (context.acceptsCommands()) {
                    if (pluggedIn()) {
                        world.clawReversed = direction == Direction.REVERSE;
                    } else {
                        unpluggedReversed = direction == Direction.REVERSE;
                    }
                }
            }
        }

        @Override
        public Direction getDirection() {
            synchronized (world) {
                boolean reversed = pluggedIn() ? world.clawReversed : unpluggedReversed;
                return reversed ? Direction.REVERSE : Direction.FORWARD;
            }
        }

        @Override
        public void setPosition(double position) {
            synchronized (world) {
                if (context.acceptsCommands()) {
                    double p = MotorState.clip(position, MIN_POSITION, MAX_POSITION);
                    if (pluggedIn()) {
                        world.clawTarget = world.clawReversed ? 1 - p : p;
                    } else {
                        unpluggedPosition = p;
                    }
                }
            }
        }

        @Override
        public double getPosition() {
            synchronized (world) {
                if (!pluggedIn()) {
                    return unpluggedPosition;
                }
                return world.clawReversed ? 1 - world.clawTarget : world.clawTarget;
            }
        }

        @Override
        public void scaleRange(double min, double max) {
            // The simulated claw always uses the full 0.0 to 1.0 range.
        }
    }

    /**
     * REV Color Sensor V3, looking down at the floor. Configured on a bus where it isn't plugged
     * in, it finds no sensor there and reads all zeros, like a real one.
     */
    static final class Color extends Device implements ColorSensor, NormalizedColorSensor, SwitchableLight {
        private static final float[] NO_LIGHT = {0f, 0f, 0f};
        private final RunContext context;
        private final boolean pluggedIn;

        Color(World world, RunContext context, String name, boolean pluggedIn) {
            super(world, name);
            this.context = context;
            this.pluggedIn = pluggedIn;
        }

        private float[] reading() {
            return pluggedIn ? world.colorReading() : NO_LIGHT;
        }

        @Override
        public NormalizedRGBA getNormalizedColors() {
            synchronized (world) {
                float[] c = reading();
                NormalizedRGBA n = new NormalizedRGBA();
                n.red = scale(c[0]);
                n.green = scale(c[1]);
                n.blue = scale(c[2]);
                n.alpha = scale((c[0] + c[1] + c[2]) / 3f);
                return n;
            }
        }

        private float scale(float reflected) {
            return (float) Math.min(1.0, reflected * world.colorGain * NORMALIZE);
        }

        @Override
        public float getGain() {
            synchronized (world) {
                return world.colorGain;
            }
        }

        @Override
        public void setGain(float newGain) {
            synchronized (world) {
                if (context.acceptsCommands() && pluggedIn) {
                    world.colorGain = Math.max(1f, newGain);
                }
            }
        }

        private int raw(int channel) {
            synchronized (world) {
                float[] c = reading();
                float v = channel < 3 ? c[channel] : (c[0] + c[1] + c[2]) / 3f;
                return (int) Math.round(v * RAW);
            }
        }

        @Override
        public int red() {
            return raw(0);
        }

        @Override
        public int green() {
            return raw(1);
        }

        @Override
        public int blue() {
            return raw(2);
        }

        @Override
        public int alpha() {
            return raw(3);
        }

        @Override
        public int argb() {
            return getNormalizedColors().toColor();
        }

        @Override
        public void enableLed(boolean enable) {
            enableLight(enable);
        }

        @Override
        public void enableLight(boolean enable) {
            synchronized (world) {
                if (context.acceptsCommands() && pluggedIn) {
                    world.colorLight = enable;
                }
            }
        }

        @Override
        public boolean isLightOn() {
            synchronized (world) {
                return world.colorLight;
            }
        }
    }

    /** REV Touch Sensor. Configured on a port where it isn't plugged in, it's never pressed. */
    static final class Touch extends Device implements TouchSensor {
        private final boolean pluggedIn;

        Touch(World world, String name, boolean pluggedIn) {
            super(world, name);
            this.pluggedIn = pluggedIn;
        }

        @Override
        public double getValue() {
            return isPressed() ? 1.0 : 0.0;
        }

        @Override
        public boolean isPressed() {
            synchronized (world) {
                return pluggedIn && world.touchPressed();
            }
        }
    }
}
