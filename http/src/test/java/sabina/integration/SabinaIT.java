/*
 * Copyright © 2011 Per Wendel. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package sabina.integration;

import static java.lang.System.getProperty;
import static java.util.logging.Level.FINEST;
import static java.util.logging.Logger.getLogger;
import static sabina.Sabina.*;
import static sabina.integration.TestScenario.*;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import sabina.Router.VoidHandler;

/**
 * WARNING!!! Run in a single thread. Multi thread in this test can cause arbitrary breakages.
 *
 * @author jamming
 */
@Test public final class SabinaIT {
    private static TestScenario testScenario = new TestScenario ("undertow", 4567, true, true);
    private static String part = "param";

    @BeforeClass public static void setupLogging () {
        Logger rootLogger = getLogger ("");
        Stream.of (rootLogger.getHandlers ()).forEach (it -> it.setLevel (FINEST));
        rootLogger.setLevel (FINEST);
    }

    @BeforeClass public static void setupFile () throws IOException { ServerIT.setupFile (); }

    @BeforeClass public static void setup () {
        get ("/reset/route", it -> "should not be executed");
        reset ();
        get ("/error500", (VoidHandler)it -> { throw new IllegalStateException (); });

        secure (getKeyStoreLocation (), getKeystorePassword ());
        filesLocation ("/public", getProperty ("java.io.tmpdir"));

        start (testScenario.port);
        testScenario.waitForStartup ();
    }

    @AfterClass public static void cleanup () {
        stop ();
        testScenario.waitForShutdown ();
    }

    @AfterClass public static void cleanupFile () { ServerIT.cleanupFile (); }

    public void routes_after_reset_are_not_available () {
        UrlResponse response = testScenario.doGet ("/reset/route");
        assert response.status == 404;
    }

    @Test (expectedExceptions = IllegalStateException.class)
    public void reset_on_a_running_server_raises_an_error () {
        reset ();
    }

    public void uncaugh_exception_return_a_500_error () {
        UrlResponse response = testScenario.doGet ("/error500");
        assert response.body.equals ("<html><body><h2>500 Internal Error</h2></body></html>");
        assert response.status == 500;
    }
}
