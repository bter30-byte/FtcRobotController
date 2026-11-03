package robotsim;

import org.firstinspires.ftc.robotcore.external.Func;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/** Telemetry that shows up in the simulator's Driver Station panel. */
final class SimTelemetry implements Telemetry {

    private final RunContext context;
    private final List<ItemImpl> items = new ArrayList<ItemImpl>();
    private final LinkedList<String> logLines = new LinkedList<String>();
    private boolean autoClear = true;
    private int msInterval = 250;
    private String itemSeparator = " | ";
    private String captionSeparator = " : ";
    private int logCapacity = 9;

    SimTelemetry(RunContext context) {
        this.context = context;
    }

    private final class ItemImpl implements Item, Line {
        String caption;
        String value = "";
        Func<?> producer;
        String format;
        boolean isLine;
        final List<ItemImpl> children = new ArrayList<ItemImpl>();

        String render() {
            if (isLine) {
                StringBuilder sb = new StringBuilder(caption == null ? "" : caption);
                for (int i = 0; i < children.size(); i++) {
                    if (sb.length() > 0 && (i > 0 || caption.length() > 0)) {
                        sb.append(i == 0 ? " " : itemSeparator);
                    }
                    sb.append(children.get(i).render());
                }
                return sb.toString();
            }
            String v = value;
            if (producer != null) {
                Object o = producer.value();
                v = format != null ? String.format(Locale.US, format, o) : String.valueOf(o);
            }
            StringBuilder sb = new StringBuilder(caption).append(captionSeparator).append(v);
            for (ItemImpl c : children) {
                sb.append(itemSeparator).append(c.render());
            }
            return sb.toString();
        }

        @Override
        public Item setCaption(String c) {
            caption = c;
            return this;
        }

        @Override
        public Item setValue(String fmt, Object... args) {
            value = String.format(Locale.US, fmt, args);
            producer = null;
            return this;
        }

        @Override
        public Item setValue(Object v) {
            value = String.valueOf(v);
            producer = null;
            return this;
        }

        @Override
        public Item addData(String c, String fmt, Object... args) {
            ItemImpl child = newItem(c, String.format(Locale.US, fmt, args));
            children.add(child);
            return child;
        }

        @Override
        public Item addData(String c, Object v) {
            ItemImpl child = newItem(c, String.valueOf(v));
            children.add(child);
            return child;
        }
    }

    private ItemImpl newItem(String caption, String value) {
        ItemImpl item = new ItemImpl();
        item.caption = caption == null ? "" : caption;
        item.value = value;
        return item;
    }

    @Override
    public synchronized Item addData(String caption, String format, Object... args) {
        ItemImpl item = newItem(caption, String.format(Locale.US, format, args));
        items.add(item);
        return item;
    }

    @Override
    public synchronized Item addData(String caption, Object value) {
        ItemImpl item = newItem(caption, String.valueOf(value));
        items.add(item);
        return item;
    }

    @Override
    public synchronized <T> Item addData(String caption, Func<T> valueProducer) {
        ItemImpl item = newItem(caption, "");
        item.producer = valueProducer;
        items.add(item);
        return item;
    }

    @Override
    public synchronized <T> Item addData(String caption, String format, Func<T> valueProducer) {
        ItemImpl item = newItem(caption, "");
        item.producer = valueProducer;
        item.format = format;
        items.add(item);
        return item;
    }

    @Override
    public synchronized boolean removeItem(Item item) {
        return items.remove(item);
    }

    @Override
    public synchronized Line addLine() {
        return addLine("");
    }

    @Override
    public synchronized Line addLine(String lineCaption) {
        ItemImpl line = newItem(lineCaption, "");
        line.isLine = true;
        items.add(line);
        return line;
    }

    @Override
    public synchronized boolean removeLine(Line line) {
        return items.remove(line);
    }

    @Override
    public synchronized boolean update() {
        List<String> lines = new ArrayList<String>(logLines);
        for (ItemImpl item : items) {
            lines.add(item.render());
        }
        context.publishTelemetry(Collections.unmodifiableList(lines));
        if (autoClear) {
            items.clear();
        }
        return true;
    }

    @Override
    public synchronized void clear() {
        items.clear();
    }

    @Override
    public synchronized void clearAll() {
        items.clear();
        logLines.clear();
    }

    @Override
    public boolean isAutoClear() {
        return autoClear;
    }

    @Override
    public void setAutoClear(boolean autoClear) {
        this.autoClear = autoClear;
    }

    @Override
    public int getMsTransmissionInterval() {
        return msInterval;
    }

    @Override
    public void setMsTransmissionInterval(int ms) {
        msInterval = ms;
    }

    @Override
    public String getItemSeparator() {
        return itemSeparator;
    }

    @Override
    public void setItemSeparator(String s) {
        itemSeparator = s;
    }

    @Override
    public String getCaptionValueSeparator() {
        return captionSeparator;
    }

    @Override
    public void setCaptionValueSeparator(String s) {
        captionSeparator = s;
    }

    @Override
    public void speak(String text) {
        // The simulator doesn't talk.
    }

    @Override
    public Log log() {
        return new Log() {
            @Override
            public void add(String entry) {
                synchronized (SimTelemetry.this) {
                    logLines.add(entry);
                    while (logLines.size() > logCapacity) {
                        logLines.removeFirst();
                    }
                }
            }

            @Override
            public void add(String format, Object... args) {
                add(String.format(Locale.US, format, args));
            }

            @Override
            public void clear() {
                synchronized (SimTelemetry.this) {
                    logLines.clear();
                }
            }

            @Override
            public void setCapacity(int capacity) {
                logCapacity = capacity;
            }

            @Override
            public int getCapacity() {
                return logCapacity;
            }
        };
    }
}
