package org.firstinspires.ftc.teamcode.lessons;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.SwitchableLight;

/*
 * LESSON 17 (part 1) - Color Sensor: Stop at the Black Tape
 * Big idea: a SENSOR turns the world into a number, and a THRESHOLD turns that number into a
 * yes/no decision.
 *
 * The color sensor points DOWN at the floor, near the front of the robot. It measures how much
 * light bounces back:
 *     gray floor tile  ->  medium brightness   (about 0.28)
 *     black tape       ->  very dark           (about 0.04)
 * The THRESHOLD is a number halfway between the two. Brighter than the threshold = floor.
 * Darker = tape.
 *
 * The robot drives forward until the sensor sees the tape, then stops.
 *
 * ROBOT CONFIGURATION NEEDED: "left_drive", "right_drive" (DC motors),
 *                             "sensor_color" (REV Color Sensor V3 on I2C bus 1)
 */

@Autonomous(name = "L17 Stop At Tape", group = "Lessons")
@Disabled  // Hides this ORIGINAL from the menu. Delete this line in YOUR copy so your copy shows up.
public class L17_StopAtTape extends LinearOpMode {

    // Measure YOUR floor and YOUR tape, then put this number halfway between them.
    static final double BLACK_THRESHOLD = 0.15;
    static final double DRIVE_POWER = 0.3;

    private NormalizedColorSensor colorSensor;

    @Override
    public void runOpMode() {
        DcMotor leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        DcMotor rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        leftDrive.setDirection(DcMotor.Direction.REVERSE);
        // BRAKE stops the robot quickly, so it doesn't roll past the tape.
        leftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        colorSensor = hardwareMap.get(NormalizedColorSensor.class, "sensor_color");
        // Gain makes the readings brighter. FIRST's own line-following sample uses 15.
        colorSensor.setGain(15);
        // Turn on the sensor's own light, so it doesn't depend on the room's lights.
        if (colorSensor instanceof SwitchableLight) {
            ((SwitchableLight) colorSensor).enableLight(true);
        }

        // Instead of waitForStart(), this loop keeps showing the brightness until START is
        // pressed. Hold the robot over the floor, then over the tape, and write both numbers down.
        while (opModeInInit()) {
            telemetry.addData("Brightness", "%.3f", getBrightness());
            telemetry.addData("Threshold", BLACK_THRESHOLD);
            telemetry.addLine("Press START to drive to the tape.");
            telemetry.update();
        }

        leftDrive.setPower(DRIVE_POWER);
        rightDrive.setPower(DRIVE_POWER);

        // Keep driving as long as the sensor sees the floor (brighter than the threshold).
        while (opModeIsActive() && getBrightness() > BLACK_THRESHOLD) {
            telemetry.addData("Looking for tape. Brightness", "%.3f", getBrightness());
            telemetry.update();
        }

        leftDrive.setPower(0);
        rightDrive.setPower(0);

        telemetry.addData("Found the tape! Brightness", "%.3f", getBrightness());
        telemetry.update();
        sleep(2000);
    }

    /** Returns how bright the floor under the sensor is: 0.0 = black, 1.0 = white. */
    private double getBrightness() {
        NormalizedRGBA colors = colorSensor.getNormalizedColors();
        return colors.alpha;
    }
}
