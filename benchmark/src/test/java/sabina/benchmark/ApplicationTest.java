/*
 * Copyright © 2015 Juan José Aguililla. All rights reserved.
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

package sabina.benchmark;

import static org.apache.http.client.fluent.Request.Get;
import static org.testng.AssertJUnit.*;
import static sabina.Sabina.stop;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * <p>TODO
 * Write article about stress test with TestNG (scenarios, combine different tests in scenarios,
 * adding random pauses...)
 *
 * <p>TODO Change assert's order
 */
@Test public final class ApplicationTest {
    private static final int THREADS = 16, EXECUTIONS = 75, WARM_UP = 10;

    private static final String ENDPOINT = "http://localhost:5050";
    private static final Gson GSON = new Gson ();

    @BeforeClass public void setup () throws IOException {
        Application.main (null);

        for (int ii = 0; ii < WARM_UP; ii++) {
            json ();
            plaintext ();
            no_query_parameter ();
            empty_query_parameter ();
            text_query_parameter ();
            zero_queries ();
            one_thousand_queries ();
            one_query ();
            ten_queries ();
            one_hundred_queries ();
            five_hundred_queries ();
            fortunes ();
            no_updates_parameter ();
            empty_updates_parameter ();
            text_updates_parameter ();
            zero_updates ();
            one_thousand_updates ();
            one_update ();
            ten_updates ();
            one_hundred_updates ();
            five_hundred_updates ();
        }
    }

    @AfterClass public static void close () {
        stop ();
    }

    public void json () throws IOException {
        HttpResponse response = get (ENDPOINT + "/json");
        String content = getContent (response);

        checkResponse (response, content, "application/json");
        assertEquals ("Hello, World!", GSON.fromJson (content, Map.class).get ("message"));
    }

    public void plaintext () throws IOException {
        HttpResponse response = get (ENDPOINT + "/plaintext");
        String content = getContent (response);

        checkResponse (response, content, "text/plain");
        assertEquals ("Hello, World!", content);
    }

    public void no_query_parameter () throws IOException {
        HttpResponse response = get (ENDPOINT + "/db");
        String content = getContent (response);

        checkResponse (response, content, "application/json");
        Map<?, ?> resultsMap = GSON.fromJson (content, Map.class);
        assertTrue (resultsMap.containsKey ("id") && resultsMap.containsKey ("randomNumber"));
    }

    public void empty_query_parameter () throws IOException {
        checkDbRequest ("/query?queries", 1);
    }

    public void text_query_parameter () throws IOException {
        checkDbRequest ("/query?queries=text", 1);
    }

    public void zero_queries () throws IOException {
        checkDbRequest ("/query?queries=0", 1);
    }

    public void one_thousand_queries () throws IOException {
        checkDbRequest ("/query?queries=1000", 500);
    }

    public void one_query () throws IOException {
        checkDbRequest ("/query?queries=1", 1);
    }

    public void ten_queries () throws IOException {
        checkDbRequest ("/query?queries=10", 10);
    }

    public void one_hundred_queries () throws IOException {
        checkDbRequest ("/query?queries=100", 100);
    }

    public void five_hundred_queries () throws IOException {
        checkDbRequest ("/query?queries=500", 500);
    }

    public void fortunes () throws IOException {
        HttpResponse response = get (ENDPOINT + "/fortune");
        String content = getContent (response);
        String contentType = response.getEntity ().getContentType ().getValue ();

        assertTrue (response.getFirstHeader ("Server") != null);
        assertTrue (response.getFirstHeader ("Date") != null);
        assertTrue (content.contains ("&lt;script&gt;alert(&quot;This should not be displayed"));
        assertTrue (content.contains ("フレームワークのベンチマーク"));
        assertEquals ("text/html; charset=utf-8", contentType.toLowerCase ());
    }

    public void no_updates_parameter () throws IOException {
        HttpResponse response = get (ENDPOINT + "/update");
        String content = getContent (response);

        checkResponse (response, content, "application/json");
        Map<?, ?> resultsMap = GSON.fromJson (content, Map.class);
        assertTrue (resultsMap.containsKey ("id") && resultsMap.containsKey ("randomNumber"));
    }

    public void empty_updates_parameter () throws IOException {
        checkDbRequest ("/update?queries", 1);
    }

    public void text_updates_parameter () throws IOException {
        checkDbRequest ("/update?queries=text", 1);
    }

    public void zero_updates () throws IOException {
        checkDbRequest ("/update?queries=0", 1);
    }

    public void one_thousand_updates () throws IOException {
        checkDbRequest ("/update?queries=1000", 500);
    }

    public void one_update () throws IOException {
        checkDbRequest ("/update?queries=1", 1);
    }

    public void ten_updates () throws IOException {
        checkDbRequest ("/update?queries=10", 10);
    }

    public void one_hundred_updates () throws IOException {
        checkDbRequest ("/update?queries=100", 100);
    }

    public void five_hundred_updates () throws IOException {
        checkDbRequest ("/update?queries=500", 500);
    }

    @Test(threadPoolSize = THREADS, invocationCount = EXECUTIONS)
    public void stress_json () throws IOException {
        json ();
    }

    @Test(threadPoolSize = THREADS, invocationCount = EXECUTIONS)
    public void stress_plaintext () throws IOException {
        plaintext ();
    }

    @Test(threadPoolSize = THREADS, invocationCount = EXECUTIONS)
    public void stress_no_query_parameter () throws IOException {
        no_query_parameter ();
    }

    @Test(threadPoolSize = THREADS, invocationCount = EXECUTIONS)
    public void stress_empty_query_parameter () throws IOException {
        empty_query_parameter ();
    }

    @Test(threadPoolSize = THREADS, invocationCount = EXECUTIONS)
    public void stress_text_query_parameter () throws IOException {
        text_query_parameter ();
    }

    @Test(threadPoolSize = THREADS, invocationCount = EXECUTIONS)
    public void stress_zero_queries () throws IOException {
        zero_queries ();
    }

    @Test(threadPoolSize = THREADS, invocationCount = EXECUTIONS)
    public void stress_one_thousand_queries () throws IOException {
        one_thousand_queries ();
    }

    @Test(threadPoolSize = THREADS, invocationCount = EXECUTIONS)
    public void stress_one_query () throws IOException {
        one_query ();
    }

    @Test(threadPoolSize = THREADS, invocationCount = EXECUTIONS)
    public void stress_ten_queries () throws IOException {
        ten_queries ();
    }

    @Test(threadPoolSize = THREADS, invocationCount = EXECUTIONS)
    public void stress_one_hundred_queries () throws IOException {
        one_hundred_queries ();
    }

    @Test(threadPoolSize = THREADS, invocationCount = EXECUTIONS)
    public void stress_five_hundred_queries () throws IOException {
        five_hundred_queries ();
    }

    @Test(threadPoolSize = THREADS, invocationCount = EXECUTIONS)
    public void stress_fortunes () throws IOException {
        fortunes ();
    }

    @Test(threadPoolSize = THREADS, invocationCount = EXECUTIONS)
    public void stress_no_updates_parameter () throws IOException {
        no_updates_parameter ();
    }

    @Test(threadPoolSize = THREADS, invocationCount = EXECUTIONS)
    public void stress_empty_updates_parameter () throws IOException {
        empty_updates_parameter ();
    }

    @Test(threadPoolSize = THREADS, invocationCount = EXECUTIONS)
    public void stress_text_updates_parameter () throws IOException {
        text_updates_parameter ();
    }

    @Test(threadPoolSize = THREADS, invocationCount = EXECUTIONS)
    public void stress_zero_updates () throws IOException {
        zero_updates ();
    }

    @Test(threadPoolSize = THREADS, invocationCount = EXECUTIONS)
    public void stress_one_thousand_updates () throws IOException {
        one_thousand_updates ();
    }

    @Test(threadPoolSize = THREADS, invocationCount = EXECUTIONS)
    public void stress_one_update () throws IOException {
        one_update ();
    }

    @Test(threadPoolSize = THREADS, invocationCount = EXECUTIONS)
    public void stress_ten_updates () throws IOException {
        ten_updates ();
    }

    @Test(threadPoolSize = THREADS, invocationCount = EXECUTIONS)
    public void stress_one_hundred_updates () throws IOException {
        one_hundred_updates ();
    }

    @Test(threadPoolSize = THREADS, invocationCount = EXECUTIONS)
    public void stress_five_hundred_updates () throws IOException {
        five_hundred_updates ();
    }

    private void checkDbRequest (String path, int itemsCount) throws IOException {
        HttpResponse response = get (ENDPOINT + path);
        String content = getContent (response);

        checkResponse (response, content, "application/json");
        checkResultItems (content, itemsCount);
    }

    private HttpResponse get (String uri) throws IOException {
        return Get (uri).execute ().returnResponse ();
    }

    private String getContent (HttpResponse response) throws IOException {
        InputStream in = response.getEntity ().getContent ();
        return new Scanner (in).useDelimiter ("\\A").next ();
    }

    private void checkResponse (HttpResponse res, String content, String contentType) {
        assertTrue (res.getFirstHeader ("Server") != null);
        assertTrue (res.getFirstHeader ("Date") != null);
        assertEquals (content.length (), res.getEntity ().getContentLength ());
        assertTrue (res.getEntity ().getContentType ().getValue ().contains (contentType));
    }

    private void checkResultItems (String result, int size) {
        List<?> resultsList = GSON.fromJson (result, List.class);
        assertEquals (size, resultsList.size ());

        for (int ii = 0; ii < size; ii++) {
            Map<?, ?> r = (Map)resultsList.get (ii);
            assertTrue (r.containsKey ("id") && r.containsKey ("randomNumber"));
        }
    }
}
