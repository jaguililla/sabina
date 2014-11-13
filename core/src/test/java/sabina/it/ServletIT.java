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

package sabina.it;

import static java.lang.System.exit;
import static java.lang.System.out;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import sabina.util.TestUtil;

public class ServletIT {

    private static final String SOMEPATH = "/somepath";
    private static final int PORT = 9393;
    private static final Server server = new Server ();

    private static TestUtil testUtil;

    @AfterClass public static void shutDown () throws Exception {
        server.stop ();
        TestUtil.waitForShutdown ("localhost", PORT);
    }

    @BeforeClass public static void startUp () {
        testUtil = new TestUtil (PORT);

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

        TestUtil.waitForStartup ("localhost", PORT);
    }

    @Test public void getHi () {
        TestUtil.UrlResponse response = testUtil.doMethod ("GET", SOMEPATH + "/hi");
        assertEquals (200, response.status);
        assertEquals ("Hello World!", response.body);
    }

    @Test public void hiHead () {
        TestUtil.UrlResponse response = testUtil.doMethod ("HEAD", SOMEPATH + "/hi");
        assertEquals (200, response.status);
        assertEquals ("", response.body);
    }

    @Test public void getHiAfterFilter () {
        TestUtil.UrlResponse response = testUtil.doMethod ("GET", SOMEPATH + "/hi");
        assertTrue (response.headers.get ("after").contains ("foobar"));
    }

    @Test public void getRoot () {
        TestUtil.UrlResponse response = testUtil.doMethod ("GET", SOMEPATH + "/");
        assertEquals (200, response.status);
        assertEquals ("Hello Root!", response.body);
    }

    @Test public void echoParam1 () {
        TestUtil.UrlResponse response = testUtil.doMethod ("GET", SOMEPATH + "/shizzy");
        assertEquals (200, response.status);
        assertEquals ("echo: shizzy", response.body);
    }

    @Test public void echoParam2 () {
        TestUtil.UrlResponse response = testUtil.doMethod ("GET", SOMEPATH + "/gunit");
        assertEquals (200, response.status);
        assertEquals ("echo: gunit", response.body);
    }

    @Test public void unauthorized () throws Exception {
        TestUtil.UrlResponse urlResponse = testUtil.doMethod ("GET", SOMEPATH + "/protected/resource");
        assertTrue (urlResponse.status == 401);
    }

    @Test public void notFound () throws Exception {
        TestUtil.UrlResponse urlResponse = testUtil.doMethod ("GET", SOMEPATH + "/no/resource");
        assertTrue (urlResponse.status == 404);
    }

    @Test public void post () {
        TestUtil.UrlResponse response = testUtil.doMethod ("POST", SOMEPATH + "/poster", "Fo shizzy");
        out.println (response.body);
        assertEquals (201, response.status);
        assertTrue (response.body.contains ("Fo shizzy"));
    }
}
