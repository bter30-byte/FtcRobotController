package robotsim;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A robot configuration, like the one saved on a real Control Hub: which device type is on
 * which port, and what it's called. hardwareMap.get(...) looks names up here.
 *
 * The simulated robot is always wired the same way (see SimDevices). The configuration only
 * decides what each port is called, so a wrong configuration fails the same way it would on a
 * real robot.
 */
final class RobotConfig {

    static final String NOTHING = "Nothing";
    static final String GOBILDA = "goBILDA 5202/3/4 series";
    static final String HD_HEX = "REV Robotics 20:1 HD Hex Motor";
    static final String CORE_HEX = "REV Robotics Core Hex Motor";
    static final String SERVO = "Servo";
    static final String TOUCH = "REV Touch Sensor";
    static final String COLOR = "REV Color Sensor V3";
    static final String IMU = "REV internal IMU (BHI260AP)";

    static final String COURSE_BOT = "CourseBot";

    /** The device lists on the Control Hub screen. I2C buses have a list instead of fixed ports. */
    enum Section {
        MOTORS("Motors", 4, NOTHING, GOBILDA, HD_HEX, CORE_HEX),
        SERVOS("Servos", 6, NOTHING, SERVO),
        DIGITAL("Digital Devices", 8, NOTHING, TOUCH),
        PWM("PWM Devices", 2, NOTHING),
        ANALOG("Analog Input Devices", 4, NOTHING),
        I2C0("I2C Bus 0", -1, NOTHING, IMU, COLOR),
        I2C1("I2C Bus 1", -1, NOTHING, COLOR),
        I2C2("I2C Bus 2", -1, NOTHING, COLOR),
        I2C3("I2C Bus 3", -1, NOTHING, COLOR);

        final String title;
        final int ports;
        final String[] types;

        Section(String title, int ports, String... types) {
            this.title = title;
            this.ports = ports;
            this.types = types;
        }

        boolean isI2c() {
            return ports < 0;
        }

        int bus() {
            return ordinal() - I2C0.ordinal();
        }

        static Section i2c(int bus) {
            return values()[I2C0.ordinal() + bus];
        }

        /** How the Robot panel and error messages name a port, e.g. "Motor 0", "I2C bus 1". */
        String portLabel(int port) {
            switch (this) {
                case MOTORS:
                    return "Motor " + port;
                case SERVOS:
                    return "Servo " + port;
                case DIGITAL:
                    return "Digital " + port;
                case PWM:
                    return "PWM " + port;
                case ANALOG:
                    return "Analog " + port;
                default:
                    return "I2C bus " + bus();
            }
        }
    }

    /** One configured device. For I2C devices, port is its place in the bus's list. */
    static final class Device {
        final Section section;
        final int port;
        final String type;
        final String name;

        Device(Section section, int port, String type, String name) {
            this.section = section;
            this.port = port;
            this.type = type;
            this.name = name;
        }
    }

    final String name;
    private final List<Device> devices = new ArrayList<Device>();

    RobotConfig(String name) {
        this.name = name;
    }

    /** The configuration every lesson uses (see curriculum/reference/robot-configuration.md). */
    static RobotConfig courseBot() {
        RobotConfig c = new RobotConfig(COURSE_BOT);
        c.add(Section.MOTORS, 0, GOBILDA, "left_drive");
        c.add(Section.MOTORS, 1, GOBILDA, "right_drive");
        c.add(Section.MOTORS, 2, GOBILDA, "arm");
        c.add(Section.SERVOS, 0, SERVO, "claw");
        c.add(Section.DIGITAL, 1, TOUCH, "touch_sensor");
        c.add(Section.I2C0, 0, IMU, "imu");
        c.add(Section.I2C1, 0, COLOR, "sensor_color");
        return c;
    }

    /** What "New" starts with: like a real Control Hub, only the built-in IMU. */
    static RobotConfig fresh(String name) {
        RobotConfig c = new RobotConfig(name);
        c.add(Section.I2C0, 0, IMU, "imu");
        return c;
    }

    RobotConfig copy(String newName) {
        RobotConfig c = new RobotConfig(newName);
        c.devices.addAll(devices);
        return c;
    }

    void add(Section section, int port, String type, String deviceName) {
        devices.add(new Device(section, port, type, deviceName));
    }

    List<Device> devices() {
        return Collections.unmodifiableList(devices);
    }

    List<Device> devices(Section section) {
        List<Device> result = new ArrayList<Device>();
        for (Device d : devices) {
            if (d.section == section) {
                result.add(d);
            }
        }
        return result;
    }

    Device at(Section section, int port) {
        for (Device d : devices) {
            if (d.section == section && d.port == port) {
                return d;
            }
        }
        return null;
    }

    /** Replaces everything in one section (the Done button on a device screen). */
    void replaceSection(Section section, List<Device> newDevices) {
        for (int i = devices.size() - 1; i >= 0; i--) {
            if (devices.get(i).section == section) {
                devices.remove(i);
            }
        }
        devices.addAll(newDevices);
    }

    /** The name a port's device has, or "-" when nothing is configured there. */
    String nameAt(Section section, int port) {
        Device d = at(section, port);
        return d == null ? "-" : d.name;
    }

    String nameOnBus(int bus) {
        List<Device> list = devices(Section.i2c(bus));
        for (Device d : list) {
            if (COLOR.equals(d.type)) {
                return d.name;
            }
        }
        return "-";
    }

    /** First duplicate name, or null. Every device needs its own name. */
    String duplicateName() {
        List<String> seen = new ArrayList<String>();
        for (Device d : devices) {
            if (seen.contains(d.name)) {
                return d.name;
            }
            seen.add(d.name);
        }
        return null;
    }

    /** For the "Unable to find a hardware device" tip: what this configuration has. */
    String describeForHelp() {
        StringBuilder sb = new StringBuilder();
        appendGroup(sb, "DcMotor", Section.MOTORS, null);
        appendGroup(sb, "Servo", Section.SERVOS, null);
        appendGroup(sb, "TouchSensor", Section.DIGITAL, TOUCH);
        StringBuilder colors = new StringBuilder();
        for (Device d : devices) {
            if (COLOR.equals(d.type)) {
                colors.append(colors.length() == 0 ? "" : ", ").append(d.name);
            }
        }
        if (colors.length() > 0) {
            sb.append("    ").append(colors).append("   (NormalizedColorSensor or ColorSensor)\n");
        }
        if (sb.length() == 0) {
            sb.append("    (nothing yet)\n");
        }
        return sb.toString();
    }

    private void appendGroup(StringBuilder sb, String javaType, Section section, String onlyType) {
        StringBuilder names = new StringBuilder();
        for (Device d : devices) {
            if (d.section == section && (onlyType == null || onlyType.equals(d.type))) {
                names.append(names.length() == 0 ? "" : ", ").append(d.name);
            }
        }
        if (names.length() > 0) {
            sb.append("    ").append(names).append("   (").append(javaType).append(")\n");
        }
    }

    // ------------------------------------------------------------------ saving

    // XML tags, in the style of the configuration files a real Robot Controller saves.
    private static final String[][] TAGS = {
            {GOBILDA, "goBILDA5202SeriesMotor"},
            {HD_HEX, "RevRobotics20HdHexMotor"},
            {CORE_HEX, "RevRoboticsCoreHexMotor"},
            {SERVO, "Servo"},
            {TOUCH, "RevTouchSensor"},
            {COLOR, "RevColorSensorV3"},
            {IMU, "ControlHubImuBHI260AP"},
    };

    private static String tagFor(String type) {
        for (String[] t : TAGS) {
            if (t[0].equals(type)) {
                return t[1];
            }
        }
        return null;
    }

    private static String typeFor(String tag) {
        for (String[] t : TAGS) {
            if (t[1].equals(tag)) {
                return t[0];
            }
        }
        return null;
    }

    String toXml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n");
        sb.append("<Robot type=\"FirstInspires-FTC\">\n");
        sb.append("    <LynxUsbDevice name=\"Control Hub Portal\" serialNumber=\"(embedded)\" parentModuleAddress=\"173\">\n");
        sb.append("        <LynxModule name=\"Control Hub\" port=\"173\">\n");
        for (Device d : devices) {
            sb.append("            <").append(tagFor(d.type)).append(" name=\"").append(escape(d.name))
                    .append("\" port=\"").append(d.port).append('"');
            if (d.section.isI2c()) {
                sb.append(" bus=\"").append(d.section.bus()).append('"');
            }
            sb.append(" />\n");
        }
        sb.append("        </LynxModule>\n    </LynxUsbDevice>\n</Robot>\n");
        return sb.toString();
    }

    private static String escape(String s) {
        return s.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;");
    }

    static RobotConfig fromXml(String configName, File file) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
        RobotConfig c = new RobotConfig(configName);
        NodeList all = doc.getElementsByTagName("*");
        for (int i = 0; i < all.getLength(); i++) {
            Node n = all.item(i);
            if (!(n instanceof Element)) {
                continue;
            }
            Element e = (Element) n;
            String type = typeFor(e.getTagName());
            if (type == null) {
                continue;
            }
            int port = parse(e.getAttribute("port"));
            Section section;
            if (isMotorType(type)) {
                section = Section.MOTORS;
            } else if (SERVO.equals(type)) {
                section = Section.SERVOS;
            } else if (TOUCH.equals(type)) {
                section = Section.DIGITAL;
            } else {
                section = Section.i2c(Math.max(0, Math.min(3, parse(e.getAttribute("bus")))));
            }
            c.add(section, port, type, e.getAttribute("name"));
        }
        return c;
    }

    private static boolean isMotorType(String type) {
        return GOBILDA.equals(type) || HD_HEX.equals(type) || CORE_HEX.equals(type);
    }

    private static int parse(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // ------------------------------------------------------------------ storage

    /**
     * The saved configurations on this laptop, like the ones saved on a real Control Hub. They
     * live in the user's home folder (not in the repo), so each laptop has its own.
     */
    static final class Store {
        private static final String ACTIVE_FILE = "active.txt";
        final File dir;

        Store(File dir) {
            this.dir = dir;
        }

        static Store openDefault() {
            String override = System.getProperty("robotsim.configdir");
            File dir = override != null ? new File(override)
                    : new File(System.getProperty("user.home"), ".ftc-robot-simulator" + File.separator + "configurations");
            return new Store(dir);
        }

        /** First run: save CourseBot and make it active. */
        void ensureCourseBot() {
            if (names().isEmpty() && activeName() == null) {
                try {
                    save(courseBot());
                    setActive(COURSE_BOT);
                } catch (IOException ignored) {
                    // can't write: active() falls back to CourseBot in memory
                }
            }
        }

        List<String> names() {
            List<String> names = new ArrayList<String>();
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.getName().endsWith(".xml")) {
                        names.add(f.getName().substring(0, f.getName().length() - 4));
                    }
                }
            }
            Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
            return names;
        }

        RobotConfig load(String configName) {
            File f = new File(dir, configName + ".xml");
            if (!f.isFile()) {
                return null;
            }
            try {
                return fromXml(configName, f);
            } catch (Exception e) {
                return null;
            }
        }

        void save(RobotConfig c) throws IOException {
            if (!dir.isDirectory() && !dir.mkdirs()) {
                throw new IOException("Couldn't create " + dir);
            }
            Writer w = new OutputStreamWriter(new FileOutputStream(new File(dir, c.name + ".xml")),
                    StandardCharsets.UTF_8);
            try {
                w.write(c.toXml());
            } finally {
                w.close();
            }
        }

        void delete(String configName) {
            new File(dir, configName + ".xml").delete();
            if (configName.equals(activeName())) {
                new File(dir, ACTIVE_FILE).delete();
            }
        }

        String activeName() {
            File f = new File(dir, ACTIVE_FILE);
            if (!f.isFile()) {
                return null;
            }
            try {
                String s = new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8).trim();
                return s.isEmpty() ? null : s;
            } catch (IOException e) {
                return null;
            }
        }

        void setActive(String configName) throws IOException {
            if (!dir.isDirectory() && !dir.mkdirs()) {
                throw new IOException("Couldn't create " + dir);
            }
            Files.write(new File(dir, ACTIVE_FILE).toPath(), configName.getBytes(StandardCharsets.UTF_8));
        }

        /** The active configuration, or null if none is active. */
        RobotConfig active() {
            String n = activeName();
            if (n == null) {
                return names().isEmpty() ? courseBot() : null;
            }
            return load(n);
        }
    }

    /** Configuration names become file names, so keep them simple. */
    static boolean isValidName(String s) {
        return s != null && s.matches("[A-Za-z0-9][A-Za-z0-9 _-]{0,39}");
    }
}
