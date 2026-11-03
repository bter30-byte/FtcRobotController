package robotsim;

import com.qualcomm.robotcore.hardware.Gamepad;

import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * Turns keyboard keys into gamepad1, so you can drive without a real gamepad.
 *
 *   Left stick:  W A S D        Right stick: arrow keys       Hold Shift: half speed
 *   A B X Y:     J K U I        Bumpers: Q E                  Triggers: Z C
 *   D-pad:       T F G H        Start: Enter   Back: Backspace
 *   Press the touch sensor by hand: Space
 */
final class KeyboardGamepad {

    static final String LEGEND = "<html><b>Left stick</b> W A S D &nbsp; <b>Right stick</b> arrow keys "
            + "&nbsp; <b>Shift</b> = half speed<br><b>A B X Y</b> = J K U I &nbsp; <b>Bumpers</b> Q E "
            + "&nbsp; <b>Triggers</b> Z C &nbsp; <b>D-pad</b> T F G H<br><b>Space</b> = press the "
            + "touch sensor by hand &nbsp; (click the field first if keys don't work)</html>";

    private final Set<Integer> down = new HashSet<Integer>();
    private boolean shift;

    synchronized boolean handles(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_W: case KeyEvent.VK_A: case KeyEvent.VK_S: case KeyEvent.VK_D:
            case KeyEvent.VK_UP: case KeyEvent.VK_DOWN: case KeyEvent.VK_LEFT: case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_J: case KeyEvent.VK_K: case KeyEvent.VK_U: case KeyEvent.VK_I:
            case KeyEvent.VK_Q: case KeyEvent.VK_E: case KeyEvent.VK_Z: case KeyEvent.VK_C:
            case KeyEvent.VK_T: case KeyEvent.VK_F: case KeyEvent.VK_G: case KeyEvent.VK_H:
            case KeyEvent.VK_ENTER: case KeyEvent.VK_BACK_SPACE: case KeyEvent.VK_SPACE:
            case KeyEvent.VK_SHIFT:
                return true;
            default:
                return false;
        }
    }

    synchronized void keyChanged(int keyCode, boolean pressed, boolean shiftDown) {
        shift = shiftDown;
        if (pressed) {
            down.add(keyCode);
        } else {
            down.remove(keyCode);
        }
    }

    synchronized void releaseAll() {
        down.clear();
        shift = false;
    }

    /** True while any gamepad key is held, so the Driver Station can light up the user icon. */
    synchronized boolean anyDown() {
        return !down.isEmpty();
    }

    /** True while a stick key (W A S D or an arrow) is held. */
    synchronized boolean stickKeyDown() {
        for (int key : new int[]{KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D,
                KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT}) {
            if (down.contains(key)) {
                return true;
            }
        }
        return false;
    }

    synchronized boolean touchHeld() {
        return down.contains(KeyEvent.VK_SPACE);
    }

    private boolean is(int key) {
        return down.contains(key);
    }

    private float axis(int negative, int positive) {
        float v = (is(positive) ? 1f : 0f) - (is(negative) ? 1f : 0f);
        return shift ? v * 0.5f : v;
    }

    /** Copies the current keys into a gamepad. Remember: stick UP is negative. */
    synchronized void applyTo(Gamepad g) {
        boolean[] b = new boolean[Gamepad.BUTTON_COUNT];
        b[Gamepad.index("a")] = is(KeyEvent.VK_J);
        b[Gamepad.index("b")] = is(KeyEvent.VK_K);
        b[Gamepad.index("x")] = is(KeyEvent.VK_U);
        b[Gamepad.index("y")] = is(KeyEvent.VK_I);
        b[Gamepad.index("dpad_up")] = is(KeyEvent.VK_T);
        b[Gamepad.index("dpad_down")] = is(KeyEvent.VK_G);
        b[Gamepad.index("dpad_left")] = is(KeyEvent.VK_F);
        b[Gamepad.index("dpad_right")] = is(KeyEvent.VK_H);
        b[Gamepad.index("left_bumper")] = is(KeyEvent.VK_Q);
        b[Gamepad.index("right_bumper")] = is(KeyEvent.VK_E);
        b[Gamepad.index("start")] = is(KeyEvent.VK_ENTER);
        b[Gamepad.index("back")] = is(KeyEvent.VK_BACK_SPACE);
        g.simUpdate(
                axis(KeyEvent.VK_A, KeyEvent.VK_D),
                axis(KeyEvent.VK_W, KeyEvent.VK_S),
                axis(KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT),
                axis(KeyEvent.VK_UP, KeyEvent.VK_DOWN),
                is(KeyEvent.VK_Z) ? 1f : 0f,
                is(KeyEvent.VK_C) ? 1f : 0f,
                b);
    }
}
