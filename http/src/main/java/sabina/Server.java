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

import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static java.lang.System.getProperty;
import static java.util.logging.Logger.getLogger;
import static sabina.HttpMethod.*;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.logging.Logger;

import sabina.route.RouteMatcher;
import sabina.route.RouteMatcherFactory;
import sabina.server.Backend;
import sabina.server.BackendFactory;

/**
 * The main building block of a Sabina application is a set of routes. A route is
 * made up of three simple pieces:
 *
 * <ul>
 * <li>A verb (get, post, put, delete, head, trace, options)</li>
 * <li>A path (/hello, /users/:name)</li>
 * <li>A callback ( handle(Request request, Response response) )</li>
 * </ul>
 *
 * Example:
 *
 * <pre>
 *   get("/hello", it -&gt; "Hello World!");
 * </pre>
 *
 * <p>TODO Register custom 404 pages and so on
 *
 * @author Per Wendel
 */
public final class Server {
    private static final Logger LOG = getLogger (Server.class.getName ());

    private static final int DEFAULT_PORT = 4567;
    private static final String DEFAULT_HOST = "0.0.0.0";

    public static Server server (int port) {
        return new Server (port);
    }

    private int port = DEFAULT_PORT;
    private String ipAddress = DEFAULT_HOST;

    private String keystoreFile;
    private String keystorePassword;
    private String truststoreFile;
    private String truststorePassword;

    private String staticFileFolder;
    private String externalStaticFileFolder;

    private String backend = getProperty ("sabina.backend", "undertow");

    private Backend server;
    private RouteMatcher routeMatcher = RouteMatcherFactory.create ();

    public Server () {
        super ();
    }

    public Server (int port) {
        port (port);
    }

    public Server (String backend, int port) {
        port (port);
        backend (backend);
    }

    /**
     * Set the IP address that Sabina should listen on. If not called the default
     * address is '0.0.0.0'. This has to be called before any route mapping is done.
     *
     * @param ipAddress The ip.
     */
    public void host (String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * Set the port that Sabina should listen on. If not called the default port
     * is 4567. This has to be called before any route mapping is done.
     * If provided port = 0 then the an arbitrary available port will be used.
     *
     * @param port The port number
     */
    public void port (int port) {
        this.port = port;
    }

    public void port (String port) {
        port (parseInt (port));
    }

    public String backend () {
        return backend;
    }

    /**
     * Sets the backend used by this server. After start it would throw an exception.
     *
     * @param backend .
     */
    public void backend (String backend) {
        if (isRunning ())
            throw new IllegalArgumentException ("Can not change the backend of a running server");

        this.backend = backend;
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
    public void secure (
        String keystoreFile, String keystorePassword,
        String truststoreFile, String truststorePassword) {

        if (keystoreFile == null)
            throw new IllegalArgumentException ("Must provide a keystore to run secured");

        this.keystoreFile = keystoreFile;
        this.keystorePassword = keystorePassword;
        this.truststoreFile = truststoreFile;
        this.truststorePassword = truststorePassword;
    }

    public void secure (String keystoreFile, String keystorePassword) {
        secure (keystoreFile, keystorePassword, null, null);
    }

    /**
     * Sets the folder in classpath serving static files. <b>Observe: this method
     * must be called before all other methods.</b>
     *
     * @param folder the folder in classpath.
     */
    public void staticFileLocation (String folder) {
        staticFileFolder = folder.startsWith ("/")? folder.substring (1) : folder;
    }

    /**
     * Sets the external folder serving static files. <b>Observe: this method
     * must be called before all other methods.</b>
     *
     * @param externalFolder the external folder serving static files.
     */
    public void externalStaticFileLocation (String externalFolder) {
        externalStaticFileFolder = externalFolder;
    }

    public void filesLocation (String folder, String externalFolder) {
        staticFileLocation (folder);
        externalStaticFileLocation (externalFolder);
    }

    public void start () {
        new Thread (() -> {
            server = BackendFactory.create (backend, routeMatcher, hasMultipleHandlers ());
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
        LOG.info (format ("Server started at: %s:%s with %s backend", ipAddress, port, backend));
    }

    private boolean hasMultipleHandlers () {
        return staticFileFolder != null || externalStaticFileFolder != null;
    }

    /**
     * Stops the Sabina server and clears all routes
     */
    public void stop () {
        if (server != null)
            server.shutDown ();
        server = null;
    }

    public boolean isRunning () {
        return server != null;
    }

    public void reset () {
        if (isRunning ())
            throw new IllegalArgumentException ("Can not reset running server");

        routeMatcher = RouteMatcherFactory.create ();
    }

    Server addRoute (Action action) {
        routeMatcher.processRoute (
            action.method + " '" + action.path + "'", action.acceptType, action);

        return this;
    }

    /**
     * Maps an exception handler to be executed when an exception occurs during routing.
     *
     * @param exceptionClass the exception class.
     * @param aHandler        The handler.
     * @param <T> Exception type.
     * @return The server whith the added exception handler.
     */
    public <T extends Exception> Server exception(
        Class<T> exceptionClass, BiConsumer<T, Request> aHandler) {

        Fault<?> wrapper = new Fault<> (exceptionClass, aHandler);
        routeMatcher.processFault (wrapper);
        return this;
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
    public Server delete (String path, Route.Handler handler) {
        return addRoute (new Route (delete, path, handler));
    }

    public Server delete (String path, Route.VoidHandler handler) {
        return delete (path, wrap (handler));
    }

    public Server get (String path, Route.Handler handler) {
        return addRoute (new Route (get, path, handler));
    }

    public Server get (String path, Route.VoidHandler handler) {
        return get (path, wrap (handler));
    }

    public Server head (String path, Route.Handler handler) {
        return addRoute (new Route (head, path, handler));
    }

    public Server head (String path, Route.VoidHandler handler) {
        return head (path, wrap (handler));
    }

    public Server options (String path, Route.Handler handler) {
        return addRoute (new Route (options, path, handler));
    }

    public Server options (String path, Route.VoidHandler handler) {
        return options (path, wrap (handler));
    }

    public Server patch (String path, Route.Handler handler) {
        return addRoute (new Route (patch, path, handler));
    }

    public Server patch (String path, Route.VoidHandler handler) {
        return patch (path, wrap (handler));
    }

    public Server post (String path, Route.Handler handler) {
        return addRoute (new Route (post, path, handler));
    }

    public Server post (String path, Route.VoidHandler handler) {
        return post (path, wrap (handler));
    }

    public Server put (String path, Route.Handler handler) {
        return addRoute (new Route (put, path, handler));
    }

    public Server put (String path, Route.VoidHandler handler) {
        return put (path, wrap (handler));
    }

    public Server trace (String path, Route.Handler handler) {
        return addRoute (new Route (trace, path, handler));
    }

    public Server trace (String path, Route.VoidHandler handler) {
        return trace (path, wrap (handler));
    }

    public Server delete (String path, String contentType, Route.Handler handler) {
        return addRoute (new Route (delete, path, contentType, handler));
    }

    public Server delete (String path, String contentType, Route.VoidHandler handler) {
        return delete (path, contentType, wrap (handler));
    }

    public Server get (String path, String contentType, Route.Handler handler) {
        return addRoute (new Route (get, path, contentType, handler));
    }

    public Server get (String path, String contentType, Route.VoidHandler handler) {
        return get (path, contentType, wrap (handler));
    }

    public Server head (String path, String contentType, Route.Handler handler) {
        return addRoute (new Route (head, path, contentType, handler));
    }

    public Server head (String path, String contentType, Route.VoidHandler handler) {
        return head (path, contentType, wrap (handler));
    }

    public Server options (String path, String contentType, Route.Handler handler) {
        return addRoute (new Route (options, path, contentType, handler));
    }

    public Server options (String path, String contentType, Route.VoidHandler handler) {
        return options (path, contentType, wrap (handler));
    }

    public Server patch (String path, String contentType, Route.Handler handler) {
        return addRoute (new Route (patch, path, contentType, handler));
    }

    public Server patch (String path, String contentType, Route.VoidHandler handler) {
        return patch (path, contentType, wrap (handler));
    }

    public Server post (String path, String contentType, Route.Handler handler) {
        return addRoute (new Route (post, path, contentType, handler));
    }

    public Server post (String path, String contentType, Route.VoidHandler handler) {
        return post (path, contentType, wrap (handler));
    }

    public Server put (String path, String contentType, Route.Handler handler) {
        return addRoute (new Route (put, path, contentType, handler));
    }

    public Server put (String path, String contentType, Route.VoidHandler handler) {
        return put (path, contentType, wrap (handler));
    }

    public Server trace (String path, String contentType, Route.Handler handler) {
        return addRoute (new Route (trace, path, contentType, handler));
    }

    public Server trace (String path, String contentType, Route.VoidHandler handler) {
        return trace (path, contentType, wrap (handler));
    }

    private Route.Handler wrap (Route.VoidHandler handler) {
        return request -> {
            handler.accept (request);
            return "";
        };
    }

    /*
     * TODO Use to next two functions to ease usage (lambdas in a form: (req, res) -> "")
     */
    private Route.Handler wrap (BiFunction<Request, Response, Object> handler) {
        return request -> handler.apply (request, request.response);
    }

    private Route.Handler wrap (BiConsumer<Request, Response> handler) {
        return request -> {
            handler.accept (request, request.response);
            return ""; // TODO Returning null is like not found (404)
        };
    }

}
