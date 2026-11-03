package com.qualcomm.robotcore.hardware;

/** Simulator copy of the FTC SDK class: one color reading, each part from 0.0 to 1.0. */
public class NormalizedRGBA {
    public float red;
    public float green;
    public float blue;
    /** Overall brightness. Dark tape gives a small number; a light floor gives a bigger one. */
    public float alpha;

    public NormalizedRGBA() {
    }

    public int toColor() {
        int a = clip255(alpha), r = clip255(red), g = clip255(green), b = clip255(blue);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static int clip255(float v) {
        int i = Math.round(v * 255f);
        return i < 0 ? 0 : (i > 255 ? 255 : i);
    }
}
