package com.qualcomm.robotcore.util;

import java.util.concurrent.TimeUnit;

import robotsim.SimClock;

/** Simulator copy of the FTC SDK stopwatch. Measures simulator time. */
public class ElapsedTime {

    public enum Resolution {
        SECONDS, MILLISECONDS
    }

    public static final long SECOND_IN_NANO = 1_000_000_000L;
    public static final long MILLIS_IN_NANO = 1_000_000L;

    private long startNanos;
    private final Resolution resolution;

    public ElapsedTime() {
        this(Resolution.SECONDS);
    }

    public ElapsedTime(long startTimeNanoseconds) {
        this.resolution = Resolution.SECONDS;
        this.startNanos = startTimeNanoseconds;
    }

    public ElapsedTime(Resolution resolution) {
        this.resolution = resolution;
        reset();
    }

    public long now(TimeUnit unit) {
        return unit.convert(SimClock.nowNanos(), TimeUnit.NANOSECONDS);
    }

    public void reset() {
        startNanos = SimClock.nowNanos();
    }

    public double startTime() {
        return startNanos / (double) unitNanos();
    }

    public long startTimeNanoseconds() {
        return startNanos;
    }

    /** Elapsed time in this stopwatch's resolution (seconds by default). */
    public double time() {
        return nanoseconds() / (double) unitNanos();
    }

    public long time(TimeUnit unit) {
        return unit.convert(nanoseconds(), TimeUnit.NANOSECONDS);
    }

    public double seconds() {
        return nanoseconds() / (double) SECOND_IN_NANO;
    }

    public double milliseconds() {
        return nanoseconds() / (double) MILLIS_IN_NANO;
    }

    public long nanoseconds() {
        return SimClock.nowNanos() - startNanos;
    }

    public Resolution getResolution() {
        return resolution;
    }

    private long unitNanos() {
        return resolution == Resolution.SECONDS ? SECOND_IN_NANO : MILLIS_IN_NANO;
    }

    @Override
    public String toString() {
        return String.format("%.4f %s", time(), resolution == Resolution.SECONDS ? "seconds" : "milliseconds");
    }
}
