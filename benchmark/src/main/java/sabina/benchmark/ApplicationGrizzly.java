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
import static java.lang.System.exit;
import static java.lang.System.getProperty;
import static java.lang.System.in;
import static org.glassfish.grizzly.http.server.HttpServer.createSimpleServer;
import static sabina.benchmark.Application.loadConfiguration;
import static sabina.content.JsonContent.toJson;
import static sabina.view.MustacheView.renderMustache;

import java.io.IOException;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.function.BiConsumer;

import javax.servlet.*;
import javax.servlet.http.*;

import io.undertow.servlet.handlers.FilterHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.*;

/**
 * .
 */
final class ApplicationGrizzly {
    static final Repository REPOSITORY = loadRepository ();

    private static final String MESSAGE = "Hello, World!";
    private static final String CONTENT_TYPE_TEXT = "text/plain";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String QUERIES_PARAM = "queries";

    static Repository loadRepository () {
        switch (getProperty ("sabina.benchmark.repository", "mysql")) {
            case "mongodb":
                return new MongoDbRepository (loadConfiguration ());
            case "mysql":
            default:
                return new MySqlRepository (loadConfiguration ());
        }
    }

    private static void getDb (HttpServletRequest req, HttpServletResponse res) {
        final World[] worlds = REPOSITORY.getWorlds (getQueries (req), false);
        res.setContentType (CONTENT_TYPE_JSON);
        try {
            res.getWriter ().write (
                toJson (req.getParameter (QUERIES_PARAM) == null? worlds[0] : worlds)
            );
        }
        catch (IOException e) {
            e.printStackTrace ();
        }
    }

    private static void getFortunes (HttpServletRequest req, HttpServletResponse res) {
        List<Fortune> fortunes = REPOSITORY.getFortunes ();
        fortunes.add (new Fortune (0, "Additional fortune added at request time."));
        fortunes.sort ((a, b) -> a.message.compareTo (b.message));

        res.setContentType ("text/html; charset=utf-8");
        try {
            res.getWriter ().write (renderMustache ("/fortunes.mustache", fortunes));
        }
        catch (IOException e) {
            e.printStackTrace ();
        }
    }

    private static void getUpdates (HttpServletRequest req, HttpServletResponse res) {
        World[] worlds = REPOSITORY.getWorlds (getQueries (req), true);
        res.setContentType (CONTENT_TYPE_JSON);

        try {
            res.getWriter ().write (
                toJson (req.getParameter (QUERIES_PARAM) == null? worlds[0] : worlds)
            );
        }
        catch (IOException e) {
            e.printStackTrace ();
        }
    }

    private static int getQueries (HttpServletRequest req) {
        try {
            String parameter = req.getParameter (QUERIES_PARAM);
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

    private static void getPlaintext (HttpServletRequest req, HttpServletResponse res) {
        res.setContentType (CONTENT_TYPE_TEXT);
        try {
            res.getWriter ().write (MESSAGE);
        }
        catch (IOException e) {
            e.printStackTrace ();
        }
    }

    private static void getJson (HttpServletRequest req, HttpServletResponse res) {
        res.setContentType (CONTENT_TYPE_JSON);
        try {
            res.getWriter ().write (toJson (new Message ()));
        }
        catch (IOException e) {
            e.printStackTrace ();
        }
    }

    private static void addCommonHeaders (ServletRequest req, ServletResponse res) {
        HttpServletResponseWrapper resw = new HttpServletResponseWrapper ((HttpServletResponse)res);
        resw.addHeader ("Server", "Undertow/1.1.2");
        resw.addDateHeader ("Date", new Date ().getTime ());
    }

    public static void main (String[] args) {
        WebappContext context = new WebappContext ("grizzly web context");

        get (context, "/json", ApplicationGrizzly::getJson);
        get (context, "/db", ApplicationGrizzly::getDb);
        get (context, "/query", ApplicationGrizzly::getDb);
        get (context, "/fortune", ApplicationGrizzly::getFortunes);
        get (context, "/update", ApplicationGrizzly::getUpdates);
        get (context, "/plaintext", ApplicationGrizzly::getPlaintext);
        after (context, ApplicationGrizzly::addCommonHeaders);

        HttpServer server = createSimpleServer (null, 5050);
        context.deploy (server);
        try {
            server.start ();

//            while (true) {
//                if (in.read () == 10) {
//                    server.shutdown ();
//                    exit (0);
//                }
//            }
        }
        catch (Exception e) {
            e.printStackTrace ();
            exit (1);
        }
    }

    @FunctionalInterface
    interface Handler extends BiConsumer<HttpServletRequest, HttpServletResponse> {}

    @FunctionalInterface
    interface FilterHandler extends BiConsumer<ServletRequest, ServletResponse> {}

    private static volatile int id;

    private static void get (WebappContext context, String path, Handler handler) {
        context.addServlet (String.valueOf (id++), new HttpServlet () {
                @Override protected void doGet (HttpServletRequest req, HttpServletResponse resp)
                    throws ServletException, IOException {
                    handler.accept (req, resp);
                }
            }
        )
        .addMapping (path);
    }

    private static void after (WebappContext context, FilterHandler handler) {
        context.addFilter (String.valueOf (id++), new Filter () {
                @Override public void init (FilterConfig filterConfig) throws ServletException {}
                @Override public void destroy () {}

                @Override public void doFilter (
                    ServletRequest request, ServletResponse response, FilterChain chain)
                    throws IOException, ServletException {

                    chain.doFilter (request, response);
                    handler.accept (request, response);
                }
            }
        )
        .addMappingForUrlPatterns (EnumSet.allOf (DispatcherType.class), "/*");
    }
}
