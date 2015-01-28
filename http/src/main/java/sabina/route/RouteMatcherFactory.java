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

/**
 * RouteMatcherFactory
 *
 * @author Per Wendel
 */
public final class RouteMatcherFactory {
    private static RouteMatcher routeMatcher;

    private RouteMatcherFactory () {
        throw new IllegalStateException ();
    }

    public static synchronized RouteMatcher get () {
        return routeMatcher == null? routeMatcher = new SimpleRouteMatcher () : routeMatcher;
    }
}
