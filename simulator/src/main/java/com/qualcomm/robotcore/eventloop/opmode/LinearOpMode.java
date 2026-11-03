package com.qualcomm.robotcore.eventloop.opmode;

import robotsim.SimClock;

/**
 * Simulator copy of the FTC SDK's LinearOpMode. Time here is simulator time, so slow motion
 * slows down sleep() and ElapsedTime too.
 */
public abstract class LinearOpMode extends OpMode {

    public LinearOpMode() {
    }

    public abstract void runOpMode() throws InterruptedException;

    /** Waits until START (or STOP) is pressed. */
    public void waitForStart() {
        if (simContext != null) {
            simContext.awaitStart();
        }
    }

    public final void idle() {
        // The real SDK yields here. A tiny pause keeps busy loops from hogging the laptop.
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public final void sleep(long milliseconds) {
        SimClock clock = SimClock.get();
        if (clock == null) {
            try {
                Thread.sleep(milliseconds);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return;
        }
        clock.sleepUntil(clock.now() + milliseconds * 1_000_000L, simContext);
    }

    public final boolean opModeIsActive() {
        boolean active = isStarted() && !isStopRequested();
        if (active) {
            idle();
        }
        return active;
    }

    public final boolean opModeInInit() {
        return !isStarted() && !isStopRequested();
    }

    public final boolean isStarted() {
        return simContext != null && simContext.isStarted();
    }

    public final boolean isStopRequested() {
        return simContext == null || simContext.isStopRequested() || Thread.currentThread().isInterrupted();
    }

    // In the real SDK these iterative methods are final in LinearOpMode and unused by teams.
    @Override
    public final void init() {
    }

    @Override
    public final void init_loop() {
    }

    @Override
    public final void start() {
    }

    @Override
    public final void loop() {
    }

    @Override
    public final void stop() {
    }
}
