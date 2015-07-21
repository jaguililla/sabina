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

import static java.lang.Integer.parseInt;
import static java.lang.System.getProperty;
import static sabina.content.JsonContent.toJson;
import static sabina.view.MustacheView.renderMustache;

import sabina.Request;
import sabina.Sabina;
import sabina.server.MatcherFilter;

import java.util.*;
import java.util.Date;
import javax.servlet.FilterConfig;
import javax.servlet.annotation.WebFilter;

/**
 * .
 */
@WebFilter ("/*")
public final class Application extends MatcherFilter {
    static final String SETTINGS_RESOURCE = "/server.properties";
    static final Repository REPOSITORY = loadRepository ();
    static final int DB_ROWS = 10000;

    private static final String MESSAGE = "Hello, World!";
    private static final String CONTENT_TYPE_TEXT = "text/plain";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String QUERIES_PARAM = "queries";

    static Properties loadConfiguration () {
        try {
            Properties settings = new Properties ();
            settings.load (Application.class.getResourceAsStream (SETTINGS_RESOURCE));
            return settings;
        }
        catch (Exception ex) {
            throw new RuntimeException (ex);
        }
    }

    static Repository loadRepository () {
        switch (getProperty ("sabina.benchmark.repository", "mysql")) {
            case "mongodb":
                return new MongoDbRepository (loadConfiguration ());
            case "mysql":
            default:
                return new MySqlRepository (loadConfiguration ());
        }
    }

    private static Object getDb (Request it) {
        final World[] worlds = REPOSITORY.getWorlds (getQueries (it), false);
        it.response.type (CONTENT_TYPE_JSON);
        return toJson (it.queryParams (QUERIES_PARAM) == null? worlds[0] : worlds);
    }

    private static Object getFortunes (Request it) {
        List<Fortune> fortunes = REPOSITORY.getFortunes ();
        fortunes.add (new Fortune (0, "Additional fortune added at request time."));
        fortunes.sort ((a, b) -> a.message.compareTo (b.message));

        it.response.type ("text/html; charset=utf-8");
        return renderMustache ("/fortunes.mustache", fortunes);
    }

    private static Object getUpdates (Request it) {
        World[] worlds = REPOSITORY.getWorlds (getQueries (it), true);
        it.response.type (CONTENT_TYPE_JSON);
        return toJson (it.queryParams (QUERIES_PARAM) == null? worlds[0] : worlds);
    }

    private static int getQueries (final Request request) {
        try {
            String parameter = request.queryParams (QUERIES_PARAM);
            if (parameter == null)
                return 1;

            int queries = parseInt (parameter);
            if (queries < 1)
                return 1;
            if (queries > 500)
                return 500;

            return queries;
        }
        catch (NumberFormatException ex) {
            return 1;
        }
    }

    private static Object getPlaintext (Request it) {
        it.response.type (CONTENT_TYPE_TEXT);
        return MESSAGE;
    }

    private static Object getJson (Request it) {
        it.response.type (CONTENT_TYPE_JSON);
        return toJson (new Message ());
    }

    private static void addCommonHeaders (Request it) {
        it.header ("Server", "Undertow/1.1.2");
        it.response.addDateHeader ("Date", new Date ().getTime ());
    }

    public static void main (String[] args) {
        Sabina.get ("/json", Application::getJson);
        Sabina.get ("/db", Application::getDb);
        Sabina.get ("/query", Application::getDb);
        Sabina.get ("/fortune", Application::getFortunes);
        Sabina.get ("/update", Application::getUpdates);
        Sabina.get ("/plaintext", Application::getPlaintext);
        Sabina.after (Application::addCommonHeaders);

        Properties settings = loadConfiguration ();
        Sabina.host (settings.getProperty ("web.host"));
        Sabina.port (settings.getProperty ("web.port"));
        Sabina.start ();
    }

    @Override public void init (FilterConfig filterConfig) {
        get ("/json", Application::getJson);
        get ("/db", Application::getDb);
        get ("/query", Application::getDb);
        get ("/fortune", Application::getFortunes);
        get ("/update", Application::getUpdates);
        get ("/plaintext", Application::getPlaintext);
        after (Application::addCommonHeaders);
    }
}
