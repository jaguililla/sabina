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
import static java.util.logging.Logger.getLogger;
import static sabina.HttpMethod.after;
import static sabina.HttpMethod.before;
import static sabina.HttpMethod.*;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

import sabina.route.RouteMatcher;
import sabina.route.RouteMatcherFactory;
import sabina.server.Backend;
import sabina.server.BackendFactory;

/**
 * The main building block of a Sabina application is a set of routes. A route is
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
 *   serve (
 *     get("/hello", it -&gt; "Hello World!")
 *   );
 * </pre>
 * <p>
 *
 * @author Per Wendel
 */
public final class Server {
    private static final Logger LOG = getLogger (Server.class.getName ());

    public static final int DEFAULT_PORT = 4567;
    public static final String DEFAULT_IP = "0.0.0.0";

    public static Server server (int port) {
        return new Server (port);
    }

    public static Server server (String [] aArgs) {
        // TODO Parse args to parameters
        return new Server ();
    }

    private int port = DEFAULT_PORT;
    private String ipAddress = DEFAULT_IP;

    private String keystoreFile;
    private String keystorePassword;
    private String truststoreFile;
    private String truststorePassword;

    private String staticFileFolder;
    private String externalStaticFileFolder;

    private Backend server;

    /** TODO Only supports one matcher! */
    private final RouteMatcher routeMatcher = RouteMatcherFactory.get ();
    /** Holds a map of Exception classes and associated handlers. */
    private final Map<Class<? extends Exception>, Fault> exceptionMap = new HashMap<> ();

    public Server () {
        super ();
    }

    public Server (int port) {
        setPort (port);
    }

    /**
     * Set the IP address that Sabina should listen on. If not called the default
     * address is '0.0.0.0'. This has to be called before any route mapping is done.
     *
     * @param ipAddress The ipAddress.
     */
    public synchronized void setIpAddress (String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * Set the port that Sabina should listen on. If not called the default port
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

    public synchronized void start () {
        new Thread (() -> {
            server = BackendFactory.create (hasMultipleHandlers ());
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
     * Stops the Sabina server and clears all routes
     */
    public synchronized void stop () {
        if (server != null)
            server.shutDown ();
    }

    protected synchronized Server addRoute (Action action) {
//        LOG.fine (">>> " + action);
        System.out.println (">>> " + action);

        routeMatcher.parseValidateAddRoute (
            action.method + " '" + action.path + "'", action.acceptType, action);

        return this;
    }

    /**
     * Maps an exception handler to be executed when an exception occurs during routing
     *
     * @param exceptionClass the exception class
     * @param aHandler        The handler
     */
    public synchronized <T extends Exception> Server exception(
        Class<T> exceptionClass, BiConsumer<T, Request> aHandler) {

        Fault wrapper = new Fault<> (aHandler);
        map (exceptionClass, wrapper);
        return this;
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

    /*
     * Filters
     */
    public Server after (Filter.Handler handler) {
        return addRoute (new Filter (after, handler));
    }

    public Server before (Filter.Handler handler) {
        return addRoute (new Filter (before, handler));
    }

    public Server after (String path, Filter.Handler handler) {
        return addRoute (new Filter (after, path, handler));
    }

    public Server before (String path, Filter.Handler handler) {
        return addRoute (new Filter (before, path, handler));
    }

    public Server after (String path, String contentType, Filter.Handler handler) {
        return addRoute (new Filter (after, path, contentType, handler));
    }

    public Server before (String path, String contentType, Filter.Handler handler) {
        return addRoute (new Filter (before, path, contentType, handler));
    }

    /*
     * Routes
     */
    public Server connect (String path, Route.Handler handler) {
        return addRoute (new Route (connect, path, handler));
    }

    public Server delete (String path, Route.Handler handler) {
        return addRoute (new Route (delete, path, handler));
    }

    public Server get (String path, Route.Handler handler) {
        return addRoute (new Route (get, path, handler));
    }

    public Server head (String path, Route.Handler handler) {
        return addRoute (new Route (head, path, handler));
    }

    public Server options (String path, Route.Handler handler) {
        return addRoute (new Route (options, path, handler));
    }

    public Server patch (String path, Route.Handler handler) {
        return addRoute (new Route (patch, path, handler));
    }

    public Server post (String path, Route.Handler handler) {
        return addRoute (new Route (post, path, handler));
    }

    public Server put (String path, Route.Handler handler) {
        return addRoute (new Route (put, path, handler));
    }

    public Server trace (String path, Route.Handler handler) {
        return addRoute (new Route (trace, path, handler));
    }

    public Server connect (String path, String contentType, Route.Handler handler) {
        return addRoute (new Route (connect, path, contentType, handler));
    }

    public Server delete (String path, String contentType, Route.Handler handler) {
        return addRoute (new Route (delete, path, contentType, handler));
    }

    public Server get (String path, String contentType, Route.Handler handler) {
        return addRoute (new Route (get, path, contentType, handler));
    }

    public Server head (String path, String contentType, Route.Handler handler) {
        return addRoute (new Route (head, path, contentType, handler));
    }

    public Server options (String path, String contentType, Route.Handler handler) {
        return addRoute (new Route (options, path, contentType, handler));
    }

    public Server patch (String path, String contentType, Route.Handler handler) {
        return addRoute (new Route (patch, path, contentType, handler));
    }

    public Server post (String path, String contentType, Route.Handler handler) {
        return addRoute (new Route (post, path, contentType, handler));
    }

    public Server put (String path, String contentType, Route.Handler handler) {
        return addRoute (new Route (put, path, contentType, handler));
    }

    public Server trace (String path, String contentType, Route.Handler handler) {
        return addRoute (new Route (trace, path, contentType, handler));
    }
}
