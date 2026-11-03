package robotsim;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
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
        return map;
    }

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
