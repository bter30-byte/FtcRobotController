package robotsim;

import java.awt.Desktop;
import java.awt.Window;
import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * FTC Robot Simulator: runs the team's OpModes on a simulated robot, using the same FTC code
 * (LinearOpMode, hardwareMap, setPower, sleep, telemetry, gamepad1...) as the real robot.
 * Start it from Android Studio with the "Robot Simulator" run configuration.
 */
public final class SimulatorMain {

    private SimulatorMain() {
    }

    public static void main(String[] args) {
        // macOS: the name shown in the menu bar and Dock (instead of "java"). Ignored elsewhere.
        System.setProperty("apple.awt.application.name", "FTC Robot Simulator");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // the default look is fine
        }
        final File teamCode = CodeLoader.locateTeamCode();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (teamCode == null) {
                    JOptionPane.showMessageDialog(null,
                            "Couldn't find the TeamCode folder.\nStart the simulator from Android Studio "
                                    + "with the \"Robot Simulator\" run configuration.",
                            "FTC Robot Simulator", JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
                SimFrame frame = new SimFrame(new CodeLoader(teamCode));
                frame.setVisible(true);
                bringToFront(frame);
            }
        });
    }

    /**
     * Android Studio starts the simulator in the background, and on macOS a program started that
     * way can open behind Android Studio. This asks for the window to come to the front.
     */
    private static void bringToFront(Window window) {
        try {
            // Desktop.requestForeground exists from Java 9 on; look it up by name so this still
            // compiles for Java 8 (the robot's Java version).
            Desktop.Action foreground = Desktop.Action.valueOf("APP_REQUEST_FOREGROUND");
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(foreground)) {
                Desktop.class.getMethod("requestForeground", boolean.class).invoke(Desktop.getDesktop(), true);
            }
        } catch (Exception ignored) {
            // older Java or not supported here: toFront() below still helps
        }
        window.toFront();
        window.requestFocus();
    }
}
