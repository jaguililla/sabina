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
import static spark.HttpMethod.*;
import static spark.servlet.SparkFilter.configureExternalStaticResources;
import static spark.servlet.SparkFilter.configureStaticResources;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import org.slf4j.Logger;
import spark.route.RouteMatcher;
import spark.route.RouteMatcherFactory;
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
 * Spark.get(new Route("/hello") {
 *    public Object handle(Request request, Response response) {
 *       return "Hello World!";
 *    }
 * });
 * </pre>
 * <p>
 *
 * @author Per Wendel
 */
public class Spark {
    private static final Logger LOG = getLogger (Spark.class);
    private static final int SPARK_DEFAULT_PORT = 4567;
    private static final String INIT_ERROR =
        "This must be done before route mapping has begun";

    private static int port = SPARK_DEFAULT_PORT;
    private static boolean initialized = false;
    private static String ipAddress = "0.0.0.0";

    private static String keystoreFile;
    private static String keystorePassword;
    private static String truststoreFile;
    private static String truststorePassword;

    private static String staticFileFolder;
    private static String externalStaticFileFolder;

    private static SparkServer server;
    private static RouteMatcher routeMatcher;

    private static boolean runFromServlet;
    private static boolean servletStaticLocationSet;
    private static boolean servletExternalStaticLocationSet;

    /** Holds a map of Exception classes and associated handlers. */
    private static Map<Class<? extends Exception>, Fault> exceptionMap = new HashMap<>();

    /**
     * Set the IP address that Spark should listen on. If not called the default
     * address is '0.0.0.0'. This has to be called before any route mapping is done.
     *
     * @param ipAddress The ipAddress
     */
    public static synchronized void setIpAddress (String ipAddress) {
        if (initialized)
            throw new IllegalStateException (INIT_ERROR);

        Spark.ipAddress = ipAddress;
    }

    /**
     * Set the port that Spark should listen on. If not called the default port
     * is 4567. This has to be called before any route mapping is done.
     * If provided port = 0 then the an arbitrary available port will be used.
     *
     * @param port The port number
     */
    public static synchronized void setPort (int port) {
        if (initialized)
            throw new IllegalStateException (INIT_ERROR);

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
        String keystoreFile, String keystorePassword,
        String truststoreFile, String truststorePassword) {

        if (initialized)
            throw new IllegalStateException (INIT_ERROR);

        if (keystoreFile == null)
            throw new IllegalArgumentException ("Must provide a keystore to run secured");

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
        if (initialized && !runFromServlet)
            throw new IllegalStateException (INIT_ERROR);

        staticFileFolder = folder.startsWith ("/")? folder.substring (1) : folder;
        if (!servletStaticLocationSet) {
            if (runFromServlet) {
                configureStaticResources (staticFileFolder);
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
        if (initialized && !runFromServlet)
            throw new IllegalStateException (INIT_ERROR);

        externalStaticFileFolder = externalFolder;
        if (!servletExternalStaticLocationSet) {
            if (runFromServlet) {
                configureExternalStaticResources (externalStaticFileFolder);
                servletExternalStaticLocationSet = true;
            }
        }
        else {
            LOG.warn ("External static file location has already been set");
        }
    }

    protected static void addRoute (Action route) {
        init ();
        routeMatcher.parseValidateAddRoute (
            route.method + " '" + route.path + "'", route.acceptType, route);
    }

    private static synchronized void init () {
        if (!initialized) {
            routeMatcher = RouteMatcherFactory.get ();
            new Thread (() -> {
                server = SparkServerFactory.create (hasMultipleHandlers ());
                server.startUp (
                    ipAddress, port,
                    keystoreFile, keystorePassword,
                    truststoreFile, truststorePassword,
                    staticFileFolder, externalStaticFileFolder);
            }).start ();
            initialized = true;
        }
    }

    private static boolean hasMultipleHandlers () {
        return staticFileFolder != null || externalStaticFileFolder != null;
    }

    /**
     * Map the route for HTTP GET requests
     *
     * @param aPath The route
     */
    public static synchronized void get (String aPath, Function<Context, Object> aHandler) {
        addRoute (new Route (get, aPath, aHandler));
    }

    public static synchronized void get (
        String aPath, String aAcceptType, Function<Context, Object> aHandler) {

        addRoute (new Route (get, aPath, aAcceptType, aHandler));
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param aPath The route
     */
    public static synchronized void post (String aPath, Function<Context, Object> aHandler) {
        addRoute (new Route (post, aPath, aHandler));
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param aPath The route
     */
    public static synchronized void put (String aPath, Function<Context, Object> aHandler) {
        addRoute (new Route (put, aPath, aHandler));
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param aPath The route
     */
    public static synchronized void patch (String aPath, Function<Context, Object> aHandler) {
        addRoute (new Route (patch, aPath, aHandler));
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param aPath The route
     */
    public static synchronized void delete (String aPath, Function<Context, Object> aHandler) {
        addRoute (new Route (delete, aPath, aHandler));
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param aPath The route
     */
    public static synchronized void head (String aPath, Function<Context, Object> aHandler) {
        addRoute (new Route (head, aPath, aHandler));
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param aPath The route
     */
    public static synchronized void trace (String aPath, Function<Context, Object> aHandler) {
        addRoute (new Route (trace, aPath, aHandler));
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param aPath The route
     */
    public static synchronized void connect (
        String aPath, Function<Context, Object> aHandler) {

        addRoute (new Route (connect, aPath, aHandler));
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param aPath The route
     */
    public static synchronized void options (
        String aPath, Function<Context, Object> aHandler) {

        addRoute (new Route (options, aPath, aHandler));
    }

    /**
     * Maps a filter to be executed before any matching routes
     *
     * @param aHandler The filter
     */
    public static synchronized void before (Consumer<Context> aHandler) {
        addRoute (new Filter (before, aHandler));
    }

    public static synchronized void before (String aPath, Consumer<Context> aHandler) {
        addRoute (new Filter (before, aPath, aHandler));
    }

    public static synchronized void before (
        String aPath, String aAcceptType, Consumer<Context> aHandler) {

        addRoute (new Filter (before, aPath, aAcceptType, aHandler));
    }

    /**
     * Maps a filter to be executed after any matching routes
     *
     * @param aHandler The filter
     */
    public static synchronized void after (Consumer<Context> aHandler) {
        addRoute (new Filter (after, aHandler));
    }

    public static synchronized void after (String aPath, Consumer<Context> aHandler) {
        addRoute (new Filter (after, aPath, aHandler));
    }

    public static synchronized void after (
        String aPath, String aAcceptType, Consumer<Context> aHandler) {

        addRoute (new Filter (after, aPath, aAcceptType, aHandler));
    }

    /**
     * Stops the Spark server and clears all routes.
     */
    public static synchronized void stop () {
        if (server != null) {
            routeMatcher.clearRoutes ();
            server.shutDown ();
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
     * @param aHandler        The handler
     */
    public static synchronized <T extends Exception> void exception(
        Class<T> exceptionClass, BiConsumer<T, Context> aHandler) {

        Fault wrapper = new Fault<T> (exceptionClass, aHandler);
        map (exceptionClass, wrapper);
    }

    /**
     * Maps the given handler to the provided exception type. If a handler was already registered to the same type, the
     * handler is overwritten.
     *
     * @param exceptionClass Type of exception
     * @param handler        Handler to map to exception
     */
    public static void map(Class<? extends Exception> exceptionClass, Fault handler) {
        exceptionMap.put(exceptionClass, handler);
    }

    /**
     * Returns the handler associated with the provided exception class
     *
     * @param exceptionClass Type of exception
     * @return Associated handler
     */
    public static Fault getHandler(Class<? extends Exception> exceptionClass) {
        // If the exception map does not contain the provided exception class, it might
        // still be that a superclass of the exception class is.
        if (!exceptionMap.containsKey(exceptionClass)) {

            Class<?> superclass = exceptionClass.getSuperclass();
            do {
                // Is the superclass mapped?
                if (exceptionMap.containsKey(superclass)) {
                    // Use the handler for the mapped superclass, and cache handler
                    // for this exception class
                    Fault handler = exceptionMap.get(superclass);
                    exceptionMap.put(exceptionClass, handler);
                    return handler;
                }

                // Iteratively walk through the exception class's superclasses
                superclass = superclass.getSuperclass();
            } while (superclass != null);

            // No handler found either for the superclasses of the exception class
            // We cache the null value to prevent future
            exceptionMap.put(exceptionClass, null);
            return null;
        }

        // Direct map
        return exceptionMap.get(exceptionClass);
    }

    /**
     * Returns the handler associated with the provided exception class
     *
     * @param exception Exception that occurred
     * @return Associated handler
     */
    public static Fault getHandler(Exception exception) {
        return getHandler(exception.getClass());
    }

    // Hide constructor
    protected Spark () {
        throw new IllegalStateException ();
    }
}
