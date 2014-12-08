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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static sabina.HttpMethod.*;

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

    public static Route connect (String path, Function<Exchange, Object> handler) {
        return new Route (connect, path, handler);
    }

    public static Route delete (String path, Function<Exchange, Object> handler) {
        return new Route (delete, path, handler);
    }

    public static Route get (String path, Function<Exchange, Object> handler) {
        return new Route (get, path, handler);
    }

    public static Route head (String path, Function<Exchange, Object> handler) {
        return new Route (head, path, handler);
    }

    public static Route options (String path, Function<Exchange, Object> handler) {
        return new Route (options, path, handler);
    }

    public static Route patch (String path, Function<Exchange, Object> handler) {
        return new Route (patch, path, handler);
    }

    public static Route post (String path, Function<Exchange, Object> handler) {
        return new Route (post, path, handler);
    }

    public static Route put (String path, Function<Exchange, Object> handler) {
        return new Route (put, path, handler);
    }

    public static Route trace (String path, Function<Exchange, Object> handler) {
        return new Route (trace, path, handler);
    }

    public static Route connect (
        String path, String contentType, Function<Exchange, Object> handler) {

        return new Route (connect, path, contentType, handler);
    }

    public static Route delete (
        String path, String contentType, Function<Exchange, Object> handler) {

        return new Route (delete, path, contentType, handler);
    }

    public static Route get (
        String path, String contentType, Function<Exchange, Object> handler) {

        return new Route (get, path, contentType, handler);
    }

    public static Route head (
        String path, String contentType, Function<Exchange, Object> handler) {

        return new Route (head, path, contentType, handler);
    }

    public static Route options (
        String path, String contentType, Function<Exchange, Object> handler) {

        return new Route (options, path, contentType, handler);
    }

    public static Route patch (
        String path, String contentType, Function<Exchange, Object> handler) {

        return new Route (patch, path, contentType, handler);
    }

    public static Route post (
        String path, String contentType, Function<Exchange, Object> handler) {

        return new Route (post, path, contentType, handler);
    }

    public static Route put (
        String path, String contentType, Function<Exchange, Object> handler) {

        return new Route (put, path, contentType, handler);
    }

    public static Route trace (
        String path, String contentType, Function<Exchange, Object> handler) {

        return new Route (trace, path, contentType, handler);
    }

    private final Function<Exchange, Object> handler;

    /**
     * Constructor.
     *
     * @param path The route path which is used for matching. (e.g. /hello, users/:name).
     */
    Route (
        final HttpMethod method, final String path, final Function<Exchange, Object> handler) {

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
        final Function<Exchange, Object> handler) {

        super (method, path, isNullOrEmpty (acceptType)? DEFAULT_ACCEPT_TYPE : acceptType);
        checkArgument (handler != null);
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
        return handler.apply (new Exchange (req, res));
    }
}
