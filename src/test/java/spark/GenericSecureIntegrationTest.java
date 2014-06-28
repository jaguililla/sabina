package spark;

import static java.lang.System.out;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static spark.Spark.*;
import static spark.util.SparkTestUtil.getKeyStoreLocation;
import static spark.util.SparkTestUtil.getKeystorePassword;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.util.SparkTestUtil;
import spark.util.SparkTestUtil.UrlResponse;

public class GenericSecureIntegrationTest {

    private static SparkTestUtil testUtil = new SparkTestUtil (4567);

    @AfterClass public static void tearDown () {
        stop ();
        testUtil.waitForShutdown ();
    }

    @BeforeClass public static void setup () throws InterruptedException {

        // note that the keystore stuff is retrieved from SparkTestUtil which
        // respects JVM params for keystore, password
        // but offers a default included store if not.
        Spark.setSecure (getKeyStoreLocation (), getKeystorePassword (), null, null);

        before ("/protected/*", it -> it.halt (401, "Go Away!"));

        get ("/hi", it -> "Hello World!");

        get ("/:param", it -> "echo: " + it.params (":param"));

        get ("/paramwithmaj/:paramWithMaj", it -> "echo: " + it.params (":paramWithMaj"));

        get ("/", it -> "Hello Root!");

        post ("/poster", it -> {
            String body = it.requestBody ();
            it.status (201); // created
            return "Body was: " + body;
        });

        patch ("/patcher", it -> {
            String body = it.requestBody ();
            it.status (200);
            return "Body was: " + body;
        });

        after ("/hi", it -> it.header ("after", "foobar"));

        testUtil.waitForStartup ();
    }

    @Test public void testGetHi () {
        UrlResponse response = testUtil.doMethodSecure ("GET", "/hi");
        assertEquals (200, response.status);
        assertEquals ("Hello World!", response.body);
    }

    @Test public void testHiHead () {
        UrlResponse response = testUtil.doMethodSecure ("HEAD", "/hi");
        assertEquals (200, response.status);
        assertEquals ("", response.body);
    }

    @Test public void testGetHiAfterFilter () {
        UrlResponse response = testUtil.doMethodSecure ("GET", "/hi");
        assertTrue (response.headers.get ("after").contains ("foobar"));
    }

    @Test public void testGetRoot () {
        UrlResponse response = testUtil.doMethodSecure ("GET", "/");
        assertEquals (200, response.status);
        assertEquals ("Hello Root!", response.body);
    }

    @Test public void testEchoParam1 () {
        UrlResponse response = testUtil.doMethodSecure ("GET", "/shizzy");
        assertEquals (200, response.status);
        assertEquals ("echo: shizzy", response.body);
    }

    @Test public void testEchoParam2 () {
        UrlResponse response = testUtil.doMethodSecure ("GET", "/gunit");
        assertEquals (200, response.status);
        assertEquals ("echo: gunit", response.body);
    }

    @Test public void testEchoParamWithMaj () {
        UrlResponse response = testUtil.doMethodSecure ("GET", "/paramwithmaj/plop");
        assertEquals (200, response.status);
        assertEquals ("echo: plop", response.body);
    }

    @Test public void testUnauthorized () throws Exception {
        UrlResponse urlResponse = testUtil.doMethodSecure ("GET", "/protected/resource");
        assertTrue (urlResponse.status == 401);
    }

    @Test public void testNotFound () throws Exception {
        UrlResponse urlResponse = testUtil.doMethodSecure ("GET", "/no/resource");
        assertTrue (urlResponse.status == 404);
    }

    @Test public void testPost () {
        UrlResponse response = testUtil.doMethodSecure ("POST", "/poster", "Fo shizzy");
        out.println (response.body);
        assertEquals (201, response.status);
        assertTrue (response.body.contains ("Fo shizzy"));
    }

    @Test public void testPatch () {
        UrlResponse response = testUtil.doMethodSecure ("PATCH", "/patcher", "Fo shizzy");
        out.println (response.body);
        assertEquals (200, response.status);
        assertTrue (response.body.contains ("Fo shizzy"));
    }
}
