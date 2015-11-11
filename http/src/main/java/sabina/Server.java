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
import static java.lang.Runtime.getRuntime;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.lang.management.ManagementFactory.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;
import static org.slf4j.LoggerFactory.getLogger;
import static sabina.util.Builders.entry;
import static sabina.util.Io.read;
import static sabina.util.Strings.filter;
import static sabina.util.Configuration.*;

import java.lang.management.MemoryUsage;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import sabina.util.*;

import sabina.backend.Backend;
import sabina.backend.BackendFactory;

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
public class Server extends Router {
    private static final Logger LOG = getLogger (Server.class);

    /**
     * 1. Application resource (basic defaults)
     * 2. URL shared config accross different systems (architecture)
     * 3. System properties (could be system wide)
     * 4. Config file (installation configuration)
     * 5. Program parameters (specified at application startup)
     */
    private static final Configuration CONFIGURATION = configuration ().load (
        resource ("sabina.properties"),
        resource ("application.properties"),
        system ("sabina"),
        file ("application.properties")
    );

    private int port = configuration ().getInt ("sabina.port");
    private String bind = configuration ().getString ("sabina.bind");

    private String keystoreFile = configuration ().getString ("sabina.keystore.file");
    private String keystorePassword = configuration ().getString ("sabina.keystore.password");
    private String truststoreFile = configuration ().getString ("sabina.truststore.file");
    private String truststorePassword = configuration ().getString ("sabina.truststore.password");

    private String resourcesLocation = configuration ().getString ("sabina.resources.location");
    private String filesLocation = configuration ().getString ("sabina.files.location");

    private String backend = configuration ().getString ("sabina.backend");

    private Backend server;

    public Server () {
        super ();
    }

    public Server (int port) {
        this ();
        port (port);
    }

    public Server (String backend, int port) {
        this ();
        port (port);
        backend (backend);
    }

    /**
     * Set the IP address that Sabina should listen on. If not called the default
     * address is '0.0.0.0'. This has to be called before any route mapping is done.
     *
     * @param ipAddress The ip.
     */
    public void bind (String ipAddress) {
        this.bind = ipAddress;
    }

    public String bind () {
        return bind;
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

    public int port () {
        return port;
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
        new Thread (() -> {
            server = BackendFactory.create (backend, this, hasMultipleHandlers ());
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
        showBanner ();
    }

    public void start (String port) {
        start (null, parseInt (port));
    }

    public void start (int port) {
        start (null, port);
    }

    public void start (String bind, String port) {
        start (bind, parseInt (port));
    }

    public void start (String bind, int port) {
        if (bind != null)
            bind (bind);
        port (port);
        start ();
    }

    private void showBanner () {
        String banner = read (CONFIGURATION.getString ("sabina.banner"));

        Map<String, Object> configuration = CONFIGURATION.keys ().stream ()
            .map (k -> entry (k, CONFIGURATION.get (k)))
            .collect (toMap (Entry::getKey, Entry::getValue));

        // Override manually changed ones
        configuration.put ("sabina.backend", backend ().toUpperCase ());
        configuration.put ("sabina.bind", bind);
        configuration.put ("sabina.port", port);
        configuration.put ("sabina.keystore.file", keystoreFile == null? "" : keystoreFile);
        configuration.put ("sabina.truststore.file", truststoreFile == null? "" : truststoreFile);
        configuration.put ("sabina.resources.location", resourcesLocation == null? "" : resourcesLocation);
        configuration.put ("sabina.files.location", filesLocation == null? "" : filesLocation);

        // Add runtime data
        Runtime rt = getRuntime ();
        MemoryUsage heap = getMemoryMXBean ().getHeapMemoryUsage ();
        try {
            configuration.put ("sabina.host", InetAddress.getLocalHost ().getCanonicalHostName ());
        }
        catch (UnknownHostException e) {
            configuration.put ("sabina.host", "UNKNOWN");
        }
        configuration.put ("sabina.cpus", rt.availableProcessors ());
        configuration.put ("sabina.jvm.memory", format ("%,d", heap.getInit () / 1024));
        configuration.put ("sabina.jvm", getRuntimeMXBean ().getVmName ());
        configuration.put ("sabina.jvm.version", getRuntimeMXBean ().getSpecVersion ());

        // Generate 'Settings' list
        configuration.put ("sabina.application.settings", configuration.entrySet ().stream ()
                .filter (e -> !e.getKey ().startsWith ("sabina."))
                .map (e -> format ("%20s : %s", e.getKey (), e.getValue ()))
                .collect (joining (Strings.EOL, Strings.EOL, ""))
        );

        // Add startup data
        long bootTime = currentTimeMillis () - getRuntimeMXBean ().getStartTime ();
        configuration.put ("sabina.boot.time", format ("%01.3f", bootTime / 1000f));

        rt.gc (); // Run GC just after start

        configuration.put ("sabina.used.memory", format ("%,d", heap.getUsed () / 1024));

        LOG.info ("Application started{}", filter (banner, configuration));
    }

    private boolean hasMultipleHandlers () {
        return !Strings.isEmpty (resourcesLocation) || !Strings.isEmpty (filesLocation);
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

//        routeMatcher = RouteMatcherFactory.create (); // TODO
    }
}
