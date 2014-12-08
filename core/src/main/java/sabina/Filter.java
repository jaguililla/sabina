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
import static sabina.HttpMethod.after;
import static sabina.HttpMethod.before;

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
    private static final String DEFAULT_CONTENT_TYPE = "text/html";

    public static Filter after (Consumer<Exchange> handler) {
        return new Filter (after, handler);
    }

    public static Filter before (Consumer<Exchange> handler) {
        return new Filter (before, handler);
    }

    public static Filter after (String path, Consumer<Exchange> handler) {
        return new Filter (after, path, handler);
    }

    public static Filter before (String path, Consumer<Exchange> handler) {
        return new Filter (before, path, handler);
    }

    public static Filter after (String path, String contentType, Consumer<Exchange> handler) {
        return new Filter (after, path, contentType, handler);
    }

    public static Filter before (
        String path, String contentType, Consumer<Exchange> handler) {

        return new Filter (before, path, contentType, handler);
    }

    private final Consumer<Exchange> handler;

    /**
     * Constructs a filter that matches on everything.
     */
    Filter (final HttpMethod method, final Consumer<Exchange> handler) {
        this (method, ALL_PATHS, handler);
    }

    /**
     * Constructor.
     *
     * @param path The filter path which is used for matching. (e.g. /hello, users/:name).
     */
    Filter (final HttpMethod method, final String path, final Consumer<Exchange> handler) {
        this (method, path, DEFAULT_CONTENT_TYPE, handler);
    }

    Filter (
        final HttpMethod method,
        final String path,
        final String acceptType,
        final Consumer<Exchange> handler) {

        super (method, path, isNullOrEmpty (acceptType)? DEFAULT_CONTENT_TYPE : acceptType);

        checkArgument (handler != null);
        this.handler = handler;
    }

    /**
     * Invoked when a request is made on this filter's corresponding path e.g. '/hello'.
     *
     * @param req The request object providing information about the HTTP request.
     * @param res The response object providing functionality for modifying the response.
     */
    public void handle (Request req, Response res) {
        handler.accept (new Exchange (req, res));
    }
}
