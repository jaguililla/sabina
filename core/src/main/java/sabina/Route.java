/*
 * Copyright © 2011 Per Wendel. All rights reserved.
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

import static sabina.HttpMethod.*;

import java.util.function.Consumer;
import java.util.function.Function;

import sabina.builder.*;

/**
 * A Route is built up by a path (for url-matching) and the implementation of the 'handle'
 * method.
 * When a request is made, if present, the matching routes 'handle' method is invoked. The
 * object that is returned from 'handle' will be set to the response body (toString()).
 *
 * @author Per Wendel
 */
public final class Route extends Action {
    private static final String DEFAULT_ACCEPT_TYPE = "*/*";

    /*
     * PATHS
     */
    public static PathNode path (String path, Node... aNodes) {
        return new PathNode (path, aNodes);
    }

    public static PathNode path (String path, Consumer<Context> handler) {
        return path (path, Filter.before (handler));
    }

    /*
     * METHODS
     */
    public static RouteNode connect (Function<Context, Object> handler) {
        return new RouteNode (connect, handler);
    }

    public static RouteNode delete (Function<Context, Object> handler) {
        return new RouteNode (delete, handler);
    }

    public static RouteNode get (Function<Context, Object> handler) {
        return new RouteNode (get, handler);
    }

    public static RouteNode head (Function<Context, Object> handler) {
        return new RouteNode (head, handler);
    }

    public static RouteNode options (Function<Context, Object> handler) {
        return new RouteNode (options, handler);
    }

    public static RouteNode patch (Function<Context, Object> handler) {
        return new RouteNode (patch, handler);
    }

    public static RouteNode post (Function<Context, Object> handler) {
        return new RouteNode (post, handler);
    }

    public static RouteNode put (Function<Context, Object> handler) {
        return new RouteNode (put, handler);
    }

    public static RouteNode trace (Function<Context, Object> handler) {
        return new RouteNode (trace, handler);
    }

    /*
     * CONTENT TYPES
     */
    public static ContentTypeNode contentType (String contentType, MethodNode... aMethods) {
        return new ContentTypeNode (contentType, aMethods);
    }

    public static ContentTypeNode textHtml (MethodNode... aMethods) {
        return contentType ("text/html", aMethods);
    }

    public static ContentTypeNode textPlain (MethodNode... aMethods) {
        return contentType ("text/plain", aMethods);
    }

    public static ContentTypeNode applicationJson (MethodNode... aMethods) {
        return contentType ("application/json", aMethods);
    }

    /*
     * METHOD UTILITIES
     */
    public static PathNode connect (String path, Function<Context, Object> handler) {
        return path (path, connect (handler));
    }

    public static PathNode delete (String path, Function<Context, Object> handler) {
        return path (path, delete (handler));
    }

    public static PathNode get (String path, Function<Context, Object> handler) {
        return path (path, get (handler));
    }

    public static PathNode head (String path, Function<Context, Object> handler) {
        return path (path, head (handler));
    }

    public static PathNode options (String path, Function<Context, Object> handler) {
        return path (path, options (handler));
    }

    public static PathNode patch (String path, Function<Context, Object> handler) {
        return path (path, patch (handler));
    }

    public static PathNode post (String path, Function<Context, Object> handler) {
        return path (path, post (handler));
    }

    public static PathNode put (String path, Function<Context, Object> handler) {
        return path (path, put (handler));
    }

    public static PathNode trace (String path, Function<Context, Object> handler) {
        return path (path, trace (handler));
    }

    public static PathNode connect (
        String path, String contentType, Function<Context, Object> handler) {

        return path (path, contentType (contentType, connect (handler)));
    }

    public static PathNode delete (
        String path, String contentType, Function<Context, Object> handler) {

        return path (path, contentType (contentType, delete (handler)));
    }

    public static PathNode get (
        String path, String contentType, Function<Context, Object> handler) {

        return path (path, contentType (contentType, get (handler)));
    }

    public static PathNode head (
        String path, String contentType, Function<Context, Object> handler) {

        return path (path, contentType (contentType, head (handler)));
    }

    public static PathNode options (
        String path, String contentType, Function<Context, Object> handler) {

        return path (path, contentType (contentType, options (handler)));
    }

    public static PathNode patch (
        String path, String contentType, Function<Context, Object> handler) {

        return path (path, contentType (contentType, patch (handler)));
    }

    public static PathNode post (
        String path, String contentType, Function<Context, Object> handler) {

        return path (path, contentType (contentType, post (handler)));
    }

    public static PathNode put (
        String path, String contentType, Function<Context, Object> handler) {

        return path (path, contentType (contentType, put (handler)));
    }

    public static PathNode trace (
        String path, String contentType, Function<Context, Object> handler) {

        return path (path, contentType (contentType, trace (handler)));
    }

    private final Function<Context, Object> handler;

    /**
     * Constructor.
     *
     * @param path The route path which is used for matching. (e.g. /hello, users/:name).
     */
    Route (
        final HttpMethod method, final String path, final Function<Context, Object> handler) {

        this (method, path, DEFAULT_ACCEPT_TYPE, handler);
    }

    /**
     * Constructor.
     *
     * @param path The route path which is used for matching. (e.g. /hello, users/:name).
     * @param acceptType The accept type which is used for matching.
     */
    Route (
        final HttpMethod method,
        final String path,
        final String acceptType,
        final Function<Context, Object> handler) {

        super (method, path, acceptType);

        if (handler == null)
            throw new IllegalArgumentException ();

        this.handler = handler;
    }

    /**
     * Invoked when a req is made on this route's corresponding path e.g. '/hello'.
     *
     * @param req The request object providing information about the HTTP request.
     * @param res The response object providing functionality for modifying the response.
     *
     * @return The content to be set in the response.
     */
    public Object handle (Request req, Response res) {
        return handler.apply (new Context (req, res));
    }
}
