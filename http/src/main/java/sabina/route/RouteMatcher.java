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

package sabina.route;

import java.util.List;

import sabina.Fault;
import sabina.HttpMethod;
import sabina.Route;

/**
 * Route matcher
 *
 * @author Per Wendel
 */
public interface RouteMatcher {
    /**
     * Parses, validates and adds a route
     *
     * @param target .
     */
    void processRoute (Route target);

    <T extends Exception> void processFault (Fault<T> handler);

    /**
     * Finds the target route for the requested route path and accept type
     *
     * @param httpMethod .
     * @param path .
     * @param acceptType .
     *
     * @return .
     */
    RouteMatch findTarget (HttpMethod httpMethod, String path, String acceptType);

    List<RouteMatch> findTargets (HttpMethod httpMethod, String path, String acceptType);

    Fault<? extends Exception> findHandler(Class<? extends Exception> exceptionClass);
}
