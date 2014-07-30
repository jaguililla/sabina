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

import static spark.utils.SparkUtils.ALL_PATHS;

import java.util.function.Consumer;

/**
 * A Filter is built up by a path (for url-matching) and the implementation of the 'handle'
 * method.
 * When a request is made, if present, the matching routes 'handle' method is invoked.
 *
 * @author Per Wendel
 */
public class Filter extends Action {

    protected static final String DEFAUT_CONTENT_TYPE = "text/html";

    final Consumer<Context> mHandler;

    /**
     * Constructs a filter that matches on everything.
     */
    protected Filter (Consumer<Context> aHandler) {
        super (ALL_PATHS, DEFAUT_CONTENT_TYPE);
        mHandler = aHandler;
    }

    /**
     * Constructor.
     *
     * @param path The filter path which is used for matching. (e.g. /hello, users/:name).
     */
    protected Filter (String path, Consumer<Context> aHandler) {
        super (path, DEFAUT_CONTENT_TYPE);
        mHandler = aHandler;
    }

    protected Filter (String path, String acceptType, Consumer<Context> aHandler) {

        super (path, acceptType);
        mHandler = aHandler;
    }

    /**
     * Invoked when a request is made on this filter's corresponding path e.g. '/hello'.
     *
     * @param request The request object providing information about the HTTP request.
     * @param response The response object providing functionality for modifying the response.
     */
    public void handle (Request request, Response response) {
        mHandler.accept (new Context (request, response));
    }
}
