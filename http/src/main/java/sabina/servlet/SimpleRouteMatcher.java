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

package sabina.servlet;

import static java.util.Collections.EMPTY_LIST;
import static java.util.stream.Collectors.toList;
import static sabina.Request.convertRouteToList;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import sabina.*;

/**
 * Simple route matcher that is supposed to work exactly as Sinatra's
 *
 * @author Per Wendel
 */
final class SimpleRouteMatcher implements RouteMatcher {
//    private static final Logger LOG = getLogger (SimpleRouteMatcher.class.getName ());

    private final Router router;

    SimpleRouteMatcher (Router router) {
        this.router = router;
    }

    /**
     * finds target for a requested route
     *
     * @param httpMethod the http method
     * @param path the path
     *
     * @return the target
     */
    @Override public RouteMatch findTarget (HttpMethod httpMethod, String path) {
        final List<Route> routeEntries = this.findTargetsForRequestedRoute (httpMethod, path);
        final Route entry = routeEntries.size () > 0? routeEntries.get(0) : null;
        return entry != null? new RouteMatch (entry, path) : null;
    }

    /**
     * Finds multiple targets for a requested route.
     *
     * @param httpMethod the http method
     * @param path the route path
     *
     * @return the targets
     */
    @Override public List<RouteMatch> findTargets (final HttpMethod httpMethod, final String path) {
        final List<RouteMatch> matchSet = new ArrayList<> ();
        final List<Route> routeEntries = findTargetsForRequestedRoute (httpMethod, path);

        matchSet.addAll (routeEntries.stream ()
            .map (routeEntry -> new RouteMatch (routeEntry, path))
            .collect (Collectors.toList ()));

        return matchSet;
    }

    /**
     * Finds multiple targets for a requested route.
     *
     * @param httpMethod the http method
     * @param path the route path
     *
     * @return the targets
     */
    @Override public List<RouteMatch> findTargets (final FilterOrder httpMethod, final String path) {
        final List<RouteMatch> matchSet = new ArrayList<> ();
        final List<Filter> routeEntries = findTargetsForRequestedRoute (httpMethod, path);

        matchSet.addAll (routeEntries.stream ()
            .map (routeEntry -> new RouteMatch (null, path)) // TODO
            .collect (Collectors.toList ()));

        return matchSet;
    }

    /**
     * Returns the handler associated with the provided exception class
     *
     * @param exceptionClass Type of exception
     * @return Associated handler
     */
    @SuppressWarnings ("unchecked")
    @Override
    public <T extends Exception> BiConsumer<T, Request> findHandler(Class<T> exceptionClass) {
        // If the exception map does not contain the provided exception class, it might
        // still be that a superclass of the exception class is.
        if (!router.exceptionMap.containsKey(exceptionClass)) {

            Class<? extends Exception> superclass =
                (Class<? extends Exception>)exceptionClass.getSuperclass();
            do {
                // Is the superclass mapped?
                if (router.exceptionMap.containsKey(superclass)) {
                    // Use the handler for the mapped superclass, and cache handler
                    // for this exception class
                    BiConsumer<T, Request> handler =
                        (BiConsumer<T, Request>)router.exceptionMap.get(superclass);
                    router.exceptionMap.put(exceptionClass, handler);
                    return handler;
                }

                // Iteratively walk through the exception class's superclasses
                superclass = (Class<? extends Exception>)superclass.getSuperclass();
            } while (superclass != null);

            // No handler found either for the superclasses of the exception class
            // We cache the null value to prevent future
            router.exceptionMap.put(exceptionClass, null);
            return null;
        }

        // Direct map
        return (BiConsumer<T, Request>)router.exceptionMap.get (exceptionClass);
    }

    private List<Route> findTargetsForRequestedRoute (
        HttpMethod httpMethod, String path) {

        return router.routeMap.containsKey (httpMethod)?
            router.routeMap.get(httpMethod).stream ()
                .filter (entry -> matches (entry, path))
                .collect (toList ()) :
            EMPTY_LIST;
    }

    private List<Filter> findTargetsForRequestedRoute (
        FilterOrder httpMethod, String path) {

        return router.filterMap.containsKey (httpMethod)?
            router.filterMap.get(httpMethod).stream ()
                .filter (entry -> matches (entry, path))
                .collect (toList ()) :
            EMPTY_LIST;
    }

    public boolean matches (Filter route, String path) {
        return route.path.equals (Router.ALL_PATHS) || matchPath (route, path);
    }

    public boolean matches (Route route, String path) {
        return matchPath (route, path);
    }

    private boolean matchPath (Route route, String path) {
        if (!route.path.endsWith ("*") && ((path.endsWith ("/") && !route.path.endsWith ("/"))
            || (route.path.endsWith ("/") && !path.endsWith ("/")))) {
            // One and not both ends with slash
            return false;
        }
        if (route.path.equals (path)) {
            // Paths are the same
            return true;
        }

        // check params
        List<String> pathList = convertRouteToList (path);

        int thisPathSize = route.routeParts.size ();
        int pathSize = pathList.size ();

        if (thisPathSize == pathSize) {
            for (int i = 0; i < thisPathSize; i++) {
                String thisPathPart = route.routeParts.get (i);
                String pathPart = pathList.get (i);

                if ((i == thisPathSize - 1) && (thisPathPart.equals ("*") && route.path
                    .endsWith ("*"))) {
                    // wildcard match
                    return true;
                }

                if ((!thisPathPart.startsWith (":"))
                    && !thisPathPart.equals (pathPart)
                    && !thisPathPart.equals ("*")) {
                    return false;
                }
            }
            // All parts matched
            return true;
        }
        else {
            // Number of "path parts" not the same
            // check wild card:
            if (route.path.endsWith ("*")) {
                if (pathSize == (thisPathSize - 1) && (path.endsWith ("/"))) {
                    // Hack for making wildcards work with trailing slash
                    pathList.add ("");
                    pathList.add ("");
                    pathSize += 2;
                }

                if (thisPathSize < pathSize) {
                    for (int i = 0; i < thisPathSize; i++) {
                        String thisPathPart = route.routeParts.get (i);
                        String pathPart = pathList.get (i);
                        if (thisPathPart.equals ("*") && (i == thisPathSize - 1) && route.path
                            .endsWith ("*")) {
                            // wildcard match
                            return true;
                        }
                        if (!thisPathPart.startsWith (":")
                            && !thisPathPart.equals (pathPart)
                            && !thisPathPart.equals ("*")) {
                            return false;
                        }
                    }
                    // All parts matched
                    return true;
                }
                // End check wild card
            }
            return false;
        }
    }

    private boolean matchPath (Filter route, String path) {
        if (!route.path.endsWith ("*") && ((path.endsWith ("/") && !route.path.endsWith ("/"))
            || (route.path.endsWith ("/") && !path.endsWith ("/")))) {
            // One and not both ends with slash
            return false;
        }
        if (route.path.equals (path)) {
            // Paths are the same
            return true;
        }

        // check params
        List<String> pathList = convertRouteToList (path);

        int thisPathSize = route.routeParts.size ();
        int pathSize = pathList.size ();

        if (thisPathSize == pathSize) {
            for (int i = 0; i < thisPathSize; i++) {
                String thisPathPart = route.routeParts.get (i);
                String pathPart = pathList.get (i);

                if ((i == thisPathSize - 1) && (thisPathPart.equals ("*") && route.path
                    .endsWith ("*"))) {
                    // wildcard match
                    return true;
                }

                if ((!thisPathPart.startsWith (":"))
                    && !thisPathPart.equals (pathPart)
                    && !thisPathPart.equals ("*")) {
                    return false;
                }
            }
            // All parts matched
            return true;
        }
        else {
            // Number of "path parts" not the same
            // check wild card:
            if (route.path.endsWith ("*")) {
                if (pathSize == (thisPathSize - 1) && (path.endsWith ("/"))) {
                    // Hack for making wildcards work with trailing slash
                    pathList.add ("");
                    pathList.add ("");
                    pathSize += 2;
                }

                if (thisPathSize < pathSize) {
                    for (int i = 0; i < thisPathSize; i++) {
                        String thisPathPart = route.routeParts.get (i);
                        String pathPart = pathList.get (i);
                        if (thisPathPart.equals ("*") && (i == thisPathSize - 1) && route.path
                            .endsWith ("*")) {
                            // wildcard match
                            return true;
                        }
                        if (!thisPathPart.startsWith (":")
                            && !thisPathPart.equals (pathPart)
                            && !thisPathPart.equals ("*")) {
                            return false;
                        }
                    }
                    // All parts matched
                    return true;
                }
                // End check wild card
            }
            return false;
        }
    }
}
