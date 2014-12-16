/*
 * Copyright © 2014 Juan José Aguililla. All rights reserved.
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

package sabina;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Arrays.asList;
import static java.util.logging.Logger.getLogger;
import static sabina.HttpMethod.after;
import static sabina.HttpMethod.before;
import static sabina.HttpMethod.*;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;

import sabina.route.RouteMatcher;
import sabina.route.RouteMatcherFactory;
import sabina.webserver.SparkServer;
import sabina.webserver.SparkServerFactory;

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
public final class Server {
    private static final Logger LOG = getLogger (Server.class.getName ());

    public static final int DEFAULT_PORT = 4567;
    public static final String DEFAULT_IP = "0.0.0.0";

    /*
     * Server
     */
    public static Server server (int port, Action... aHandler) {
        return new Server (port, aHandler);
    }

    public static Server server (Action... aHandler) {
        return new Server (aHandler);
    }

    public static Server server (String [] aArgs, Action... aHandler) {
        // TODO Parse args to parameters
        return server (aHandler);
    }

    public static void serve (Action... aHandler) {
        server (aHandler).startUp ();
    }

    public static void serve (String [] aArgs, Action... aHandler) {
        server (aArgs, aHandler).startUp ();
    }

    /*
     * Filters
     */
    public static Filter after (Filter.Handler handler) {
        return new Filter (after, handler);
    }

    public static Filter before (Filter.Handler handler) {
        return new Filter (before, handler);
    }

    public static Filter after (String path, Filter.Handler handler) {
        return new Filter (after, path, handler);
    }

    public static Filter before (String path, Filter.Handler handler) {
        return new Filter (before, path, handler);
    }

    public static Filter after (String path, String contentType, Filter.Handler handler) {
        return new Filter (after, path, contentType, handler);
    }

    public static Filter before (String path, String contentType, Filter.Handler handler) {
        return new Filter (before, path, contentType, handler);
    }

    /*
     * Routes
     */
    public static Route connect (String path, Route.Handler handler) {
        return new Route (connect, path, handler);
    }

    public static Route delete (String path, Route.Handler handler) {
        return new Route (delete, path, handler);
    }

    public static Route get (String path, Route.Handler handler) {
        return new Route (get, path, handler);
    }

    public static Route head (String path, Route.Handler handler) {
        return new Route (head, path, handler);
    }

    public static Route options (String path, Route.Handler handler) {
        return new Route (options, path, handler);
    }

    public static Route patch (String path, Route.Handler handler) {
        return new Route (patch, path, handler);
    }

    public static Route post (String path, Route.Handler handler) {
        return new Route (post, path, handler);
    }

    public static Route put (String path, Route.Handler handler) {
        return new Route (put, path, handler);
    }

    public static Route trace (String path, Route.Handler handler) {
        return new Route (trace, path, handler);
    }

    public static Route connect (String path, String contentType, Route.Handler handler) {
        return new Route (connect, path, contentType, handler);
    }

    public static Route delete (String path, String contentType, Route.Handler handler) {
        return new Route (delete, path, contentType, handler);
    }

    public static Route get (String path, String contentType, Route.Handler handler) {
        return new Route (get, path, contentType, handler);
    }

    public static Route head (String path, String contentType, Route.Handler handler) {
        return new Route (head, path, contentType, handler);
    }

    public static Route options (String path, String contentType, Route.Handler handler) {
        return new Route (options, path, contentType, handler);
    }

    public static Route patch (String path, String contentType, Route.Handler handler) {
        return new Route (patch, path, contentType, handler);
    }

    public static Route post (String path, String contentType, Route.Handler handler) {
        return new Route (post, path, contentType, handler);
    }

    public static Route put (String path, String contentType, Route.Handler handler) {
        return new Route (put, path, contentType, handler);
    }

    public static Route trace (String path, String contentType, Route.Handler handler) {
        return new Route (trace, path, contentType, handler);
    }

    private int port = DEFAULT_PORT;
    private String ipAddress = DEFAULT_IP;

    private String keystoreFile;
    private String keystorePassword;
    private String truststoreFile;
    private String truststorePassword;

    private String staticFileFolder;
    private String externalStaticFileFolder;

    private SparkServer server;

    /** TODO Only supports one matcher! */
    private final RouteMatcher routeMatcher = RouteMatcherFactory.get ();
    /** Holds a map of Exception classes and associated handlers. */
    private final Map<Class<? extends Exception>, Fault> exceptionMap = new HashMap<> ();

    public Server (int port, Action... nodes) {
        this (nodes);
        setPort (port);
    }

    public Server (Action... nodes) {
        checkArgument (nodes != null);
        assert nodes != null;
        asList (nodes).forEach (this::addRoute);
    }

    /**
     * Set the IP address that Spark should listen on. If not called the default
     * address is '0.0.0.0'. This has to be called before any route mapping is done.
     *
     * @param ipAddress The ipAddress.
     */
    public synchronized void setIpAddress (String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * Set the port that Spark should listen on. If not called the default port
     * is 4567. This has to be called before any route mapping is done.
     * If provided port = 0 then the an arbitrary available port will be used.
     *
     * @param port The port number
     */
    public synchronized void setPort (int port) {
        this.port = port;
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
    public synchronized void setSecure (
        String keystoreFile, String keystorePassword,
        String truststoreFile, String truststorePassword) {

        if (keystoreFile == null)
            throw new IllegalArgumentException ("Must provide a keystore to run secured");

        this.keystoreFile = keystoreFile;
        this.keystorePassword = keystorePassword;
        this.truststoreFile = truststoreFile;
        this.truststorePassword = truststorePassword;
    }

    /**
     * Sets the folder in classpath serving static files. <b>Observe: this method
     * must be called before all other methods.</b>
     *
     * @param folder the folder in classpath.
     */
    public synchronized void staticFileLocation (String folder) {
        staticFileFolder = folder.startsWith ("/")? folder.substring (1) : folder;
    }

    /**
     * Sets the external folder serving static files. <b>Observe: this method
     * must be called before all other methods.</b>
     *
     * @param externalFolder the external folder serving static files.
     */
    public synchronized void externalStaticFileLocation (String externalFolder) {
        externalStaticFileFolder = externalFolder;
    }

    public synchronized void startUp () {
        new Thread (() -> {
            server = SparkServerFactory.create (hasMultipleHandlers ());
            server.startUp (
                ipAddress,
                port,
                keystoreFile,
                keystorePassword,
                truststoreFile,
                truststorePassword,
                staticFileFolder,
                externalStaticFileFolder);
        }).start ();
    }

    private boolean hasMultipleHandlers () {
        return staticFileFolder != null || externalStaticFileFolder != null;
    }

    /**
     * Stops the Spark server and clears all routes
     */
    public synchronized void stop () {
        if (server != null)
            server.shutDown ();
    }

    protected synchronized void addRoute (Action action) {
//        LOG.fine (">>> " + action);
        System.out.println (">>> " + action);

        routeMatcher.parseValidateAddRoute (
            action.method + " '" + action.path + "'", action.acceptType, action);
    }

    /**
     * Maps an exception handler to be executed when an exception occurs during routing
     *
     * @param exceptionClass the exception class
     * @param aHandler        The handler
     */
    public synchronized <T extends Exception> void exception(
        Class<T> exceptionClass, BiConsumer<T, Exchange> aHandler) {

        Fault wrapper = new Fault<> (exceptionClass, aHandler);
        map (exceptionClass, wrapper);
    }

    /**
     * Maps the given handler to the provided exception type. If a handler was already registered to the same type, the
     * handler is overwritten.
     *
     * @param exceptionClass Type of exception
     * @param handler        Handler to map to exception
     */
    public void map(Class<? extends Exception> exceptionClass, Fault handler) {
        exceptionMap.put(exceptionClass, handler);
    }

    /**
     * Returns the handler associated with the provided exception class
     *
     * @param exceptionClass Type of exception
     * @return Associated handler
     */
    public Fault getHandler(Class<? extends Exception> exceptionClass) {
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
    public Fault getHandler(Exception exception) {
        return getHandler (exception.getClass ());
    }
}
