/*
 * Copyright (c) 2015. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package sabina.util;

import com.google.common.base.Preconditions;

/**
 * TODO .
 *
 * @author jam
 */
public class Checks {
    public static void checkArgument (boolean condition) {
        Preconditions.checkArgument (condition);
    }

    public static void checkArgument (boolean condition, String message) {
        Preconditions.checkArgument (condition, message);
    }

    public static void checkArgument (boolean condition, String message, Object... arguments) {
        Preconditions.checkArgument (condition, message, arguments);
    }

    private Checks () {
        throw new IllegalStateException ();
    }
}
