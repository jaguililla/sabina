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

package sabina;

import static java.lang.System.exit;
import static java.lang.System.out;
import static sabina.util.SparkTestUtil.waitForShutdown;
import static sabina.util.SparkTestUtil.waitForStartup;
import static org.testng.Assert.*;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import sabina.util.SparkTestUtil;

public class ServletTest {

    private static final String SOMEPATH = "/somepath";
    private static final int PORT = 9393;
    private static final Server server = new Server ();

    private static SparkTestUtil testUtil;

    @AfterClass public static void shutDown () throws Exception {
        server.stop ();
        SparkTestUtil.waitForShutdown ("localhost", PORT);
    }

    @BeforeClass public static void startUp () {
        testUtil = new SparkTestUtil (PORT);

        ServerConnector connector = new ServerConnector (server);

        // Set some timeout options to make debugging easier.
        connector.setIdleTimeout (1000 * 60 * 60);
        connector.setSoLingerTime (-1);
        connector.setPort (PORT);
        server.setConnectors (new Connector[] { connector });

        WebAppContext bb = new WebAppContext ();
        bb.setServer (server);
        bb.setContextPath (SOMEPATH);
        bb.setWar ("src/test/webapp");

        server.setHandler (bb);

        new Thread (() -> {
            try {
                out.println (">>> STARTING EMBEDDED JETTY SERVER SparkFilter jUnit tests");
                server.start ();
                server.join ();
                out.println (">>> STOPPING EMBEDDED JETTY SERVER");
            }
            catch (Exception e) {
                e.printStackTrace ();
                exit (100);
            }
        }).start ();

        SparkTestUtil.waitForStartup ("localhost", PORT);
    }

    @Test public void getHi () {
        SparkTestUtil.UrlResponse response = testUtil.doMethod ("GET", SOMEPATH + "/hi");
        assertEquals (200, response.status);
        assertEquals ("Hello World!", response.body);
    }

    @Test public void hiHead () {
        SparkTestUtil.UrlResponse response = testUtil.doMethod ("HEAD", SOMEPATH + "/hi");
        assertEquals (200, response.status);
        assertEquals ("", response.body);
    }

    @Test public void getHiAfterFilter () {
        SparkTestUtil.UrlResponse response = testUtil.doMethod ("GET", SOMEPATH + "/hi");
        assertTrue (response.headers.get ("after").contains ("foobar"));
    }

    @Test public void getRoot () {
        SparkTestUtil.UrlResponse response = testUtil.doMethod ("GET", SOMEPATH + "/");
        assertEquals (200, response.status);
        assertEquals ("Hello Root!", response.body);
    }

    @Test public void echoParam1 () {
        SparkTestUtil.UrlResponse response = testUtil.doMethod ("GET", SOMEPATH + "/shizzy");
        assertEquals (200, response.status);
        assertEquals ("echo: shizzy", response.body);
    }

    @Test public void echoParam2 () {
        SparkTestUtil.UrlResponse response = testUtil.doMethod ("GET", SOMEPATH + "/gunit");
        assertEquals (200, response.status);
        assertEquals ("echo: gunit", response.body);
    }

    @Test public void unauthorized () throws Exception {
        SparkTestUtil.UrlResponse urlResponse = testUtil.doMethod ("GET", SOMEPATH + "/protected/resource");
        assertTrue (urlResponse.status == 401);
    }

    @Test public void notFound () throws Exception {
        SparkTestUtil.UrlResponse urlResponse = testUtil.doMethod ("GET", SOMEPATH + "/no/resource");
        assertTrue (urlResponse.status == 404);
    }

    @Test public void post () {
        SparkTestUtil.UrlResponse response = testUtil.doMethod ("POST", SOMEPATH + "/poster", "Fo shizzy");
        out.println (response.body);
        assertEquals (201, response.status);
        assertTrue (response.body.contains ("Fo shizzy"));
    }
}