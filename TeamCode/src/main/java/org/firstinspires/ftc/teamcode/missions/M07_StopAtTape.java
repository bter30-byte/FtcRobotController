package org.firstinspires.ftc.teamcode.missions;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.SwitchableLight;

/*
 * MISSION 7 - Stop at the Tape                           (try it after Lesson 17)
 * Goal: drive forward and STOP with the color sensor on the black tape.
 *
 * The tape moves every time you press Reset robot, so a timer can't do this. The color
 * sensor can: the floor is bright, the tape is dark.
 *
 * This program has TWO bugs. Before you press START, look at the brightness on the
 * Driver Station: is the floor brighter or darker than the threshold?
 */

@Autonomous(name = "M07 Stop at the Tape", group = "Missions")
public class M07_StopAtTape extends LinearOpMode {

    // Halfway between the floor (about 0.28) and the tape (about 0.04)
    static final double BLACK_THRESHOLD = 0.15;

    private NormalizedColorSensor colorSensor;

    @Override
    public void runOpMode() {
        DcMotor leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        DcMotor rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        leftDrive.setDirection(DcMotor.Direction.REVERSE);
        leftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        colorSensor = hardwareMap.get(NormalizedColorSensor.class, "sensor_color");
        // Gain makes the readings brighter. The floor and tape numbers above are at gain 15.
        colorSensor.setGain(1);
        if (colorSensor instanceof SwitchableLight) {
            ((SwitchableLight) colorSensor).enableLight(true);
        }

        // Until START is pressed, show what the sensor sees.
        while (opModeInInit()) {
            telemetry.addData("Brightness", "%.3f", getBrightness());
            telemetry.addData("Threshold", BLACK_THRESHOLD);
            telemetry.update();
        }

        leftDrive.setPower(0.3);
        rightDrive.setPower(0.3);

        // Keep driving while the sensor sees the floor
        while (opModeIsActive() && getBrightness() < BLACK_THRESHOLD) {
            telemetry.addData("Looking for tape. Brightness", "%.3f", getBrightness());
            telemetry.update();
        }

        // Found the tape: stop
        leftDrive.setPower(0);
        rightDrive.setPower(0);
        sleep(1000);
    }

    /** How bright the floor under the sensor is: 0.0 = black, 1.0 = white. */
    private double getBrightness() {
        return colorSensor.getNormalizedColors().alpha;
    }
}
