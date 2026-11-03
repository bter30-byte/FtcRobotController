package robotsim;

import java.util.Collections;
import java.util.List;

/** Everything about one run of one OpMode: INIT, START, STOP, telemetry, errors. */
public final class RunContext {

    private final Object lock = new Object();
    private volatile boolean started = false;
    private volatile boolean stopRequested = false;
    volatile boolean finished = false;
    volatile Throwable error = null;
    volatile List<String> telemetryLines = Collections.emptyList();

    public boolean isStarted() {
        return started;
    }

    public boolean isStopRequested() {
        return stopRequested;
    }

    /** Hardware commands are ignored after STOP, like the real SDK force-stopping a program. */
    public boolean acceptsCommands() {
        return !stopRequested;
    }

    public void awaitStart() {
        synchronized (lock) {
            while (!started && !stopRequested) {
                try {
                    lock.wait(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    void start() {
        synchronized (lock) {
            started = true;
            lock.notifyAll();
        }
    }

    public void requestStop() {
        synchronized (lock) {
            stopRequested = true;
            lock.notifyAll();
        }
    }

    void publishTelemetry(List<String> lines) {
        telemetryLines = lines;
    }

    public List<String> telemetry() {
        return telemetryLines;
    }
}
