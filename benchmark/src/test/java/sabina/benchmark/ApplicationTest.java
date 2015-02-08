package sabina.benchmark;

import static org.apache.http.client.fluent.Request.Get;
import static org.testng.AssertJUnit.*;
import static sabina.benchmark.Application.main;
import static sabina.Sabina.stop;
import static sun.misc.IOUtils.readFully;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

@org.testng.annotations.Test (enabled = false)
public final class ApplicationTest {
    private static final String ENDPOINT = "http://localhost:8080";
    private static final Gson GSON = new Gson ();

    @org.testng.annotations.BeforeClass @BeforeClass
    public static void setup () {
        main (null);
    }

    @org.testng.annotations.AfterClass @AfterClass
    public static void close () {
        stop ();
    }

    @Test public void json () throws IOException {
        HttpResponse response = get (ENDPOINT + "/json");
        String content = getContent (response);

        checkResponse (response, content, "application/json");
        assertEquals ("Hello, World!", GSON.fromJson (content, Map.class).get ("message"));
    }

    @Test public void plaintext () throws IOException {
        HttpResponse response = get (ENDPOINT + "/plaintext");
        String content = getContent (response);

        checkResponse (response, content, "text/plain");
        assertEquals ("Hello, World!", content);
    }

    @Test public void no_query_parameter () throws IOException {
        HttpResponse response = get (ENDPOINT + "/db");
        String content = getContent (response);

        checkResponse (response, content, "application/json");
        Map<?, ?> resultsMap = GSON.fromJson (content, Map.class);
        assertTrue (resultsMap.containsKey ("id") && resultsMap.containsKey ("randomNumber"));
    }

    @Test public void empty_query_parameter () throws IOException {
        HttpResponse response = get (ENDPOINT + "/db?queries");
        String content = getContent (response);

        checkResponse (response, content, "application/json");
        checkResultItems (content, 1);
    }

    @Test public void text_query_parameter () throws IOException {
        HttpResponse response = get (ENDPOINT + "/db?queries=text");
        String content = getContent (response);

        checkResponse (response, content, "application/json");
        checkResultItems (content, 1);
    }

    @Test public void zero_queries () throws IOException {
        HttpResponse response = get (ENDPOINT + "/db?queries=0");
        String content = getContent (response);

        checkResponse (response, content, "application/json");
        checkResultItems (content, 1);
    }

    @Test public void one_thousand_queries () throws IOException {
        HttpResponse response = get (ENDPOINT + "/db?queries=1000");
        String content = getContent (response);

        checkResponse (response, content, "application/json");
        checkResultItems (content, 500);
    }

    @Test public void one_query () throws IOException {
        HttpResponse response = get (ENDPOINT + "/db?queries=1");
        String content = getContent (response);

        checkResponse (response, content, "application/json");
        checkResultItems (content, 1);
    }

    @Test public void ten_queries () throws IOException {
        HttpResponse response = get (ENDPOINT + "/db?queries=10");
        String content = getContent (response);

        checkResponse (response, content, "application/json");
        checkResultItems (content, 10);
    }

    @Test public void five_hundred_queries () throws IOException {
        HttpResponse response = get (ENDPOINT + "/db?queries=500");
        String content = getContent (response);

        checkResponse (response, content, "application/json");
        checkResultItems (content, 500);
    }

    @Test public void fortunes () throws IOException {
        HttpResponse response = get (ENDPOINT + "/fortunes");
        String content = getContent (response);
        String contentType = response.getEntity ().getContentType ().getValue ();

        assertTrue (response.getFirstHeader ("Server") != null);
        assertTrue (response.getFirstHeader ("Date") != null);
        assertTrue (content.contains ("&lt;script&gt;alert(&quot;This should not be displayed"));
        assertTrue (content.contains ("フレームワークのベンチマーク"));
        assertEquals ("text/html; charset=utf-8", contentType);
    }

    @Test public void updates () throws IOException {
        // TODO
    }

    private HttpResponse get (String uri) throws IOException {
        return Get (uri).execute ().returnResponse ();
    }

    private String getContent (HttpResponse aResponse) throws IOException {
        InputStream in = aResponse.getEntity ().getContent ();
        // TODO Replace readFully
        return new String (readFully (in, -1, true));
    }

    private void checkResponse (HttpResponse res, String content, String contentType) {
        assertTrue (res.getFirstHeader ("Server") != null);
        assertTrue (res.getFirstHeader ("Date") != null);
        assertEquals (content.length (), res.getEntity ().getContentLength ());
        assertEquals (contentType, res.getEntity ().getContentType ().getValue ());
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
