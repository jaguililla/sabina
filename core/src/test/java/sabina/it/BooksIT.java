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

import static org.testng.Assert.*;
import static sabina.Server.*;
import static sabina.util.TestUtil.UrlResponse;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import sabina.Server;
import sabina.util.TestUtil;

public class BooksIT {
    private static final String AUTHOR = "FOO", TITLE = "BAR", NEW_TITLE = "SPARK";

    private static TestUtil testUtil = new TestUtil ();

    private static Server server;

    private static String sid = "1";

    private static int id = 1;

    /**
     * Map holding the books
     */
    private static Map<String, Book> books = new HashMap<> ();

    private static UrlResponse doMethod (String requestMethod, String path)
        throws FileNotFoundException {

        return testUtil.doMethod (requestMethod, path);
    }

    @AfterClass public static void shutDown () throws InterruptedException {
        server.stop ();
        testUtil.waitForShutdown ();
    }

    @BeforeClass public static void startUp () throws InterruptedException {
        server = server (
            before (it -> it.header ("FOZ", "BAZ")),

            post ("/books", it1 -> {
                String author = it1.queryParams ("author");
                String title = it1.queryParams ("title");
                Book book = new Book (author, title);

                books.put (String.valueOf (id), book);

                it1.status (201); // 201 Created
                return id++;
            }),

            // Gets the book resource for the provided id
            get ("/books/:id", it1 -> {
                Book book = books.get (it1.params (":id"));
                if (book != null) {
                    return "Title: " + book.getTitle () + ", Author: " + book.getAuthor ();
                }
                else {
                    it1.status (404); // 404 Not found
                    return "Book not found";
                }
            }),

            // Updates the book resource for the provided id with new information
            // author and title are sent as query parameters e.g. /books/<id>?author=Foo&title=Bar
            put ("/books/:id", it1 -> {
                String id1 = it1.params (":id");
                Book book = books.get (id1);
                if (book != null) {
                    String newAuthor = it1.queryParams ("author");
                    String newTitle = it1.queryParams ("title");
                    if (newAuthor != null)
                        book.setAuthor (newAuthor);

                    if (newTitle != null)
                        book.setTitle (newTitle);

                    return "Book with id '" + id1 + "' updated";
                }
                else {
                    it1.status (404); // 404 Not found
                    return "Book not found";
                }
            }),

            // Deletes the book resource for the provided id
            delete ("/books/:id", it1 -> {
                String id1 = it1.params (":id");
                Book book = books.remove (id1);
                if (book != null) {
                    return "Book with id '" + id1 + "' deleted";
                }
                else {
                    it1.status (404); // 404 Not found
                    return "Book not found";
                }
            }),

            // Gets all available book resources (id's)
            get ("/books", it1 -> {
                String ids = "";

                for (String id1 : books.keySet ())
                    ids += id1 + " ";

                return ids;
            }),

            after (it -> it.header ("FOO", "BAR"))
        );

        server.setPort (testUtil.getPort ());
        server.startUp ();

        testUtil.waitForStartup ();
    }

    @Test public void createBook () throws FileNotFoundException {
        UrlResponse res =
            doMethod ("POST", "/books?author=" + AUTHOR + "&title=" + TITLE);
        sid = res.body.trim ();

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
        assertTrue (res.body.contains (sid));
        assertEquals (200, res.status);
    }

    @Test public void getBook () throws FileNotFoundException {
        // ensure there is a book
        createBook ();
        UrlResponse res = doMethod ("GET", "/books/" + sid);

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
        UrlResponse res = doMethod ("PUT", "/books/" + sid + "?title=" + NEW_TITLE);

        assertNotNull (res);
        assertNotNull (res.body);
        assertTrue (res.body.contains (sid));
        assertTrue (res.body.contains ("updated"));
        assertEquals (200, res.status);
    }

    @Test public void getUpdatedBook () throws FileNotFoundException {
        updateBook ();
        UrlResponse res = doMethod ("GET", "/books/" + sid);

        assertNotNull (res);
        assertNotNull (res.body);
        assertTrue (res.body.contains (AUTHOR));
        assertTrue (res.body.contains (NEW_TITLE));
        assertEquals (200, res.status);
    }

    @Test public void deleteBook () throws FileNotFoundException {
        UrlResponse res = doMethod ("DELETE", "/books/" + sid);

        assertNotNull (res);
        assertNotNull (res.body);
        assertTrue (res.body.contains (sid));
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
