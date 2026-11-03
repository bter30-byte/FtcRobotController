package com.qualcomm.robotcore.eventloop.opmode;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import robotsim.RunContext;

/** Simulator-only helper that hands an OpMode its hardware map, telemetry, and gamepads. */
public final class OpModeSimBridge {
    private OpModeSimBridge() {
    }

    public static void attach(OpMode opMode, RunContext context, HardwareMap map, Telemetry telemetry,
                              Gamepad gamepad1, Gamepad gamepad2) {
        opMode.simAttach(context, map, telemetry, gamepad1, gamepad2);
    }
}
