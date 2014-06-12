package spark;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.stop;

import java.io.FileNotFoundException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.examples.Books;
import spark.util.SparkTestUtil;
import spark.util.SparkTestUtil.UrlResponse;

public class BooksIntegrationTest {
    private static final int PORT = 4567;
    private static final String AUTHOR = "FOO", TITLE = "BAR", NEW_TITLE = "SPARK";

    private static SparkTestUtil testUtil;

    private static String id = "1";

    private static UrlResponse doMethod (String requestMethod, String path)
        throws FileNotFoundException {

        return testUtil.doMethod (requestMethod, path);
    }

    @AfterClass public static void tearDown () throws InterruptedException {
        stop ();
        sleep (100);
    }

    @BeforeClass public static void setup () throws InterruptedException {
        testUtil = new SparkTestUtil (4567);
        before (it -> it.header ("FOZ", "BAZ"));
        Books.books ();
        after (it -> it.header ("FOO", "BAR"));
        sleep (100);
    }

    @Test public void createBook () throws FileNotFoundException {
        UrlResponse res =
            doMethod ("POST", "/books?author=" + AUTHOR + "&title=" + TITLE);
        id = res.body.trim ();

        assertNotNull (res);
        assertNotNull (res.body);
        assertTrue (Integer.valueOf (res.body) > 0);
        assertEquals (201, res.status);
    }

    @Test public void listBooks () throws FileNotFoundException {
        createBook ();
        UrlResponse res = doMethod ("GET", "/books");

        assertNotNull (res);
        assertNotNull (res.body.trim ());
        assertTrue (res.body.trim ().length () > 0);
        assertTrue (res.body.contains (id));
        assertEquals (200, res.status);
    }

    @Test public void getBook () throws FileNotFoundException {
        // ensure there is a book
        createBook ();
        UrlResponse res = doMethod ("GET", "/books/" + id);

        assertNotNull (res);
        assertNotNull (res.body);
        assertTrue (res.body.contains (AUTHOR));
        assertTrue (res.body.contains (TITLE));
        assertEquals (200, res.status);

        // verify response header set by filters:
        assertTrue (res.headers.get ("FOO").equals ("BAR"));
        assertTrue (res.headers.get ("FOZ").equals ("BAZ"));
    }

    @Test public void updateBook () throws FileNotFoundException {
        createBook ();
        UrlResponse res = doMethod ("PUT", "/books/" + id + "?title=" + NEW_TITLE);

        assertNotNull (res);
        assertNotNull (res.body);
        assertTrue (res.body.contains (id));
        assertTrue (res.body.contains ("updated"));
        assertEquals (200, res.status);
    }

    @Test public void getUpdatedBook () throws FileNotFoundException {
        updateBook ();
        UrlResponse res = doMethod ("GET", "/books/" + id);

        assertNotNull (res);
        assertNotNull (res.body);
        assertTrue (res.body.contains (AUTHOR));
        assertTrue (res.body.contains (NEW_TITLE));
        assertEquals (200, res.status);
    }

    @Test public void deleteBook () throws FileNotFoundException {
        UrlResponse res = doMethod ("DELETE", "/books/" + id);

        assertNotNull (res);
        assertNotNull (res.body);
        assertTrue (res.body.contains (id));
        assertTrue (res.body.contains ("deleted"));
        assertEquals (200, res.status);
    }

    @Test public void bookNotFound () throws FileNotFoundException {
        UrlResponse res = doMethod ("GET", "/books/" + 9999);

        assertNotNull (res);
        assertNotNull (res.body);
        assertTrue (res.body.contains ("not found"));
        assertEquals (404, res.status);
    }
}
