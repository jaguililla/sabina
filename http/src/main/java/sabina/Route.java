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
    /** This is just a "type alias". */
    public static interface Handler extends Function<Request, Object> {}

    private static final String DEFAULT_ACCEPT_TYPE = "*/*";

    private final Handler handler;

    /**
     * Constructor.
     *
     * @param path The route path which is used for matching. (e.g. /hello, users/:name).
     */
    Route (final HttpMethod method, final String path, final Handler handler) {
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
        final Handler handler) {

        super (method, path, isNullOrEmpty (acceptType)? DEFAULT_ACCEPT_TYPE : acceptType);
        checkArgument (handler != null);
        this.handler = handler;
    }

    /**
     * Invoked when a req is made on this route's corresponding path e.g. '/hello'.
     *
     * @param req The request object providing information about the HTTP request.
     *
     * @return The content to be set in the response.
     */
    public Object handle (final Request req) {
        return handler.apply (req);
    }
}
