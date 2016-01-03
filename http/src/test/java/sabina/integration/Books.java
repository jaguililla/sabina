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

import static org.testng.Assert.*;
import static sabina.integration.TestScenario.*;

import java.util.HashMap;
import java.util.Map;

import sabina.Server;

/**
 * Simple CRUD example.
 *
 * @author Per Wendel
 */
final class Books {
    private static int id = 1;

    /**
     * Map holding the books
     */
    private static Map<String, Book> books = new HashMap<> ();

    static {
        books.put ("100", new Book ("Miguel_de_Cervantes", "Don_Quixote"));
        books.put ("101", new Book ("William_Shakespeare", "Hamlet"));
        books.put ("102", new Book ("Homer", "The_Odyssey"));
    }

    static void setup (Server s) {
        s.post ("/books", it -> {
            String author = it.queryParams ("author");
            String title = it.queryParams ("title");
            Book book = new Book (author, title);

            books.put (String.valueOf (id), book);

            it.status (201); // 201 Created
            return id++;
        });

        s.get ("/books/:id", it -> {
            Book book = books.get (it.params (":id"));
            if (book != null) {
                return "Title: " + book.getTitle () + ", Author: " + book.getAuthor ();
            }
            else {
                it.status (404); // 404 Not found
                return "Book not found";
            }
        });

        // Updates the book resource for the provided id with new information
        // author and title are sent as query parameters e.g. /books/<id>?author=Foo&title=Bar
        s.put ("/books/:id", it -> {
            String id1 = it.params (":id");
            Book book = books.get (id1);
            if (book != null) {
                String newAuthor = it.queryParams ("author");
                String newTitle = it.queryParams ("title");
                if (newAuthor != null)
                    book.setAuthor (newAuthor);

                if (newTitle != null)
                    book.setTitle (newTitle);

                return "Book with id '" + id1 + "' updated";
            }
            else {
                it.status (404); // 404 Not found
                return "Book not found";
            }
        });

        s.delete ("/books/:id", it -> {
            String id1 = it.params (":id");
            Book book = books.remove (id1);
            if (book != null) {
                return "Book with id '" + id1 + "' deleted";
            }
            else {
                it.status (404); // 404 Not found
                return "Book not found";
            }
        });

        // Gets all available book resources (id's)
        s.get ("/books", it -> {
            String ids = "";

            for (String id1 : books.keySet ())
                ids += id1 + " ";

            return ids;
        });
    }

    static void createBook (TestScenario testScenario) {
        UrlResponse res = testScenario.doPost ("/books?author=Vladimir_Nabokov&title=Lolita");
        assertTrue (Integer.valueOf (res.body) > 0);
        assertEquals (201, res.status);
    }

    static void listBooks (TestScenario testScenario) {
        UrlResponse res = testScenario.doGet ("/books");
        assertTrue (res.body.contains ("100") && res.body.contains ("101"));
        assertEquals (200, res.status);
    }

    static void getBook (TestScenario testScenario) {
        UrlResponse res = testScenario.doGet ("/books/101");
        assertTrue (res.body.contains ("William_Shakespeare"));
        assertTrue (res.body.contains ("Hamlet"));
        assertEquals (200, res.status);
    }

    static void updateBook (TestScenario testScenario) {
        UrlResponse res = testScenario.doPut ("/books/100?title=Don_Quixote");
        assertTrue (res.body.contains ("100"));
        assertTrue (res.body.contains ("updated"));
        assertEquals (200, res.status);

        res = testScenario.doGet ("/books/100");
        assertTrue (res.body.contains ("Miguel_de_Cervantes"));
        assertTrue (res.body.contains ("Don_Quixote"));
        assertEquals (200, res.status);
    }

    static void deleteBook (TestScenario testScenario) {
        UrlResponse res = testScenario.doDelete ("/books/102");
        assertTrue (res.body.contains ("102"));
        assertTrue (res.body.contains ("deleted"));
        assertEquals (200, res.status);
        books.put ("102", new Book ("Homer", "The_Odyssey")); // Restore book for next tests
    }

    static void bookNotFound (TestScenario testScenario) {
        UrlResponse res = testScenario.doGet ("/books/9999");
        testScenario.assertResponseContains (res, "not found", 404);
    }
}
