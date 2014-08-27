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

package spark;

import static org.testng.Assert.*;
import static spark.Spark.*;

import java.io.FileNotFoundException;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import spark.examples.Books;
import spark.util.SparkTestUtil;
import spark.util.SparkTestUtil.UrlResponse;

public class BooksIntegrationTest {
    private static final int PORT = 4567;
    private static final String AUTHOR = "FOO", TITLE = "BAR", NEW_TITLE = "SPARK";

    private static SparkTestUtil testUtil = new SparkTestUtil (4567);

    private static String id = "1";

    private static UrlResponse doMethod (String requestMethod, String path)
        throws FileNotFoundException {

        return testUtil.doMethod (requestMethod, path);
    }

    @AfterClass public static void shutDown () throws InterruptedException {
//        sleep (5); // Avoid stopping before processing last test
        stop ();
        testUtil.waitForShutdown ();
    }

    @BeforeClass public static void startUp () throws InterruptedException {
        setPort (testUtil.getPort ());

        before (it -> it.header ("FOZ", "BAZ"));
        Books.books ();
        after (it -> it.header ("FOO", "BAR"));

        testUtil.waitForStartup ();
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
