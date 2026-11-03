package robotsim;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

/**
 * The "Select TeleOp" / "Select Autonomous" list, like the Driver Station's. Programs are grouped
 * under their group name. Programs that are hidden on the real robot by @Disabled are shown in
 * gray, because the simulator can still run them.
 */
final class OpModeChooser {

    private OpModeChooser() {
    }

    /** One row: either a group heading (entry == null) or a program. */
    private static final class Row {
        final String heading;
        final CodeLoader.Entry entry;

        Row(String heading, CodeLoader.Entry entry) {
            this.heading = heading;
            this.entry = entry;
        }
    }

    static void show(Component anchor, final DriverStationPanel.Host host, boolean teleOp) {
        Window owner = SwingUtilities.getWindowAncestor(anchor);
        final JDialog dialog = new JDialog(owner);
        dialog.setUndecorated(true);

        DefaultListModel<Row> model = new DefaultListModel<Row>();
        String group = null;
        for (CodeLoader.Entry e : host.programs()) {
            if (e.teleOp != teleOp) {
                continue;
            }
            String g = e.group.isEmpty() ? "(no group)" : e.group;
            if (!g.equals(group)) {
                model.addElement(new Row(g, null));
                group = g;
            }
            model.addElement(new Row(null, e));
        }
        if (model.isEmpty()) {
            model.addElement(new Row("No " + (teleOp ? "TeleOp" : "Autonomous") + " programs loaded", null));
        }

        final JList<Row> list = new JList<Row>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        final CodeLoader.Entry current = host.selectedProgram();
        list.setCellRenderer(new ListCellRenderer<Row>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends Row> l, Row row, int index,
                                                          boolean selected, boolean focus) {
                JLabel label = new JLabel();
                label.setOpaque(true);
                if (row.entry == null) {
                    label.setText(row.heading);
                    label.setFont(label.getFont().deriveFont(Font.BOLD, 12f));
                    label.setForeground(new Color(0x6d, 0x28, 0xd9));
                    label.setBackground(new Color(0xf3, 0xee, 0xfc));
                    label.setBorder(BorderFactory.createEmptyBorder(6, 10, 4, 10));
                } else {
                    String text = row.entry.name + (row.entry.disabled ? "   (hidden on robot)" : "");
                    label.setText(text);
                    label.setFont(label.getFont().deriveFont(row.entry == current ? Font.BOLD : Font.PLAIN, 14f));
                    label.setForeground(row.entry.disabled ? new Color(0x6e, 0x77, 0x81) : new Color(0x1f, 0x23, 0x28));
                    label.setBackground(selected ? new Color(0xe3, 0xd7, 0xfb) : Color.WHITE);
                    label.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xe6, 0xe6, 0xe6)),
                            BorderFactory.createEmptyBorder(8, 26, 8, 10)));
                }
                return label;
            }
        });
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int i = list.locationToIndex(e.getPoint());
                if (i >= 0 && list.getModel().getElementAt(i).entry != null) {
                    host.chooseProgram(list.getModel().getElementAt(i).entry);
                    dialog.dispose();
                }
            }
        });
        list.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dialog.dispose();
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    Row r = list.getSelectedValue();
                    if (r != null && r.entry != null) {
                        host.chooseProgram(r.entry);
                        dialog.dispose();
                    }
                }
            }
        });

        JLabel title = new JLabel(teleOp ? "Select TeleOp" : "Select Autonomous");
        title.setFont(title.getFont().deriveFont(Font.PLAIN, 19f));
        title.setForeground(new Color(0x6d, 0x28, 0xd9));
        title.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(0x6d, 0x28, 0xd9)),
                BorderFactory.createEmptyBorder(10, 12, 8, 12)));

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createLineBorder(new Color(0x9e, 0x9e, 0x9e)));
        content.add(title, BorderLayout.NORTH);
        content.add(scroll, BorderLayout.CENTER);
        dialog.setContentPane(content);

        int height = Math.min(460, 52 + model.getSize() * 34);
        dialog.setSize(new Dimension(Math.max(300, anchor.getWidth() - 40), height));
        Point p = anchor.getLocationOnScreen();
        dialog.setLocation(p.x + 20, p.y + 24);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowDeactivated(WindowEvent e) {
                dialog.dispose();
            }
        });
        dialog.setVisible(true);
        list.requestFocusInWindow();
    }
}
