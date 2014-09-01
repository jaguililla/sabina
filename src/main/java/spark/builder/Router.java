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

package spark.builder;

import static spark.HttpMethod.*;

import java.util.function.Consumer;
import java.util.function.Function;

import spark.Context;

public class Router {
    /*
     * PATHS
     */
    public static PathNode path (String path, Node... aNodes) {
        return new PathNode (path, aNodes);
    }

    public static PathNode path (String path, Consumer<Context> handler) {
        return path (path, before (handler));
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
     * METHODS
     */
    public static FilterNode after (Consumer<Context> handler) {
        return new FilterNode (after, handler);
    }

    public static FilterNode before (Consumer<Context> handler) {
        return new FilterNode (before, handler);
    }

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
     * METHOD UTILITIES
     */
    public static PathNode after (String path, Consumer<Context> handler) {
        return path (path, after (handler));
    }

    public static PathNode before (String path, Consumer<Context> handler) {
        return path (path, before (handler));
    }

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

    public static PathNode after (String path, String contentType, Consumer<Context> handler) {
        return path (path, contentType (contentType, after (handler)));
    }

    public static PathNode before (String path, String contentType, Consumer<Context> handler) {
        return path (path, contentType (contentType, before (handler)));
    }

    public static PathNode connect (String path, String contentType, Function<Context, Object> handler) {
        return path (path, contentType (contentType, connect (handler)));
    }

    public static PathNode delete (String path, String contentType, Function<Context, Object> handler) {
        return path (path, contentType (contentType, delete (handler)));
    }

    public static PathNode get (String path, String contentType, Function<Context, Object> handler) {
        return path (path, contentType (contentType, get (handler)));
    }

    public static PathNode head (String path, String contentType, Function<Context, Object> handler) {
        return path (path, contentType (contentType, head (handler)));
    }

    public static PathNode options (String path, String contentType, Function<Context, Object> handler) {
        return path (path, contentType (contentType, options (handler)));
    }

    public static PathNode patch (String path, String contentType, Function<Context, Object> handler) {
        return path (path, contentType (contentType, patch (handler)));
    }

    public static PathNode post (String path, String contentType, Function<Context, Object> handler) {
        return path (path, contentType (contentType, post (handler)));
    }

    public static PathNode put (String path, String contentType, Function<Context, Object> handler) {
        return path (path, contentType (contentType, put (handler)));
    }

    public static PathNode trace (String path, String contentType, Function<Context, Object> handler) {
        return path (path, contentType (contentType, trace (handler)));
    }
}
