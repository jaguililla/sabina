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

import static java.util.logging.Logger.getLogger;
import static sabina.Route.path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

import sabina.builder.*;
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
    public static final String DEFAULT_HOST = "localhost";

    public static Server server (Node... aHandler) {
        return new Server (aHandler);
    }

    public static Server server (String [] aArgs, Node... aHandler) {
        // TODO Parse args to parameters
        return server (aHandler);
    }

    public static Server server (List<Parameter<?>> parameters, Node... aHandler) {
        Server s = server (aHandler);
        for (Parameter<?> p : parameters)
            switch (p.name) {
                case PORT:
                    s.setPort ((Integer)p.value);
                    break;
                case HOST:
                    s.setIpAddress ((String)p.value);
                    break;
                case KEYSTORE:
                    s.keystoreFile = (String)p.value;
                    break;
                case KEYSTORE_PASSWORD:
                    s.keystorePassword = (String)p.value;
                    break;
                case TRUSTSTORE:
                    s.truststoreFile = (String)p.value;
                    break;
                case TRUSTSTORE_PASSWORD:
                    s.truststorePassword = (String)p.value;
                    break;
                case RESOURCES_FOLDER:
                    s.staticFileFolder = (String)p.value;
                    break;
                case FILES_FOLDER:
                    s.externalStaticFileFolder = (String)p.value;
                    break;
                default:
                    throw new IllegalStateException ("Not handled parameter: " + p.name);
            }
        return s;
    }

    public static void serve (Node... aHandler) {
        server (aHandler).startUp ();
    }

    public static void serve (String [] aArgs, Node... aHandler) {
        server (aArgs, aHandler).startUp ();
    }

    public static void serve (List<Parameter<?>> parameters, Node... aHandler) {
        server (parameters, aHandler).startUp ();
    }

    private int port = DEFAULT_PORT;
    private String ipAddress = "0.0.0.0";

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

    public Server (Node... nodes) {
        List<Action> actions = getActions (nodes);
        for (Action a : actions)
            addRoute (a);
    }

    List<Action> getActions (Node... nodes) {
        List<Action> r = new ArrayList<> ();
        getRules (r, path ("/", nodes));
        return r;
    }

    // TODO Add actions of leaf nodes
    void getRules (final List<Action> rules, final Node root) {
        for (Node n : root.children)
            if (n.children.isEmpty ())
                rules.add (getAction ((MethodNode)n));
            else
                getRules (rules, n);
    }

    Action getAction (MethodNode node) {
        String aContentType = "";
        String aPath = "";

        for (Node p = node.parent; p != null; p = p.parent)
            if (p instanceof PathNode)
                aPath = ((PathNode)p).path + aPath;
            else if (p instanceof ContentTypeNode)
                aContentType += ((ContentTypeNode)p).contentType;
            else
                throw new IllegalStateException ("Unsupported node type");

        return node instanceof FilterNode?
            new Filter (node.method, aPath, aContentType, ((FilterNode)node).handler) :
            new Route (node.method, aPath, aContentType, ((RouteNode)node).handler);
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

    private synchronized void startUp () {
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
