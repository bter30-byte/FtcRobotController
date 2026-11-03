package robotsim;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * The Driver Station's "Configure Robot" screens: the saved configurations, then Control Hub
 * Portal, Control Hub, and a screen for each kind of device, with a type and a name per port.
 */
final class ConfigureRobotDialog extends JDialog {

    private static final Color BG = new Color(0x24, 0x0c, 0x4f);
    private static final Color TILE = new Color(0x34, 0x16, 0x72);
    private static final Color TILE_HOVER = new Color(0x45, 0x1f, 0x8f);
    private static final Color BAR = new Color(0x6d, 0x28, 0xd9);
    private static final Color TOP = new Color(0x19, 0x08, 0x38);
    private static final Color GREEN = new Color(0x1f, 0xb5, 0x4d);
    private static final Color TEXT = new Color(0xf2, 0xee, 0xfa);
    private static final Color DIM = new Color(0xb9, 0xa8, 0xe0);
    private static final Color NAME_BLUE = new Color(0x09, 0x69, 0xda);

    private static ConfigureRobotDialog instance;

    private final RobotConfig.Store store;
    private final Runnable onChange;
    private final JLabel title = new JLabel();
    private final JLabel path = new JLabel();
    private final JPanel body = new JPanel();
    private final JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
    private final JScrollPane scroll;

    /** The configuration being edited, and whether it has been saved before. */
    private RobotConfig working;
    private boolean isNew;
    private String status;

    /** Opens the window (or brings it to the front if it's already open). */
    static void open(Window owner, RobotConfig.Store store, Runnable onChange) {
        if (instance == null || !instance.isDisplayable()) {
            instance = new ConfigureRobotDialog(owner, store, onChange);
        }
        instance.showList();
        instance.setVisible(true);
        instance.toFront();
    }

    static ConfigureRobotDialog current() {
        return instance;
    }

    private ConfigureRobotDialog(Window owner, RobotConfig.Store store, Runnable onChange) {
        super(owner, "Configure Robot");
        this.store = store;
        this.onChange = onChange;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(TOP);
        header.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        title.setForeground(TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        path.setForeground(DIM);
        path.setFont(path.getFont().deriveFont(Font.PLAIN, 11.5f));
        header.add(title, BorderLayout.NORTH);
        header.add(path, BorderLayout.SOUTH);

        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(BG);
        body.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        JPanel bodyHolder = new WidthTrackingPanel();
        bodyHolder.setBackground(BG);
        bodyHolder.add(body, BorderLayout.NORTH);
        scroll = new JScrollPane(bodyHolder, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        footer.setBackground(TOP);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(header, BorderLayout.NORTH);
        getContentPane().add(scroll, BorderLayout.CENTER);
        getContentPane().add(footer, BorderLayout.SOUTH);
        setSize(600, 620);
        setLocationRelativeTo(owner);
    }

    // ------------------------------------------------------------------ screens

    /** The first screen: every saved configuration, with Edit, Activate, and Delete. */
    void showList() {
        start("Configure Robot", "Available configurations");
        String active = store.activeName();
        JLabel activeLabel = label(active == null ? "No configuration is active." : "Active Configuration: " + active,
                14f, Font.BOLD, active == null ? new Color(0xff, 0xb4, 0xa8) : GREEN);
        put(activeLabel);
        if (status != null) {
            put(label(status, 12f, Font.PLAIN, DIM));
            status = null;
        }
        gap(8);
        List<String> names = store.names();
        if (names.isEmpty()) {
            put(label("No saved configurations. Press New, or Restore CourseBot.", 13f, Font.PLAIN, DIM));
        }
        for (final String n : names) {
            JPanel row = tile();
            row.setLayout(new BorderLayout(8, 0));
            boolean isActive = n.equals(active);
            row.add(label(n + (isActive ? "   (active)" : ""), 14f, Font.BOLD, isActive ? GREEN : TEXT),
                    BorderLayout.CENTER);
            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
            buttons.setOpaque(false);
            buttons.add(button("Edit", new Runnable() {
                public void run() {
                    edit(n);
                }
            }));
            JButton activate = button("Activate", new Runnable() {
                public void run() {
                    activate(n);
                }
            });
            activate.setEnabled(!isActive);
            buttons.add(activate);
            buttons.add(button("Delete", new Runnable() {
                public void run() {
                    delete(n);
                }
            }));
            row.add(buttons, BorderLayout.EAST);
            put(row);
        }
        gap(12);
        put(label("<html>Saved on this laptop, the way a real configuration is saved on the Control Hub. "
                + "Programs find devices by the names in the <b>active</b> configuration. "
                + "Changes take effect the next time you press INIT.</html>", 11.5f, Font.PLAIN, DIM));

        footer.add(button("New", new Runnable() {
            public void run() {
                working = RobotConfig.fresh("");
                isNew = true;
                showUsbDevices();
            }
        }));
        footer.add(button("Restore CourseBot", new Runnable() {
            public void run() {
                restoreCourseBot();
            }
        }));
        footer.add(button("Close", new Runnable() {
            public void run() {
                dispose();
            }
        }));
        finish();
    }

    /** USB devices in the configuration: on a Control Hub, that's just the Control Hub Portal. */
    private void showUsbDevices() {
        start("USB Devices in configuration", configLabel());
        put(label("Pick a device to see what's plugged into it.", 12f, Font.PLAIN, DIM));
        gap(6);
        put(navRow("Control Hub Portal", "Embedded  ·  1 hub", new Runnable() {
            public void run() {
                showPortal();
            }
        }));
        footer.add(button("Save", new Runnable() {
            public void run() {
                save();
            }
        }));
        footer.add(button("Cancel", new Runnable() {
            public void run() {
                showList();
            }
        }));
        finish();
    }

    private void showPortal() {
        start("Control Hub Portal", configLabel() + "  ›  Control Hub Portal");
        put(navRow("Control Hub", "Port 173  ·  the hub inside the robot", new Runnable() {
            public void run() {
                showHub();
            }
        }));
        footer.add(button("Done", new Runnable() {
            public void run() {
                showUsbDevices();
            }
        }));
        footer.add(button("Cancel", new Runnable() {
            public void run() {
                showUsbDevices();
            }
        }));
        finish();
    }

    /** The list of device kinds: Motors, Servos, Digital Devices, ... I2C Bus 3. */
    private void showHub() {
        start("Control Hub", configLabel() + "  ›  Control Hub Portal  ›  Control Hub");
        for (final RobotConfig.Section section : RobotConfig.Section.values()) {
            int count = working.devices(section).size();
            String sub = count == 0 ? "nothing configured" : count + " configured";
            put(navRow(section.title, sub, new Runnable() {
                public void run() {
                    if (section.isI2c()) {
                        showI2c(section, null);
                    } else {
                        showPorts(section);
                    }
                }
            }));
        }
        footer.add(button("Done", new Runnable() {
            public void run() {
                showPortal();
            }
        }));
        footer.add(button("Cancel", new Runnable() {
            public void run() {
                showPortal();
            }
        }));
        finish();
    }

    /** One row per port: Port 0, 1, 2, ... each with a device type and a name. */
    private void showPorts(final RobotConfig.Section section) {
        start(section.title, configLabel() + "  ›  Control Hub  ›  " + section.title);
        final List<JComboBox<String>> types = new ArrayList<JComboBox<String>>();
        final List<JTextField> names = new ArrayList<JTextField>();
        for (int port = 0; port < section.ports; port++) {
            RobotConfig.Device d = working.at(section, port);
            JComboBox<String> type = typeBox(section, d == null ? RobotConfig.NOTHING : d.type);
            JTextField name = nameField(d == null ? "" : d.name);
            linkEnabled(type, name);
            types.add(type);
            names.add(name);
            put(portRow("Port " + port, type, name));
        }
        if (section.types.length == 1) {
            gap(6);
            put(label("The course robot has nothing plugged in here.", 12f, Font.PLAIN, DIM));
        } else if (section == RobotConfig.Section.DIGITAL) {
            gap(6);
            put(label("<html>Tip: a REV Touch Sensor in the port marked 0-1 is configured as "
                    + "<b>port 1</b>.</html>", 12f, Font.PLAIN, DIM));
        }
        footer.add(button("Done", new Runnable() {
            public void run() {
                List<RobotConfig.Device> list = new ArrayList<RobotConfig.Device>();
                for (int port = 0; port < section.ports; port++) {
                    String type = (String) types.get(port).getSelectedItem();
                    if (!RobotConfig.NOTHING.equals(type)) {
                        list.add(new RobotConfig.Device(section, port, type, names.get(port).getText().trim()));
                    }
                }
                if (accept(section, list, "Port ")) {
                    showHub();
                }
            }
        }));
        footer.add(button("Cancel", new Runnable() {
            public void run() {
                showHub();
            }
        }));
        finish();
    }

    /** An I2C bus has a list of devices; Add puts another one on the bus. */
    private void showI2c(final RobotConfig.Section section, List<RobotConfig.Device> rowsSoFar) {
        start(section.title, configLabel() + "  ›  Control Hub  ›  " + section.title);
        final List<RobotConfig.Device> rows = rowsSoFar != null ? rowsSoFar
                : new ArrayList<RobotConfig.Device>(working.devices(section));
        final List<JComboBox<String>> types = new ArrayList<JComboBox<String>>();
        final List<JTextField> names = new ArrayList<JTextField>();
        for (int i = 0; i < rows.size(); i++) {
            RobotConfig.Device d = rows.get(i);
            JComboBox<String> type = typeBox(section, d.type);
            JTextField name = nameField(d.name);
            linkEnabled(type, name);
            types.add(type);
            names.add(name);
            put(portRow("Device " + i, type, name));
        }
        if (rows.isEmpty()) {
            put(label("Nothing on this bus. Press Add to add a device.", 12f, Font.PLAIN, DIM));
        }
        gap(6);
        if (section == RobotConfig.Section.I2C0) {
            put(label("<html>The Control Hub's built-in IMU is always on bus 0, so other sensors go on "
                    + "bus 1, 2, or 3. (The simulator doesn't simulate the IMU.)</html>", 12f, Font.PLAIN, DIM));
        }
        footer.add(button("Add", new Runnable() {
            public void run() {
                List<RobotConfig.Device> keep = collect(section, types, names, true);
                keep.add(new RobotConfig.Device(section, keep.size(), RobotConfig.NOTHING, ""));
                showI2c(section, keep);
            }
        }));
        footer.add(button("Done", new Runnable() {
            public void run() {
                if (accept(section, collect(section, types, names, false), "Device ")) {
                    showHub();
                }
            }
        }));
        footer.add(button("Cancel", new Runnable() {
            public void run() {
                showHub();
            }
        }));
        finish();
    }

    private static List<RobotConfig.Device> collect(RobotConfig.Section section, List<JComboBox<String>> types,
                                                    List<JTextField> names, boolean keepEmpty) {
        List<RobotConfig.Device> list = new ArrayList<RobotConfig.Device>();
        for (int i = 0; i < types.size(); i++) {
            String type = (String) types.get(i).getSelectedItem();
            if (keepEmpty || !RobotConfig.NOTHING.equals(type)) {
                list.add(new RobotConfig.Device(section, list.size(), type, names.get(i).getText().trim()));
            }
        }
        return list;
    }

    // ------------------------------------------------------------------ actions

    /** Checks a device screen before Done: every device needs a name, and names can't repeat. */
    private boolean accept(RobotConfig.Section section, List<RobotConfig.Device> list, String rowWord) {
        for (RobotConfig.Device d : list) {
            if (d.name.isEmpty()) {
                error(rowWord + d.port + " (" + d.type + ") needs a name.\n\n"
                        + "This name is what your code puts in quotes: hardwareMap.get(..., \"name\").");
                return false;
            }
        }
        RobotConfig test = working.copy(working.name);
        test.replaceSection(section, list);
        String dup = test.duplicateName();
        if (dup != null) {
            error("Two devices are both named \"" + dup + "\".\n\nEvery device needs its own name, "
                    + "or hardwareMap.get can't tell them apart.");
            return false;
        }
        working.replaceSection(section, list);
        return true;
    }

    private void edit(String configName) {
        RobotConfig c = store.load(configName);
        if (c == null) {
            error("Couldn't read the configuration \"" + configName + "\".");
            return;
        }
        working = c;
        isNew = false;
        showUsbDevices();
    }

    private void save() {
        String configName = working.name;
        if (isNew) {
            Object answer = JOptionPane.showInputDialog(this, "Configuration name:", "Save configuration",
                    JOptionPane.PLAIN_MESSAGE, null, null, "MyRobot");
            if (answer == null) {
                return;
            }
            configName = answer.toString().trim();
            if (!RobotConfig.isValidName(configName)) {
                error("Use letters, numbers, spaces, - or _ (up to 40 characters).");
                return;
            }
            if (store.names().contains(configName) && JOptionPane.showConfirmDialog(this,
                    "\"" + configName + "\" already exists. Replace it?", "Save configuration",
                    JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                return;
            }
        }
        try {
            store.save(working.copy(configName));
        } catch (IOException e) {
            error("Couldn't save: " + e.getMessage());
            return;
        }
        boolean active = configName.equals(store.activeName());
        status = active ? "Saved \"" + configName + "\". It's active, so the next INIT uses it."
                : "Saved \"" + configName + "\". Press Activate to use it.";
        if (active) {
            onChange.run();
        }
        showList();
    }

    private void activate(String configName) {
        try {
            store.setActive(configName);
        } catch (IOException e) {
            error("Couldn't activate: " + e.getMessage());
            return;
        }
        status = "\"" + configName + "\" is active. The robot stopped; press INIT to run a program.";
        onChange.run();
        showList();
    }

    private void delete(String configName) {
        if (JOptionPane.showConfirmDialog(this, "Delete \"" + configName + "\"?", "Delete configuration",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        boolean wasActive = configName.equals(store.activeName());
        store.delete(configName);
        if (wasActive) {
            onChange.run();
        }
        showList();
    }

    private void restoreCourseBot() {
        try {
            store.save(RobotConfig.courseBot());
            store.setActive(RobotConfig.COURSE_BOT);
        } catch (IOException e) {
            error("Couldn't save: " + e.getMessage());
            return;
        }
        status = "CourseBot is back to the course robot's wiring, and it's active.";
        onChange.run();
        showList();
    }

    private void error(String text) {
        JOptionPane.showMessageDialog(this, text, "Configure Robot", JOptionPane.WARNING_MESSAGE);
    }

    private String configLabel() {
        return isNew ? "New configuration (not saved yet)" : working.name;
    }

    // ------------------------------------------------------------------ building blocks

    private void start(String heading, String breadcrumb) {
        title.setText(heading);
        path.setText(breadcrumb);
        body.removeAll();
        footer.removeAll();
    }

    private void finish() {
        body.revalidate();
        body.repaint();
        footer.revalidate();
        footer.repaint();
        scroll.getVerticalScrollBar().setValue(0);
    }

    private void put(JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (c instanceof JPanel) {
            c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
        }
        body.add(c);
        body.add(Box.createVerticalStrut(6));
    }

    private void gap(int height) {
        body.add(Box.createVerticalStrut(height));
    }

    private static JLabel label(String text, float size, int style, Color color) {
        // Long notes wrap to the window's width.
        JLabel l = new JLabel(text.startsWith("<html>") ? "<html><div style='width:430px'>" + text.substring(6) : text);
        l.setFont(l.getFont().deriveFont(style, size));
        l.setForeground(color);
        return l;
    }

    private static JPanel tile() {
        JPanel p = new JPanel();
        p.setBackground(TILE);
        p.setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 10));
        return p;
    }

    /** Keeps the screen as wide as the window, so long rows shrink instead of scrolling sideways. */
    private static final class WidthTrackingPanel extends JPanel implements javax.swing.Scrollable {
        WidthTrackingPanel() {
            super(new BorderLayout());
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(java.awt.Rectangle r, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(java.awt.Rectangle r, int orientation, int direction) {
            return 120;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    /** A row you click to go one screen deeper, like the Driver Station's lists. */
    private static JPanel navRow(String text, String sub, final Runnable onClick) {
        final JPanel row = tile();
        row.setLayout(new BorderLayout(8, 0));
        JPanel words = new JPanel(new BorderLayout());
        words.setOpaque(false);
        words.add(label(text, 14.5f, Font.BOLD, TEXT), BorderLayout.NORTH);
        words.add(label(sub, 11.5f, Font.PLAIN, DIM), BorderLayout.SOUTH);
        row.add(words, BorderLayout.CENTER);
        row.add(label("›", 22f, Font.BOLD, DIM), BorderLayout.EAST);
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        row.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onClick.run();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                row.setBackground(TILE_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                row.setBackground(TILE);
            }
        });
        return row;
    }

    private static JPanel portRow(String portText, JComboBox<String> type, JTextField name) {
        JPanel row = tile();
        row.setLayout(new BorderLayout(10, 0));
        JLabel port = label(portText, 13.5f, Font.BOLD, TEXT);
        port.setPreferredSize(new Dimension(72, 24));
        row.add(port, BorderLayout.WEST);
        row.add(type, BorderLayout.CENTER);
        row.add(name, BorderLayout.EAST);
        return row;
    }

    private static JComboBox<String> typeBox(RobotConfig.Section section, String selected) {
        JComboBox<String> box = new JComboBox<String>(section.types);
        box.setSelectedItem(selected);
        box.setFont(box.getFont().deriveFont(13f));
        box.setEnabled(section.types.length > 1);
        return box;
    }

    private static JTextField nameField(String text) {
        JTextField f = new JTextField(text, 13);
        f.setPreferredSize(new Dimension(170, 28));
        f.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
        f.setForeground(NAME_BLUE);
        f.setDisabledTextColor(new Color(0x8c, 0x95, 0x9f));
        return f;
    }

    /** A port set to "Nothing" has no name. */
    private static void linkEnabled(final JComboBox<String> type, final JTextField name) {
        name.setEnabled(!RobotConfig.NOTHING.equals(type.getSelectedItem()));
        type.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean on = !RobotConfig.NOTHING.equals(type.getSelectedItem());
                name.setEnabled(on);
                if (!on) {
                    name.setText("");
                }
                if (on && name.getText().trim().isEmpty()) {
                    name.requestFocusInWindow();
                }
            }
        });
    }

    private static JButton button(String text, final Runnable action) {
        JButton b = new JButton(text);
        b.setUI(new BasicButtonUI());
        b.setBackground(BAR);
        b.setForeground(Color.WHITE);
        b.setFont(b.getFont().deriveFont(Font.BOLD, 12.5f));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
            }
        });
        return b;
    }
}
