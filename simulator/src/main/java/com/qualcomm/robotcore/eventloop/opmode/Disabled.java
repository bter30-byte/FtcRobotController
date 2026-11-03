package com.qualcomm.robotcore.eventloop.opmode;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Simulator copy of the FTC SDK annotation. On the real robot it hides an OpMode from the
 * Driver Station menu. The simulator still lists it, marked "(hidden on robot)".
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Disabled {
}
