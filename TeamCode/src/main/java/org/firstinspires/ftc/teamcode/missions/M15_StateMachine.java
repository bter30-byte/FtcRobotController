package org.firstinspires.ftc.teamcode.missions;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

/*
 * MISSION 15 - State Machine                       (try it after Lessons 10 and 14)
 * Goal: drive to the wall, back up, turn right, and park in the box.
 *
 * The program is a state machine: the robot is always in ONE state, and each state says
 * what to do and when to move on to the next one. The Driver Station shows the state.
 *
 * This program has TWO bugs. Watch the "State" line: does the robot do what each state says?
 */

@Autonomous(name = "M15 State Machine", group = "Missions")
public class M15_StateMachine extends LinearOpMode {

    enum State { DRIVE_TO_WALL, BACK_UP, TURN_RIGHT, DRIVE_TO_PARK, DONE }

    private DcMotor leftDrive;
    private DcMotor rightDrive;

    @Override
    public void runOpMode() {
        leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        leftDrive.setDirection(DcMotor.Direction.REVERSE);
        leftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        TouchSensor bumper = hardwareMap.get(TouchSensor.class, "touch_sensor");

        State state = State.DRIVE_TO_WALL;
        ElapsedTime timer = new ElapsedTime();

        waitForStart();
        timer.reset();

        while (opModeIsActive() && state != State.DONE) {
            switch (state) {
                case DRIVE_TO_WALL:
                    setDrive(0.4, 0.4);
                    if (bumper.isPressed() || timer.seconds() > 5) {
                        state = State.BACK_UP;
                        timer.reset();
                    }
                    break;

                case BACK_UP:
                    setDrive(-0.4, -0.4);
                    if (timer.seconds() > 0.5) {
                        state = State.TURN_RIGHT;
                    }

                case TURN_RIGHT:
                    setDrive(0.4, -0.4);
                    if (timer.seconds() > 0.75) {
                        state = State.DRIVE_TO_PARK;
                        timer.reset();
                    }
                    break;

                case DRIVE_TO_PARK:
                    setDrive(0.4, 0.4);
                    if (timer.seconds() > 1.5) {
                        state = State.DONE;
                    }
                    break;

                default:
                    break;
            }
            telemetry.addData("State", state);
            telemetry.addData("Time in this state", "%.2f", timer.seconds());
            telemetry.update();
        }

        setDrive(0, 0);
    }

    private void setDrive(double left, double right) {
        leftDrive.setPower(left);
        rightDrive.setPower(right);
    }
}
