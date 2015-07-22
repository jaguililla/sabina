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
import static java.lang.System.currentTimeMillis;
import static java.lang.System.getProperty;
import static java.util.Arrays.asList;
import static java.util.logging.Logger.getLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
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
public final class Server implements Router {
    private static final Logger LOG = getLogger (Server.class.getName ());

    private static final int DEFAULT_PORT = 4567;
    private static final String DEFAULT_BIND = "0.0.0.0";

    public static Server server (final int port) {
        return new Server (port);
    }

    private int port = DEFAULT_PORT;
    private String bind = DEFAULT_BIND;

    private String keystoreFile;
    private String keystorePassword;
    private String truststoreFile;
    private String truststorePassword;

    private String resourcesLocation;
    private String filesLocation;
    private List<Consumer<Server>> configurationCallbacks = new ArrayList<> ();

    private String backend = getProperty ("sabina.backend", "undertow");

    private Backend server;
    RouteMatcher routeMatcher = RouteMatcherFactory.create ();

    /** Starts counting since instance creation. */
    private final long start = currentTimeMillis ();

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

    public void configure (Consumer<Server>... callbacks) {
        this.configurationCallbacks = asList (callbacks);
    }

    /**
     * Set the IP address that Sabina should listen on. If not called the default
     * address is '0.0.0.0'. This has to be called before any route mapping is done.
     *
     * @param ipAddress The ip.
     */
    public void host (String ipAddress) {
        this.bind = ipAddress;
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
        resourcesLocation = folder.startsWith ("/")? folder.substring (1) : folder;
    }

    /**
     * Sets the external folder serving static files. <b>Observe: this method
     * must be called before all other methods.</b>
     *
     * @param externalFolder the external folder serving static files.
     */
    public void filesLocation (String externalFolder) {
        filesLocation = externalFolder;
    }

    public void filesLocation (String folder, String externalFolder) {
        resourcesLocation (folder);
        filesLocation (externalFolder);
    }

    public void start () {
        configurationCallbacks.stream ().forEach (c -> c.accept (this));
        new Thread (() -> {
            server = BackendFactory.create (backend, routeMatcher, hasMultipleHandlers ());
            server.startUp (
                bind,
                port,
                keystoreFile,
                keystorePassword,
                truststoreFile,
                truststorePassword,
                resourcesLocation,
                filesLocation);
        }).start ();
        LOG.info (
            format (
                "Server started in %dms at: %s:%s with %s backend",
                currentTimeMillis () - start,
                bind,
                port,
                backend
            )
        );
    }

    private boolean hasMultipleHandlers () {
        return resourcesLocation != null || filesLocation != null;
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

    @Override public RouteMatcher getMatcher () { return routeMatcher; }
}
