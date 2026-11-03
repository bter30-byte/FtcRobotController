package robotsim;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeSimBridge;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.lang.reflect.InvocationTargetException;

/**
 * Runs one OpMode at a time, like the Robot Controller app: INIT, START, STOP. Each program
 * runs on its own thread, just like on the real robot.
 */
final class OpModeRunner {

    enum Phase {
        IDLE("Pick a program, then press INIT"),
        INIT("INIT done. Press START"),
        RUNNING("RUNNING"),
        FINISHED("Program finished"),
        STOPPED("Stopped"),
        CRASHED("Program crashed. Read the message");

        final String label;

        Phase(String label) {
            this.label = label;
        }
    }

    private final World world;
    private final SimClock clock;

    private volatile Phase phase = Phase.IDLE;
    private volatile RunContext context;
    private volatile Thread thread;
    private volatile String message;
    private volatile boolean messageIsError;
    private volatile long stopWallNanos = -1;
    private boolean warnedStuck = false;
    private CodeLoader.Entry entry;
    /** The active robot configuration (null = none active), and the one the current run used. */
    private volatile RobotConfig config = RobotConfig.courseBot();
    private volatile RobotConfig runConfig;

    final Gamepad gamepad1 = new Gamepad();
    final Gamepad gamepad2 = new Gamepad();

    OpModeRunner(World world, SimClock clock) {
        this.world = world;
        this.clock = clock;
        gamepad1.id = 1;
        gamepad2.id = 2;
    }

    Phase phase() {
        return phase;
    }

    RunContext context() {
        return context;
    }

    CodeLoader.Entry entry() {
        return entry;
    }

    String message() {
        return message;
    }

    boolean messageIsError() {
        return messageIsError;
    }

    void setMessage(String text, boolean isError) {
        message = text;
        messageIsError = isError;
    }

    void setConfig(RobotConfig c) {
        config = c;
    }

    RobotConfig config() {
        return config;
    }

    /** INIT: build the program and run it up to waitForStart(). */
    synchronized void init(CodeLoader.Entry e) {
        stop();
        entry = e;
        message = null;
        warnedStuck = false;
        stopWallNanos = -1;
        final RunContext ctx = new RunContext();
        context = ctx;
        RobotConfig cfg = config;
        runConfig = cfg;
        HardwareMap map = SimDevices.createHardwareMap(world, ctx, cfg == null ? new RobotConfig(null) : cfg);
        SimTelemetry telemetry = new SimTelemetry(ctx);
        final OpMode opMode;
        try {
            opMode = e.type.getDeclaredConstructor().newInstance();
        } catch (InvocationTargetException ex) {
            crash(ctx, ex.getCause() != null ? ex.getCause() : ex);
            return;
        } catch (Throwable ex) {
            crash(ctx, ex);
            return;
        }
        OpModeSimBridge.attach(opMode, ctx, map, telemetry, gamepad1, gamepad2);
        world.onInit();
        phase = Phase.INIT;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                runProgram(opMode, ctx, telemetry);
            }
        }, "OpMode: " + e.name);
        t.setDaemon(true);
        thread = t;
        t.start();
    }

    /** START: let the program continue past waitForStart(). */
    synchronized void start() {
        RunContext ctx = context;
        if (phase == Phase.INIT && ctx != null) {
            world.onStart(clock.now());
            ctx.start();
            phase = Phase.RUNNING;
        }
    }

    /** STOP: end the program and cut power, like the Driver Station's stop button. */
    synchronized void stop() {
        RunContext ctx = context;
        if (ctx == null || ctx.finished || ctx.isStopRequested()) {
            return;
        }
        ctx.requestStop();
        Thread t = thread;
        if (t != null) {
            t.interrupt();
        }
        world.stopAllMotors();
        stopWallNanos = System.nanoTime();
        phase = Phase.STOPPED;
    }

    /** Called every frame: notices when a program ends or crashes. */
    synchronized void poll() {
        RunContext ctx = context;
        if (ctx == null) {
            return;
        }
        if (ctx.finished && (phase == Phase.INIT || phase == Phase.RUNNING || phase == Phase.STOPPED)) {
            if (ctx.error != null) {
                phase = Phase.CRASHED;
                message = ErrorHelp.explain(ctx.error, runConfig);
                messageIsError = true;
            } else if (phase != Phase.STOPPED) {
                phase = Phase.FINISHED;
            }
        }
        Thread t = thread;
        if (phase == Phase.STOPPED && !warnedStuck && t != null && t.isAlive()
                && System.nanoTime() - stopWallNanos > 2_000_000_000L) {
            warnedStuck = true;
            message = "Your program is still running after STOP. The simulator stopped the robot "
                    + "anyway. On a real robot this causes a \"stuck in stop\" error.\n"
                    + "Tip: every loop should check opModeIsActive(), for example\n"
                    + "    while (opModeIsActive()) { ... }";
            messageIsError = true;
        }
    }

    private void crash(RunContext ctx, Throwable t) {
        ctx.error = t;
        ctx.finished = true;
        phase = Phase.CRASHED;
        message = ErrorHelp.explain(t, runConfig);
        messageIsError = true;
    }

    private void runProgram(OpMode opMode, RunContext ctx, SimTelemetry telemetry) {
        try {
            if (opMode instanceof LinearOpMode) {
                ((LinearOpMode) opMode).runOpMode();
            } else {
                runIterative(opMode, ctx, telemetry);
            }
        } catch (InterruptedException e) {
            // stopped
        } catch (Throwable t) {
            ctx.error = t;
        } finally {
            ctx.finished = true;
            if (context == ctx) {
                world.stopAllMotors();
            }
        }
    }

    /** The older "iterative" style of OpMode: init(), init_loop(), start(), loop(), stop(). */
    private void runIterative(OpMode op, RunContext ctx, SimTelemetry telemetry) {
        op.init();
        telemetry.update();
        while (!ctx.isStarted() && !ctx.isStopRequested()) {
            op.init_loop();
            telemetry.update();
            clock.sleepUntil(clock.now() + 20_000_000L, ctx);
        }
        if (!ctx.isStopRequested()) {
            op.start();
            while (!ctx.isStopRequested()) {
                op.time = op.getRuntime();
                op.loop();
                telemetry.update();
                clock.sleepUntil(clock.now() + 20_000_000L, ctx);
            }
        }
        op.stop();
        telemetry.update();
    }
}
