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

import sabina.Route.Handler;
import sabina.Route.VoidHandler;
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
 * <li>A p (/hello, /users/:name)</li>
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
     * Sets the folder in classp serving static files. <b>Observe: this method
     * must be called before all other methods.</b>
     *
     * @param folder the folder in classp.
     */
    public void resourcesLocation (String folder) {
        staticFileFolder = folder.startsWith ("/")? folder.substring (1) : folder;
    }

    /**
     * Sets the external folder serving static files. <b>Observe: this method
     * must be called before all other methods.</b>
     *
     * @param externalFolder the external folder serving static files.
     */
    public void filesLocation (String externalFolder) {
        externalStaticFileFolder = externalFolder;
    }

    public void filesLocation (String folder, String externalFolder) {
        resourcesLocation (folder);
        filesLocation (externalFolder);
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
            throw new IllegalStateException ("Can not reset running server");

        routeMatcher = RouteMatcherFactory.create ();
    }

    Server addRoute (Route action) {
        routeMatcher.processRoute (action);
        return this;
    }

    Server add (HttpMethod method, Handler h) {
        return addRoute (new Route (method, h));
    }

    Server add (HttpMethod method, String p, Handler h) {
        return addRoute (new Route (method, p, h));
    }

    Server add (HttpMethod method, String p, String ct, Handler h) {
        return addRoute (new Route (method, p, ct, h));
    }

    Server add (HttpMethod method, VoidHandler h) {
        return add (method, wrap (h));
    }

    Server add (HttpMethod method, String p, VoidHandler h) {
        return add (method, p, wrap (h));
    }

    Server add (HttpMethod method, String p, String ct, VoidHandler h) {
        return add (method, p, ct, wrap (h));
    }

    /**
     * Maps an exception handler to be executed when an exception occurs during routing.
     *
     * @param exceptionClass the exception class.
     * @param h The handler.
     * @param <T> Exception type.
     * @return The server whith the added exception h.
     */
    public <T extends Exception> Server exception(
        Class<T> exceptionClass, BiConsumer<T, Request> h) {

        Fault<?> wrapper = new Fault<> (exceptionClass, h);
        routeMatcher.processFault (wrapper);
        return this;
    }

    /*
     * Filters
     */
    public Server after (VoidHandler h) { return add (after, h); }
    public Server before (VoidHandler h) { return add (before, h); }
    public Server after (String p, VoidHandler h) { return add (after, p, h); }
    public Server before (String p, VoidHandler h) { return add (before, p, h); }
    public Server after (String p, String ct, VoidHandler h) { return add (after, p, ct, h); }
    public Server before (String p, String ct, VoidHandler h) { return add (before, p, ct, h); }

    /*
     * Routes
     */
    public Server delete (String p, Handler h) { return add (delete, p, h); }
    public Server delete (String p, VoidHandler h) { return delete (p, wrap (h)); }
    public Server get (String p, Handler h) { return add (get, p, h); }
    public Server get (String p, VoidHandler h) { return get (p, wrap (h)); }
    public Server head (String p, Handler h) { return add (head, p, h); }
    public Server head (String p, VoidHandler h) { return head (p, wrap (h)); }
    public Server options (String p, Handler h) { return add (options, p, h); }
    public Server options (String p, VoidHandler h) { return options (p, wrap (h)); }
    public Server patch (String p, Handler h) { return add (patch, p, h); }
    public Server patch (String p, VoidHandler h) { return patch (p, wrap (h)); }
    public Server post (String p, Handler h) { return add (post, p, h); }
    public Server post (String p, VoidHandler h) { return post (p, wrap (h)); }
    public Server put (String p, Handler h) { return add (put, p, h); }
    public Server put (String p, VoidHandler h) { return put (p, wrap (h)); }
    public Server trace (String p, Handler h) { return add (trace, p, h); }
    public Server trace (String p, VoidHandler h) { return trace (p, wrap (h)); }
    public Server delete (String p, String ct, Handler h) { return add (delete, p, ct, h); }
    public Server delete (String p, String ct, VoidHandler h) { return delete (p, ct, wrap (h)); }
    public Server get (String p, String ct, Handler h) { return add (get, p, ct, h); }
    public Server get (String p, String ct, VoidHandler h) { return get (p, ct, wrap (h)); }
    public Server head (String p, String ct, Handler h) { return add (head, p, ct, h); }
    public Server head (String p, String ct, VoidHandler h) { return head (p, ct, wrap (h)); }
    public Server options (String p, String ct, Handler h) { return add (options, p, ct, h); }
    public Server options (String p, String ct, VoidHandler h) { return options (p, ct, wrap (h)); }
    public Server patch (String p, String ct, Handler h) { return add (patch, p, ct, h); }
    public Server patch (String p, String ct, VoidHandler h) { return patch (p, ct, wrap (h)); }
    public Server post (String p, String ct, Handler h) { return add (post, p, ct, h); }
    public Server post (String p, String ct, VoidHandler h) { return post (p, ct, wrap (h)); }
    public Server put (String p, String ct, Handler h) { return add (put, p, ct, h); }
    public Server put (String p, String ct, VoidHandler h) { return put (p, ct, wrap (h)); }
    public Server trace (String p, String ct, Handler h) { return add (trace, p, ct, h); }
    public Server trace (String p, String ct, VoidHandler h) { return trace (p, ct, wrap (h)); }

    private Handler wrap (VoidHandler h) {
        return request -> {
            h.accept (request);
            return "";
        };
    }

    /*
     * TODO Use to next two functions to ease usage (lambdas in a form: (req, res) -> "")
     */
    private Handler wrap (BiFunction<Request, Response, Object> h) {
        return request -> h.apply (request, request.response);
    }

    private Handler wrap (BiConsumer<Request, Response> h) {
        return request -> {
            h.accept (request, request.response);
            return ""; // TODO Returning null is like not found (404)
        };
    }
}
