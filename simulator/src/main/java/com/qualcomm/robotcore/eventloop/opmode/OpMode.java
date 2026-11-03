package com.qualcomm.robotcore.eventloop.opmode;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.HashMap;

import robotsim.RunContext;
import robotsim.SimClock;

/**
 * Simulator copy of the FTC SDK's iterative OpMode. Same public fields and methods that
 * team code uses; the simulator fills them in before the program starts.
 */
public abstract class OpMode {

    public static final HashMap<String, Object> blackboard = new HashMap<String, Object>();

    public volatile Gamepad gamepad1 = new Gamepad();
    public volatile Gamepad gamepad2 = new Gamepad();
    public Telemetry telemetry;
    public volatile HardwareMap hardwareMap;
    public volatile double time = 0.0;

    // Set by the simulator. Not part of the real SDK's public API.
    RunContext simContext;
    private long startNanos = SimClock.nowNanos();

    public OpMode() {
    }

    public abstract void init();

    public void init_loop() {
    }

    public void start() {
    }

    public abstract void loop();

    public void stop() {
    }

    public final void requestOpModeStop() {
        if (simContext != null) {
            simContext.requestStop();
        }
    }

    public final void terminateOpModeNow() {
        requestOpModeStop();
    }

    public double getRuntime() {
        return (SimClock.nowNanos() - startNanos) / 1e9;
    }

    public void resetRuntime() {
        startNanos = SimClock.nowNanos();
    }

    public void updateTelemetry(Telemetry telemetry) {
        telemetry.update();
    }

    // ---- simulator plumbing ----

    void simAttach(RunContext context, HardwareMap map, Telemetry tel, Gamepad g1, Gamepad g2) {
        this.simContext = context;
        this.hardwareMap = map;
        this.telemetry = tel;
        this.gamepad1 = g1;
        this.gamepad2 = g2;
        this.startNanos = SimClock.nowNanos();
    }
}
