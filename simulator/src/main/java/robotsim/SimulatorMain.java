package robotsim;

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
                new SimFrame(new CodeLoader(teamCode)).setVisible(true);
            }
        });
    }
}
