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

import static spark.route.HttpMethod.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import spark.route.RouteMatcher;
import spark.route.RouteMatcherFactory;

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
public class Router {
    private final RouteMatcher routeMatcher = RouteMatcherFactory.get ();

    /** Holds a map of Exception classes and associated handlers. */
    private final Map<Class<? extends Exception>, Fault> exceptionMap = new HashMap<> ();

    public Router (Action... actions) {
        for (Action a : actions)
            addRoute (a);
    }

    protected synchronized void addRoute (Action action) {
        routeMatcher.parseValidateAddRoute (
            action.method + " '" + action.path + "'", action.acceptType, action);
    }

    /**
     * Map the route for HTTP GET requests
     *
     * @param aPath The route
     */
    public void get (String aPath, Function<Context, Object> aHandler) {
        addRoute (new Route (get, aPath, aHandler));
    }

    public synchronized void get (
        String aPath, String aAcceptType, Function<Context, Object> aHandler) {

        addRoute (new Route (get, aPath, aAcceptType, aHandler));
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param aPath The route
     */
    public synchronized void post (String aPath, Function<Context, Object> aHandler) {
        addRoute (new Route (post, aPath, aHandler));
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param aPath The route
     */
    public synchronized void put (String aPath, Function<Context, Object> aHandler) {
        addRoute (new Route (put, aPath, aHandler));
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param aPath The route
     */
    public synchronized void patch (String aPath, Function<Context, Object> aHandler) {
        addRoute (new Route (patch, aPath, aHandler));
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param aPath The route
     */
    public synchronized void delete (String aPath, Function<Context, Object> aHandler) {
        addRoute (new Route (delete, aPath, aHandler));
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param aPath The route
     */
    public synchronized void head (String aPath, Function<Context, Object> aHandler) {
        addRoute (new Route (head, aPath, aHandler));
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param aPath The route
     */
    public synchronized void trace (String aPath, Function<Context, Object> aHandler) {
        addRoute (new Route (trace, aPath, aHandler));
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param aPath The route
     */
    public synchronized void connect (
        String aPath, Function<Context, Object> aHandler) {

        addRoute (new Route (connect, aPath, aHandler));
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param aPath The route
     */
    public synchronized void options (
        String aPath, Function<Context, Object> aHandler) {

        addRoute (new Route (options, aPath, aHandler));
    }

    /**
     * Maps a filter to be executed before any matching routes
     *
     * @param aHandler The filter
     */
    public synchronized void before (Consumer<Context> aHandler) {
        addRoute (new Filter (before, aHandler));
    }

    public synchronized void before (String aPath, Consumer<Context> aHandler) {
        addRoute (new Filter (before, aPath, aHandler));
    }

    public synchronized void before (
        String aPath, String aAcceptType, Consumer<Context> aHandler) {

        addRoute (new Filter (before, aPath, aAcceptType, aHandler));
    }

    /**
     * Maps a filter to be executed after any matching routes
     *
     * @param aHandler The filter
     */
    public synchronized void after (Consumer<Context> aHandler) {
        addRoute (new Filter (after, aHandler));
    }

    public synchronized void after (String aPath, Consumer<Context> aHandler) {
        addRoute (new Filter (after, aPath, aHandler));
    }

    public synchronized void after (
        String aPath, String aAcceptType, Consumer<Context> aHandler) {

        addRoute (new Filter (after, aPath, aAcceptType, aHandler));
    }

    // Hide constructor
    protected Router () {
        throw new IllegalStateException ();
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
