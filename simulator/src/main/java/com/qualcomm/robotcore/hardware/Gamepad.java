package com.qualcomm.robotcore.hardware;

import robotsim.SimClock;

/**
 * Simulator copy of the FTC SDK gamepad. Same public fields as the real one. In the
 * simulator, the keyboard fills them in (see the key map in the simulator window).
 */
public class Gamepad {

    public volatile float left_stick_x = 0f;
    public volatile float left_stick_y = 0f;
    public volatile float right_stick_x = 0f;
    public volatile float right_stick_y = 0f;
    public volatile boolean dpad_up = false;
    public volatile boolean dpad_down = false;
    public volatile boolean dpad_left = false;
    public volatile boolean dpad_right = false;
    public volatile boolean a = false;
    public volatile boolean b = false;
    public volatile boolean x = false;
    public volatile boolean y = false;
    public volatile boolean guide = false;
    public volatile boolean start = false;
    public volatile boolean back = false;
    public volatile boolean left_bumper = false;
    public volatile boolean right_bumper = false;
    public volatile boolean left_stick_button = false;
    public volatile boolean right_stick_button = false;
    public volatile float left_trigger = 0f;
    public volatile float right_trigger = 0f;
    public volatile boolean left_trigger_pressed = false;
    public volatile boolean right_trigger_pressed = false;

    // PlayStation-style names for the same buttons
    public volatile boolean circle = false;
    public volatile boolean cross = false;
    public volatile boolean triangle = false;
    public volatile boolean square = false;
    public volatile boolean share = false;
    public volatile boolean options = false;
    public volatile boolean touchpad = false;
    public volatile boolean ps = false;

    public volatile int id = 0;

    // ---- edge detection (xWasPressed / xWasReleased) ----
    private static final int A = 0, B = 1, X = 2, Y = 3, DUP = 4, DDOWN = 5, DLEFT = 6, DRIGHT = 7,
            LB = 8, RB = 9, START = 10, BACK = 11, GUIDE = 12, LSB = 13, RSB = 14, LT = 15, RT = 16;
    private static final int COUNT = 17;
    private final boolean[] last = new boolean[COUNT];
    private final int[] presses = new int[COUNT];
    private final int[] releases = new int[COUNT];
    private final int[] pressesSeen = new int[COUNT];
    private final int[] releasesSeen = new int[COUNT];

    private volatile long rumbleUntilNanos = 0;

    public Gamepad() {
    }

    // ---- rumble ----
    public void rumble(int durationMs) {
        rumbleUntilNanos = SimClock.nowNanos() + durationMs * 1_000_000L;
    }

    public void rumble(double rumble1, double rumble2, int durationMs) {
        rumble(durationMs);
    }

    public void rumbleBlips(int count) {
        rumble(count * 250);
    }

    public void stopRumble() {
        rumbleUntilNanos = 0;
    }

    public boolean isRumbling() {
        return SimClock.nowNanos() < rumbleUntilNanos;
    }

    // ---- was pressed / was released ----
    private synchronized boolean pressed(int i) {
        boolean r = presses[i] != pressesSeen[i];
        pressesSeen[i] = presses[i];
        return r;
    }

    private synchronized boolean released(int i) {
        boolean r = releases[i] != releasesSeen[i];
        releasesSeen[i] = releases[i];
        return r;
    }

    public boolean aWasPressed() { return pressed(A); }
    public boolean aWasReleased() { return released(A); }
    public boolean bWasPressed() { return pressed(B); }
    public boolean bWasReleased() { return released(B); }
    public boolean xWasPressed() { return pressed(X); }
    public boolean xWasReleased() { return released(X); }
    public boolean yWasPressed() { return pressed(Y); }
    public boolean yWasReleased() { return released(Y); }
    public boolean crossWasPressed() { return pressed(A); }
    public boolean crossWasReleased() { return released(A); }
    public boolean circleWasPressed() { return pressed(B); }
    public boolean circleWasReleased() { return released(B); }
    public boolean squareWasPressed() { return pressed(X); }
    public boolean squareWasReleased() { return released(X); }
    public boolean triangleWasPressed() { return pressed(Y); }
    public boolean triangleWasReleased() { return released(Y); }
    public boolean dpadUpWasPressed() { return pressed(DUP); }
    public boolean dpadUpWasReleased() { return released(DUP); }
    public boolean dpadDownWasPressed() { return pressed(DDOWN); }
    public boolean dpadDownWasReleased() { return released(DDOWN); }
    public boolean dpadLeftWasPressed() { return pressed(DLEFT); }
    public boolean dpadLeftWasReleased() { return released(DLEFT); }
    public boolean dpadRightWasPressed() { return pressed(DRIGHT); }
    public boolean dpadRightWasReleased() { return released(DRIGHT); }
    public boolean leftBumperWasPressed() { return pressed(LB); }
    public boolean leftBumperWasReleased() { return released(LB); }
    public boolean rightBumperWasPressed() { return pressed(RB); }
    public boolean rightBumperWasReleased() { return released(RB); }
    public boolean startWasPressed() { return pressed(START); }
    public boolean startWasReleased() { return released(START); }
    public boolean optionsWasPressed() { return pressed(START); }
    public boolean optionsWasReleased() { return released(START); }
    public boolean backWasPressed() { return pressed(BACK); }
    public boolean backWasReleased() { return released(BACK); }
    public boolean shareWasPressed() { return pressed(BACK); }
    public boolean shareWasReleased() { return released(BACK); }
    public boolean guideWasPressed() { return pressed(GUIDE); }
    public boolean guideWasReleased() { return released(GUIDE); }
    public boolean psWasPressed() { return pressed(GUIDE); }
    public boolean psWasReleased() { return released(GUIDE); }
    public boolean leftStickButtonWasPressed() { return pressed(LSB); }
    public boolean leftStickButtonWasReleased() { return released(LSB); }
    public boolean rightStickButtonWasPressed() { return pressed(RSB); }
    public boolean rightStickButtonWasReleased() { return released(RSB); }
    public boolean leftTriggerWasPressed() { return pressed(LT); }
    public boolean leftTriggerWasReleased() { return released(LT); }
    public boolean rightTriggerWasPressed() { return pressed(RT); }
    public boolean rightTriggerWasReleased() { return released(RT); }

    public boolean atRest() {
        return left_stick_x == 0 && left_stick_y == 0 && right_stick_x == 0 && right_stick_y == 0
                && left_trigger == 0 && right_trigger == 0;
    }

    // ---- simulator plumbing: not part of the real SDK ----

    /** Copies a new keyboard snapshot into the public fields and records button edges. */
    public synchronized void simUpdate(float lx, float ly, float rx, float ry, float lt, float rt,
                                       boolean[] buttons) {
        left_stick_x = lx;
        left_stick_y = ly;
        right_stick_x = rx;
        right_stick_y = ry;
        left_trigger = lt;
        right_trigger = rt;
        left_trigger_pressed = lt > 0.5f;
        right_trigger_pressed = rt > 0.5f;
        boolean[] now = new boolean[COUNT];
        System.arraycopy(buttons, 0, now, 0, Math.min(buttons.length, LT));
        now[LT] = left_trigger_pressed;
        now[RT] = right_trigger_pressed;
        a = cross = now[A];
        b = circle = now[B];
        x = square = now[X];
        y = triangle = now[Y];
        dpad_up = now[DUP];
        dpad_down = now[DDOWN];
        dpad_left = now[DLEFT];
        dpad_right = now[DRIGHT];
        left_bumper = now[LB];
        right_bumper = now[RB];
        start = options = now[START];
        back = share = now[BACK];
        guide = ps = now[GUIDE];
        left_stick_button = now[LSB];
        right_stick_button = now[RSB];
        for (int i = 0; i < COUNT; i++) {
            if (now[i] && !last[i]) {
                presses[i]++;
            } else if (!now[i] && last[i]) {
                releases[i]++;
            }
            last[i] = now[i];
        }
    }

    /** Button order used by {@link #simUpdate}. */
    public static final int BUTTON_COUNT = LT;

    public static int index(String name) {
        switch (name) {
            case "a": return A;
            case "b": return B;
            case "x": return X;
            case "y": return Y;
            case "dpad_up": return DUP;
            case "dpad_down": return DDOWN;
            case "dpad_left": return DLEFT;
            case "dpad_right": return DRIGHT;
            case "left_bumper": return LB;
            case "right_bumper": return RB;
            case "start": return START;
            case "back": return BACK;
            case "guide": return GUIDE;
            default: return -1;
        }
    }
}
