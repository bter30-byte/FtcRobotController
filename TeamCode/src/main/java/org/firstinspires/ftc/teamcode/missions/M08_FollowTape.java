package org.firstinspires.ftc.teamcode.missions;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.SwitchableLight;

/*
 * MISSION 8 - Follow the Tape                            (try it after Lesson 17)
 * Goal: follow the black tape all the way to the END.
 *
 * The robot rides the tape's LEFT edge: when the sensor sees tape, it has drifted too far
 * right, so it should steer left. When it sees the floor, it should steer right.
 *
 * This program has TWO bugs. Watch the "Seeing" line on the Driver Station: does it
 * ever say FLOOR? Fix that first, then watch what happens at the first turn.
 * (Lesson 17: the floor reads about 0.28, the tape about 0.04.)
 */

@Autonomous(name = "M08 Follow the Tape", group = "Missions")
public class M08_FollowTape extends LinearOpMode {

    static final double BLACK_THRESHOLD = 0.5;    // halfway between floor and tape
    static final double BASE_POWER = 0.3;         // how fast to go forward
    static final double STEER_POWER = 0.03;       // how hard to correct

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

        telemetry.addData("Status", "Sensor on the LEFT edge of the tape. Press START.");
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
            telemetry.addData("Seeing", brightness < BLACK_THRESHOLD ? "TAPE" : "FLOOR");
            telemetry.update();
        }
    }
}
