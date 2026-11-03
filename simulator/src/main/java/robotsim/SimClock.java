package robotsim;

/**
 * Simulator time. The physics loop moves it forward; sleep() and ElapsedTime read it.
 * Because everything uses this clock, slow motion slows the robot AND the program together.
 */
public final class SimClock {

    private static volatile SimClock active;

    private long nanos = 0;

    public static SimClock get() {
        return active;
    }

    public static void setActive(SimClock clock) {
        active = clock;
    }

    /** Simulator time if a simulator is running, otherwise the real clock. */
    public static long nowNanos() {
        SimClock c = active;
        return c == null ? System.nanoTime() : c.now();
    }

    public synchronized long now() {
        return nanos;
    }

    public synchronized void advance(long deltaNanos) {
        nanos += deltaNanos;
        notifyAll();
    }

    /**
     * Waits until simulator time reaches {@code targetNanos}. Returns early (false) if the
     * program is stopped or the thread is interrupted.
     */
    public boolean sleepUntil(long targetNanos, RunContext context) {
        synchronized (this) {
            while (nanos < targetNanos) {
                if (context != null && context.isStopRequested()) {
                    return false;
                }
                try {
                    wait(20);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }
        return true;
    }
}
