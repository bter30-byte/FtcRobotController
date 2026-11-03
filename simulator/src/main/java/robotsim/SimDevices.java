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

    static HardwareMap createHardwareMap(World world, RunContext context) {
        HardwareMap map = new HardwareMap();
        map.put("left_drive", new Motor(world, world.left, context, 0));
        map.put("right_drive", new Motor(world, world.right, context, 1));
        map.put("arm", new Motor(world, world.arm, context, 2));
        map.put("claw", new Claw(world, context));
        map.put("touch_sensor", new Touch(world));
        map.put("sensor_color", new Color(world, context));
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

        Motor(World world, MotorState state, RunContext context, int port) {
            super(world, state.name);
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

    static final class Claw extends Device implements Servo {
        private final RunContext context;

        Claw(World world, RunContext context) {
            super(world, "claw");
            this.context = context;
        }

        @Override
        public int getPortNumber() {
            return 0;
        }

        @Override
        public void setDirection(Direction direction) {
            synchronized (world) {
                if (context.acceptsCommands()) {
                    world.clawReversed = direction == Direction.REVERSE;
                }
            }
        }

        @Override
        public Direction getDirection() {
            synchronized (world) {
                return world.clawReversed ? Direction.REVERSE : Direction.FORWARD;
            }
        }

        @Override
        public void setPosition(double position) {
            synchronized (world) {
                if (context.acceptsCommands()) {
                    double p = MotorState.clip(position, MIN_POSITION, MAX_POSITION);
                    world.clawTarget = world.clawReversed ? 1 - p : p;
                }
            }
        }

        @Override
        public double getPosition() {
            synchronized (world) {
                return world.clawReversed ? 1 - world.clawTarget : world.clawTarget;
            }
        }

        @Override
        public void scaleRange(double min, double max) {
            // The simulated claw always uses the full 0.0 to 1.0 range.
        }
    }

    /** REV Color Sensor V3, on I2C bus 1, looking down at the floor. */
    static final class Color extends Device implements ColorSensor, NormalizedColorSensor, SwitchableLight {
        private final RunContext context;

        Color(World world, RunContext context) {
            super(world, "sensor_color");
            this.context = context;
        }

        @Override
        public NormalizedRGBA getNormalizedColors() {
            synchronized (world) {
                float[] c = world.colorReading();
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
                if (context.acceptsCommands()) {
                    world.colorGain = Math.max(1f, newGain);
                }
            }
        }

        private int raw(int channel) {
            synchronized (world) {
                float[] c = world.colorReading();
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
                if (context.acceptsCommands()) {
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

    static final class Touch extends Device implements TouchSensor {
        Touch(World world) {
            super(world, "touch_sensor");
        }

        @Override
        public double getValue() {
            return isPressed() ? 1.0 : 0.0;
        }

        @Override
        public boolean isPressed() {
            synchronized (world) {
                return world.touchPressed();
            }
        }
    }
}
