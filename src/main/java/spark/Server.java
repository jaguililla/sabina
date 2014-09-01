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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import spark.builder.Node;
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
public class Server {
    private static final Logger LOG = getLogger (Server.class);

    private static final String INIT_ERROR =
        "This must be done before route mapping has begun";

    public static final int DEFAULT_PORT = 4567;
    public static final String DEFAULT_HOST = "localhost";

    private int port = DEFAULT_PORT;
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

    private final RouteMatcher routeMatcher = RouteMatcherFactory.get ();
    /** Holds a map of Exception classes and associated handlers. */
    private final Map<Class<? extends Exception>, Fault> exceptionMap = new HashMap<> ();

    public Server (Node... actions) {
        buildActions (actions).forEach (this::addRoute);
    }

    private List<Action> buildActions (Node... actions) {
        return null;
    }

//    static void getActions (final List<Action> rules, final Node root) {
//        for (Node n : root.children)
//            if (n.children.isEmpty ())
//                rules.add (((MethodNode)n).getRule ());
//            else
//                getActions (rules, n);
//    }
//
//    static List<Action> getActions (final Node root) {
//        ArrayList<Action> rules = new ArrayList<> ();
//        getActions (rules, root);
//
//        if (LOG.isLoggable (INFO))
//            for (Action r : rules)
//                LOG.info ("Rule for " + r.method + " " + r.path + " (" + r.contentType + ")");
//
//        return rules;
//    }
//
//    Action getAction () {
//        String aContentType = "";
//        String aPath = "";
//
//        for (Node p = parent; p != null; p = p.parent)
//            if (p instanceof PathNode)
//                aPath = ((PathNode)p).path + aPath;
//            else if (p instanceof ContentTypeNode)
//                aContentType += ((ContentTypeNode)p).contentType;
//            else
//                throw new IllegalStateException ("Unsupported node type");
//
//        return new Rule (handler, method, aContentType, aPath);
//    }

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
            server.shutDown ();
        }
        initialized = false;
    }

    public synchronized void runFromServlet () {
        runFromServlet = true;
        if (!initialized) {
            initialized = true;
        }
    }

    protected synchronized void addRoute (Action action) {
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
        Class<T> exceptionClass, BiConsumer<T, Context> aHandler) {

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
