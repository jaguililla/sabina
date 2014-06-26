package spark;

import static java.lang.System.exit;
import static java.lang.System.out;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static spark.util.SparkTestUtil.sleep;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.util.SparkTestUtil;
import spark.util.SparkTestUtil.UrlResponse;

public class ServletTest {

    private static final String SOMEPATH = "/somepath";
    private static final int PORT = 9393;
    private static final Server server = new Server ();

    private static SparkTestUtil testUtil;

    @AfterClass public static void tearDown () throws Exception {
        server.stop ();
        sleep (500);
    }

    @BeforeClass public static void setup () {
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

        sleep (500);
    }

    @Test public void testGetHi () {
        UrlResponse response = testUtil.doMethod ("GET", SOMEPATH + "/hi", null);
        assertEquals (200, response.status);
        assertEquals ("Hello World!", response.body);
    }

    @Test public void testHiHead () {
        UrlResponse response = testUtil.doMethod ("HEAD", SOMEPATH + "/hi", null);
        assertEquals (200, response.status);
        assertEquals ("", response.body);
    }

    @Test public void testGetHiAfterFilter () {
        UrlResponse response = testUtil.doMethod ("GET", SOMEPATH + "/hi", null);
        assertTrue (response.headers.get ("after").contains ("foobar"));
    }

    @Test public void testGetRoot () {
        UrlResponse response = testUtil.doMethod ("GET", SOMEPATH + "/", null);
        assertEquals (200, response.status);
        assertEquals ("Hello Root!", response.body);
    }

    @Test public void testEchoParam1 () {
        UrlResponse response = testUtil.doMethod ("GET", SOMEPATH + "/shizzy", null);
        assertEquals (200, response.status);
        assertEquals ("echo: shizzy", response.body);
    }

    @Test public void testEchoParam2 () {
        UrlResponse response = testUtil.doMethod ("GET", SOMEPATH + "/gunit", null);
        assertEquals (200, response.status);
        assertEquals ("echo: gunit", response.body);
    }

    @Test public void testUnauthorized () throws Exception {
        UrlResponse urlResponse =
            testUtil.doMethod ("GET", SOMEPATH + "/protected/resource", null);
        assertTrue (urlResponse.status == 401);
    }

    @Test public void testNotFound () throws Exception {
        UrlResponse urlResponse = testUtil.doMethod ("GET", SOMEPATH + "/no/resource", null);
        assertTrue (urlResponse.status == 404);
    }

    @Test public void testPost () {
        UrlResponse response = testUtil.doMethod ("POST", SOMEPATH + "/poster", "Fo shizzy");
        out.println (response.body);
        assertEquals (201, response.status);
        assertTrue (response.body.contains ("Fo shizzy"));
    }
}
