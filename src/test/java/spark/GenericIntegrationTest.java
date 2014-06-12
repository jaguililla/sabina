package spark;

import static java.lang.System.out;
import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static spark.Spark.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.util.SparkTestUtil;
import spark.util.SparkTestUtil.UrlResponse;

public class GenericIntegrationTest {

    private static SparkTestUtil testUtil;
    private static File tmpExternalFile;

    @AfterClass public static void tearDown () {
        Spark.stop ();
        if (tmpExternalFile != null)
            tmpExternalFile.delete ();
    }

    @BeforeClass public static void setup () throws IOException, InterruptedException {
        testUtil = new SparkTestUtil (4567);
        tmpExternalFile =
            new File (System.getProperty ("java.io.tmpdir"), "externalFile.html");

        FileWriter writer = new FileWriter (tmpExternalFile);
        writer.write ("Content of external file");
        writer.flush ();
        writer.close ();

        staticFileLocation ("/public");
        externalStaticFileLocation (System.getProperty ("java.io.tmpdir"));

        before ("/protected/*", it -> it.halt (401, "Go Away!"));

        before ("/protected/*", "application/json", it ->
                it.halt (401, "{\"message\": \"Go Away!\"}")
        );

        get ("/hi", "application/json", it -> "{\"message\": \"Hello World\"}");

        get ("/hi", it -> "Hello World!");

        get ("/param/:param", it -> "echo: " + it.params (":param"));

        get ("/paramandwild/:param/stuff/*", it ->
                "paramandwild: " + it.params (":param") + it.splat ()[0]
        );

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

        sleep (100);
    }

    @Test public void filters_should_be_accept_type_aware () throws Exception {
        UrlResponse response =
            testUtil.doMethod ("GET", "/protected/resource", null, "application/json");
        assertTrue (response.status == 401);
        assertEquals ("{\"message\": \"Go Away!\"}", response.body);
    }

    @Test public void routes_should_be_accept_type_aware () throws Exception {
        UrlResponse response = testUtil.doMethod ("GET", "/hi", null, "application/json");
        assertEquals (200, response.status);
        assertEquals ("{\"message\": \"Hello World\"}", response.body);
    }

    @Test public void testGetHi () {
        UrlResponse response = testUtil.doMethod ("GET", "/hi", null);
        assertEquals (200, response.status);
        assertEquals ("Hello World!", response.body);
    }

    @Test public void testHiHead () {
        UrlResponse response = testUtil.doMethod ("HEAD", "/hi", null);
        assertEquals (200, response.status);
        assertEquals ("", response.body);
    }

    @Test public void testGetHiAfterFilter () {
        UrlResponse response = testUtil.doMethod ("GET", "/hi", null);
        assertTrue (response.headers.get ("after").contains ("foobar"));
    }

    @Test public void testGetRoot () {
        UrlResponse response = testUtil.doMethod ("GET", "/", null);
        assertEquals (200, response.status);
        assertEquals ("Hello Root!", response.body);
    }

    @Test public void testParamAndWild () {
        UrlResponse response =
            testUtil.doMethod ("GET", "/paramandwild/thedude/stuff/andits", null);
        assertEquals (200, response.status);
        assertEquals ("paramandwild: thedudeandits", response.body);
    }

    @Test public void testEchoParam1 () {
        UrlResponse response = testUtil.doMethod ("GET", "/param/shizzy", null);
        assertEquals (200, response.status);
        assertEquals ("echo: shizzy", response.body);
    }

    @Test public void testEchoParam2 () {
        UrlResponse response = testUtil.doMethod ("GET", "/param/gunit", null);
        assertEquals (200, response.status);
        assertEquals ("echo: gunit", response.body);
    }

    @Test public void testEchoParamWithUpperCaseInValue () {
        final String camelCased = "ThisIsAValueAndSparkShouldRetainItsUpperCasedCharacters";
        UrlResponse response = testUtil.doMethod ("GET", "/param/" + camelCased, null);
        assertEquals (200, response.status);
        assertEquals ("echo: " + camelCased, response.body);
    }

    @Test public void testTwoRoutesWithDifferentCaseButSameName () {
        String lowerCasedRoutePart = "param";
        String uppperCasedRoutePart = "PARAM";

        registerEchoRoute (lowerCasedRoutePart);
        registerEchoRoute (uppperCasedRoutePart);
        assertEchoRoute (lowerCasedRoutePart);
        assertEchoRoute (uppperCasedRoutePart);
    }

    private static void registerEchoRoute (final String routePart) {
        get (new Route ("/tworoutes/" + routePart + "/:param") {
            @Override public Object handle (Request request, Response response) {
                return routePart + " route: " + request.params (":param");
            }
        });
    }

    private static void assertEchoRoute (String routePart) {
        final String expected = "expected";
        UrlResponse response =
            testUtil.doMethod ("GET", "/tworoutes/" + routePart + "/" + expected, null);
        assertEquals (200, response.status);
        assertEquals (routePart + " route: " + expected, response.body);
    }

    @Test public void testEchoParamWithMaj () {
        UrlResponse response = testUtil.doMethod ("GET", "/paramwithmaj/plop", null);
        assertEquals (200, response.status);
        assertEquals ("echo: plop", response.body);
    }

    @Test public void testUnauthorized () throws Exception {
        UrlResponse response = testUtil.doMethod ("GET", "/protected/resource", null);
        assertTrue (response.status == 401);
    }

    @Test public void testNotFound () throws Exception {
        UrlResponse response = testUtil.doMethod ("GET", "/no/resource", null);
        assertTrue (response.status == 404);
    }

    @Test public void testPost () {
        UrlResponse response = testUtil.doMethod ("POST", "/poster", "Fo shizzy");
        out.println (response.body);
        assertEquals (201, response.status);
        assertTrue (response.body.contains ("Fo shizzy"));
    }

    @Test public void testPatch () {
        UrlResponse response = testUtil.doMethod ("PATCH", "/patcher", "Fo shizzy");
        out.println (response.body);
        assertEquals (200, response.status);
        assertTrue (response.body.contains ("Fo shizzy"));
    }

    @Test public void testStaticFile () throws Exception {
        UrlResponse response = testUtil.doMethod ("GET", "/css/style.css", null);
        assertEquals (200, response.status);
        assertEquals ("/*\n * Content of css file\n */\n", response.body);
    }

    @Test public void testExternalStaticFile () throws Exception {
        UrlResponse response = testUtil.doMethod ("GET", "/externalFile.html", null);
        assertEquals (200, response.status);
        assertEquals ("Content of external file", response.body);
    }
}
