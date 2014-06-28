package spark;

import static spark.Spark.post;
import static spark.Spark.stop;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.util.SparkTestUtil;

/**
 * System tests for the Cookies support.
 *
 * @author dreambrother
 */
public class CookiesIntegrationTest {

    private static SparkTestUtil testUtil = new SparkTestUtil (4567);

    @BeforeClass public static void initRoutes () throws InterruptedException {

        post ("/assertNoCookies", it -> {
            if (!it.cookies ().isEmpty ()) {
                it.halt (500);
            }
            return "";
        });

        post ("/setCookie", it -> {
            it.cookie (it.queryParams ("cookieName"), it.queryParams ("cookieValue"));
            return "";
        });

        post ("/assertHasCookie", it -> {
            String cookieValue = it.cookie (it.queryParams ("cookieName"));
            if (!it.queryParams ("cookieValue").equals (cookieValue))
                it.halt (500);
            return "";
        });

        post ("/removeCookie", it -> {
            String cookieName = it.queryParams ("cookieName");
            String cookieValue = it.cookie (cookieName);
            if (!it.queryParams ("cookieValue").equals (cookieValue))
                it.halt (500);
            it.removeCookie (cookieName);
            return "";
        });

        testUtil.waitForStartup ();
    }

    @AfterClass public static void stopServer () {
        stop ();
        testUtil.waitForShutdown ();
    }

    @Test public void emptyCookies () {
        httpPost ("/assertNoCookies");
    }

    @Test public void createCookie () {
        String cookieName = "testCookie";
        String cookieValue = "testCookieValue";
        String cookie = cookieName + "&cookieValue=" + cookieValue;
        httpPost ("/setCookie?cookieName=" + cookie);
        httpPost ("/assertHasCookie?cookieName=" + cookie);
    }

    @Test public void removeCookie () {
        String cookieName = "testCookie";
        String cookieValue = "testCookieValue";
        String cookie = cookieName + "&cookieValue=" + cookieValue;
        httpPost ("/setCookie?cookieName=" + cookie);
        httpPost ("/removeCookie?cookieName=" + cookie);
        httpPost ("/assertNoCookies");
    }

    private void httpPost (String aPath) {
        testUtil.doMethod ("POST", aPath, "");
    }
}
