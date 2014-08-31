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

package spark;

import static org.slf4j.LoggerFactory.getLogger;
import static spark.servlet.SparkFilter.configureExternalStaticResources;
import static spark.servlet.SparkFilter.configureStaticResources;

import org.slf4j.Logger;
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
public class Server {
    private static final Logger LOG = getLogger (Server.class);
    private static final int SPARK_DEFAULT_PORT = 4567;
    private static final String INIT_ERROR =
        "This must be done before route mapping has begun";

    private int port = SPARK_DEFAULT_PORT;
    private boolean initialized = false;
    private String ipAddress = "0.0.0.0";

    private String keystoreFile;
    private String keystorePassword;
    private String truststoreFile;
    private String truststorePassword;

    private String staticFileFolder;
    private String externalStaticFileFolder;

    private SparkServer server;

    private boolean runFromServlet;
    private boolean servletStaticLocationSet;
    private boolean servletExternalStaticLocationSet;

    /**
     * Set the IP address that Spark should listen on. If not called the default
     * address is '0.0.0.0'. This has to be called before any route mapping is done.
     *
     * @param ipAddress The ipAddress
     */
    public synchronized void setIpAddress (String ipAddress) {
        if (initialized)
            throw new IllegalStateException (INIT_ERROR);

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
        if (initialized)
            throw new IllegalStateException (INIT_ERROR);

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

        if (initialized)
            throw new IllegalStateException (INIT_ERROR);

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
    public synchronized void externalStaticFileLocation (String externalFolder) {
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

    private synchronized void init () {
        if (!initialized) {
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

    private boolean hasMultipleHandlers () {
        return staticFileFolder != null || externalStaticFileFolder != null;
    }

    /**
     * Stops the Spark server and clears all routes
     */
    public synchronized void stop () {
        if (server != null) {
            server.stop ();
        }
        initialized = false;
    }

    public synchronized void runFromServlet () {
        runFromServlet = true;
        if (!initialized) {
            initialized = true;
        }
    }

    // Hide constructor
    protected Server () {
        throw new IllegalStateException ();
    }
}
