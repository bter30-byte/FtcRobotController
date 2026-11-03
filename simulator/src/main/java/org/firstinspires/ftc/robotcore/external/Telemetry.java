package org.firstinspires.ftc.robotcore.external;

/** Simulator copy of the FTC SDK telemetry interface (the parts team code commonly uses). */
public interface Telemetry {

    interface Item {
        Item setCaption(String caption);

        Item setValue(String format, Object... args);

        Item setValue(Object value);

        Item addData(String caption, String format, Object... args);

        Item addData(String caption, Object value);
    }

    interface Line {
        Item addData(String caption, String format, Object... args);

        Item addData(String caption, Object value);
    }

    interface Log {
        void add(String entry);

        void add(String format, Object... args);

        void clear();

        void setCapacity(int capacity);

        int getCapacity();
    }

    Item addData(String caption, String format, Object... args);

    Item addData(String caption, Object value);

    <T> Item addData(String caption, Func<T> valueProducer);

    <T> Item addData(String caption, String format, Func<T> valueProducer);

    boolean removeItem(Item item);

    Line addLine();

    Line addLine(String lineCaption);

    boolean removeLine(Line line);

    /** Sends everything added since the last update() to the Driver Station screen. */
    boolean update();

    void clear();

    void clearAll();

    boolean isAutoClear();

    void setAutoClear(boolean autoClear);

    int getMsTransmissionInterval();

    void setMsTransmissionInterval(int msTransmissionInterval);

    String getItemSeparator();

    void setItemSeparator(String itemSeparator);

    String getCaptionValueSeparator();

    void setCaptionValueSeparator(String captionValueSeparator);

    void speak(String text);

    Log log();
}
