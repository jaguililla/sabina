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

package spark;

import static spark.HttpMethod.*;

import java.util.function.Function;

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

    public static Route get (String path, Function<Context, Object> handler) {
        return new Route (get, path, handler);
    }

    public static Route get (
        String path, String acceptType, Function<Context, Object> handler) {

        return new Route (get, path, acceptType, handler);
    }

    public static Route post (String path, Function<Context, Object> handler) {
        return new Route (post, path, handler);
    }

    public static Route put (String path, Function<Context, Object> handler) {
        return new Route (put, path, handler);
    }

    public static Route patch (String path, Function<Context, Object> handler) {
        return new Route (patch, path, handler);
    }

    public static Route delete (String path, Function<Context, Object> handler) {
        return new Route (delete, path, handler);
    }

    public static Route head (String path, Function<Context, Object> handler) {
        return new Route (head, path, handler);
    }

    public static Route trace (String path, Function<Context, Object> handler) {
        return new Route (trace, path, handler);
    }

    public static Route connect (String path, Function<Context, Object> handler) {
        return new Route (connect, path, handler);
    }

    public static Route options (String path, Function<Context, Object> handler) {
        return new Route (options, path, handler);
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
