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
import static co.there4.bali.Checks.checkArgument;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import sabina.route.RouteMatcher;

/**
 * Trait to support router methods in classes with access to a RouteMatcher.
 *
 * @author jamming
 */
public interface Router {
    /** This is just a "type alias". */
    interface Handler extends Function<Request, Object> {}
    /** This is just a "type alias". */
    interface VoidHandler extends Consumer<Request> {}

    RouteMatcher getMatcher ();

    /**
     * Parses, validates and adds a route
     */
    default void addRoute (Route action) { getMatcher ().processRoute (action); }

    default void on (HttpMethod m, Handler h) {
        addRoute (new Route (m, h));
    }

    default void on (HttpMethod m, String p, Handler h) {
        addRoute (new Route (m, p, h));
    }

    default Handler wrap (VoidHandler h) {
        return request -> {
            h.accept (request);
            return Void.TYPE;
        };
    }

    /**
     * Maps an exception handler to be executed when an exception occurs during routing.
     *
     * @param exception the exception class.
     * @param h The handler.
     * @param <T> Exception type.
     */
    default <T extends Exception> void exception (Class<T> exception, BiConsumer<T, Request> h) {
        checkArgument (h != null);
        getMatcher ().processFault (exception, h);
    }

    /*
     * Filters
     */
    default void after (VoidHandler h) { on (AFTER, wrap (h)); }
    default void before (VoidHandler h) { on (BEFORE, wrap (h)); }
    default void after (String p, VoidHandler h) { on (AFTER, p, wrap (h)); }
    default void before (String p, VoidHandler h) { on (BEFORE, p, wrap (h)); }

    /*
     * Routes
     */
    default void delete (String p, Handler h) { on (DELETE, p, h); }
    default void delete (String p, VoidHandler h) { delete (p, wrap (h)); }
    default void get (String p, Handler h) { on (GET, p, h); }
    default void get (String p, VoidHandler h) { get (p, wrap (h)); }
    default void head (String p, Handler h) { on (HEAD, p, h); }
    default void head (String p, VoidHandler h) { head (p, wrap (h)); }
    default void options (String p, Handler h) { on (OPTIONS, p, h); }
    default void options (String p, VoidHandler h) { options (p, wrap (h)); }
    default void patch (String p, Handler h) { on (PATCH, p, h); }
    default void patch (String p, VoidHandler h) { patch (p, wrap (h)); }
    default void post (String p, Handler h) { on (POST, p, h); }
    default void post (String p, VoidHandler h) { post (p, wrap (h)); }
    default void put (String p, Handler h) { on (PUT, p, h); }
    default void put (String p, VoidHandler h) { put (p, wrap (h)); }
    default void trace (String p, Handler h) { on (TRACE, p, h); }
    default void trace (String p, VoidHandler h) { trace (p, wrap (h)); }
}
