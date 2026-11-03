package robotsim;

/** Turns a crash into a message a beginner can act on. */
final class ErrorHelp {

    private ErrorHelp() {
    }

    static String explain(Throwable t) {
        StringBuilder sb = new StringBuilder();
        String where = whereInYourCode(t);
        String msg = t.getMessage() == null ? "" : t.getMessage();

        if (t instanceof IllegalArgumentException && msg.startsWith("Unable to find a hardware device")) {
            sb.append(msg).append(where).append("\n\nTip: the name in quotes must match the robot's ")
                    .append("configuration exactly (capitals and _ count). This robot has:\n")
                    .append("    left_drive, right_drive, arm   (DcMotor)\n")
                    .append("    claw   (Servo)\n")
                    .append("    touch_sensor   (TouchSensor)\n")
                    .append("    sensor_color   (NormalizedColorSensor or ColorSensor)");
        } else if (t instanceof IllegalStateException && msg.contains("RUN_TO_POSITION")) {
            sb.append(msg).append(where).append("\n\nTip: call setTargetPosition(...) BEFORE ")
                    .append("setMode(DcMotor.RunMode.RUN_TO_POSITION).");
        } else if (t instanceof NullPointerException) {
            sb.append("Something was null (empty)").append(where).append(".\n\nTip: did you use a ")
                    .append("variable before giving it a value? For devices, make sure ")
                    .append("hardwareMap.get(...) runs first.");
        } else if (t instanceof ArithmeticException && msg.contains("/ by zero")) {
            sb.append("You divided by zero").append(where).append('.');
        } else if (t instanceof StackOverflowError) {
            sb.append("A method keeps calling itself forever").append(where).append('.');
        } else if (t instanceof NoSuchMethodException || t instanceof InstantiationException) {
            sb.append("The simulator couldn't create your program. OpMode classes must be public ")
                    .append("and have no constructor arguments.");
        } else {
            sb.append(t.getClass().getSimpleName());
            if (!msg.isEmpty()) {
                sb.append(": ").append(msg);
            }
            sb.append(where);
        }
        return sb.toString();
    }

    /** Finds the first line of team code in the stack trace, e.g. " (M05_BugHunt.java, line 31)". */
    private static String whereInYourCode(Throwable t) {
        for (StackTraceElement e : t.getStackTrace()) {
            if (e.getClassName().startsWith("org.firstinspires.ftc.teamcode") && e.getLineNumber() > 0) {
                return String.format(" (%s, line %d)", e.getFileName(), e.getLineNumber());
            }
        }
        return "";
    }
}
