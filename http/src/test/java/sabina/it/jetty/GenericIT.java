/*
 * Copyright (c) 2015. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package sabina.it.jetty;

import static sabina.util.TestUtil.*;

import java.io.IOException;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test public class GenericIT extends sabina.it.undertow.GenericIT {

    @BeforeClass public static void setup () throws InterruptedException, IOException {
        resetBackend ();
        setBackend ("jetty");
        sabina.it.undertow.GenericIT.setup ();
    }

    public void notFoundJetty () throws Exception {
        notFound ();
    }
}
