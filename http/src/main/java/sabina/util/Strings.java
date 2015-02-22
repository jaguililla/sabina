/*
 * Copyright (c) 2015. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package sabina.util;

/**
 * TODO .
 *
 * @author jam
 */
public class Strings {
    public static boolean isNullOrEmpty (String str) {
        return com.google.common.base.Strings.isNullOrEmpty (str);
    }

    private Strings () {
        throw new IllegalStateException ();
    }
}
