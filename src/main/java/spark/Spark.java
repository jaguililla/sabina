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

import static org.slf4j.LoggerFactory.getLogger;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import org.slf4j.Logger;
import spark.route.HttpMethod;
import spark.route.RouteMatcher;
import spark.route.RouteMatcherFactory;
import spark.webserver.SparkFilter;
import spark.webserver.SparkServer;
import spark.webserver.SparkServerFactory;

/**
 * The main building block of a Spark application is a set of routes. A route is
 * made up of three simple pieces:
 * <p>
 * <ul>
 * <li>A verb (get, post, put, delete, head, trace, connect, options)</li>
 * <li>A path (/hello, /users/:name)</li>
 * <li>A callback ( handle(Request request, Response response) )</li>
 * </ul>
 * <p>
 * Example:
 * <p>
 * <pre>
 * {@code
 * Spark.get(new Route("/hello") {
 *    public Object handle(Request request, Response response) {
 *       return "Hello World!";
 *    }
 * });
 * </pre>
 * <p>
 * <code>
 * <p>
 * </code>
 *
 * @author Per Wendel
 */
public class Spark {
    private static class HandlerRoute extends Route {
        final Function<RouteContext, Object> mHandler;

        protected HandlerRoute (String path, Function<RouteContext, Object> aHandler) {
            super (path, DEFAULT_ACCEPT_TYPE);
            mHandler = aHandler;
        }

        protected HandlerRoute (
            String path, String acceptType, Function<RouteContext, Object> aHandler) {

            super (path, acceptType);
            mHandler = aHandler;
        }

        @Override public Object handle (Request request, Response response) {
            return mHandler.apply (new RouteContext (this, request, response));
        }
    }

    private static class HandlerFilter extends Filter {
        final Consumer<FilterContext> mHandler;

        protected HandlerFilter (Consumer<FilterContext> aHandler) {
            super ();
            mHandler = aHandler;
        }

        protected HandlerFilter (String path, Consumer<FilterContext> aHandler) {
            super (path, DEFAUT_CONTENT_TYPE);
            mHandler = aHandler;
        }

        protected HandlerFilter (
            String path, String acceptType, Consumer<FilterContext> aHandler) {

            super (path, acceptType);
            mHandler = aHandler;
        }

        @Override public void handle (Request request, Response response) {
            mHandler.accept (new FilterContext (this, request, response));
        }
    }

    private static class HandlerException<T extends Exception>
        extends ExceptionHandler<T> {

        final BiConsumer<T, FilterContext> mHandler;

        protected HandlerException (
            Class<T> aException, BiConsumer<T, FilterContext> aHandler) {
            super (aException);
            mHandler = aHandler;
        }

        @Override public void handle (
            T exception, Request request, Response response) {

            mHandler.accept (exception, new FilterContext (null, request, response));

        }
    }

    private static final Logger LOG = getLogger (Spark.class);
    private static final int SPARK_DEFAULT_PORT = 4567;
    private static int port = SPARK_DEFAULT_PORT;
    private static boolean initialized = false;
    private static String ipAddress = "0.0.0.0";

    private static String keystoreFile;
    private static String keystorePassword;
    private static String truststoreFile;
    private static String truststorePassword;

    private static String staticFileFolder = null;
    private static String externalStaticFileFolder = null;

    private static SparkServer server;
    private static RouteMatcher routeMatcher;

    private static boolean runFromServlet;
    private static boolean servletStaticLocationSet;
    private static boolean servletExternalStaticLocationSet;

    /**
     * Set the IP address that Spark should listen on. If not called the default
     * address is '0.0.0.0'. This has to be called before any route mapping is
     * done.
     *
     * @param ipAddress The ipAddress
     */
    public static synchronized void setIpAddress (String ipAddress) {
        if (initialized) {
            throwBeforeRouteMappingException ();
        }
        Spark.ipAddress = ipAddress;
    }

    private static void throwBeforeRouteMappingException () {
        throw new IllegalStateException (
            "This must be done before route mapping has begun");
    }

    /**
     * Set the port that Spark should listen on. If not called the default port
     * is 4567. This has to be called before any route mapping is done.
     * If provided port = 0 then the an arbitrary available port will be used.
     *
     * @param port The port number
     */
    public static synchronized void setPort (int port) {
        if (initialized) {
            throwBeforeRouteMappingException ();
        }
        Spark.port = port;
    }

    /**
     * Set the connection to be secure, using the specified keystore and
     * truststore. This has to be called before any route mapping is done. You
     * have to supply a keystore file, truststore file is optional (keystore
     * will be reused).
     * <p>
     * This method is only relevant when using embedded Jetty servers. It should
     * not be used if you are using Servlets, where you will need to secure the
     * connection in the servlet container
     *
     * @param keystoreFile The keystore file location as string
     * @param keystorePassword the password for the keystore
     * @param truststoreFile the truststore file location as string, leave null to reuse
     * keystore
     * @param truststorePassword the trust store password
     */
    public static synchronized void setSecure (
        String keystoreFile,
        String keystorePassword, String truststoreFile,
        String truststorePassword) {
        if (initialized) {
            throwBeforeRouteMappingException ();
        }

        if (keystoreFile == null) {
            throw new IllegalArgumentException (
                "Must provide a keystore file to run secured");
        }

        Spark.keystoreFile = keystoreFile;
        Spark.keystorePassword = keystorePassword;
        Spark.truststoreFile = truststoreFile;
        Spark.truststorePassword = truststorePassword;
    }

    /**
     * Sets the folder in classpath serving static files. <b>Observe: this method
     * must be called before all other methods.</b>
     *
     * @param folder the folder in classpath.
     */
    public static synchronized void staticFileLocation (String folder) {
        if (initialized && !runFromServlet) {
            throwBeforeRouteMappingException ();
        }
        staticFileFolder = folder.startsWith ("/")? folder.substring (1) : folder;
        if (!servletStaticLocationSet) {
            if (runFromServlet) {
                SparkFilter.configureStaticResources (staticFileFolder);
                servletStaticLocationSet = true;
            }
        }
        else {
            LOG.warn ("Static file location has already been set");
        }
    }

    /**
     * Sets the external folder serving static files. <b>Observe: this method
     * must be called before all other methods.</b>
     *
     * @param externalFolder the external folder serving static files.
     */
    public static synchronized void externalStaticFileLocation (String externalFolder) {
        if (initialized && !runFromServlet) {
            throwBeforeRouteMappingException ();
        }
        externalStaticFileFolder = externalFolder;
        if (!servletExternalStaticLocationSet) {
            if (runFromServlet) {
                SparkFilter.configureExternalStaticResources (externalStaticFileFolder);
                servletExternalStaticLocationSet = true;
            }
        }
        else {
            LOG.warn ("External static file location has already been set");
        }
    }

    /**
     * Map the route for HTTP GET requests
     *
     * @param route The route
     */
    public static synchronized void get (Route route) {
        addRoute (HttpMethod.get.name (), route);
    }

    protected static void addRoute (String httpMethod, Route route) {
        init ();
        routeMatcher.parseValidateAddRoute (httpMethod + " '" + route.getPath ()
            + "'", route.getAcceptType (), route);
    }

    private static synchronized void init () {
        if (!initialized) {
            routeMatcher = RouteMatcherFactory.get ();
            new Thread (() -> {
                server = SparkServerFactory.create (hasMultipleHandlers ());
                server.ignite (
                    ipAddress,
                    port,
                    keystoreFile,
                    keystorePassword,
                    truststoreFile,
                    truststorePassword,
                    staticFileFolder,
                    externalStaticFileFolder);
            }).start ();
            initialized = true;
        }
    }

    private static boolean hasMultipleHandlers () {
        return staticFileFolder != null || externalStaticFileFolder != null;
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param route The route
     */
    public static synchronized void post (Route route) {
        addRoute (HttpMethod.post.name (), route);
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param route The route
     */
    public static synchronized void put (Route route) {
        addRoute (HttpMethod.put.name (), route);
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param route The route
     */
    public static synchronized void patch (Route route) {
        addRoute (HttpMethod.patch.name (), route);
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param route The route
     */
    public static synchronized void delete (Route route) {
        addRoute (HttpMethod.delete.name (), route);
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param route The route
     */
    public static synchronized void head (Route route) {
        addRoute (HttpMethod.head.name (), route);
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param route The route
     */
    public static synchronized void trace (Route route) {
        addRoute (HttpMethod.trace.name (), route);
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param route The route
     */
    public static synchronized void connect (Route route) {
        addRoute (HttpMethod.connect.name (), route);
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param route The route
     */
    public static synchronized void options (Route route) {
        addRoute (HttpMethod.options.name (), route);
    }

    /**
     * Maps a filter to be executed before any matching routes
     *
     * @param filter The filter
     */
    public static synchronized void before (Filter filter) {
        addFilter (HttpMethod.before.name (), filter);
    }

    protected static void addFilter (String httpMethod, Filter filter) {
        init ();
        routeMatcher.parseValidateAddRoute (httpMethod + " '" + filter.getPath ()
            + "'", filter.getAcceptType (), filter);
    }

    /**
     * Maps a filter to be executed after any matching routes
     *
     * @param filter The filter
     */
    public static synchronized void after (Filter filter) {
        addFilter (HttpMethod.after.name (), filter);
    }

    public static synchronized void get (
        String aPath, Function<RouteContext, Object> aHandler) {
        addRoute (HttpMethod.get.name (), new HandlerRoute (aPath, aHandler));
    }

    public static synchronized void get (
        String aPath, String aAcceptType, Function<RouteContext, Object> aHandler) {

        addRoute (HttpMethod.get.name (), new HandlerRoute (aPath, aAcceptType, aHandler));
    }

    public static synchronized void post (
        String aPath, Function<RouteContext, Object> aHandler) {
        addRoute (HttpMethod.post.name (), new HandlerRoute (aPath, aHandler));
    }

    public static synchronized void put (
        String aPath, Function<RouteContext, Object> aHandler) {
        addRoute (HttpMethod.put.name (), new HandlerRoute (aPath, aHandler));
    }

    public static synchronized void patch (
        String aPath, Function<RouteContext, Object> aHandler) {
        addRoute (HttpMethod.patch.name (), new HandlerRoute (aPath, aHandler));
    }

    public static synchronized void delete (
        String aPath, Function<RouteContext, Object> aHandler) {
        addRoute (HttpMethod.delete.name (), new HandlerRoute (aPath, aHandler));
    }

    public static synchronized void head (
        String aPath, Function<RouteContext, Object> aHandler) {
        addRoute (HttpMethod.head.name (), new HandlerRoute (aPath, aHandler));
    }

    public static synchronized void trace (
        String aPath, Function<RouteContext, Object> aHandler) {
        addRoute (HttpMethod.trace.name (), new HandlerRoute (aPath, aHandler));
    }

    public static synchronized void connect (
        String aPath, Function<RouteContext, Object> aHandler) {
        addRoute (HttpMethod.connect.name (), new HandlerRoute (aPath, aHandler));
    }

    public static synchronized void options (
        String aPath, Function<RouteContext, Object> aHandler) {
        addRoute (HttpMethod.options.name (), new HandlerRoute (aPath, aHandler));
    }

    public static synchronized void before (Consumer<FilterContext> aHandler) {
        addFilter (HttpMethod.before.name (), new HandlerFilter (aHandler));
    }

    public static synchronized void before (String aPath, Consumer<FilterContext> aHandler) {
        addFilter (HttpMethod.before.name (), new HandlerFilter (aPath, aHandler));
    }

    public static synchronized void before (
        String aPath, String aAcceptType, Consumer<FilterContext> aHandler) {

        addFilter (HttpMethod.before.name (),
            new HandlerFilter (aPath, aAcceptType, aHandler));
    }

    public static synchronized void after (Consumer<FilterContext> aHandler) {
        addFilter (HttpMethod.after.name (), new HandlerFilter (aHandler));
    }

    public static synchronized void after (String aPath, Consumer<FilterContext> aHandler) {
        addFilter (HttpMethod.after.name (), new HandlerFilter (aPath, aHandler));
    }

    public static synchronized void after (
        String aPath, String aAcceptType, Consumer<FilterContext> aHandler) {

        addFilter (HttpMethod.after.name (), new HandlerFilter (aPath, aAcceptType, aHandler));
    }

    /**
     * Stops the Spark server and clears all routes
     */
    public static synchronized void stop () {
        if (server != null) {
            routeMatcher.clearRoutes ();
            server.stop ();
        }
        initialized = false;
    }

    public static synchronized void runFromServlet () {
        runFromServlet = true;
        if (!initialized) {
            routeMatcher = RouteMatcherFactory.get ();
            initialized = true;
        }
    }

    /**
     * Maps an exception handler to be executed when an exception occurs during routing
     *
     * @param exceptionClass the exception class
     * @param handler        The handler
     */
    public static synchronized <T extends Exception> void exception(
        Class<T> exceptionClass, BiConsumer<T, FilterContext> aHandler) {

        // wrap
        ExceptionHandler wrapper = new ExceptionHandler<T> (exceptionClass) {
            @Override public void handle(T exception, Request request, Response response) {
                // TODO Change FilterContext (null...) this WILL fail calling 'halt'
                aHandler.accept (exception, new FilterContext (null, request, response));
            }
        };

        ExceptionMapper.getInstance ().map(exceptionClass, wrapper);
    }

    // Hide constructor
    protected Spark () {
        throw new IllegalStateException ();
    }
}
