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

import static spark.HttpMethod.after;
import static spark.HttpMethod.before;

import java.util.function.Consumer;

/**
 * A Filter is built up by a path (for url-matching) and the implementation of the 'handle'
 * method.
 * When a request is made, if present, the matching routes 'handle' method is invoked.
 *
 * @author Per Wendel
 */
public final class Filter extends Action {
    public static final String ALL_PATHS = "+/*paths";

    private static final String DEFAUT_CONTENT_TYPE = "text/html";

    public static Filter before (Consumer<Context> handler) {
        return new Filter (before, handler);
    }

    public static Filter before (String path, Consumer<Context> handler) {
        return new Filter (before, path, handler);
    }

    public static Filter before (String path, String acceptType, Consumer<Context> handler) {
        return new Filter (before, path, acceptType, handler);
    }

    public static Filter after (Consumer<Context> handler) {
        return new Filter (after, handler);
    }

    public static Filter after (String path, Consumer<Context> handler) {
        return new Filter (after, path, handler);
    }

    public static Filter after (String path, String acceptType, Consumer<Context> handler) {
        return new Filter (after, path, acceptType, handler);
    }

    private final Consumer<Context> handler;

    /**
     * Constructs a filter that matches on everything.
     */
    Filter (final HttpMethod method, final Consumer<Context> handler) {
        this (method, ALL_PATHS, handler);
    }

    /**
     * Constructor.
     *
     * @param path The filter path which is used for matching. (e.g. /hello, users/:name).
     */
    Filter (final HttpMethod method, final String path, final Consumer<Context> handler) {
        this (method, path, DEFAUT_CONTENT_TYPE, handler);
    }

    Filter (
        final HttpMethod method,
        final String path,
        final String acceptType,
        final Consumer<Context> handler) {

        super (method, path, acceptType);

        if (handler == null)
            throw new IllegalArgumentException ();

        this.handler = handler;
    }

    /**
     * Invoked when a request is made on this filter's corresponding path e.g. '/hello'.
     *
     * @param req The request object providing information about the HTTP request.
     * @param res The response object providing functionality for modifying the response.
     */
    public void handle (Request req, Response res) {
        handler.accept (new Context (req, res));
    }
}
