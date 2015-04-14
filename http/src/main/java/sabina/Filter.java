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

import static sabina.util.Checks.checkArgument;
import static sabina.util.Strings.isNullOrEmpty;

import java.util.function.Consumer;

/**
 * A Filter is built up by a path (for url-matching) and the implementation of the 'handle'
 * method.
 * When a request is made, if present, the matching routes 'handle' method is invoked.
 *
 * @author Per Wendel
 */
public final class Filter extends Action {
    /** This is just a "type alias". */
    public interface Handler extends Consumer<Request> {}

    public static final String ALL_PATHS = "+/*paths";
    private static final String DEFAULT_CONTENT_TYPE = "text/html";

    private final Handler handler;

    /**
     * Constructs a filter that matches on everything.
     * @param method .
     * @param handler .
     */
    Filter (final HttpMethod method, final Handler handler) {
        this (method, ALL_PATHS, handler);
    }

    /**
     * Constructor.
     *
     * @param method .
     * @param path The filter path which is used for matching. (e.g. /hello, users/:name).
     * @param handler .
     */
    Filter (final HttpMethod method, final String path, final Handler handler) {
        this (method, path, DEFAULT_CONTENT_TYPE, handler);
    }

    Filter (
        final HttpMethod method,
        final String path,
        final String acceptType,
        final Handler handler) {

        super (method, path, isNullOrEmpty (acceptType)? DEFAULT_CONTENT_TYPE : acceptType);

        checkArgument (handler != null);
        this.handler = handler;
    }

    /**
     * Invoked when a request is made on this filter's corresponding path e.g. '/hello'.
     *
     * @param req The request object providing information about the HTTP request.
     */
    public void handle (final Request req) {
        handler.accept (req);
    }
}
