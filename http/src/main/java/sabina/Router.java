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

import static sabina.FilterOrder.*;
import static sabina.HttpMethod.*;
import static sabina.util.Checks.checkArgument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.*;

/**
 * Trait to support router methods in classes with access to a RouteMatcher.
 *
 * @author jamming
 */
public abstract class Router {

    /** This is just a "type alias". */
    public interface Handler extends Function<Request, Object> {}
    /** This is just a "type alias". */
    public interface VoidHandler extends Consumer<Request> {}

    public static final String ALL_PATHS = "";

    /*
     * TODO Change 'public' for 'private' once refactored
     */
    public final Map<HttpMethod, List<Route>> routeMap = new HashMap<> ();
    public final Map<FilterOrder, List<Filter>> filterMap = new HashMap<> ();

    // TODO
    public void use (String context, Router otherRouter) {

    }

    /** Holds a map of Exception classes and associated handlers. */
    public final
    Map<Class<? extends Exception>, BiConsumer<? extends Exception, Request>> exceptionMap =
        new HashMap<> ();

    private void addRoute (Route action) {
        HttpMethod method = action.method;
        if (!routeMap.containsKey (method))
            routeMap.put (method, new ArrayList<> ());
        routeMap.get (method).add (action);
    }

    private void addFilter (Filter action) {
        FilterOrder order = action.order;
        if (!filterMap.containsKey (order))
            filterMap.put (order, new ArrayList<> ());
        filterMap.get (order).add (action);
    }

    public void on (FilterOrder m, Handler h) {
        addFilter (new Filter (m, h));
    }

    public void on (FilterOrder m, String p, Handler h) {
        addFilter (new Filter (m, p, h));
    }

    public void on (HttpMethod m, Handler h) {
        addRoute (new Route (m, h));
    }

    public void on (HttpMethod m, String p, Handler h) {
        addRoute (new Route (m, p, h));
    }

    private Handler wrap (VoidHandler h) {
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
    public <T extends Exception> void exception (Class<T> exception, BiConsumer<T, Request> h) {
        checkArgument (h != null);
        exceptionMap.put(exception, h);
    }

    /*
     * Filters
     */
    public void after (VoidHandler h) { on (AFTER, wrap (h)); }
    public void before (VoidHandler h) { on (BEFORE, wrap (h)); }
    public void after (String p, VoidHandler h) { on (AFTER, p, wrap (h)); }
    public void before (String p, VoidHandler h) { on (BEFORE, p, wrap (h)); }

    /*
     * Routes
     */
    public void delete (String p, Handler h) { on (DELETE, p, h); }
    public void delete (String p, VoidHandler h) { delete (p, wrap (h)); }
    public void get (String p, Handler h) { on (GET, p, h); }
    public void get (String p, VoidHandler h) { get (p, wrap (h)); }
    public void head (String p, Handler h) { on (HEAD, p, h); }
    public void head (String p, VoidHandler h) { head (p, wrap (h)); }
    public void options (String p, Handler h) { on (OPTIONS, p, h); }
    public void options (String p, VoidHandler h) { options (p, wrap (h)); }
    public void patch (String p, Handler h) { on (PATCH, p, h); }
    public void patch (String p, VoidHandler h) { patch (p, wrap (h)); }
    public void post (String p, Handler h) { on (POST, p, h); }
    public void post (String p, VoidHandler h) { post (p, wrap (h)); }
    public void put (String p, Handler h) { on (PUT, p, h); }
    public void put (String p, VoidHandler h) { put (p, wrap (h)); }
    public void trace (String p, Handler h) { on (TRACE, p, h); }
    public void trace (String p, VoidHandler h) { trace (p, wrap (h)); }
}
