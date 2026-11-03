package org.firstinspires.ftc.teamcode.lessons;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.SwitchableLight;

/*
 * LESSON 17 (part 2) - Color Sensor: Follow the Black Tape
 * Big idea: a loop that checks a sensor and CORRECTS, over and over, can follow a path it has
 * never seen before.
 *
 * Trick: follow the LEFT EDGE of the tape, not the middle.
 *   Sensor sees tape (dark)    -> we drifted onto the tape  -> steer LEFT,  back toward the floor
 *   Sensor sees floor (bright) -> we drifted off the tape   -> steer RIGHT, back toward the tape
 * The robot wiggles along the edge, and the edge leads it wherever the tape goes.
 *
 * ROBOT CONFIGURATION NEEDED: "left_drive", "right_drive" (DC motors),
 *                             "sensor_color" (REV Color Sensor V3 on I2C bus 1)
 */

@Autonomous(name = "L17 Follow Tape", group = "Lessons")
@Disabled  // Hides this ORIGINAL from the menu. Delete this line in YOUR copy so your copy shows up.
public class L17_FollowTape extends LinearOpMode {

    static final double BLACK_THRESHOLD = 0.15;   // same idea as part 1: halfway between floor and tape
    static final double BASE_POWER = 0.3;         // how fast to go forward
    static final double STEER_POWER = 0.15;       // how hard to correct

    @Override
    public void runOpMode() {
        DcMotor leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        DcMotor rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        leftDrive.setDirection(DcMotor.Direction.REVERSE);

        NormalizedColorSensor colorSensor = hardwareMap.get(NormalizedColorSensor.class, "sensor_color");
        colorSensor.setGain(15);
        if (colorSensor instanceof SwitchableLight) {
            ((SwitchableLight) colorSensor).enableLight(true);
        }

        telemetry.addData("Status", "Put the sensor on the LEFT edge of the tape, then press START.");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            double brightness = colorSensor.getNormalizedColors().alpha;
            double leftPower;
            double rightPower;

            if (brightness < BLACK_THRESHOLD) {
                // On the tape: steer left
                leftPower = BASE_POWER - STEER_POWER;
                rightPower = BASE_POWER + STEER_POWER;
            } else {
                // On the floor: steer right
                leftPower = BASE_POWER + STEER_POWER;
                rightPower = BASE_POWER - STEER_POWER;
            }

            leftDrive.setPower(leftPower);
            rightDrive.setPower(rightPower);

            telemetry.addData("Brightness", "%.3f", brightness);
            telemetry.addData("Seeing", brightness < BLACK_THRESHOLD ? "TAPE (steer left)" : "FLOOR (steer right)");
            telemetry.update();
        }
    }
}
